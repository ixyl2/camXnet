/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;

/**
 *
 * @author ixyl2
 */
public class MapNode extends Person {

    double lng;
    double lat;
    double closeness;
    int reachCount;
    double pratio = 0;
    double between;
    //double adeg;
    private boolean involved;
    private int assignedC;
    private int assignedSize;
    private int outside = 0;
    int[] sigma;
    double[] delta;
    double dia;
    double[] currDist;
    double lcloseness;
    double weight;
    static int originalGSize;
    ArrayList<Set<MapNode>> parents;
    double visitCount;
    double restCount;
    protected HashMap<MapNode, Float> edgeBet; //SERIALISATION ONLY
    final Object edgeBetLock = new Object();

    MapNode(int id, String name, double lng, double lat) {
        super(id, name);
        this.lng = lng;
        this.lat = lat;

        dia = 0;
        lcloseness = 0;
        weight = 1;
        visitCount = 0;

        edgeBet = new HashMap<MapNode, Float>();

    }

    float getEdgeBet(MapNode f) {

        if (!this.friendOf(f)) {
            throw new RuntimeException("Trying to get edge bet of non friend");
        }

        return edgeBet.get(f) == null ? 0 : edgeBet.get(f);
    }

    void addEdgeBet(MapNode f, float toAdd) {

        if (!this.friendOf(f)) {
            throw new RuntimeException("Trying to add edge bet of non friend");
        }

        synchronized (edgeBetLock) {
            if (edgeBet.containsKey(f)) {
                edgeBet.put(f, edgeBet.get(f) + toAdd);
            } else {
                edgeBet.put(f, toAdd);
            }
        }

    }

    public double getDistfrom(MapNode p) {
        return MapGraph.distFrom(this.lat, this.lng, p.lat, p.lng);
    }

    public double getDistfrom(double xlat, double xlon) {
        return MapGraph.distFrom(this.lat, this.lng, xlat, xlon);
    }

    public double getEstDistfrom(double xlat, double xlon) {
        return MapGraph.estED(this.lat, this.lng, xlat, xlon);
    }

    void addVisit(double count) {
        visitCount += count;
    }

    void putDia(double d) {
        this.dia = d;
    }

    void putWeight(int s) {
        this.weight = s;
    }

    void putOriginalGSize(int s) {

        originalGSize = s;
    }

    boolean friendOf(MapNode p) {
        return friends.containsKey(p);
    }

    void getInvolved() {
        this.involved = true;
    }

    void disable() {
        this.involved = false;

    }

    void assignC(int c) {
        this.assignedC = c;
    }

    public int getAssignedC() {
        return this.assignedC;
    }

    void assignSize(int size) {
        this.assignedSize = size;
    }

    public int getAssignedSize() {
        return this.assignedSize;
    }

    boolean isInvolved() {
        return involved;
    }

    void iniBet(int noThreads) {
        between = 0;
        sigma = new int[noThreads];
        delta = new double[noThreads];
        currDist = new double[noThreads];
        parents = new ArrayList<Set<MapNode>>();
        for (int i = 0; i < noThreads; i++) {
            parents.add(new HashSet<MapNode>());
        }
    }

    void resetBet(int i) {
        this.sigma[i] = 0;
        this.delta[i] = 0;
        this.currDist[i] = -1;
        parents.get(i).clear();
    }

    void iniClose(int noThreads) {
        currDist = new double[noThreads];
    }

    void putLClose(double c) {
        this.lcloseness = c;
    }

    double getLClose() {
        return this.lcloseness;
    }

    int distanceFrom(MapNode p) { //only for <=3
        if (p.equals(this)) {
            return 0;
        }
        if (this.friendOf(p)) {
            return 1;
        }
        for (Person x : p.getFriends()) {
            if (this.friendOf((MapNode) x)) {
                return 2;
            }
        }
        for (Person x : p.getFriends()) {
            for (Person xf : x.getFriends()) {
                if (this.friendOf((MapNode) xf)) {
                    return 3;
                }
            }
        }
        return 4;
    }

    //weighted
    void bet2(Collection<MapNode> people, int thread, double e) {

        if (this.weight != 0) {

            //HashMap<MapNode, FibonacciHeapNode<MapNode>> ref = new HashMap<MapNode, FibonacciHeapNode<MapNode>>();

            //double time = System.currentTimeMillis();

            //System.out.println("Time for ours 4 " + (System.currentTimeMillis() - time));

            for (MapNode p : people) {
                p.resetBet(thread);
                //  ref.put(p, new FibonacciHeapNode<MapNode>(p,0));
            }

            //  FibonacciHeap<MapNode> q2 = new FibonacciHeap<MapNode>();
            myHeap<MapNode, Double> q = new myHeap<MapNode, Double>(0, true);
            ArrayList<MapNode> S = new ArrayList<MapNode>();

            q.put(this, (double) 0);
            //q2.insert(ref.get(this),0);

            this.currDist[thread] = 0;
            this.sigma[thread] = 1;

            while (q.size() > 0) {
                // MapNode p = q2.removeMin().getData();
                MapNode p = q.pop();
                S.add(p);
                for (Person xx : p.getFriends()) {

                    MapNode n = (MapNode) xx;

                    double newDis = p.currDist[thread] + p.getWeightTo(n);


                    if (n.currDist[thread] == -1) {
                        q.put(n, newDis);
                        //q2.insert(ref.get(x), newDis);
                        n.currDist[thread] = newDis;
                    }


                    if (n.currDist[thread] * (1 - e) > newDis) { // if a very shorter bath between x and p is found

                        q.put(n, newDis);
                        //q2.decreaseKey(ref.get(x), newDis);

                        n.currDist[thread] = newDis;

                        n.sigma[thread] = p.sigma[thread];

                        n.parents.get(thread).clear();

                        n.parents.get(thread).add(p);


                    } else if (n.currDist[thread] * (1 + e) >= newDis) {
                        //add current node as parent
                        n.sigma[thread] += p.sigma[thread];
                        n.parents.get(thread).add(p);
                    }
                }
            }


            for (int i = S.size() - 1; i > 0; i--) {

                MapNode w = S.get(i);

                for (MapNode v : w.parents.get(thread)) {

                    if (v.weight != 0) {

                        double x = ((double) v.sigma[thread] / w.sigma[thread]) * (v.weight * this.weight + w.delta[thread]);

                        v.delta[thread] += x;
                        
                        w.addEdgeBet(v, (float) x);
                        
                        v.addEdgeBet(w, (float) x);
                    }

//                if (w.sigma[thread] == 0) {
//                    v.delta[thread] = 0;
//                }

                    synchronized (w) {
                        w.between += w.delta[thread];
                    }

                }
                //System.out.println(w.getID() + " " + w.delta[thread]);
            }


            //System.out.println("here " + (System.currentTimeMillis() - time));

        }
    }

    //unweighted
    void bet(int N, Collection<MapNode> people, int thread) {

        //   double time = System.currentTimeMillis();
        //  System.out.println("hi ");

        ArrayList<Set<MapNode>> level = new ArrayList<Set<MapNode>>();
        HashSet<MapNode> sofar = new HashSet<MapNode>();
        for (MapNode p : people) {
            p.resetBet(thread);
        }

        for (int i = 0; i <= N; i++) {
            level.add(new HashSet());
        }
        level.get(0).add(this);
        sofar.add(this);

        for (int i = 1; i <= N; i++) {
            for (MapNode p : level.get(i - 1)) {
                for (Person px : p.getFriends()) {
                    MapNode x = (MapNode) px;
                    if (!sofar.contains(x)) {
                        level.get(i).add(x);
                        sofar.add(x);

                    }
                }
            }
            if (level.get(i).isEmpty()) {
                N = i - 1;
                break;
            }
        }

        for (MapNode p : people) {
            p.resetBet(thread);
        }

        this.sigma[thread] = 1;

        for (int i = 0; i < N; i++) {
            for (MapNode p : level.get(i)) {
                for (Person px : p.getFriends()) {
                    MapNode x = (MapNode) px;
                    if (level.get(i + 1).contains(x)) {
                        x.sigma[thread] += p.sigma[thread];
                        x.parents.get(thread).add(p);
                    }
                }
                //System.out.println(p.getID() + " " + p.sigma[thread]);
            }
        }



        for (int i = N; i > 0; i--) {

            for (MapNode p : level.get(i)) {

                for (MapNode x : p.parents.get(thread)) {
                    //    for (MapNode x : p.getFriends()) {
                    //   if (level.get(i - 1).contains(x)) {

                    x.delta[thread] += ((double) x.sigma[thread] / p.sigma[thread]) * (1 + p.delta[thread]);
                    synchronized (p) {
                        p.between += p.delta[thread];
                    }
                }
            }
            // System.out.println(p.getID() + " " + p.delta[thread]);
            //   }
        }
        //System.out.println("hi done: " + (System.currentTimeMillis() - time));
    }

    //unweighted
    void closeNess(int N, Collection<MapNode> people) {

        ArrayList<Set<MapNode>> level = new ArrayList<Set<MapNode>>();
        HashSet<MapNode> sofar = new HashSet<MapNode>();
        for (int i = 0; i <= N; i++) {
            level.add(new HashSet());
        }
        level.get(0).add(this);
        sofar.add(this);

        //FIX: reachcount should equal sofar.size instead?
        synchronized (this) {
            this.reachCount = people.size() - (int) this.weight;

        }
        this.closeness = 0;

        for (int i = 1; i <= N; i++) {
            for (MapNode p : level.get(i - 1)) {
                for (Person px : p.getFriends()) {
                    MapNode x = (MapNode) px;
                    if (!sofar.contains(x)) {
                        level.get(i).add(x);
                        sofar.add(x);
                        this.closeness += i * x.weight;

                        synchronized (x) {
                            if (x.reachCount < people.size() - x.weight) {
                                x.closeness += i * x.weight;
                                x.reachCount++;
                            }
                        }
                    }
                }
            }
            if (level.get(i).isEmpty()) {
                System.out.println(i - 1);
                break; //Terminate because this level has no one in, the lenght of the graph is i-1
            }

        }
        // System.out.println(this.dia + "  "+ this.closeness);
    }

    //weighted
    void closeNess2(Collection<MapNode> people, int thread) {

        //assumes all nodes to be processed( no reach count)
        myHeap<MapNode, Double> q = new myHeap<MapNode, Double>(0, true);

        q.put(this, (double) 0);

        synchronized (this) {
            //this.reachCount = MapNode.originalGSize - this.weight;

            this.closeness = 0;
        }

        for (MapNode p : people) {
            p.currDist[thread] = -1;
        }

        this.currDist[thread] = 0;

        while (q.size() > 0) {
            MapNode p = q.pop();
            for (Person px : p.getFriends()) {
                MapNode x = (MapNode) px;

                double newDis = p.currDist[thread] + p.getWeightTo(x);

                if (x.currDist[thread] == -1) {
                    q.put(x, newDis);
                    x.currDist[thread] = newDis;

                    synchronized (this) {
                        this.closeness += x.currDist[thread] * x.weight;
                    }
//                    synchronized (x) {
//                        if (x.reachCount < originalGSize - x.weight) {
//                            x.closeness += x.currDist[thread] * x.weight;
//                        }
//                    }
                } else if (x.currDist[thread] > newDis) { // if a new shortest bath between x and p is found

                    // x is visited already, but a shorter path is found, hence correct its distance from p.
                    synchronized (this) {
                        this.closeness -= x.currDist[thread] * x.weight;
                    }
//                    synchronized (x) {
//                        if (x.reachCount < originalGSize - x.weight) {
//                            x.closeness -= x.currDist[thread] * x.weight;
//                        }
//                    }

                    q.put(x, newDis);

                    x.currDist[thread] = newDis;

                    synchronized (this) {
                        this.closeness += x.currDist[thread] * x.weight;
                    }
//                    synchronized (x) {
//                        if (x.reachCount < originalGSize - x.weight) {
//                            x.closeness += x.currDist[thread] * x.weight;
//                        }
//                    }
                }
            }
        }

        //System.out.println(this.getID()+ " " + this.closeness);

    }

    //weighted network only
    public double getTotalWeight() {
        double w = 0;
        for (Person p : this.getFriends()) {
            w += this.getWeightTo(p);
        }
        return w;
    }

    double getSimple(double close) {

        HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();

        for (Person px : this.getFriends()) {
            MapNode p = (MapNode) px;
            int cid = p.getMaxClusterID();
            if (count.containsKey(cid)) {
                count.put(cid, count.get(cid) + 1);
            } else {
                count.put(cid, 1);
            }
        }

        double entropy = 0;

        for (Integer i : count.keySet()) {
            double p = (double) count.get(i) / (double) this.friends.size();

            entropy += p * -Math.log(p);
        }

        if (close < 0) {
            return this.size() * entropy;
        }



        return close * entropy;

    }

    MapNode randomFriend(boolean outsider) {

        if (outsider && !this.hasOutsider()) {
            return null;
        }
        if (!outsider && !this.hasInsider()) {
            return null;
        }

        ArrayList<Person> randomisedPeople = new ArrayList<Person>(friends.keySet());


        MapNode p = (MapNode) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));

        if (outsider) {
            while (p.assignedC == this.assignedC) {
                p = (MapNode) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));
            }
        } else {
            while (p.assignedC != this.assignedC) {
                p = (MapNode) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));
            }
        }

        return p;
    }

    MapNode removeRandomFriend(boolean outsider) {

        MapNode f = randomFriend(outsider);

        removeFriend(f);

        return f;
    }

    boolean hasInsider() {
        return (this.friends.size() - this.outside) > 0;
    }

    boolean hasOutsider() {
        return this.outside > 0;
    }

    public int size() {
        return friends.size();
    }
}
