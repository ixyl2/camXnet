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
public class Modularity extends Thread {

    Graph graph;
    static double mod;
    static int edge;
    Collection<Cluster> subClusters;

    Modularity(Collection<Cluster> clusters) {
        subClusters = clusters;
    }

    static double getModularity(Collection<Cluster> clusters) {

        mod = 0;

        int noOfThreads = 1;

        ArrayList<Cluster> clusterList = new ArrayList<Cluster>(clusters);

        ArrayList<Modularity> threads = new ArrayList<Modularity>();

        for (int t = 0; t < noOfThreads; t++) {

            int fromIndex = (int) Math.round(((float) clusterList.size() / noOfThreads) * t);
            int toIndex = (int) Math.round(((float) clusterList.size() / noOfThreads) * (t + 1));

            threads.add(new Modularity(clusterList.subList(fromIndex, toIndex)));

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

        return mod;
    }

    static double getModularity(Map<Integer, Cluster> clusters) {
        return getModularity(clusters.values());
    }
    
    public void run() {

        for (Cluster c : subClusters) {
            try {

                double add = c.recalLocalModularity();

                synchronized (this) {
                    mod += add;
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    }
}
