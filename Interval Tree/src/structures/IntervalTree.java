package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		// COMPLETE THIS METHOD
		if(lr == 'r')
			sortRight(intervals);
		else
			sortLeft(intervals);
	}
	
	private static void sortRight(ArrayList<Interval> list){
		for(int x = 1; x < list.size();x++){
			for(int y = x; y > 0; y--){
				if(list.get(y-1).rightEndPoint > list.get(y).rightEndPoint){
					Interval temp = list.get(y);
					list.set(y, list.get(y-1));
					list.set(y-1, temp );
				}
				else 
					break;
			}
		}
	}
	
	private static void sortLeft(ArrayList<Interval> list){
		for(int x = 1; x < list.size();x++){
			for(int y = x; y > 0; y--){
				if(list.get(y-1).leftEndPoint > list.get(y).leftEndPoint){
					Interval temp = list.get(y);
					list.set(y, list.get(y-1));
					list.set(y-1, temp );
				}
				else 
					break;
			}
		}
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(Interval x: leftSortedIntervals){
			if(!list.contains(x.leftEndPoint)){
				list.add(x.leftEndPoint);
			}
		}
		for(Interval x: rightSortedIntervals){
			if(!list.contains(x.rightEndPoint)){
				int i = 0;
				while(i < list.size() && list.get(i).intValue() < x.rightEndPoint)
					i++;
				if(i == list.size())
					list.add(x.rightEndPoint);
				else
					list.add(i, x.rightEndPoint);
			}
		}
		return list;
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		Queue<IntervalTreeNode> q = new Queue<IntervalTreeNode>();
		for(Integer x: endPoints){
			IntervalTreeNode nodes = new IntervalTreeNode(x,x,x);
			nodes.leftIntervals = new ArrayList<Interval>();
			nodes.rightIntervals = new ArrayList<Interval>();
			q.enqueue(nodes);
		}
				
		int s = q.size;
		if(s == 1)
		{
			IntervalTreeNode temp = q.dequeue();
			return temp;
		}
		
		while(q.size() > 1){
			int temp = q.size();
			while(temp > 1){
				IntervalTreeNode n1 = q.dequeue();
				IntervalTreeNode n2 = q.dequeue();
//				float v1 = findLeft(n1);
				float v1 = n1.maxSplitValue;
//				System.out.println("v1 =" + v1);
//				float v2 = findRight(n2);
				float v2 = n2.minSplitValue;
//				System.out.println("v2 =" + v2);
				float sp = (v1+v2)/2;
//				System.out.println("sp =" + sp);
				IntervalTreeNode n = new IntervalTreeNode(sp, n1.minSplitValue, n2.maxSplitValue);
//				n.leftIntervals = new ArrayList<Interval>();
//				n.rightIntervals = new ArrayList<Interval>();
				n.leftChild = n1;
				n.rightChild = n2;
				q.enqueue(n);
				temp -= 2;
			}
			
			if(temp == 1){
				q.enqueue(q.dequeue());
			}
		}
		return q.dequeue();
	}
	
//	private static float findLeft(IntervalTreeNode r){
//		while(r.rightChild != null)
//			r = r.rightChild;
//		return r.splitValue;
//		
//	}
//	
//	private static float findRight(IntervalTreeNode l){
//		while(l.leftChild != null)
//			l = l.leftChild;
//		return l.splitValue;
//	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		for(Interval l: leftSortedIntervals){
			IntervalTreeNode temp = findLargest(l, root);
			if(temp.leftIntervals == null)
				temp.leftIntervals = new ArrayList<Interval>();
			temp.leftIntervals.add(l);
		}
		for(Interval r: rightSortedIntervals){
			IntervalTreeNode temp = findLargest(r, root);
			if(temp.rightIntervals == null)
				temp.rightIntervals = new ArrayList<Interval>();
			temp.rightIntervals.add(r);
		}
	}
	
	private IntervalTreeNode findLargest(Interval i, IntervalTreeNode node){
		if(i.contains(node.splitValue))
			return node;
		if(node.splitValue > i.rightEndPoint)
			return findLargest(i, node.leftChild);
		else
			return findLargest(i, node.rightChild);
	}
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		return findIntersectingIntervals(this.root, q);
	}
	
	private ArrayList<Interval> findIntersectingIntervals(IntervalTreeNode root, Interval q){
		ArrayList<Interval> resultList = new ArrayList<Interval>();
		IntervalTreeNode t = root;
		float splitval = t.splitValue;
		ArrayList<Interval> leftList = t.leftIntervals;
		ArrayList<Interval> rightList = t.rightIntervals;
		IntervalTreeNode lsub = t.leftChild;
		IntervalTreeNode rsub = t.rightChild;
		
		if((t.leftChild == null && t.rightChild == null))
			return resultList;
		if(q.contains(splitval)){
			if(leftList != null)
				resultList.addAll(leftList);
//			t.matchLeft(q, resultList);
//			t.matchRight(q, resultList);
			resultList.addAll(findIntersectingIntervals(lsub, q));
			resultList.addAll(findIntersectingIntervals(rsub, q));
		}
		else if(splitval < q.leftEndPoint){
//			t.matchRight(q, resultList);
			if(rightList != null){
				int i = rightList.size()-1;
				while(i >= 0 && rightList.get(i).intersects(q)){
					resultList.add(rightList.get(i));
					i--;
				}
			}
			resultList.addAll(findIntersectingIntervals(rsub, q));
		}	
		else if(splitval > q.rightEndPoint){
//			t.matchLeft(q, resultList);
			if(leftList != null){
				int i = 0;
				while(i < leftList.size() && leftList.get(i).intersects(q)){
					resultList.add(leftList.get(i));
					i++;
				}
			}
			resultList.addAll(findIntersectingIntervals(lsub, q));
		}
		
		return resultList;
	}

}

