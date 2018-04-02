package br.ufpi.codivision.core.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializable {
	public static void serialize(Object object) {
		try {
			FileOutputStream output = new FileOutputStream( System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(object.getClass().getSimpleName().concat(".dat")));
			ObjectOutputStream recorder = new ObjectOutputStream(output);
			recorder.writeObject(object);
			recorder.flush();
			recorder.close();
			output.flush();
	    	output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Object deserialize(@SuppressWarnings("rawtypes") Class c) {
		FileInputStream input;
		try {
			input = new FileInputStream(System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(c.getSimpleName().concat(".dat")));
			ObjectInputStream reader = new ObjectInputStream(input);
			Object object = reader.readObject();
			reader.close();
			input.close();
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
