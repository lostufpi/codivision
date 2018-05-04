package br.ufpi.codivision.feature.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.ufpi.codivision.core.util.sort.ReflectionUtil;

public class GenericComparator<T> implements Comparator<T> {

	private SortType sortType = null;
	private String methodName = null;

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

	public static <T> void sortList(List<T> list, String sortField, SortType sortType) {
		GenericComparator<T> comparator = new GenericComparator<T>(sortField, sortType);
		if(list != null) {
			Collections.sort(list, comparator);
		}
	}

	public static <T> void sortArray(T[] array, String sortField, SortType sortType) {
		GenericComparator<T> comparator = new GenericComparator<T>(sortField, sortType);
		Arrays.sort(array, comparator);
	}
}