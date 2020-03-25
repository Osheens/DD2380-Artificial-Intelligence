import java.io.*;
import java.util.Scanner;
import java.util.Collections;
import java.text.DecimalFormat;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;



public class hmm2
{
    public static void main(String[] args)throws Exception {
        Scanner scan = new Scanner(System.in);
        //Loading matrix A
        String[] r1 = scan.nextLine().split("\\s");
        int n_1, m_1;
        n_1 = Integer.parseInt(r1[0]);
        m_1 = Integer.parseInt(r1[1]);

        int update = 2;
        double[][] matrixA = new double[n_1][m_1];
        for (int i = 0; i < n_1; i++) {
            for (int j = 0; j < m_1; j++) {
                matrixA[i][j] = Double.parseDouble(r1[update]);
                update = update + 1;
            }
        }

        //Loading matrix B
        String[] r2 = scan.nextLine().split("\\s");
        int n_2, m_2;
        n_2 = Integer.parseInt(r2[0]);
        m_2 = Integer.parseInt(r2[1]);

        update = 2;

        double[][] matrixB = new double[n_2][m_2];
        for (int i = 0; i < n_2; i++) {
            for (int j = 0; j < m_2; j++) {
                matrixB[i][j] = Double.parseDouble(r2[update]);
                update = update + 1;
            }
        }

        //Loading matrix Pi
        String[] r3 = scan.nextLine().split("\\s");
        int n_3, m_3;
        n_3 = Integer.parseInt(r3[0]);
        m_3 = Integer.parseInt(r3[1]);

        update = 2;

        double[][] matrixPi = new double[n_3][m_3];
        for (int i = 0; i < n_3; i++) {
            for (int j = 0; j < m_3; j++) {
                matrixPi[i][j] = Double.parseDouble(r3[update]);
                update = update + 1;
            }
        }

        //Loading Observation Matrix
        String[] r4 = scan.nextLine().split("\\s");
        int n_4;
        n_4 = Integer.parseInt(r4[0]);

        update = 1;

        int[] matrixObs = new int[n_4];
        for (int i = 0; i < n_4; i++) {
            matrixObs[i] = Integer.parseInt(r4[update]);
            update = update + 1;
        }

        // Backtracking
        ComputeDeltas(matrixA, matrixB, matrixPi, matrixObs, n_4, m_3);
    }
    public static void ComputeDeltas(double[][] matrixA, double[][] matrixB, double[][] matrixPi, int[] matrixObs, int n_4, int m_3)
    {
        int[] StatesMaxArg = new int[n_4];
        double[] d1 = new double[m_3];
        double[] d1_sorted = new double[m_3];
        
        for (int i=0; i<m_3; i++) {
            d1[i] = matrixPi[0][i] * matrixB[i][matrixObs[0]];
        }
        
        d1_sorted = d1.clone();
        Arrays.sort(d1_sorted);
            
        int i = 0;
        
        while (i < d1_sorted.length) {
            if (d1[i] == d1_sorted[d1_sorted.length-1]) {
                StatesMaxArg[0] = i;
            break;
        } 
        else { 
            i = i + 1; 
        } 
        }
        
        double[] linewiseVector = new double[m_3];
        double[] linewiseVectorSorted = new double[m_3];
        double[] m_vector = new double[m_3];
        int[][] arg_max = new int[m_3][n_4];
        double[] final_d = new double[m_3];
        int idx = 0;
        

        for (int k=1; k<n_4; k++) 
        {
            for(i=0; i<m_3; i++) 
            {
                for(int j=0; j<m_3; j++) 
                {
                    linewiseVector[j] = d1[j]*matrixA[j][i]*matrixB[i][matrixObs[k]];
                }
                linewiseVectorSorted = linewiseVector.clone();
                Arrays.sort(linewiseVectorSorted);
                double r_max = linewiseVectorSorted[linewiseVectorSorted.length-1];
                m_vector[i] = r_max;
                idx = 0;
                while (idx < linewiseVectorSorted.length)
                {
                    if (linewiseVector[idx] == r_max)
                    { 
                        arg_max[i][k] = idx;
                        break;
                    } 
                    else 
                    { 
                        idx = idx + 1;
                    }
                }
                idx = 0;
            }
            idx =0;
            d1 = m_vector.clone();

        }
      
        //Backtracking
        
        final_d = d1.clone();
        Arrays.sort(final_d);
        idx = 0;

        while (idx < final_d.length)
        {
            if (d1[idx] == final_d[final_d.length-1])
            {
                StatesMaxArg[0] = arg_max[idx][n_4-1];
                break;
            }
            else idx = idx + 1;
        }

        StatesMaxArg[n_4-1] = idx;

        for(int k=(n_4-1); k>0; k--)
        { 
            StatesMaxArg[k-1] = arg_max[StatesMaxArg[k]][k];
        }
        
        //printing the deltas
        String result_1 = new String();
        String result_2 = new String();
        result_2 = "";
        for (i=0; i<n_4; i++)
        {
            result_1 = Integer.toString(StatesMaxArg[i]);
            result_2 = result_2 + " " + result_1;
        }

        System.out.println(result_2);
    }
    
}