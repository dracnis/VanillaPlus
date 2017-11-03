package fr.soreth.VanillaPlus.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Channel.Channel;
import fr.soreth.VanillaPlus.Data.SessionValue.BooleanSession;
import fr.soreth.VanillaPlus.Data.SessionValue.DoubleSession;
import fr.soreth.VanillaPlus.Data.SessionValue.IntSession;
import fr.soreth.VanillaPlus.Data.SessionValue.StringSession;
import fr.soreth.VanillaPlus.Menu.Menu;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.CraftPlayerUtils;
import fr.soreth.VanillaPlus.Utils.Minecraft.MinecraftUtils;

public class VPPlayer implements VPSender{
	private final int id;
	private UUID uuid;
	private Player player;
	private Localizer language;
	private VersusState versus;
	private Map<Integer, IntSession> statistics = new HashMap<Integer, IntSession>();
	private Map<Integer, BooleanSession> unlockedAchievement = new HashMap<Integer, BooleanSession>();
	private Map<Integer, BooleanSession> unlockedTitle = new HashMap<Integer, BooleanSession>();
	private Map<Integer, DoubleSession> currency = new HashMap<Integer, DoubleSession>();
	private Title title;
	private Menu menu;
	private IntSession titleId;
	private String name, prefix, suffix, prefixColor, suffixColor;
	StringSession nick;
	protected Channel currentChannel;
	private int offline, groupLevel;
	private HashMap<PlayerSettings, BooleanSession>settings;
	protected Location location;
	private List<PotionEffect>effects;
	private ItemStack[] inventory, armor;
	private Integer level, food;
	private GameMode gameMode;
	private boolean save = true;
	private boolean keep = false;
	private boolean changed = false;
	private HashMap<Plugin, Object>link = new HashMap<Plugin, Object>();
	private Float saturation;
	public VPPlayer(int id, UUID uuid) {
		this.uuid = uuid;
		this.id = id;
		this.language = VanillaPlusCore.getDefaultLang();
		this.versus = new VersusState();
	}
	public void setLanguage(Localizer language){
		if(VanillaPlusCore.isUsed(language))
			if(this.language != language){
				changed = true;
				this.language = language;
			}
	}
	public void setLanguage(String language){
		setLanguage(Localizer.getByCode(language));
	}
	public Localizer getLanguage(){
		return language;
	}
	public Player getPlayer() {
		return player;
	}
	public CommandSender getSender() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
		if(!player.hasMetadata("id"))
		player.setMetadata("id", new FixedMetadataValue(VanillaPlus.getInstance(), id));
	}
	public UUID getUUID() {
		return uuid;
	}
	public String getName() {
		return nick.get()==null ? name == null ? "§7" : name : nick.get();
	}
	public void setNick(String nick) {
		if(nick == null || nick.isEmpty()){
			if(this.nick.get() != null){
				this.nick.set(null);
				changed = true;
			}
		}
		else{
			String temp = nick.length() > 16 ? nick.substring(0, 15) : nick;
			if(!temp.equals(getNick())){
				changed = true;
				if(temp.equals(name))
					this.nick.set(null);
				else
					this.nick.set(temp);
			}
		}
	}
	public String getNick() {
		return this.nick.get();
	}
	public boolean isNick(){
		return this.nick.get() != null;
	}
	public String getRealName(){
		return name;
	}
	@Deprecated
	public void setRealName(String name){
		this.name = name;
	}
	public Channel getChannel(){
		return currentChannel;
	}
	public double getCurrency(int currencyId){
		return this.currency.get(currencyId).get();
	}
	public Map<Integer, DoubleSession> getCurrency() {
		return this.currency;
	}
	public void withdraw(int currencyId, double amount){
		if(amount <= 0) return;
		changed = true;
		this.currency.get(currencyId).add(-amount);
	}
	public void deposit(int currencyId, double amount) {
		if(amount <= 0) return;
		changed = true;
		this.currency.get(currencyId).add(amount);
	}
	public void setCurrency(int currencyId, double amount) {
		if(this.currency.get(currencyId).set(amount));
			changed = true;
	}
	public void setCurrency(int currencyId, DoubleSession value){
		this.currency.put(currencyId, value);
	}
	public void setChannel(Channel channel) {
		channel.addPlayer(this);
		this.currentChannel = channel;
	}
	public int getID() {
		return id;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof VPPlayer)
			return ((VPPlayer) obj).getID()==getID();
		else if(obj instanceof Player)
			return ((Player)obj).getUniqueId() == getUUID();
		return false;
	}
	@Override
	public int hashCode() {
		return id;
	}
	public Title getTitle() {
		return this.title;
	}
	public void setTitle(Title title){
		this.title = title;
		if(titleId == null)
			this.titleId = new IntSession(title.getID());
		else
			if(!(this.titleId.get() == (title == null ? 0 : title.getID()))){
				changed = true;
				titleId.set(title == null ? 0 : title.getID());
			}
	}
	public void upTitle(Title title){
		this.title = title;
		if(titleId == null)
			this.titleId = new IntSession(title == null ? 0 : title.getID());
		else{
			this.titleId.save();
			this.titleId.setLast(title == null ? 0 : title.getID());
		}
	}
	public IntSession getTitleSession(){
		if(titleId == null)
			titleId = new IntSession(0);
		return titleId;
	}
	public boolean titleChanged(){
		return titleId == null ? false : titleId.changed();
	}
	public void removeTitle(Title title){
		BooleanSession current = unlockedTitle.get(title.getID());
		if(current != null){
			current.set(false);
			if(this.title == title)
				this.title = null;
		}
	}
	public boolean hasTitle(int id) {
		BooleanSession current = unlockedTitle.get(id);
		if(current != null){
			return current.get();
		}
		return false;
	}
	public void setTitles(Map<Integer, BooleanSession> resultTitle){
		unlockedTitle = resultTitle;
	}
	public Map<Integer, BooleanSession> getTitles(){
		return unlockedTitle;
	}
	public void addTitle(int title){
		BooleanSession current = unlockedTitle.get(title);
		if(current == null){
			current = new BooleanSession(false);
			current.set(true);
			unlockedTitle.put(title, current);
			changed = true;
		}else{
			if(current.set(true))changed = true;
		}
	}
	public boolean isOnline() {
		return offline == -1 && player != null;
	}
	public void setOffline(){
		this.offline = 0;
	}
	public int getOffline(){
		return offline;
	}
	public void addOffline(){
		if(offline!=-1)
		this.offline ++;
	}
	public void setAchievements(Map<Integer, BooleanSession> resultAchievements){
		unlockedAchievement = resultAchievements;
	}
	public boolean hasAchievement(int id) {
		BooleanSession current = unlockedAchievement.get(id);
		if(current != null){
			return current.get();
		}
		return false;
	}
	public void addAchievements(int id){
		BooleanSession current = unlockedAchievement.get(id);
		if(current == null){
			current = new BooleanSession(false);
			current.set(true);
			unlockedAchievement.put(id, current);
			changed = true;
		}else
			if(current.set(true))changed = true;
	}
	public Map<Integer, BooleanSession> getAchievements(){
		return unlockedAchievement;
	}
	public Map<Integer, IntSession> getStatistics() {
		return statistics;
	}
	public void setStatistics(Map<Integer, IntSession> resultStatistics){
		statistics = resultStatistics;
	}
	public void upStat(int statId, int amount){
		if(amount == 0)return;
		changed = true;
		this.statistics
		.get(statId)
		.add(amount);
	}
	public int getStat(int statId) {
		IntSession value = this.statistics.get(statId);
		return (value == null ? 0 : value.get()); 
	}
	public int getSessionStat(int statId) {
		IntSession value = this.statistics.get(statId);
		return (value == null ? 0 : value.getSession()); 
	}
	public HashMap<PlayerSettings, BooleanSession> getSettings() {
		return settings;
	}
	public void setSettings(HashMap<PlayerSettings, BooleanSession>settings) {
		this.settings = settings;
	}
	public boolean getSetting(PlayerSettings setting){
		return this.settings.get(setting).get();
	}
	public void setSetting(PlayerSettings setting, boolean state){
		if(this.settings.get(setting).set(state))
			changed=true;
	}
	public void setPrefix(String prefix) {
		this.prefix = Utils.capitalize(prefix.replaceAll("&", "§"));
		if(prefix.length()>1)
			this.prefixColor = this.prefix.substring(this.prefix.length()-2);
		else
			this.prefixColor = "";
	}
	public String getPrefix(){
		return prefix == null ? "" : prefix;
	}
	public String getPrefixColor(){
		return prefixColor;
	}
	public void setSuffix(String suffix) {
		this.suffix = Utils.capitalize(suffix.replaceAll("&", "§"));
		if(suffix.length()>1)
			this.suffixColor = this.suffix.substring(this.suffix.length()-2);
		else
			this.suffixColor = "";
	}
	public String getSuffix(){
		return suffix == null ? "" : suffix;
	}
	public String getSuffixColor(){
		return suffixColor;
	}
	public void setGroupLevel(int level){
		this.groupLevel = level;
	}
	public int getGroupLevel(){
		return this.groupLevel;
	}
	public void teleport(Location location) {
		if(isOnline())
			player.teleport(location);
		else
			this.location = location;	
	}
	public VersusState getVersus(){
		return this.versus;
	}
	public void setLink(Plugin p, Object o){
		link.put(p, o);
	}
	public Menu getMenu() {
		return this.menu;
	}
	public void setMenu(Menu menu) {
		if(!isOnline() || this.menu == menu )return;
		if(this.menu == null) {
			this.inventory = getInventory(true).clone();
			ItemStack air = new ItemStack(Material.AIR);
			for(int i = 0 ; i < inventory.length ; i++) {
				if(menu.getIcon(this, i+1) == null || inventory[i] == null) {
					inventory[i] = air;
				}
			}
			this.menu = menu;
		}else if(menu == null) {
			ItemStack[] memory = getInventory(false);
			this.inventory = null;
			ItemStack[] current = getInventory(true).clone();
			for(int i = 0 ; i < memory.length ; i++) {
				if(this.menu.getIcon(this, i+1) == null) {
					memory[i] = current[i];
				}
			}
			this.menu = null;
			setInventory(memory, true);
		}else {
			setMenu(null);
			setMenu(menu);
		}
	}
	public Object getLink(Plugin p){
		return link.get(p);
	}
	public void addPotionEffect(PotionEffect potionEffect) {
		if(isOnline()){
			MinecraftUtils.applyEffect(this, potionEffect);
		}else{
			if(effects == null)
				effects = new ArrayList<PotionEffect>();
			effects.add(potionEffect);
		}		
	}
	public void setOnline(){
		this.offline = -1;
		if(location != null){
			player.teleport(location);
			location = null;
		}
		if(effects != null){
			for(PotionEffect effect : effects)
				MinecraftUtils.applyEffect(this, effect);
			effects = null;
		}
		if(inventory != null){
			player.getInventory().setContents(inventory);
			player.updateInventory();
			inventory = null;
		}
		if(armor != null){
			player.getInventory().setArmorContents(armor);
			player.updateInventory();
			armor = null;
		}
		if(gameMode != null){
			player.setGameMode(gameMode);;
			gameMode = null;
		}
		if(level != null){
			player.setLevel(level);
			player.setExp(0);
			level = null;
		}
		if(food != null){
			player.setFoodLevel(food);
			food = null;
		}
		if(saturation != null){
			player.setSaturation(saturation);
			saturation = null;
		}
	}
	public void setInventory(ItemStack[] inventory, boolean ignoreMenu) {
		if(isOnline() && ( this.menu == null || ignoreMenu ) ){			
			player.getInventory().setContents(inventory);
			player.updateInventory();
		}else{
			this.inventory = inventory;
		}
	}
	public void setArmor(ItemStack[] armor){
		if(isOnline()){
			player.getInventory().setArmorContents(armor);
			player.updateInventory();
		}else{
			this.armor = armor;
		}
	}
	public void setGameMode(GameMode gameMode) {
		if(isOnline()){
			player.setGameMode(gameMode);
		}else{
			this.gameMode = gameMode;
		}
	}
	public void setLevel(int level) {
		if(isOnline()){
			player.setLevel(level);
			player.setExp(0);
		}else{
			this.level = level;
		}
	}
	public void clearEffect(){
		if(isOnline()){
			for (PotionEffect effect : player.getActivePotionEffects())
				player.removePotionEffect(effect.getType());
		}else{
			setPotionEffect(new ArrayList<PotionEffect>());
		}
	}
	public void setPotionEffect(List<PotionEffect> effects){
		if(isOnline()){
			clearEffect();
			player.addPotionEffects(effects);
		}else{
			this.effects = effects;
		}
	}
	public Location getLocation() {
		if(location != null)
			return location;
		return player.getLocation();
	}
	public ItemStack[] getInventory(boolean ignoreMenu) {
		if(inventory != null && (!isOnline()  || !ignoreMenu))
			return inventory;
		return player.getInventory().getContents();
	}
	public ItemStack[] getArmor() {
		if(armor != null)
			return armor;
		return player.getInventory().getArmorContents();
	}
	public int getLevel() {
		if(level != null)
			return level;
		return player.getLevel();
	}
	public GameMode getGameMode() {
		if(gameMode != null)
			return gameMode;
		return player.getGameMode();
	}
	public List<PotionEffect> getPotionEffect() {
		if(effects != null)
			return effects;
		List<PotionEffect>effects = new ArrayList<PotionEffect>();
		effects.addAll(player.getActivePotionEffects());
		return effects;
	}
	public int getFood() {
		if(food != null)
			return food;
		return player.getFoodLevel();
	}
	public void setFood(int value){
		if(isOnline()){
			player.setFoodLevel(value);
		}else
			food = value;
	}
	public float getSaturation() {
		if(saturation != null)
			return saturation;
		return player.getSaturation();
	}
	public void setSaturation(float value){
		if(isOnline()){
			player.setSaturation(value);
		}else
			saturation = value;
	}
	public void clearInventory(boolean ignoreMenu) {
		setInventory(new ItemStack[getInventory(true).length], ignoreMenu);
	}
	public void clearArmor() {
		setArmor(new ItemStack[getArmor().length]);
	}
	public void heal(double amount) {
		double max = player.getMaxHealth() - player.getHealth();
		player.setHealth(max < amount ? (max + player.getHealth()) : player.getHealth() + amount);
	}
	public void heal() {
		player.setHealth(player.getMaxHealth());
	}
	public void feed() {
		setFood(20);
		setSaturation(20);
	}
	public boolean save() {
		return save && changed;
	}
	public void setSave(boolean state) {
		this.save = state;
	}
	public boolean keep() {
		return keep;
	}
	public void setKeep(boolean state) {
		this.keep = state;
	}
	public double getAbso() {
		return CraftPlayerUtils.getAbso(getPlayer());
	}
	@Deprecated
	public void saved() {
		this.changed = false;
	}
	public void vanilla() {
		if(player == null)return;
		player.setWalkSpeed(0.2f);
		player.setMaxHealth(20.0);
	}
	public void kill(){
		VPPlayer killer = versus.getLastDamager();
		VanillaPlusCore.getVersusManager().kill(this, killer, null, DamageCause.CUSTOM, killer != null);
	}
	@Override
	public void sendMessage(String message) {
		if(player != null && isOnline())
			player.sendMessage(message);
	}
}
