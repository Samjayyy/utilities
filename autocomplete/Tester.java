package utilities.autocomplete;

import java.util.ArrayList;
import java.util.UUID;

public class Tester {
	static final int MAX_RECORDS = 100000;
	static final int LOOKUP_TESTS = 100000;
	public static void main(String[] args) {
		testCorrectness();
	}
	static void testCorrectness(){
		log("Starting test..");
		LazyTrieNode root = new LazyTrieNode();
		// empty
		assertTrue(root.find("").isEmpty());
		assertTrue(root.find("abcd").isEmpty());
		// insert abcd - 1
		root.insert("abcd", 1);
		assertTrue(root.find("a").size()==1);
		assertTrue(root.find("a").get(0)==1);
		assertTrue(root.find("ab").get(0)==1);
		assertTrue(root.find("abc").get(0)==1);
		assertTrue(root.find("abcd").get(0)==1);
		// insert abcde - 2
		root.insert("abcde", 2);
		assertTrue(root.find("a").size()==2);
		assertTrue(root.find("a").get(0)==1);
		assertTrue(root.find("a").get(1)==2);
		assertTrue(root.find("ab").get(0)==1);
		assertTrue(root.find("ab").get(1)==2);
		assertTrue(root.find("abc").get(0)==1);
		assertTrue(root.find("abc").get(1)==2);
		assertTrue(root.find("abcd").get(0)==1);
		assertTrue(root.find("abcd").get(1)==2);
		assertTrue(root.find("abcde").get(0)==2);
		// insert bcd - 3
		root.insert("bcd", 3);
		assertTrue(root.find("b").size()==1);
		assertTrue(root.find("b").get(0)==3);
		assertTrue(root.find("bc").get(0)==3);
		assertTrue(root.find("bcd").get(0)==3);
		assertTrue(root.find("bcde").isEmpty());
		// insert bcd - 4
		root.insert("bcd", 4);
		assertTrue(root.find("b").size()==2);
		assertTrue(root.find("b").get(0)==3);
		assertTrue(root.find("b").get(1)==4);
		assertTrue(root.find("bc").get(0)==3);
		assertTrue(root.find("bc").get(1)==4);
		assertTrue(root.find("bcd").get(0)==3);
		assertTrue(root.find("bcd").get(1)==4);
		assertTrue(root.find("bcde").isEmpty());
		log("all testCases passed");
	}
	static void assertTrue(boolean condition){
		if(!condition)
			throw new AssertionError();
	}
	static void testPerformance(){
		log("Starting test..");
        printMemory();
        long start = System.currentTimeMillis();
        log("Loading data..");
		ArrayList<String> lijst = new ArrayList<String>();
		for(int i=0;i<MAX_RECORDS;i++){
			lijst.add(UUID.randomUUID().toString());
		}
		log("Time: "+(System.currentTimeMillis()-start)+" ms");
        printMemory();
		// test Trie
        System.err.println("Start filling the trie");
        start = System.currentTimeMillis();
        LazyTrieNode root = new LazyTrieNode();
		for(int i=0;i<lijst.size();i++){
			root.insert(lijst.get(i), i);
		}
		System.err.println("Time: "+(System.currentTimeMillis()-start)+" ms");
        printMemory();
        
        log("do "+LOOKUP_TESTS+" lookup tests..");
        start = System.currentTimeMillis();
        for(int i=0;i<LOOKUP_TESTS;i++){
        	root.find(UUID.randomUUID().toString().substring(0, 4));
        }
		System.err.println("Time: "+(System.currentTimeMillis()-start)+" ms");
        log(" -- - -  --");
        log(" -- Done --");
        log(" -- - -  --");
	}
	static void printMemory(){
		int mb = 1024*1024;
        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();         
        log("##### Heap utilization statistics [MB] #####");         
        //Print used memory
        log("Used Memory:"+ (runtime.totalMemory() - runtime.freeMemory()) / mb+" MB"); 
        //Print free memory
        log("Free Memory:" + runtime.freeMemory() / mb+" MB");
        //Print total available memory
        log("Total Memory:" + runtime.totalMemory() / mb+" MB");
        //Print Maximum available memory
        log("Max Memory:" + runtime.maxMemory() / mb+" MB");
        log();
	}
	static void log(){
		log("");
	}
	static void log(String s){
		System.err.println(s);
	}
}
