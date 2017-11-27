package br.ufpi.codivision.core.util.sort;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Comparator genérico para ordenar qualquer Entity
 * {@link http://javafree.uol.com.br/artigo/874495/Criando-um-GenericComparator.html}
 * 
 * 
 * @author afviriato
 * 
 * @param <T>
 *            Classe a ser ordenada
 */
public class GenericComparator<T> implements Comparator<T> {

	private SortType sortType = null;
	private String methodName = null;

	/**
	 * Constrói um GenericComparator de acordo com o campo e o tipo
	 * 
	 * @param sortField
	 *            Campo para ordenação
	 * @param sortType
	 *            Tipo de ordenação, ascendente (ASC) ou descendente (DESC)
	 */
	public GenericComparator(String sortField, SortType sortType) {
		this.sortType = sortType;
		this.methodName = ReflectionUtil.buildGetMethodName(sortField);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(T o1, T o2) {
		try {
			Method method1 = o1.getClass().getMethod(this.methodName, new Class[] {});
			Comparable comp1 = (Comparable) method1.invoke(o1, new Object[] {});

			Method method2 = o1.getClass().getMethod(this.methodName, new Class[] {});
			Comparable comp2 = (Comparable) method2.invoke(o2, new Object[] {});

			return comp1.compareTo(comp2) * this.sortType.getIndex();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Organiza um List<T> de acordo com o campo e o tipo (ASC ou DESC)
	 * 
	 * @param <T>
	 *            Classe dos objetos que serão ordenados
	 * @param list
	 *            List<T> a ser ordenada
	 * @param sortField
	 *            Campo para ordenação
	 * @param sortType
	 *            Tipo da ordenação (ASC ou DESC)
	 */

	public static <T> void sortList(List<T> list, String sortField, SortType sortType) {
		GenericComparator<T> comparator = new GenericComparator<T>(sortField, sortType);
		if(list != null) {
			Collections.sort(list, comparator);
		}
	}

	/**
	 * Organiza um T[] de acorco com o campo e o tipo (ASC ou DESC)
	 * 
	 * @param <T>
	 *            Classe dos objetos que serão ordenados
	 * @param array
	 *            T[] a ser ordenado
	 * @param sortField
	 *            Campo para ordenação
	 * @param sortType
	 *            Tipo da ordenação (ASC ou DESC)
	 */

	public static <T> void sortArray(T[] array, String sortField, SortType sortType) {
		GenericComparator<T> comparator = new GenericComparator<T>(sortField, sortType);
		Arrays.sort(array, comparator);
	}
}

