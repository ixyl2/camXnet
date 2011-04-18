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
class Point {

    Point(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    int or, on, ur, un;
    double lat;
    double lng;

    int getRush() {
        return or + ur;
    }

    int getNonRush() {
        return on + un;
    }

    int getOccupied() {
        return or + on;
    }

    int getUnOccupied() {
        return ur + un;
    }

    int getAll() {
        return or + ur + on + un;
    }
}

public class TrafMap implements Serializable {

    KDTree<Point> map;

    TrafMap() {
    }

    void read(String filename, String seperator, int lines, int latc, int lngc, int occuc, int timec, int precision, String timezone) {


        int line = 0;

        map = new KDTree<Point>(2);

        System.out.println("Processing file : " + filename);

        LineReader reader = new LineReader(new File(filename));

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(timezone));

        if (reader.token != null) {


            try {


                for (String t = reader.nextToken(); t != null; t = reader.nextToken()) {
                    line++;

                    t = t.trim();
                    String[] token = t.split(seperator);


                    // if it is a valid line
                    if (token.length > 1) {


                        try {

                            boolean occupied = Integer.parseInt(token[occuc]) > 0;

                            cal.setTimeInMillis(Integer.parseInt(token[timec]) * 1000);
                            
                            int day = cal.get(Calendar.DAY_OF_WEEK);
                            int hour = cal.get(Calendar.HOUR_OF_DAY);


                            boolean rushHour = false;

                            if (day != Calendar.SUNDAY && day != Calendar.SATURDAY) {
                                if ((hour >= 6 && hour <= 10) || (hour >= 16 && hour <= 19)) {
                                    rushHour = true;
                                }
                            }


                            double pw = Math.pow(10, precision);
                            double lat = ((int) (Double.parseDouble(token[latc]) * pw)) / pw;
                            double lng = ((int) (Double.parseDouble(token[lngc]) * pw)) / pw;

//                            double lat = Double.parseDouble(token[latc]);
//                            double lng = Double.parseDouble(token[lngc]);
                            double[] coord = {lng, lat};

                            if (map.search(coord) != null) {
                                Point x = map.search(coord);

                                if (occupied) {
                                    if (rushHour) {
                                        ++x.or;
                                    } else {
                                        ++x.on;
                                    }
                                } else {
                                    if (rushHour) {
                                        ++x.ur;
                                    } else {
                                        ++x.un;
                                    }
                                }
                            } else {
                                Point r = new Point(lat, lng);

                                if (occupied) {
                                    if (rushHour) {
                                        ++r.or;
                                    } else {
                                        ++r.on;
                                    }
                                } else {
                                    if (rushHour) {
                                        ++r.ur;
                                    } else {
                                        ++r.un;
                                    }
                                }

                                map.insert(coord, r);

                            }


                        } catch (Exception ex) {
                            //System.err.println("Trace reading error in " + filename + " line" + line);
                            //ex.printStackTrace();
                        }


                    }
                    if (lines != -1 && line > lines) {
                        break;
                    }

                    if (line % 500000 == 0) {
                        System.out.println(line);
                    }

                }


                System.out.println(line + " lines");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
//    double query(double lat, double lng, MapGraph graph, double bandwidth, boolean KDE) {
//        double count = 0;
//
//        double w = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.maxlat, graph.minlng);
//        double l = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.minlat, graph.maxlng);
//
//        double xcut = Math.abs(graph.maxlng - graph.minlng) * (bandwidth / w) / 2;
//        double ycut = Math.abs(graph.maxlat - graph.minlat) * (bandwidth / l) / 2;
//
//        double[] mink = {lng - xcut, lat - ycut};
//        double[] maxk = {lng + xcut, lat + ycut};
//
//        try {
//            List<Point> results = map.range(mink, maxk);
//            List<MapNode> nodes = graph.kdtree.range(mink, maxk);
//
//
//            if (!results.isEmpty()) {
//                for (Point r : results) {
//                    if (KDE) {
//                        count += r.count * Main.normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
//                    } else {
//                        count += r.count;
//                    }
//                }
//
//                if (!nodes.isEmpty()) {
//                    //count /= nodes.size();
//                }
//                if (KDE) {
//                    count /= results.size() * bandwidth;
//                }
//            }
//
//
////            if (!results.isEmpty()) {
////                count /= (double) results.size();
////            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return count;
//    }
}
