package omkar.lucene.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

public class TryDocumentClas {
	public static void main(String[] args) {
		String line = "Hi, I love the PIzza + I love whatever u@is going";
		Document doc = new Document();
		TextField txt = new TextField("field1", new StringReader(line));
		doc.add(txt);
		for(IndexableField f: doc.getFields()) {
			System.out.println(f.name() + f.toString());
		}
	}
}
