package fr.soreth.VanillaPlus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.soreth.VanillaPlus.Utils.Utils;


/**
 * This class will collect all the errors found.
 */
public class ErrorLogger {

	private static LinkedHashMap<Instant, String> errors = new LinkedHashMap<Instant, String>();
	private static List<String>prefixList = new ArrayList<String>();
	private static DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT ).withZone( ZoneId.systemDefault() );
	public static String addError(String error){
		/*if(error.contains("Invalid"))
			for (StackTraceElement ste : Thread.currentThread().getStackTrace())
		    System.out.println(ste);
		*/
		error = ChatColor.translateAlternateColorCodes('&', Utils.toString(prefixList) + error);
		errors.put(Instant.now(), error);
		VanillaPlusCore.getVPConsole().sendMessage(error);
		return error;
	}
	//public static LinkedHashMap<Instant, String> getErrors() {
	//	return errors;
	//}
    /**
     * Add prefix to future errors
     *
     * @param prefix The prefix to add.
     */
	public static void addPrefix(String prefix) {
		prefixList.add(prefix + " §7=>§a ");
	}
    /**
     * Get error amount.
     *
     * @return The amount of error.
     */
	public static int getSize() {
		return errors.size();
	}
    /**
     * Remove the last prefix to future errors
     *
     */
	public static void removePrefix(){
		if(!prefixList.isEmpty())
			prefixList.remove(prefixList.size()-1);
	}
    /**
     * Send all errors to the player.
     *
     * @param player The receiver.
     */
	public static void sendError(Player player){
		if(player==null)return;
		player.sendMessage(Error.LOGGER.getMessage());
		for(Entry<Instant, String> error : errors.entrySet())
			player.sendMessage("§8[§c" + formatter.format(error.getKey()) + "§8]§7 : " + error.getValue());
	}
	@SuppressWarnings("rawtypes")
	private static final List<Class>logged = new LinkedList<Class>();
	@SuppressWarnings("rawtypes")
	public static <T extends Enum> void addMissingEnum(T[] enumList, String input) {
		if(logged.contains(enumList.getClass())) {
			ErrorLogger.addError(input + " isn't valid please a valid " + enumList.getClass().getSimpleName());
		}else {
			ErrorLogger.addError(input + " isn't valid please use : " + Utils.toString(Arrays.asList(enumList),", "));
			logged.add(enumList.getClass());
		}
	}
}