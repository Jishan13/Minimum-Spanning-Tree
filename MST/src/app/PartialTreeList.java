package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {

	/**
	 * Inner class - to build the partial tree circular linked list
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;

		/**
		 * Next node in linked list
		 */
		public Node next;

		/**
		 * Initializes this node by setting the tree part to the given tree, and setting
		 * next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;

	/**
	 * Number of nodes in the CLL
	 */
	private int size;

	/**
	 * Initializes this list to empty
	 */
	public PartialTreeList() {
		rear = null;
		size = 0;
	}

	/**
	 * Adds a new tree to the end of the list
	 * 
	 * @param tree Tree to be added to the end of the list
	 */
	public void append(PartialTree tree) {
		Node ptr = new Node(tree);
		if (rear == null) {
			ptr.next = ptr;
		} else {
			ptr.next = rear.next;
			rear.next = ptr;
		}
		rear = ptr;
		size++;
	}

	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		/* COMPLETE THIS METHOD */
		PartialTreeList L = new PartialTreeList();
		for (int i = 0; i < graph.vertices.length; i++) {
			PartialTree T = new PartialTree(graph.vertices[i]);
			Vertex v = graph.vertices[i];
			Vertex.Neighbor neighbors = graph.vertices[i].neighbors;
			while (neighbors != null) {
				Vertex n = neighbors.vertex;
				int weight = neighbors.weight;
				Arc arc = new Arc(v, n, weight);
				T.getArcs().insert(arc);
				neighbors = neighbors.next;
			}
			L.append(T);
		}
		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree
	 * list for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is
	 *         irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		ArrayList<Arc> MST = new ArrayList<Arc>();
		while (ptlist.size > 1) {
			PartialTree first = ptlist.remove();
			Arc arc = null;
			Vertex v1 = null;
			Vertex v2 = null;
			boolean isV2dup = true;
			while (isV2dup) {
				arc = first.getArcs().deleteMin();
				v1 = arc.getv1();
				v2 = arc.getv2();
				isV2dup = v2Checker(v1, v2);
			}
			if (!isV2dup)
				MST.add(arc);
			PartialTree second = ptlist.removeTreeContaining(v2);
			if (second == null) {
			}
			first.merge(second);
			ptlist.append(first);
		}
		return MST;
	}

	private static boolean v2Checker(Vertex v1, Vertex v2) {
		if (v1.getRoot().equals(v2.getRoot()))
			return true;
		return false;
	}

	/**
	 * Removes the tree that is at the front of the list.
	 * 
	 * @return The tree that is removed from the front
	 * @throws NoSuchElementException If the list is empty
	 */
	public PartialTree remove() throws NoSuchElementException {

		if (rear == null) {
			throw new NoSuchElementException("list is empty");
		}
		PartialTree ret = rear.next.tree;
		if (rear.next == rear) {
			rear = null;
		} else {
			rear.next = rear.next.next;
		}
		size--;
		return ret;

	}

	/**
	 * Removes the tree in this list that contains a given vertex.
	 * 
	 * @param vertex Vertex whose tree is to be removed
	 * @return The tree that is removed
	 * @throws NoSuchElementException If there is no matching tree
	 */
	public PartialTree removeTreeContaining(Vertex vertex) throws NoSuchElementException {
		/* COMPLETE THIS METHOD */
		Node front = this.rear.next;
		Node prev = this.rear;
		PartialTree ret = null;
		do {
			if (rear.next == rear && v2Checker(front.tree.getRoot(), vertex)) {
				ret = rear.tree;
				rear = null;
				size--;
				return ret;
			}
			if (v2Checker(front.tree.getRoot(), vertex)) {
				ret = front.tree;
				if (front == rear) {
					rear = prev;
				}
				prev.next = front.next;
				size--;
				return ret;
			}
			prev = front;
			front = front.next;
		} while (front != this.rear.next);
		throw new NoSuchElementException("No Such Element Found");
	}

	/**
	 * Gives the number of trees in this list
	 * 
	 * @return Number of trees
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns an Iterator that can be used to step through the trees in this list.
	 * The iterator does NOT support remove.
	 * 
	 * @return Iterator for this list
	 */
	public Iterator<PartialTree> iterator() {
		return new PartialTreeListIterator(this);
	}

	private class PartialTreeListIterator implements Iterator<PartialTree> {

		private PartialTreeList.Node ptr;
		private int rest;

		public PartialTreeListIterator(PartialTreeList target) {
			rest = target.size;
			ptr = rest > 0 ? target.rear.next : null;
		}

		public PartialTree next() throws NoSuchElementException {
			if (rest <= 0) {
				throw new NoSuchElementException();
			}
			PartialTree ret = ptr.tree;
			ptr = ptr.next;
			rest--;
			return ret;
		}

		public boolean hasNext() {
			return rest != 0;
		}

		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}
}
