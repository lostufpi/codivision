package br.ufpi.codivision.feature.java.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import br.ufpi.codivision.core.util.Constants;
import br.ufpi.codivision.feature.java.algorithm.model.ClassMethodVisit;
import br.ufpi.codivision.feature.java.model.Attribute;
import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.Feature;
import br.ufpi.codivision.feature.java.model.FeatureClass;
import br.ufpi.codivision.feature.java.model.Method;
import br.ufpi.codivision.feature.java.model.MethodInvocation;
import br.ufpi.codivision.feature.java.model.NodeInfo;
import br.ufpi.codivision.feature.java.model.Package;
import br.ufpi.codivision.feature.java.model.Variable;

/**
 * @author Vanderson Moura
 *
 */
public class FeatureDefiner {
	
	/**
	 * 
	 */
	public FeatureDefiner() {
	}
	
	/**
	 * @param controllerPakages
	 * @param graph
	 * @return
	 */
	public List<Feature> definer(List<Package> controllerPakages, Graph<NodeInfo, DefaultEdge> graph){
		List<Feature> features = new ArrayList<>();
		List<FeatureClass> featureClasses = new ArrayList<FeatureClass>();
		
		for (Package controllerPackage : controllerPakages) {
			for (Class controllerClass : controllerPackage.getClasses()) {
				for (Method controllerMethod : controllerClass.getMethods()) {
					if(isPrimaryMethod(controllerMethod, graph)){
						String name = controllerClass.formatName().concat(Constants.DOT).concat(controllerMethod.getName());
						List<Class> classes = defineClassesToFeature(controllerClass, controllerMethod, graph);
						Feature f = new Feature(name);
						featureClasses = new ArrayList<FeatureClass>();
						for (Class c : classes) {
							FeatureClass fc = new FeatureClass();
							fc.setFeature(f);
							fc.setClass_(c);
							featureClasses.add(fc);
						}
						f.setFeatureClasses(featureClasses);
						features.add(f);
					}
				}
			}
		}
		return features;
	}
	
	/**
	 * FAZ A FILTRAGEM DE MÉTODOS MAIS IMPORTANTES DA CLASSE
	 * 
	 * @param controllerMethod
	 * @return
	 */
	public boolean isPrimaryMethod(Method controllerMethod, Graph<NodeInfo, DefaultEdge> graph){
		
		//VERIFICA SE É UM CONSTRUTOR
		if(controllerMethod.getName().equals(controllerMethod.getOwnerClass().getName())){
			return false;
		}
		//VERIFICA SE O MÉTODO NÃO FAZ CHAMADA À OUTROS MÉTODOS (DA PRÓPRIA CLASSE OU NÃO) 
		if(controllerMethod.getMethodInvocations().size() == 0){
			return false;
		}
		
		ClassMethodVisit cmv;
		boolean isCalledByOtherMethod = false;
		
		for (Method m : controllerMethod.getOwnerClass().getMethods()) {
			if(!m.equals(controllerMethod)){
				for (MethodInvocation mi : m.getMethodInvocations()) {
					cmv = buildClassMethodVisit(mi, m.getOwnerClass(), m, graph);
					if(cmv != null){
						if(cmv.getMethod().equals(controllerMethod)){
							isCalledByOtherMethod = true;
						}
					}
				}
			}
		}
		
		if(isCalledByOtherMethod){
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param controllerClass
	 * @param controllerMethod
	 * @param graph
	 * @return
	 */
	private List<Class> defineClassesToFeature (Class controllerClass, Method controllerMethod, Graph<NodeInfo, DefaultEdge> graph) {		
		List<Method> visitedMethods = new ArrayList<Method>();
		List<Class> referencesClass = new ArrayList<Class>();
		List<ClassMethodVisit> cmvList = new ArrayList<ClassMethodVisit>();
		
		cmvList.add(new ClassMethodVisit(controllerClass, controllerMethod));
		
		while(cmvList.size() > 0){
			Class sourceClass = cmvList.get(0).getC();
			Method sourceMethod = cmvList.get(0).getMethod();
			addSimpleName(referencesClass, sourceMethod, graph);
			formatAndAddMultiTypesVariables(referencesClass, sourceMethod, graph);
			
			for (MethodInvocation mi : sourceMethod.getMethodInvocations()) {
				if(!referencesClass.contains(sourceClass)){
					referencesClass.add(sourceClass);
				}
				
				ClassMethodVisit cmv = buildClassMethodVisit(mi, sourceClass, sourceMethod, graph);
				if(cmv != null){
					if(!visitedMethods.contains(cmv.getMethod())){
						visitedMethods.add(cmv.getMethod());
						cmvList.add(cmv);
					}
				}
			}
			cmvList.remove(0);
		}
		return referencesClass;	
	}
	
	/**
	 * @param mi
	 * @param sourceClass
	 * @param sourceMethod
	 * @param graph
	 * @return
	 */
	private ClassMethodVisit buildClassMethodVisit(MethodInvocation mi, Class sourceClass, Method sourceMethod, Graph<NodeInfo, DefaultEdge> graph){
		String expression = mi.getExpression();
		String type = null;
		Class targetClass = null;
		
		if(expression != null){ //SE O MÉTODO CHAMADO NÃO É INTERNO À CLASSE, OU SEJA, NÃO PRECISA SER CHAMADO POR UMA EXPRESSÃO
			if(!expression.contains(Constants.THIS)){ // SE NÃO CONTER "THIS", PODE SER UMA VARIÁVEL LOCAL
				for (Variable v : sourceMethod.getVariables()) {
					if(v.getName().equals(expression)){
						type = v.getType();
					}
				}
			}
			
			if(type == null){ //SE TYPE FOR "NULL", A VARIÁVEL PODE SER UM ATRIBUTO DA CLASSE, NÃO PRECEDIDO DE "THIS"
				for (Attribute a : sourceClass.getAttributes()) {
					if(a.getName().equals(expression.substring(expression.indexOf(Constants.DOT)+1))){
						type = a.getType();
						break;
					}
				}
			}
			
			if(type == null){ //SE NÃO FOR UM ATRIBUTO DA CLASSE OU DO MÉTODO, A EXPRESSÃO PODE REPRESENTAR UMA CLASSE (ACESSANDO MÉTODO ESTÁTICO) 
				Class expressionClass = findClassFromType(graph, sourceClass, expression);
				if(expressionClass != null){
					type = expressionClass.formatName();
				}
			}
		}
		targetClass = type == null ? sourceClass : findClassFromType(graph, sourceClass, type);
		
		if(targetClass != null){
			Method targetMethod = findTargetMethod(sourceClass, targetClass, sourceMethod, mi);
			if(targetMethod != null){
				return new ClassMethodVisit(targetClass, targetMethod);
			}
			//checar métodos @Override
		}
		return null;
	}
	
//	private void checkCalledSuperClassMethod(Class sourceClass){
//		List<Method> overrideMethods = new ArrayList<>();
//		Class superClasss = sourceClass.getSuperClass();
//		
//		if(superClasss != null){
//			for (Method method : superClasss.getMethods()) {
//			}
//		}
//	}
	
	/**
	 * @param sourceClass
	 * @param targetClass
	 * @param sourceMethod
	 * @param mi
	 * @return
	 */
	private Method findTargetMethod(Class sourceClass, Class targetClass, Method sourceMethod, MethodInvocation mi){
		Method targetMethod = null;
		boolean isEqualParam = true;
		
		for (Method m : targetClass.getMethods()) {
			if(m.getParameters().size() == mi.getArguments().size() && m.getName().equals(mi.getName())){
				isEqualParam = checkMethodParameters(mi.getArguments(), mi.getArguments().size(), sourceClass, sourceMethod, m);
				if(isEqualParam){
					targetMethod = m;
					break;
				}
			}
		}
		
		if(targetMethod == null){ //SE METHOD FOR "NULL", PODE SER QUE O MÉTODO EXISTA, PORÉM PODEM TER RECEBIDO ARGUMENTOS DO TIPO DE DADOS PRIMITIVOS E POSSUIR PARÂMETROS ABSTRATOS  
			for (Method m : targetClass.getMethods()) { 
				if(m.getParameters().size() == mi.getArguments().size() && m.getName().equals(mi.getName())){
					isEqualParam = checkMethodParametersPrimitAbst(mi.getArguments(), mi.getArguments().size(), sourceClass, sourceMethod, m);
					if(isEqualParam){
						targetMethod = m;
						break;
					}
				}
			}
		}
		
		if(targetMethod == null){ //SE METHOD FOR NULL, PODE SER QUE O MÉTODO EXISTA, PORÉM COM PARÂMETROS OPCIONAIS
			for (Method m : targetClass.getMethods()) { 
				if(m.getName().equals(mi.getName())){
					if(m.containsOptionalParam()){
						isEqualParam = checkMethodParameters(mi.getArguments(), m.getParameters().size()-1, sourceClass, sourceMethod, m);
						if(isEqualParam){
							targetMethod = m;
							break;
						}
					}
				}
			}
		}
		return targetMethod;
	}
	
	/**
	 * @param arguments
	 * @param limitSize
	 * @param sourceClass
	 * @param sourceMethod
	 * @param targetMethod
	 * @return
	 */
	private boolean checkMethodParameters(List<String> arguments, int limitSize, Class sourceClass, Method sourceMethod, Method targetMethod){
		boolean isEqualParam = true;
		List<String> argumentsTypes = new ArrayList<String>();
		argumentsTypes = getTypes(arguments, sourceClass, sourceMethod);
		
		for (int i = 0; i < limitSize; i++) {
			if(!argumentsTypes.get(i).equals(Constants.UNDEFINED)){
				String paramType = formatType(targetMethod.getParameters().get(i).getType())[0];
				String argumentType = formatType(argumentsTypes.get(i))[0];
				if(!argumentType.equals(paramType)){
					isEqualParam = false;
				}
			}
		}
		return isEqualParam;
	}
	
	/**
	 * @param arguments
	 * @param limitSize
	 * @param sourceClass
	 * @param sourceMethod
	 * @param targetMethod
	 * @return
	 */
	private boolean checkMethodParametersPrimitAbst(List<String> arguments, int limitSize, Class sourceClass, Method sourceMethod, Method targetMethod){
		boolean isEqualParam = true;
		List<String> argumentsTypes = new ArrayList<String>();
		argumentsTypes = getTypes(arguments, sourceClass, sourceMethod);
		
		for (int i = 0; i < limitSize; i++) {
			if(!argumentsTypes.get(i).equals(Constants.UNDEFINED)){
				String paramType = formatType(targetMethod.getParameters().get(i).getType())[0];
				String argumentType = formatType(argumentsTypes.get(i))[0];
				if(!checkOnlyPrimitiveAbstractType(paramType, argumentType)){
					isEqualParam = false;
				}
			}
		}
		return isEqualParam;
	}
	
	private boolean checkOnlyPrimitiveAbstractType(String paramType, String argumentType){
		if((paramType.equals(Constants.PRIMITIVE_INT) && argumentType.equals(Constants.INTEGER)) || (paramType.equals(Constants.INTEGER) && argumentType.equals(Constants.PRIMITIVE_INT))) {
			return true;
		}
		if((paramType.equals(Constants.PRIMITIVE_DOUBLE) && argumentType.equals(Constants.DOUBLE)) || (paramType.equals(Constants.DOUBLE) && argumentType.equals(Constants.PRIMITIVE_DOUBLE))) {
			return true;
		}
		if((paramType.equals(Constants.PRIMITIVE_BOOLEAN) && argumentType.equals(Constants.BOOLEAN)) || (paramType.equals(Constants.BOOLEAN) && argumentType.equals(Constants.PRIMITIVE_BOOLEAN))) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param arguments
	 * @param sourceClass
	 * @param sourceMethod
	 * @return
	 */
	private List<String> getTypes (List<String> arguments, Class sourceClass, Method sourceMethod){
		List<String> types = new ArrayList<String>();
		
		for (String argument : arguments) {
			int oldSizeTypes = types.size();
			if(argument.contains(Constants.DOUBLE_QUOTE)){
				types.add(Constants.STRING_TYPE);
			}else if(argument.contains(Constants.THIS)){
				for (Attribute attribute : sourceClass.getAttributes()) {
					if(attribute.getName().equals(argument)){
						types.add(attribute.getType());
						break;
					}
				}
			}else{
				if(argument.contains(Constants.SINGLE_QUOTE)){
					types.add(Constants.CHAR_TYPE);
				}else if(argument.contains(Constants.DOT)){
					Double d = null;
					try {
						d = Double.valueOf(argument.substring(0,argument.indexOf(Constants.DOT)));
					} catch (Exception e) {}
					
					if(d != null){
						types.add(Constants.PRIMITIVE_DOUBLE);
					}
				}else{
					//SE ESTÁ SENDO PASSADO COMO ARGUMENTO UMA INSTANCIAÇÃO DE UM OBJETO (EX.: new Object(), O TIPO SERÁ ENTÃO Object). O ESPAÇO APÓS O 'new' É NECESSÁRIO PARA NÃO CAPTURAR ARGUMENTOS COM SUBSTRING 'new'
					if(argument.contains(Constants.NEW.concat(Constants.WHITESPACE))){ 
						types.add(argument.substring(argument.indexOf(Constants.NEW) + Constants.NEW.length() + 1, argument.indexOf(Constants.OPEN_PARENTHESE)));
					}else if(!argument.contains(Constants.OPEN_KEY)){
						Integer i = null;
						try {
							i = Integer.valueOf(argument);
						} catch (Exception e) {}
						
						if(i != null){
							types.add(Constants.PRIMITIVE_INT); // CONSIDERANDO QUE É DO TIPO 'int'
						}else{
							boolean isMethodVariable = false;
							for (Variable variable : sourceMethod.getVariables()) { 
								if(variable.getName().equals(argument)){ // VERIFICA SE É DO TIPO DE ALGUMA VARIÁVEL DO MÉTODO
									types.add(variable.getType());
									isMethodVariable = true;
									break;
								}
							}
							if(!isMethodVariable){
								for (Attribute attribute : sourceClass.getAttributes()) {
									if(attribute.getName().equals(argument)){
										types.add(attribute.getType());
										break;
									}
								}
							}
						}
					}
				}
			}
			
			if(oldSizeTypes == types.size()){
				types.add(Constants.UNDEFINED);
			}
		}
		return types;
	}
	
	/**
	 * 
	 * INCLUI À LISTA DE CLASSES REFERENCIADAS TODOS OS SIMPLE NAMES IDENTIFICADOS NO 
	 * MÉTODO INCLUINDO PARAMETROS, RETORNO, ENUMNS E CLASSES COM ACESSO DIRETO A ATRIBUTOS 
	 * ESTÁTICOS (QUE NÃO É CARACTERIZADO COMO UM MÉTODO INVOCATION).
	 *      
	 * @param referencesClass
	 * @param method
	 * @param graph
	 *
	 */
	private void addSimpleName(List<Class> referencesClass, Method method, Graph<NodeInfo, DefaultEdge> graph){
		for (String simpleName : method.getSimpleNames()) {
			Class c = findClassFromType(graph, method.getOwnerClass(), simpleName);
			if(c != null){
				if(!referencesClass.contains(c)){
					referencesClass.add(c);
				}
			}
		}
	}
	
	/**
	 * 
	 * MÉTODO PARA ADICIONAR REFERENCIAS DE VARIAVÉIS COM TIPO COMPOSTO. 
	 * EX.: List<Class>, Class<Method, Attribute> -> SÃO ADICIONADOS Class, Method e Attribute.
	 * 
	 * @param referencesClass
	 * @param sourceMethod
	 * @param graph
	 */
	private void formatAndAddMultiTypesVariables (List<Class> referencesClass, Method sourceMethod,  Graph<NodeInfo, DefaultEdge> graph){
		String type = null;
		for (String simpleName : sourceMethod.getSimpleNames()) {
			for (Variable variable : sourceMethod.getVariables()) {
				if(variable.getName().equals(simpleName)){
					type = variable.getType();
					break;
				}
			}
			if(type == null){
				for (Attribute attribute : sourceMethod.getOwnerClass().getAttributes()) {
					if(attribute.getName().equals(simpleName)){
						type = attribute.getType();
						break;
					}
				}
			}
			String[] types = type != null ? formatType(type) : null;
			if(types != null){
				for (String t : types) {
					Class c = findClassFromType(graph, sourceMethod.getOwnerClass(), t);
					if(c != null){
						if(!referencesClass.contains(c)){
							referencesClass.add(c);
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param graph
	 * @param classSource
	 * @param type
	 * @return
	 */
	private Class findClassFromType (Graph<NodeInfo, DefaultEdge> graph, Class classSource, String type){
		NodeInfo ni = new NodeInfo(new Class());
	
		for (Iterator<NodeInfo> iterator = graph.vertexSet().iterator(); iterator.hasNext();) {
			NodeInfo node = iterator.next();
			if(node.getC().equals(classSource)){
				ni = node;
			}	
		}
		type = formatType(type)[0];
		
		for (Iterator<DefaultEdge> i =  graph.edgesOf(ni).iterator(); i.hasNext();) {
			DefaultEdge de = (DefaultEdge) i.next();
			if(type.equals(graph.getEdgeTarget(de).getC().formatName())){
				return graph.getEdgeTarget(de).getC();
			}
		}
		return null;
	}
	
	/**
	 * @param type
	 * @return
	 */
	private String[] formatType(String type){
		String[] splitedStr;
		splitedStr = type.replace(Constants.LESS_THEN, Constants.NUMBER_SIGN)
				.replace(Constants.DOT, Constants.NUMBER_SIGN)
				.replace(Constants.BIGGER_THEN, Constants.NUMBER_SIGN)
				.replace(Constants.COMMA, Constants.NUMBER_SIGN)
				.replace(Constants.WHITESPACE, Constants.NUMBER_SIGN)
				.replace(Constants.OPEN_BRACKET, Constants.NUMBER_SIGN)
				.replace(Constants.CLOSE_BRACKET, Constants.NUMBER_SIGN)
				.split(Constants.NUMBER_SIGN);
		return splitedStr;
	}
}
