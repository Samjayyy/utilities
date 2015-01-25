package utilities.autocomplete;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author Sammie
 */
public class LazyTrieNode implements IAutocomplete {
	private static final ArrayList<Integer> EMPTY = new ArrayList<Integer>();
	
	private ArrayList<Integer> values;
	private HashMap<Character, LazyTrieNode> next;
	String rest;
	public LazyTrieNode() {
		next = new HashMap<Character, LazyTrieNode>();
		values = new ArrayList<Integer>();
		rest = null;
	}
	// insert value in trie
	public void insert(String word, int val) {
		LazyTrieNode current = this;
		for(int i=0;i<word.length();i++){
			LazyTrieNode nextNode = current.next.get(word.charAt(i));
			if (nextNode == null) {
				nextNode = new LazyTrieNode();
				nextNode.rest = word.substring(i+1); // initially we do not further expand
				nextNode.values.add(val);
				current.next.put(word.charAt(i), nextNode);
				break;
			}else if(nextNode.rest != null && nextNode.rest.length() > 0){
				nextNode.insert(nextNode.rest, nextNode.values.get(0)); // expand lazy node
				nextNode.rest = null;
			}
			current = nextNode;
			if(current.values.size()<MAX_HITS)
				current.values.add(val);
		}
	}
	public ArrayList<Integer> find(String key){
		LazyTrieNode current = this;
		for(int i=0;i<key.length();i++){
			LazyTrieNode nextNode = current.next.get(key.charAt(i));
			if (nextNode == null) {
				if(current.rest != null 
						&& current.rest.startsWith(key.substring(i))){
					break; // only one possible solution left
				}
				return EMPTY;
			}
			current = nextNode;
		}
		return current.values;
	}
}
