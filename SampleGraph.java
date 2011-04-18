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
public class SampleGraph extends Graph {

    int originalSize;
    HashMap<Integer, HashMap<Integer, Double>> exittimes;

    SampleGraph(Graph graph, int size) { 

        int edge = 0;

        people = new HashMap<Integer, Person>();

        Person p = graph.getRandomPerson();

        people.put(p.getID(), new Person(p.getID()));

        HashSet<Person> q = new HashSet<Person>();

        q.add(p);


        while (q.size() > 0) {
            Person px = q.iterator().next();
            q.remove(px);

            for (Person pxf : px.getFriends()) {
                if (this.size() >= size) {
                    break;
                }
                if (!people.containsKey(pxf.getID())) {
                    q.add(pxf);
                    people.put(pxf.getID(), new Person(pxf.getID()));
                }

            }
            if (this.size() >= size) {
                break;
            }
        }

        for (Person pp : people.values()) {
            for (Person pf : graph.getMap().get(pp.getID()).getFriends()) {
                if (people.containsKey(pf.getID())) {
                    pp.addFriend(people.get(pf.getID()), 1.0);
                    people.get(pf.getID()).addFriend(pp, 1.0);
                    edge += 2;
                }
            }
        }


        System.out.println("Created a sample graph with " + this.size() + " nodes and " + edge + " edges.");
    }

}

