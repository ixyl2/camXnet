/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import java.util.Random;
import java.io.*;
import camxnet.Utils.*;
import java.text.*;
import edu.wlu.cs.levy.CG.*;
import cern.jet.stat.Probability.*;

/**
 *
 * @author ixyl2
 */
public class Main {

    static Random rng = new Random();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // file name, size to crawl, integer?, clique?, spacing
        //default args[0], -1, true, false, \\s
        if (args.length != 0) {
            System.out.println("Usage : java -jar camxnet network.dat");
        } else {
            // File Graph
            // String filename, int N, boolean intID, boolean clique, boolean weighted, String seperator
            // Other Graphs
            //graph = new Clique(11);
            //System.out.println(graph.getPerson(1).getClustering());
            //dynamic(graph, 300);
            //bipartite(graph,"symptoms.txt");
            //graph = new Graph("lympho.bin");
            //marco();
            //MapGraph graph = new MapGraph("sh.txt", -1, 0, "\\s", 5);
//            for (int j = 0; j < 5; j++) {
//
//                String trafficFile = "";
//
//                switch (j) {
//                    case 0:
//                        trafficFile = "SF-simplify-120-20diam-overall.traf";
//                        break;
//                    case 1:
//                        trafficFile = "SF-simplify-120-20diam-rush.traf";
//                        break;
//                    case 2:
//                        trafficFile = "SF-simplify-120-20diam-nonrush.traf";
//                        break;
//                    case 3:
//                        trafficFile = "SF-simplify-120-20diam-1sthalf.traf";
//                        break;
//                    case 4:
//                        trafficFile = "SF-simplify-120-20diam-2ndhalf.traf";
//                }
            //System.out.println("NW S Betweenness........." + trafficFile);
            //MapGraph graph = new MapGraph("sh.txt", -1, 0, "\\s", 5, true);
            MapGraph graph = new MapGraph("sfx.txt", -1, 0, "\\s", 5, true);
            //((MapGraph) graph).simplify();
            graph.getComponents();
            graph.simplify();
            graph.runMaxMin();
            //HeatMap map = new HeatMap(graph);
            //map.read("all.txt", "\\s", -1);
            //map.read("shtaxi.txt", "\\s", -1);
            //
            //Traffic.readTraffic(graph, "C:/Users/ixyl2/Code/camXnet_1/SF-extended.txt", 1.5, 1000000);
            //Traffic.readTraffic(graph, "C:/Users/ixyl2/Code/camXnet_1/shtaxi-ext.txt", 1.5, 50000000);
            //graph.outputTraffic("SH-simplify-20diam-1.5-2ndhalf");
            //
            //readTraffic(graph, "SF-simplify-60-20diam-rush.traf");
            //
//                calCloseness(new ArrayList<MapNode>(graph.people.values()), null, 3, -1, 1, 1);
//
            TrafMap traf = new TrafMap();
            HeatMap1 rest = new HeatMap1();


            //SF
            //traf.read("all.txt", "\\s", 1000000, 0, 1, 2, 3, 4, "GMT-7");
            rest.read("rest.txt", "\\s", -1, 0, 1);


            //SH
            //traf.read("shtaxi.txt", "\\s", 1000000, 0, 1, 2, 3, 4, "GMT-0");
            //rest.read("shrest.txt", ",,", -1, 2, 1);

            rest.spread(graph, 300);


            for (MapNode p : graph.people.values()) {
                p.weight = p.restCount;
            }

//
            //calBet(new ArrayList<MapNode>(graph.people.values()), null, 4, graph.size() / 10, 1, 0);


//            graph.outputTLP("SF-edgebet", true, 3);
//            graph.outputTLP("SF-nodebet", true, 0);
//
            //pixelCorrelate(graph, rest, traf, 10, 200);
            //pixelCorrelate2(graph, rest, traf, 10, 300);
            pixelCorrelate3(graph, rest, traf, 10, 300);
            //nodeCorrelate(graph);
            //////////////////////////////////////
//                System.out.println("Speed Betweenness.........");
//
//                graph = new MapGraph("sfx.txt", -1, 0, "\\s", 5, true);
//
//                graph.getComponents();
//                graph.simplify();
//                graph.runMaxMin();
//
//
//                readTraffic(graph, trafficFile);
//
//
//                for (MapNode p : graph.people.values()) {
//                    p.weight = 1;
//                }
//
//                calBet(new ArrayList<MapNode>(graph.people.values()), null, 3, graph.size() , 1, 0);
//
//                pixelCorrelate(graph, rest, 10, 300);
//                nodeCorrelate(graph);
//
//                //////////////////////////////////////
//
//                System.out.println("Euclidean Betweenness.........");
//
//                graph = new MapGraph("sfx.txt", -1, 0, "\\s", 5, false);
//
//                graph.getComponents();
//                graph.simplify();
//                graph.runMaxMin();
//
//
//                readTraffic(graph, trafficFile);
//
//
//                for (MapNode p : graph.people.values()) {
//                    p.weight = 1;
//                }
//
//                calBet(new ArrayList<MapNode>(graph.people.values()), null, 3, graph.size() , 1, 0);
//
//                pixelCorrelate(graph, rest, 10, 300);
//                nodeCorrelate(graph);
//
//                //////////////////////////////////////
//
//                System.out.println("Normal Betweenness.........");
//
//                graph = new MapGraph("sfx.txt", -1, 0, "\\s", 5, false);
//
//                graph.getComponents();
//                graph.printCompCount();
//                graph.simplify();
//                graph.runMaxMin();
//
//
//                readTraffic(graph, trafficFile);
//
//
//                calBet(new ArrayList<MapNode>(graph.people.values()), null, 3, graph.size() , 0, 0);
//
//                pixelCorrelate(graph, rest, 10, 300);
//                nodeCorrelate(graph);
            //calCloseness(new ArrayList<MapNode>(graph.people.values()), null, 3, -1, 1);
            //   graph.outputBet("SF-Density");
////
//            graph.outputTLP("SF-Simplified-WeightedDistance-Bet", true, 0);
            //graph.outputTLP("SF-simplify-rest", true, 3);
//            }
        }
    }

    static double normalY(double x) {
        return Math.pow(2 * Math.PI, -0.5) * Math.exp((x * x) / 2);
    }

    static void readTraffic(MapGraph graph, String file) {
        System.out.println("Processing ..." + file);
        LineReader reader = new LineReader(new File(file));
        if (reader.token != null) {
            try {
                for (String t = reader.nextToken(); t != null; t = reader.nextToken()) {
                    t = t.trim();
                    String[] token = t.split("\\s");
                    // if it is a valid line
                    if (token.length > 1) {
                        int idx = Integer.parseInt(token[0]);
                        if (graph.people.get(idx) != null) {
                            graph.people.get(idx).visitCount = Double.parseDouble(token[1]);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    static void nodeCorrelate(MapGraph graph) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        int i = 0;
        ArrayList<Double> visits = new ArrayList<Double>();
        ArrayList<Double> close = new ArrayList<Double>();
        ArrayList<Double> restx = new ArrayList<Double>();
        ArrayList<Double> bet = new ArrayList<Double>();
        ArrayList<Double> deg = new ArrayList<Double>();
        for (MapNode p : graph.people.values()) {
            restx.add(p.restCount);
            close.add(p.closeness);
            bet.add(p.between);
            deg.add((double) p.degree());
            visits.add(p.visitCount);
            //mix.add(Math.log(1/p.closeness)*p.between);
            i++;
        }
        System.out.println("rest v visit" + twoDForm.format(getPearsonCorrelation(restx, visits)) + " Deg" + twoDForm.format(getPearsonCorrelation(deg, visits)) + "Close" + twoDForm.format(getPearsonCorrelation(close, visits)) + "Bet" + twoDForm.format(getPearsonCorrelation(bet, visits)));
    }

    static class DPoint<T> {

        DPoint(T count, double lat, double lng) {
            this.count = count;
            this.lat = lat;
            this.lng = lng;
        }
        T count;
        double lat;
        double lng;
    }

    static void pixelCorrelate(MapGraph graph, HeatMap1 rest, TrafMap traffic, int width, double bandwidth) {

        KDTree<DPoint<Double>> restMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> closeMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> degMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> betMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> nodeRestMap = new KDTree<DPoint<Double>>(2);

        //KDTree<DPoint<Double>> trafficMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> occuRushMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> occuNonMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> occuMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> unOccRushMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> unOccNonMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> unOccMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> rushMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> nonMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> allMap = new KDTree<DPoint<Double>>(2);

        DecimalFormat twoDForm = new DecimalFormat("#.###");
        //lng is xaxis
        double w = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.maxlat, graph.minlng);
        double l = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.minlat, graph.maxlng);
        int xcuts = (int) w / width;
        int ycuts = (int) l / width;
        double xcut = Math.abs(graph.maxlng - graph.minlng) / (double) xcuts;
        double ycut = Math.abs(graph.maxlat - graph.minlat) / (double) ycuts;
        double offset = (bandwidth - width) / 2 / width;
        int ii = 0;
        System.out.println("Pixel " + xcuts + " x " + ycuts + " Array Length " + xcuts * ycuts);
        for (int j = 0; j < xcuts; j++) {
            for (int k = 0; k < ycuts; k++) {
                double lng = graph.minlng + (j + 0.5) * xcut;
                double lat = graph.minlat + (k + 0.5) * ycut;
                double[] mink = {graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut};
                double[] maxk = {graph.minlng + (j + 1 + offset) * xcut, graph.minlat + (k + 1 + offset) * ycut};
                double[] mink10 = {graph.minlng + j * xcut, graph.minlat + k * ycut};
                double[] maxk10 = {graph.minlng + (j + 1) * xcut, graph.minlat + (k + 1) * ycut};
                try {
                    List<MapNode> results = graph.kdtree.range(mink, maxk);
                    if (!results.isEmpty()) {
                        List<Point> trafficResults = traffic.map.range(mink10, maxk10);
                        List<Restaurant> restResults = rest.map.range(mink10, maxk10);
                        if (!restResults.isEmpty()) {
                            double rCount = 0;
                            for (Restaurant r : restResults) {
                                rCount += r.count;
                            }
                            restMap.insert(new double[]{lng, lat}, new DPoint<Double>(rCount, lat, lng));
                        }
                        if (!trafficResults.isEmpty()) {
                            double orc = 0, onc = 0, oc = 0, urc = 0, unc = 0, uc = 0, rc = 0, nc = 0, ac = 0;
                            for (Point r : trafficResults) {
                                orc += r.or;
                                onc += r.on;
                                oc += r.getOccupied();
                                urc += r.ur;
                                unc += r.un;
                                uc += r.getUnOccupied();
                                rc += r.getRush();
                                nc += r.getNonRush();
                                ac += r.getAll();
                            }
                            if (orc != 0) {
                                occuRushMap.insert(new double[]{lng, lat}, new DPoint<Double>(orc, lat, lng));
                            }
                            if (onc != 0) {
                                occuNonMap.insert(new double[]{lng, lat}, new DPoint<Double>(onc, lat, lng));
                            }
                            if (oc != 0) {
                                occuMap.insert(new double[]{lng, lat}, new DPoint<Double>(oc, lat, lng));
                            }
                            if (urc != 0) {
                                unOccRushMap.insert(new double[]{lng, lat}, new DPoint<Double>(urc, lat, lng));
                            }
                            if (unc != 0) {
                                unOccNonMap.insert(new double[]{lng, lat}, new DPoint<Double>(unc, lat, lng));
                            }
                            if (uc != 0) {
                                unOccMap.insert(new double[]{lng, lat}, new DPoint<Double>(uc, lat, lng));
                            }
                            if (rc != 0) {
                                rushMap.insert(new double[]{lng, lat}, new DPoint<Double>(rc, lat, lng));
                            }
                            if (nc != 0) {
                                nonMap.insert(new double[]{lng, lat}, new DPoint<Double>(nc, lat, lng));
                            }
                            if (ac != 0) {
                                allMap.insert(new double[]{lng, lat}, new DPoint<Double>(ac, lat, lng));
                            }
                        }
                    }
                    results = graph.kdtree.range(mink10, maxk10);
                    if (!results.isEmpty()) {
                        ii++;
                        double bCount = 0;
                        double cCount = 0;
                        double dCount = 0;
                        double rCount = 0;
                        for (MapNode n : results) {
                            bCount += n.between;
                            cCount += n.closeness;
                            dCount += n.degree();
                            rCount += n.restCount;
                        }
                        betMap.insert(new double[]{lng, lat}, new DPoint<Double>(bCount / (double) results.size(), lat, lng));
                        closeMap.insert(new double[]{lng, lat}, new DPoint<Double>(cCount / (double) results.size(), lat, lng));
                        degMap.insert(new double[]{lng, lat}, new DPoint<Double>(dCount / (double) results.size(), lat, lng));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        double[] closex = new double[ii];
        double[] degx = new double[ii];
        double[] betx = new double[ii];
        double[] restx = new double[ii];
        // double[] visitx = new double[ii];
        double[] occuRush = new double[ii];
        double[] occuNon = new double[ii];
        double[] occu = new double[ii];
        double[] unOccRush = new double[ii];
        double[] unOccNon = new double[ii];
        double[] unOcc = new double[ii];
        double[] rush = new double[ii];
        double[] non = new double[ii];
        double[] all = new double[ii];
        ii = 0;
        for (int j = 0; j < xcuts; j++) {
            for (int k = 0; k < ycuts; k++) {
                double lng = graph.minlng + (j + 0.5) * xcut;
                double lat = graph.minlat + (k + 0.5) * ycut;
                double[] mink10 = {graph.minlng + j * xcut, graph.minlat + k * ycut};
                double[] maxk10 = {graph.minlng + (j + 1) * xcut, graph.minlat + (k + 1) * ycut};
                double[] mink = {graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut};
                double[] maxk = {graph.minlng + (j + 1 + offset) * xcut, graph.minlat + (k + 1 + offset) * ycut};
//                System.out.println(MapGraph.distFrom(graph.minlat + (k - offset) * ycut, graph.minlng + (j - offset) * xcut, graph.minlat + (k + 1 + offset) * ycut, graph.minlng + (j - offset) * xcut));
//                System.out.println(MapGraph.distFrom(graph.minlat + (k - offset) * ycut, graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut, graph.minlng + (j + 1 + offset) * xcut));
                try {
                    List<MapNode> results = graph.kdtree.range(mink10, maxk10);
                    if (!results.isEmpty()) {
                        results = graph.kdtree.range(mink, maxk);
                        restx[ii] = 0;
                        betx[ii] = 0;
//                        visitx[ii] = 0;
                        degx[ii] = 0;
                        closex[ii] = 0;
                        occuRush[ii] = 0;
                        occuNon[ii] = 0;
                        occu[ii] = 0;
                        unOccRush[ii] = 0;
                        unOccNon[ii] = 0;
                        unOcc[ii] = 0;
                        rush[ii] = 0;
                        non[ii] = 0;
                        all[ii] = 0;
                        List<DPoint<Double>> _results = restMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                restx[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = betMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                betx[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = closeMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                closex[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = degMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                degx[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = occuRushMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                occuRush[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = occuNonMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                occuNon[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = occuMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                occu[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = unOccRushMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                unOccRush[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = unOccNonMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                unOccNon[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = unOccMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                unOcc[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = rushMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                rush[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = nonMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                non[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = allMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                all[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            ii++;
        }
        System.out.println("Array Length : "
                + closex.length);
        System.out.println(
                " \tOccupied+Rush\tOccupied+Non Rush\tOccupied\tUnOccupied+Rush\tUnoccupied+NonRush\tUnOccupied\tRush\tNonRush\tAll");
        System.out.println(
                "Deg\t" + twoDForm.format(getPearsonCorrelation(degx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, all)));
        System.out.println(
                "Closeness\t" + twoDForm.format(getPearsonCorrelation(closex, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, non)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, all)));
        //System.out.println("Visit\t" + twoDForm.format(getPearsonCorrelation(visitx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, all)));
        System.out.println(
                "Rest \t" + twoDForm.format(getPearsonCorrelation(restx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, all)));
        System.out.println(
                "Bet\t" + twoDForm.format(getPearsonCorrelation(betx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, all)));
    }

    static void pixelCorrelate2(MapGraph graph, HeatMap1 rest, TrafMap traffic, int width, double bandwidth) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        //lng is xaxis
        double w = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.maxlat, graph.minlng);
        double l = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.minlat, graph.maxlng);
        int xcuts = (int) w / width;
        int ycuts = (int) l / width;
        double xcut = Math.abs(graph.maxlng - graph.minlng) / (double) xcuts;
        double ycut = Math.abs(graph.maxlat - graph.minlat) / (double) ycuts;
        double offset = (bandwidth - width) / 2 / width;
        int ii = 0;
        for (int j = 0; j < xcuts; j++) {
            for (int k = 0; k < ycuts; k++) {
                double[] mink10 = {graph.minlng + j * xcut, graph.minlat + k * ycut};
                double[] maxk10 = {graph.minlng + (j + 1) * xcut, graph.minlat + (k + 1) * ycut};
                try {

                    List<MapNode> results = graph.kdtree.range(mink10, maxk10);
                    if (!results.isEmpty()) {
                        ii++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        double[] closex = new double[ii];
        double[] degx = new double[ii];
        double[] betx = new double[ii];
        double[] restx = new double[ii];
        // double[] visitx = new double[ii];
        double[] occuRush = new double[ii];
        double[] occuNon = new double[ii];
        double[] occu = new double[ii];
        double[] unOccRush = new double[ii];
        double[] unOccNon = new double[ii];
        double[] unOcc = new double[ii];
        double[] rush = new double[ii];
        double[] non = new double[ii];
        double[] all = new double[ii];
        System.out.println("Pixel " + xcuts + " x " + ycuts + " Array Length " + closex.length);
        ii = 0;
        for (int j = 0; j < xcuts; j++) {
            for (int k = 0; k < ycuts; k++) {
                double[] mink10 = {graph.minlng + j * xcut, graph.minlat + k * ycut};
                double[] maxk10 = {graph.minlng + (j + 1) * xcut, graph.minlat + (k + 1) * ycut};
                double[] mink = {graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut};
                double[] maxk = {graph.minlng + (j + 1 + offset) * xcut, graph.minlat + (k + 1 + offset) * ycut};
//                System.out.println(MapGraph.distFrom(graph.minlat + (k - offset) * ycut, graph.minlng + (j - offset) * xcut, graph.minlat + (k + 1 + offset) * ycut, graph.minlng + (j - offset) * xcut));
//                System.out.println(MapGraph.distFrom(graph.minlat + (k - offset) * ycut, graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut, graph.minlng + (j + 1 + offset) * xcut));
                try {
                    List<MapNode> results = graph.kdtree.range(mink10, maxk10);
                    if (!results.isEmpty()) {
                        List<Restaurant> restResults = rest.map.range(mink, maxk);
                        List<Point> trafficResults = traffic.map.range(mink, maxk);
                        results = graph.kdtree.range(mink, maxk);
                        restx[ii] = 0;
                        betx[ii] = 0;
                        //visitx[ii] = 0;
                        degx[ii] = 0;
                        closex[ii] = 0;
                        occuRush[ii] = 0;
                        occuNon[ii] = 0;
                        occu[ii] = 0;
                        unOccRush[ii] = 0;
                        unOccNon[ii] = 0;
                        unOcc[ii] = 0;
                        rush[ii] = 0;
                        non[ii] = 0;
                        all[ii] = 0;

//                        if (!restResults.isEmpty()) {
//                            //int rc = 0;
//                            for (Restaurant r : restResults) {
//                                restx[ii] += r.count * normalY(MapGraph.distFrom(graph.minlat + (k + 0.5) * ycut, graph.minlng + (j + 0.5) * xcut, r.lat, r.lng) / bandwidth);
//                                //rc += r.count;
//                            }
//                            //restx[ii] /= rc * bandwidth;
//                        }

                        if (!trafficResults.isEmpty()) {
//                            int orc = 0, onc = 0, oc = 0, urc = 0, unc = 0, uc = 0, rc = 0, nc = 0, ac = 0;
                            for (Point r : trafficResults) {
                                double ww = normalY(MapGraph.distFrom(graph.minlat + (k + 0.5) * ycut, graph.minlng + (j + 0.5) * xcut, r.lat, r.lng) / bandwidth);
                                //double ww = 1;
                                occuRush[ii] += r.or * ww;
                                occuNon[ii] += r.on * ww;
                                occu[ii] += r.getOccupied() * ww;
                                unOccRush[ii] += r.ur * ww;
                                unOccNon[ii] += r.un * ww;
                                unOcc[ii] += r.getUnOccupied() * ww;
                                rush[ii] += r.getRush() * ww;
                                non[ii] += r.getNonRush() * ww;
                                all[ii] += r.getAll() * ww;
//                                orc += r.or;
//                                onc += r.on;
//                                oc += r.getOccupied();
//                                urc += r.ur;
//                                unc += r.un;
//                                uc += r.getUnOccupied();
//                                rc += r.getRush();
//                                nc += r.getNonRush();
//                                ac += r.getAll();
                            }
//                            occuRush[ii] /= (orc == 0) ? 1 : orc * bandwidth;
//                            occuNon[ii] /= (onc == 0) ? 1 : onc * bandwidth;
//                            occu[ii] /= (oc == 0) ? 1 : oc * bandwidth;
//                            unOccRush[ii] /= (urc == 0) ? 1 : urc * bandwidth;
//                            unOccNon[ii] /= (unc == 0) ? 1 : unc * bandwidth;
//                            unOcc[ii] /= (uc == 0) ? 1 : uc * bandwidth;
//                            rush[ii] /= (rc == 0) ? 1 : rc * bandwidth;
//                            non[ii] /= (nc == 0) ? 1 : nc * bandwidth;
//                            all[ii] /= (ac == 0) ? 1 : ac * bandwidth;
                            //System.out.println(occuRush[ii] + " " + all[ii] );
                        }
                        double totalW = 0;
                        for (MapNode p : results) {
                            double weight = normalY(MapGraph.distFrom(graph.minlat + (k + 0.5) * ycut, graph.minlng + (j + 0.5) * xcut, p.lat, p.lng) / bandwidth);
                            betx[ii] += p.between * weight;
                            restx[ii] += p.restCount * weight;
                            //visitx[ii] += p.visitCount;
                            closex[ii] += p.closeness * weight;
                            degx[ii] += p.degree() * weight;
                            totalW += weight;
                        }
                        betx[ii] /= totalW;
                        //restx[ii] /= totalW;
                        //visitx[ii] /= results.size() * bandwidth;
                        degx[ii] /= totalW;
                        closex[ii] /= totalW;
                        ii++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("Array Length : " + closex.length);
        System.out.println(" \tOccupied+Rush\tOccupied+Non Rush\tOccupied\tUnOccupied+Rush\tUnoccupied+NonRush\tUnOccupied\tRush\tNonRush\tAll");
        System.out.println("Deg\t" + twoDForm.format(getPearsonCorrelation(degx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, all)));
        System.out.println("Closeness\t" + twoDForm.format(getPearsonCorrelation(closex, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, non)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, all)));
        //System.out.println("Visit\t" + twoDForm.format(getPearsonCorrelation(visitx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, all)));
        System.out.println("Rest \t" + twoDForm.format(getPearsonCorrelation(restx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, all)));
        System.out.println("Bet\t" + twoDForm.format(getPearsonCorrelation(betx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, all)));
    }

    static void pixelCorrelate3(MapGraph graph, HeatMap1 rest, TrafMap traffic, int width, double bandwidth) {

        DecimalFormat twoDForm = new DecimalFormat("#.###");
        //lng is xaxis
        double w = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.maxlat, graph.minlng);
        double l = MapGraph.distFrom(graph.maxlat, graph.maxlng, graph.minlat, graph.maxlng);

        int xcuts = (int) w / width;
        int ycuts = (int) l / width;

        double xcut = Math.abs(graph.maxlng - graph.minlng) / (double) xcuts;
        double ycut = Math.abs(graph.maxlat - graph.minlat) / (double) ycuts;

        double[][] pixels = new double[xcuts][ycuts];

        System.out.println("Pixel " + xcuts + " x " + ycuts + " Array Length " + xcuts * ycuts);

        double offset = (bandwidth - width) / 2 / width;


        int count = 0;

        try {
            for (MapNode n : graph.people.values()) {
                if (count++ % 100 == 0) {
                    System.out.println(count);
                }

                for (Person fx : n.getFriends()) {
                    MapNode f = (MapNode) fx;

                    if (n.id < f.id) { //each pair of node only inspected once

                        //HashSet<DPoint<Double>> pixels = new HashSet<DPoint<Double>>();

                        if (n.lng != f.lng) {

                            MapNode left = n;
                            MapNode right = f;

                            if (n.lng > f.lng) {
                                left = f;
                                right = n;
                            }

                            //DPoint<Double> closest = pixelMap.nearest(new double[]{left.lng, left.lat});

                            //pixels.add(closest);

                            double slope = (right.lat - left.lat) / (right.lng - left.lng);

                            int x = (int) Math.floor((left.lng - graph.minlng) / xcut); //x

                            int y = (int) Math.floor((graph.maxlat - left.lat) / ycut); //y

                            pixels[x][y] = 1;

                            double currentLng = graph.minlng + (x + 1) * xcut;

                            double currentLat = graph.maxlat - y * ycut; //going up


                            double deltaLat = ycut;

                            if (slope < 0) { //going down
                                deltaLat = -ycut;
                                currentLat = graph.maxlat - (y+1) * ycut; //y
                            }


                            while (currentLng <= right.lng) { //traverse from left to right


                                if (slope != 0) {

                                    double lng_lat = (currentLat - left.lat) / slope + left.lng;

                                    if (currentLng < lng_lat) {

                                        pixels[++x][y] = 255;

                                        currentLng += xcut;
                                    } else {

                                        currentLat += deltaLat;

                                        if (slope < 0) {
                                            pixels[x][++y] = 1;
                                            if (currentLat < right.lat) {
                                                break;
                                            }

                                        } else {
                                            pixels[x][--y] = 1;

                                            if (currentLat > right.lat) {
                                                break;
                                            }


                                        }
                                    }
                                } else { //line is flat

                                    pixels[++x][y] = 255;
                                    currentLng += xcut;
                                }

                            }
                        } else { // line is vertical

                            MapNode top = n;
                            MapNode btm = f;

                            if (n.lat < f.lat) {
                                top = f;
                                btm = n;
                            }


                            int x = (int) Math.floor((btm.lng - graph.minlng) / xcut); //x

                            int y = (int) Math.floor((graph.maxlat - btm.lat) / ycut); //y

                            pixels[x][y] = 1;

                            double currentLat = graph.maxlat - y * ycut;

                            while (currentLat <= top.lat) { //traverse from left to right

                                pixels[x][--y] = 1;

                                currentLat += ycut;

                            }
                        }

                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("here2");

//        LineWriter writer = new LineWriter(new File("C:/map3.csv"));
//        try {
//            int pixelCount = 0;
//            for (int k = 0; k < ycuts; k++) {
//                for (int j = 0; j < xcuts; j++) {
//
//                    writer.write((int) pixels[j][k] + "\t");
//
//
//                    if (pixels[j][k] > 0) {
//                        pixelCount++;
//                    }
//
//
//                }
//
//                writer.writeLine();
//            }
//
//            writer.flush();
//        } catch (Exception e) {
//        }
        //System.out.println(pixelCount);

        KDTree<DPoint<Double>> restMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> closeMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> degMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> betMap = new KDTree<DPoint<Double>>(2);

        //KDTree<DPoint<Double>> trafficMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> occuRushMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> occuNonMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> occuMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> unOccRushMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> unOccNonMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> unOccMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> rushMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> nonMap = new KDTree<DPoint<Double>>(2);
        KDTree<DPoint<Double>> allMap = new KDTree<DPoint<Double>>(2);

        System.out.println("Pixel " + xcuts + " x " + ycuts + " Array Length " + xcuts * ycuts);

        int ii = 0;
        for (int j = 0; j < xcuts; j++) {
            for (int k = 0; k < ycuts; k++) {
                double lng = graph.minlng + (j + 0.5) * xcut;
                double lat = graph.minlat + (k + 0.5) * ycut;
                double[] mink = {graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut};
                double[] maxk = {graph.minlng + (j + 1 + offset) * xcut, graph.minlat + (k + 1 + offset) * ycut};
                double[] mink10 = {graph.minlng + j * xcut, graph.minlat + k * ycut};
                double[] maxk10 = {graph.minlng + (j + 1) * xcut, graph.minlat + (k + 1) * ycut};
                try {
                    List<MapNode> results = graph.kdtree.range(mink, maxk);
                    if (!results.isEmpty()) {
                        List<Point> trafficResults = traffic.map.range(mink10, maxk10);
                        List<Restaurant> restResults = rest.map.range(mink10, maxk10);
                        if (!restResults.isEmpty()) {
                            double rCount = 0;
                            for (Restaurant r : restResults) {
                                rCount += r.count;
                            }
                            restMap.insert(new double[]{lng, lat}, new DPoint<Double>(rCount, lat, lng));
                        }
                        if (!trafficResults.isEmpty()) {
                            double orc = 0, onc = 0, oc = 0, urc = 0, unc = 0, uc = 0, rc = 0, nc = 0, ac = 0;
                            for (Point r : trafficResults) {
                                orc += r.or;
                                onc += r.on;
                                oc += r.getOccupied();
                                urc += r.ur;
                                unc += r.un;
                                uc += r.getUnOccupied();
                                rc += r.getRush();
                                nc += r.getNonRush();
                                ac += r.getAll();
                            }
                            if (orc != 0) {
                                occuRushMap.insert(new double[]{lng, lat}, new DPoint<Double>(orc, lat, lng));
                            }
                            if (onc != 0) {
                                occuNonMap.insert(new double[]{lng, lat}, new DPoint<Double>(onc, lat, lng));
                            }
                            if (oc != 0) {
                                occuMap.insert(new double[]{lng, lat}, new DPoint<Double>(oc, lat, lng));
                            }
                            if (urc != 0) {
                                unOccRushMap.insert(new double[]{lng, lat}, new DPoint<Double>(urc, lat, lng));
                            }
                            if (unc != 0) {
                                unOccNonMap.insert(new double[]{lng, lat}, new DPoint<Double>(unc, lat, lng));
                            }
                            if (uc != 0) {
                                unOccMap.insert(new double[]{lng, lat}, new DPoint<Double>(uc, lat, lng));
                            }
                            if (rc != 0) {
                                rushMap.insert(new double[]{lng, lat}, new DPoint<Double>(rc, lat, lng));
                            }
                            if (nc != 0) {
                                nonMap.insert(new double[]{lng, lat}, new DPoint<Double>(nc, lat, lng));
                            }
                            if (ac != 0) {
                                allMap.insert(new double[]{lng, lat}, new DPoint<Double>(ac, lat, lng));
                            }
                        }
                    }

                    //do something to pixel[j][k]
                    ii++;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }


        double[] closex = new double[ii];
        double[] degx = new double[ii];
        double[] betx = new double[ii];
        double[] restx = new double[ii];
        // double[] visitx = new double[ii];
        double[] occuRush = new double[ii];
        double[] occuNon = new double[ii];
        double[] occu = new double[ii];
        double[] unOccRush = new double[ii];
        double[] unOccNon = new double[ii];
        double[] unOcc = new double[ii];
        double[] rush = new double[ii];
        double[] non = new double[ii];
        double[] all = new double[ii];
        ii = 0;
        for (int j = 0; j < xcuts; j++) {
            for (int k = 0; k < ycuts; k++) {
                double lng = graph.minlng + (j + 0.5) * xcut;
                double lat = graph.minlat + (k + 0.5) * ycut;
                double[] mink10 = {graph.minlng + j * xcut, graph.minlat + k * ycut};
                double[] maxk10 = {graph.minlng + (j + 1) * xcut, graph.minlat + (k + 1) * ycut};
                double[] mink = {graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut};
                double[] maxk = {graph.minlng + (j + 1 + offset) * xcut, graph.minlat + (k + 1 + offset) * ycut};
//                System.out.println(MapGraph.distFrom(graph.minlat + (k - offset) * ycut, graph.minlng + (j - offset) * xcut, graph.minlat + (k + 1 + offset) * ycut, graph.minlng + (j - offset) * xcut));
//                System.out.println(MapGraph.distFrom(graph.minlat + (k - offset) * ycut, graph.minlng + (j - offset) * xcut, graph.minlat + (k - offset) * ycut, graph.minlng + (j + 1 + offset) * xcut));
                try {
                    List<MapNode> results = graph.kdtree.range(mink10, maxk10);
                    if (!results.isEmpty()) {
                        results = graph.kdtree.range(mink, maxk);
                        restx[ii] = 0;
                        betx[ii] = 0;
//                        visitx[ii] = 0;
                        degx[ii] = 0;
                        closex[ii] = 0;
                        occuRush[ii] = 0;
                        occuNon[ii] = 0;
                        occu[ii] = 0;
                        unOccRush[ii] = 0;
                        unOccNon[ii] = 0;
                        unOcc[ii] = 0;
                        rush[ii] = 0;
                        non[ii] = 0;
                        all[ii] = 0;
                        List<DPoint<Double>> _results = restMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                restx[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = betMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                betx[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = closeMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                closex[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = degMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                degx[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = occuRushMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                occuRush[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = occuNonMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                occuNon[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = occuMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                occu[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = unOccRushMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                unOccRush[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = unOccNonMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                unOccNon[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = unOccMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                unOcc[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = rushMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                rush[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = nonMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                non[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                        _results = allMap.range(mink, maxk);
                        if (!_results.isEmpty()) {
                            for (DPoint<Double> r : _results) {
                                all[ii] += r.count * normalY(MapGraph.distFrom(lat, lng, r.lat, r.lng) / bandwidth);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            ii++;
        }
        System.out.println("Array Length : "
                + closex.length);
        System.out.println(
                " \tOccupied+Rush\tOccupied+Non Rush\tOccupied\tUnOccupied+Rush\tUnoccupied+NonRush\tUnOccupied\tRush\tNonRush\tAll");
        System.out.println(
                "Deg\t" + twoDForm.format(getPearsonCorrelation(degx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(degx, all)));
        System.out.println(
                "Closeness\t" + twoDForm.format(getPearsonCorrelation(closex, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, non)) + "\t" + twoDForm.format(getPearsonCorrelation(closex, all)));
        //System.out.println("Visit\t" + twoDForm.format(getPearsonCorrelation(visitx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(visitx, all)));
        System.out.println(
                "Rest \t" + twoDForm.format(getPearsonCorrelation(restx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(restx, all)));
        System.out.println(
                "Bet\t" + twoDForm.format(getPearsonCorrelation(betx, occuRush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, occuNon)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, occu)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOccRush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOccNon)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, unOcc)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, rush)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, non)) + "\t" + twoDForm.format(getPearsonCorrelation(betx, all)));



   }

    static void commDetect(Graph graph) {
        double time = System.currentTimeMillis();
        if (graph.size() > 0) {
            // Graph graph, int clusterS, double hopDecrease, double ratio, boolean sync, int recoverTime, int noThreads, double sampleRatio
            LabelPropagation lp = new LabelPropagation(graph, 1, 0, 0.6, true, 0, 4, 1);
            lp.run();
            System.out.println("Time used for detection: " + (System.currentTimeMillis() - time));
            //lp.printResults("result.out");
        }
    }

    static void dynamic(Graph graph, int days) {
        //Graph graph, int poiMean, double expMean, int threshold, int threshold2, double maxW, int days, int intro, int timesteps, boolean telecom?
        //Dynamic dyn = new Dynamic(graph, 100, 2, 20, 1, 120, days, 15, 100, true);
        //Graph graph, int poiMean, double expMean, int threshold, int threshold2, double maxW, int days, int intro, boolean telecom
        ContDynamic cdyn = new ContDynamic(graph, 115.0, 1, 6, 1, 120.0, days, 15, true);
        cdyn.run();
    }

    static void pr(String p) {
        System.out.println(p);
    }

    static void marco() {
//        Graph graph = new FileGraph("marco/Lympho1-corr-all", -1, false, false, true, "\\s");
        Graph graph = new FileGraph("marco/Lympho1.net.all.1.centromere", -1, false, false, true, "\\s");
        //Graph graph = new FileGraph("yeast", -1, false, false, true, "\\s");
        graph.getPeople().iterator().next().linkGraph(graph);
        graph.outputXGML("centromere");
// Analysis on 10-11-2010, plotting graphs for julien
//        graph.filteredBy(1.0, 1.0, 1.0, 1);
//
//        graph.outputDegreeSeq("lympho1");
//        graph.outputClusternessSeq("lympho1");
//
//        graph.filteredBy(2.0, 1.0, 1.0, 1);
//
//        graph.outputDegreeSeq("lympho2");
//        graph.outputClusternessSeq("lympho2");
        //////////////// Reading chromo from the name of the node
//        int[] chromo = new int[graph.size()];
//
//        for (Person p : graph.getPeople()) {
//            if (p.getName().split("_", 2)[0].substring(3).equals("X")) {
//                chromo[p.getID()] = 23;
//            } else if (p.getName().split("_", 2)[0].substring(3).equals("Y")) {
//                chromo[p.getID()] = 24;
//            } else {
//                chromo[p.getID()] = Integer.parseInt(p.getName().split("_", 2)[0].substring(3));
//            }
//        }
//        System.out.println("Before Weight : " + graph.totalWeight());
//
//        ////////////////// //To Remove intra chromosome interactions
//        for (Person p : graph.getPeople()) {
//            ArrayList<Person> fds = new ArrayList<Person>(p.getFriends());
//            for (Person pf : fds) {
//                if (chromo[p.getID()] == chromo[pf.getID()]) { //remove links between different(!=) or same(=) chromosomes
//                    p.removeFriend(pf);
//                    pf.removeFriend(p);
//                }
//            }
//        }
        //graph.filteredBy(16, 1.0, 1.0, 1);
        //graph.outputXGML("16-1-inter");
        //assign chromosome number to each node;
        //double linkWeight, double nodeWeight, double nodeMaxLinkWeight, int degree
        //System.out.println(graph.getClustering());
        //graph.printCompCount();
        //commDetect(graph);
//        System.out.println(graph.getClustering());
//        commDetect(graph);
//        graph.printCompCount();
//    for (int i = 1; i <9 ; i *= 2) {
//        graph.filteredBy(i, 1.0, 1.0, 1);
//        graph.outputDegreeSeq("lympho-"+i);
//        System.out.println(" " +i);
//
//    }
        //
////////////////// Reading chromo from the name of the node
//        int[] chromo = new int[graph.size()];
//
//        for (Person p : graph.getPeople()) {
//            if (p.getName().split("_", 2)[0].substring(3).equals("X")) {
//                chromo[p.getID()] = 23;
//            } else if (p.getName().split("_", 2)[0].substring(3).equals("Y")) {
//                chromo[p.getID()] = 24;
//            } else {
//                chromo[p.getID()] = Integer.parseInt(p.getName().split("_", 2)[0].substring(3));
//            }
//        }
//        System.out.println("Before Weight : " + graph.totalWeight());
//////////////// Remove links with weight > x
//        for (Person p : graph.getPeople()) {
//            ArrayList<Person> fds = new ArrayList<Person>(p.getFriends());
//            for (Person pf : fds) {
//                if (p.getWeightTo(pf) > 1.1) { //remove links between different chromosomes
//                    p.removeFriend(pf);
//                    pf.removeFriend(p);
//                }
//            }
//        }
//        graph.outputDegreeSeq("weight-1-only");
//        graph.filteredBy(1.0, 1.0, 1.0, 1);
        //graph.printSamples("original");
        System.out.println("Weight : " + graph.totalWeight());
//        graph.outputNodeWeights("yeast");
        //graph.outputSimple("yeast-1-simple");
        try {
            //graph.serialise("lympho.bin");
            //graph = new Graph("yeast.bin");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            //graph.saveAs("graph.bin");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //commDetect(graph);
//        HashMap<int[], ArrayList<Double>> links = graph.getLinks(1.5);
        // PRINTING RANDOM GRAPHS
//        int runs = 100;
//        for (int i = 0; i < runs; i++) {
//
//            System.out.println(i + "...");
//            graph.randomizeW(chromo);
//
//            graph.printSamples("fulldist2/rand-" + i);
//
////                for (int[] link : links.keySet()) {
////                    if (graph.getPerson(link[0]).isFriendOf(graph.getPerson(link[1]))) {
////                        links.get(link).add(graph.getPerson(link[0]).getWeightTo(graph.getPerson(link[1])));
////                    }
////                }
//            }
//            LineWriter wr = new LineWriter(new File("linktest-" + System.currentTimeMillis() + "-" + runs + "runs.txt"));
//            try {
//                for (int[] link : links.keySet()) {
//                    double sum = 0;
//                    double sum2 = 0;
//                    for (Double d : links.get(link).subList(1, links.get(link).size())) {
//                        sum += d;
//                        sum2 += d * d;
//                    }
//
//                    double x = links.get(link).get(0);
//                    double mean = sum / (double) runs;
//                    double sd = Math.sqrt((sum2 / (double) runs - mean * mean));
//                    wr.writeLine(graph.getPerson(link[0]).getName() + "\t" + graph.getPerson(link[1]).getName() + "\t" + x + "\t" + sum + "\t" + sum2+ "\t" + mean+ "\t" + sd + "\t" + (new Double((x - mean) / sd)).toString() + "\t" + (new Double(cern.jet.stat.Probability.errorFunctionComplemented((x - mean) / sd))).toString());
//                }
//                wr.flush();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
    }

    static void bipartite(Graph graph, String file) {
        // String filename, int N, boolean intID, boolean weighted, String seperator
        graph = new BipartiteGraph(file, -1, false, false, "\\s");
//        Graph pGraph = ((BipartiteGraph)graph).projectionGraph(true);
//        pGraph.filteredBy(0.7, 2);
//        pGraph.outputXGML("herbs");
        Graph pGraph = ((BipartiteGraph) graph).projectionGraph(false);
        //pGraph.filteredBy(0.5, 2);
        pGraph.outputXGML("symptoms");
    }

    static void centrality(Graph graph) {
    }

    static void motif(Graph graph) {
        double time = System.currentTimeMillis();
        ArrayList<Person> toRemove = new ArrayList<Person>();
        for (Person p : graph.getPeople()) {
            if (p.degree() == 0) {
                toRemove.add(p);
            }
        }
        for (Person p : toRemove) {
            graph.removePerson(p);
        }
        graph.sortIDs(); //fix
        graph = new nBMGraph(10000, 10, 20, 3, 2, 0.1);
        graph = new BAGraph(50000, 3, 3);
        System.out.println("Time used for reading file: " + (System.currentTimeMillis() - time));
        System.out.println("The graph has " + graph.getNumEdges() + " edges and " + graph.getComponents().size() + " component(s).");
        //initial infectant, p, r = 0.25?, d = 0.01
        for (int i = 0; i
                < 1000; i++) {
            graph.randomize(500000);
            graph.outputCompList("graph" + i);
        }
        LineReader reader = new LineReader(new File("NodesAttributes.txt"));
        HashMap<String, Integer> m = new HashMap<String, Integer>();
        if (reader.token != null) {
            try {
                for (String t = reader.nextToken(); t
                        != null; t = reader.nextToken()) {
                    t = t.trim();
                    String[] token = t.split("\\s");
                    // if it is a valid line
                    if (token.length > 1) {
                        m.put(token[0], Integer.parseInt(token[2]));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        LineWriter wr2 = new LineWriter(new File("sizelength.list"));
        try {
            for (Person p : graph.getPeople()) {
                wr2.writeLine(p.getName() + "\t" + p.getFriends().size() + "\t" + m.get(p.getName()));
            }
            wr2.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } //graph.outputGML("marco");
    }

    static void epidemic(Graph graph) {
        String time = Utils.now("dd_MM_yy_h_mm_ss");
        int days = 200;
        int runs = 25;
        int n = 10000;
        (new File("analysis_" + time)).mkdir();
        for (int inf = 0; inf
                <= 20; inf += 2) {
            for (int death = 0; death
                    <= 0; death += 5) {
                for (int recov = 0; recov
                        <= 20; recov += 2) {
                    double p = (double) inf / 100;
                    double d = (double) death / 100;
                    double r = (double) recov / 100;
                    double[][] result = new double[6][days + 1];
                    LineWriter write = new LineWriter(new File("analysis_" + time + "/" + inf + "_" + death + "_" + recov + ".txt"));
                    for (int i = 0; i
                            < runs; i++) {
                        graph = new BAGraph(n, 3, 3);
                        Epidemic epi = new Epidemic(graph, 1, p, r, d);
                        result = Utils.add(result, epi.run(days));
                    }
                    result = Utils.divide(result, runs);
                    try {
                        for (int i = 0; i
                                < result[0].length; i++) {
                            write.write(i + "\t");
                            for (int j = 0; j
                                    < result.length; j++) {
                                write.write(result[j][i] + "\t");
                            }
                            write.writeLine("");
                        }
                        write.flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    static double getPearsonCorrelation(double[] scores1, double[] scores2) {
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = scores1[0];
        double mean_y = scores2[0];
        for (int i = 2; i
                < scores1.length + 1; i +=
                        1) {
            double sweep = Double.valueOf(i - 1) / i;
            double delta_x = scores1[i - 1] - mean_x;
            double delta_y = scores2[i - 1] - mean_y;
            sum_sq_x +=
                    delta_x * delta_x * sweep;
            sum_sq_y +=
                    delta_y * delta_y * sweep;
            sum_coproduct +=
                    delta_x * delta_y * sweep;
            mean_x +=
                    delta_x / i;
            mean_y +=
                    delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x / scores1.length);
        double pop_sd_y = (double) Math.sqrt(sum_sq_y / scores1.length);
        double cov_x_y = sum_coproduct / scores1.length;
        result =
                cov_x_y / (pop_sd_x * pop_sd_y);
        return result;
    }

    static double getPearsonCorrelation(Double[] scores1, Double[] scores2) {
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = scores1[0];
        double mean_y = scores2[0];
        for (int i = 2; i
                < scores1.length + 1; i +=
                        1) {
            double sweep = Double.valueOf(i - 1) / i;
            double delta_x = scores1[i - 1] - mean_x;
            double delta_y = scores2[i - 1] - mean_y;
            sum_sq_x +=
                    delta_x * delta_x * sweep;
            sum_sq_y +=
                    delta_y * delta_y * sweep;
            sum_coproduct +=
                    delta_x * delta_y * sweep;
            mean_x +=
                    delta_x / i;
            mean_y +=
                    delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x / scores1.length);
        double pop_sd_y = (double) Math.sqrt(sum_sq_y / scores1.length);
        double cov_x_y = sum_coproduct / scores1.length;
        result =
                cov_x_y / (pop_sd_x * pop_sd_y);
        return result;
    }

    static double getPearsonCorrelation(ArrayList<Double> scores1, ArrayList<Double> scores2) {
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = scores1.get(0);
        double mean_y = scores2.get(0);
        for (int i = 2; i
                < scores1.size() + 1; i +=
                        1) {
            double sweep = Double.valueOf(i - 1) / i;
            double delta_x = scores1.get(i - 1) - mean_x;
            double delta_y = scores2.get(i - 1) - mean_y;
            sum_sq_x +=
                    delta_x * delta_x * sweep;
            sum_sq_y +=
                    delta_y * delta_y * sweep;
            sum_coproduct +=
                    delta_x * delta_y * sweep;
            mean_x +=
                    delta_x / i;
            mean_y +=
                    delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x / scores1.size());
        double pop_sd_y = (double) Math.sqrt(sum_sq_y / scores1.size());
        double cov_x_y = sum_coproduct / scores1.size();
        result =
                cov_x_y / (pop_sd_x * pop_sd_y);
        return result;
    }
    //Betweenness Sampling

    static void calBet(ArrayList<MapNode> people, ArrayList<MapNode> subList, int noThreads, int noSamples, int mode, double e) {
        System.out.println("Starting Betweenness");
        ArrayList<MapNode> sampleList;
        for (MapNode p : people) {
            p.iniBet(noThreads);
        }
        ArrayList<MapNode> pp;
        if (subList == null) {
            pp = new ArrayList<MapNode>(people);
        } else {
            pp = subList;
        }
        if (noSamples < 0 || noSamples >= pp.size()) {
            sampleList = pp;
        } else {
            HashSet<MapNode> rs = new HashSet<MapNode>();
            while (rs.size() < noSamples) {
                rs.add(pp.get(rng.nextInt(pp.size())));
            }
            sampleList = new ArrayList(rs);
        }
        ArrayList<betThread> bthreads = new ArrayList<betThread>();
        for (int t = 0; t
                < noThreads; t++) {
            int fromIndex = (int) Math.round(((float) sampleList.size() / noThreads) * t);
            int toIndex = (int) Math.round(((float) sampleList.size() / noThreads) * (t + 1));
            bthreads.add(new betThread(sampleList.subList(fromIndex, toIndex), people, t, 9999, mode, e));
            bthreads.get(t).start();
        }
        for (int t = 0; t
                < noThreads; t++) {
            try {
                bthreads.get(t).join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Betweenenss Done.");
    }

    static class betThread extends Thread {

        List<MapNode> pi;
        Collection<MapNode> people;
        int id;
        int depth;
        int mode;
        double e;
        static volatile int count;

        betThread(List<MapNode> pi, Collection<MapNode> people, int id, int depth, int mode, double e) {
            this.pi = pi;
            this.people = people;
            this.id = id;
            this.depth = depth;
            this.mode = mode;
            this.e = e;
            count = 0;
        }

        public void run() {
            //System.out.println("started size: " + people.size());
            if (mode == 0) {
                for (MapNode p : pi) {
                    p.bet(depth, people, id);
                    count++;
                    if (count % 100 == 0) {
                        System.out.print(count + "\r");
                    }
                }
            } else {
                for (MapNode p : pi) {
                    p.bet2(people, id, e);
                    count++;
                    if (count % 100 == 0) {
                        System.out.print(count + "\r");
                    }
                }
            }
        }
    }

    static HashMap<Integer, Double> calCloseness(Collection<MapNode> people, ArrayList<MapNode> subList, int noThreads, int noSamples, int depth, int mode) {
        System.out.println("Starting Closeness...");
        ArrayList<MapNode> sampleList;
        HashMap<Integer, Double> closeness = new HashMap<Integer, Double>();
        for (MapNode p : people) {
            p.closeness = 0;
            p.reachCount = 0;
            if (mode == 1) {
                p.iniClose(noThreads);
            }
        }
        if (subList == null) {
            ArrayList<MapNode> pp = new ArrayList<MapNode>(people);
            if (noSamples < 0) {
                sampleList = pp;
            } else {
                HashSet<MapNode> rs = new HashSet<MapNode>();
                while (rs.size() < noSamples) {
                    rs.add(pp.get(rng.nextInt(people.size())));
                }
                sampleList = new ArrayList(rs);
            }
        } else {
            sampleList = subList;
        }
        ArrayList<closenessThread> cthreads = new ArrayList<closenessThread>();
        for (int t = 0; t
                < noThreads; t++) {
            int fromIndex = (int) Math.round(((float) sampleList.size() / noThreads) * t);
            int toIndex = (int) Math.round(((float) sampleList.size() / noThreads) * (t + 1));
            cthreads.add(new closenessThread(sampleList.subList(fromIndex, toIndex), people, depth, t, mode));
            //FIX mode = 1 and doesnt work
            cthreads.get(t).start();
        }
        for (int t = 0; t
                < noThreads; t++) {
            try {
                cthreads.get(t).join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
//        for (MapNode p : people) {
//            closeness.put(p.getID(), p.closeness / p.reachCount);
//        }
        System.out.println("Closeness Done.");
        return closeness;
    }

    static class closenessThread extends Thread {

        List<MapNode> pi;
        Collection<MapNode> people;
        int id;
        int depth;
        int mode;

        closenessThread(List<MapNode> pi, Collection<MapNode> people, int depth, int id, int mode) {
            this.pi = pi;
            this.people = people;
            this.depth = depth;
            this.mode = mode;
            this.id = id;
        }

        public void run() {
            //System.out.println("started size: " + people.size());
            if (mode == 0) {
                for (MapNode p : pi) {
                    p.closeNess(depth, people);
                }
            } else {
                for (MapNode p : pi) {
                    p.closeNess2(people, id);
                }
            }
        }
    }
}
