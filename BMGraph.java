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
public class BMGraph extends Graph {

    HashMap<Integer, Integer> cCount;
    HashMap<Integer, Cluster> assignedClusters;

    Collection<Cluster> getAssignedClusters() {
        return assignedClusters.values();
    }

    void BMGraph(int N, int C, double pOut) {

        for (int i = 1; i <= N; i++) {
            people.put(i, new Person(i));
        }

        int z = (int) (N / C / 2);

        int zOut = (int) ((N / C / 2) * pOut);

        for (int i = 1; i <= N; i++) {
            Person p = people.get(i);

            while (p.degree() < zOut) {
                p.addFriend(people.get(((int) (p.getID() / 32)) * 32 + rng.nextInt(32)), 1.0);
            }

            while (p.degree() < z) {
                p.addFriend(people.get(((int) (p.getID() / 32)) * 32 + rng.nextInt(32)), 1.0);
            }
        }

    }

}
