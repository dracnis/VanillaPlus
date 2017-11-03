package fr.soreth.VanillaPlus;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Utils;


public class Manager<S,T> {
    /**
     * List of the registered Class extensions.
     */
	protected Map<String, Class<? extends T>>extensionClasses = new TreeMap<String, Class<? extends T>>();
    /**
     * List of the registered Object extensions.
     */
	protected Map<S, T>extensions = new TreeMap<S, T>();
    /**
     * The Extension's type's name.
     */
	protected String type;
	protected Class<T>extensionType;
	protected Class<S>keyType;
	public Manager(Class<S>key, Class<T> type) {
		extensionType = type;
		keyType = key;
		this.type = extensionType.getSimpleName();
		if(!Modifier.isAbstract(type.getModifiers()) && !type.isInterface())
			register(type, "BASE");
	}
    /**
     * Registers a class.
     *
     * @param classs The class to add.
     */
    public boolean register(Class<? extends T> classs, String name) {
    	if(name==null)
    		return false;
    	if(extensionClasses.containsKey(name)){
			ErrorLogger.addError(type + "'s extension class" + name + " already exist !");
			return false;
    	}
    	extensionClasses.put(name, classs);
    	return true;
    }
    /**
     * Registers an Extension.
     *
     * @param name The extension's name.
     * @param extension The extension.
     * @param log If true, will be logged if already exist.
     */
    public boolean register(S key, T extension, boolean log) {
    	if(extensions.containsKey(key) && log){
    		//String[] type = this.type.split(".");
			ErrorLogger.addError(type + "'s extension " + key + " already exist !");
			return false;
    	}
    	extensions.put(key, extension);
    	return true;
    }
    /**
     * Unregisters an Extension.
     *
     * @param name The extension's name.
     * @param extension The extension.
     */
    public boolean unregister(S key, boolean log) {
    	if(!extensions.containsKey(key) && log){
    		//String[] type = this.type.split(".");
			ErrorLogger.addError(type + "'s extension " + key + " don't exist !");
			return false;
    	}
    	extensions.remove(key);
    	return true;
    }
    /**
     * Create an Extension from arguments.
     *
     * @param name The extension's type's name.
     * @param args The arguments.
     */
	public T create(String typeName, Object... args){
		if(typeName == null) typeName = "BASE";
		Class<? extends T> extensionClass = extensionClasses.get(typeName);
		if(extensionClass == null){
			ErrorLogger.addError(type  + " TYPE " + typeName + " " + Error.INVALID.getMessage());
			for(Entry<String, Class<? extends T>> s : extensionClasses.entrySet())
				System.out.print(s.getKey() + " " + s.getValue().getSimpleName());
			return null;
		}
		if(args == null){
			ErrorLogger.addError(type  + " TYPE " + typeName);
			return null;
		}
    	Class<?>[] clazz = new Class[args.length];
    	for(int i = 0 ; i < args.length ; i++){
    		Object o = args[i];
    		if(o != null){
        		clazz[i] = o.getClass();
        		if(clazz[i].equals(MemorySection.class))
        			clazz[i] = ConfigurationSection.class;
    		}
    	}
		@SuppressWarnings("unchecked")
		T result = (T) ReflectionUtils.instance(
				ReflectionUtils.getConstructor(extensionClass, clazz), args);
		if(result == null){
    		//String[] type = this.type.split(".");
			ErrorLogger.addError(type  + " => " + typeName + Error.MISSING.getMessage());
			return null;
		}
		return result;
	}
    /**
     * Get the Extension from name.
     *
     * @param name The extension's name.
     * @param log Log if null.
     */
	public T get(Object typeName, boolean log){
		T result = extensions.get(typeName);
    	if(log && result == null){
			ErrorLogger.addError(type + " => " + typeName + " " + Error.MISSING.getMessage());
			return null;
    	}
    	return result;
	}
    /**
     * @return all the loaded objects. 
     */
	public Collection<T> getLoaded(){
		return extensions.values();
	}
    /**
     * @return all the loaded objects. 
     */
	public Collection<Class<? extends T>> getLoadedClasses(){
		return extensionClasses.values();
	}
    /**
     * Initialize the manager from the configuration section.
     *
     * @param section The configuration section to use.
     */
	@SuppressWarnings("unchecked")
	protected void init(ConfigurationSection section, Object... args){
		if(section == null)return;
		for(String key : section.getKeys(false)){
			ErrorLogger.addPrefix(key);
			ConfigurationSection subSection = section.getConfigurationSection(key);
			if(keyType == String.class)
				register((S) key, create(subSection.getString(Node.TYPE.get(), Node.BASE.get()), addArg(subSection, args)), true);
			else if(keyType == Integer.class)
				register((S) Integer.valueOf(Utils.parseInt(key, 1, true)), create(subSection.getString(Node.TYPE.get(),
						Node.BASE.get()), addArg(subSection, args)), true);
			ErrorLogger.removePrefix();
		}
	}
	private static Object[] addArg(Object o, Object...  args) {
		Object[]result = new Object[args == null ? 1 : (args.length+1)];
		result[0]=o;
		if(args!=null)
    	for(int i = 1 ; i < result.length ; i++){
    		result[i] = args[i-1];
    	}
		return result;
	}
}
