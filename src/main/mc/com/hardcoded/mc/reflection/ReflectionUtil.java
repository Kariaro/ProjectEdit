package com.hardcoded.mc.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
	public static List<Class<?>> getClasses(Class<?> obj_class) {
		if(obj_class == null || obj_class == Object.class) return List.of();
		
		List<Class<?>> classes = new ArrayList<>();
		classes.add(obj_class);
		classes.addAll(getClasses(obj_class.getSuperclass()));
		return classes;
	}
	
	public static Object getField(Class<?> obj_class, Object obj, String fieldName) throws Exception {
		if(obj_class == null) return null;
		
		List<Class<?>> classes = getClasses(obj_class);
		
		for(Class<?> clazz : classes) {
			for(Field field : clazz.getDeclaredFields()) {
				if(field.getName().equals(fieldName)) {
					boolean accessable = field.canAccess(obj);
					
					field.setAccessible(true);
					Object value = field.get(obj);
					field.setAccessible(accessable);
					
					return value;
				}
			}
		}
		
		return null;
	}
	
	public static Object get(Class<?> obj_class, Object obj, String... fields) throws Exception {
		if(obj_class == null) return null;
		
		for(String field : fields) {
			Object next_object = getField(obj_class, obj, field);
			if(next_object == null) return null;
			
			obj_class = next_object.getClass();
			obj = next_object;
		}
		
		return obj;
	}
	
	public static Object get(Object obj, String... fields) throws Exception {
		return get(obj.getClass(), obj, fields);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldFromPathCast(Object obj, String fieldPath, T def) throws Exception {
		T value = (T)get(obj.getClass(), obj, fieldPath.split("/"));
		return value == null ? def:value;
	}
	
	public static Object getFieldFromPath(Object obj, String fieldPath) throws Exception {
		return get(obj.getClass(), obj, fieldPath.split("/"));
	}

	public static Method getMethod(Class<?> obj_class, Object obj, String methodName, Class<?>... params) throws Exception {
		if(obj_class == null) return null;
		
		List<Class<?>> classes = getClasses(obj_class);
		
		for(Class<?> clazz : classes) {
			try {
				return clazz.getDeclaredMethod(methodName, params);
			} catch(NoSuchMethodException e) {
				
			}
		}
		
		return null;
	}
	
	public static Method getMethod(Object obj, String methodName, Class<?>... params) throws Exception {
		return getMethod(obj.getClass(), obj, methodName, params);
	}
}
