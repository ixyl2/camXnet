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
public class MapGraph extends Graph {

    HashMap<Integer, MapNode> people;
    KDTree<MapNode> kdtree;
    double minlat, minlng, maxlat, maxlng;
    //TreeMap<Double, TreeMap<Double, MapNode>> map;
    //ToDo: remove intID

    MapGraph(String filename, int N, double threshold, String seperator, int precision, boolean speed) {

        super();
        kdtree = new KDTree<MapNode>(2);

        //map = new TreeMap<Double, TreeMap<Double, MapNode>>();

        System.out.println("Processing file : " + filename);

        LineReader reader = new LineReader(new File(filename));
        double weightx = 0;
        int xx = 0;
        boolean first = true;

        minlat = Double.MAX_VALUE;
        minlng = Double.MAX_VALUE;
        maxlat = -200;
        maxlng = -200;

        if (reader.token != null) {


            HashMap<String, Integer> id = new HashMap<String, Integer>();

            int perFrom = 0;
            int perTo = 0;
            int edge = 0;
            int line = 0;

            people = new HashMap<Integer, MapNode>();

            try {

                ArrayList<MapNode> toConnect = new ArrayList<MapNode>();
                for (String t = reader.nextToken(); t != null; t = reader.nextToken()) {


                    t = t.trim();
                    String[] token = t.split(seperator);


                    // if it is a valid line
                    if (token.length > 1) {
                        int idx = Integer.parseInt(token[0]);

                        if (first) {
                            first = false;
                            xx = idx;
                        }

                        double factor = 1;

                        if (speed) {
                            if (token[3].equalsIgnoreCase("S1100")) {
                                factor = 2; //default 2
                            } else if (token[3].equalsIgnoreCase("S1200")) {
                                factor = 2; //default 3
                            }


                            if (token[3].equalsIgnoreCase("trunk")) {
                                factor = 2; //default 2
                            } else if (token[3].equalsIgnoreCase("trunk_link")) {
                                factor = 2; //default 3
                            } else if (token[3].equalsIgnoreCase("primary")) {
                                factor = 2; //default 3
                            } else if (token[3].equalsIgnoreCase("primary_link")) {
                                factor = 2; //default 3
                            } else if (token[3].equalsIgnoreCase("motorway")) {
                                factor = 2; //default 3
                            } else if (token[3].equalsIgnoreCase("motorway_link")) {
                                factor = 2; //default 3
                            }

                        }

                        if (xx != idx) {
                            xx = idx;
                            for (int i = 0; i < toConnect.size() - 1; i++) {
                                MapNode n1 = (MapNode) toConnect.get(i);
                                MapNode n2 = (MapNode) toConnect.get(i + 1);
                                double dist = MapGraph.distFrom(n1.lat, n1.lng, n2.lat, n2.lng) / factor;

                                n1.addFriend(n2, dist);
                                n2.addFriend(n1, dist);
                                weightx += dist;
                            }

                            toConnect = new ArrayList<MapNode>();
                        }



                        double lng = Double.parseDouble(token[1]);
                        double lat = Double.parseDouble(token[2]);



                        line++;

                        String gps = (int) Math.round(lng * Math.pow(10, precision)) + "," + (int) Math.round(lat * Math.pow(10, precision));
                        //System.out.println(gps);
                        if (!id.containsKey(gps)) {
                            id.put(gps, id.size());
                        }

                        perFrom = id.get(gps);

                        if (!people.containsKey(perFrom)) {
                            people.put(perFrom, new MapNode(perFrom, gps, lng, lat));
                            double[] coord = {lng, lat};

                            kdtree.insert(coord, people.get(perFrom));
                        }
                        toConnect.add(people.get(perFrom));

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }

            for (MapNode i : people.values()) {
                edge += i.getFriends().size();
            }

            System.out.println("Number of nodes: " + people.size() + " Number of edges: " + edge + " Total Weight : " + weightx);

            runMaxMin();

        }
    }

    void removePerson(MapNode p) {
        p.removeAllFriends();
        people.remove(p.getID());
        double[] coord = {p.lng, p.lat};
        try {
            this.kdtree.delete(coord);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void outputCoord(String filename) {

        HashSet edges = new HashSet();

        LineWriter xml = new LineWriter(new File(filename + ".coord"));

        try {
            for (MapNode p : people.values()) {
                xml.writeLine("(" + p.lng + "," + p.lat + ",0)");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Directed, i.e. total degree
    @Override
    int getNumEdges() {
        int edges = 0;
        for (Person p : people.values()) {
            edges += p.degree();
        }
        return edges / 2;
    }

    @Override
    int size() {
        return people.size();
    }

    @Override
    double totalWeight() {
        double weight = 0;
        for (MapNode p : this.people.values()) {
            weight += p.getTotalWeight();
        }
        return weight / 2;
    }

    // end of class definition
    void simplify() {
        boolean changed;



        do {
            changed = false;



            for (Iterator it = people.values().iterator(); it.hasNext();) {
                MapNode p = (MapNode) it.next();


                if (p.degree() == 2) {

                    changed = true;
                    MapNode p1 = (MapNode) p.friends.keySet().toArray()[0];
                    MapNode p2 = (MapNode) p.friends.keySet().toArray()[1];


                    double newWeight = p.getWeightTo(p1) + p.getWeightTo(p2);


                    if (p1.friendOf(p2)) {
//                        p1.putWeight(p2, (newWeight + p1.getWeightTo(p2)) / 2);
//                        p2.putWeight(p1, (newWeight + p2.getWeightTo(p1)) / 2);
                        p1.putWeight(p2, Math.min(newWeight, p1.getWeightTo(p2)));
                        p2.putWeight(p1, Math.min(newWeight, p1.getWeightTo(p2)));


                    } else {
                        p1.addFriend(p2, newWeight);
                        p2.addFriend(p1, newWeight);


                    }
                    it.remove();


                    this.removePerson(p);


                }
            }

            for (Iterator it = people.values().iterator(); it.hasNext();) {
                MapNode p = (MapNode) it.next();


                if (p.degree() == 0) {
                    changed = true;
                    it.remove();


                    this.removePerson(p);


                }

            }




        } while (changed);
        System.out.println(this.size() + " " + this.getNumEdges() + " " + this.totalWeight());
        runMaxMin();


    }

    void runMaxMin() {
        for (MapNode p : this.people.values()) {
            double lng = p.lng;


            double lat = p.lat;


            if (lng < minlng) {
                minlng = lng;


            }
            if (lng > maxlng) {
                maxlng = lng;


            }
            if (lat < minlat) {
                minlat = lat;


            }
            if (lat > maxlat) {
                maxlat = lat;


            }

        }
    }

    //Extreme Simplyfy
    void simplifyEx() {
        boolean changed;


        boolean changed1;


        boolean changed2;



        do {
            changed = false;



            do {
                changed2 = false;


                for (Iterator it = people.values().iterator(); it.hasNext();) {
                    MapNode p = (MapNode) it.next();


                    if (p.degree() == 2) {
                        changed = true;
                        changed2 = true;
                        MapNode p1 = (MapNode) p.friends.keySet().toArray()[0];
                        MapNode p2 = (MapNode) p.friends.keySet().toArray()[1];


                        double newWeight = p.getWeightTo(p1) + p.getWeightTo(p2);


                        if (p1.friendOf(p2)) {
                            p1.putWeight(p2, Math.min(newWeight, p1.getWeightTo(p2)));
                            p2.putWeight(p1, Math.min(newWeight, p1.getWeightTo(p2)));


                        } else {
                            p1.addFriend(p2, newWeight);
                            p2.addFriend(p1, newWeight);


                        }

                        it.remove();


                        this.removePerson(p);



                    }
                }
            } while (changed2);



            do {
                changed1 = false;


                for (Iterator it = people.values().iterator(); it.hasNext();) {
                    MapNode p = (MapNode) it.next();


                    if (p.degree() <= 1) {
                        changed = true;
                        changed1 = true;
                        it.remove();


                        this.removePerson(p);


                    }

                }
            } while (changed1);





            System.out.println(this.size() + " " + this.getNumEdges() + " " + this.totalWeight());


        } while (changed);


        runMaxMin();


    }

    @Override
    void outputXGML(String filename) {

        HashSet edges = new HashSet();

        LineWriter xml = new LineWriter(new File(filename + ".xml"));



        try {
            xml.writeLine("<?xml version='1.0'?>\n"
                    + "<graph directed='0' id='5' label=''\n"
                    + "        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
                    + "        xmlns:ns1='http://www.w3.org/1999/xlink'\n"
                    + "        xmlns:dc='http://purl.org/dc/elements/1.1/'\n"
                    + "        xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n"
                    + "        xmlns='http://www.cs.rpi.edu/XGMML'>\n");
            //Print Nodes


            for (MapNode p : people.values()) {
                xml.writeLine("<node id='" + p.getID() + "' label='" + p.getID() + "'>\n"
                        + "<att name='weight' type='integer' value='" + p.degree() + "'/>\n"
                        + "<att name='x' value='" + ((MapNode) p).lng + "' type='integer' />\n"
                        + "<att name='y' value='" + ((MapNode) p).lat + "' type='integer' />\n"
                        + "</node>");


            }


            for (MapNode p : people.values()) {

                for (Person f : p.getFriends()) {
                    String edge = p.getID() + "\t" + f.getID();


                    if (p.getID() > f.getID()) {
                        edge = f.getID() + "\t" + p.getID();


                    }

                    if (!edges.contains(edge)) {
                        xml.writeLine(" <edge label='' source='" + p.getID() + "' target='" + f.getID() + "'>\n"
                                + //"<att name='interaction' value='"+p.getWeightTo(f)+"' type='' />\n" +
                                "<att name='weight' value='" + p.getWeightTo(f) + "' type='real' />\n"
                                + "</edge>");
                        edges.add(edge);


                    }
                }
            }
            xml.writeLine("</graph>");
            xml.flush();


        } catch (Exception ex) {
            ex.printStackTrace();


        }
    }

    @Override
    void outputGML(
            String name) {

        HashSet edges = new HashSet();
        LineWriter xml = new LineWriter(new File(name + ".gml"));
        String[] colour = new String[5000];



        for (int j = 0; j
                < 5000; j++) {
            colour[j] = Integer.toHexString((int) (Math.random() * 0xFFFFFF));


        }

        try {
            xml.writeLine("graph [");
            xml.writeLine("directed 0");
            xml.writeLine("graphics [ fill \"#FFFFFF\" ]");
            //Print Nodes


            for (MapNode p : people.values()) {
                xml.writeLine("node [");
                xml.writeLine("id " + p.getID());
                xml.writeLine("label \"" + p.getID() + "\"");
                xml.writeLine("graphics [ center [ x " + ((MapNode) p).lng + " y " + ((MapNode) p).lat + " ] ]");


                //xml.writeLine("degree " + p.adeg);
                //xml.writeLine("bet " + (int) p.between);
                //xml.writeLine("close " + p.acloseness);
//                xml.writeLine("graphics [ fill    \"#" + colour[p.getMaxClusterID() % 5000] + "\" outline \"#000000\" ]");
//xml.writeLine("graphics [ fill \"#FFFFFF\" h " +  (10 + (int)Math.sqrt(p.abetween)/10) + " w " +  (10 + (int)Math.sqrt(p.abetween)/10) + " ]");
//xml.writeLine("graphics [ fill \"#FFFFFF\" h " + (int)Math.sqrt(p.weight) + " w " + (int)Math.sqrt(p.weight) + " ]");
                //xml.writeLine("\t" + "<att name='cluster' value='" + p.getMaxClusterID() + "'/>");
                //xml.writeLine("\t" + "<att name='weight' value='" + (int) (p.abetween) + "'/>");
                xml.writeLine("]");


            }

            for (MapNode p : people.values()) {
                for (Person f : p.getFriends()) {

                    String edge = p.getID() + "\t" + f.getID();


                    if (p.getID() > f.getID()) {
                        edge = f.getID() + "\t" + p.getID();


                    }

                    if (!edges.contains(edge)) {

                        xml.writeLine("edge [");

                        xml.writeLine("source " + p.getID());
                        xml.writeLine("target " + f.getID());


                        // Color c = new Color((int) ((Math.log10(p.getWeightTo(f)) / 4) * 255), 0, 255 - (int) ((Math.log10(p.getWeightTo(f)) / 4) * 255));


                        // xml.writeLine("graphics [ width 1 type \"line\" fill \"#" + Integer.toHexString(c.getRGB() & 0xffffff + 0x1000000).substring(1) + "\" ]");
                        xml.writeLine("]");

                        edges.add(edge);


                    }

                }
            }

            xml.writeLine("]");

            xml.flush();

            xml.close();


        } catch (Exception ex) {
            ex.printStackTrace();


        }
    }

    void outputBet(String name) {

        LineWriter xml = new LineWriter(new File(name + ".bet"));



        try {
            for (MapNode p : people.values()) {
                xml.writeLine(p.getID() + " " + p.between);


            }

            xml.close();


        } catch (Exception ex) {
            ex.printStackTrace();


        }


    }

    void outputTraffic(String name) {

        LineWriter xml = new LineWriter(new File(name + ".traf"));



        try {
            for (MapNode p : people.values()) {
                xml.writeLine(p.getID() + " " + p.visitCount);


            }

            xml.close();


        } catch (Exception ex) {
            ex.printStackTrace();


        }


    }

    void outputTLP(
            String name, boolean withProperty, int mode) {

        HashSet edges = new HashSet();
        LineWriter xml = new LineWriter(new File(name + ".tlp"));



        try {
            xml.writeLine("(tlp \"2.0\";");

            //Print Nodes
            xml.writeLine("(nodes");


            for (MapNode p : people.values()) {
                xml.write(p.getID() + " ");



            }
            xml.writeLine(")");



            int edgeCount = 0;


            double maxWeight = 0;


            double minWeight = Double.MAX_VALUE;

            //double maxWeight2 = 0;
            //double minWeight2 = Double.MAX_VALUE;


            ArrayList<Double> lengthList = new ArrayList<Double>();
            ArrayList<Double> betList = new ArrayList<Double>();


            for (MapNode p : people.values()) {


                for (Person f : p.getFriends()) {

                    String edge = p.getID() + "\t" + f.getID();


                    if (p.getID() > f.getID()) {
                        edge = f.getID() + "\t" + p.getID();


                    }

                    if (!edges.contains(edge)) {

                        xml.writeLine("(edge " + edgeCount + " " + p.getID() + " " + f.getID() + ")");
                        lengthList.add(p.getWeightTo(f));

                        //weight2 = Math.min(p.between, ((MapNode) f).between);



                        double weight = 0;


                        switch (mode) {
                            case 0:
                                weight = Math.min(p.between, ((MapNode) f).between);
                                break;

                            case 1:
                                weight = (p.visitCount + ((MapNode) f).visitCount) / 2;

                                break;

                            case 2:
                                weight = (p.closeness + ((MapNode) f).closeness) / 2;
                                break;

                            case 3:
                                weight = p.getEdgeBet((MapNode) f);
                                break;
                            default:
                                weight = (p.restCount + ((MapNode) f).restCount) / 2;

                        }

                        betList.add(Math.log(weight + 1));


                        if (Math.log(weight + 1) > maxWeight) {
                            maxWeight = Math.log(weight + 1);


                        }

                        if (Math.log(weight + 1) < minWeight) {
                            minWeight = Math.log(weight + 1);


                        }

                        edgeCount++;

                        // Color c = new Color((int) ((Math.log10(p.getWeightTo(f)) / 4) * 255), 0, 255 - (int) ((Math.log10(p.getWeightTo(f)) / 4) * 255));


                        // xml.writeLine("graphics [ width 1 type \"line\" fill \"#" + Integer.toHexString(c.getRGB() & 0xffffff + 0x1000000).substring(1) + "\" ]");

                        edges.add(edge);


                    }

                }
            }

            xml.writeLine("(property  0 layout \"viewLayout\"");
            xml.writeLine("(default \"(0,0,0)\" \"()\")");


            for (MapNode p : people.values()) {

                xml.writeLine("(node " + p.getID() + " \"(" + p.getName() + ",0)\")");



            }
            xml.writeLine(")");

            edgeCount = 0;

            xml.writeLine("(property  0 string \"viewLabel\"");
            xml.writeLine("(default \"\" \"\")");
            edgeCount = 0;


            for (Double d : lengthList) {


                xml.writeLine("(edge " + edgeCount++ + " \"" + d.intValue() + "\")");




            }

            xml.writeLine(")");



            if (withProperty) {
//                xml.writeLine("(property  0 size \"viewSize\"");
//                xml.writeLine("(default \"(0,0,0)\" \"(1,1,1)\")");
//                for (MapNode p : people.values()) {
//
//                    double size = (p.between / maxBet) * 500 + 1;
//                    xml.writeLine("(node " + p.getID() + " \"(" + size + "," + size + "," + size + ")\")");
//
//                }
//                xml.writeLine(")");



                xml.writeLine("(property  0 color \"viewColor\"");
                xml.writeLine("(default \"(0,0,0,0)\" \"(0,0,0,0)\")");
                edgeCount = 0;


                for (Double d : betList) {
                    if ((d - minWeight) / (maxWeight - minWeight) > 0.5) {
                        xml.writeLine("(edge " + edgeCount++ + " \"(" + (int) (2 * Math.max(((d - minWeight) / (maxWeight - minWeight) - 0.5) * 255, 0)) + "," + (int) (2 * (Math.abs(1 - (d - minWeight) / (maxWeight - minWeight)) * 255)) + "," + (int) (2 * (Math.max((0.5 - (d - minWeight) / (maxWeight - minWeight)) * 255, 0))) + ",255)\")");


                    } else {
                        xml.writeLine("(edge " + edgeCount++ + " \"(" + (int) (2 * Math.max(((d - minWeight) / (maxWeight - minWeight) - 0.5) * 255, 0)) + "," + (int) (2 * (Math.abs((d - minWeight) / (maxWeight - minWeight)) * 255)) + "," + (int) (2 * (Math.max((0.5 - (d - minWeight) / (maxWeight - minWeight)) * 255, 0))) + ",255)\")");


                    }
                }
                xml.writeLine(")");




            }



            xml.writeLine(")");

            xml.flush();

            xml.close();


        } catch (Exception ex) {
            ex.printStackTrace();


        }
    }

    double getPerDis(double lat, double lng, double lat1, double lng1, double lat2, double lng2) {

        double norm = Math.sqrt((lat2 - lat1) * (lat2 - lat1) + (lng2 - lng1) * (lng2 - lng1));



        double dist = Math.abs((lat2 - lat1) * (lng1 - lng) - (lat1 - lat) * (lng2 - lng1))
                / norm;



        double latx = dist * ((lng1 - lng2) / norm) + lat;


        double lngy = dist * ((lat2 - lat1) / norm) + lng;



        double r = distFrom(lat, lng, latx, lngy);



        return r;


    }

    MapNode getClosestNode(double lat, double lng) {
        try {
            return kdtree.nearest(new double[]{lng, lat});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    MapNode getClosestNodeByEdge(double lat, double lng, int thresh) {
        double dist = Double.MAX_VALUE;
//        MapNode r = null;
//        for (MapNode p : this.people.values()) {
//            if (p.getDistfrom(lat1, lng1) < dist) {
//                dist = p.getDistfrom(lat1, lng1);
//                r = p;
//            }
//        }
//
//        if(r.getDistfrom(lat1, lng1) > 1000) {
//            return null;
//        }


        MapNode r = null;
        MapNode r2 = null;


        double[] coord = {lng, lat};
//        try {
//            r = kdtree.nearest(coord);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }




        try {

            for (MapNode p : kdtree.nearest(coord, 10)) {

                for (Person pxx : p.getFriends()) {
                    MapNode px = (MapNode) pxx;



                    double minlt = Math.min(p.lat, px.lat);


                    double maxlt = Math.max(p.lat, px.lat);


                    double minlg = Math.min(p.lng, px.lng);


                    double maxlg = Math.max(p.lng, px.lng);



                    if (((minlt <= lat) && (maxlt >= lat)) || ((minlg <= lat) && (maxlg >= lat))) {
                        double perDis = getPerDis(lat, lng, p.lat, p.lng, px.lat, px.lng);



                        if (perDis < dist) {
                            dist = perDis;
                            r = p;
                            r2 = px;


                        }
                    }
                }
            }
        } catch (Exception ex) {

            ex.printStackTrace();


        }


        if (dist > thresh) {
            return null;


        }

        if (distFrom(lat, lng, r.lat, r.lng) > distFrom(lat, lng, r2.lat, r2.lng)) {
            return r2;


        }
        return r;


    }

    MapNode registerTravel(double lat1, double lng1, double lat2, double lng2, MapNode last) {
        return registerTravel(getClosestNodeByEdge(lat1, lng1, 30), getClosestNodeByEdge(lat2, lng2, 30), last);

    }

    MapNode registerTravel(MapNode a, MapNode b, MapNode last) {



        if (a == null || b == null) {
            return null;


        }



        if ((last == null) || (a != last)) {
            if (a == b) {
                a.addVisit(1.0);


            } else {
                if (a.distanceFrom(b) <= 3) {
                    travelTo(a, b, 0);


                } else {
                    travelTo(a, b, 0);


                }
            }
        } else if (last == a) {
            if (a != b) {
                if (a.distanceFrom(b) <= 3) {
                    travelTo(a, b, 0);


                } else {
                    travelTo(a, b, 0);


                }
                a.visitCount--;


            }
        }

        return b;



    }

    void travelTo(MapNode from, MapNode to, double e) {

        //System.out.println("this " + from.getName() + " " + to.getName());

        myHeap<MapNode, Double> q = new myHeap<MapNode, Double>(0, true);
        ArrayList<MapNode> S = new ArrayList<MapNode>();
        HashSet<MapNode> visited = new HashSet<MapNode>();

        q.put(from, (double) 0);
        //q2.insert(ref.get(this),0);

        from.currDist[0] = 0;
        from.sigma[0] = 1;



        while (q.size() > 0) {
            // MapNode p = q2.removeMin().getData();
            MapNode p = q.pop();
            S.add(p);
            visited.add(p);



            if (p == to) {
                break;


            }

            for (Person xx : p.getFriends()) {

                MapNode n = (MapNode) xx;

                visited.add(n);



                double newDis = p.currDist[0] + p.getWeightTo(n);




                if (n.currDist[0] == -1) {
                    q.put(n, newDis);

                    n.currDist[0] = newDis;


                }

                //1-e
                if (n.currDist[0] * (1 - e) > newDis) { // if a very shorter bath between x and p is found

                    q.put(n, newDis);

                    n.currDist[0] = newDis;

                    n.sigma[0] = p.sigma[0];

                    n.parents.get(0).clear();

                    n.parents.get(0).add(p);




                } else if (n.currDist[0] * (1 + e) >= newDis) {

                    n.sigma[0] += p.sigma[0];

                    n.parents.get(0).add(p);


                }


            }

        }

        to.delta[0] = 1;
        to.visitCount += 1;




        for (int i = S.size() - 1; i
                > 0; i--) {

            MapNode w = S.get(i);



            for (MapNode v : w.parents.get(0)) {

                v.delta[0] += ((double) v.sigma[0] / w.sigma[0]) * w.delta[0];

                v.visitCount += ((double) v.sigma[0] / w.sigma[0]) * w.delta[0];



            }

        }



        for (MapNode p : visited) {
            p.resetBet(0);


        }
    }

    public static double estED(double lat1, double lng1, double lat2, double lng2) {
        return (lat1 - lat2) * (lat1 - lat2) + (lng1 - lng2) * (lng1 - lng2);


    }

    public static double distFrom(MapNode node1, MapNode node2) {
        return distFrom(node1.lat, node1.lng, node2.lat, node2.lng);


    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;


        double dLat = Math.toRadians(lat2 - lat1);


        double dLng = Math.toRadians(lng2 - lng1);


        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);


        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));


        double dist = earthRadius * c;



        double meterConversion = 1609;



        return dist * meterConversion;


    }

    public static double speedFrom(double lat1, double lng1, double lat2, double lng2, int seconds) {


        return 3.6 * distFrom(lat1, lng1, lat2, lng2) / seconds;


    }

    //return components
    TreeMap<Integer, ArrayList<HashSet<Person>>> getComponents() {
        HashMap<Integer, HashSet<Person>> components = new HashMap<Integer, HashSet<Person>>();
        HashSet<Person> toCrawl = new HashSet<Person>(this.people.values());



        int i = 0; //component count



        while (toCrawl.size() > 0) {
            HashSet<Person> q = new HashSet<Person>();

            components.put(i, new HashSet<Person>());

            Person p = toCrawl.iterator().next();

            q.add(p);

            components.get(i).add(p);



            while (q.size() > 0) {
                Person px = q.iterator().next();
                q.remove(px);
                toCrawl.remove(px);


                for (Person pxf : px.getFriends()) {
                    if (toCrawl.contains(pxf)) {
                        q.add(pxf);
                        components.get(i).add(pxf);


                    }

                }
            }

            //crawling finsihed, next component, if toCrawl is not empty
            i++;


        }

        TreeMap<Integer, ArrayList<HashSet<Person>>> map = new TreeMap<Integer, ArrayList<HashSet<Person>>>();



        for (HashSet<Person> comp : components.values()) {
            if (map.get(comp.size()) == null) {

                map.put(comp.size(), new ArrayList<HashSet<Person>>());


            }
            map.get(comp.size()).add(comp);


        }

        int c = 0;


        for (Integer ix : map.descendingKeySet()) {
            if (c > 0) {
                for (HashSet<Person> s : map.get(ix)) {
                    for (Person p : s) {
                        this.removePerson((MapNode) p);


                    }
                }
            }
            c++;


        }

        return map;


    }

    void printCompCount() {

        TreeMap<Integer, ArrayList<HashSet<Person>>> componentMap = this.getComponents();



        for (Integer s : componentMap.keySet()) {

            System.out.println(s + " " + componentMap.get(s).size());


        }

        System.out.println(this.size() + " " + this.getNumEdges() + " " + this.totalWeight());

    }
}
