/**
 * 
 */
package br.ufpi.codivision.core.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import br.ufpi.codivision.core.util.Levenshtein;

/**
 * @author Werney Ayala
 *
 */
public class DiffUtil {
	
	private Stack<String> additions;
	private Stack<String> deletions;
	
	private int adds;
	private int mods;
	private int dels;
	private int conditions;
	
	/**
	 * 
	 */
	public DiffUtil() {
		this.additions = new Stack<String>();
		this.deletions = new Stack<String>();
	}
	
	public Map<String, Integer> analyze(String fileDiff){
		
		this.adds = 0;
		this.mods = 0;
		this.dels = 0;
		this.conditions = 0;
		
		HashMap<String, Integer> modifications = new HashMap<String, Integer>();
		if(fileDiff !=null ){
		String[] lines = fileDiff.split("\n");
		
		for(int i = 0; i < lines.length; i++){
			if((i > 3) && (lines[i].length() > 0) && (!lines[i].contains("import ")) && (!lines[i].contains("package "))){
				if((lines[i].charAt(0) == '+') && (lines[i].substring(1).trim().length() > 0))
					additions.push(lines[i].substring(1));
				else if((lines[i].charAt(0) == '-') && (lines[i].substring(1).trim().length() > 0))
					deletions.push(lines[i].substring(1));
				else if ((!additions.isEmpty()) || (!deletions.isEmpty()))
					compare();
			}
		}
		}
		modifications.put("adds", adds);
		modifications.put("mods", mods);
		modifications.put("dels", dels);
		modifications.put("conditions", conditions);
		return modifications;
		
	}
	
	private void compare(){
		
		for (String temp : additions) {
			if (temp.trim().startsWith("if("))
				conditions++;
		}
		
		while((!additions.isEmpty()) || (!deletions.isEmpty())){
			if(additions.isEmpty()){
				deletions.pop();
				dels++;
			} else if(deletions.isEmpty()){
				additions.pop();
				adds++;
			} else {
				String add = additions.pop();
				String del = deletions.pop();
				if(isSimilar(add, del)){
					mods++;
				} else if(additions.size() > deletions.size()){
					deletions.push(del);
					adds++;
				} else {
					additions.push(add);
					dels++;
				}
			}
		}
	}
	
	public boolean isSimilar(String string1, String string2){
		int result = Levenshtein.getLevenshteinDistance(string1, string2);
		if(((double)result/string1.length()) < 0.75)
			return true;
		return false;
		
	}
	
	/**
	 * @return the adds
	 */
	public int getAdds() {
		return adds;
	}

	/**
	 * @param adds the adds to set
	 */
	public void setAdds(int adds) {
		this.adds = adds;
	}

	/**
	 * @return the mods
	 */
	public int getMods() {
		return mods;
	}

	/**
	 * @param mods the mods to set
	 */
	public void setMods(int mods) {
		this.mods = mods;
	}

	/**
	 * @return the dels
	 */
	public int getDels() {
		return dels;
	}

	/**
	 * @param dels the dels to set
	 */
	public void setDels(int dels) {
		this.dels = dels;
	}

	/**
	 * @return the conditions
	 */
	public int getConditions() {
		return conditions;
	}

	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(int conditions) {
		this.conditions = conditions;
	}

}
