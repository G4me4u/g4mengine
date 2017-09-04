package com.g4mesoft.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BinaryTree<K extends Comparable<K>, V> {

	private Node root;
	
	public void insert(Map<K, V> entries) {
		Iterator<Map.Entry<K, V>> entryItr = entries.entrySet().iterator();
		while (entryItr.hasNext()) {
			Map.Entry<K, V> entry = entryItr.next();
			insert(entry.getKey(), entry.getValue());
		}
	}
	
	public void insert(K key, V value) {
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
}
