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
public class ContDynamic {

    Graph graph;
    double expMean;
    double msgMean;
    int threshold;
    int threshold2 = 1;
    int days;
    int intro;
    double maxW;
    boolean telecom;
    HashMap<Person, HashSet<Person>> goodFriends;
    Random rng;
    TreeMap<Double, int[]> scheduler;
    Double[][] schedulerAccess;
    ArrayList<ArrayList<PriorityQueue<Double>>> arrivals;
    ArrayList<ArrayList<PriorityQueue<Double>>> removals;

    ContDynamic(Graph graph, double poiMean, double expMean, int threshold, int threshold2, double maxW, int days, int intro, boolean telecom) {

        this.graph = graph;

        this.msgMean = 1.0 / poiMean;
        this.expMean = expMean;

        this.threshold = threshold;
        this.days = days;
        this.maxW = maxW;

        this.telecom = telecom; //telecommunication model

        this.intro = intro;

        rng = new Random(System.currentTimeMillis());


        scheduler = new TreeMap<Double, int[]>();
        schedulerAccess = new Double[graph.size()][graph.size()];

        arrivals = new ArrayList<ArrayList<PriorityQueue<Double>>>(graph.size());
        removals = new ArrayList<ArrayList<PriorityQueue<Double>>>(graph.size());

    }

    void reset() {
        //reset all weight to 0
        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                p.putWeight(pf, 0);
                pf.putWeight(p, 0);
            }
        }

        for (int i = 0; i < graph.size(); i++) {
            arrivals.add(new ArrayList<PriorityQueue<Double>>(graph.size()));
            removals.add(new ArrayList<PriorityQueue<Double>>(graph.size()));
            for (int j = 0; j < graph.size(); j++) {
                arrivals.get(i).add(new PriorityQueue<Double>());
                removals.get(i).add(new PriorityQueue<Double>());
            }
        }



        //generate arrival times
        for (int i = 0; i < graph.size(); i++) {
            for (int j = 0; j < graph.size(); j++) {
                if (i < j) { //undirected!
                    double current = 0;
                    double interval = 0;
                    double pause = interval;
                    while (current < days + 1) { // so that the scheduler can terminate after days
                        current += sampleExp(msgMean);

                        arrivals.get(i).get(j).add(current);

                        if (current > pause) {
                            current += interval;
                            pause += interval * 2;
                        }
                    }
                }
            }
        }

        //populate the scheduler
        for (int i = 0; i < graph.size(); i++) {
            for (int j = 0; j < graph.size(); j++) {
                if (i < j) { //undirected!
                    scheduler.put(arrivals.get(i).get(j).peek(), new int[]{i, j});
                    schedulerAccess[i][j] = arrivals.get(i).get(j).peek();
                }
            }
        }


        if (!telecom) {
            goodFriends = new HashMap<Person, HashSet<Person>>();

            for (Person p : graph.getPeople()) {
                goodFriends.put(p, new HashSet<Person>());

            }
        }
    }

    void updateScheduler(Person p, Person pf, Double time) {

        updateScheduler(p.getID(), pf.getID(), time);

    }

    void updateScheduler(int i, int j, Double time) {

        if (i > j) { //make sure i < j
            int x = i;
            i = j;
            j = x;
        }

        if (schedulerAccess[i][j] == null || time.doubleValue() < schedulerAccess[i][j].doubleValue()) { // only update if the new time is smaller the the current one
            scheduler.remove(schedulerAccess[i][j]);
            scheduler.put(time, new int[]{i, j});
            schedulerAccess[i][j] = time;
        }

    }

    void run() {
        reset();
//        ArrayList<Person> randomPP = new ArrayList<Person>();
//        ArrayList<Person> randomPF = new ArrayList<Person>();
//        for (int i = 0; i < 200; i++) { //sample random links
//            Person p = graph.getPerson(rng.nextInt(graph.size());
//            randomPP.add(p);
//            Person pf = p.randomFriend();
//            randomPF.add(pf);
//        }

        Double currentTime = 0.0;

        //LineWriter lw = new LineWriter(new File("sid/run-" + System.currentTimeMillis()));

        try {

            double intervals = 0;

            while (currentTime <= days) {

                currentTime = scheduler.firstKey();

                int p = scheduler.firstEntry().getValue()[0];
                int pf = scheduler.firstEntry().getValue()[1];


                scheduler.pollFirstEntry();

                updateWeights(p, pf, currentTime);





                if ((int) (currentTime * 10) == (int) (intervals * 10)) {
                    //System.out.println(currentTime);
                    System.out.println(currentTime + "\t" + average() + "\t" + xc());
                    intervals += 0.1;
//                    System.out.println(scheduler.size());
////                    for (Double d : scheduler.keySet()) {
////                        System.out.println(d);
////                    }
//
//                    int ar = 0;
//                    int re = 0;
//                    for (int i = 0; i < graph.size(); i++) {
//
//                        for (int j = 0; j < graph.size(); j++) {
//                            if (i < j) {
//                                ar += arrivals.get(i).get(j).size();
//                                re += removals.get(i).get(j).size();
//                            }
//                        }
//                    }
//
//                    System.out.println(ar + " " + re);
                }


                //System.out.println(currentTime);

                //lw.write(average(50) + "\t" + xc());
                //lw.writeLine();


            }
            //lw.flush();
            } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println(currentTime + "\t" + average() + "\t" + xc());

//        System.out.println(scheduler.size());
//
//        int ar = 0;
//        int re = 0;
//        for (int i = 0; i < graph.size(); i++) {
//
//            for (int j = 0; j < graph.size(); j++) {
//                if (i < j) {
//                    ar += arrivals.get(i).get(j).size();
//                    re += removals.get(i).get(j).size();
//                }
//            }
//        }
//        System.out.println(ar + " " + re);
    }

    //BUG : time1 and time2 cannot be equal in terms of value: WHY?!
    void updateWeights(int i, int j, Double currentTime) {

        Person p = graph.getPerson(i);
        Person pf = graph.getPerson(j);

        if (arrivals.get(i).get(j).peek() == currentTime) {
            if (p.getWeightTo(pf) >= maxW) {
                boolean go = true;
                while (go) {

//                    go = false;
//                    Person px = graph.getRandomPerson();
//                    Person pxf = px.randomFriend();
//
//                    Person px2 = graph.getRandomPerson();
//                    Person px2f = px2.randomFriend();
//
//                    if ((px.getWeightTo(pxf) < maxW) && (px2.getWeightTo(px2f) < maxW)) { //there is enough capacity left in both links
//                        px.putWeight(pxf, px.getWeightTo(pxf) + 1);
//                        pxf.putWeight(px, pxf.getWeightTo(px) + 1);
//                        px2.putWeight(px2f, px2.getWeightTo(px2f) + 1);
//                        px2f.putWeight(px2, px2f.getWeightTo(px2) + 1);
//
//                        double time1 = currentTime + sampleExp(expMean);
//
//                        if (px.getID() < pxf.getID()) {
//                            removals.get(px.getID()).get(pxf.getID()).add(time1);
//                        } else {
//                            removals.get(pxf.getID()).get(px.getID()).add(time1);
//                        }
//
//                        updateScheduler(px, pxf, time1);
//
//                        if (px2.getID() < px2f.getID()) {
//                            removals.get(px2.getID()).get(px2f.getID()).add(time1);
//                        } else {
//                            removals.get(px2f.getID()).get(px2.getID()).add(time1);
//                        }
//
//                        updateScheduler(px2, px2f, time1);
//                    }

                    Person pf2 = p.randomFriend();
                    if (pf2 != pf) {

                        go = false;
                        if ((p.getWeightTo(pf2) < maxW) && (pf.getWeightTo(pf2) < maxW)) { //there is enough capacity left in both links
                            p.putWeight(pf2, p.getWeightTo(pf2) + 1);
                            pf2.putWeight(p, pf2.getWeightTo(p) + 1);
                            pf.putWeight(pf2, pf.getWeightTo(pf2) + 1);
                            pf2.putWeight(pf, pf2.getWeightTo(pf) + 1);

                            double exp = sampleExp(expMean);

                            Double time1 = currentTime + exp;

                            Double time2 = currentTime + exp;
                            time1 *= 0.99999;

                            if (pf2.getID() < p.getID()) {
                                removals.get(pf2.getID()).get(p.getID()).add(time1);
                            } else {
                                removals.get(p.getID()).get(pf2.getID()).add(time1);
                            }

                            updateScheduler(p, pf2, time1);

                            if (pf2.getID() < pf.getID()) {
                                removals.get(pf2.getID()).get(pf.getID()).add(time2);
                            } else {
                                removals.get(pf.getID()).get(pf2.getID()).add(time2);
                            }

                            updateScheduler(pf, pf2, time2);

                        }
                    }
                }
            } else {
                p.putWeight(pf, p.getWeightTo(pf) + 1);
                pf.putWeight(p, pf.getWeightTo(p) + 1);
                Double time1 = currentTime + sampleExp(expMean);
                removals.get(i).get(j).add(time1);
                updateScheduler(p, pf, time1);
            }
            arrivals.get(i).get(j).poll();
        } else if (removals.get(i).get(j).peek() == currentTime) {
            p.putWeight(pf, p.getWeightTo(pf) - 1);
            pf.putWeight(p, pf.getWeightTo(p) - 1);
            removals.get(i).get(j).poll();
            if (p.getWeightTo(pf) < 0) {
                System.out.println("Error 1");
            }
        } else {
            System.out.println("Error 2");

        }



        //update the scheduler
        if (arrivals.get(i).get(j).isEmpty()) {
            if (!removals.get(i).get(j).isEmpty()) {
                scheduler.put(removals.get(i).get(j).peek(), new int[]{i, j});
                schedulerAccess[i][j] = removals.get(i).get(j).peek();
            } else {
                schedulerAccess[i][j] = null;
            }
        } else if (removals.get(i).get(j).isEmpty()) {
            if (!arrivals.get(i).get(j).isEmpty()) {
                scheduler.put(arrivals.get(i).get(j).peek(), new int[]{i, j});
                schedulerAccess[i][j] = arrivals.get(i).get(j).peek();
            } else {
                schedulerAccess[i][j] = null;
            }
        } else { //both are not empty
            if (arrivals.get(i).get(j).peek().doubleValue() < removals.get(i).get(j).peek().doubleValue()) {
                scheduler.put(arrivals.get(i).get(j).peek(), new int[]{i, j});
                schedulerAccess[i][j] = arrivals.get(i).get(j).peek();
            } else {
                scheduler.put(removals.get(i).get(j).peek(), new int[]{i, j});
                schedulerAccess[i][j] = removals.get(i).get(j).peek();
            }
        }

    }

    double[] weightSamples(int n) {

        double[] samples = new double[n];
        for (int i = 0; i <
                n; i++) {
            Person p = graph.getRandomPerson();

            Person pf = p.randomFriend();

            samples[i] = p.getWeightTo(pf);
        }

        return samples;
    }

    double average() {

        double w = 0;
        double e = 0;
        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                if (p.getID() > pf.getID()) { //undirected
                    w += p.getWeightTo(pf);
                    e++;
                }
            }
        }
        return w / e;
    }

    double average(int n) {
        double[] sums = weightSamples(n);
        double sum = 0;
        for (int i = 0; i <
                n; i++) {
            sum += sums[i];
        }

        return sum / (double) n;
    }

    void outSamples(String file, int n, boolean append) {
        LineWriter lw = new LineWriter(new File("sid/" + file), append);

        double[] samples = weightSamples(n);

        try {
            for (int i = 0; i <
                    n; i++) {
                lw.writeLine(samples[i] + "");
            }

            lw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    double xc() {
        int total = 0;
        int count = 0;
        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                if (p.getID() > pf.getID()) { //undirected
                    total++;
                    if (p.getWeightTo(pf) > (maxW - 0.5)) {
                        count++;
                    }

                }
            }
        }
        return (double) count / total;
    }

    void outEvolution(String file, int n) {
        LineWriter lw = new LineWriter(new File("sid/" + file));

        double[] samples = weightSamples(n);

        try {
            for (int i = 0; i <
                    n; i++) {
                lw.writeLine(samples[i] + "");
            }

            lw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    double sampleExp(double mean) {
        return (-mean * Math.log(1 - rng.nextDouble()));
    }

}
