package omkar.lucene.demo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap; 

public class XmlReader {
	public static HashMap<Integer, ArrayList<String>> clusteridToContext = new HashMap<Integer, ArrayList<String>>();
	public static Date start = new Date();
	public static Date initTime = new Date();
	public static Integer count = 0;
	
	public static void main(String[] args) throws IOException {
		FileWriter csvWriter = new FileWriter("new.csv");
//		getFiles("data", csvWriter);
		getFiles("/media/omkar/Data/develop/Dissertation-2020/data/citeseerx-partial-papers", csvWriter);

		csvWriter.close();
		for(Integer i: clusteridToContext.keySet()) {
			System.out.println(i);
			System.out.println(clusteridToContext.get(i));
		}
	}
	public static void getFiles(String path, FileWriter csvWriter) throws IOException {
//		final Path docsPath = Paths.get("/media/omkar/Data/develop/Dissertation-2020/data/citeseerx-partial-papers");
		final Path docsPath = Paths.get(path);
		
		if(Files.isDirectory(docsPath)) {
			System.out.println(docsPath + "is a Directory");
			Files.walkFileTree(docsPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
					Date tmp = new Date();
					count++;
					if((tmp.getTime()-start.getTime())>1000) {
						System.out.println("Time Passed: " + (tmp.getTime()-initTime.getTime())/1000 + " secs, File Parsed: " + count);
						start = new Date();
					}
//					System.out.println(file.getParent().toString() + "/" + file.getFileName().toString());
					readXml(file.getParent().toString() + "/" + file.getFileName().toString(), csvWriter);
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
	
	public static void readXml(String path, FileWriter csvWriter) {
		try {
//			File input = new File("data.xml");
			File input = new File(path);
//			System.out.println("File Abs Path: " + input.getAbsolutePath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.parse(input);
			
			doc.getDocumentElement().normalize();
//			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
//			getMetaData(doc, csvWriter);
			getCitations(doc, csvWriter);
//			for(Integer id: clusteridToContext.keySet()) {
//				System.out.println(id);
//				for(String context: clusteridToContext.get(id)) {
//					System.out.println(context);
//				}
//			}
//			System.out.println(clusteridToContext);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getMetaData(Document doc, FileWriter csvWriter) {
		String title = doc.getElementsByTagName("title").item(0).getTextContent();
		String venue = doc.getElementsByTagName("venue").item(0).getTextContent();
		String year = doc.getElementsByTagName("year").item(0).getTextContent();
		String key = doc.getElementsByTagName("key").item(0).getTextContent();
		String doi = doc.getElementsByTagName("doi").item(0).getTextContent();
		String paperAbstract = doc.getElementsByTagName("abstract").item(0).getTextContent();
		
		System.out.println("MetaData");
		System.out.println("-----------------------------------------------");
		System.out.println("Title: " + title +
							"\nVenue: " + venue +
							"\nYear: " + year +
							"\nKey: " + key +
							"\nDOI: " + doi +
							"\nAbstract: " + paperAbstract);

		NodeList nList = doc.getElementsByTagName("author");
		
		System.out.println("Current Element :" + nList.item(0).getNodeName());
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			
			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				System.out.println("\t" + (i+1) + ": "+ eElement.getTextContent());
			}
		}

		System.out.println("-----------------------------------------------");
	}
	
	public static void getCitations(Document doc, FileWriter csvWriter) throws NumberFormatException, IOException {
		NodeList nList = doc.getElementsByTagName("citation");
//		System.out.println("Citations");
//		System.out.println("-----------------------------------------------");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
//			System.out.println("\nCurrent Element :" + nNode.getNodeName());
			
			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String raw = eElement.getElementsByTagName("raw").item(0).getTextContent();
				String context = eElement.getElementsByTagName("contexts").item(0).getTextContent();
				String clusterId = eElement.getElementsByTagName("clusterid").item(0).getTextContent();
//				System.out.println("Raw: " + raw);
//				System.out.println("Context: " + context);
//				System.out.println("ClusterID: " + clusterId);
//				System.out.println("-----------------------------------------------");
				mergeCitation(Integer.parseInt(clusterId), context, csvWriter);
			}
		}
		
	}

	public static void mergeCitation(Integer clusterId, String context, FileWriter csvWriter) throws IOException {
		csvWriter.flush();
		csvWriter.append(clusterId.toString());
		csvWriter.append(",");
		csvWriter.append(context);
		csvWriter.append("\n");
		csvWriter.flush();
//		if(!context.equals("None")) {
//			if(clusteridToContext.get(clusterId)!=null) {
//				clusteridToContext.get(clusterId).add(context);
//			}
//			else {
//				ArrayList<String> tmp = new ArrayList<String>();
//				tmp.add(context);
//				clusteridToContext.put(clusterId, tmp);
//			}
//		}
	}
}
