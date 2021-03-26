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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap; 

public class CreateIdContextFile {

	public static Date start = new Date();
	public static Date initTime = new Date();
	public static Integer count = 0;
	
	public static void main(String[] args) throws IOException {
		FileWriter csvWriter = new FileWriter("new.csv");
		getFiles("/media/omkar/Data/develop/Dissertation-2020/data/citeseerx-partial-papers", csvWriter);
		csvWriter.close();
		System.out.println("Finished!");
	}
	public static void getFiles(String path, FileWriter csvWriter) throws IOException {
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
					readXml(file.getParent().toString() + "/" + file.getFileName().toString(), csvWriter);
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
	
	public static void readXml(String path, FileWriter csvWriter) {
		try {
			File input = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.parse(input);
			
			doc.getDocumentElement().normalize();
			getCitations(doc, csvWriter);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getCitations(Document doc, FileWriter csvWriter) throws NumberFormatException, IOException {
		NodeList nList = doc.getElementsByTagName("citation");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			
			if(nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
//				String raw = eElement.getElementsByTagName("raw").item(0).getTextContent();
				String context = eElement.getElementsByTagName("contexts").item(0).getTextContent().trim().replaceAll("\n", "");
				String clusterId = eElement.getElementsByTagName("clusterid").item(0).getTextContent().trim().replaceAll("\n", "");
				if(clusterId.length()>0 && context.length()>0 && !context.equals("None")) {
					writeFile(clusterId, context, csvWriter);
				}
			}
		}
		
	}

	public static void writeFile(String clusterId, String context, FileWriter csvWriter) throws IOException {
		csvWriter.flush();
		csvWriter.append(clusterId.toString());
		csvWriter.append(",");
		csvWriter.append(context);
		csvWriter.append("\n");
		csvWriter.flush();
	}
}
