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
public abstract class DetectionAlgorithm extends Thread {

    Graph graph;
    Map<Integer, Cluster> clusters;

    DetectionAlgorithm(Graph graph) {
        this.graph = graph;
        clusters = new HashMap<Integer, Cluster>();
    }

    public abstract void run();

    public void printResults(String filename) {
        LineWriter lw = new LineWriter(new File(filename));

        try {
            for (Cluster c : clusters.values()) {
                for (Person p : c.getMembers()) {
                    lw.writeLine(p.getID() + "\t" + c.getID());
                }
            }
            lw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
