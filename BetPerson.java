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
public class BetPerson extends Person {

    double closeness;
    int reachCount;
    double pratio = 0;
    double between;
    double adeg;
    private boolean involved;
    private int assignedC;
    private int assignedSize;
    private int outside = 0;
    int[] sigma;
    double[] delta;
    double dia;
    double[] currDist;
    double lcloseness;
    int weight;
    static int originalGSize;
    ArrayList<Set<BetPerson>> parents;

    BetPerson(int id) {
        super(id);

        reset();
        dia = 0;
        lcloseness = 0;
        weight = 1;
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

    boolean friendOf(BetPerson p) {
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
        parents = new ArrayList<Set<BetPerson>>();
        for (int i = 0; i < noThreads; i++) {
            parents.add(new HashSet<BetPerson>());
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

    int distanceFrom(BetPerson p) { //only for <=3
        if (p.equals(this)) {
            return 0;
        }
        if (this.friendOf(p)) {
            return 1;
        }
        for (Person x : p.getFriends()) {
            if (this.friendOf((BetPerson) x)) {
                return 2;
            }
        }
        return 3;
    }

//    void sendCloseness(int i, LinkedList<BetPerson> nextLevel) {
//        for (BetPerson p : friends) {
//            if (p.closenessM[i] < 0) {
//                p.closenessM[i] = (byte) (this.closenessM[i] + 1);
//                nextLevel.add(p);
//            }
//            if (p.closenessM[i] == (this.closenessM[i] + 1)) {
//                p.pathCount[i] += this.pathCount[i];
//            }
//        }
//    }
    void bet2(Collection<BetPerson> people, int thread, double e) {

        //HashMap<BetPerson, FibonacciHeapNode<BetPerson>> ref = new HashMap<BetPerson, FibonacciHeapNode<BetPerson>>();

        //double time = System.currentTimeMillis();

        //System.out.println("Time for ours 4 " + (System.currentTimeMillis() - time));

        for (BetPerson p : people) {
            p.resetBet(thread);
            //  ref.put(p, new FibonacciHeapNode<BetPerson>(p,0));
        }

        //  FibonacciHeap<BetPerson> q2 = new FibonacciHeap<BetPerson>();
        myHeap<BetPerson, Double> q = new myHeap<BetPerson, Double>(0, true);
        ArrayList<BetPerson> S = new ArrayList<BetPerson>();

        q.put(this, (double) 0);
        //q2.insert(ref.get(this),0);

        this.currDist[thread] = 0;
        this.sigma[thread] = 1;

        while (q.size() > 0) {
            // BetPerson p = q2.removeMin().getData();
            BetPerson p = q.pop();
            S.add(p);
            for (Person xx : p.getFriends()) {

                BetPerson x = (BetPerson) xx;

                double newDis = p.currDist[thread] + p.getWeightTo(x);

                if (x.currDist[thread] == -1) {
                    q.put(x, newDis);
                    //q2.insert(ref.get(x), newDis);
                    x.currDist[thread] = newDis;
                }

                if (x.currDist[thread] * (1 - e) > newDis) { // if a very shorter bath between x and p is found

                    q.put(x, newDis);
                    //q2.decreaseKey(ref.get(x), newDis);

                    x.currDist[thread] = newDis;

                    x.sigma[thread] = p.sigma[thread];

                    x.parents.get(thread).clear();

                    x.parents.get(thread).add(p);


                } else if (x.currDist[thread] * (1 + e) >= newDis) {

                    x.sigma[thread] += p.sigma[thread];
                    x.parents.get(thread).add(p);
                }


            }

        }


        for (int i = S.size() - 1; i > 0; i--) {

            BetPerson w = S.get(i);

            for (BetPerson v : w.parents.get(thread)) {

                v.delta[thread] += ((double) v.sigma[thread] / w.sigma[thread]) * (v.weight * this.weight + w.delta[thread]);
                synchronized (w) {
                    w.between += w.delta[thread];
                }
            }
            // System.out.println(w.getID() + " " + w.delta[thread]);
        }

        //System.out.println("here " + (System.currentTimeMillis() - time));


    }

    void bet(int N, Collection<BetPerson> people, int thread) {

        //   double time = System.currentTimeMillis();
        //  System.out.println("hi ");

        ArrayList<Set<BetPerson>> level = new ArrayList<Set<BetPerson>>();
        HashSet<BetPerson> sofar = new HashSet<BetPerson>();
        for (BetPerson p : people) {
            p.resetBet(thread);
        }

        for (int i = 0; i <= N; i++) {
            level.add(new HashSet());
        }
        level.get(0).add(this);
        sofar.add(this);

        for (int i = 1; i <= N; i++) {
            for (BetPerson p : level.get(i - 1)) {
                for (Person px : p.getFriends()) {
                    BetPerson x = (BetPerson) px;
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

        for (BetPerson p : people) {
            p.resetBet(thread);
        }

        this.sigma[thread] = 1;

        for (int i = 0; i < N; i++) {
            for (BetPerson p : level.get(i)) {
                for (Person px : p.getFriends()) {
                    BetPerson x = (BetPerson) px;
                    if (level.get(i + 1).contains(x)) {
                        x.sigma[thread] += p.sigma[thread];
                        x.parents.get(thread).add(p);
                    }
                }
                //System.out.println(p.getID() + " " + p.sigma[thread]);
            }
        }



        for (int i = N; i > 0; i--) {

            for (BetPerson p : level.get(i)) {

                for (BetPerson x : p.parents.get(thread)) {
                    //    for (BetPerson x : p.getFriends()) {
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

    void closeNess(int N, Collection<BetPerson> people) {

        ArrayList<Set<BetPerson>> level = new ArrayList<Set<BetPerson>>();
        HashSet<BetPerson> sofar = new HashSet<BetPerson>();
        for (int i = 0; i <= N; i++) {
            level.add(new HashSet());
        }
        level.get(0).add(this);
        sofar.add(this);

        //FIX: reachcount should equal sofar.size instead?
        synchronized (this) {
            this.reachCount = people.size() - this.weight;

        }
        this.closeness = 0;

        for (int i = 1; i <= N; i++) {
            for (BetPerson p : level.get(i - 1)) {
                for (Person px : p.getFriends()) {
                    BetPerson x = (BetPerson) px;
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

    void closeNess2(Collection<BetPerson> people, int thread) {

        //assumes all nodes to be processed( no reach count)
        myHeap<BetPerson, Double> q = new myHeap<BetPerson, Double>(0, true);

        q.put(this, (double) 0);

        synchronized (this) {
            this.reachCount = BetPerson.originalGSize - this.weight;

            this.closeness = 0;
        }

        for (BetPerson p : people) {
            p.currDist[thread] = -1;
        }

        this.currDist[thread] = 0;

        while (q.size() > 0) {
            BetPerson p = q.pop();
            for (Person px : p.getFriends()) {
                BetPerson x = (BetPerson) px;

                double newDis = p.currDist[thread] + p.getWeightTo(x);

                if (x.currDist[thread] == -1) {
                    q.put(x, newDis);
                    x.currDist[thread] = newDis;

                    synchronized (this) {
                        this.closeness += x.currDist[thread] * x.weight;
                    }
                    synchronized (x) {
                        if (x.reachCount < originalGSize - x.weight) {
                            x.closeness += x.currDist[thread] * x.weight;
                        }
                    }
                } else if (x.currDist[thread] > newDis) { // if a new shortest bath between x and p is found

                    // x is visited already, but a shorter path is found, hence correct its distance from p.
                    synchronized (this) {
                        this.closeness -= x.currDist[thread] * x.weight;
                    }
                    synchronized (x) {
                        if (x.reachCount < originalGSize - x.weight) {
                            x.closeness -= x.currDist[thread] * x.weight;
                        }
                    }

                    q.put(x, newDis);

                    x.currDist[thread] = newDis;

                    synchronized (this) {
                        this.closeness += x.currDist[thread] * x.weight;
                    }
                    synchronized (x) {
                        if (x.reachCount < originalGSize - x.weight) {
                            x.closeness += x.currDist[thread] * x.weight;
                        }
                    }
                }
            }
        }

    }

    double getSimple(double close) {

        HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();

        for (Person px : this.getFriends()) {
            BetPerson p = (BetPerson) px;
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

    BetPerson randomFriend(boolean outsider) {

        if (outsider && !this.hasOutsider()) {
            return null;
        }
        if (!outsider && !this.hasInsider()) {
            return null;
        }

        ArrayList<Person> randomisedPeople = new ArrayList<Person>(friends.keySet());


        BetPerson p = (BetPerson) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));

        if (outsider) {
            while (p.assignedC == this.assignedC) {
                p = (BetPerson) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));
            }
        } else {
            while (p.assignedC != this.assignedC) {
                p =  (BetPerson) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));
            }
        }

        return p;
    }

    BetPerson removeRandomFriend(boolean outsider) {

        BetPerson f = randomFriend(outsider);

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
