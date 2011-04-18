/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

/**
 *
 * @author ixyl2
 */
public class Clique extends Graph {

    Clique(int n) { //Length of ring, Size of clique


            for (int i = 0; i < n; i++) {
                people.put(i, new Person(i));
            }

            //construct original clique
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                        people.get(i).addFriend(people.get(j), 1.0);
                        people.get(j).addFriend(people.get(i), 1.0);
                }
            }
    }
}
