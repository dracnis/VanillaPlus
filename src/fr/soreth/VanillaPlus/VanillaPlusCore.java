package fr.soreth.VanillaPlus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.soreth.VanillaPlus.Channel.ChannelManager;
import fr.soreth.VanillaPlus.Command.CPManager;
import fr.soreth.VanillaPlus.Data.IConnectionManager;
import fr.soreth.VanillaPlus.Event.PostLoadEvent;
import fr.soreth.VanillaPlus.Event.SimpleLoadEvent;
import fr.soreth.VanillaPlus.IRequirement.IRequirementManager;
import fr.soreth.VanillaPlus.IReward.IRewardManager;
import fr.soreth.VanillaPlus.Icon.IconManager;
import fr.soreth.VanillaPlus.Menu.MenuManager;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.PH.PlaceHolderManager;
import fr.soreth.VanillaPlus.Player.AchievementManager;
import fr.soreth.VanillaPlus.Player.CurrencyManager;
import fr.soreth.VanillaPlus.Player.PlayerManager;
import fr.soreth.VanillaPlus.Player.TitleManager;
import fr.soreth.VanillaPlus.Player.VersusManager;
import fr.soreth.VanillaPlus.SPH.SPlaceHolderManager;
import fr.soreth.VanillaPlus.StatType.StatManager;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

/**
 * Main class of the API.
 *
 * @author Soreth
 */
public class VanillaPlusCore implements VanillaPlusExtension{
	private static final String bukkitVersion				= Bukkit.getServer().getClass().getPackage().getName().substring(
			Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
	private static final int bukkitVersionID				= Utils.parseInt(VanillaPlusCore.bukkitVersion.replaceAll("[vR]", "").replaceAll("_", "0"), 0, true);
	private static final Random random						= new Random();
	private static final VPConsole console					= new VPConsole();
	private static final VanillaPlus instance				= VanillaPlus.getInstance();
	private static YamlConfiguration config					= ConfigUtils.getYaml(instance, "config", true);
	private static Localizer defaultLang					= Localizer.getByCode(VanillaPlusCore.config.getString(Node.LANG.get()), Localizer.ENGLISH);
	private static final List<Localizer> langs				= loadLangs();
	private static final boolean isSpigot					= ReflectionUtils.getClass("net.md_5.bungee.api.chat.BaseComponent") != null;
	private static boolean isBungee							= Bukkit.getPluginManager().getPlugin("ExtensionBungee") != null;
	private static List<Localizer> loadLangs(){
		List<Localizer>result = new ArrayList<>();
		result.add(defaultLang);
		
			for(String lang : config.getStringList(Node.LANG.getList())){
				Localizer local = Localizer.getByCode(lang);
				if(local == null || result.contains(local)) {
					continue;
				}
				result.add(local);
			}
			return result;
	}
	
	private static final AchievementManager achievementM	= new AchievementManager();
	private static final ChannelManager channelM			= new ChannelManager(instance);
	private static final CPManager commandM					= new CPManager(instance);
	private static final CurrencyManager currencyM			= new CurrencyManager();
	private static final ExtraManager extraM				= new ExtraManager(instance);
	private static final IconManager iconM					= new IconManager();
	private static final IConnectionManager iConnectionM	= new IConnectionManager();
	private static final IRequirementManager iRequirementM	= new IRequirementManager();
	private static final IRewardManager iRewardM			= new IRewardManager();
	private static final MenuManager menuM					= new MenuManager();
	private static final MComponentManager mCM				= new MComponentManager(instance);
	private static final MessageManager messageM			= new MessageManager(mCM);
	private static final PlaceHolderManager pHM				= new PlaceHolderManager();
	private static PlayerManager pM;
	private static final SPlaceHolderManager sPHM			= new SPlaceHolderManager();
	private static final StatManager statM					= new StatManager();
	private static final TitleManager titleM				= new TitleManager();
	private static final VersusManager versusM				= new VersusManager();
	public VanillaPlusCore() {
		VanillaPlusCore.console.setNick(config.getString("CONSOLE_NAME", "@Console"));
		File menusFolder = new File(instance.getDataFolder(), "Menu");
		if (!menusFolder.isDirectory()) {
			String menuPrefix = "Menu" + File.separatorChar;
			ConfigUtils.copyFiles(menuPrefix, Arrays.asList("Achievement.yml", "Lang.yml", "Lobby.yml", "Menu.yml",
				"Restriction.yml", "Time.yml", "Title.yml"), instance);
		}
		pM	= new PlayerManager(this);
		Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			@Override
			public void run() {
				init(instance);
			}
		}, 1);
	}
	@Deprecated
	static void disable() {
		menuM.disable();
		pM.disable();
		iConnectionM.disable();
	}
	@SuppressWarnings("deprecation")
	private void init(VanillaPlus vanillaPlus){
		
		SimpleLoadEvent event = new SimpleLoadEvent();
		Bukkit.getServer().getPluginManager().callEvent(event);
		event.addExtension(this);
		
		mCM				.init(config.getConfigurationSection("MESSAGE_COMPONENT"));

		for(VanillaPlusExtension extension : event.getExtensions()) {
			ErrorLogger.addPrefix(extension.getInstance().getName());
			channelM		.init(extension);
			iConnectionM	.init(extension);
			extraM			.init(extension);
			titleM			.init(extension);
			currencyM		.init(extension);
			achievementM	.init(extension);
			ErrorLogger.removePrefix();
		}
		currencyM			.init(this);
		for(VanillaPlusExtension extension : event.getExtensions()) {
			ErrorLogger.addPrefix(extension.getInstance().getName());
			statM			.init(extension);
			ErrorLogger.removePrefix();
		}
		statM				.init(this);

		for(VanillaPlusExtension extension : event.getExtensions()) {
			ErrorLogger.addPrefix(extension.getInstance().getName());
			pHM				.init(ConfigUtils.getYaml(extension.getInstance(), "PlaceHolder", false), extension.getMessageCManager());
			iRequirementM	.init(extension);
			iRewardM		.init(extension);
			ErrorLogger.removePrefix();
		}
		iRequirementM		.init(this);
		titleM				.init(this);
		channelM			.init(this);
		for(VanillaPlusExtension extension : event.getExtensions()) {
			ErrorLogger.addPrefix(extension.getInstance().getName());
			achievementM	.initPost(extension);
			menuM			.add(extension);
			ErrorLogger.removePrefix();
		}
		achievementM		.init(this);
		menuM				.start(config.getConfigurationSection("MENU"));

		for(VanillaPlusExtension extension : event.getExtensions()) {
			ErrorLogger.addPrefix(extension.getInstance().getName());
			iconM			.init(extension);
			menuM			.add(extension);
			ErrorLogger.removePrefix();
		}
		iconM				.init(this);
		menuM				.postIconInit();
		for(VanillaPlusExtension extension : event.getExtensions()) {
			ErrorLogger.addPrefix(extension.getInstance().getName());
			versusM			.init(extension);
			commandM		.init(extension);
			ErrorLogger.removePrefix();
		}
		versusM			.init(this);
		commandM		.init(this);
		pM				.init(config.getConfigurationSection("PLAYER"));
		
		Bukkit.getServer().getPluginManager().callEvent(new PostLoadEvent());
	}
    /**
     * Test if server is running with Spigot API.
     *
     * @return true if use.
     */
	public static boolean isSpigot(){
		return isSpigot;
	}
    /**
     * Test if server is running with ExtensionBungee.
     *
     * @return true if use.
     */
	public static boolean isBungee(){
		return isBungee;
	}
    /**
     * Test if the given language is used by the API.
     *
     * @param language the language to check.
     * @return If used.
     */
	public static boolean isUsed(Localizer language){
		return langs.contains(language);
	}
    /**
     * Get the Bukkit version.
     * Useful for reflection.
     *
     * @return The Bukkit version.
     */
	public static String getBukkitVersion() {
		return bukkitVersion;
	}
    /**
     * Get the Bukkit version id.
     * Useful for reflection compare.
     *
     * @return The Bukkit version id, v1_8_R3 become 10803.
     */
	public static int getBukkitVersionID() {
		return bukkitVersionID;
	}
    /**
     * Get the default language.
     *
     * @return The default language.
     */
	public static Localizer getDefaultLang(){
		return defaultLang;
	}
    /**
     * Get the list of configured languages.
     *
     * @return The list of configured languages.
     */
	public static List<Localizer> getLangs(){
		List<Localizer>result = new ArrayList<Localizer>();
		result.addAll(langs);
		return result;
	}
    /**
     * Get a random used by API.
     *
     * @return The API's random.
     */
	public static Random getRandom(){
		return random;
	}
    /**
     * Get a random used by API.
     *
     * @param collection The collection of object to retrieve a random.
     * @return The random object, null if collection is null or empty.
     */
	public static <T> T getRandom(Collection<T>collection){
		if(collection == null || collection.isEmpty())return null;
		return getObject(collection, random.nextInt(collection.size()));
	}

    /**
     * Get an object in collection by id.
     *
     * @param collection The collection of object to retrieve.
     * @param id The object's id.
     * @return The object, null if collection is null or empty or if id < than 0 or greater than collection size.
     */
	public static <T> T getObject(Collection<T>collection, int id){
		if(collection == null || collection.isEmpty() || id < 0 || id >= collection.size())return null;
		for(T current : collection) {
			if(id == 0)
				return current;
			id --;
		}	
		return null;
	}

    /**
     * Get the AchievementManager.
     * Add error to logger if null.
     *
     * @return The API's AchievementManager.
     */
	public static AchievementManager getAchievementManager() {
		if(achievementM == null) Error.MISSING.add("AchievementManager"); return achievementM;
	}
    /**
     * Get the ChannelManager.
     * Add error to logger if null.
     *
     * @return The API's ChannelManager.
     */
	public static ChannelManager getChannelManager(){
		if(channelM == null) Error.MISSING.add("ChannelManager");	return channelM;
	}
    /**
     * Get the CommandManager.
     * Add error to logger if null.
     *
     * @return The API's CommandManager.
     */
	public static CPManager getCommandManager(){
		if(commandM == null) Error.MISSING.add("CommandManager");	return commandM;
	}
    /**
     * Get the CurrencyManager.
     * Add error to logger if null.
     *
     * @return The API's CurrencyManager.
     */
	public static CurrencyManager getCurrencyManager(){
		if(currencyM == null) Error.MISSING.add("CurrencyManager"); return currencyM;
	}
    /**
     * Get the CustomFoodManager.
     * Add error to logger if null.
     *
     * @return The API's CustomFoodManager.
     */
	public static ExtraManager getCustomFoodManager(){
		if(extraM == null) Error.MISSING.add("ExtraManager"); return extraM;
	}
    /**
     * Get the IconManager.
     * Add error to logger if null.
     *
     * @return The API's IconManager.
     */
	public static IconManager getIconManager(){
		if(iconM == null)	Error.MISSING.add("IconManager"); return iconM;
	}
    /**
     * Get the IConnectionManager.
     * Add error to logger if null.
     *
     * @return The API's IConnectionManager.
     */
	public static IConnectionManager getIConnectionManager(){
		if(iConnectionM == null)	Error.MISSING.add("IConnectionManager"); return iConnectionM;
	}
    /**
     * Get the IRequirementManager.
     * Add error to logger if null.
     *
     * @return The API's IRequirementManager.
     */
	public static IRequirementManager getIRequirementManager(){
		if(iRequirementM == null) Error.MISSING.add("IRequirementManager"); return iRequirementM;
	}
    /**
     * Get the IRewardManager.
     * Add error to logger if null.
     *
     * @return The API's IRewardManager.
     */
	public static IRewardManager getIRewardManager(){
		if(iRewardM == null) Error.MISSING.add("IRewardManager"); return iRewardM;
	}
    /**
     * Get the Core instance.
     * Add error to logger if null.
     *
     * @return The API's Core.
     */
	@Override
	public Plugin getInstance() {
		if(instance == null) Error.MISSING.add("Core"); return instance;
	}
    /**
     * Get the MenuManager.
     * Add error to logger if null.
     *
     * @return The API's MenuManager.
     */
	public static MenuManager getMenuManager(){
		if(menuM == null)	Error.MISSING.add("MenuManager"); return menuM;
	}
    /**
     * Get the MessageBaseManager.
     * Add error to logger if null.
     *
     * @return The API's MessageBaseManager.
     */
	public MComponentManager getMessageCManager() {
		if(mCM == null)	ErrorLogger.addError("[!]Critical ! No MComponentManager found, contact developper !");
		return mCM;
	}
    /**
     * Get the MessageManager.
     * Add error to logger if null.
     *
     * @return The API's MessageManager.
     */
	public MessageManager getMessageManager(){
		if(messageM == null) Error.MISSING.add("MessageManager"); return messageM;
	}
    /**
     * Get the PlaceHolderManager.
     * Add error to logger if null.
     *
     * @return The API's PlaceHolderManager.
     */
	public static PlaceHolderManager getPHManager() {
		if(pHM == null) Error.MISSING.add("PlaceHolderManager");	return pHM;
	}
    /**
     * Get the PlayerManager.
     * Add error to logger if null.
     *
     * @return The API's PlayerManager.
     */
	public static PlayerManager getPlayerManager(){
		if(pM == null) Error.MISSING.add("PlayerManager"); return pM;
	}
    /**
     * Get the TitleManager.
     * Add error to logger if null.
     *
     * @return The API's TitleManager.
     */
	public static TitleManager getTitleManager() {
		if(titleM == null) Error.MISSING.add("TitleManager");	return titleM;
	}
    /**
     * Get the PlayerPlaceHolderManager.
     * Add error to logger if null.
     *
     * @return The API's SPlaceHolderManager.
     */
	public static SPlaceHolderManager getSPlaceHolderManager(){
		if(sPHM == null) Error.MISSING.add("SPlaceHolderManager"); return sPHM;
	}
    /**
     * Get the StatManager.
     * Add error to logger if null.
     *
     * @return The API's StatManager.
     */
	public static StatManager getStatManager() {
		if(statM == null) Error.MISSING.add("StatManager"); return statM;
	}
    /**
     * Get the VersusManager.
     * Add error to logger if null.
     *
     * @return The API's VersusManager.
     */
	public static VersusManager getVersusManager(){
		if(versusM == null) Error.MISSING.add("VersusManager"); return versusM;
	}
    /**
     * Get the VPConsole.
     * Add error to logger if null.
     *
     * @return The API's VPConsole.
     */
	public static VPConsole getVPConsole(){
		if(console == null) Error.MISSING.add("VPConsole"); return console;
	}

    /**
     * Set the default server language but will not save.
     *
     * @param language The language to set
     */
	public static void setDefaultLang(Localizer language) {
		if(language == null || langs.contains(language))
			return;
		defaultLang = language;
	}
}
