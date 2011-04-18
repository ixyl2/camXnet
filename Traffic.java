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
public class Traffic {

    static void readTraffic(MapGraph graph, String file, double thresh, int lines) {
        LineReader reader = new LineReader(new File(file));
        HashSet<MapNode> s = new HashSet<MapNode>();
        Calendar cal = Calendar.getInstance();

        int line = 0;

        int numTrips = 0;
        int okTrips = 0;
        boolean first = true;
        int count = 0;
        int firstTime = 0;
        int time = 0;
        double dist = 0;

        double oldlat = 0;
        double oldlng = 0;

        double firstlat = 0;
        double firstlng = 0;

        double lat = 0;
        double lng = 0;

        try {

            int tripLength = 0;
            for (String t = reader.nextToken(); t != null; t = reader.nextToken()) {
                if (t.length() > 0) {

                    //list.add(t);

                    String[] token = t.split("\\s");

                    lat = Double.parseDouble(token[0]);
                    lng = Double.parseDouble(token[1]);
                    time = Integer.parseInt(token[2]);

                    if (first) {
                        numTrips++;
                        firstTime = time;
                        firstlat = lat;
                        firstlng = lng;
                        first = false;
                    } else {
                        dist += MapGraph.distFrom(lat, lng, oldlat, oldlng);
                    }

                    MapNode n = graph.getClosestNode(lat, lng);

                    if (MapGraph.distFrom(lat, lng, n.lat, n.lng) < 30) {
                        s.add(n);
                    }

                    count++;

                    oldlat = lat;
                    oldlng = lng;

                } else {
                    //empty line reached...
//                    System.out.println(count);
//                    System.out.println(s.size());

//                    cal.setTimeInMillis(firstTime * 1000);
//                    cal.setTimeZone(TimeZone.getTimeZone("GMT-7"));
//                    int day = cal.get(Calendar.DAY_OF_WEEK);
//                    int hour = cal.get(Calendar.HOUR_OF_DAY);
//
//
//                    boolean rushHour = false;
//
//                    if (day != Calendar.SUNDAY && day != Calendar.SATURDAY) {
//                        if ((hour >= 6 && hour <= 10) || (hour >= 16 && hour <= 19)) {
//                            rushHour = true;
//                        }
//                    }

                    //  if (rushHour) {
                    //if (firstTime >= 1212054169) {
                        if (dist <= MapGraph.distFrom(firstlat, firstlng, lat, lng) * thresh) {
                            okTrips++;

                            for (MapNode n : s) {
                                n.visitCount += 1;
                            }
                        }
                    //}
                    //  }
                    s.clear();
                    count = 0;
                    first = true;
                    dist = 0;
                    if (lines != -1 && line > lines) {
                        break;
                    }

                }
                line++;

                if (line % 500000 == 0) {
                    System.out.println(line);
                    System.out.println(okTrips / (double) numTrips);
                }




            }


            System.out.println("Done!!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
