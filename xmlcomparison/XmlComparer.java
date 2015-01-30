package utilities.xmlcomparison;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlComparer {
	
	// tree friendly representation
	// current version doesn't take attributes into account
	static class MyNode implements Comparable<MyNode>{
		final Node original;
		ArrayList<MyNode> children;
		private String textValue;
		private boolean checked;
		private int depth;
		public MyNode(Node node) {
			this(node,0);//root
		}
		public MyNode(Node node, int depth) {
			this.children = new ArrayList<MyNode>();
			this.original = node;
			this.textValue = null;
			this.checked = false;
			this.depth = depth;
			initParse();
		}
		private void initParse(){
			if(!this.original.hasChildNodes())return;
			NodeList nl = this.original.getChildNodes();
			for (int count = 0; count < nl.getLength(); count++) {
				if (nl.item(count).getNodeType() == Node.ELEMENT_NODE){
					this.children.add(new MyNode(nl.item(count),this.depth+1));					
				}else if(nl.item(count).getNodeType() == Node.TEXT_NODE){
					this.textValue = nl.item(count).getNodeValue();
				}
			}
			Collections.sort(this.children);
		}
		@Override
		public int compareTo(MyNode oth) {
			if(this.original.getNodeName().equals(oth.original.getNodeName())){
				if(IsNullOrWhiteSpace(this.textValue) ^ IsNullOrWhiteSpace(oth.textValue)){
					return IsNullOrWhiteSpace(this.textValue) ? -1 : 1;
				}
				if(IsNullOrWhiteSpace(this.textValue)){
					int maxChilds = Math.min(this.children.size(), oth.children.size());
					for(int i=0;i<maxChilds;i++){
						int cmp = this.children.get(i).compareTo(oth.children.get(i));
						if(cmp!=0)return cmp;
					}
					return Integer.signum(this.children.size()-oth.children.size());
				}
				return this.textValue.compareTo(oth.textValue);
			}
			return this.original.getNodeName().compareTo(oth.original.getNodeName());		
		}
		private static boolean IsNullOrWhiteSpace(String s){
			if(s==null)return true;
			return s.trim().isEmpty();
		}
		private static void printSpaces(int count){
			if(count<=0)return;
			System.err.print(" ");
			printSpaces(count-1);
		}
		@Override
		public String toString() {
			return "attr name : " + original.getNodeName() + 
					(IsNullOrWhiteSpace(this.textValue) ? "" : "\r\n" + "attr value : " + this.textValue);
		}
		public void printUnchecked(){
			if(this.checked)return;
			printSpaces(this.depth);
			System.err.println("<"+original.getNodeName()+">");
			for(MyNode chld : children)
				chld.printUnchecked();
			if(!IsNullOrWhiteSpace(this.textValue)){
				printSpaces(this.depth+1);
				System.err.println(this.textValue);
			}
			printSpaces(this.depth);
			System.err.println("</"+original.getNodeName()+">");
		}
	}
	
	public static void main(String[] args) throws Exception{
		String filename1,filename2; 
		if(args.length==2){
			filename1 = args[0];
			filename2 = args[1];
		}else{
			filename1 = System.getProperty("user.dir")+"\\A.xml";
			filename2 = System.getProperty("user.dir")+"\\B.xml";
		}
		System.err.println("working directory: "+System.getProperty("user.dir"));
		File fileA = new File(filename1),
			fileB = new File(filename2);
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		MyNode A = new MyNode(dBuilder.parse(fileA)),
			B = new MyNode(dBuilder.parse(fileB));
		check(A,B);
		if(A.checked && B.checked){
			System.err.println("XML IS EXACT THE SAME");
		}else{
			System.err.println("Incompatible nodes: ");
			A.printUnchecked();
			System.err.println("-----");
			B.printUnchecked();
		}
	}
	static void check(MyNode A, MyNode B){
		if(A.compareTo(B) == 0){
			A.checked = B.checked = true;
			return;
		}
		int a = 0,
			b = 0;
		while(a < A.children.size() && b < B.children.size()){
			check(A.children.get(a++),B.children.get(b++));
		}
	}	
}
