import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.lang.reflect.Array;
    
public class hmm0
{
    public static void main(String[] args)throws Exception
    {
        Scanner scan = new Scanner(System.in);

        //matrix a
        String[] r1 = scan.nextLine().split("\\s");

        int n_1,m_1;
        n_1 = Integer.parseInt(r1[0]);

        m_1 = Integer.parseInt(r1[1]);

        ArrayList<double[]> a = new ArrayList<double[]>();

        int update=2;
            double[][] matrixA = new double[n_1][m_1];
            for(int i=0; i<n_1; i++)
            {
                for (int j=0; j<m_1; j++)
                {
                    matrixA[i][j] = Double.parseDouble(r1[update]);
                    update=update+1;
                }
            }
            for(int i = 0; i < matrixA.length; i++){
                a.add(matrixA[i]);
               }

        //matrix b
        String[] r2 = scan.nextLine().split("\\s");
        int n_2,m_2;
        n_2 = Integer.parseInt(r2[0]);
        m_2 = Integer.parseInt(r2[1]);


        ArrayList<double[]> b = new ArrayList<double[]>();
        update = 2;
            double[][] matrixB = new double[n_2][m_2];
            for(int i=0; i<n_2; i++)
            {
                for (int j=0; j<m_2; j++)
                {
                    matrixB[i][j] = Double.parseDouble(r2[update]);
                    update=update+1;
                }
            }
            for(int i = 0; i < matrixB.length; i++){

                b.add(matrixB[i]);
               }


        //vector p
        String[] r3 = scan.nextLine().split("\\s");

        int n_3,m_3;
        n_3 = Integer.parseInt(r3[0]);
        m_3 = Integer.parseInt(r3[1]);
        ArrayList<double[]> p = new ArrayList<double[]>();
        update = 2;
            double[][] matrixPi = new double[n_3][m_3];
            for(int i=0; i<n_3; i++)
            {
                for (int j=0; j<m_3; j++)
                {
                    matrixPi[i][j] = Double.parseDouble(r3[update]);
                    update=update+1;

                }
            }
            for(int i = 0; i < matrixPi.length; i++){

                p.add(matrixPi[i]);
               }


        //multiplication p and a matrix
        double[][] pa = new double[p.size()][a.get(0).length];
        pa =ProductOfMatrices(ConvertArrayListToDouble(p),ConvertArrayListToDouble(a));

        //multiplication p, a and b
        double[][] pab = new double[pa.length][b.get(0).length];
        pab =ProductOfMatrices(pa,ConvertArrayListToDouble(b));
        String result = new String();
        String result_2 = Integer.toString(pab.length) +" "+ Integer.toString(pab[0].length);
        for (int i=0; i<(pab.length); i++) {
            for (int j=0; j<(pab[0].length); j++) {
            result = Double.toString(Math.round(pab[i][j] * 100.0) / 100.0);
            result_2 = result_2 + " " + result;

        }

        System.out.println(result_2);

}
}
//Compute the Product of Matrices in this method
private static double[][]ProductOfMatrices(double[][] mult1, double[][] mult2)
{
    double[][] product = new double[mult1.length][mult2[0].length];
    //multiplication of the matrix
    for (int i = 0; i < mult1.length; i++) {
        for (int j = 0; j < mult2[0].length; j++) {
            product[i][j] = 0;
            for (int k = 0; k < mult1[0].length; k++) {
                product[i][j] += mult1[i][k] * mult2[k][j];
            }

        }
    }
    return product;
}

//Parse arrayLists to arrays of double
private static double[][]ConvertArrayListToDouble(ArrayList<double[]> input)
{
    double[][] answer = new double[input.size()][input.get(0).length];
    for (int i =0; i< input.size();i++){
        for (int j = 0; j< input.get(0).length;j++){
            answer[i][j] = input.get(i)[j];
        }
    }
    return answer;
}
}