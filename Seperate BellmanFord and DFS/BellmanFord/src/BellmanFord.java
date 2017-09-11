
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

public class BellmanFord 
{
	static int distLow=1,distMedium=2,distHigh=3;
	static int speedLow=30,speedMedium=50,speedHigh=70;
	static long startTime;
	static long endTime;
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
			  
			   File file=new File("RoadGenerator_100.dot");
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
			   
			   startTime = System.nanoTime();
			   	//Finding Paths before assigning Traffic Jam
			   bellman(udg,src,dest);
			  // System.out.println("POSSIBLE PATHS: "+ listPaths);
			   endTime = System.nanoTime();
			    double seconds = (double)(endTime - startTime) / 1000000000.0;
			    System.out.println("Took "+seconds + " secs");
		   }
		   catch(Exception ex)
		   {
			   System.out.println(ex.toString());
			   ex.printStackTrace();
			   return;
		   }
		   
		  
	}
	
	
	
	
	
	public static void bellman(UndirectedGraph udg1,String v, String t)
	{
		
		double adj[][]=new double[udg1.getNodeCount()][udg1.getNodeCount()];

		ArrayList<String> nodes=new ArrayList<String>();
		ArrayList<Double> nodesDist=new ArrayList<Double>();
		
		for(Node n1:udg1.getNodes())
			nodes.add(n1.getLabel());
		
		for(int i=0;i<nodes.size();i++)
		{
			nodesDist.add(1000.0);
		}
		
		nodesDist.set(nodes.indexOf(v),0.0);
		
		Collections.sort(nodes);
		System.out.println(nodes+" "+nodes.size());
		System.out.println(nodesDist);

		
		for(int i=0;i<nodes.size();i++)
		{
			for(Edge e:udg1.getEdges())
			{
				//System.out.println(e.getLabel()+" "+e.getSource().getLabel()+" "+e.getTarget().getLabel());
			    double v2=nodesDist.get(nodes.indexOf(e.getTarget().getLabel()));
				double v1=nodesDist.get(nodes.indexOf(e.getSource().getLabel()));
			    double ew=e.getWeight();
			    double ett=0.0,dist=0.0;
			    if(ew==30.0)dist=1;
			    else if(ew==50.0)dist=2;
			    else if(ew==70.0)dist=3;
			    
			    ett=(double)(dist/ew);
			    
				if(v2>v1+ett)
			    {
					nodesDist.set(nodes.indexOf(e.getTarget().getLabel()), v1+ett);
			    }
			}
		}
		
		System.out.println(nodesDist);
		System.out.println(v+" to "+t+" takes "+nodesDist.get(nodes.indexOf(t)));
		
		
		
	}
	
	
	
	 	
}


