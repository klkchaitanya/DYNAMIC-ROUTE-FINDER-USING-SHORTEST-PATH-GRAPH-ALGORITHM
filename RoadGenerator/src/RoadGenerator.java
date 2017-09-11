import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class RoadGenerator 
{
   
   public static void main(String args[])
   {
	   int numJunctions; 
	   Scanner sc=new Scanner(System.in);
	   System.out.println("Enter number of junctions:\n");
	   int numJuncs=sc.nextInt();	   
	   generateNetwork(numJuncs);
	   
       
       
   }
   
   public static void generateNetwork(int numJuncs)
   {
	   int hmJuncs = (int)(0.1 * numJuncs);
       int mmJuncs = (int)(0.2 * numJuncs);
       int mlJuncs = (int)(0.3 * numJuncs);
       int llJuncs = (int)(0.4 * numJuncs);
       int numHSegments=0,numMSegments=0,numLSegments=0;
       int low=30,medium=50,high=70;
       int llstart=9,llsteps=12;
       
       System.out.println(hmJuncs+" "+mmJuncs+" "+mlJuncs+" "+llJuncs);
       
       //High-Medium Array
       ArrayList<String> hmJuncsArray=new ArrayList<String>();
       for (int i = 0; i < hmJuncs; i++)
       {
           hmJuncsArray.add("HM"+(i+1));
       }
       
       //Medium-Medium Array
       ArrayList<String> mmJuncsArray=new ArrayList<String>();
       for (int i = 0; i < mmJuncs; i++)
       {
           mmJuncsArray.add("MM"+(i+1));
       }
       
       //Medium-Low Array
       ArrayList<String> mlJuncsArray=new ArrayList<String>();
       for (int i = 0; i < mlJuncs; i++)
       {
           mlJuncsArray.add("ML"+(i+1));
       }
       
       //Low-Low Array
       ArrayList<String> llJuncsArray=new ArrayList<String>();       
       for (int i = 0; i < llJuncs; i++)
       {
           llJuncsArray.add("LL"+(i+1));
       }
       
       
       
       PrintWriter writer;
	try {
		writer = new PrintWriter("RoadGenerator.dot", "UTF-8");
        writer.println("graph xmpl"+"\n"
                +"{"+"\n"
                + "rankdir=\"LR\";"+"\n"
                + "ordering=out"+"\n"
//                +"node[shape=round fillcolor="+"'green'"+"]"+"\n");
                + "node[shape=circle]" + "\n");
        
        //Y-Axis First High Speed Road
        for(int i=0;i<hmJuncs-1;i=i+1)
        {
            writer.append(
            "{rank=\"same\";"+hmJuncsArray.get(i)+"--"+hmJuncsArray.get(i+1)+"[label=\"HM"+(i+1)+"HM"+(i+2)+"\" weight="+high+" color=\"red\"]};"+"\n");
           numHSegments=numHSegments+1;
        }
        
        
        //Y-Axis Last Two Medium Speed Roads
        int mmJuncs1 = mmJuncs/2;
        int mmJuncs2 = mmJuncs-mmJuncs1;
        for (int i = 0; i < mmJuncs1 - 1; i = i + 1)
        {
        	writer.append(
            "{rank=\"same\";" + mmJuncsArray.get(i) + "--" + mmJuncsArray.get(i + 1) + "[label=\"MM"+(i+1)+"MM"+(i+2)+"\" weight="+medium+" color=\"orange\"]};" + "\n");
        	numMSegments++;

        }
        for (int i = mmJuncs1 ; i < mmJuncs - 1; i = i + 1)
        {
            writer.append(
                    "{rank=\"same\";" + mmJuncsArray.get(i) + "--" + mmJuncsArray.get(i + 1) +  "[label=\"MM"+(i+1)+"MM"+(i+2)+"\" weight="+medium+" color=\"orange\"]};" + "\n");
            numMSegments++;
        }
        
        
      //X-Axis Medium Speed Generation
        int i=0;
        for (int j = 1; j < hmJuncs; j = j + 1)
            {
                if(i < mlJuncs - 3) 
                
              {
                writer.append(hmJuncsArray.get(j) + "--" + mlJuncsArray.get(i)+ "[label=\"HM"+(j+1)+"ML"+(i+1)+"\" weight="+medium+" color=\"orange\"]" + "\n");
                writer.append(mlJuncsArray.get(i) + "--" + mlJuncsArray.get(i+1)+ "[label=\"ML"+(i+1)+"ML"+(i+2)+"\" weight="+medium+" color=\"orange\"]" + "\n");
                writer.append(mlJuncsArray.get(i+1) + "--" + mlJuncsArray.get(i+2)+ "[label=\"ML"+(i+2)+"ML"+(i+3)+"\" weight="+medium+" color=\"orange\"]" + "\n");
                writer.append(mlJuncsArray.get(i+2) + "--" + mlJuncsArray.get(i+3)+ "[label=\"ML"+(i+3)+"ML"+(i+4)+"\" weight="+medium+" color=\"orange\"]" + "\n");
                writer.append(mlJuncsArray.get(i+3) + "--" + mmJuncsArray.get(j)+ "[label=\"ML"+(i+4)+"MM"+(j+1)+"\" weight="+medium+" color=\"orange\"]"+ "\n");
                writer.append(mmJuncsArray.get(j) + "--" +mmJuncsArray.get(j+mmJuncs1)+ "[label=\"MM"+(j+1)+"MM"+(j+mmJuncs1+1)+"\" weight="+medium+" color=\"orange\"]"+ "\n");
                numMSegments=numMSegments+6;
              }
                i = i + 4;

           }

        //Low-Low Juncs Generation
        i = 0;
        for (int j = 0; j < mlJuncs-4 ; j = j + 1)
        {
           if(i < llJuncs-3)
            {
            
        	   writer.append("{rank=\"same\";" + mlJuncsArray.get(j) + "--" + llJuncsArray.get(i)+ "[label=\"ML"+(j+1)+"LL"+(i+1)+"\" weight="+low+" color=\"green\"]};" + "\n");
        	   writer.append("{rank=\"same\";" + llJuncsArray.get(i) + "--" + llJuncsArray.get(i+1)+ "[label=\"LL"+(i+1)+"LL"+(i+2)+"\" weight="+low+" color=\"green\"]};" + "\n");
        	   writer.append("{rank=\"same\";" + llJuncsArray.get(i+1) + "--" + llJuncsArray.get(i+2)+ "[label=\"LL"+(i+2)+"LL"+(i+3)+"\" weight="+low+" color=\"green\"]};" + "\n");
                // sm.WriteLine(squareJunctions[i] + "--" + squareJunctions[i + 1] + "\n");
        	   writer.append("{rank=\"same\";" + llJuncsArray.get(i+2) + "--" + mlJuncsArray.get(j+4)+ "[label=\"LL"+(i+3)+"ML"+(j+4+1)+"\" weight="+low+" color=\"green\"]};" + "\n");
        	   numLSegments=numLSegments+4;
            }
           i = i + 3;
        }
        
        
        for(i=0;i<llJuncs-3;i++)
        {
        	if(i==(llstart+(0*llsteps))||i==(llstart+(1*llsteps))||i==(llstart+(2*llsteps))||i==(llstart+(3*llsteps))||i==(llstart+(4*llsteps)))
     	    	i=i+3;
        	writer.append( llJuncsArray.get(i) + "--" + llJuncsArray.get(i+3)+ "[label=\"LL"+(i+1)+"LL"+(i+3+1)+"\" weight="+low+" color=\"green\"]" + "\n");
     	   numLSegments=numLSegments+1;
        }
        
        writer.append("}");
        
        writer.close();
        
        System.out.println("H:"+numHSegments+" "+"M:"+numMSegments+" "+"L:"+numLSegments);
        System.out.println("Total Segments:"+(numHSegments+numMSegments+numLSegments));
		} 
		catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} 	
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	   }
	}
