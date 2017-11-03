package fr.soreth.VanillaPlus.Utils;

import java.lang.reflect.Constructor;

import fr.soreth.VanillaPlus.ErrorLogger;

public class ReflectionObject {
	   private Object reflectedObject;
	   private Constructor<?> reflectedConstructor;
	   private Class<?> reflectedClass;
	   private boolean isValid = true;
	   public ReflectionObject(String className, Class<?>... classes){
		   reflectedClass = ReflectionUtils.getClass(className);
		   if(reflectedClass==null)
			   isValid = false;
		   reflectedConstructor = ReflectionUtils.getDeclaredConstructor(reflectedClass, classes);
		   if(reflectedConstructor==null)
			   isValid = false;
	   }
	   public ReflectionObject(Class<?> packetClass, Class<?>... classes) {
		   reflectedClass = packetClass;
		   if(reflectedClass==null)
			   isValid = false;
		   reflectedConstructor = ReflectionUtils.getDeclaredConstructor(reflectedClass, classes);
		   if(reflectedConstructor==null)
			   isValid = false;
	   }
	public ReflectionObject instance(Object ... options){
		   reflectedObject = ReflectionUtils.instance(reflectedConstructor, options);
		   if(reflectedObject == null)
			   isValid = false;
		   return this;
	   }
	   public boolean isValid(){
		   return isValid;
	   }
	   public boolean isGoodType(Class<?>type){
		   if(type.isAssignableFrom(reflectedClass)){
			   return true;
		   }
		   ErrorLogger.addError(reflectedClass.getName() + " is not assignable for  '" + type.getName() + "'" );
		   return false;
	   }
	   public Object getObject(){
		   return isValid ? reflectedObject : null;
	   }
	   public Class<?> getClazz(){
		   return isValid ? reflectedClass : null;
	   }
	   public void setField(String fieldName, Object value){
		   ReflectionUtils.setField(fieldName, reflectedObject, value);
	   }
	   public void setDeclaredField(String fieldName, Object value){
		   ReflectionUtils.setDeclaredField(fieldName, reflectedObject, value);
	   }
	   public Object getField(String fieldName, Object value){
		   return ReflectionUtils.getField(fieldName, reflectedObject);
	   }
	   public Object getDeclaredField(String fieldName, Object value){
		   return ReflectionUtils.getDeclaredField(fieldName, reflectedObject);
	   }
	   public Object invoke(String MethodName, Object... args){
		   return ReflectionUtils.invoke(MethodName, reflectedObject, args);
	   }
	   
	   
}
