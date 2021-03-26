package omkar.lucene.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class IndexFiles {
	private IndexFiles() {}
	
	public static void main(String[] args) {
		
		String indexPath = "D:\\develop\\eclipse-workspace\\Lucene\\files\\LuceneSourceIndex\\";
//		String docsPath = "D:\\develop\\eclipse-workspace\\Lucene\\files\\toy_indexing_files\\";
		String docsPath = "D:\\develop\\lucene-8.8.1\\";
		boolean create = true;
		startIndexing(indexPath, docsPath, create);
	}
	
	public static void startIndexing(String indexPath, String docsPath, boolean create) {
		final Path docDir = Paths.get(docsPath);
		System.out.println("Starting the Code...");
		
		Date start = new Date();
		try {
			System.out.println("Indexing to directory " + indexPath);
			
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			
			if(create) {
				iwc.setOpenMode(OpenMode.CREATE);
			}else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			
//			iwc.setRAMBufferSizeMB(256.0);
			
			IndexWriter writer = new IndexWriter(dir, iwc);
			IndexDocs(writer, docDir);
			
//			writer.forceMerge(1);
			
			writer.close();
			
			Date end = new Date();
			System.out.print("Finished. Time Taken: ");
			System.out.println(end.getTime() - start.getTime() + " ms");
			
		} catch (IOException e) {
			System.out.println("caught IOException");
		}
	}
	
	static void IndexDocs(final IndexWriter writer, Path docsPath) throws IOException{
//		If it's a directory then walk each file
		if(Files.isDirectory(docsPath)) {
			System.out.println(docsPath + "is a Directory");
			Files.walkFileTree(docsPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					}catch(IOException ignore) {
						
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}else {
			indexDoc(writer, docsPath, Files.getLastModifiedTime(docsPath).toMillis());
		}
	}
	
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException{
		try(InputStream stream = Files.newInputStream(file)){
			
			Document doc = new Document();
			
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);
			doc.add(new LongPoint("modified", lastModified));
			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
			
			if(writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				System.out.println("adding " + file);
				writer.addDocument(doc);
			}else {
				System.out.println("updating" + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
			
		}
	}
}



