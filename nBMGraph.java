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
public class nBMGraph extends BMGraph {


    nBMGraph(int N, int average, int max, double gamma, double beta, double mu) {

        // Number of People, Average Degree, Max Degree, Degree Power Law, Cluster Power Law, Mixing Parameter

        HashMap<Integer, BMPerson> people = new HashMap<Integer, BMPerson>();
        
        assignedClusters = new HashMap<Integer, Cluster>();

        powerLaw peoplePL = new powerLaw(N, average, gamma, max);

        powerLaw clusterPL = new powerLaw(N, beta, max, peoplePL.getMin()); //Needs to change this always!!!!!!!!!!!!!!!:(

        peoplePL = new powerLaw(clusterPL.allowedP(), average, gamma, clusterPL.getActualMax() - 1);

        //FIX : PeoplePL first finds the min degree by bisection, then clusterPL use that min degree to generate a powerLaw
        // since min cluster degree has to > people min degree and also max..>..max, and also N changes, so peoplePL has to be regenerated again,
        // but it follows that people min degree is again changed, hence we repeat the process until the N's correspond and
        // cluster min indeed > people degree min

        while (clusterPL.getMin() <= peoplePL.getMin()) {

        clusterPL = new powerLaw(N, beta, max, peoplePL.getMin()+1);

        peoplePL = new powerLaw(clusterPL.allowedP(), average, gamma, clusterPL.getActualMax() - 1);

        }

        cCount = new HashMap<Integer, Integer>();

        //Construct people
        int count = 1;
        int xcount = 0;
        for (int i = peoplePL.getMin(); i <= max; i++) {
            for (int j = 0; j < peoplePL.getNum(i); j++) {
                people.put(count, new BMPerson(count));
                people.get(count).assignSize(i);
                count++;
                xcount += i;
            }

        }

//        System.out.println("N : " + N + "/" + count);
        System.out.println("Links - Total : " + xcount + " Average : " + average + "/" + peoplePL.realAverage() + " Max: " + max + "/" + peoplePL.getActualMax() + " Min : " + peoplePL.getMin());


        //Construct clusters
        count = 0;
        xcount = 0;
        for (int i = clusterPL.getMin(); i <= clusterPL.getActualMax(); i++) {
            for (int j = 0; j < clusterPL.getNum(i); j++) {
                assignedClusters.put(count, new Cluster(count));
                assignedClusters.get(count).assignSize((short)i);
                cCount.put(count, i);
                count++;
                xcount += i;
            }
        }

        System.out.println("No. of Clusters: " + count + " Allowed People: " + xcount + " Max Size: " + clusterPL.getActualMax() + " Min : " + clusterPL.getMin() + " Average : " + clusterPL.realAverage());


//        System.out.println("Adding people into clusters...");
        //Add people into Clusters
        count = 0; //No. of homed people

        ArrayList<BMPerson> homelessPeople = new ArrayList<BMPerson>(((HashMap) people.clone()).values());


        while (homelessPeople.size() > 0) {

            BMPerson randPerson = homelessPeople.get(rng.nextInt(homelessPeople.size()));

            Cluster randCluster = assignedClusters.get(rng.nextInt(assignedClusters.size()));

            if (randCluster.getAssignedSize() > (int) Math.round(randPerson.getAssignedSize() * (1 - mu))) {
                if (randCluster.isFull()) {
                    homelessPeople.add((BMPerson) randCluster.kickRandomMember());
                //System.out.println(homelessPeople.degree());
                }
                randCluster.addMember(randPerson);
                randPerson.assignC(randCluster.getID());
                homelessPeople.remove(randPerson);
            }

//            if (homelessPeople.degree()%1000 ==0) {
//                System.out.println(homelessPeople.degree() + "more to add.");
//            }

        }

//        System.out.println("Adding links within clusters...");

        //Add links 

        for (int i = 0; i < assignedClusters.size(); i++) {
            Cluster currentC = assignedClusters.get(i);

            int[] cumulativePDF = new int[currentC.getActualsize()];

            // Compute cumulative probability distribution.

            ArrayList<Person> mem = new ArrayList<Person>(currentC.getMembers());

            HashSet<Person> hungryMem = new HashSet<Person>(currentC.getMembers());

            cumulativePDF[0] = ((BMPerson)mem.get(0)).getAssignedSize();

            for (int j = 1; j < currentC.getActualsize(); j++) {
                cumulativePDF[j] = cumulativePDF[j - 1] + ((BMPerson)mem.get(j)).getAssignedSize();
            }

//            System.out.println("Processing Cluster " + i + " " + mem.degree());
            //Going thru each member...
            int loopCount = 0;
            while (hungryMem.size() > 0) {


                int size = hungryMem.size();

                Person p = hungryMem.iterator().next();

                int allowedSize = (int) Math.round(((BMPerson)p).getAssignedSize() * (1 - mu));
                //System.out.println(p.getID() + " " + allowedSize);
                while (p.degree() < allowedSize) {

                    int random = rng.nextInt(cumulativePDF[cumulativePDF.length - 1]);

                    //	Select discrete value corresponding
                    //  to matching position in cumulative PDF.

                    Person f = null;
                    for (int j = 0; j < currentC.getActualsize(); j++) {
                        if (random < cumulativePDF[j]) {
                            f = mem.get(j);
                            break;
                        }
                    }


                    // fix hungry mem is adding back f's friend g whose friend degree -1, but may be repetitive
                    if ((f.getID() != p.getID()) && (!f.isFriendOf(p))) {
                        if (f.degree() >= (int) Math.round(((BMPerson)f).getAssignedSize() * (1 - mu))) {
                            hungryMem.add(((BMPerson)f).removeRandomFriend(false));
                        }
                        p.addFriend(f, 1.0);
                        f.addFriend(p, 1.0);
                    //System.out.println("here " + p.degree() + "/" + hungryMem.degree() + " " + p.getID());
                    }

                }

                hungryMem.remove(p);
//                if (hungryMem.degree()%1000 ==0) {
//                System.out.println(hungryMem.degree() + "more to add.");
//            }

                if (hungryMem.size() == size) {
                    loopCount++;
                    //System.out.println(loopCount);
                    if (loopCount == 50) {
                        //System.out.println("good bye");
                        loopCount = 0;
                        break;
                    }
                } else {
                    loopCount = 0;
                }
            }
        }

//        System.out.println("Adding inter community links...");
        int[] cumulativePDF = new int[people.size()];

        ArrayList<Person> pps = new ArrayList<Person>(people.values());

        HashSet<Person> hungryPP = new HashSet<Person>(people.values());

        cumulativePDF[0] = ((BMPerson)pps.get(0)).getAssignedSize();

        int loopCount = 0;

        for (int j = 1; j < pps.size(); j++) {
            cumulativePDF[j] = cumulativePDF[j - 1] + pps.get(j).degree();
        }

        while (hungryPP.size() > 0) {

            int size = hungryPP.size();



            BMPerson p = (BMPerson) hungryPP.iterator().next();

            //System.out.println("here " + p.degree() + "/" + hungryPP.degree());


            while (p.getAssignedSize() > p.degree() && (p.getAssignedSize() != (1 - mu) * p.getAssignedSize())) {

                // System.out.println("here " + p.degree() + "/" + hungryPP.degree());

                BMPerson f = (BMPerson) pps.get(rng.nextInt(pps.size()));


                if ((f.getAssignedC() != p.getAssignedC()) && (!f.isFriendOf(p))) {

                    if ((f.degree() >= f.getAssignedSize()) && (f.hasOutsider())) {
                        hungryPP.add(f.removeRandomFriend(true));
                        hungryPP.remove(f);
                    }

                    p.addFriend(f, 1.0);
                    f.addFriend(p, 1.0);


                }

            }
            hungryPP.remove(p);

//            if (hungryPP.degree()%1000 ==0) {
//                System.out.println(hungryPP.degree() + "more to add.");
//            }

            if (hungryPP.size() == size) {
                loopCount++;
                //System.out.println(loopCount);
                if (loopCount == 50) {
                    //System.out.println("good bye");
                    loopCount = 0;
                    break;
                }
            } else {
                loopCount = 0;
            }
        }

        count = 0;
        int linkCount = 0;
        for (Person p : people.values()) {
            count++;
            linkCount += p.getFriends().size();
        }
        System.out.println("Final Size: " + count + " Link: " + linkCount);

    }

    public HashMap getOriginalCCount() {
        return cCount;
    }
}
