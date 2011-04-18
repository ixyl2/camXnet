/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;

import java.util.*;

/**
 *
 * @author ixyl2
 */
public class Epidemic {

    Graph graph;
    private HashSet<Person> infected;
    private HashSet<Person> recovered;
    private HashSet<Person> dead;
    private double p, r, d;
    private Random rng = new Random();

    //think about how to spread the initial infectants
    Epidemic(Graph graph, int initialInfectants, double p, double r, double d) {

        this.graph = graph;
        this.p = p;
        this.r = r;
        this.d = d;

        infected = new HashSet<Person>();
        recovered = new HashSet<Person>();
        dead = new HashSet<Person>();

        Person pi = graph.getRandomPerson();
        infected.add(pi);

        //if...
        calDistance(pi);
    }

    //add the bit for decaying death rate
    double[][] run(int days) {

        double[][] result = new double[6][days + 1];

        //System.out.println("Day\tSus\tInf\tDead\tRec\tMortality");
        //System.out.println("0\t" + this.numSusceptible() + "\t" + this.numInfected() + "\t" + this.numDead() + "\t" + this.numRecovered() + "\t" + this.mortality());

        result[0][0] = this.numSusceptible();
        result[1][0] = this.numInfected();
        result[2][0] = this.numDead();
        result[3][0] = this.numRecovered();
        result[4][0] = this.mortality();
        result[5][0] = this.numCases();

        for (int i = 1; i <= days; i++) { if (this.numInfected()>0) {           process();
            spread();
}
            result[0][i] = this.numSusceptible();
            result[1][i] = this.numInfected();
            result[2][i] = this.numDead();
            result[3][i] = this.numRecovered();
            result[4][i] = this.mortality();
            result[5][i] = this.numCases();
        }         return result;
    }

    private void process() { //for each infected, let them die or recover or stay

        HashSet<Person> toRemove = new HashSet<Person>();
        double dr;
        double rand;
        
        for (Person pi : infected) {
            dr = d;
            if (pi.getDistance() > 1) {
                dr = d - (double) pi.getDistance() * (d/4);
            }

            if (dr < 0) {
                dr = 0;
            }

            rand = rng.nextDouble();

            if (rand <= r) {
                recovered.add(pi);
                toRemove.add(pi);
            } else if (rand <= r + dr) {
                dead.add(pi);
                toRemove.add(pi);
            }
        }

        infected.removeAll(toRemove);
    }

    private void spread() { //for each infected, infected others with probability p

        HashSet<Person> toBeInfected = new HashSet<Person>();
        double rand;
        for (Person pi : infected) {
            for (Person pf : pi.getFriends()) {
                if ((!recovered.contains(pf)) && (!dead.contains(pf))) {
                    rand = rng.nextDouble();
                    if (rand <= p) {
                        toBeInfected.add(pf);
                    }
                }
            }
        }

        infected.addAll(toBeInfected);
    }

    private void calDistance(Person p) {

        for (Person x : graph.getPeople()) {
            x.putDistance(999);
        }

        p.putDistance(0);

        ArrayList<HashSet<Person>> levels = new ArrayList<HashSet<Person>>();

        levels.add(0, new HashSet<Person>());
        levels.get(0).add(p);

        for (int i = 1; (levels.get(i - 1).size() != 0); i++) {
            levels.add(i, new HashSet<Person>());
            for (Person x : levels.get(i - 1)) {
                for (Person xf : x.getFriends()) {
                    if (xf.getDistance() == 999) { //unvisited
                        xf.putDistance(x.getDistance() + 1);
                        levels.get(i).add(xf);
                    }
                }
            }
        }
    }

    int numDead() {
        return dead.size();
    }

    int numRecovered() {
        return recovered.size();
    }

    int numInfected() {
        return infected.size();
    }

    int numCases() {
        return numDead() + numRecovered() + numInfected();
    }

    int numSusceptible() {
        return graph.size() - numCases();
    }

    double mortality() {
        return (double) numDead() / numCases();
    }

}
