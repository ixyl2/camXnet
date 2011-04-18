/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import java.io.*;

/**
 *
 * @author ixyl2
 */
//
//abstract class Node implements Comparable<Node>, Serializable {
//    transient static Random rng = new Random();
//    static Graph graph;
//    protected int id;
//    protected String name;
//
//    transient protected HashSet<Person> friends;
//
//    Node(int id) {
//        this.id = id;
//        friends = new HashSet<Person, Float>();
//    }
//
//    Node(int id, String name) {
//        this(id);
//        this.name = name;
//    }
//}

interface Node<T> {


    void linkGraph(Graph g) ;


    int getID() ;

    //fix:
    void changeID(int id);

    String getName() ;

    //boolean isFriendOf(T p) ;

    //Set<T> getFriends();

    //weighter network only
    double getTotalWeight() ;

    //weighter network only
    double getMaxWeight() ;

    boolean addFriend(T e, double weight);

    //weighted network only
    double getWeightTo(T e);

    //weighted network only
    void putWeight(Person e, double w) ;

    void removeFriend(T e) ;

    void removeAllFriends() ;

    void serialise() ;

    void unSerialise() ;

    Person randomFriend();

    public int degree() ;

  //  public int compareTo(T per);

}

public class Person implements Node<Person>, Comparable<Person>, Serializable {

    transient static Random rng = new Random();
    static Graph graph;
    protected int id;
    protected String name;
    transient protected HashMap<Person, Float> friends;
    protected HashMap<Integer, Float> friendList; //SERIALISATION ONLY
    //clusterable person : fix
    transient private Cluster cluster;
    transient private Map<Integer, Byte> clusterID;
    transient private Map<Integer, Byte> newClusterID;
    transient private Map<Integer, Byte> oldClusterID;
    transient double ratio = 0;
    //For Epidemics;
    transient private int distance;

    Person(int id) {

        this.id = id;
        friends = new HashMap<Person, Float>();

        clusterID = new HashMap<Integer, Byte>();
        newClusterID = new HashMap<Integer, Byte>();
        oldClusterID = new HashMap<Integer, Byte>();
    }

    Person(int id, String name) {
        this(id);
        this.name = name;
    }

    public void linkGraph(Graph g) {
        graph = g;
    }

    //for epi
    void putDistance(int d) {
        this.distance = d;
    }

    //for epi
    int getDistance() {
        return this.distance;
    }

    public int getID() {
        return this.id;
    }

    //fix:
    public void changeID(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    void reset() {
        clusterID.clear();
        ratio = 0;
    }

    //cluster only
    void label() {
        clusterID.put(id, (byte) 0);
    }

    public boolean isFriendOf(Person p) {
        return friends.containsKey(p);
    }

    //weighter network only
    public double getTotalWeight() {
        double w = 0;
        for (Person p : this.getFriends()) {
            w += this.getWeightTo(p);
        }
        return w;
    }

    //weighter network only
    public double getMaxWeight() {
        double w = 1.0;
        for (Person p : this.getFriends()) {
            if (this.getWeightTo(p) > w) {
                w = this.getWeightTo(p);
            }
        }
        return w;
    }

    //cluster only
    Cluster getCluster() {
        return this.cluster;
    }

    //cluster only
    boolean isInBorder() {
        return ratio < 0.99;
    }

    public boolean addFriend(Person e, double weight) {
        if (e.id == this.id) { //FIX
            //throw new RuntimeException("Adding self as friend! Check your Code!");
            return false;
        }
        if (!friends.containsKey(e)) {

            friends.put(e, (float) weight);

            return true;
        }
        return false;
    }

    //weighted network only
    public double getWeightTo(Person e) {
        return this.friends.get(e);
    }

    //weighted network only
    public void putWeight(Person e, double w) {
        if (e.id == this.id) { //FIX
            //throw new RuntimeException("Referencing self as friend! Check your Code!");
        }
        this.friends.put(e, (float) w);
    }

    public void removeFriend(Person e) {
        friends.remove(e);

    }

    public void removeAllFriends() {

        for (Person p : this.getFriends()) {
            p.friends.remove(this);
        }
        this.friends.clear();

    }

    public void serialise() {
        this.friendList = new HashMap<Integer, Float>();
        for (Person p : this.friends.keySet()) {
            this.friendList.put(p.getID(), this.friends.get(p));
        }
    }
    //for unserialisation??

    public void unSerialise() {
        friends = new HashMap<Person, Float>();
        clusterID = new HashMap<Integer, Byte>();
        newClusterID = new HashMap<Integer, Byte>();
        oldClusterID = new HashMap<Integer, Byte>();

        for (Integer i : this.friendList.keySet()) {
            this.addFriend(graph.getPerson(i), this.friendList.get(i));
        }

        this.friendList.clear();
    }

    public Person randomFriend() {
        return (Person) this.getFriends().toArray()[this.getFriends().size()];
    }

    public boolean equals(Person per) {
        return (this.id == per.id);
    }

    public int degree() {
        return friends.size();
    }

    public int compareTo(Person per) {
        //this optimization is usually worthwhile, and can
        //always be added
        return (this.id - per.id);

    }

    public Set<Person> getFriends() {
        return friends.keySet();
    }

    //cluster only
    public void confirmUpdate() {
        synchronized (this) {
            oldClusterID.clear();
            oldClusterID.putAll(clusterID);
            clusterID.clear();
            clusterID.putAll(newClusterID);
        }
    }

    //cluster only
    public void revertUpdate() {
        clusterID.clear();
        clusterID.putAll(oldClusterID);
    }

    //cluster only
    public Map<Integer, Byte> getClusterID() {
        return this.clusterID;
    }

    //FIX!! Now multiple messages will not work!
    //cluster only
    public int getMaxClusterID() {

        if (clusterID.size() == 1) {
            return clusterID.keySet().iterator().next();
        }

        double max = -1;
        int maxID = 0;
        for (Integer i : clusterID.keySet()) {
            if (clusterID.get(i) > max) {
                max = clusterID.get(i);
                maxID = i;
            }
        }
        return maxID;
    }

    //cluster only
    //TODO: check usage
    void assignCluster(Cluster c) {
        this.cluster = c;
    }

    double getClustering() {

        if (this.degree() >= 2) {
            int poss = this.degree() * (this.degree() - 1);
            int count = 0;

            for (Person p : this.getFriends()) {
                for (Person pf : this.getFriends()) {
                    if (!p.equals(pf) && p.isFriendOf(pf)) {
                        count++;
                    }
                }
            }
            return (double) count / poss;
        }
        return 0;

    }

    //cluster only
    public void updateClusterF(double sampleRatio, boolean sync) {

        HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();

        //Include myself...
        for (Integer i : this.clusterID.keySet()) {
            counter.put(i, 1);
        }
        newClusterID.clear();
        int max = 1;
        if (sampleRatio == 1) {

            //Receive messages from neighbours
            for (Person p : this.getFriends()) {

                int m = 0;
                synchronized (p) {
                    m = p.getMaxClusterID();
                }

                Integer ix = counter.get(m);

                if (ix == null) {
                    counter.put(m, 1);
                } else {
                    counter.put(m, ix + 1);
                    if (ix + 1 > max) {
                        max = ix + 1;
                    }
                }
            }
        } else {
            int samples = this.friends.size();

            if (sampleRatio < 0) {
                samples = (int) Math.ceil(Math.pow((double) samples, -sampleRatio));
            } else {
                samples = (int) Math.ceil(this.friends.size() * sampleRatio);
            }

            HashSet<Person> set = new HashSet<Person>();

            for (int i = 0; i < samples; i++) {

                Person p;
                do {
                    p = this.randomFriend();
                } while (set.contains(p));

                set.add(p);

                int m = 0;
                synchronized (p) {
                    m = p.getMaxClusterID();
                }

                Integer ix = counter.get(m);

                if (ix == null) {
                    counter.put(m, 1);
                } else {
                    counter.put(m, ix + 1);
                    if (ix + 1 > max) {
                        max = ix + 1;
                    }
                }
            }
        }

        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (Integer i : counter.keySet()) {
            if (counter.get(i) == max) {
                ids.add(i);
            }
        }


        if (ids.size() > 0) {
            newClusterID.put(ids.get(rng.nextInt(ids.size())), (byte) 0); //0 is dummy 
        }

        ratio = (double) max / (this.degree() + 1);
        if (!sync) {
            confirmUpdate();
        }

    }

    //cluster only
    public void updateCluster(int clusterLimit, double hopDecrease, double sampleRatio, boolean sync) {

        HashMap<Integer, Double> counter = new HashMap<Integer, Double>(); // Label --> Summed score (weighted)
        HashMap<Integer, Byte> counterDist = new HashMap<Integer, Byte>(); // Label --> Distance from Origin
        HashMap<Integer, Integer> counter2 = new HashMap<Integer, Integer>(); // Label --> Count

        newClusterID.clear();

        //Include myself...

        counterDist.putAll(clusterID);

        for (Integer i : clusterID.keySet()) {
            counter2.put(i, 1);
        }

//Receive messages from neighbours
        for (Person p : friends.keySet()) {

//            double fp; ??

            Map<Integer, Byte> message = new HashMap<Integer, Byte>(); //

            //Prevent update and message pass from happening together
            synchronized (p) {
                message.putAll(p.getClusterID());
            }

            for (Integer pKey : message.keySet()) {

                if (!counter.containsKey(pKey)) {
                    counter.put(pKey, Math.max(0, (1 - message.get(pKey) * hopDecrease)));

                    counterDist.put(pKey, (byte) (message.get(pKey) + 1));

                    counter2.put(pKey, 1);
                } else {
                    counter.put(pKey, counter.get(pKey) + Math.max(0, (1 - message.get(pKey) * hopDecrease)));

                    if (message.get(pKey) + 1 < counterDist.get(pKey)) {
                        counterDist.put(pKey, (byte) (message.get(pKey) + 1));
                    }

                    counter2.put(pKey, counter2.get(pKey) + 1);
                }

            }

        }

        // Sort the clusters with maximum message score
        TreeSet set = new TreeSet(new Comparator() {

            public int compare(Object obj, Object obj1) {
                return -((Comparable) ((Map.Entry) obj).getValue()).compareTo(((Map.Entry) obj1).getValue());
            }
        });

        set.addAll(counter.entrySet());

        // Pick the top x and renew the clusterID list in descending order
        // Restrict backward passing by disregarding new score(must be less than original) for existing cluster for this node
        int x = 0;
        for (Iterator i = set.iterator(); i.hasNext();) {
            if (x == clusterLimit) {
                break;
            }

            Map.Entry entry = (Map.Entry) i.next();

            Integer key = (Integer) entry.getKey();

            if (clusterID.containsKey(key)) {
                newClusterID.put(key, clusterID.get(key));
                ratio =
                        (double) (counter2.get(key) - 1) / this.degree();
            } else {
                newClusterID.put(key, counterDist.get(key));
                ratio =
                        (double) counter2.get(key) / this.degree();
            }

//System.out.println(newClusterID.get(key));
            x++;
        }

        if (!sync) {
            confirmUpdate();
        }

    }
}
