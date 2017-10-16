package br.ufpi.codivision.debit;

import java.util.ArrayList;
import java.util.List;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.codesmell.CodeSmellFactory;
import br.ufpi.codivision.debit.codesmell.CodeSmellID;
import br.ufpi.codivision.debit.codesmell.ICodeSmell;
import br.ufpi.codivision.debit.mining.CodeAnalysisProcessor;
import br.ufpi.codivision.debit.mining.RepositoryMiner;
import br.ufpi.codivision.debit.model.File;
import br.ufpi.codivision.debit.parser.java.JavaParser;

public class Teste {

	public static void main(String[] args) {
		
		
		
		CodeAnalysisProcessor processor = new CodeAnalysisProcessor();
		File processFile = processor.processFile("package br.ufpi.codivision.debit;\r\n" + 
				"\r\n" + 
				"public class Emxemplo {\r\n" + 
				"	\r\n" + 
				"	public void teste() {\r\n" + 
				"		int[] array = new int[10];\r\n" + 
				"		for (int i = 0; i < array.length; i++) {\r\n" + 
				"			break;\r\n //TODO" + 
				"			for (int j = 0; j < array.length; j++) {\r\n" + 
				"				for (int j2 = 0;//FIXME j2 < array.length; j2++) {\r\n" + 
				"					for (int k = 0; //TODO k < array.length; k++) {\r\n" + 
				"						for //XXX (int k2 = 0; k2 < array.length; k2++) {\r\n" + 
				"							\r\n" + 
				"						}\r\n" + 
				"					//TODO}\r\n" + 
				"				}\r\n" + 
				"			}\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"}\r\n" + 
				"");
		System.out.println(processFile);
	
	}

}
