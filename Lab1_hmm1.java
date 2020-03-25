import java.io.*;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class hmm1 
{
    public static void main(String[] args)throws Exception
    {
        Scanner scan = new Scanner(System.in);
        //Load matrix A
        String[] r1 = scan.nextLine().split("\\s");
        
        int N_A,M_A;
        N_A = Integer.parseInt(r1[0]);
        M_A = Integer.parseInt(r1[1]);
        
        
        int update=2;
        double[][] matrixA= new double[N_A][M_A];
        for(int i=0; i<N_A; i++)
        {
            for (int j=0; j<M_A; j++)
            {
               
                matrixA[i][j] = Double.parseDouble(r1[update]);
                
                update=update+1;
            }
        }
        
        //Load matrix B
        String[] r2 = scan.nextLine().split("\\s");
        int N_B,M_B;
        N_B = Integer.parseInt(r2[0]);
        M_B = Integer.parseInt(r2[1]);
        
        update = 2;
    
        double[][] matrixB= new double[N_B][M_B];
        for(int i=0; i<N_B; i++)
        {
            for (int j=0; j<M_B; j++)
            {
                matrixB[i][j] = Double.parseDouble(r2[update]);
                update=update+1;
                }
        }
        
        //Load matrix Pi
        String[] r3 = scan.nextLine().split("\\s"); 
        int N_PI,M_PI;
        N_PI = Integer.parseInt(r3[0]);
        M_PI = Integer.parseInt(r3[1]);
        
        update = 2;
        
        double[][] matrixPi= new double[N_PI][M_PI];
        for(int i=0; i<N_PI; i++)
        {
            for (int j=0; j<M_PI; j++)
            {
                matrixPi[i][j] = Double.parseDouble(r3[update]);
                update=update+1;
            }
        }
        
        //Load sequence of Observations
        String[] r4 = scan.nextLine().split("\\s");  
        int N_Obs;
        N_Obs = Integer.parseInt(r4[0]);
        
        update = 1;
        
        int[] matrixObs= new int[N_Obs];
        for(int i=0; i<N_Obs; i++)
        {
            matrixObs[i] = Integer.parseInt(r4[update]);
            update=update+1;
        }
                
        ComputeAlpha(matrixA, matrixB, matrixPi, matrixObs, M_PI, N_Obs);
       
    }
    
    public static void ComputeAlpha(double[][] matrixA, double[][] matrixB,double[][] matrixPi,int[] matrixObs, int M_PI, int N_Obs)
    {
        //Computing alpha1
        double[] alpha1 = new double[M_PI];
        for (int i=0; i<M_PI; i++) 
        {
            alpha1[i] = matrixPi[0][i] * matrixB[i][matrixObs[0]];
        }
        
        //Computing alpha2
        double[] alpha2 = new double[M_PI];
        double tot = 0;
        
        for (int k=1; k<N_Obs; k++) 
        {
            for (int i=0; i<M_PI; i++) 
            {
                for (int j=0; j<M_PI; j++) 
                {
                    tot = tot + alpha1[j]*matrixA[j][i];
                }
                alpha2 [i] = tot * matrixB[i][matrixObs[k]];
                tot = 0;
            }
            alpha1 = alpha2.clone();
        }
        
        PrintResult(M_PI, alpha2);
    }
    
    public static void PrintResult(int M_PI, double[] alpha2)
    {
        String printout = new String();
        double totalSum = 0;
        for (int i=0; i<M_PI; i++) 
        {
            totalSum = totalSum + alpha2[i];
        }
        printout = Double.toString(Math.round(totalSum * 1000000.0) / 1000000.0);
        System.out.println(printout);
    }
}
    
