/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camxnet;
import java.util.Calendar;
import java.text.SimpleDateFormat;


/**
 *
 * @author ixyl2
 */
public class Utils {

    static double[][] add(double[][] a, double[][] b) {

        double[][] result = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }

        return result;
    }

    static double[][] subtract(double[][] a, double[][] b) {

        double[][] result = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }

        return result;
    }

    static double[][] divide(double[][] a, double d) {

        double[][] result = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] / d;
            }
        }

        return result;
    }

    static double[][] divide(double[][] a, int d) {

        double[][] result = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] / (double) d;
            }
        }

        return result;
    }

    static double[][] multiply(double[][] a, double m) {

        double[][] result = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] * (double) m;
            }
        }

        return result;
    }

    static double[][] multiply(double[][] a, int m) {

        double[][] result = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] * (double) m;
            }
        }

        return result;
    }

    public static String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());

    }

}
