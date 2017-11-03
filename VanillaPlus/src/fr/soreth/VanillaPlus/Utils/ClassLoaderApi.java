package fr.soreth.VanillaPlus.Utils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import org.bukkit.plugin.Plugin;

import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class ClassLoaderApi {
	/*
	public static List<Class<?>> loadClass(File folder, Class<?> subClass){
	    List<Class<?>> classs = new ArrayList<Class<?>>();
	    ClassLoader loader = null;
		try {
			loader = URLClassLoader.newInstance(
				    new URL[] { folder.toURI().toURL() },
				    subClass.getClassLoader()
				);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}
	    for(File file : folder.listFiles()){
		   	if(!file.exists())
		   		continue;
		   	if(file.isDirectory())
		   		continue;
		   	if(!file.canRead())
		   		continue;
			try {
				Class<?> clazz = Class.forName(file.getName().replaceAll(".class", ""), true, loader);
				if(clazz.isAssignableFrom(subClass))
					classs.add(clazz);
			} catch (ClassNotFoundException e) {
				
			}
				
	    }
	    return classs;
	}*/

	private static Method ADD_URL_METHOD = ReflectionUtils.getDeclaredMethod("addURL", URLClassLoader.class, URL.class);

	public static File downloadJar(Plugin plugin, File libDir, String name, String urlString){
		name = name + ".jar";
		File file = new File(libDir, name);
		if (file.exists()) {
			return file;
		}
		ConfigUtils.createRecursively(libDir, true);
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (InputStream in = url.openStream()) {
			Files.copy(in, file.toPath());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!file.exists()) {
			ErrorLogger.addError(file.toString() + " not found.");
			return null;
		} else {
			return file;
		}
    }
	public static void loadJar(Plugin plugin, File file){
		URLClassLoader classLoader = (URLClassLoader) plugin.getClass().getClassLoader();
		classLoader = (URLClassLoader) classLoader.getParent();
		try {
			ReflectionUtils.invoke(ADD_URL_METHOD, classLoader, file.toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}