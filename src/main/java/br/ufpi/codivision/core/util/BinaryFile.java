package br.ufpi.codivision.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BinaryFile {

	public static File writeObject(Object object, String path) throws IOException {
		File binaryFile = new File(path + ".dat");

		FileOutputStream fileOutputStream = new FileOutputStream(binaryFile);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

		objectOutputStream.writeObject(object);

		objectOutputStream.flush();
		objectOutputStream.close();
		fileOutputStream.close();

		return binaryFile;
	}

	public static Object readObject(InputStream file) throws IOException, ClassNotFoundException, java.io.StreamCorruptedException {

		
		ObjectInputStream objectInputStream = new ObjectInputStream(file);

		Object object = objectInputStream.readObject();

		
		objectInputStream.close();

		return object;
	}
}
