/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

/**
 *
 * @author ixyl2
 */
public class LNClique extends Graph {

    LNClique(int l, int n, double pr) { //Length of ring, Size of clique


        if (l != -1) {
            for (int i = 0; i < l + n; i++) {
                people.put(i, new Person(i));
            }

            //construct original clique
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (Math.random() < pr) {
                        people.get(i).addFriend(people.get(j), 1.0);
                        people.get(j).addFriend(people.get(i), 1.0);
                    }
                }
            }

            //add ndoes

            for (int i = n; i < l + n; i++) {
                for (int j = i - n; j < i; j++) {
                    if (Math.random() < pr) {
                        people.get(i).addFriend(people.get(j), 1.0);
                        people.get(j).addFriend(people.get(i), 1.0);
                    }
                }
            }
        } else {
            for (int i = 1; i < 2 * n ; i++) {
                people.put(i, new Person(i));
            }

            for (int i = 1; i <= n; i++) {
                for (int j = i + 1; j <= n; j++) {
                    if (Math.random() < pr) {
                        people.get(i).addFriend(people.get(j), 1.0);
                        people.get(j).addFriend(people.get(i), 1.0);
                    }
                }
            }

            for (int i = n+1; i < 2 * n ; i++) {
                for (int j = i - 1; j >= 2 * (i - n); j--) {
                    if (Math.random() < pr) {
                        people.get(i).addFriend(people.get(j), 1.0);
                        people.get(j).addFriend(people.get(i), 1.0);
                    }
                }

            }

        }
        int edge = 0;

        for (Person i : people.values()) {
            edge += i.getFriends().size();
        }
    //System.out.println("Number of nodes: " + people.degree() + " Number of edges: " + edge);


    }

}
