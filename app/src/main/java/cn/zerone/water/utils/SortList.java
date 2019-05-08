package cn.zerone.water.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortList<E> {
    public  void Sort(List<E> list, final String method, final String sort) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m1 = ((E) a).getClass().getMethod(method,  new Class[0]);
                    Method m2 = ((E) b).getClass().getMethod(method, new Class[0]);
                    if (sort != null && "desc".equals(sort))// 倒序
                        ret = m2.invoke(((E) b), new Object[]{}).toString()
                                .compareTo(m1.invoke(((E) a), new Object[]{}).toString());
                    else
                        // 正序
                        ret = m1.invoke(((E) a), new Object[]{}).toString()
                                .compareTo(m2.invoke(((E) b), new Object[]{}).toString());
                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }
                return ret;
            }
        });
    }
}