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
public class BMPerson extends Person {
    
    //For Benchmark Graphs
    private int assignedC;
    private int assignedSize;
    static int originalGSize;
    private int outside = 0;

    BMPerson(int id) {
        super(id);
    }


    boolean addFriend(BMPerson e, double weight) {
        if (e.id == this.id) { //FIX
            //throw new RuntimeException("Adding self as friend! Check your Code!");
            return false;
        }
        if (!friends.containsKey(e)) {
            if (e.assignedC != this.assignedC) {
                this.outside++;
            }
            friends.put(e, (float) weight);

            return true;
        }
        return false;
    }

    void removeFriend(BMPerson e) {
        if (friends.remove(e) != null) {
            //fix :

            if (e.assignedC != this.assignedC) {
                this.outside--;
            }
        }
    }

    Person randomFriend(boolean outsider) {

        if (outsider && !this.hasOutsider()) {
            return null;
        }
        if (!outsider && !this.hasInsider()) {
            return null;
        }

        ArrayList<Person> randomisedPeople = new ArrayList<Person>(friends.keySet());

        BMPerson p = (BMPerson) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));

        if (outsider) {
            while (p.assignedC == this.assignedC) {
                p = (BMPerson) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));
            }
        } else {
            while (p.assignedC != this.assignedC) {
                p = (BMPerson) randomisedPeople.get(rng.nextInt(randomisedPeople.size()));
            }
        }

        return p;
    }

    //used in nBMGraph only
    Person removeRandomFriend(boolean outsider) {

        Person f = randomFriend(outsider);

        f.removeFriend(this);

        this.removeFriend(f);

        return f;
    }

    boolean hasInsider() {
        return (this.friends.size() - this.outside) > 0;
    }

    boolean hasOutsider() {
        return this.outside > 0;
    }
    // Benchmark graphs only
    void assignC(int c) {
        this.assignedC = c;
    }

    public int getAssignedC() {
        return this.assignedC;
    }

    void assignSize(int size) {
        this.assignedSize = size;
    }

    public int getAssignedSize() {
        return this.assignedSize;
    }


}
