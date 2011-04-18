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
public class LabelPropagation extends DetectionAlgorithm {

    private int clusterS; // No. of Labels to send
    private double hopDecrease; // attenuation
    private double ratio; // The time saving neighbourhood ratio
    private boolean sync;
    private int recoverTime;
    private int noThreads;
    private double sampleRatio;

    LabelPropagation(Graph graph, int clusterS, double hopDecrease, double ratio, boolean sync, int recoverTime, int noThreads, double sampleRatio) {
        super(graph);
        this.sync = sync;
        this.clusterS = clusterS;
        this.hopDecrease = hopDecrease;
        this.ratio = ratio;
        this.recoverTime = recoverTime;
        this.noThreads = noThreads;
        this.sampleRatio = sampleRatio;
    }

    public void run() {

        ArrayList<Person> people = new ArrayList<Person>(graph.getPeople());
        ArrayList<Person> randomisedPeople = new ArrayList<Person>(people);

        // PRE-PROCESSING //////////////////////////////////

        boolean fast = false;
        int maxRuns = 20;


        //System.out.println("Cluster S: " + clusterS + " Hop Decrease: " + hopDecrease + " Discrimination Power: " + disPower + " Repeat: " + repeat + " Sync: " + sync + " Mode: " + mode + " Percent: " + percentLabel);

        for (Person p : people) {
            p.reset();
            p.label();
        }

        Collections.shuffle(randomisedPeople);


        double pMod = 0;  // for termination
        double mod = -1;

        boolean cont = true;
        // ACTUAL RUNS////////////////////////////////

        clusters = new HashMap<Integer, Cluster>();

        for (Person p : people) {
            if (!clusters.containsKey(p.getMaxClusterID())) {
                clusters.put(p.getMaxClusterID(), new Cluster(p.getMaxClusterID()));
            }
            ((Cluster) clusters.get(p.getMaxClusterID())).addMember(p);
        }

        System.out.println("Initial No. of Clusters: " + clusters.size());

        for (int i = 1; i <= maxRuns; i++) {

            if ((i > 1) && (recoverTime > 0)) {
                hopDecrease -= hopDecrease / recoverTime;
                recoverTime--;
            }

            if (hopDecrease < 0 || recoverTime == 0) {
                hopDecrease = 0;
            }

            if ((clusterS == 1) && (hopDecrease == 0)) {
                fast = true;
            }

            // Message Passing......

            ArrayList<messagePassThread> threads = new ArrayList<messagePassThread>();

            Collections.shuffle(randomisedPeople);

            int[] avoidCount = new int[1];

            for (int t = 0; t < noThreads; t++) {

                int fromIndex = (int) Math.round(((float) randomisedPeople.size() / noThreads) * t);
                int toIndex = (int) Math.round(((float) randomisedPeople.size() / noThreads) * (t + 1));

                threads.add(new messagePassThread(randomisedPeople.subList(fromIndex, toIndex), clusterS, hopDecrease, sync, fast, ratio, sampleRatio, avoidCount));

                threads.get(t).start();
            }

            //Wait for threads to finish
            for (int t = 0; t < noThreads; t++) {
                try {
                    threads.get(t).join();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


            //Update people if synchronized
            if (sync) {
                for (Person p : people) {
                    p.confirmUpdate();
                }
            }


            clusters.clear();

            for (Person p : people) {
                if (!clusters.containsKey(p.getMaxClusterID())) {
                    clusters.put(p.getMaxClusterID(), new Cluster(p.getMaxClusterID()));
                }
                ((Cluster) clusters.get(p.getMaxClusterID())).addMember(p);
            }

            clusters.values().iterator().next().putEdge(graph.getNumEdges());


            pMod = mod;

            mod = Modularity.getModularity(clusters.values());

            if (pMod > mod || (Math.abs((pMod - mod) / pMod)) < 0.005) {
                cont = false;
            }

            System.out.println("After Run " + i + " - No. of Clusters: " + clusters.size() + " Modularity : " + mod);

            if (!cont) {

                if (graph.getClass().getSuperclass().getName().equals("socialcrawl.BMGraph")) {
                    double nmi = NMI.getNMI(graph, clusters.values());
                    System.out.println("Terminating Run " + i + " - Total Clusters: " + clusters.size() + " Modularity : " + mod + " NMI : " + nmi);
                } else {

                    System.out.println("Terminating Run : " + i);
                }
                break;
            }
        }
    }

}

class messagePassThread extends Thread {

    List<Person> people;
    int runs;
    int clusterS;
    double hopDecrease;
    boolean sync;
    boolean fast;
    double ratio;
    double sampleRatio;
    int[] avoidCount;

    messagePassThread(List<Person> people, int clusterS, double hopDecrease, boolean sync, boolean fast, double ratio, double sampleRatio, int[] avoidCount) {
        this.people = people;
        this.clusterS = clusterS;
        this.hopDecrease = hopDecrease;
        this.sync = sync;
        this.fast = fast;
        this.ratio = ratio;
        this.sampleRatio = sampleRatio;
        this.avoidCount = avoidCount;
    }

    public void run() {

        //System.out.println("started degree: " + people.degree());
        if (fast) {
            for (Person p : people) {
                if (p.ratio <= ratio) {
                    p.updateClusterF(sampleRatio, sync);
                } else {
                    avoidCount[0]++;
                }
            }
        } else {
            for (Person p : people) {
                if (p.ratio <= ratio) {
                    p.updateCluster(clusterS, hopDecrease, sampleRatio, sync);
                } else {
                    avoidCount[0]++;
                }
            }
        }
    }

}
