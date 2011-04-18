/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import java.io.*;
import java.awt.Color;

/**
 *
 * @author ixyl2
 */
public class Graph implements Serializable {

    static Random rng = new Random();
    protected HashMap<Integer, Person> people;

    Graph() {
        people = new HashMap<Integer, Person>();
    }

    Graph(String filename) {
        //Unserialise
        System.out.println("Attempting to read from binary file");

        try {
            people = ((Graph) loadFrom(filename)).people;
            people.get(0).linkGraph(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Deserialising");
        for (Person p : people.values()) {
            p.unSerialise();
        }

        int edge = 0;
        double weight = 0;
        for (Person i : people.values()) {
            edge += i.getFriends().size();
            weight += i.getTotalWeight();
        }

        System.out.println("Number of nodes: " + people.size() + " Number of edges: " + edge + " Weight:" + weight);
    }

    Graph(Collection<Person> people) {
        this.people = new HashMap<Integer, Person>();

        for (Person p : people) {
            this.people.put(p.getID(), new Person(p.getID(), p.getName()));
        }

        for (Person p : people) {
            for (Person f : p.getFriends()) {
                this.people.get(p.getID()).addFriend(this.people.get(f.getID()), p.getWeightTo(f));
                this.people.get(f.getID()).addFriend(this.people.get(p.getID()), f.getWeightTo(p));
            }

        }

    }

    Collection<Person> getPeople() {
        return people.values();
    }

    HashMap<Integer, Person> getMap() {
        return people;
    }

    double totalWeight() {
        double weight = 0;
        for (Person p : this.getPeople()) {
            weight += p.getTotalWeight();
        }
        return weight;
    }

    void randomize(int n) {
        for (int i = 0; i < n; i++) {
            Person p1 = this.getRandomPerson();
            Person p1f = p1.randomFriend();

            Person p2 = this.getRandomPerson();
            Person p2f = p2.randomFriend();

            while (p2.equals(p1) || p2.isFriendOf(p1) || p1f.isFriendOf(p2f) || p2f.equals(p1f)) {
                p2 = this.getRandomPerson();
                p2f = p2.randomFriend();
            }

            p1.removeFriend(p1f);
            p1f.removeFriend(p1);
            p2.removeFriend(p2f);
            p2f.removeFriend(p2);

            p1.addFriend(p2, 1.0);
            p1f.addFriend(p2f, 1.0);
            p2.addFriend(p1, 1.0);
            p2f.addFriend(p1f, 1.0);
        }
    }

    //randomise weighted network
    void randomizeW() {

        for (Person p : this.getPeople()) {
            //System.out.print("\r" + ++count + " of " + this.size());

            ArrayList<Person> fds = new ArrayList<Person>(p.getFriends());

            //System.out.println("Weight before : " + p.getTotalWeight() + "degree: " + p.degree());
            for (Person pf : fds) { // reconnect each link of weight w from p to pf to some random p' as if there are w links
                double w = p.getWeightTo(pf);

                p.removeFriend(pf);
                pf.removeFriend(p);

                for (int i = 0; i < w - 0.1; i++) {

                    Person p1 = null; // pick random p'
                    Person p1f = null;

                    while ((p1 == null) || p.equals(p1) || (p1f == null) || pf.equals(p1f)) {
                        p1 = this.getRandomPerson();
                        p1f = null;
                        if ((!p1.equals(p)) && (p1.degree() > 0)) {
                            p1f = p1.randomFriend();
                        }
                    }

                    //delete p1 to p1f, connect p to p1 and pf to p1f, the code above makes sure that p != p1, pf != p1f
                    //note that it is ok for pf = p1, in which case pf will delete a frienda and connect itself back
                    //also if p1f = p, then pf will connect back to p, and p1 to p, hence nothing is changed


                    double ww = p1.getWeightTo(p1f); // assume undirected network

                    //delete p1 to p1f
                    if (ww <= 1.01) {
                        p1.removeFriend(p1f);
                        p1f.removeFriend(p1);
                    } else {
                        p1.putWeight(p1f, ww - 1.0);
                        p1f.putWeight(p1, ww - 1.0);
                    }


                    //connect p to p1
                    if (p.isFriendOf(p1)) {
                        p.putWeight(p1, p.getWeightTo(p1) + 1.0);
                        p1.putWeight(p, p1.getWeightTo(p) + 1.0);
                    } else {
                        p.addFriend(p1, 1.0);
                        p1.addFriend(p, 1.0);
                    }

                    //connect pf to p1f

                    if (pf.isFriendOf(p1f)) {
                        pf.putWeight(p1f, pf.getWeightTo(p1f) + 1.0);
                        p1f.putWeight(pf, p1f.getWeightTo(pf) + 1.0);
                    } else {
                        pf.addFriend(p1f, 1.0);
                        p1f.addFriend(pf, 1.0);
                    }

                }
            }
            //System.out.println("Weight after : " + p.getTotalWeight() + "degree: " + p.degree());

        }

    }

    //randomise weighted network with inter chromosome restriction.
    void randomizeW(int[] chromo) {

        for (Person p : this.getPeople()) {
            //System.out.print("\r" + ++count + " of " + this.size());

            ArrayList<Person> fds = new ArrayList<Person>(p.getFriends());

            //System.out.println("Weight before : " + p.getTotalWeight() + "degree: " + p.degree());
            for (Person pf : fds) { // reconnect each link of weight w from p to pf to some random p' as if there are w links
                double w = p.getWeightTo(pf);

                p.removeFriend(pf);
                pf.removeFriend(p);

                for (int i = 0; i < w - 0.1; i++) {

                    Person p1 = null; // pick random p'
                    Person p1f = null;

                    while ((p1 == null) || p.equals(p1) || (p1f == null) || pf.equals(p1f) || (chromo[p1.getID()] == chromo[p.getID()]) || (chromo[pf.getID()] == chromo[p1f.getID()])) {
                        p1 = this.getRandomPerson();
                        p1f = null;
                        if ((!p1.equals(p)) && (p1.degree() > 0)) {
                            p1f = p1.randomFriend();
                        }
                    }

                    //delete p1 to p1f, connect p to p1 and pf to p1f, the code above makes sure that p != p1, pf != p1f
                    //note that it is ok for pf = p1, in which case pf will delete a frienda and connect itself back
                    //also if p1f = p, then pf will connect back to p, and p1 to p, hence nothing is changed


                    double ww = p1.getWeightTo(p1f); // assume undirected network

                    //delete p1 to p1f
                    if (ww <= 1.01) {
                        p1.removeFriend(p1f);
                        p1f.removeFriend(p1);
                    } else {
                        p1.putWeight(p1f, ww - 1.0);
                        p1f.putWeight(p1, ww - 1.0);
                    }


                    //connect p to p1
                    if (p.isFriendOf(p1)) {
                        p.putWeight(p1, p.getWeightTo(p1) + 1.0);
                        p1.putWeight(p, p1.getWeightTo(p) + 1.0);
                    } else {
                        p.addFriend(p1, 1.0);
                        p1.addFriend(p, 1.0);
                    }

                    //connect pf to p1f

                    if (pf.isFriendOf(p1f)) {
                        pf.putWeight(p1f, pf.getWeightTo(p1f) + 1.0);
                        p1f.putWeight(pf, p1f.getWeightTo(pf) + 1.0);
                    } else {
                        pf.addFriend(p1f, 1.0);
                        p1f.addFriend(pf, 1.0);
                    }

                }
            }
            //System.out.println("Weight after : " + p.getTotalWeight() + "degree: " + p.degree());

        }

    }

    // Directed, i.e. total degree
    int getNumEdges() {
        int edges = 0;
        for (Person p : people.values()) {
            edges += p.degree();
        }
        return edges;
    }

    int size() {
        return people.size();
    }

//    void diameter() {
//
//        ArrayList<Set<Person>> level = new ArrayList<Set<Person>>();
//        HashSet<Person> sofar = new HashSet<Person>();
//        for (int i = 0; i <= 9999; i++) {
//            level.add(new HashSet());
//        }
//        level.get(0).add(this);
//        sofar.add(this);
//
//        //FIX: reachcount should equal sofar.size instead?
//        synchronized (this) {
//            this.reachCount = people.size() - this.weight;
//
//        }
//        this.closeness = 0;
//
//        for (int i = 1; i <= N; i++) {
//            for (MapNode p : level.get(i - 1)) {
//                for (Person px : p.getFriends()) {
//                    MapNode x = (MapNode) px;
//                    if (!sofar.contains(x)) {
//                        level.get(i).add(x);
//                        sofar.add(x);
//                        this.closeness += i * x.weight;
//
//                        synchronized (x) {
//                            if (x.reachCount < people.size() - x.weight) {
//                                x.closeness += i * x.weight;
//                                x.reachCount++;
//                            }
//                        }
//                    }
//                }
//            }
//            if (level.get(i).isEmpty()) {
//                System.out.println(i - 1);
//                break; //Terminate because this level has no one in, the lenght of the graph is i-1
//            }
//
//        }
//        // System.out.println(this.dia + "  "+ this.closeness);
//    }

    void removePerson(Person p) {
        p.removeAllFriends();
        people.remove(p.getID());
    }

    Person getPerson(int id) {
        return people.get(id);
    }

    void filteredBy(double linkWeight, double nodeWeight, double nodeMaxLinkWeight, int degree) {

        //if group = true, make a projection of group 1 from group 2

        ArrayList<Person> toRemove = new ArrayList<Person>();

        boolean changed = true;

        while (changed) {

            changed = false;

            for (Person p : this.getPeople()) {
                if (p.degree() < degree) {
                    toRemove.add(p);
                }
            }

            for (Person p : toRemove) {
                this.removePerson(p);
            }

            if (toRemove.size() > 0) {
                changed = true;
            }

            toRemove = new ArrayList<Person>();

            for (Person p : this.getPeople()) {
                if (p.getTotalWeight() < nodeWeight) {
                    toRemove.add(p);
                }
            }

            for (Person p : toRemove) {
                this.removePerson(p);
            }

            if (toRemove.size() > 0) {
                changed = true;
            }

            toRemove = new ArrayList<Person>();

            for (Person p : this.getPeople()) {
                if (p.getMaxWeight() < nodeMaxLinkWeight) {
                    toRemove.add(p);
                }
            }

            for (Person p : toRemove) {
                this.removePerson(p);
            }

            if (toRemove.size() > 0) {
                changed = true;
            }

            for (Person p : this.getPeople()) {
                toRemove = new ArrayList<Person>();
                for (Person pf : p.getFriends()) {
                    if (p.getWeightTo(pf) < linkWeight) {
                        toRemove.add(pf);
                    }
                }
                for (Person pf : toRemove) {
                    p.removeFriend(pf);
                }
                if (toRemove.size() > 0) {
                    changed = true;
                }
            }
            System.out.println("Size : " + this.size() + "Edges : " + this.getNumEdges());
        }

        sortIDs();

    }

    void connectAll(Collection<Person> pps) {
        for (Person p : pps) {
            for (Person pf : pps) {
                if (p != pf) {
                    if (p.isFriendOf(pf)) {
                        p.putWeight(pf, p.getWeightTo(pf) + 1);
                    } else {
                        p.addFriend(pf, 1);
                    }
                }
            }
        }
    }

    Person getRandomPerson() {
        //System.out.println(people.degree());
        return people.get(rng.nextInt(people.size()));
    }

    double getClustering() {
        double cl = 0;
        for (Person p : this.getPeople()) {
            cl += p.getClustering();
        }
        return cl / (double) this.size();
    }

    void sortIDs() {
        HashMap<Integer, Person> newPeople = new HashMap<Integer, Person>();
        int i = 0;
        for (Person p : people.values()) {
            newPeople.put(i, p);
            p.changeID(i);
            i++;
        }
        this.people = newPeople;

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

        return map;
    }

    void outputNodeWeights(String name) {
        LineWriter out = new LineWriter(new File(name + ".NodeWeights"));

        try {
            for (Person p : this.getPeople()) {
                out.writeLine(p.getTotalWeight() + "");
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void outputEdgeWeights(String name) {
        LineWriter out = new LineWriter(new File(name + ".EdgeWeights"));

        try {
            for (Person p : this.getPeople()) {
                for (Person pf : p.getFriends()) {
                    out.writeLine(p.getWeightTo(pf) + "");
                }
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void printCompCount() {

        TreeMap<Integer, ArrayList<HashSet<Person>>> componentMap = this.getComponents();

        for (Integer s : componentMap.keySet()) {

            System.out.println(s + " " + componentMap.get(s).size());
        }
    }

    void outputCompList(String folder) {

        TreeMap<Integer, ArrayList<HashSet<Person>>> componentMap = this.getComponents();

        (new File("" + folder)).mkdir();

        for (Integer s : componentMap.keySet()) {

            if (s > 2) {

                ArrayList<HashSet<Person>> comps = componentMap.get(s);

                (new File(folder + "/" + s)).mkdir();

                int count = 0;
                for (HashSet<Person> comp : comps) {
                    Graph a = new Graph(comp);
                    a.outputSimple(folder + "/" + s + "/" + (count++) + ".list");
                }

            }
        }
    }

    void outputCompCount(String name) {

        LineWriter out = new LineWriter(new File(name + ".compcount"));

        try {
            TreeMap<Integer, ArrayList<HashSet<Person>>> componentMap = this.getComponents();
            for (Integer s : componentMap.keySet()) {
                for (int i = 0; i < componentMap.get(s).size();
                        i++) {
                    out.writeLine(s + "");
                }
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void outputDegreeSeq(String name) {
        LineWriter out = new LineWriter(new File(name + ".degseq"));

        try {
            for (Person p : this.getPeople()) {
                out.writeLine(p.degree() + "");
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    void outputClusternessSeq(String name) {
        LineWriter out = new LineWriter(new File(name + ".clusternessSeq"));

        try {
            for (Person p : this.getPeople()) {
                out.writeLine(p.getClustering() + "");
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    void outputGML(String name) {

        HashSet edges = new HashSet();
        LineWriter xml = new LineWriter(new File(name + ".gml"));
        String[] colour = new String[5000];

        for (int j = 0; j < 5000; j++) {
            colour[j] = Integer.toHexString((int) (Math.random() * 0xFFFFFF));
        }

        try {
            xml.writeLine("graph [");
            xml.writeLine("directed 0");
            xml.writeLine("graphics [ fill \"#FFFFFF\" ]");
            //Print Nodes
            for (Person p : people.values()) {
                xml.writeLine("node [");
                xml.writeLine("id " + p.getID());
                xml.writeLine("label \"" + p.getID() + "\"");
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

            for (Person p : people.values()) {
                for (Person f : p.getFriends()) {

                    String edge = p.getID() + "\t" + f.getID();
                    if (p.getID() > f.getID()) {
                        edge = f.getID() + "\t" + p.getID();
                    }

                    if (!edges.contains(edge)) {

                        xml.writeLine("edge [");

                        xml.writeLine("source " + p.getID());
                        xml.writeLine("target " + f.getID());


                        Color c = new Color((int) ((Math.log10(p.getWeightTo(f)) / 4) * 255), 0, 255 - (int) ((Math.log10(p.getWeightTo(f)) / 4) * 255));


                        xml.writeLine("graphics [ width 1 type \"line\" fill \"#" + Integer.toHexString(c.getRGB() & 0xffffff + 0x1000000).substring(1) + "\" ]");
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

    void outputXML(String filename) {


        LineWriter xml = new LineWriter(new File(filename));

        try {
            xml.writeLine("<?xml version='1.0' encoding='UTF-8'?>");
            xml.writeLine("<graphml xmlns='http://graphml.graphdrawing.org/xmlns'>");
            xml.writeLine("<graph edgedefault='undirected'>");
            xml.writeLine("<key id='name' for='node' attr.name='name' attr.type='int'/>");
            xml.writeLine("<key id='cluster' for='node' attr.name='cluster' attr.type='int'/>");
            xml.writeLine("<key id='weight' for='node' attr.name='weight' attr.type='int'/>");
            //Print Nodes
            for (Person p : people.values()) {
                xml.writeLine("<node id='" + p.getID() + "'>");
                xml.writeLine("\t" + "<data key='name'>" + p.getID() + "</data>");
                xml.writeLine("\t" + "<data key='cluster'>" + p.getMaxClusterID() + "</data>");
                //xml.writeLine("\t" + "<data key='weight'>" + (int) (p.between * 10) + "</data>");
                xml.writeLine("</node>");
            }

            for (Person p : people.values()) {
                for (Person f : p.getFriends()) {
                    xml.writeLine(" <edge source='" + p.getID() + "' target='" + f.getID() + "'></edge>");
                }
            }
            xml.writeLine("</graph>");
            xml.writeLine("</graphml>");
            xml.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
            for (Person p : people.values()) {
                xml.writeLine("<node id='" + p.getID() + "' label='" + p.getName() + "'>\n"
                        + "<att name='weight' type='integer' value='" + p.degree() + "'/>\n"
                        + "</node>");
            }


            for (Person p : people.values()) {

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

    void outputSimple(String filename) {

        HashSet edges = new HashSet();
        LineWriter xml = new LineWriter(new File(filename + ".simple"));

        try {
            for (Person p : people.values()) {
                for (Person f : p.getFriends()) {
                    String edge = p.getID() + " " + f.getID();
                    if (p.getID() > f.getID()) {
                        edge = f.getID() + " " + p.getID();
                    }

                    if (!edges.contains(edge)) {
                        xml.writeLine(p.getID() + "\t" + f.getID());
                        edges.add(edge);
                    }
                }
            }

            xml.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void serialise(String filename) throws IOException {
        for (Person p : this.getPeople()) {
            p.serialise();
        }
        ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream(filename));
        objstream.writeObject(this);
        objstream.close();
        for (Person p : this.getPeople()) {
            p.friendList.clear();
        }
    }

    private static Object loadFrom(String filename) throws ClassNotFoundException, IOException {
        ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(filename));
        Object object = objstream.readObject();
        objstream.close();
        return object;
    }

    void printSamples(int n, String filename) {
        LineWriter samples = new LineWriter(new File(filename));

        HashSet edges = new HashSet();
        try {
            for (int i = 0; i < n; i++) {
                Person p = this.getRandomPerson();
                Person f = p.randomFriend();
                String edge = p.getID() + " " + f.getID();
                if (p.getID() > f.getID()) {
                    edge = f.getID() + " " + p.getID();
                }

                if (!edges.contains(edge)) {
                    samples.writeLine(p.getWeightTo(f) + "");
                    edges.add(edge);
                }
            }
            samples.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void printSamples(String filename) {
        LineWriter samples = new LineWriter(new File(filename));

        try {
            for (Person p : this.getPeople()) {
                for (Person f : p.getFriends()) {

                    if (p.getID() > f.getID()) {
                        samples.writeLine(p.getWeightTo(f) + "");
                    }

                }
                samples.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    HashMap<int[], ArrayList<Double>> getLinks(double weight) {
        HashMap<int[], ArrayList<Double>> links = new HashMap<int[], ArrayList<Double>>();

        for (Person p : this.getPeople()) {
            for (Person pf : p.getFriends()) {
                if (p.getID() < pf.getID()) {
                    if (p.getWeightTo(pf) > weight) {
                        int[] link = {p.getID(), pf.getID()};
                        links.put(link, new ArrayList<Double>());
                        links.get(link).add(p.getWeightTo(pf));
                    }
                }
            }
        }

        return links;
    }
}
