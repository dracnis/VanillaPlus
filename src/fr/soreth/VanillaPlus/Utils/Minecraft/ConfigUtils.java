package fr.soreth.VanillaPlus.Utils.Minecraft;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;

public class ConfigUtils {
	public static Location loadLocation(ConfigurationSection c){
		if(c == null){
			ErrorLogger.addError("Invalid Location");
			return null;
		}
		double x,y,z = 0.0;
		float yaw,pitch = 0F;
		World world;
		world = Bukkit.getWorld(c.getString("WORLD","world"));
		if( world == null){
			ErrorLogger.addError("Invalid world name !");
			return null;
		}
		x = c.getDouble("X", 0.0);
		y = c.getDouble("Y", 0.0);
		z = c.getDouble("Z", 0.0);
		yaw = (float) c.getDouble("YAW", 0.0);
		pitch = (float) c.getDouble("PITCH", 0.0);
		return new Location(world, x, y, z, yaw, pitch);
	}
	public static void copyFiles(String prefix, List<String> path, Plugin plugin){
		for(String filePath : path){
			copyFile(prefix+filePath, plugin);
		}
	}
	public static void copyFiles(List<String> path, Plugin plugin){
		for(String filePath : path){
			copyFile(filePath, plugin);
		}
	}
	public static void copyFile(String path, Plugin plugin){
		path = fixPath(path);
		File temp = new File(plugin.getDataFolder(),path);
		//if dont exist try copy it
		if(!temp.exists()){
			//test if plugin exist
			if(plugin.getResource(path.replace(File.separatorChar, '/'))!=null)
				plugin.saveResource(path.replace(File.separatorChar, '/'), true);
		}
		//if still don't exist create it
		if(!temp.exists()){
			temp = new File(plugin.getDataFolder(), path);
			createRecursively(temp, false);
		}
	}
	public static void createRecursively(File file, boolean folder){
		if(file == null || file.exists())return;
		File parent = file.getParentFile();
		if (parent.exists() || parent.mkdirs()) {
			if(folder)
				file.mkdir();
			else
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					ErrorLogger.addError("I/O Exception for file : " + file.getAbsolutePath());
				}
		}
	}
	public static YamlConfiguration getYaml(Plugin p, String path, boolean create){
		path = fixPath(path);
		File temp = new File(p.getDataFolder(), path);
		if(!temp.exists()){
			copyFile(path, p);
			if(!temp.exists()){
				if(create)
				try {
					temp.createNewFile();
				} catch (IOException e) {
					ErrorLogger.addError("Can't create " + path + " IO Exception !");
					e.printStackTrace();
				}
				else
					return null;
			}
		}
		YamlConfiguration file = YamlConfiguration.loadConfiguration(temp);
		return file;
	}
	public static YamlConfiguration getYaml(File file, String path){
		path = fixPath(path);
		File temp = new File(file, path);
		if(!temp.exists()){
			try {
				temp.createNewFile();
			} catch (IOException e) {
				ErrorLogger.addError("Can't create " + path + " IO Exception !");
				e.printStackTrace();
			}
		}
		YamlConfiguration result = YamlConfiguration.loadConfiguration(temp);
		return result;
	}
	public static void copyDefaultNode(YamlConfiguration configFile, Plugin plugin, String path, String nodPath){
		configFile.createSection(nodPath);
		path = fixPath(path);
		InputStream file = plugin.getResource(path.replace(File.separatorChar, '/'));
		if(file!=null){
			YamlConfiguration defaultFile = YamlConfiguration.loadConfiguration(new InputStreamReader(file));
			if(defaultFile.contains(nodPath))
				configFile.set(nodPath, defaultFile.get(path));
			else
				configFile.set(nodPath, Error.MISSING_NODE.getMessage());
		}
		File temp = new File(plugin.getDataFolder(), path);
		try {
			configFile.save(temp);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.addError("I/O Exception for file : " + temp.getAbsolutePath());
		}
	}
	public static String fixPath(String path){
		if(!path.endsWith(".yml"))
			path = path+".yml";
		//return path.replaceAll("/", File.separator);
		return path;
		
	}
	public static String getClassName(File file) {
		try {
			@SuppressWarnings("resource")
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
		    dis.readLong(); // skip header and class version
		    int cpcnt = (dis.readShort()&0xffff)-1;
		    int[] classes = new int[cpcnt];
		    String[] strings = new String[cpcnt];
		    for(int i=0; i<cpcnt; i++) {
		        int t = dis.read();
		        if(t==7) classes[i] = dis.readShort()&0xffff;
		        else if(t==1) strings[i] = dis.readUTF();
		        else if(t==5 || t==6) { dis.readLong(); i++; }
		        else if(t==8) dis.readShort();
		        else dis.readInt();
		    }
		    dis.readShort(); // skip access flags
		    return strings[classes[(dis.readShort()&0xffff)-1]-1].replace('/', '.');
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static void removeRecusively(File folder) {
		if(folder.exists()) {
			File files[] = folder.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					removeRecusively(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
	}
	public static void copyFileOrFolder(File source, File dest, CopyOption...  options){
		if(source.isDirectory())
			copyFolder(source, dest, options);
		else {
			ensureParentFolder(dest);
			copyFile(source, dest, options);
		}
	}
	private static void copyFolder(File source, File dest, CopyOption... options){
		if(!dest.exists())
			dest.mkdirs();
		File[] contents = source.listFiles();
		if(contents != null) {
	    	for(File f : contents) {
	        	File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
	            if(f.isDirectory())
	            	copyFolder(f, newFile, options);
	            else
	            	copyFile(f, newFile, options);
	        }
	    }
	}
	private static void copyFile(File source, File dest, CopyOption... options){
		try {
			Files.copy(source.toPath(), dest.toPath(), options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void ensureParentFolder(File file) {
		File parent = file.getParentFile();
	    if(parent != null && !parent.exists())
	    	parent.mkdirs();
	} 
}
