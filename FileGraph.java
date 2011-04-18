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
public class FileGraph extends Graph {

    //ToDo: remove intID
    FileGraph(String filename, int N, boolean intID, boolean clique, boolean weighted, String seperator) {

        super();

        System.out.println("Processing file : " + filename);

        LineReader reader = new LineReader(new File(filename));
        double weightx = 0;
        if (reader.token != null) {

            HashMap<String, Integer> id = new HashMap<String, Integer>();

            int perFrom = 0;
            int perTo = 0;
            int edge = 0;
            int line = 0;

            people = new HashMap<Integer, Person>();

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

                        } else {
                            if (!id.containsKey(token[0])) {
                                id.put(token[0], id.size());
                            }
                            perFrom = id.get(token[0]);

                            if (!people.containsKey(perFrom)) {
                                people.put(perFrom, new Person(perFrom, token[0]));
                            }

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
                                        id.put(token[i], id.size());
                                    }
                                    perTo = id.get(token[i]);
                                }

                                //Only allows friends whose id is < N (of course)
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

                                }
                            }

                            // if each line represents a clique
                            if (clique) {
                                for (int i = 1; i < token.length; i++) {
                                    if (intID) {
                                        perFrom = Integer.parseInt(token[i]);
                                    } else {
                                        perFrom = id.get(token[i]);
                                    }
                                    for (int j = i + 1; j < token.length; j++) {
                                        if (intID) {
                                            perTo = Integer.parseInt(token[j]);
                                        } else {
                                            perFrom = id.get(token[i]);
                                        }
                                        if (people.containsKey(perTo)) {
                                            people.get(perFrom).addFriend(people.get(perTo), 1.0);
                                            people.get(perTo).addFriend(people.get(perFrom), 1.0);
                                        }
                                    }
                                }
                            }
                        } else { //Fix me: Weighted, currently only (A B Weight) per line allowed...and assumes undirected weight

                            if (intID) {
                                perTo = Integer.parseInt(token[1]);
                            } else {

                                if (!id.containsKey(token[1])) {
                                    id.put(token[1], id.size());
                                }
                                perTo = id.get(token[1]);
                            }

                            if (perFrom != perTo) { //ignore self connection now
                                if (N == -1 || perTo <= N) {

                                    double weight = Double.parseDouble(token[2]);
                                    weightx += weight;
                                    if (!people.containsKey(perTo)) {
                                        if (intID) {
                                            people.put(perTo, new Person(perTo));
                                        } else {
                                            people.put(perTo, new Person(perTo, token[1]));
                                        }
                                    }

                                    if (!people.get(perFrom).isFriendOf(people.get(perTo))) {
                                    people.get(perFrom).addFriend(people.get(perTo), weight);
                                    people.get(perTo).addFriend(people.get(perFrom), weight);
                                    } else {
                                        if (weight != people.get(perFrom).getWeightTo(people.get(perTo))) {
                                            //System.out.println("Error: " + people.get(perFrom).getName() + " " + people.get(perTo).getName());
                                        }
                                     }
                                }
                            }

                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }

            for (Person i : people.values()) {
                edge += i.getFriends().size();
            }

            System.out.println("Number of nodes: " + people.size() + " Number of edges: " + edge + " Total Weight : " + weightx);


        }
    }

}
