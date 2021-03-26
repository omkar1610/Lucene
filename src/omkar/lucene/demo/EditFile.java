package omkar.lucene.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;

public class EditFile {
	public static Date start = new Date();
	public static Date initTime = new Date();
	public static Integer count = 0;
	
	public static void main(String[] args) throws IOException {
//		saveFileForEachId();
//		createMergedFile();
//		System.out.println(new String(Files.readAllBytes(Paths.get("context_for_each_id/0.txt")), StandardCharsets.UTF_8));
//		readFile();
		System.out.println("Hello");
	}
	
	public static void readFile() throws IOException {
		ArrayList<ArrayList<Integer>> tmp = new ArrayList<ArrayList<Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(new File("merged_file.txt")));
		String line;  
		Integer count = 0;
		while((line=br.readLine())!=null) {
//			System.out.println(count + " - " + line.split(",", 2)[0] + " - " + line.split(",", 2)[1].length());
			count++;
			ArrayList<Integer> t = new ArrayList<Integer>();
			t.add(Integer.parseInt(line.split(",", 2)[0]));
			t.add(line.split(",", 2)[1].length());
			tmp.add(t);
		}
		System.out.println(tmp);
	}
	
	public static void saveFileForEachId() throws IOException {
		File file = new File("new.csv");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;  
		Integer count = 1;
		Date init = new Date();
		Date start = new Date();
		
		while((line=br.readLine())!=null) {
			Date end = new Date();
			if((end.getTime()-start.getTime())>1000) {
				System.out.println("Time Passed: " + (end.getTime()-init.getTime())/1000 + " secs, Line Written: " + count);
				start = new Date();
			}
			count++;
//			System.out.println(line);
			String[] arrSplit = line.split(",", 2);
			String id = arrSplit[0];
			String context = arrSplit[1];
			BufferedWriter writer = new BufferedWriter(new FileWriter("context_for_each_id/"+id+".txt", true));  
			writer.append(context+" ");
			writer.close();
		}
		br.close();
		System.out.println("Finished!");
	}
	
	public static void createMergedFile() throws IOException {
		final Path docsPath = Paths.get("context_for_each_id");
		BufferedWriter writer = new BufferedWriter(new FileWriter("merged_file.txt", true));  
		
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
					String id = file.getFileName().toString().split("\\.",2)[0];
					String context = new String(Files.readAllBytes(Paths.get(file.getParent().toString() + "/" + file.getFileName().toString())), StandardCharsets.UTF_8);
//					System.out.println(file.getFileName().toString().split("\\.",2)[0]);
					writer.append(id+","+context+"\n");
					return FileVisitResult.CONTINUE;
				}
			});
		}
		writer.close();
		System.out.println("Finished!");
	}

}

