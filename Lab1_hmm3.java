import java.io.*;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.lang.reflect.Array;


public class hmm3
{
    public static void main(String[] args)throws Exception
    { 
        
                Scanner scan = new Scanner(System.in);
                //matrix a
                String[] r1 = scan.nextLine().split("\\s");
                int N_A,M_A;
                N_A = Integer.parseInt(r1[0]);
                M_A = Integer.parseInt(r1[1]);
                ArrayList<double[]> a = new ArrayList<double[]>();
                
                int count=2;
                double[][] Matrix_A= new double[N_A][M_A];
                for(int i=0; i<N_A; i++)
                {
                    for (int j=0; j<M_A; j++)
                    {
                        Matrix_A[i][j] = Double.parseDouble(r1[count]);
                        count=count+1;
                    }
                }
                for(int i = 0; i < Matrix_A.length; i++)
                {
                    a.add(Matrix_A[i]);
                }
                //matrix b
                String[] r2 = scan.nextLine().split("\\s");
                int N_B,M_B;
                N_B = Integer.parseInt(r2[0]);
                M_B = Integer.parseInt(r2[1]);
                ArrayList<double[]> b = new ArrayList<double[]>();
                
                count = 2;
            
                double[][] Matrix_B= new double[N_B][M_B];
                for(int i=0; i<N_B; i++)
                {
                    for (int j=0; j<M_B; j++)
                    {
                        Matrix_B[i][j] = Double.parseDouble(r2[count]);
                        count=count+1;
                        }
                    }
                for(int i = 0; i < Matrix_B.length; i++)
                {
                    b.add(Matrix_B[i]);
                }
                //vector p
                String[] r3 = scan.nextLine().split("\\s"); 
                int N_PI;
                N_PI = Integer.parseInt(r3[1]);
                ArrayList<double[]> p = new ArrayList<double[]>();
                count = 2;
                
                double[] Vector_PI= new double[N_PI];
                for(int i=0; i<N_PI; i++)
                {
                        Vector_PI[i] = Double.parseDouble(r3[count]);
                        count=count+1;
        
                }
                
                    //vector sequence
                    String[] r4 = scan.nextLine().split("\\s");  
                    int N_Obs;
                    N_Obs = Integer.parseInt(r4[0]);
                    
                    count = 1;
                    
                    int[] Obs_Seq= new int[N_Obs];
                    for(int i=0; i<N_Obs; i++)
                    {
                        Obs_Seq[i] = Integer.parseInt(r4[count]);
                        
                        count=count+1;
                
                        
                    }
      
        
        
                double[][] alpha1 = new double[N_Obs][N_PI];
                double[][] alpha = new double[N_Obs][N_PI];
                double[] Scale = new double[N_Obs];
                
                double[][] beta1= new double[N_Obs][N_PI];
                double[][] beta= new double[N_Obs][N_PI];

                
                double[][][] gamma1 = new double[N_Obs][M_A][N_A];
                double[][] gamma = new double[N_Obs][N_A];
                
                double[] PI_Estimate= new double[N_PI];
                double[][] MatrixA_Estimate= new double[N_A][M_A];
                double[][] MatrixB_Estimate= new double[N_B][M_B];
                double numerator,denominator;
                
                double logProb=0.0;
                int max_iter = 500;
                int repetition = 0;
                double old_log_prob = -1000000.0;
        
                while(true)       
                {
                    Scale[0] = 0.0;
                    for (int i=0; i<N_PI; i++) 
                    {
                        alpha1[0][i] = Vector_PI[i] * Matrix_B[i][Obs_Seq[0]];
                        Scale[0] += alpha1[0][i];
                        
                    }
                     //scaling alpha
                    Scale[0] = 1.0/Scale[0];
                    for (int i=0; i<N_PI; i++)
                     {
                       alpha1[0][i] = Scale[0]*alpha1[0][i];
                     }
                
                    //alpha[k][i] computation
                    
                    for (int k=1; k<N_Obs; k++) 
                    {
                        Scale[k]=0;
                        for (int i=0; i<N_PI; i++) 
                        {
                            alpha1[k][i] = 0.0;
                            for (int j=0; j<N_PI; j++) 
                            {
                                alpha1[k][i] = alpha1[k][i] + (alpha1[k-1][j]*Matrix_A[j][i]);
                            }
                            alpha1[k][i] = alpha1[k][i] * Matrix_B[i][Obs_Seq[k]];
                            Scale[k] = Scale[k] + alpha1[k][i];
                            
                        }
                        Scale[k] = 1.0/Scale[k];
                        for (int i=0; i<N_PI; i++) 
                        {
                            alpha1[k][i] = Scale[k]*alpha1[k][i];
                        }  
                        
                   }
                   alpha=alpha1.clone();
         
                     //BACKWARD OPERATOR beta pass
                        
                        
                        //scale beta
                        for (int i=0; i<N_A; i++) 
                        {
                            beta1[N_Obs-1][i] = Scale[N_Obs-1];
                        }
                        
                        //beta pass
                        for (int t=N_Obs-2; t>=0; t--)
                        {
                            for (int i=0; i<N_PI; i++) 
                            {
                                beta1[t][i] = 0.0;
                                for (int j=0; j<N_PI; j++) 
                                {                            
                                    //updates
                                    beta1[t][i] = beta1[t][i] + (Matrix_A[i][j]*Matrix_B[j][Obs_Seq[t+1]]*beta1[t+1][j]);
                                }
                                beta1[t][i] = Scale[t]*beta1[t][i];
                            }
                        }
                        beta=beta1.clone();
                         
                         
                         //Gamma pass
                         
                         for(int t=0;t<N_Obs-1;t++)  
                         {
                              denominator = 0.0;
                             for (int i=0; i<N_PI; i++) 
                                 {
                                 for (int j=0; j<N_PI; j++) 
                                     {
                                       denominator = denominator + alpha[t][i]*Matrix_A[i][j]*Matrix_B[j][Obs_Seq[t+1]]*beta[t+1][j]; 
                                     }
                                 }
                             for(int i=0; i<N_PI;i++)
                             {
                                 gamma[t][i]=0.0;
                                 for(int j=0; j<N_PI; j++)
                                 {
                                     gamma1[t][i][j] = (alpha[t][i]*Matrix_A[i][j]*Matrix_B[j][Obs_Seq[t+1]]*beta[t+1][j])/denominator;  
                                     gamma[t][i]+=gamma1[t][i][j]; 
                                 }
                             }
                                
                                    
                                 
                         
                        }
                        
                    
                     //Restimate pi
                    for (int i=0; i<N_PI; i++)
                        {
                           PI_Estimate[i] = gamma[0][i];
                        }
                    //Restimate A
                    for (int i=0; i<N_PI; i++)
                       {
                            
                        for(int j=0; j<N_PI; j++)
                        {
                            numerator =0.0;
                            denominator = 0.0;
                            for(int t=0; t<N_Obs-1; t++)
                            {
                                numerator = numerator + gamma1[t][i][j];
                                denominator += gamma[t][i];
                            }
                                  
                            MatrixA_Estimate[i][j] = numerator/denominator;
                                
                        }
                    }
                    
                    //Restimate B
                    for (int i=0; i<N_PI; i++)
                    {
                        for(int j=0; j<M_B; j++)
                        {
                            numerator = 0.0;
                            denominator = 0.0;
                            for(int t=0; t<N_Obs-1; t++)
                            {
                                if(Obs_Seq[t]==j)
                                {
                                    numerator += gamma[t][i];
                                }
                                denominator = denominator + gamma[t][i];
                            }             
                            MatrixB_Estimate[i][j] = numerator/denominator;                        
                        }
                    }
                    
                    for(int i=0; i<N_PI; i++)
                    {
                        Vector_PI[i]= gamma[0][i];
                     
                    }
                     
                    Matrix_A = MatrixA_Estimate;
                    Matrix_B = MatrixB_Estimate;
            
                    //Compute log
                    
                    logProb = 0.0;
                    for (int i=0; i<N_Obs; i++)
                        {
                            logProb += (Math.log(Scale[i])/Math.log(10));
                   
                        }
                    logProb = -logProb;
                    repetition++;
                    
                    if((repetition<max_iter)&&(logProb>old_log_prob))
                    {
                        old_log_prob = logProb;
                    }
                    else
                    { 
                        break;
                    }
                    
        }
        
        String transition_matrix = N_A+" "+M_A;
        String emmision_matrix = N_B+" "+M_B;
        String Y = "";
        String Z = "";
            for (int i=0; i<N_A; i++) 
                {
                    for(int j=0; j<M_A; j++)
                        {
                            Y = Double.toString(Math.round(Matrix_A[i][j] * 1000000.0) / 1000000.0);
                            transition_matrix  = transition_matrix  +" "+ Y+" ";                           
                        }
                    
                }
                System.out.println(transition_matrix);
                
            for (int i=0; i<N_B; i++) 
            {
                for(int j=0; j<M_B; j++)
                    {
                        Z = Double.toString(Math.round(Matrix_B[i][j] * 1000000.0) / 1000000.0);
                        emmision_matrix  = emmision_matrix  +" "+ Z+" ";                           
                    }
                
            }
            System.out.println(emmision_matrix);
                
    }
}

    
   
             