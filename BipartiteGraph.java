/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import java.io.*;

/**
 *
 * @author ixyl2
 */
public class BipartiteGraph extends Graph {

    HashSet<Person> group1;
    HashSet<Person> group2;

    BipartiteGraph(String filename, int N, boolean intID, boolean weighted, String seperator) {

        super();

        System.out.println("Processing file : " + filename);

        LineReader reader = new LineReader(new File(filename));

        if (reader.token != null) {

            HashMap<String, Integer> id = new HashMap<String, Integer>();

            int perFrom = 0;
            int perTo = 0;
            int edge = 0;
            int line = 0;

            people = new HashMap<Integer, Person>();

            group1 = new HashSet<Person>();
            group2 = new HashSet<Person>();

            try {
                for (String t = reader.nextToken(); t != null; t = reader.nextToken()) {


                    t = t.trim();
                    String[] token = t.split(seperator);

                    // if it is a valid line
                    if (token.length > 1) {
                        line++;

                        if (intID) {
                            perFrom = Integer.parseInt(token[0]);

                            if (!people.containsKey(perFrom)) {
                                people.put(perFrom, new Person(perFrom));
                            }

                            group1.add(people.get(perFrom));

                        } else {
                            if (!id.containsKey(token[0])) {
                                id.put(token[0], id.size() + 1);
                            }
                            perFrom = id.get(token[0]);

                            if (!people.containsKey(perFrom)) {
                                people.put(perFrom, new Person(perFrom, token[0]));
                            }

                            group1.add(people.get(perFrom));

                        }

                        //STOPS when detected the N+1 th person
                        // N = -1 means the whole file
                        if (perFrom > N - 1 && N > 0) {
                            System.out.println(people.size() + " people processed.");
                            break;
                        }


                        if (!weighted) {

                            for (int i = 1; i < token.length; i++) {
                                if (intID) {
                                    perTo = Integer.parseInt(token[i]);
                                } else {

                                    if (!id.containsKey(token[i])) {
                                        id.put(token[i], id.size() + 1);
                                    }
                                    perTo = id.get(token[i]);
                                }

                                //Only allows friends whose id is < N (FIX, perTo may not be in order in an int graph)
                                if (N == -1 || perTo <= N) {

                                    if (!people.containsKey(perTo)) {
                                        if (intID) {
                                            people.put(perTo, new Person(perTo));
                                        } else {
                                            people.put(perTo, new Person(perTo, token[i]));
                                        }
                                    }
                                    people.get(perFrom).addFriend(people.get(perTo), 1.0);
                                    people.get(perTo).addFriend(people.get(perFrom), 1.0);

                                    if (group1.contains(people.get(perTo))) {
                                        System.out.println(people.get(perTo).getName());
                                    }

                                    group2.add(people.get(perTo));
                                    if (group2.contains(people.get(perFrom))) {
                                        System.out.println(people.get(perFrom).getName());
                                    }
                                }
                            }

                        } else { //Fix me: Weighted, currently only (A B Weight) per line allowed...and assumes undirected weight

                            if (intID) {
                                perTo = Integer.parseInt(token[1]);
                            } else {

                                if (!id.containsKey(token[1])) {
                                    id.put(token[1], id.size() + 1);
                                }
                                perTo = id.get(token[1]);
                            }

                            if (N == -1 || perTo <= N) {

                                double weight = Double.parseDouble(token[2]);
                                if (!people.containsKey(perTo)) {
                                    if (intID) {
                                        people.put(perTo, new Person(perTo));
                                    } else {
                                        people.put(perTo, new Person(perTo, token[1]));
                                    }
                                }

                                people.get(perFrom).addFriend(people.get(perTo), weight);
                                people.get(perTo).addFriend(people.get(perFrom), weight);

                            }

                            group2.add(people.get(perTo));
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }

            for (Person i : people.values()) {
                edge += i.getFriends().size();
            }

            System.out.println("Number of nodes: " + people.size() + " Number of edges: " + edge);

        }
    }

    Graph projectionGraph(boolean group) {
        Graph g = new Graph();

        HashMap<Integer, Person> gPeople = g.getMap();

        //if group = true, make a projection of group 1 from group 2

        HashSet<Person> groupA = this.group1;
        HashSet<Person> groupB = this.group2;

        if (!group) {
            groupA = this.group2;
            groupB = this.group1;
        }

        for (Person p : groupA) {
            gPeople.put(p.getID(), new Person(p.getID(), p.getName()));
        }

        for (Person p : groupB) {
            ArrayList<Person> clique = new ArrayList<Person>();
            for (Person pf : p.getFriends()) {
                clique.add(gPeople.get(pf.getID()));
            }
            if (clique.size() > 1) {
                g.connectAll(clique);
            }
        }

        for (Person p : gPeople.values()) {
            for (Person pf : p.getFriends()) {
                p.putWeight(pf, p.getWeightTo(pf)/Math.sqrt(people.get(p.getID()).degree()*people.get(pf.getID()).degree()));
            }
        }

        return g;
    }

}
