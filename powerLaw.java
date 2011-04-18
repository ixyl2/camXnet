/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package camxnet;

/**
 *
 * @author ixyl2
 */

public class powerLaw {

    double C;
    double average;
    double gamma;
    int min;
    int max;
    int N;
    int compensate = 0;
    int actualSum = 0;

    powerLaw(int N, double average, double gamma, int max) {
        this.N = N;
        this.average = average;
        this.gamma = gamma;
        this.max = max;

        fitmin();

        C = N / sum(-gamma);

        for (int i = 0; i <= max; i++) {
            actualSum += getNum(i);
        }

        compensate = N - actualSum;

    //System.out.println("people : C " + C + " " + this.max + " " + min + " " + actualSum);
    }

    powerLaw(int N, double gamma, int max, int min) {
        this.N = N;
        this.min = min;
        this.gamma = gamma;
        this.max = max;

        C = N / sum(1 - gamma);

        while (allowedP() < N) {
            C += (N - allowedP()) / sum(1 - gamma);
        //System.out.println("allowed : " + allowedP() + " " + C);
        }

    //System.out.println("C " + C + " " + this.max + " " + min + "getNum" + getNum(10));
    }

    double sum(double gamma) {

        double sum = 0;

        for (int i = min; i <= max; i++) {
            sum += Math.pow(i, gamma);
        }

        return sum;

    }

    int allowedP() {

        int sum = 0;

        for (int i = min; i <= max; i++) {
            sum += getNum(i) * i;
        }

        return sum;

    }

    double realAverage() {

        return sum(1 - gamma) / sum(-gamma);

    }

    // FIX 
    void fitmin() {
        int minA = 0;
        int minB = max;

        int currentMin = (int) ((minA + minB) / 2);

        while (minB - minA > 1) {




            min = currentMin;


            if (realAverage() < average) {
                minA = currentMin;
            } else {
                minB = currentMin;
            }

            currentMin = (int) ((minA + minB) / 2);


        }

        min = currentMin;
    //System.out.println(min + " A " + minA + " B " + minB + " real average " + realAverage() + " av "+ average);
    }

    int getMin() {
        return min;
    }

    int getActualMax() {
        int aMax = 0;
        for (int i = max; i >= min; i--) {
            if (getNum(i) != 0) {
                aMax = i;
                break;
            }
        }
        return aMax;
    }

    int getNum(int i) {
        if ((i < min) || (i > max)) {
            return 0;
        }
        if (i == min) {
            return (int) Math.round(Math.pow(i, -gamma) * C) + compensate;
        }
        return (int) Math.round(Math.pow(i, -gamma) * C);
    }
}

