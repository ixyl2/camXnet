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
public class randomGraph extends Graph {

    randomGraph(int N, int E, double p) {

        super();
        
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

        if (p > 0) {

            for (int i = 0; i < people.size(); i++) {

                for (int j = 0; j < people.size(); j++) {
                    if ((i != j) && (rng.nextDouble() < p)) {
                        people.get(i).addFriend(people.get(j), 1.0);
                        people.get(j).addFriend(people.get(i), 1.0);
                    }

                }
            }

        }

    }
}
