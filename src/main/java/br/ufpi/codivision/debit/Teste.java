package br.ufpi.codivision.debit;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.codesmell.BrainClass;
import br.ufpi.codivision.debit.codesmell.BrainMethod;
import br.ufpi.codivision.debit.codesmell.CodeSmellFactory;
import br.ufpi.codivision.debit.codesmell.CodeSmellID;
import br.ufpi.codivision.debit.codesmell.ICodeSmell;
import br.ufpi.codivision.debit.mining.CodeAnalysisProcessor;
import br.ufpi.codivision.debit.mining.RepositoryMiner;
import br.ufpi.codivision.debit.parser.java.JavaParser;

public class Teste {

	public static void main(String[] args) {
		
		JavaParser parser = new JavaParser();
		AST generate = parser.generate("teste", "package br.ufpi.codivision.debit;\r\n" + 
				"\r\n" + 
				"public class Emxemplo {\r\n" + 
				"	\r\n" + 
				"	public void teste() {\r\n" + 
				"		int[] array = new int[10];\r\n" + 
				"		for (int i = 0; i < array.length; i++) {\r\n" + 
				"			break;\r\n" + 
				"			for (int j = 0; j < array.length; j++) {\r\n" + 
				"				for (int j2 = 0; j2 < array.length; j2++) {\r\n" + 
				"					for (int k = 0; k < array.length; k++) {\r\n" + 
				"						for (int k2 = 0; k2 < array.length; k2++) {\r\n" + 
				"							\r\n" + 
				"						}\r\n" + 
				"					}\r\n" + 
				"				}\r\n" + 
				"			}\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"}\r\n" + 
				"");
		RepositoryMiner repositoryMiner = new RepositoryMiner();
		List<ICodeSmell> codeSmells = new ArrayList<>();
		
		for(CodeSmellID code: CodeSmellID.values()) {
			codeSmells.add(CodeSmellFactory.getCodeSmell(code));
		}
		
		repositoryMiner.setCodeSmells(codeSmells);
		
		
		CodeAnalysisProcessor processor = new CodeAnalysisProcessor(repositoryMiner);
		Document processFile = processor.processFile("teste", generate);
		System.out.println(processFile);
	
	}

}
