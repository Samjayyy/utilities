package utilities.autocomplete;

/**
 * @author Sammie
 */
public class TrieNode {
	private static final int M_CHAR = 26, M_NUM = 10;
	private static final int MAX_HITS = 100;
	private static final int[] EMPTY = new int[]{0};
	
	private int[] values;
	private TrieNode[] next;
	public TrieNode() {
		next = new TrieNode[M_CHAR+M_NUM];
		values = new int[MAX_HITS+1];
	}
	// insert value in trie
	public void insert(String word, int val) {
		TrieNode current = this;
		for(int i=0;i<word.length();i++){
			int cval = getVal(word.charAt(i));
			if(cval<0)continue;
			if (current.next[cval] == null) {
				current.next[cval] = new TrieNode();
			}
			current = current.next[cval];
			if(current.values[0]<MAX_HITS)
				current.values[++current.values[0]]=val;
		}
	}
	public int[] find(String key){
		TrieNode current = this;
		for(int i=0;i<key.length();i++){
			int cval = getVal(key.charAt(i));
			if(cval<0)continue;
			if (current.next[cval] == null) {
				return EMPTY;
			}
			current = current.next[cval];
		}
		return current.values;
	}
	// Only use [A-z][0-9], ignore special chars and spaces
	private int getVal(char c){
		int val = c-'a';
		if(val >= 0 && val < M_CHAR)return val;
		val = c-'A';
		if(val >= 0 && val < M_CHAR)return val;
		val = c-'0';
		if(val >= 0 && val < M_NUM)return val+M_CHAR;
		return -1;
	}
}