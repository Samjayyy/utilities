package utilities.xmlcomparison;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sammie
 * 
 * XmlComparer compares two scrambled XMLs and tries to check if they are equal
 * It prints out all nodes which couldn't be matched, or have an unmatching child.
 * 
 * Notes:
 * - In this version attributes are not being taken into account (did not need it so far)
 * - 2 almost equal nodes from both xmls may appear as totally unmatching 
 * 		because there are other nodes added/missing in one of the xmls.
 * 		This may cause the comparer to end up in comparing them with other nodes.
 */
public class XmlComparer {
	
	private static final PrintStream Out = System.out;
	private static final String FILENAMES = "-FILES";

	// tree friendly representation
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
					if(IsNullOrWhiteSpace(nl.item(count).getNodeValue()))continue;
					if(IsNullOrWhiteSpace(this.textValue)){
						this.textValue = nl.item(count).getNodeValue().trim();						
					}else{
						this.textValue += nl.item(count).getNodeValue().trim();
					}
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
		@Override
		public String toString() {
			return "attr name : " + original.getNodeName() + 
					(IsNullOrWhiteSpace(this.textValue) ? "" : "\r\n" + "attr value : " + this.textValue);
		}
		public void printUnchecked(){
			if(this.checked)return;
			printSpaces(this.depth);
			Out.println("<"+original.getNodeName()+">");
			for(MyNode chld : children)
				chld.printUnchecked();
			if(!IsNullOrWhiteSpace(this.textValue)){
				printSpaces(this.depth+1);
				Out.println(this.textValue);
			}
			printSpaces(this.depth);
			Out.println("</"+original.getNodeName()+">");
		}
	}
	
	public static void main(String[] args) throws Exception{
		// default config
		String filename1 = System.getProperty("user.dir")+"\\A.xml",
				filename2 = System.getProperty("user.dir")+"\\B.xml";		
		for(int i=0;i<args.length;i++){
			if(FILENAMES.equalsIgnoreCase(args[i])){
				if(i+2>=args.length){
					Out.println("Passing the files argument is expected to be followed by the two xmls");
					Out.println("e.g. xmlcompare -files A.xml B.xml");
				}
				filename1 = args[++i];
				filename2 = args[++i];
			}else{
				Out.println("-- UNKNOWN PARAMETER: "+args[i]+" --");
			}
		}
		Out.println("working directory: "+System.getProperty("user.dir"));
		FileInputStream fileA = new FileInputStream(filename1),
			fileB = new FileInputStream(filename2);
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		MyNode A = new MyNode(dBuilder.parse(fileA)),
			B = new MyNode(dBuilder.parse(fileB));
		checkBestMatch(A,B);
		if(A.checked && B.checked){
			Out.println("XML IS EXACT THE SAME");
		}else{
			Out.println("Incompatible nodes: ");
			A.printUnchecked();
			Out.println("-----");
			B.printUnchecked();
		}
	}
	private static void checkBestMatch(MyNode A, MyNode B){
		if(A.checked || B.checked)
			throw new AssertionError("A and B should still be in stat to check");
		if(A.compareTo(B) == 0){
			A.checked = B.checked = true;
			return;
		}
		ArrayList<MyNode> aTodo = new ArrayList<MyNode>(A.children),
				bTodo = new ArrayList<MyNode>(B.children);
		matchExact(aTodo, bTodo);
		int a = 0,
			b = 0;
		while(a < aTodo.size() && b < bTodo.size()){
			checkBestMatch(aTodo.get(a++),bTodo.get(b++));
		}		
	}
	
	private static void matchExact(ArrayList<MyNode> aList, ArrayList<MyNode> bList){
		Iterator<MyNode> aIt = aList.iterator();
		while(aIt.hasNext()){
			MyNode a = aIt.next();
			Iterator<MyNode> bIt = bList.iterator();
			while(bIt.hasNext()){
				MyNode b = bIt.next();
				int cmp = a.compareTo(b);
				if(cmp == 0){
					a.checked=b.checked=true;
					aIt.remove();bIt.remove();
				}
				// we assume sorting is correct and no other b will be equal to a
				// from the moment that b is bigger than a. (since we will only encounter even bigger b's)
				if(cmp >= 0)break; 
			}
		}
	}
	private static boolean IsNullOrWhiteSpace(String s){
		if(s==null)return true;
		return s.trim().isEmpty();
	}
	private static void printSpaces(int count){
		if(count<=0)return;
		Out.print(" ");
		printSpaces(count-1);
	}
}
