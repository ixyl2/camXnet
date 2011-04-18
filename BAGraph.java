/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

/**
 *
 * @author ixyl2
 */
public class BAGraph extends Graph {

    BAGraph(int N, int m0, int m) {

        //m0 number of initial nodes
        if (m0 < 2) {
            System.out.println("m0 should be greater than 1, forcing to 2.");
            m0 = 2;
        }

        //number of edges to add per iteration
        if (m > m0) {
            System.out.println("m should be leq m0, forcing to m0.");
            m = m0;
        }

        people.put(0, new Person(0));
        people.put(1, new Person(1));

        people.get(0).addFriend(people.get(1), 1.0);
        people.get(1).addFriend(people.get(0), 1.0);


        for (int i = 2; i < m0; i++) {
            people.put(i, new Person(i));

            int addF = rng.nextInt(people.size()-1);
            people.get(i).addFriend(people.get(addF), 1.0);
            people.get(addF).addFriend(people.get(i), 1.0);
        }

        // Add N - m_0 new nodes to the system.
        for (int i = m0; i < N; i++) {

            int[] cumulativePDF = new int[people.size()];

            // Compute cumulative probability distribution.

            cumulativePDF[0] = people.get(0).degree();

            for (int j = 1; j < people.size(); j++) {
                cumulativePDF[j] = cumulativePDF[j - 1] + people.get(j).degree();
            }


            people.put(i, new Person(i));
            int k = 0; //number of edges connected for this new node so far, has to reach m.
            while (k < m) {
                int random = rng.nextInt(cumulativePDF[cumulativePDF.length - 1]);

                //	Select discrete value corresponding
                //  to matching position in cumulative PDF.

                for (int j = 0; j < cumulativePDF.length; j++) {
                    if (random < cumulativePDF[j]) {
                        if (people.get(i).addFriend(people.get(j), 1.0) && (people.get(j).addFriend(people.get(i), 1.0))) {
                            k++;
                        }
                        break;
                    }
                }

            }

        }
    }

}
