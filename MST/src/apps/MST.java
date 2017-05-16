package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		/* COMPLETE THIS METHOD */
		PartialTreeList mst = new PartialTreeList();
		for(Vertex x: graph.vertices){
			PartialTree temp = new PartialTree(x);
			Vertex.Neighbor list = x.neighbors;
			while(list != null){
				PartialTree.Arc arc = new PartialTree.Arc(x,list.vertex, list.weight);
				temp.getArcs().insert(arc);
				list = list.next;
			}
			mst.append(temp);
		}
		
		return mst;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		/* COMPLETE THIS METHOD */
		ArrayList<PartialTree.Arc> path = new ArrayList<PartialTree.Arc>();
		while(ptlist.size() > 1){
			
//			Iterator<PartialTree> iter = ptlist.iterator();
//			while (iter.hasNext()) {
//				PartialTree pt = iter.next();
//				System.out.println("Root "+ pt.getRoot());
//			}
			
			PartialTree ptx = ptlist.remove();
			
//			System.out.println(ptx.toString());
			
			MinHeap<PartialTree.Arc> pqx = ptx.getArcs();
			PartialTree.Arc temp = pqx.deleteMin();
//			System.out.println(ptx.getRoot().getRoot() + " connect " + temp.v2);
			while(ptx.getRoot().getRoot().equals(temp.v2.getRoot())){
				temp = pqx.deleteMin();
//				System.out.println(temp.toString());
//				System.out.println(ptx.getRoot().getRoot() + " connect " + temp.v2);
			}
			path.add(temp);
			PartialTree pty = ptlist.removeTreeContaining(temp.v2); 
			pty.getRoot().parent = ptx.getRoot().getRoot();
			ptx.merge(pty);
			ptlist.append(ptx);
//			System.out.println();
//			System.out.println(path);
//			System.out.println();
//			ptlist.print();
		}
		return path;
	}
}
