package br.ufpi.codivision.feature.java.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufpi.codivision.feature.java.model.Class;

/**
 * @author Vanderson Moura
 *
 */
public class ReadClassFiles {
	
	public static List<Class> readFiles(String directory) {
		List<Class> classes = new ArrayList<>();
		File files[];
		File dir = new File(directory);
		files = dir.listFiles();
		StringBuffer sb;
		
		for (int i = 0; i < files.length; i++) {
			try {
				sb = new StringBuffer();
				FileReader reader = new FileReader(files[i]);
				int c;
				do {
					c = reader.read();
					if (c != -1) {
						sb.append((char) c);
					}
				} while (c != -1);
				reader.close();
				sb.toString();
				String name = files[i].getName().substring(0, files[i].getName().lastIndexOf(Constants.DOT));
				name = name.replace(Constants.DOT, Constants.FILE_SEPARATOR);
				name = name.concat(files[i].getName().substring(files[i].getName().lastIndexOf(Constants.DOT), files[i].getName().length()));			
				classes.add(new Class(name, sb.toString(), new String()));

			} catch (IOException e) {
				System.out.println("Error!");
			}
		}
		return classes;
	}
}
