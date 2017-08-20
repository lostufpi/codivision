package br.ufpi.codivision.core.util;

import java.util.List;

import br.ufpi.codivision.core.model.vo.AuthorPercentage;
import br.ufpi.codivision.core.model.vo.CommitHistory;
/**
 * Classe que implementa o algoritmo de ordenacao quick sort
 * @author Irvayne Matheus
 *
 */
public class QuickSort {
	
	
	public static void sort(List<CommitHistory> commits){
		
		quickSort(commits,0,commits.size()-1);
		
	}
	
	private static void quickSort(List<CommitHistory> commits,int inicio,int fim){
		if(inicio<fim){
			int pivo = particiona(commits,inicio, fim);
			quickSort(commits,inicio,pivo-1);
			quickSort(commits,pivo+1,fim);
		}
	}
	
	private static int particiona(List<CommitHistory> commits, int inicio, int fim){
			CommitHistory pivo = commits.get(inicio);
			int i = inicio + 1, f = fim;
			
			while(i<= f){
				if(commits.get(i).getData()[0] >= pivo.getData()[0]){
					i++;
				}else if(pivo.getData()[0] > commits.get(f).getData()[0]){
					f--;
				}else{
					CommitHistory commit = commits.get(i);
					commits.set(i, commits.get(f));
					commits.set(f, commit);
					i++;
					f--;
				}
				
			}
			commits.set(inicio, commits.get(f));
			commits.set(f, pivo);
			return f;
	}
	
	public static void sort2(List<AuthorPercentage> commits){
		
		quickSort2(commits,0,commits.size()-1);
		
	}
	
	private static void quickSort2(List<AuthorPercentage> commits,int inicio,int fim){
		if(inicio<fim){
			int pivo = particiona2(commits,inicio, fim);
			quickSort2(commits,inicio,pivo-1);
			quickSort2(commits,pivo+1,fim);
		}
	}
	
	private static int particiona2(List<AuthorPercentage> commits, int inicio, int fim){
			AuthorPercentage pivo = commits.get(inicio);
			int i = inicio + 1, f = fim;
			
			while(i<= f){
				if((commits.get(i).getAbsolute() * 100) / (commits.get(i).getAbsolute() + commits.get(i).getY()) >= ((pivo.getAbsolute() * 100) / (pivo.getY() + pivo.getAbsolute()))){
					i++;
				}else if(((pivo.getAbsolute() * 100) / (pivo.getY() + pivo.getAbsolute())) > (commits.get(f).getAbsolute() * 100) / (commits.get(f).getAbsolute() + commits.get(f).getY())){
					f--;
				}else{
					AuthorPercentage commit = commits.get(i);
					commits.set(i, commits.get(f));
					commits.set(f, commit);
					i++;
					f--;
				}
				
			}
			commits.set(inicio, commits.get(f));
			commits.set(f, pivo);
			return f;
	}
}