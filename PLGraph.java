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
public class PLGraph extends Graph {//wtf?

    PLGraph(int N, int E, double e) {

        ArrayList<Person> a = new ArrayList<Person>(people.values());
        

        for (int i = 0; i < N; i++) {
            System.out.println(i);
            people.put(i, new Person(i));
        }


        if (E > 0) {
            int actualE = 0;
            while (actualE < E) {
                Person p1 = people.get(rng.nextInt(people.size()));
                Person p2 = people.get(rng.nextInt(people.size()));

                if ((p1.addFriend(p2, 1.0)) && (p2.addFriend(p1, 1.0))) {
                    actualE++;
                }
            }
        }
    }
}
