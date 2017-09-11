


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import org.gephi.algorithms.shortestpath.BellmanFordShortestPathAlgorithm;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.ElementListener;

public class GraphImporter 
{
	static int distLow=1,distMedium=2,distHigh=3;
	static int speedLow=30,speedMedium=50,speedHigh=70;
	static BufferedWriter bw;
	static ArrayList<String> bestPath;
	static ArrayList<String> prevPath;
	static ArrayList<String> finishedNodes;
	static Edge segment;
	static Node startNode;
	static Node endNode;
	static long startTime;
	static long endTime;
	static double segmentTime;
	static double estimatedTime;
	
	public static void main(String args[])
	{
		   
	       bestPath=null;
	       finishedNodes=new ArrayList<String>();
		
		   ArrayList<String> roadSegments=new ArrayList<String>();
		   System.out.println("reached"); 
		   ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		   pc.newProject();
		   Workspace workspace = pc.getCurrentWorkspace();
		   
		   ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		   org.gephi.io.importer.api.Container container;
		   try 
		   {
			    bw = null;
				FileWriter fw = null;
		        File file1=new File("data50.txt");
		        if (!file1.exists()) {
					file1.createNewFile();
				}
		        
		       
			  
			   File file=new File("RoadGenerator_502.dot");
			   container = importController.importFile(file);
			   container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);
			   ((ContainerLoader) container).setAllowAutoNode(false);			   
			   importController.process(container,new DefaultProcessor(),workspace);			   
			   GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
			   UndirectedGraph udg=graphModel.getUndirectedGraph();
			   System.out.println("Nodes:"+udg.getNodeCount()+" "+"edges:"+udg.getEdgeCount());
			   
			   
//-------------READING EDGES FROM DOT FILE INTO AN ARRAY LIST-------------------------------------			   
			   for(Edge e:udg.getEdges())
			   {
				  roadSegments.add(e.getLabel());				  
			   }
//-------------END OF READING EDGES FROM DOT FILE INTO AN ARRAY LIST-------------------------------------			   

			   
//-------------ASKING USER FOR SOURCE AND DESTINATION-------------------------------------------------
			   String src,dest;
			   Scanner sc=new Scanner(System.in);
			   System.out.println("Enter Source:");
			   src=sc.nextLine();
			   System.out.println("Enter Destination:");
			   dest=sc.nextLine();
			   startTime = System.nanoTime();
			   //System.out.println(src+" "+dest);
			   int count_while=0;
			   
//----------------LOOP WHILE BEST-PATH SIZE>0 ---------------------------------------------------------			   
			   while(bestPath==null||bestPath.size()>0)
			   {
				    count_while++;
				    fw = new FileWriter(file1.getAbsoluteFile(), true);
					bw = new BufferedWriter(fw);
				    System.out.println("\n"+"USER IS NEAR TO "+src);
				    bw.write("\n"+"USER IS NEAR TO "+src);
			  
			   for(Edge e:udg.getEdges())
			   {
				   int dis=0;
				   if(e.getWeight()==speedHigh||e.getWeight()==speedHigh-1)dis=distHigh;
				   else if(e.getWeight()==speedMedium||e.getWeight()==speedMedium-2)dis=distMedium;
				   else if(e.getWeight()==speedLow)dis=distLow;
				   				
			   }
			   
//-----------FINDING PATHS BEFORE REDUCING SPEEDS--------------------------------------------------
			   if(count_while==1)
			   AllPaths(udg,src,dest,0.0);			  			   
//------------END OF FINDING PATHS BEFORE REDUCING SPEEDS---------------------------------------------------------

			   
//-------------RANDOMLY REDUCING SPEEDS OF SOME SEGMENTS--------------------------------------
			   int jamSegments=(udg.getEdgeCount()/2);
			   ArrayList<String> jamSegs=new ArrayList<String>();
			   for(int i=0;i<jamSegments;i++)
			   {
				   int r=ThreadLocalRandom.current().nextInt(0,roadSegments.size());			   
				   //System.out.println(roadSegments.get(r));
				   if(!jamSegs.contains(roadSegments.get(r))&&(!bestPath.contains(roadSegments.get(r))))
				    jamSegs.add(roadSegments.get(r));
				   else 
					   i--;
			   }
			   
			   for(int i=0;i<jamSegments;i++)
			   {
				   for(Edge e:udg.getEdges())
				   {
					   if(e.getLabel().equals(jamSegs.get(i)))
					   {
						   e.setWeight((int)e.getWeight()/3);
					//--	   System.out.println(e.getLabel()+" - "+e.getWeight());
					//--	   bw.write("\n"+e.getLabel()+" - "+e.getWeight());
					   }
				   }
			   }
//-----------------------END OF RANDOMLY REDUCING THE SPEEDS------------------------------------------
			   
			 
//----------------------REDUCING SPEED OF ONE SEGMENT OF CURRENT BEST PATH----------------------------
			  
			   prevPath=new ArrayList<String>();
			   if(bestPath.size()>=2){
			   int r1=ThreadLocalRandom.current().nextInt(0,bestPath.size()-1);  
			   System.out.println("PREVOIUS BEST PATH:"+ bestPath+" "+estimatedTime);
			   prevPath=bestPath;
			   String node1=bestPath.get(r1);
			   String node2=bestPath.get(r1+1);
			   segment=udg.getEdge(udg.getNode(node1), udg.getNode(node2));
			   System.out.println("REDUCING PREVIOUS PATH SEGMENT SPEED: "+"("+node1+" - "+node2+")"+" - "+segment.getLabel());
			   startNode=udg.getNode(node1);
			   endNode=udg.getNode(node2);					   
			   if(segment.getWeight()>=30)
			   segment.setWeight((int)segment.getWeight()/3);
			   
			   }
//-----------------------END OF REDUCING SPEED OF ONE SEGMENT OF CURRENT BEST PATH---------------------
			 			   
			   
			 //GRAPH STATUS AFTER REDUCING SPEED------------------------------------------------------
			 //--  bw.write("\n"+"AFTER REDUCING SPEED");
			 //--  System.out.print("\nSEGMENT"+" - "+"DISTANCE"+" - "+"SPEED"+" - "+"ETIME\n");
			 //--  bw.write("\n"+"\n"+"SEGMENT"+" - "+"DISTANCE"+" - "+"SPEED"+" - "+"ETIME");
			   for(Edge e:udg.getEdges())
			   {
				   int dis=0;
				   if(e.getWeight()==speedHigh||e.getWeight()==(int)speedHigh/3)dis=distHigh;
				   else if(e.getWeight()==speedMedium||e.getWeight()==(int)speedMedium/3)dis=distMedium;
				   else if(e.getWeight()==speedLow||e.getWeight()==(int)speedLow/3)dis=distLow;
				   
			 //--	   System.out.println(e.getLabel()+" - "+dis+" - "+e.getWeight()+" - "+(double)dis/e.getWeight());
			 //--	   bw.write("\n"+e.getLabel()+" - "+dis+" - "+e.getWeight()+" - "+(double)dis/e.getWeight());
			   }
			 
			   
//-------------------------FINDING BEST PATH AFTER REDUCING THE SPEEDS-----------------------------------
			   AllPaths(udg,src,dest,0.0);			   			   
//-------------------------END OF FINDING BEST PATH AFTER REDUCING SPEED---------------------------------------------------------

			   
//-------------------------FINDING 'BY HOW MUCH PREV BEST PATH SEGMENT SPEED SHD BE TO EQUAL CURRENT BEST----------------			   
			   double prevPathNewEtt=0;
			   double segmentEtt=0;
			   //System.out.println(prevPath);
			   for(int i=0;i<prevPath.size()-1;i=i+1)
			   {
				  double wei=udg.getEdge(udg.getNode(prevPath.get(i)), udg.getNode(prevPath.get(i+1))).getWeight();
				  if(wei==speedHigh||wei==(int)speedHigh/3||wei==speedHigh-1)
	        		{
					  segmentEtt=distHigh/wei;
	        		}
	        		else if(wei==speedMedium||wei==(int)speedMedium/3||wei==speedMedium-2)
	        		{
	        			segmentEtt=distMedium/wei;
	        		}
	        		else if(wei==speedLow||wei==(int)speedLow/3)
	        		{
	        			segmentEtt=distLow/wei;
	        		}
				  //System.out.println(wei);
				  prevPathNewEtt=prevPathNewEtt+segmentEtt;
			   }
			   
			   System.out.println("---AFTER RANDOM JAMS---");
			   System.out.println("PREV BEST PATH'S NEW ETT: "+prevPath+" "+prevPathNewEtt);
			   System.out.println("NEW BEST PATH: "+bestPath+" "+estimatedTime);
			   double saveTime=prevPathNewEtt-estimatedTime;
			   if(saveTime==0)
				   System.out.println("No need of speed increase for previous path");
			   else
			   {
				   double reducedSegmentSpeed=udg.getEdge(startNode, endNode).getWeight();
				   int dist=0;
				   //System.out.println(reducedSegmentSpeed);
				   if(reducedSegmentSpeed==(int)speedHigh/3)
				   {  segmentTime=distHigh/reducedSegmentSpeed;
				   	  dist=distHigh;	
				   }
				   else if(reducedSegmentSpeed==(int)speedMedium/3)
				   {   segmentTime=distMedium/reducedSegmentSpeed;
				   	   dist=distMedium;
				   }
				   else if(reducedSegmentSpeed==(int)speedLow/3)
				   {   segmentTime=distLow/reducedSegmentSpeed;
				   	   dist=distLow;
				   }
				   
				  // System.out.println(saveTime);
				   double reqTimeOnSegment=segmentTime-saveTime;
				   if(reqTimeOnSegment>0)
				   {
				  // System.out.println(reqTimeOnSegment);
				   double reqSpeedOnSegment=dist/reqTimeOnSegment;
				   
				   //double remainPrevTime=prevPathNewEtt-segmentTime;
				   //double reqSpeedOnPrev=(dist/(estimatedTime-remainPrevTime));
				   System.out.println("PREV PATHS SEGMENT ("+startNode.getLabel()+"--"+endNode.getLabel()+") should increase"
				   		+ " its speed to "+reqSpeedOnSegment+ " kmph to match the new best path");
				   }
				   else
				   {
					   System.out.println("Not possible to get new ETT with increasing speed of old path segment");
				   }
			   }
//------------END OF FINDING 'BY HOW MUCH PREV BEST PATH SEGMENT SPEED SHD BE TO EQUAL CURRENT BEST----------------			   

			   
//-----------RESTORING SPEEDS OF JAM SEGMENTS------------------------------------------------------------			   
			   int numSegments=bestPath.size()-1;			   
			   for(int i=0;i<jamSegments;i++)
			   {
				   for(Edge e:udg.getEdges())
				   {
					   if(e.getLabel().equals(jamSegs.get(i)))
					   {
						   if((e.getWeight()==(int)speedLow/3)||(e.getWeight()==(int)speedMedium/3)||(e.getWeight()==(int)speedHigh/3))
						    e.setWeight(e.getWeight()*3);
				//--		   System.out.println(e.getLabel()+" - "+e.getWeight());
				//--		   bw.write("\n"+e.getLabel()+" - "+e.getWeight());
					   }
				   }
			   }
			   segment.setWeight(segment.getWeight()*3);
//-----------END OF RESTORING SPEEDS OF JAM SEGMENTS------------------------------------------------------------			   
			   

			   
//-----------EXPORTING THE PICTURE--------------------------------------------------------------------
			   PrintWriter writer;
			   writer = new PrintWriter("Result"+count_while+".dot", "UTF-8");
			   writer.println("graph result"+"\n"+"{"+"\n");
			   ArrayList<String> completedNodes=new ArrayList<String>();
			   if(bestPath.size()>=2)
			   writer.append(udg.getNode(bestPath.get(0)).getLabel()+"--"+udg.getNode(bestPath.get(1)).getLabel()+
					   "[label="+udg.getEdge(udg.getNode(bestPath.get(0)),udg.getNode(bestPath.get(1))).getLabel()+" color=\"orange\""+"]"+"\n");   

			   for(Node nd1:udg.getNodes())
			   {   
				   for(Node nd2:udg.getNeighbors(nd1))
				   {
					   Edge curEdge=udg.getEdge(nd1,nd2);
					   String EdgeLabel=curEdge.getLabel();
					   
					   if(!completedNodes.contains(nd2.getLabel()))
					   {
					   
					   
					   if(bestPath.contains(nd1.getLabel())&&bestPath.contains(nd2.getLabel()))
					   {
						   if(Math.abs(bestPath.indexOf(nd1.getLabel())-bestPath.indexOf(nd2.getLabel()))==1
								   && bestPath.indexOf(nd1.getLabel())!=0 && bestPath.indexOf(nd2.getLabel())!=0)
						   {
							   writer.append(nd1.getLabel()+"--"+nd2.getLabel()+"[label="+EdgeLabel+" weight="+curEdge.getWeight()+" color=\"green\""+"]"+"\n");   
						   }
					   }
					   else
					   {
						   writer.append(nd1.getLabel()+"--"+nd2.getLabel()+"[label="+EdgeLabel+" weight="+curEdge.getWeight()+"]"+"\n");
					   }
					   }
				   }
				   completedNodes.add(nd1.getLabel());
			   }
			   writer.append("}");
			   writer.close();
//-------------END OF EXPORT THE PICTURE--------------------------------------------- 
			   
			   
//---------------REMOVING TRAVERSED JUNCTION AND GIVING REMAINING PATH TO NEXT ITERATION-------------			   
			   bestPath.remove(src);
			   finishedNodes.add(src);
			   if(bestPath.size()>0){
			   System.out.println("NEXT ITERATION PATH: "+bestPath+" "+"\n");
			   src=bestPath.get(0);
//---------------END OF REMOVING TRAVERSED JUNCTION AND GIVING REMAINING PATH TO NEXT ITERATION-------------			   
			   
			   
			   			   
			   } //END OF IF PATH SIZE > 1-------------------------------
			   else
			   {
				   System.out.println("DONE");
				    endTime = System.nanoTime();
				    double seconds = (double)(endTime - startTime) / 1000000000.0;
				    System.out.println("Took "+seconds + " secs");
			   }
			   
			   bw.close();
			   }
			   		   
		   }
		   catch(Exception ex)
		   {
			   ex.printStackTrace();
			   return;
		   }
		   
		  
	}
	

//-----------------------------DFS---------------------------------------
	 private static Stack<String> path;   // the current path
	 private static Set<String> grayNodes;     // the set of vertices on the path
	 private static Stack<Double> pathWeight;
	 private static Set<Double> onPathWeight;
	 static ArrayList<String> listPaths;
	 static ArrayList<Double> listEtas;
	 static double totalWeight=0.0;
	 static double minEtime=1000;
     static String minPath=null;
     static int  count=0;
     static Map<String,String> colors=new HashMap<String,String>();
	 
	public static void AllPaths(UndirectedGraph udg,String s, String t, double pathWeight1) {
		 path  = new Stack<String>();
		 grayNodes  = new HashSet<String>(); //Keeps track of gray colored nodes
		 pathWeight  = new Stack<Double>();
		 onPathWeight  = new HashSet<Double>();
		 listPaths=new ArrayList<String>();
		 listEtas=new ArrayList<Double>();
		 bestPath=new ArrayList<String>();
		 minEtime=1000;
	     minPath=null;
	     count=0;
	     
	    /* for(Node n:udg.getNodes())
		 {
			 colors.put(n.getLabel(),"white"); // Initially every node is white
		 }*/
		 
		dfsVisit(udg,s, t, pathWeight1);
    }
//--------------------------END OF DFS-----------------------------------------------	

	 
//--------------MODIFIED DFS VISIT--------------------------------------------- 
    private static void dfsVisit(UndirectedGraph udg1,String v, String t, double segmentWeight) {

    	//if(count<=10){
        // add node v to current path from s
    	
        path.push(v); 
        grayNodes.add(v); //means colors.put(v,"gray"); (Making color of 'v' as gray)
        pathWeight.add(segmentWeight);
        onPathWeight.add(segmentWeight);
        
        // found path from s to t - currently prints in reverse order because of stack
        if (v.equals(t)) 
        {   double totalWeight=0.0; 
        	double eta=0.0;
        	double totalEta=0.0;
            for(double d:pathWeight)
        	{
        		if(d==speedHigh||d==(int)speedHigh/3||d==speedHigh-1)
        		{
        			eta=distHigh/d;
        		}
        		else if(d==speedMedium||d==(int)speedMedium/3||d==speedMedium-2)
        		{
        			eta=distMedium/d;
        		}
        		else if(d==speedLow||d==(int)speedLow/3)
        		{
        			eta=distLow/d;
        		}
        		totalWeight=totalWeight+d;
        		totalEta=totalEta+eta;
        	}
        	//System.out.println(path+" "+totalWeight+" "+totalEta);
            if(totalEta<minEtime)
            {
            	minEtime=totalEta;
            	estimatedTime=totalEta;
                minPath=path.toString();
                bestPath.clear();
                for(int j=0;j<path.size();j++)
                 bestPath.add(path.get(j));
            }
            count++;
        }
        else 
        {
        	for(Node n:udg1.getNodes())
        	{ //System.out.println("YES");
        	  if(n.getLabel().equals(v))
        	  {
              for (Node nei : udg1.getNeighbors(n)) 
               {
                if (!grayNodes.contains(nei.getLabel())&&!(finishedNodes.contains(nei.getLabel()))) //Means if neighbour color is not Gray and neighbour is not Eliminated
                	dfsVisit(udg1, nei.getLabel(), t,udg1.getEdge(n,nei).getWeight());
               }
              }
        	}
        }

        path.pop(); // popping out last node in the stack
        pathWeight.pop(); // popping out last weight on the stack 
        grayNodes.remove(v); // Making the gray node back to white so that it may be used in other alternative path
        
        

    	//}//END OF IF COUNT<=10
    }
//-----------END OF MODIFIED DFS VISIT------------------------------------------------------------------    
	
	 	
}
