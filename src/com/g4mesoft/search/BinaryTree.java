package com.g4mesoft.search;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.g4mesoft.search.BinaryTree.Entry;

public class BinaryTree<K extends Comparable<K>, V> implements Iterable<Entry<K, V>> {

	private Node root;
	private int modCount;
	
	public void insert(Map<K, V> entries) {
		Iterator<Map.Entry<K, V>> entryItr = entries.entrySet().iterator();
		while (entryItr.hasNext()) {
			Map.Entry<K, V> entry = entryItr.next();
			insert(entry.getKey(), entry.getValue());
		}
	}
	
	public void insert(K key, V value) {
		modCount++;
		
		if(root == null) {
			root = new Node(key, value);
			return;
		}
		
		insertInto(root, new Node(key, value));
	}
	
	private void insertInto(Node branchNode, Node insertNode) {
		Node current = branchNode;
		Node next = null;
		
		K key = insertNode.key;
		
		while(true) {
			if(key.compareTo(current.key) < 0) {
				next = current.left;
			} else {
				next = current.right;
			}
			
			if (next != null) {
				current = next;
			} else break;
		}
		
		if(key.compareTo(current.key) < 0) {
			current.left = insertNode;
		} else {
			current.right = insertNode;
		}
	}

	public Entry<K, V> removeFirstKey(K key) {
		return removeFirst((Entry<K, V> o) -> {
			return key.compareTo(o.key);
		});
	}
	
	public List<Entry<K, V>> removeAllKeys(K key) {
		return removeAll((Entry<K, V> o) -> {
			return key.compareTo(o.key);
		});
	}
	

	public Entry<K, V> removeFirst(Comparable<Entry<K, V>> filter) {
		if (root == null)
			return null;
		
		if (filter.compareTo(root) == 0) {
			Node oldRoot = root;
			
			modCount++;
			
			if (root.left != null) {
				if (root.right != null)
					insertInto(root.left, root.right);
				root = root.left;
			} else root = root.right;
			
			return oldRoot;
		}
		
		Node previous = root;
		Node next = null;
		
		while(true) {
			if(filter.compareTo(previous) < 0) {
				next = previous.left;
			} else {
				next = previous.right;
			}
			
			if (next != null) {
				if (filter.compareTo(next) == 0) {
					modCount++;
					
					if (next == previous.left) {
						previous.left = null;
					} else previous.right = null;
					
					if (next.left != null)
						insertInto(previous, next.left);
					if (next.right != null)
						insertInto(previous, next.right);
					
					return next;
				}

				previous = next;
			} else break;
		}
		
		return null;
	}

	public List<Entry<K, V>> removeAll(Comparable<Entry<K, V>> filter) {
		if (root == null)
			return null;
		
		List<Entry<K, V>> values = new ArrayList<Entry<K, V>>();
		
		if (filter.compareTo(root) == 0) {
			Node oldRoot = root;
			
			modCount++;

			if (root.left != null) {
				if (root.right != null)
					insertInto(root.left, root.right);
				root = root.left;
			} else root = root.right;
			
			oldRoot.left = oldRoot.right = null;
			values.add(oldRoot);
			
			if (root == null)
				return values;
		}
		
		Node previous = root;
		Node current = null;
		
		while(true) {
			if(filter.compareTo(previous) < 0) {
				current = previous.left;
			} else {
				current = previous.right;
			}

			if (current != null) {
				if (filter.compareTo(current) == 0) {
					modCount++;
					
					if (current == previous.left) {
						previous.left = null;
					} else previous.right = null;
					
					if (current.left != null)
						insertInto(previous, current.left);
					if (current.right != null)
						insertInto(previous, current.right);

					current.left = current.right = null;
					
					values.add(current);
				} else {
					previous = current;
				}
			} else break;
		}
		
		return values;
	}

	public boolean containsKey(K key) {
		return getEntry(key) != null;
	}
	
	public Entry<K, V> getEntry(K key) {
		if (root == null)
			return null;
		
		Node current = root;
		Node next = null;
		
		while(true) {
			if (key.equals(current.key))
				return current;
			
			if(key.compareTo(current.key) < 0) {
				next = current.left;
			} else {
				next = current.right;
			}
			
			if (next != null) {
				current = next;
			} else break;
		}
		
		return null;
	}
	
	public List<Entry<K, V>> getEntries(K key) {
		if (root == null)
			return null;
		
		List<Entry<K, V>> values = new ArrayList<Entry<K, V>>();
		
		Node current = root;
		Node next = null;
		
		while(true) {
			if (key.equals(current.key))
				values.add(current);

			if(key.compareTo(current.key) < 0) {
				next = current.left;
			} else {
				next = current.right;
			}

			if (next != null) {
				current = next;
			} else break;
		}
		
		return values;
	}
	
	public Entry<K, V> getMin() {
		if (root == null)
			return null;
		
		Node current = root;
		while(current.left != null)
			current = current.left;
		return current;
	}
	
	public Entry<K, V> getMax() {
		if (root == null)
			return null;
		
		Node current = root;
		while(current.right != null)
			current = current.right;
		return current;
	}
	
	public void clear() {
		modCount++;

		root = null; // Let gc do it's work
	}
	
	public boolean isEmpty() {
		return root == null;
	}
	
	private class Node extends Entry<K, V> {
		
		private Node left;
		private Node right;
	
		private Node(K key, V value) {
			super(key, value);
			
			left = right = null;
		}
	}
	
	public static class Entry<K extends Comparable<K>, V> implements Comparable<Entry<K, V>> {
		
		public final K key;
		public final V value;
		
		private Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public int compareTo(Entry<K, V> o) {
			return equals(o) ? 0 : (key.equals(o.key) ? 1 : key.compareTo(o.key));
		}
		
		@Override
		@SuppressWarnings({ "rawtypes"})
		public boolean equals(Object object) {
			if (object == null) return false;
			
			if (!(object instanceof Entry))
				return false;
			return equals(((Entry)object));
		}
		
		@SuppressWarnings({ "rawtypes"})
		public boolean equals(Entry other) {
			if (other == null) 
				return false;
			return key.equals(other.key) && 
					value.equals(other.value);
		}
		
		@Override
		public String toString() {
			return String.format("(%s, %s)", key, value);
		}
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new TreeIterator(root);
	}
	
	private class TreeIterator implements Iterator<Entry<K, V>> {

		private Stack<Node> nodes;
		private long expectedModCount;
		
		public TreeIterator(Node root) {
			nodes = new Stack<Node>();
			while (root != null) {
				nodes.push(root);
				root = root.left;
			}
			
			expectedModCount = modCount;
		}
		
		@Override
		public boolean hasNext() {
			return !nodes.isEmpty();
		}
		
		@Override
		public Entry<K, V> next() {
			checkForComodification();
			
			Node result = nodes.pop();
			if (result == null)
				throw new NoSuchElementException();

			Node node = result.right;
			while (node != null) {
				nodes.push(node);
				node = node.left;
			}
			
			return result;
		}
		
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
	}
}
