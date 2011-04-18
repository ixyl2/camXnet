/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import java.util.Random;

public class Cluster implements Comparable {

    private Set<Person> members = new HashSet<Person>();
    private int id;
    private short assignedSize;
    static Random rng = new Random();
    static HashMap<Integer, Cluster> globalClusters;
    static double edge;
    private myHeap<Cluster, Double> localHeap;
    private HashMap<Cluster, Integer> neighbourClusters;
    private int inLinks;
    private int outLinks;
    static myHeap<Cluster, Double> globalHeap;
    private double localModularity;
    static final Object updateGlobalHeapLock = new Object();
    static int heapSize;
    boolean sleep;
    private Cluster owner;

    Cluster(Integer id) {
        this.id = id;
    }

    public int compareTo(Object c) {
        return ((Cluster) c).getID() - id;
    }

    void linkClusters(HashMap<Integer, Cluster> clusters) {
        globalClusters = clusters;
    }

    void putEdge(int xedge) {
        edge = xedge;
    }

    void linkGlobalHeap(myHeap xGlobalHeap) {
        globalHeap = xGlobalHeap;
    }

    void putHeapSize(int xheapSize) {
        heapSize = xheapSize;
    }

    void assignSize(short size) {
        assignedSize = size;
    }

    int getAssignedSize() {
        return assignedSize;
    }

    int size() {
        return this.members.size();
    }

    boolean hasMember(Person p) {
        return members.contains(p);
    }

    boolean addMember(Person p) {
        return members.add(p);
    }

    void addMembers(Collection<Person> col) {
        members.addAll(col);
    }

    void clear() {
        members.clear();
    }

    myHeap<Cluster, Double> getLocalHeap() {
        return localHeap;
    }

    Person kickRandomMember() {
        BMPerson selected = (BMPerson) getRandomMember();
        selected.assignC(-1);
        members.remove(selected);
        return selected;
    }

    public Person getRandomMember() {
        Person selected = (Person) members.toArray()[rng.nextInt(members.size())];
        return selected;
    }

    boolean isFull() {
        return members.size() >= assignedSize;
    }

    int getActualsize() {
        return members.size();
    }

    int getID() {
        return id;
    }

    Collection<Cluster> getNeighbourClusters() {
        return neighbourClusters.keySet();
    }

    Set<Person> getMembers() {
        return members;
    }

    Set<Person> getBorderNodes() {
        HashSet<Person> nodes = new HashSet<Person>();
        for(Person p : this.members) {
            if (p.isInBorder()) {
                nodes.add(p);
            }
        }
        return nodes;
    }


    double avgFriend() {
        double total = 0;
        for (Person p : members) {
            total += p.getFriends().size();
        }

        return total / members.size();
    }

    //Usage in : QuickMod
    void initialise() {
        this.sleep = false;
        this.owner = this;
        renewNeighbourClusters();
        recalLocalModularity();
    }

    //fix this so that owner->links also know
    void renewNeighbourClusters() {
        neighbourClusters = new HashMap<Cluster, Integer>();

        for (Person p : members) {
            for (Person x : p.getFriends()) {
                if (!members.contains(x)) {
                    Cluster xC = x.getCluster();
                    if (!neighbourClusters.containsKey(xC)) {
                        neighbourClusters.put(xC, 1);
                    } else {
                        neighbourClusters.put(xC, neighbourClusters.get(xC) + 1);
                    }
                }
            }
        }

    }

    void initialiseLocalHeap(int mode) {
        localHeap = new myHeap<Cluster, Double>((byte) heapSize, false);

        for (Cluster c : getNeighbourClusters()) {
            double mg = calModGain(c);
            if (mg > 0) {
                localHeap.put(c, mg);
            }
        }

        if (mode == 0) {
            updateGlobalHeap();
        }

    }

    void joinCluster(Cluster toJoin) {

        for (Person p : members) {
            if (this.owner != this) {
                this.owner.members.remove(p);
            }
            p.assignCluster(toJoin);
            toJoin.addMember(p);
        }

    }

    //FIX
    void updateLocalHeap(Cluster toBeCombined, int mode) {
        localHeap = new myHeap<Cluster, Double>((byte) heapSize, false);

        for (Cluster c : getNeighbourClusters()) {
            c.neighbourClusters.put(this, neighbourClusters.get(c));
            double mg = calModGain(c);
            if (mg > 0) {
                localHeap.put(c, mg);
            }
            if (mode < 2) {
                c.receive(this, toBeCombined, mg, mode);
            }
        }

        if (mode == 0) {
            updateGlobalHeap();
        }
    }

    //Receives new information on a newly combined cluster c1
    public void receive(Cluster c1, Cluster c2, double score, int mode) {

        neighbourClusters.remove(c2);
        localHeap.remove(c2);

        if (score > 0) {
            localHeap.put(c1, score);
        } else {
            localHeap.remove(c1);
        }

        if (mode == 0) {
            updateGlobalHeap();
        }
    }

    //FIX
    private void updateGlobalHeap() {
        synchronized (updateGlobalHeapLock) {
            //System.out.println("here" + localHeap.degree());
            if (localHeap.size() > 0) {
                globalHeap.put(this, new Double(localHeap.peekValue()));
            } else {
                globalHeap.remove(this);
            }
        }
    }

    //FIX : Usage: QuickMod
    void combine(int mode) {
        if (mode == 0) {

            if (localHeap.peekValue().doubleValue() != globalHeap.peekValue().doubleValue()) {
                System.out.println("shit!");
            }
            combineCluster(localHeap.peek(), mode);



        } else {
            if (localHeap.size() > 0) {
                combineCluster(localHeap.peek(), mode);
            }
        }

    }

    void change() {
        this.renewNeighbourClusters();
    }

    //Usage : combine
    void combineCluster(Cluster toBeCombined, int mode) {
        if (toBeCombined.size() > this.size()) { // Always Big eat small, save time..or does it matter?
            toBeCombined.combineCluster(this, mode);
        } else {

            toBeCombined.joinCluster(this);

            globalClusters.remove(toBeCombined.getID());

            if (mode == 0) {
                globalHeap.remove(toBeCombined);
            }



            // Update neighbours by combining tbC's to this, also add the links
            for (Cluster c : toBeCombined.neighbourClusters.keySet()) {
                if (neighbourClusters.containsKey(c)) {
                    neighbourClusters.put(c, neighbourClusters.get(c) + toBeCombined.neighbourClusters.get(c));
                } else {
                    neighbourClusters.put(c, toBeCombined.neighbourClusters.get(c));
                }
            }

            neighbourClusters.remove(this);
            neighbourClusters.remove(toBeCombined);


            //FIX: dont have to do this, should grab back from the heap, but should update new I and O?
            recalLocalModularity();

            //Neccessary Step
            updateLocalHeap(toBeCombined, mode); //includes updating neighbourss and global heap

            if (mode < 2) {
                //?
                toBeCombined = null;
            } else { //Fastest mode combineds everything afterwards
                toBeCombined.owner = this;
                toBeCombined.sleep = true;
            }
        }
    }

    private double calModGain(Cluster c) {
        double modGain = 0;

        int newI = inLinks + c.inLinks + neighbourClusters.get(c);
        int newO = outLinks + c.outLinks - 2 * neighbourClusters.get(c);

        modGain = (double) (2 * newI) / edge - Math.pow((double) (2 * newI + newO) / edge, 2.0) - (localModularity() + c.localModularity());

        //double test2 = (neighbourClusters.get(c) / edge) - (Math.pow(A+B, 2.0) + Math.pow(A, 2.0) + Math.pow(B, 2.0)) / Math.pow((double)edge, 2.0);

        return modGain;
    }

    double recalLocalModularity() {
        outLinks = 0;
        inLinks = 0;
        for (Person p : members) {
            for (Person x : p.getFriends()) {
                if (members.contains(x)) {
                    inLinks++;
                } else {
                    outLinks++;
                }
            }
        }

        //  System.out.println(I+" "+O + " " + edge);
//        System.out.println((I / edge) - Math.pow((I + O)/ edge, 2.0));

        localModularity = ((double) inLinks / edge) - Math.pow((double) (inLinks + outLinks) / edge, 2.0);

        //InLinks actually counted twice, remember.
        inLinks /= 2;

        return localModularity;
    }

    double localModularity() {
        return localModularity;
    }


}
