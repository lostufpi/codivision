package br.ufpi.codivision.core.util.sort;

public class ReflectionUtil {  
	  
	   /** 
	    * Constrói o nome do método get, de acordo com o nome do atributo 
	    * @param fieldName 
	    * @return Método get do atributo 
	    */  
	   public static String buildGetMethodName(String fieldName) {  
	      StringBuilder methodName = new StringBuilder("get");  
	      methodName.append(fieldName.substring(0, 1)  
	            .toUpperCase());  
	      methodName.append(fieldName.substring(1, fieldName.length()));  
	      return methodName.toString();  
	   }  
	     
	   /** 
	    * Constrói o nome do método set, de acordo com o nome do atributo 
	    * @param fieldName 
	    * @return Método set do atributo 
	    */  
	   public static String buildSetMethodName(String fieldName) {  
	      StringBuilder methodName = new StringBuilder("set");  
	      methodName.append(fieldName.substring(0, 1)  
	            .toUpperCase());  
	      methodName.append(fieldName.substring(1, fieldName.length()));  
	      return methodName.toString();  
	   }  
	        
	}  

