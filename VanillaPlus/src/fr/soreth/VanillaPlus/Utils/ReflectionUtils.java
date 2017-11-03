package fr.soreth.VanillaPlus.Utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;

public class ReflectionUtils {
	private static final String ServerPackage = "net.minecraft.server."+VanillaPlusCore.getBukkitVersion()+".";
	private static final String BukkitPackage = "org.bukkit.craftbukkit." + VanillaPlusCore.getBukkitVersion()+"." ;
	public static Class <?> getServerClass(String name){
	    String className = ServerPackage+name;
	    return getClass(className);
	}
	public static Class <?> getBukkitClass(String name){
	    String className = BukkitPackage+name;
	    return getClass(className);
	}
	public static Class <?> getClass(String name){
		Class<?> clazz;
		try {
			clazz = Class.forName(name);
		}catch (ClassNotFoundException e){
			ErrorLogger.addError(" Class : "+name+" was not found. " );
			clazz = null;
		}
		return clazz;
	}
	public static Constructor<?> getConstructor(Class<?> type, Class<?>... fields){
		Constructor<?> constructor = null;
		try{
			if(fields == null || fields.length == 0){
				constructor = type.getConstructor(fields);
			}else{
				for(Constructor<?> c : type.getConstructors()){
					if(c.getParameterTypes().length == fields.length){
						Class<?>[] current = c.getParameterTypes();
						boolean valid = true;
						for(int i = 0 ; i < fields.length ; i++ ){
							if(current[i] != null && !current[i].isAssignableFrom(fields[i]) && !current[i].equals(Object.class)){
								valid = false;
								break;
							}
						}
						if(valid)return c;
					}
				}
			}
		}
		catch (NoSuchMethodException e) {
			ErrorLogger.addError("Method : ( " + classToString(fields) + " ) for class : " + type.getName() + " was not found." );
		} catch (SecurityException e) {
			ErrorLogger.addError("Can't access Method ( " + classToString(fields) + " ) for class : '" + type.getName() + "'." );
			e.printStackTrace();
		}
		if(constructor == null)
			ErrorLogger.addError("Method : ( " + classToString(fields) + " ) for class : " + type.getName() + " was not found." );
		return constructor;
	}
	public static Constructor<?> getDeclaredConstructor(Class<?> type, Class<?>... fields){
		Constructor<?> constructor = null;
		try{
			constructor = type.getDeclaredConstructor(fields);
			if(fields == null || fields.length == 0){
				constructor = type.getDeclaredConstructor(fields);
			}else{
				for(Constructor<?> c : type.getDeclaredConstructors()){
					if(c.getParameterTypes().length == fields.length){
						Class<?>[] current = c.getParameterTypes();
						boolean valid = true;
						for(int i = 0 ; i < fields.length ; i++ ){
							if(fields[i] != null && !fields[i].isAssignableFrom(current[i]) && !current[i].equals(Object.class)){
								valid = false;
								break;
							}
						}
						if(valid)return c;
					}
				}
			}
		}
		catch (NoSuchMethodException e) {
			ErrorLogger.addError("Constructor : ( " + classToString(fields) + " ) for class : " + type.getName() + " was not found." );
		} catch (SecurityException e) {
			ErrorLogger.addError("Can't access Method ( " + classToString(fields) + " ) for class : '" + type.getName() + "'!" );
			e.printStackTrace();
		}
		return constructor;
	}
	public static void setField(String fieldName, Object object, Object value){
		try {
			Field f = object.getClass().getField(fieldName);
			f.setAccessible(true);
			f.set(object, value);
			f.setAccessible(false);
		} catch (NoSuchFieldException e) {
			ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' don't exist!" );
		} catch (SecurityException e) {
			ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' is security protected!" );
		} catch (IllegalArgumentException e) {
			ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' can't accept '" + value.getClass().getName() + "' object!" );
		} catch (IllegalAccessException e) {
			ErrorLogger.addError("Can't set Field " + fieldName + " for class : '" + object.getClass().getName() + "' accessible!" );
		}
	}
	public static void setDeclaredField(String fieldName, Object object, Object value){
		setDeclaredField(fieldName, object instanceof Class ? (Class<?>)object : object.getClass(), object, value);
	}
	public static void setDeclaredField(String fieldName, Class<?> objectClass, Object object, Object value){
		try {
			Field f = objectClass.getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(object, value);
			f.setAccessible(false);
		} catch (NoSuchFieldException e) {
			ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' don't exist!" );
		} catch (SecurityException e) {
			ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' is security protected!" );
		} catch (IllegalArgumentException e) {
			ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' can't accept '" + value.getClass().getName() + "' object!" );
		} catch (IllegalAccessException e) {
			ErrorLogger.addError("Can't set Field " + fieldName + " for class : '" + object.getClass().getName() + "' accessible!" );
		}
	}
   	public static Object getField(String fieldName, Object object){
        try {
            Field f = (object instanceof Class<?> ) ? ((Class<?>) object).getDeclaredField(fieldName) : object.getClass().getField(fieldName);
            f.setAccessible(true);
            Object s = f.get(object);
            f.setAccessible(false);
            return s;
        } catch (NoSuchFieldException e) {
     	   ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' don't exist!" );
        } catch (SecurityException e) {
 			ErrorLogger.addError("Field " + fieldName + " for class : '" + object.getClass().getName() + "' is security protected!" );
        } catch (IllegalAccessException e) {
     	   ErrorLogger.addError("Can't set Field " + fieldName + " for class : '" + object.getClass().getName() + "' accessible!" );
        }
        return null;
    }
   	public static Object castObject(Object object, Class<?>clazz){
        return clazz.cast(object);
   	}
   	public static Method getMethod(String methodName, Class<?> staticClass, Class<?>... args){
   		try {
   			return staticClass.getMethod(methodName, args);
   		} catch (NoSuchMethodException | SecurityException e) {
			ErrorLogger.addError("Method : "+ methodName + "( " + classToString(args) + " ) for class : " + staticClass.getName() + " was not found." );
			for(Method m : staticClass.getMethods())
				ErrorLogger.addError(m.getReturnType() + " " + m.getName() + " ( " + classToString(m.getParameterTypes()) + " ) ");
		}
   		return null;
   	}
   	public static Method getMethod(String MethodName, Object object, Object... args){
    	Class<?>[] clazz = new Class[args.length];
    	for(int i = 0 ; i < args.length ; i++)
    		clazz[i] = args[i].getClass();
   		return getMethod(MethodName, object instanceof Class ? (Class<?>)object : object.getClass(), clazz);
   	}
   	public static Method getDeclaredMethod(String methodName, Class<?> staticClass, Class<?>... args){
   		try {
   			return staticClass.getDeclaredMethod(methodName, args);
   		} catch (NoSuchMethodException | SecurityException e) {
			ErrorLogger.addError("Method : "+ methodName + "( " + classToString(args) + " ) for class : " + staticClass.getName() + " was not found." );
			for(Method m : staticClass.getDeclaredMethods())
				ErrorLogger.addError(m.getReturnType() + " " + m.getName() + " ( " + classToString(m.getParameterTypes()) + " ) ");
		}
   		return null;
   	}
   	public static Method getDeclaredMethod(String MethodName, Object object, Object... args){
    	Class<?>[] clazz = new Class[args.length];
    	for(int i = 0 ; i < args.length ; i++)
    		clazz[i] = args[i].getClass();
   		return getDeclaredMethod(MethodName, object.getClass(), clazz);
   	}
   	public static Object invoke(Method method, Object object, Object... args){
        try {
        	method.setAccessible(true);
			return method.invoke(object, args);
		} catch (IllegalAccessException | SecurityException e) {
			ErrorLogger.addError("Can't access Method " + method + " for class : '" + object.getClass().getName() + "'!" );
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			ErrorLogger.addError(method.getName() +"("+classToString(method.getParameterTypes())+ ") for class : '" + object.getClass().getName() + "' can't use : "+ objectsToString(args) +"!" );
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
        return null;
    }
   	public static Object invoke(String methodName, Object object, Object... args){
        return invoke(getMethod(methodName, object, args), object, args);
    }
	public static Object getDeclaredField(String fieldName, Object object){
		return getDeclaredField(fieldName, (object instanceof Class) ? (Class<?>)object : object.getClass(), object);
	}
	public static Object getDeclaredField(String fieldName, Class<?> objectClass, Object object){
	    try {
	           Field f = objectClass.getDeclaredField(fieldName);
	           f.setAccessible(true);
	           Object s = f.get(object);
	           f.setAccessible(false);
	           return s;
	       } catch (NoSuchFieldException e) {
	    	   ErrorLogger.addError("Field " + fieldName + " for class : '" + objectClass.getName() + "' don't exist!");
	       } catch (SecurityException e) {
				ErrorLogger.addError("Field " + fieldName + " for class : '" + objectClass.getName() + "' is security protected!" );
	       } catch (IllegalAccessException e) {
	    	   ErrorLogger.addError("Can't set Field " + fieldName + " for class : '" + objectClass.getName() + "' accessible!" );
	       }
	       return null;
	   }
	   public static Object instance(Class<?> clazz){
		   try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
	    	   ErrorLogger.addError("Can't create  " + clazz + " : no access!" );
		}
		   return null;
	   }
	   public static Object instance(Constructor<?> constructor, Object ... options){
	       try {
	           return constructor.newInstance(options);
	       } catch (InstantiationException e) {
	    	   
	    	   e.printStackTrace();
	       } catch (IllegalAccessException e) {
	    	   ErrorLogger.addError("Can't create  " + constructor.getDeclaringClass() + " : no access!" );
	       } catch (IllegalArgumentException e) {
	    	   ErrorLogger.addError("Can't create  " + constructor.getDeclaringClass() + " invalid object : '" + objectsToString(options) + "'!" );
	       } catch (InvocationTargetException e) {
	    	   ErrorLogger.addError("Can't create  " + constructor.getDeclaringClass() + " invocation exception : " + e.getCause() + " " + e.getMessage() + " " + objectsToString(options));
	    	   e.printStackTrace();
	       }
	       return null;
	   }
	   public static String objectsToString(Object... args){
			String field = "";
			for(Object o : args){
				if(!field.equals(""))
					field+=", ";
				field+= o.getClass().getName();
			}
			return field;
	   }
	   public static String classToString(Class<?>... args){
			String field = "";
			for(Class<?> clazzz : args){
				if(!field.equals(""))
					field+=", ";
				if(clazzz == null)
					field += null;
				else
					field+= clazzz.getName();
			}
			return field;
	   }
	public static Object getArrayOf(Class<?> mainClass, Object... values) {
		Object result = Array.newInstance(mainClass, values == null ? 0 : values.length);
		if(values == null || values.length == 0) return result;
		for(int i = 0 ; i< values.length ; i ++){
			Array.set(result, i, values[i]);
		}
		return result;
	}
}
