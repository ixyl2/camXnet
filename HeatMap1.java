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
class Restaurant {

    Restaurant(int count, double lat, double lng) {
        this.count = count;
        this.lat = lat;
        this.lng = lng;
    }
    int count;
    double lat;
    double lng;
}

public class HeatMap1 implements Serializable {

    KDTree<Restaurant> map;
    HashSet<Restaurant> set;

    HeatMap1() {
    }

    void read(String filename, String seperator,  int lines, int latc, int lngc) {


        int line = 0;

        map = new KDTree<Restaurant>(2);
        set = new HashSet<Restaurant>();

        System.out.println("Processing file : " + filename);

        LineReader reader = new LineReader(new File(filename));


        if (reader.token != null) {


            try {


                for (String t = reader.nextToken(); t != null; t = reader.nextToken()) {
                    line++;

                    t = t.trim();
                    String[] token = t.split(seperator);




                    // if it is a valid line
                    if (token.length > 1) {


                        try {
                            double lat = Double.parseDouble(token[latc]);
                            double lng = Double.parseDouble(token[lngc]);

                            double[] coord = {lng, lat};


                            if (map.search(coord) != null) {
                                Restaurant x = map.search(coord);
                                x.count += 1;

                            } else {
                                Restaurant r = new Restaurant(1, lat, lng);
                                map.insert(coord, r);
                                set.add(r);
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

    void spread(MapGraph graph, double bandwidth) {

        double w = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.maxlat, graph.minlng);
        double l = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.minlat, graph.maxlng);

        double xcut = Math.abs(graph.maxlng - graph.minlng) * (bandwidth / w) / 2;
        double ycut = Math.abs(graph.maxlat - graph.minlat) * (bandwidth / l) / 2;


        for (Restaurant r : set) {

            double[] mink = {r.lng - xcut, r.lat - ycut};
            double[] maxk = {r.lng + xcut, r.lat + ycut};

            try {
                List<MapNode> nodes = graph.kdtree.range(mink, maxk);

                //MapNode nearest = graph.kdtree.nearest(new double[]{r.lng, r.lat});
                //System.out.println(MapGraph.distFrom(r.lat, r.lng, nearest.lat, nearest.lng));
                //System.out.println(nodes.size());
                for (MapNode n : nodes) {
                    n.restCount += r.count / (double) nodes.size();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }


    }

    double query(double lat, double lng, MapGraph graph, double bandwidth, boolean KDE) {
        double count = 0;

        double w = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.maxlat, graph.minlng);
        double l = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.minlat, graph.maxlng);

        double xcut = Math.abs(graph.maxlng - graph.minlng) * (bandwidth / w) / 2;
        double ycut = Math.abs(graph.maxlat - graph.minlat) * (bandwidth / l) / 2;

        double[] mink = {lng - xcut, lat - ycut};
        double[] maxk = {lng + xcut, lat + ycut};

        try {
            List<Restaurant> results = map.range(mink, maxk);
            List<MapNode> nodes = graph.kdtree.range(mink, maxk);


            if (!results.isEmpty()) {
                for (Restaurant r : results) {
                    if (KDE) {
                        count += r.count * Main.normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                    } else {
                        count += r.count;
                    }
                }

                if (!nodes.isEmpty()) {
                    //count /= nodes.size();
                }
                if (KDE) {
                    count /= results.size() * bandwidth;
                }
            }


//            if (!results.isEmpty()) {
//                count /= (double) results.size();
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return count;
    }
}
