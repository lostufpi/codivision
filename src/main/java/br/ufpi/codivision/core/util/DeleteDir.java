package br.ufpi.codivision.core.util;

import java.io.File;

public class DeleteDir {
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) { 
               boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                	//System.out.println(children[i]);
                    return false;
                }
            }
        }
    
        // Agora o diretório está vazio, restando apenas deletá-lo.
        return dir.delete();
    }
}