/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;
import Utility.*;
import java.io.*;

/**
 *
 * @author ixyl2
 */
public class Dynamic {

    Graph graph;
    Poisson poi;
    Exponential exp;
    double expMean;
    int threshold;
    int threshold2 = 1;
    int days;
    int intro;
    int timesteps;
    double maxW;
    boolean telecom;
    HashMap<Person, HashSet<Person>> goodFriends;
    Random rng;

    Dynamic(Graph graph, int poiMean, double expMean, int threshold, int threshold2, double maxW, int days, int intro, int timesteps, boolean telecom) {

        this.graph = graph;


        poi = new Poisson((double)poiMean / timesteps);
        exp = new Exponential(expMean);
        this.expMean = expMean;
        this.threshold = threshold;
        this.days = days;
        this.timesteps = timesteps;
        this.maxW = maxW;

        this.telecom = telecom; //telecommunication model



        this.intro = intro;

        rng = new Random();

    }

    void reset() {
        //reset all weight to 0
        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                p.putWeight(pf, 0);
            }
        }

        if (!telecom) {
            goodFriends = new HashMap<Person, HashSet<Person>>();

            for (Person p : graph.getPeople()) {
                goodFriends.put(p, new HashSet<Person>());

            }
        }
    }

    void run() {
        reset();
//        ArrayList<Person> randomPP = new ArrayList<Person>();
//        ArrayList<Person> randomPF = new ArrayList<Person>();
//        for (int i = 0; i < 200; i++) { //sample random links
//            Person p = graph.getPerson(rng.nextInt(graph.size()));
//            randomPP.add(p);
//            Person pf = p.randomFriend();
//            randomPF.add(pf);
//        }

        LineWriter lw = new LineWriter(new File("sid/run-" + System.currentTimeMillis()));

        try {


            for (int i = 1; i <= days; i++) {
                System.out.println("Day " + i);
                if (telecom) {
                    for (int j = 0; j < timesteps; j++) {
                        updateWeights2();
                        decayAll();
                    }
                } else {
                    updateWeights();
                    interact();
                }
                //System.out.println(average(1000) + "\t" + xc());
                lw.write(average(100) + "\t" + xc());
                lw.writeLine();

                if (i % 5 == 0) {
                    //  outSamples("weights", 1000, true);
                }

                //       System.out.println(graph.people.get(1).getWeightTo(graph.people.get(2)));
            }
            lw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void updateWeights() {

        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                if (p.getID() > pf.getID()) {//undirected
                    double oldWeight = p.getWeightTo(pf);
                    int newWeight = decay((int) (oldWeight + poi.sampleInt()));

                    p.putWeight(pf, Math.min(newWeight, maxW));
                    pf.putWeight(p, Math.min(newWeight, maxW));

                    if ((oldWeight <= threshold2) && (newWeight > threshold2)) {
                        goodFriends.get(p).add(pf);
                        goodFriends.get(pf).add(p);
                    } else if ((oldWeight > threshold2) && (newWeight <= threshold2)) {
                        goodFriends.get(p).remove(pf);
                        goodFriends.get(pf).remove(p);
                    }
                }

            }

        }
    }

    void updateWeights2() {


        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                if (p.getID() > pf.getID()) {//undirected
                    double oldWeight = p.getWeightTo(pf);
                    int msgs = poi.sampleInt();

                    int left = (int) (oldWeight + msgs - maxW);

                    int newWeight = (int) (oldWeight + msgs);

                    if (left > 0) {
                        newWeight = (int) maxW;
                        while (left > 0) {
                            Person pf2 = p.randomFriend();
                            if (pf2 != pf) {
                                if ((p.getWeightTo(pf2) < maxW) && (pf.getWeightTo(pf2) < maxW)) { //there is enough capacity left in both links
                                    p.putWeight(pf2, p.getWeightTo(pf2) + 1);
                                    pf2.putWeight(p, pf2.getWeightTo(p) + 1);
                                    pf.putWeight(pf2, pf.getWeightTo(pf2) + 1);
                                    pf2.putWeight(pf, pf2.getWeightTo(pf) + 1);
                                }
                                left--; //drop the call
                            }
                        }
                    }



                    p.putWeight(pf, newWeight);
                    pf.putWeight(p, newWeight);



                }
            }

        }
    }

    int decay(int n) {

        int sub = n;


        n = sub;
        for (int i = 0; i < n; i++) {
            if (Math.random() < (1 / (expMean * timesteps))) {
                sub--;
            }
        }

        return sub;
    }

    void decayAll() {
        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                if (p.getID() > pf.getID()) { //undirected
                    int weight = (int) p.getWeightTo(pf);

                    int newWeight = decay(weight);

                    p.putWeight(pf, newWeight);
                    pf.putWeight(p, newWeight);
                }
            }
        }
    }

    void interact() {

        for (Person p : graph.getPeople()) {
            for (Person pf : p.getFriends()) {
                if (p.getID() > pf.getID()) {//undirected
                    if (p.getWeightTo(pf) > threshold) {
                        if (Math.random() >= 0.5) {
                            //p introduce a friend to pf
                            //has to make sure the friend != pf
                            goodFriends.get(p).remove(pf);

                            if (goodFriends.get(p).size() > 0) {

                                // get a random good friend nf of p
                                int rand = rng.nextInt(goodFriends.get(p).size());
                                Iterator it = goodFriends.get(p).iterator();

                                Person nf = null;

                                for (int i = 0; i <= rand; i++) {
                                    nf = (Person) it.next();
                                }

                                // connect nf to pf
                                double oldWeight = pf.getWeightTo(nf);
                                double newWeight = oldWeight + intro;

                                pf.putWeight(nf, Math.min(newWeight, maxW));
                                nf.putWeight(pf, Math.min(newWeight, maxW));

                                // make sure goodfriends list updated
                                if ((oldWeight < threshold2) && (newWeight >= threshold2)) {
                                    goodFriends.get(pf).add(nf);
                                    goodFriends.get(nf).add(pf);
                                }
                            }

                            goodFriends.get(p).add(pf);
                        } else {
                            goodFriends.get(pf).remove(p);

                            //pf introduce his frind to p
                            if (goodFriends.get(pf).size() > 0) {

                                int rand = rng.nextInt(goodFriends.get(pf).size());
                                Iterator it = goodFriends.get(pf).iterator();

                                Person nf = null;

                                for (int i = 0; i <= rand; i++) {
                                    nf = (Person) it.next();
                                }

                                double oldWeight = p.getWeightTo(nf);
                                double newWeight = oldWeight + intro;

                                p.putWeight(nf, Math.min(newWeight, maxW));
                                nf.putWeight(p, Math.min(newWeight, maxW));

                                if ((oldWeight < threshold2) && (newWeight >= threshold2)) {
                                    goodFriends.get(p).add(nf);
                                    goodFriends.get(nf).add(p);
                                }
                            }

                            goodFriends.get(pf).add(p);
                        }
                    }

                }
            }

        }
    }

    double[] weightSamples(int n) {

        double[] samples = new double[n];
        for (int i = 0; i < n; i++) {
            Person p = graph.getRandomPerson();

            Person pf = p.randomFriend();

            samples[i] = p.getWeightTo(pf);
        }

        return samples;
    }

    double average(int n) {
        double[] sums = weightSamples(n);
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += sums[i];
        }

        return sum / (double) n;
    }

    void outSamples(String file, int n, boolean append) {
        LineWriter lw = new LineWriter(new File("sid/" + file), append);

        double[] samples = weightSamples(n);

        try {
            for (int i = 0; i < n; i++) {
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
            for (int i = 0; i < n; i++) {
                lw.writeLine(samples[i] + "");
            }
            lw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
