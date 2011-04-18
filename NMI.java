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
public class NMI {

    Graph graph;
    static double nmi;
    static HashMap<Integer, Integer> originalCCount;

    NMI() {
    }

    static void printConfusionMatrix(Graph graph, Collection<Cluster> clusters) {
        ArrayList<Cluster> clusterList = new ArrayList<Cluster>(clusters);

        originalCCount = ((nBMGraph) graph).getOriginalCCount();

        for (Cluster c : clusterList) {
            HashMap<Integer, Integer> cCount = new HashMap<Integer, Integer>();
            for (Person p : c.getMembers()) {

                if (cCount.containsKey(((BMPerson)p).getAssignedC())) {
                    cCount.put(((BMPerson)p).getAssignedC(), cCount.get(((BMPerson)p).getAssignedC()) + 1);
                } else {
                    cCount.put(((BMPerson)p).getAssignedC(), 1);
                }
            }
            System.out.print(c.getID() + " ");

            for (Integer i : originalCCount.keySet()) {
                if (cCount.containsKey(i)) {
                    System.out.print(cCount.get(i) + "\t");
                } else {
                    System.out.print(0 + "\t");
                }
            }
            System.out.println("");
        }

    }

    static double getNMI(Graph graph, Collection<Cluster> clusters) {

        nmi = 0;

        originalCCount = ((nBMGraph) graph).getOriginalCCount(); //assigned cluster sizes from the BMG

        ArrayList<Cluster> clusterList = new ArrayList<Cluster>(clusters);

        double hy = 0;

        for (Cluster c : clusterList) {
            hy += (double) c.getActualsize() * Math.log((double) c.getActualsize() / graph.size());
        }

        //System.out.println("check " + clusterList.degree());

        double hx = 0;
        for (Integer i : originalCCount.values()) {
            hx += (double) i * Math.log((double) i / graph.size());
        }

        double hxy = 0;
        for (Cluster c : clusterList) {
            HashMap<Integer, Integer> cCount = new HashMap<Integer, Integer>();
            for (Person p : c.getMembers()) {
                if (cCount.containsKey(((BMPerson)p).getAssignedC())) {
                    cCount.put(((BMPerson)p).getAssignedC(), cCount.get(((BMPerson)p).getAssignedC()) + 1);
                } else {
                    cCount.put(((BMPerson)p).getAssignedC(), 1);
                }
            }

            for (Integer i : originalCCount.keySet()) {
                if (cCount.containsKey(i)) {

                    //System.out.println("here"+ (double) (graph.degree() * cCount.get(i)) / (c.degree() * originalCCount.get(i)));
                    hxy += cCount.get(i) * Math.log((double) (graph.size() * cCount.get(i)) / (double) (c.getActualsize() * originalCCount.get(i)));
                }
            }

        }

        nmi = -2 * hxy / (hx + hy);

        return nmi;
    }

    static double getNMI(Map<Integer, Integer> list1, Map<Integer, Integer> list2) {

        nmi = 0;

        Map<Integer, Set<Integer>> clist1 = new HashMap<Integer, Set<Integer>>();

        for (int i : list1.keySet()) {
            if (!clist1.containsKey(list1.get(i))) {
                clist1.put(list1.get(i), new HashSet<Integer>());
                clist1.get(list1.get(i)).add(i);
            } else {
                clist1.get(list1.get(i)).add(i);
            }
        }

        Map<Integer, Set<Integer>> clist2 = new HashMap<Integer, Set<Integer>>();

        for (int i : list2.keySet()) {
            if (!clist2.containsKey(list2.get(i))) {
                clist2.put(list2.get(i), new HashSet<Integer>());
                clist2.get(list2.get(i)).add(i);
            } else {
                clist2.get(list2.get(i)).add(i);
            }
        }


        double hy = 0;

        for (Integer c : clist1.keySet()) {
            hy += (double) clist1.get(c).size() * Math.log((double) clist1.get(c).size() / list1.size());
        }

        //System.out.println("check " + clusterList.degree());

        double hx = 0;
        for (Integer c : clist2.keySet()) {
            hx += (double) clist2.get(c).size() * Math.log((double) clist2.get(c).size() / list2.size());
        }

        double hxy = 0;
        for (Integer i : clist1.keySet()) {
            HashMap<Integer, Integer> cCount = new HashMap<Integer, Integer>();

            double yCount = 0;



            for (Integer j : clist2.keySet()) {
                int test = 0;
                for (Integer member : clist2.get(j)) {
                    if (clist1.get(i).contains(member)) {
                        //test ++;
                        yCount++;
                        if (cCount.containsKey(j)) {
                            cCount.put(j, cCount.get(j) + 1);
                        } else {
                            cCount.put(j, 1);
                        }
                    }
                }
                
            }

            for (Integer j : cCount.keySet()) {
                //System.out.println("here"+ (double) (graph.degree() * cCount.get(i)) / (c.degree() * originalCCount.get(i)));
                hxy += cCount.get(j) * Math.log((double) (list1.size() * cCount.get(j)) / (double) (yCount * clist1.get(i).size()));

            }

        }

        nmi = -2 * hxy / (hx + hy);

        return nmi;
    }

}
