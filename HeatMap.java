/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import java.io.*;
import edu.wlu.cs.levy.CG.*;

/**
 *
 * @author ixyl2
 */
public class HeatMap implements Serializable {

    KDTree<Integer> map;
    MapGraph graph;

//    void serialise(String filename) throws IOException {
//        for (Person p : this.getPeople()) {
//            p.serialise();
//        }
//        ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream(filename));
//        objstream.writeObject(this);
//        objstream.close();
//        for (Person p : this.getPeople()) {
//            p.friendList.clear();
//        }
//    }
//
//    private static Object loadFrom(String filename) throws ClassNotFoundException, IOException {
//        ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(filename));
//        Object object = objstream.readObject();
//        objstream.close();
//        return object;
//    }
//
    HeatMap(MapGraph graph) {
        this.graph = graph;
    }

    void read(String filename, String seperator, int lines) {

        for (MapNode p : graph.people.values()) {
            p.iniBet(1);
            p.resetBet(0);
        }

        int line = 0;

        map = new KDTree<Integer>(2);

        System.out.println("Processing file : " + filename);

        LineReader reader = new LineReader(new File(filename));


        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        int validRead = 0;

        if (reader.token != null) {


            try {
                double oldy = 0;
                double oldx = 0;
                int oldtime = 0;
                int oldLine = 0;

                MapNode oldNode = null;

                for (String t = reader.nextToken(); t != null; t = reader.nextToken()) {


                    t = t.trim();
                    String[] token = t.split(seperator);

                    Calendar cal = Calendar.getInstance();

                    // if it is a valid line
                    if (token.length > 1) {
                        line++;

                        if ((line > lines) && (lines > 0)) {
                            break;
                        }

                        //int y = (int) Math.round(Double.parseDouble(token[0]) * Math.pow(10, precision)); //lat
                        //int x = (int) Math.round(Double.parseDouble(token[1]) * Math.pow(10, precision)); //lng

                        double lat = Double.parseDouble(token[0]);
                        double lng = Double.parseDouble(token[1]);

                        int time = Integer.parseInt(token[3]);

                        if (time > max) {
                            max = time;
                        }
                        if (time < min) {
                            min = time;
                        }

                       int occupied = Integer.parseInt(token[2]);

                        //System.out.println(time);

                        if (occupied >= 1) {
                            //if (occupied == 1) {
                            //System.out.println(t);

                            int timed = Math.abs(oldtime- time);

                            double speed = 0;
                            double dist = 0;
                            if (line > 0) {
                                speed = MapGraph.speedFrom(lat, lng, oldy, oldx, timed);
                                dist = MapGraph.distFrom(lat, lng, oldy, oldx);
                                // System.out.println(speed);
                            }

                            //1172620800-1170201601 = 2419199 (shanghai)
                            //1170201601 + 1209599 = 1171411200
                            
                            
                            //1212054169 (SF)
                            //System.out.println(Double.parseDouble(token[0]) + " " + speed + " " + (oldtime - time));

                            cal.setTimeInMillis(Long.parseLong(token[3]) * 1000);
                            //cal.setTimeZone(TimeZone.getTimeZone("GMT-7"));
                            int day = cal.get(Calendar.DAY_OF_WEEK);
                            int hour = cal.get(Calendar.HOUR_OF_DAY);


                            boolean rushHour = false;

                            if (day != Calendar.SUNDAY && day != Calendar.SATURDAY) {
                                if ((hour >= 7 && hour <= 10) || (hour >= 16 && hour <= 19)) {
                                    rushHour = true;
                                }
                            }

                            //                    System.out.println(cal);

                            if ((speed <= 120) && timed <= 60) {


//                                double[] coord = {lat, lng};
//                                if (map.search(coord) != null) {
//                                    int x = map.search(coord);
//                                    map.delete(coord);
//                                    map.insert(coord, x + 1);
//                                } else {
//                                    map.insert(coord, 1);
//                                }

                                //keep building the path until the line is jumped


                                if (line - oldLine == 1) {
                                    oldNode = graph.registerTravel(lat, lng, oldy, oldx, oldNode);
                                } else {
                                    oldNode = graph.registerTravel(lat, lng, oldy, oldx, null);
                                }

                                if (oldNode != null) {
                                    validRead++;
                                }

//                                if (map.containsKey(x)) {
//                                    if (map.get(x).containsKey(y)) {
//                                        map.get(x).put(y, map.get(x).get(y) + 1);
//                                    } else {
//                                        map.get(x).put(y, 1);
//                                    }
//                                } else {
//                                    map.put(x, new TreeMap<Integer, Integer>());
//                                    map.get(x).put(y, 1);
//                                }

                                oldLine = line;
                            }


                            oldy = lat;
                            oldx = lng;
                            oldtime = time;
                        }
                        if (line % 50000 == 0) {
                            System.out.println(line);
                        }

                    }


                }

                System.out.println(line + " lines" + validRead + " valid reads");
                System.out.println("Min : " + min + " Max : " + max);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    double query(double lat, double lng, double range) {
        double count = 0;

        double[] mink = {lat, lng};
        double[] maxk = {lat + range, lng + range};
        try {
            List<Integer> results = map.range(mink, maxk);

            for (Integer i : results) {
                count += i;
            }

            if (!results.isEmpty()) {
                count /= (double) results.size();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return count;
    }
}
