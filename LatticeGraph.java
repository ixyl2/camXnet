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
public class LatticeGraph extends Graph{

    LatticeGraph(int N) {
        for (int i = 0; i < N; i++) {
            people.put(i, new Person(i));
        }

        int length = (int)Math.sqrt((double) N);

         for (int i = 0; i < N; i++) {
            people.get(i).addFriend(people.get((i+1)%length), 1.0);
        }
    }

}
