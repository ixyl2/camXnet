/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import java.util.Map.*;
import java.util.Random;

/**
 *
 * @author ixyl2
 */
public class QuickMod extends DetectionAlgorithm {

    myHeap<Cluster, Double> globalHeap;
    Random rng = new Random();
    int mode;
    int heapSize;
    double nmi;
    double mod;

    QuickMod(Graph graph, int mode, int heapSize) {
        super(graph);
        this.mode = mode;
        this.heapSize = heapSize;
    }

    public void run() {

        if (mode == 2) {
            heapSize = 1;
        }

        globalHeap = new myHeap<Cluster, Double>((byte) heapSize, false);

        ArrayList<Person> people = new ArrayList<Person>(graph.getPeople());

        for (Person p : people) {
            clusters.put(p.getID(), new Cluster(p.getID()));
            clusters.get(p.getID()).addMember(p);
            p.assignCluster(clusters.get(p.getID()));
        }

        int noOfThreads = 8;

        //Link Static variables
        clusters.values().iterator().next().linkGlobalHeap(globalHeap);
        clusters.values().iterator().next().linkClusters((HashMap<Integer, Cluster>) clusters);
        clusters.values().iterator().next().putEdge(graph.getNumEdges());
        clusters.values().iterator().next().putHeapSize(heapSize);

        ArrayList<Cluster> clusterList = new ArrayList<Cluster>(clusters.values());

        ArrayList<updateThread> threads = new ArrayList<updateThread>();
        ArrayList<updateThread2> threads2 = new ArrayList<updateThread2>();

        for (int t = 0; t < noOfThreads; t++) {

            int fromIndex = (int) Math.round(((float) clusterList.size() / noOfThreads) * t);
            int toIndex = (int) Math.round(((float) clusterList.size() / noOfThreads) * (t + 1));

            threads.add(new updateThread(clusterList.subList(fromIndex, toIndex)));
            threads2.add(new updateThread2(clusterList.subList(fromIndex, toIndex), mode));

            threads.get(t).start();
        }

        //Wait for threads to finish
        for (int t = 0; t < noOfThreads; t++) {
            try {
                threads.get(t).join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

//        System.out.println("Initialised");
        //FIx: wtf am i doing?
        for (int t = 0; t < noOfThreads; t++) {
            try {
                threads2.get(t).start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (int t = 0; t < noOfThreads; t++) {
            try {
                threads2.get(t).join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

//        System.out.println("Updated Local Heap");
        long time = System.currentTimeMillis();

        ///////// SLOW
        if (mode == 0) {
            while (globalHeap.size() > 0) {
                double mod = Modularity.getModularity(clusters);
                if (clusters.size() % 100 == 0) {
//                    System.out.println("Total Cluster Left " + clusters.degree());
//                    System.out.println("Modularity: " + Modularity.getModularity(clusters));
                }

                globalHeap.peek().combine(mode);

                //System.out.println(Modularity.getModularity(clusters)-mod);
            }
        } else if (mode == 1) {
            /////////// FAST

            for (int i = 0; i < 100; i++) {
                double mod = Modularity.getModularity(clusters);
//                System.out.println("Total Cluster Left " + clusters.degree());
//                System.out.println("Modularity: " + mod);
                clusterList = new ArrayList<Cluster>(clusters.values());
                Collections.shuffle(clusterList);
                for (Cluster c : clusterList) {
                    if (clusters.containsKey(c.getID())) {
                        c.combine(mode);
                    } else {
                        c = null; //faster garbage collection?
                    }
                }
                if ((Math.abs((mod - Modularity.getModularity(clusters)) / mod)) < 0.001) {
//                    System.out.println("Terminating...");
                    break;
                }
            }
        } else {
            boolean begin = true;
            clusterList = new ArrayList<Cluster>(clusters.values());
            for (int i = 0; i < 100; i++) {
                double mod = Modularity.getModularity(clusters);


                int cSize = clusterList.size();
                System.out.println("Total Cluster Left " + clusters.size());
                System.out.println("Modularity: " + Modularity.getModularity(clusters));


                for (Cluster c : clusterList) {
                    if (!c.sleep || begin) {
                        c.combine(mode);

                    } else {
//                        c.switchCluster();
                    }
                }
                begin = false;


                if ((Math.abs((mod - Modularity.getModularity(clusters)) / mod)) < 0.001) {
                    clusterList = new ArrayList<Cluster>(clusters.values());
                    begin = true;
                    if (cSize == clusterList.size()) {
                        System.out.println("Terminating...");
                        break;
                    } else {
                        System.out.println("Begin another level");
                    }
                }

            }
        }


       // graph.outputGML("QM");

//        System.out.println("Total Cluster Left " + clusters.degree());
        System.out.println("Modularity: " + Modularity.getModularity(clusters));
        if (graph.getClass().getSuperclass().getName().equals("socialcrawl.BMGraph")) {
            nmi = NMI.getNMI(graph, clusters.values());
            mod = Modularity.getModularity(clusters);
            System.out.println("NMI: " + NMI.getNMI(graph, clusters.values()));
            NMI.printConfusionMatrix(graph, clusters.values());
        }

//        System.out.println("Total time used: " + (System.currentTimeMillis() - time));


    }

}

class updateThread extends Thread {

    List<Cluster> clusters;

    updateThread(List<Cluster> clusters) {
        this.clusters = clusters;
    }

    public void run() {
        for (Cluster c : clusters) {
            c.initialise();
        }
    }

}

class updateThread2 extends Thread {

    List<Cluster> clusters;
    int mode;

    updateThread2(List<Cluster> clusters, int mode) {
        this.clusters = clusters;
        this.mode = mode;
    }

    public void run() {
        for (Cluster c : clusters) {
            c.initialiseLocalHeap(mode);
        }
    }

}






