package dfs;
import java.io.File;
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

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.ElementListener;

public class dfs 
{
	static int distLow=1,distMedium=2,distHigh=3;
	static int speedLow=30,speedMedium=50,speedHigh=70;
	public static void main(String args[])
	{
		   
	       
		   ArrayList<String> roadSegments=new ArrayList<String>();
		   System.out.println("reached"); 
		   ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		   pc.newProject();
		   Workspace workspace = pc.getCurrentWorkspace();
		   
		   ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		   org.gephi.io.importer.api.Container container;
		   try 
		   {
			  
			   File file=new File("graph3.dot");
			   container = importController.importFile(file);
			   container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);
			   ((ContainerLoader) container).setAllowAutoNode(false);
			   
			   importController.process(container,new DefaultProcessor(),workspace);
			   
			   GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
			   UndirectedGraph udg=graphModel.getUndirectedGraph();
			   System.out.println("Nodes:"+udg.getNodeCount()+" "+"edges:"+udg.getEdgeCount());
			   
			   			   
			   for(Edge e:udg.getEdges())
			   {
				  roadSegments.add(e.getLabel());				  
			   }
			   

			   
			 //Asking for Source and Destination
			   String src,dest;
			   Scanner sc=new Scanner(System.in);
			   System.out.println("Enter Source:");
			   src=sc.nextLine();
			   System.out.println("Enter Destination:");
			   dest=sc.nextLine();
			   //System.out.println(src+" "+dest);
			   
			   
			   	//Finding Paths before assigning Traffic Jam
			   AllPaths(udg,src,dest,0.0);
			   System.out.println("POSSIBLE PATHS: "+ listPaths);
	           //System.out.println("RESPECTIVE ETIME: "+ listEtas);
			   //String minEtaPath1=listPaths.get(listEtas.indexOf(Collections.min(listEtas)));
			   //System.out.println("MIN ETIME PATH: "+minEtaPath1+" ETIME: "+Collections.min(listEtas));

			         
		   }
		   catch(Exception ex)
		   {
			   System.out.println(ex.toString());
			   ex.printStackTrace();
			   return;
		   }
		   
		  
	}
	
	
	 //private static ArrayList<String> path;   // the current path
	 private static Stack<String> path;
	// private static Set<String> onPath;     // the set of vertices on the path
	 private static ArrayList<String> onPath;
	 static ArrayList<String> listPaths;
	 
	 static Map<String,String> colors=new HashMap<String,String>();
	
	public static void AllPaths(UndirectedGraph udg,String s, String t, double pathWeight1) {
		 
		 path= new Stack<String>();
		 listPaths=new ArrayList<String>();
		 for(Node n:udg.getNodes())
		 {
			 colors.put(n.getLabel(),"white");
		 }
		 		 
	    dfsVisit(udg,s,t);
		 
    }
	
	
	public static void dfsVisit(UndirectedGraph udg1,String v, String t)
	{
		colors.put(v,"gray");
		path.push(v);
		System.out.println("PUSHED "+v+" into path and make its color gray");
						
		if(v.equals(t))
		{
			//System.out.println(path);
			System.out.println("Added "+path+" to paths");
			listPaths.add(path.toString());
		}
		
		else
		{
		for(Node n:udg1.getNodes())
    	{ 
		 if(n.getLabel().equals(v))
		 {
			 for(Node neib:udg1.getNeighbors(n))
			 {   System.out.println("Neigh of "+n.getLabel()+" "+neib.getLabel());
				 if(colors.get(neib.getLabel()).equals("white"))
				 {
					 System.out.println("Color of "+neib.getLabel()+" is white");
					 System.out.println(neib.getLabel()+" equals white");
					 dfsVisit(udg1, neib.getLabel(), t);
				 }
			 }
			 
			 
		 }
    	}
		}
		
		path.pop();
		System.out.println("popped "+v+" from"+path);
		colors.put(v, "white");
	}

	 
	 // use DFS
    private static void enumerate(UndirectedGraph udg1,String v, String t, double segmentWeight) {

        // add node v to current path from s
        //path.add(v);
        path.push(v);
    	onPath.add(v);
        
        // found path from s to t - currently prints in reverse order because of stack
        if (v.equals(t)) 
        {   
        	listPaths.add(path.toString());
        	
        }
        	        
        // consider all neighbors that would continue path with repeating a node
        else 
        {
        	for(Node n:udg1.getNodes())
        	{ //System.out.println("YES");
        	  if(n.getLabel().equals(v))
        	  {
              for (Node nei : udg1.getNeighbors(n)) 
               {
                if (!onPath.contains(nei.getLabel())) 
                	enumerate(udg1, nei.getLabel(), t,udg1.getEdge(n,nei).getWeight());
               }
              }
        	}
        }

        // done exploring from v, so remove from path
        path.clear();
        //path.pop();
        onPath.remove(v);
        
    }
	
	 	
}
