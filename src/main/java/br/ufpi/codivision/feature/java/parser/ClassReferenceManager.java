package br.ufpi.codivision.feature.java.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import br.ufpi.codivision.feature.java.model.Attribute;
import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.Method;
import br.ufpi.codivision.feature.java.model.Parameter;
import br.ufpi.codivision.feature.java.model.ReferenceSet;
import br.ufpi.codivision.feature.java.model.Variable;
import br.ufpi.codivision.feature.java.util.Constants;

/**
 * @author Vanderson Moura
 *
 */
public class ClassReferenceManager {
	
	/**
	 * 
	 */
	public ClassReferenceManager() {
	}
	
	/**
	 * @param content
	 * @return
	 */
	private CompilationUnit configure(String content){
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(content.toCharArray());
		return (CompilationUnit) parser.createAST(null); 
	}
	
	/**
	 * @param c
	 * @return
	 */
	public ReferenceSet references (Class c) {
		final ReferenceSet referenceSet = new ReferenceSet();
		final List<Method> methods = new ArrayList<Method>();
		final List<Attribute> attributes = new ArrayList<Attribute>();
		final Class extendClass = new Class();
		final CompilationUnit unit = configure(c.getContent());
		final Class ownerClass = c;
		
		unit.accept(new ASTVisitor() {
			/** VISITA OS IMPORTS DA CLASSE**/
			@Override
			public boolean visit(ImportDeclaration node) {
				referenceSet.getImports().add(node.getName().toString());
				return true; 
			}
			
			/** VISITA AS VARIÁVEIS PASSADAS COMO PARÂMETRO EM MÉTODOS DA CLASSE, INCLUÍNDO O CONSTRUTOR **/

			@Override
			public boolean visit(SingleVariableDeclaration node) {
				referenceSet.getReferences().add(node.getType().toString());
				return true; 
			}
			
			/** VISITA AS VARIÁVEIS DECLARADAS EM MÉTODOS DA CLASSE, INCLUINDO O CONSTRUTOR **/
			@Override
			public boolean visit(VariableDeclarationStatement node) {
				referenceSet.getReferences().add(node.getType().toString());
				return true; 
			}
			
			/** VISITA OS ATRIBUTOS DA CLASSE**/
			@Override
			public boolean visit(FieldDeclaration node) {
				String type = node.getType().toString();
				String name;
				for (Object o : node.fragments()){
					name = o.toString();
					if(name.contains(Constants.EQUAL)){
						name = name.substring(0,name.indexOf(Constants.EQUAL)); 
					}
					attributes.add(new Attribute(type, name));
				}
				referenceSet.getReferences().add(type);
				return true; 
			}
			
			/** VISITA AS CLASSES ANÔNIMAS**/
			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				referenceSet.getInnerClasses().add(node.getParent().toString());
				return true; 
			}
			
			/** VISITA A SUPER CLASSE NA RELAÇÃO DE HERANÇA**/
			@Override
			public boolean visit(CompilationUnit node) {
				 for (Object type : unit.types()){
			        TypeDeclaration typeDec = (TypeDeclaration) type;
			        Type superClassType = typeDec.getSuperclassType();
			        if(superClassType != null){
			        	String name = superClassType.toString();
			        	referenceSet.getReferences().add(name);
			        	if(name.contains(Constants.LESS_THEN)){
			        		extendClass.setName(name.substring(0,name.indexOf(Constants.LESS_THEN)).concat(Constants.JAVA_EXTENSION));
			        	}
			        }
				 }
				return true; 
			}
			
			/** VISITA OS MÉTODOS DECLARADOS NA CLASSE **/
			@Override
			public boolean visit(MethodDeclaration node) {
				String name = node.getName().getIdentifier();
				String content = node.toString();
				String body = node.getBody() != null ? node.getBody().toString() : new String();
				String returnTypeMethod = node.getReturnType2() != null ? node.getReturnType2().toString() : null;
				List<Parameter> parameters = new ArrayList<Parameter>();
				List<String> modifiers = new ArrayList<String>();
				
				for (Object o : node.parameters().toArray()) {
					String t = o.toString().split(Constants.WHITESPACE)[0];
					String n = o.toString().split(Constants.WHITESPACE)[1];
					Parameter p = new Parameter(t, n);
					if(t.contains(Constants.THREEFOLD_DOT)){
						p.setOptional(true);
					}
					parameters.add(p);
				}
				
				for (Object o : node.modifiers().toArray()) {
					modifiers.add(o.toString());
				}
				
				Method method = new Method(name, content, body, returnTypeMethod, parameters, modifiers);
				defineMethodVariables(method);
				defineMethodSimpleName(method);
				method.setMethodInvocations(getMethodInvocations(method));
				method.setOwnerClass(ownerClass);
				methods.add(method);
				if(returnTypeMethod != null){
					referenceSet.getReferences().add(returnTypeMethod);
				}
				return true;
			}
		});
		
		if(unit.getPackage()!= null){
			c.setPackageName(unit.getPackage().getName().toString());
		}else{
			c.setPackageName(Constants.EMPTY);
		}
		c.setMethods(methods);
		c.setAttributes(attributes);
		if(extendClass.getName() != null){
			c.setSuperClass(extendClass);
		}
		return referenceSet;
	}
	

	/**
	 * @param method
	 */
	private void defineMethodVariables (Method method){
		CompilationUnit unit = configure(createContentClassFromMethod(method)); 
		final List<Variable> variables = new ArrayList<Variable>();
		
		unit.accept(new ASTVisitor() {
			@Override
			public boolean visit(VariableDeclarationStatement node) {
				String type = node.getType().toString();
				String name;
				for (Object o : node.fragments()) {
					name = o.toString();
					if(name.contains(Constants.EQUAL)){
						name = name.substring(0,name.indexOf(Constants.EQUAL));
					}
					variables.add(new Variable(type, name));
				}
				return true; 
			}
			
			@Override
			public boolean visit(SingleVariableDeclaration node) {
				Variable v = new Variable(node.getType().toString(), node.getName().toString()); 
				variables.add(v);
				return true; 
			}
		});
		method.setVariables(variables);
	}
	
	/**
	 * @param method
	 */
	private void defineMethodSimpleName (Method method){
		CompilationUnit unit = configure(createContentClassFromMethod(method)); 
		final List<String> simpleNames = new ArrayList<String>();
		
		unit.accept(new ASTVisitor() {
			@Override
			public boolean visit(SimpleName node) {
				simpleNames.add(node.toString());
				return true;
			}
		});
		method.setSimpleNames(simpleNames);
	}
	
	/**
	 * @param method
	 * @return method invocation list
	 */
	private List<br.ufpi.codivision.feature.java.model.MethodInvocation> getMethodInvocations (Method method){
		CompilationUnit unit = configure(createContentClassFromMethod(method)); 
		final List<br.ufpi.codivision.feature.java.model.MethodInvocation> invocations = new ArrayList<>(); 
		
		
		unit.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodInvocation node){
				br.ufpi.codivision.feature.java.model.MethodInvocation mi;
				String expression = node.getExpression() != null ? node.getExpression().toString(): null;
				String name = node.getName().toString();
				mi = new br.ufpi.codivision.feature.java.model.MethodInvocation(expression, name);
				for (Object o : node.arguments()) {
					mi.getArguments().add(String.valueOf(o));
				}
				invocations.add(mi);
				return true;
			}
		});
		
		return invocations;
	}
	
	/**
	 * @param method
	 * @return
	 */
	private String createContentClassFromMethod(Method method){
		String openKey = Constants.OPEN_KEY;
		String closeKey = Constants.CLOSE_KEY;
		String className = method.getName().toUpperCase();
		String whiteSpace = Constants.WHITESPACE;
		return Constants.CLASS.concat(whiteSpace).concat(className).concat(openKey).concat(method.getContent()).concat(closeKey);
	}
}
