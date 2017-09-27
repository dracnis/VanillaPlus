package fr.soreth.VanillaPlus.Command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.VPCommandBlock;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;
import fr.soreth.VanillaPlus.Utils.Minecraft.NBTItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
public class CPManager extends Manager<String, CommandPlus> implements Listener{
	private static Message topHelp, commandHelp, noRequirement;
	private static Map<String, Command> commandMap;
	private static Map<Integer, Link>link = new HashMap<Integer, CPManager.Link>();
	private static VanillaPlus instance;
    @SuppressWarnings("unchecked")
	public CPManager(VanillaPlus instance) {
    	super(String.class, CommandPlus.class);
    	if(CPManager.instance != null || instance == null)return;
    	register(CPChannelSet.class,		"CHANNEL_SET");
    	register(CPChannelState.class,		"CHANNEL_STATE");
    	register(CPChannelTalk.class,		"CHANNEL_TALK");
    	register(CPCurrencyPay.class,		"CURRENCY_PAY");
    	register(CPCurrencySet.class, 		"CURRENCY_SET");
    	register(CPGamemode.class, 			"GAMEMODE");
    	register(CPLang.class, 				Node.LANG.get());
    	register(CPMenuOpen.class, 			"MENU_OPEN");
    	register(CPMessageSend.class, 		"MESSAGE_SEND");
    	register(CPMessagePrivate.class, 	"MESSAGE_PRIVATE");
    	register(CPMulti.class, 			"MULTI");
    	register(CPNick.class, 				"NICK");
    	register(CPNode.class, 				Node.NODE.get());
    	register(CPOp.class, 				"OP");
    	register(CPProfil.class, 			"PROFIL");
    	register(CPReward.class,			"REWARD");
    	register(CPTeleportLocation.class,	"TELEPORT_LOCATION");
    	register(CPTeleportPlayer.class,	"TELEPORT_PLAYER");
    	register(CPTOD.class, 				"TOD");
    	//TODO show top ( stat / money )
    	CPManager.instance = instance;
		commandMap = (Map<String, Command>) ReflectionUtils.getDeclaredField("knownCommands",
				ReflectionUtils.invoke(ReflectionUtils.getMethod("getCommandMap",
						ReflectionUtils.getBukkitClass("CraftServer")), Bukkit.getServer()));
    }
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent event) {
		if(link.isEmpty())return;
		ItemStack stack = event.getItem();
		if(stack == null)
			return;
		NBTItem item = new NBTItem(stack);
		Integer cmdId = item.getInteger("cmd");
		if(cmdId == null)
			return;
		Link cmd = link.get(cmdId);
		if(cmd==null)
			return;
		VPPlayer sender = VanillaPlusCore.getPlayerManager().getPlayer(event.getPlayer());
		if(cmd.cmd.hasRequirement(sender)){
			cmd.onExecute(sender);
			event.setCancelled(cmd.cancel);
		}
	}
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onCommand(PlayerCommandPreprocessEvent event){
		if(event.getMessage().isEmpty()){
			event.setMessage("/help");
			return;
		}
		Command cmd = commandMap.get(event.getMessage().replaceFirst("/", "").split(" ")[0].toLowerCase());
		if(cmd == null){
			event.setMessage("/help");
			return;
		}
		if(!cmd.testPermissionSilent(event.getPlayer()))
			if(!(cmd instanceof LinkCommand)){
				event.setMessage("/help");
			}
	}
	public void init(VanillaPlusCore core) {
		if(core == null)return;
		ConfigurationSection section = ConfigUtils.getYaml(core.getInstance(), "Command", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Command.yml");
		ConfigurationSection temp = section.getConfigurationSection(Node.SETTINGS.get());
		ErrorLogger.addPrefix(Node.SETTINGS.get());
		if(temp == null){
			Error.MISSING.add();
	    	topHelp			= core.getMessageManager().get(null);
	    	commandHelp		= core.getMessageManager().get(null);
	    	noRequirement	= core.getMessageManager().get(null);
    		ErrorLogger.removePrefix();
			return;
		}else{
	    	topHelp			= core.getMessageManager().get(temp.getString("TOP_HELP"));
	    	commandHelp		= core.getMessageManager().get(temp.getString("HELP"));
	    	noRequirement	= core.getMessageManager().get(temp.getString(Node.NO_REQUIREMENT.get()));
	    	List<String>toRemove = temp.getStringList("TO_REMOVE");
	    	if(toRemove != null && !toRemove.isEmpty()){
	    		for(String s : toRemove)
	    			commandMap.remove(s);
	    	}
		}
    	for(CommandPlus cmd : getLoaded()){
    		register(cmd);
    	}
    	commandMap.put(PlaceH.HELP.get(), new LinkCommand(PlaceH.HELP.get(), PlaceH.HELP.get(),
				"/"+PlaceH.HELP.get()+" [page]", Arrays.asList(PlaceH.HELP.get()), null));
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
		
		
	}
    public void init(VanillaPlusExtension extension) {
    	if(extension == null)return;
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Command", false);
		if(section == null)return;
    	//TODO allow main command to use alias command and item command in two list but create command check if is alias
		ErrorLogger.addPrefix("Command.yml");
    	ConfigurationSection temp = section.getConfigurationSection(Node.ALIASES.getList());
    	if(temp != null){
        	ErrorLogger.addPrefix(Node.ALIASES.getList());
        	for(String key : temp.getKeys(false)){
        		ErrorLogger.addPrefix(key);
        		int pos = Utils.parseInt(key, 0, true);
        		if(pos!=0){
           			ConfigurationSection sub = temp.getConfigurationSection(key);
           			link.put(pos, new Link(sub, extension.getMessageManager(), key));
        		}else
        			Error.INVALID.add();
        		ErrorLogger.removePrefix();
        	}
        	ErrorLogger.removePrefix();
    	}
		Bukkit.getServer().getPluginManager().registerEvents(this, instance);
		temp = section.getConfigurationSection(Node.COMMAND.getList()); 
		if(temp != null){
			ErrorLogger.addPrefix(Node.COMMAND.getList());
			super.init(temp, extension.getMessageManager());
			ErrorLogger.removePrefix();
		}
    	ErrorLogger.removePrefix();
    }
    public boolean register(String name, CommandPlus command, boolean log) {
    	if(super.register(name, command, log)){
    		return register(command);
    	}
    	return false;
    }
    public boolean register(CommandPlus command) {
    	if(command != null){
    		LinkCommand link = new LinkCommand(command.getName(), command.getDescription() == null ? "" : command.getDescription().getMessage(),
    				command.getUsage() == null ? "" : command.getUsage().getMessage(), command.getAliases(), command);
	    	if(!commandMap.containsKey(command.getName().toLowerCase()))
	    		commandMap.put(command.getName().toLowerCase(), link);
    	    for(String s : command.getAliases()){
    	    	if(!commandMap.containsKey(s))
    	    		commandMap.put(s.toLowerCase(), link);
    	    }
    	    return true;
    	}
    	return false;
    }
	public void remove(String name, CommandPlus cmd, boolean log) {
		if(unregister(name, log))
			remove(cmd);
	}
    public void remove(CommandPlus command){
    	if(command != null){
    		for(String s : command.getAliases())
    			commandMap.remove(s);
    		commandMap.remove(command.getName());
    	}
    }
    public static void showTopHelp(VPSender sender, String path, int page, CPNode node){
        List<CommandPlus>subs = new ArrayList<CommandPlus>();
        for(CommandPlus cmd : node.getSubs()){
        	if(cmd.hasRequirementSilent(sender))
        		subs.add(cmd);
        }
        showTopHelp(sender, path, page, subs);
    }
	private void showTopHelp(VPSender sender, String label, int page) {
        List<CommandPlus>subs = new ArrayList<CommandPlus>();
        for(CommandPlus cmd : this.getLoaded()){
        	if(cmd.is(PlaceH.HELP.get()))
        		continue;
        	if(cmd.hasRequirementSilent(sender)) {
        		subs.add(cmd);
        	}
        }
        showTopHelp(sender, label, page, subs);
	}
	private static void showTopHelp(VPSender sender, String label, int page, List<CommandPlus>subs) {
        if(page > getMaxPages(subs.size()))
        	page = getMaxPages(subs.size());
        if(page < 0)
        	page = 1;
    	topHelp.addReplacement(PlaceH.COMMAND.get(), label)
    	.addReplacement("page", page+"")
    	.addReplacement("page_max", getMaxPages(subs.size())+"").sendTo(sender);
        int from = 1;
        if (page > 1)
            from = 8 * (page - 1) + 1;
        int to = 8 * page;
        for (int h = from; h <= to; h++)
        	if(h>subs.size())
        		break;
        	else
        		showHelp(sender, label, subs.get(h-1), false);
	}
    public static void showHelp(VPSender sender, String path, CommandPlus command, boolean check){
    	if(!check || command.hasRequirementSilent(sender)) {
    		commandHelp.addCReplacement("usage", command.getUsage())
    		.addCReplacement("description", command.getDescription()).sendTo(sender);
    	}
    }
    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    private static int getMaxPages(int size) {
        int max = 8;
        int i = size;
        int j = i % max;
        return j == 0 ? i/max : ((i-j)/max + 1);
    }
    public static void sendNoPerm(VPSender sender){
    	noRequirement.sendTo(sender);
    }
	public boolean execute(CommandSender sender, String label, String[] args) {
		VPSender vpSender;
		if(sender instanceof Player)
			vpSender = VanillaPlusCore.getPlayerManager().getPlayer(((Player)sender).getUniqueId());
		else if(sender instanceof BlockCommandSender)
			vpSender = new VPCommandBlock((BlockCommandSender) sender);
		else if(sender instanceof ConsoleCommandSender)
			vpSender = VanillaPlusCore.getVPConsole();
		else
			return false;
		if(label.equalsIgnoreCase(PlaceH.HELP.get())){
			showTopHelp(vpSender, label, args.length > 0 ? Utils.parseInt(args[0], 1, false) : 1);
			return true;
		}
		for(CommandPlus command : getLoaded())
			if(command.is(label.toLowerCase())){
				if(command.hasRequirement(vpSender))
					command.onExecute(vpSender, label, new LinkedList<String>(Arrays.asList(args)));
				return true;
			}
		return false;
	}
	public List<String> tabComplete(CommandSender sender, String label,
			String[] args) {
		VPSender vpSender;
		if(sender instanceof Player)
			vpSender = VanillaPlusCore.getPlayerManager().getPlayer(((Player)sender).getUniqueId());
		else if(sender instanceof BlockCommandSender)
			vpSender = new VPCommandBlock((BlockCommandSender) sender);
		else if(sender instanceof ConsoleCommandSender)
			vpSender = VanillaPlusCore.getVPConsole();
		else
			return null;
		for(CommandPlus command : getLoaded())
			if(command.is(label)){
				if(command.hasRequirementSilent(vpSender))
				return command.onTab(vpSender, label, new LinkedList<String>(Arrays.asList(args)));
			}
		List<String>result = new ArrayList<String>();
		for(CommandPlus command : getLoaded()){
			String lower = label.toLowerCase();
			boolean tested = false;
			if(command.getName().startsWith(lower))
				if(command.hasRequirementSilent(vpSender)){
					result.add(command.getName());
					tested = true;
				}else
					continue;
			for(String s : command.getAliases()){
				if(s.startsWith(lower))
					if(tested || command.hasRequirementSilent(vpSender)){
						result.add(command.getName());
						tested = true;
					}else
						break;
			}
		}
		return result;
	}
	public class Link{
		CommandPlus cmd;
		boolean consume;
		boolean cancel;
		Link(ConfigurationSection section, MessageManager manager, String name){
			cmd = VanillaPlusCore.getCommandManager().create(section.getString(Node.TYPE.get(), Node.NODE.get()), section, manager, name);
			consume = section.getBoolean("CONSUME", false);
			cancel = section.getBoolean("CANCEL", true);
		}
		public void onExecute(VPPlayer sender) {
			if(cmd.onExecute(sender, cmd.getName(), new ArrayList<String>())) {
				if(consume){
					ItemStack stack = sender.getPlayer().getItemInHand();
					if(stack.getAmount() == 1)
						sender.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					else
						stack.setAmount(stack.getAmount()-1);
				}
			}
		}
	}
}

