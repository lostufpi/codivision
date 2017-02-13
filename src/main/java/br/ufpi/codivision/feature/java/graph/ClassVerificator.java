package br.ufpi.codivision.feature.java.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpi.codivision.feature.java.model.NodeInfo;
import br.ufpi.codivision.feature.java.model.ReferenceSet;
import br.ufpi.codivision.feature.java.util.Constants;
import br.ufpi.codivision.feature.java.model.Class;

/**
 * @author Vanderson Moura
 *
 */
public class ClassVerificator {

	/**
	 * 
	 */
	public ClassVerificator() {
	}

	/**
	 * @param nodes
	 * @param c
	 * @param referenceSet
	 * @return
	 */
	public List<NodeInfo> filterClasses(List<NodeInfo> nodes, Class c, ReferenceSet referenceSet) {
		boolean contens = false;
		NodeInfo node;
		List<NodeInfo> validReferences = new ArrayList<NodeInfo>();
		
		processReferences(nodes, referenceSet);

		//VERIFICA SE EXISTE ALGUMA REFERÊNCIA PARA UMA CLASSE DO SISTEMA QUE NÃO ESTÁ PRESENTE NOS IMPORTS
		//ISSO É NECESSÁRIO PORQUE CLASSES CONTIDAS NO MESMO PACOTE DE CLASSES REFERENCIADORAS NÃO SÃO INSERIDAS EM IMPORTS  
		for (String s : referenceSet.getReferences()) {
			//ESSA VERIFICAÇÃO É NECESSÁRIA PORQUE PODE HAVER DECLARAÇÃO DE VARIÁVEL COM O TIPO SENDO O CAMINHO DO PACOTE COMPLETO MAIS O NOME DA CLASSE.
			if(!s.contains(Constants.DOT)){ 
				for (String r : referenceSet.getImports()) {
					if(s.equals(r.substring(r.lastIndexOf(Constants.DOT)+1, r.length()))){
						contens = true;
					}
				}
				if(!contens){
					String a = c.formatPackageName().concat(Constants.FILE_SEPARATOR).concat(s).concat(Constants.JAVA_EXTENSION);
					node = searchNodeFromClassFullName(nodes, a);
					if(node != null){
						validReferences.add(node);
					}
				}else contens = false;
			}else{
				String a = s.replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.JAVA_EXTENSION);
				node = searchNodeFromClassFullName(nodes, a);
				if(node != null){
					validReferences.add(node);
				}
			}
		}
		
		//ADICIONANDO AS REFERÊNCIAS ÀS CLASSES DO SISTEMA CONTIDAS NOS IMPORTS
		for (String s : referenceSet.getImports()) {
			String r = s.replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.JAVA_EXTENSION);
			node = searchNodeFromClassFullName(nodes, r);
			if(node != null){
				validReferences.add(node);
			}
		}
		return validReferences;
	}

	/**
	 * @param nodes
	 * 
	 * @return NodeInfo CUJO O @param s ESTÁ CONTIDO NO NOME COMPLETO DA {@link Class} 
	 */
	private NodeInfo searchNodeFromClassFullName(List<NodeInfo> nodes, String s) {
		for (NodeInfo n : nodes) {
			if (n.getC().getFullname().contains(s)) {
				return n;
			}
		}
		return null;
	}
	
	/**
	 * REALIZA O PRÉ-PROCESSAMENTO DAS REFERENCIAS
	 * REFERÊNCIAS COMO "List<int[]>" SÃO SEPARADA EM "List" e "int"
	 * REFERÊNCIAS COMO "List<Classe>" SÃO SEPARADA EM "List" e "Classe" 
	 * REFERÊNCIAS COMO "List<List<Classe<T,V<R,T>>>>" SÃO SEPARADA EM "List", "List", "Classe", "T", "V", "R" e "T" 
	 * 
	 * AS REFERÊNCIAS DUPLICADAS SÃO REMOVIDAS PELO MÉTODO "removeDuplicatedReferences"
	 * 
	 * @param nodes
	 * @param referenceSet
	 */
	private void processReferences(List<NodeInfo> nodes, ReferenceSet referenceSet) {
		List<String> referencesToAdd = new ArrayList<String>();
		List<String> referencesToRemove = new ArrayList<String>();
		
		for (String r : referenceSet.getReferences()) {
			if(r.contains(Constants.DOT) || r.contains(Constants.LESS_THEN) || r.contains(Constants.OPEN_BRACKET)){
				//ESSA VERIFICAÇÃO É NECESSÁRIA PORQUE PODE HAVER DECLARAÇÃO DE VARIÁVEL COM O TIPO SENDO O CAMINHO DO PACOTE COMPLETO MAIS O NOME DA CLASSE.
				if(!isReferenceInPackagePattern(r, nodes)){
					referencesToRemove.add(r);
					//ESSA ORDEM DE VERIFICAÇÃO É IMPORTANTE, POIS O PRÓXIMO IF SÓ FUNCIONARÁ CORRETAMENTE SE NÃO EXISTIR "LESS_THEN" OU "DOT"
					if(r.contains(Constants.DOT) || r.contains(Constants.LESS_THEN)){
						referencesToAdd.addAll(processDeclarationSimbol(r));
					}else if(r.contains(Constants.OPEN_BRACKET)){
						referencesToAdd.add(r.substring(0, r.indexOf(Constants.OPEN_BRACKET)));
					}
				}
			}
		}
		referenceSet.getReferences().addAll(referencesToAdd);
		referenceSet.getReferences().removeAll(referencesToRemove);
		
		processInnerClassReferences(referenceSet);
		removeDuplicatedReferences(referenceSet);
		refineClassesSystemReferences(referenceSet, nodes);
	}
	
	/**
	 * DEVOLVE O RESULTADO DA SEPARAÇÃO DE TIPOS TAIS COMO "Classe[]", Classe<A,B>
	 * 
	 * @param declaration
	 * @return List<String> 
	 */
	private List<String> processDeclarationSimbol(String declaration) {
		List<String> splitedReferences = new ArrayList<String>();
		String[] splitedStr;
		
		splitedStr = declaration.replace(Constants.LESS_THEN, Constants.NUMBER_SIGN)
				.replace(Constants.DOT, Constants.NUMBER_SIGN)
				.replace(Constants.BIGGER_THEN, Constants.NUMBER_SIGN)
				.replace(Constants.COMMA, Constants.NUMBER_SIGN)
				.replace(Constants.WHITESPACE, Constants.NUMBER_SIGN)
				.replace(Constants.OPEN_BRACKET, Constants.NUMBER_SIGN)
				.replace(Constants.CLOSE_BRACKET, Constants.NUMBER_SIGN)
				.split(Constants.NUMBER_SIGN);

		for (int i = 0; i < splitedStr.length; i++) {
			if (!splitedStr[i].equals(Constants.EMPTY)) {
				splitedReferences.add(splitedStr[i]);
			}
		}
		return splitedReferences;
	}
	
	/**
	 * @param referenceSet
	 */
	private void removeDuplicatedReferences(ReferenceSet referenceSet){
		Map<String,String> map = new HashMap<String, String>();
		
		for (String r : referenceSet.getReferences()) {
			map.put(r, r);
		}
		referenceSet.getReferences().clear();
		referenceSet.getReferences().addAll(map.values());
	}
	
	/**
	 * REMOVE DA LISTA DE REFERÊNCIAS EM {@link ReferenceSet} TODAS AS REFERÊNCIAS DUPLICADAS
	 * 
	 * @param referenceSet
	 * @param nodes
	 */
	private void refineClassesSystemReferences(ReferenceSet referenceSet, List<NodeInfo> nodes){
		List<String> referencesToRemove = new ArrayList<String>();
		boolean contens = false;
		
		for (String s : referenceSet.getReferences()) {
			for (NodeInfo nodeInfo : nodes) {
				if(s.equals(nodeInfo.getC().formatName())){
					contens = true;
				}
			}
			if(!contens){
				referencesToRemove.add(s);
			}else contens = false;
		}
		
		referenceSet.getReferences().removeAll(referencesToRemove);
	}
	
	/**
	 * ADICIONA NA LISTA DE REFERÊNCIAS EM {@link ReferenceSet} AS REFERÊNCIAS DE CLASSES INTERNAS OU ANÔNIMAS
	 * 
	 * @param referenceSet
	 */
	private void processInnerClassReferences(ReferenceSet referenceSet){
		String innerClass = new String();
		for (String s : referenceSet.getInnerClasses()) {
			innerClass = s.substring(s.indexOf(Constants.NEW) + Constants.NEW.length(), s.indexOf(Constants.OPEN_PARENTHESE));
			innerClass = innerClass.replace(Constants.WHITESPACE, Constants.EMPTY);
			referenceSet.getReferences().add(innerClass);
		}
	}
	
	/**
	 * VERIFICA SE A REFERÊNCIA REFERE-SE À UMA DECLARAÇÃO DE VARIÁVEL COM O TIPO SENDO O CAMINHO DO PACOTE COMPLETO MAIS O NOME DA CLASSE.
	 * 
	 * @param reference
	 * @param nodes
	 * @return
	 */
	private boolean isReferenceInPackagePattern (String reference, List<NodeInfo> nodes){
		if(reference.contains(Constants.DOT)){
			String s = reference.replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.JAVA_EXTENSION);
			NodeInfo node = searchNodeFromClassFullName(nodes, s);
			if(node != null){
				return true;
			}
		}
		return false;
	}
}
