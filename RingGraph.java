/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

/**
 *
 * @author ixyl2
 */
public class RingGraph extends Graph {

    RingGraph(int l, int c, double pr) { //Length of ring, Size of clique

        for (int i = 0; i < l * c; i++) {
            people.put(i, new Person(i));
        }

        //connect internally
        for (int i = 0; i < l; i++) {         // 0...L
            for (int j = i * c; j < l * c; j += c) {   // 0...c...2c...(L-1)c
                for (int k = j; k < j + c; k++) {  // (k...k+c)
                    for (int p = k + 1; p < j + c; p++) {  //k+1...k+c
                        if (Math.random() < pr) {
                            people.get(k).addFriend(people.get(p), 1.0);
                            people.get(p).addFriend(people.get(k), 1.0);
                        }
                    }
                }
            }

        }

        //connect up cliques

        for (int i = 0; i < l; i++) {         // 0...L
            for (int j = i * c; j < l * c; j += c) {   // 0...c...2c...(L-1)c
                people.get(j).addFriend(people.get((j + c) % (l * c)), 1.0);
                people.get((j + c) % (l * c)).addFriend(people.get(j), 1.0);
            }

        }
        int edge = 0;

        for (Person i : people.values()) {
            edge += i.getFriends().size();
        }
        //System.out.println("Number of nodes: " + people.degree() + " Number of edges: " + edge);


    }

}
