package fr.soreth.VanillaPlus.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Data.Column;
import fr.soreth.VanillaPlus.Data.Column.Type;
import fr.soreth.VanillaPlus.Data.IConnection;
import fr.soreth.VanillaPlus.Data.IResultQuery;
import fr.soreth.VanillaPlus.Data.Table;
import fr.soreth.VanillaPlus.Data.Table.IUpdateQuery;
import fr.soreth.VanillaPlus.Data.SessionValue.StringSession;
import fr.soreth.VanillaPlus.Event.AsyncPlayerPlusPreLoginEvent;
import fr.soreth.VanillaPlus.Event.VPPJoinEvent;
import fr.soreth.VanillaPlus.Event.VPPLeaveEvent;
import fr.soreth.VanillaPlus.Event.VPPLoadEvent;
import fr.soreth.VanillaPlus.Event.VPPLoginEvent;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Utils.MediumEntry;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;
import fr.soreth.VanillaPlus.Utils.Minecraft.CraftPlayerUtils;

public class PlayerManager extends Manager<String, VPPDisplay> implements Listener {
	private HashMap<UUID, VPPlayer>players;
	private Table tablePlayer;
	private IConnection connection;
	private Message join, leave, pjoin;
	//private MComponent prefix, suffix;
	private Requirement msgPerm, errorPerm, bypassPM, bypassSpawnTp, gmTeleport;
	private int offline;
	private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DecimalFormat intFormat = (DecimalFormat) NumberFormat.getNumberInstance();
	private Location serverSpawn;
	private Team nick;
	private boolean init = false;
	private VPPDisplay vppDisplay;
	private Column id = new Column("id", Type.INTEGER, null, true, true, false, true);
	private Column uuid = new Column("uuid", Type.STRING_32, null, true, false, true, false);
	private Column username = new Column("username", Type.VARSTRING_16, null, true, false, false, false);
	private Column nickname = new Column("nickname", Type.VARSTRING_16);
	private Column lang = new Column("lang", Type.STRING_5);
	private Column title = new Column("title", Type.INTEGER, 0, true, false, false, false);
	private Column settings = new Column("settings", Type.INTEGER, 0, true, false, false, false);
	private Column firstcoo = new Column("firstcoo", Type.TIME, "current", true, false, false, false);
	private final VanillaPlusCore core;
	public PlayerManager(VanillaPlusCore core){
		super(String.class, VPPDisplay.class);
		if(core == null) {
			this.core = null;
			return;
		}
		this.core = core;
		intFormat.applyPattern("_VP_0000");
		players = new HashMap<UUID, VPPlayer>();
		join = core.getMessageManager().get(null);
		leave = core.getMessageManager().get(null);
		pjoin = core.getMessageManager().get(null);
		offline = 5;
		serverSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
		if(serverSpawn == null)
			serverSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
	}
	@SuppressWarnings("deprecation")
	public void init(ConfigurationSection section){
		if(init)return;
		init = true;
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(core.getInstance(), new Runnable() {
			@Override
			public void run() {
				for(VPPlayer player : getPlayers("", false)){
					if(!player.isOnline()){
						if(player.getOffline() >= offline && !player.keep()){
							if(player.save())
								save(player, false);
							players.remove(player.getUUID());
						}else
							player.addOffline();
					}
				}
			}
		}, 0, 20*60);
		ErrorLogger.addPrefix("config.yml => PLAYER");
		if(section != null){
			Bukkit.getServer().getPluginManager().registerEvents(this, core.getInstance());
			join = core.getMessageManager().get(section.getString("JOIN"));
			leave = core.getMessageManager().get(section.getString("LEAVE"));
			pjoin = core.getMessageManager().get(section.getString("PJOIN"));
			vppDisplay = create(section.getString("TAB_DISPLAY", Node.BASE.get()));
			int num = section.getInt("NICK_PRIORITY", 1000);
			if(num > 9999) num = 9999;
			String temp = intFormat.format(num);
			nick = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(temp);
			if(nick == null)
				nick = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(temp);
			if(section.contains("NICK_PREFIX")) {
				String nickPrefix = ChatColor.translateAlternateColorCodes('&', section.getString("NICK_PREFIX", ""));
				nick.setPrefix(nickPrefix.length() > 16 ? nickPrefix.substring(0, 16) : nickPrefix);
			}
			if(section.contains("NICK_SUFFIX"))
				nick.setSuffix(ChatColor.translateAlternateColorCodes('&', section.getString("NICK_SUFFIX", "")));
			serverSpawn = ConfigUtils.loadLocation(section.getConfigurationSection("SERVER_SPAWN"));
			offline = section.getInt("KEEP_IN_MEMORY", 5);
			msgPerm 		= new Requirement(section.get("MESSAGE_REQUIREMENT"), core.getMessageCManager());
			errorPerm 		= new Requirement(section.get("ERROR_REQUIREMENT"), core.getMessageCManager());
			gmTeleport 		= new Requirement(section.get("GM_TELEPORT"), core.getMessageCManager());
			bypassPM 		= new Requirement(section.get("BYPASS_PM_REQUIREMENT"), core.getMessageCManager());
			bypassSpawnTp 	= new Requirement(section.get("BYPASS_SPAWN_TP"), core.getMessageCManager());
			connection		= VanillaPlusCore.getIConnectionManager().get(section.getString("STORAGE"));
			
		}
    	startDataBase();
		for(Player p : Bukkit.getOnlinePlayers()){
			MediumEntry<VPPlayer, Boolean, Boolean> result = asyncPreLoad(p.getUniqueId(), p.getName());
			if(result == null)
				continue;
			VPPlayer player = result.getKey();
			player = preLoad(p);
			player.setOnline();
			load(player);

			AsyncPlayerPlusPreLoginEvent e = new  AsyncPlayerPlusPreLoginEvent(player);
			e.setLoginResult(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED);
			Bukkit.getServer().getPluginManager().callEvent(e);
			
			VPPLoginEvent e2 = new  VPPLoginEvent(player);
			e2.setResult(Result.ALLOWED);
			Bukkit.getServer().getPluginManager().callEvent(e2);
			
			VPPJoinEvent e3 = new  VPPJoinEvent(player);
			if(!bypassSpawnTp.has(player)){
				e3.setSpawnLocation(serverSpawn);
			}
			e3.setMessage(join);
			e3.setShowMessage(getMsgPerm().has(player));
			Bukkit.getServer().getPluginManager().callEvent(e3);
			if(e3.getMessage()!=null && e3.showMessage())
				e3.getMessage().addSReplacement(PlaceH.SENDER.get(), player).send();
			if(e3.getSpawnLocation()!=null)
				player.getPlayer().teleport(e3.getSpawnLocation());
			
			if(ErrorLogger.getSize()!=0)
				if(errorPerm.has(player)){
					ErrorLogger.sendError(p);
				}
		}
		ErrorLogger.removePrefix();
	}
	@EventHandler
	public void gmTp(PlayerTeleportEvent event) {
		if(event.getCause() == TeleportCause.SPECTATE && !gmTeleport.has(getPlayer(event.getPlayer()))){
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void playerLogin(AsyncPlayerPreLoginEvent event){
		if(!init){
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "");
			return;
		}
		MediumEntry<VPPlayer, Boolean, Boolean> result = asyncPreLoad(event.getUniqueId(), event.getName());
		if(result == null){
			ErrorLogger.addError("Couldn't asyncPreLoad " + event.getName());
			event.setLoginResult(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			//TODO add config message;
			event.setKickMessage("");
			return;
		}
		VPPlayer player = result.getKey();
		if(player.isOnline()){
			event.setLoginResult(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			//player.setOffline();
			return;
		}
		if(result.getExtraValue()) {
			Bukkit.getServer().getPluginManager().callEvent(new VPPLoadEvent(player, result.getValue()));
		}
		AsyncPlayerPlusPreLoginEvent e = new  AsyncPlayerPlusPreLoginEvent(player);
		e.setLoginResult(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED);
		Bukkit.getServer().getPluginManager().callEvent(e);
		event.setLoginResult(e.getLoginResult());
	}
	@SuppressWarnings("deprecation")
	private MediumEntry<VPPlayer, Boolean, Boolean> asyncPreLoad(UUID uuid, String name) {
		VPPlayer playerPlus = getPlayer(uuid);
		IResultQuery result = (playerPlus == null) ? tablePlayer.select().where(this.uuid, uuid.toString().replace("-", "")).execute()
				: tablePlayer.select().where(this.id, playerPlus.getID()).execute();
		boolean newplayer = false, load = false;
		if(!result.next()){
			newplayer = true;
			result.close();
			Localizer lang = VanillaPlusCore.getDefaultLang();
               tablePlayer.insert()
               .insert(this.uuid, uuid.toString().replace("-", ""))
               .insert(this.username, name)
               .insert(this.lang, lang.getCode())
               .insert(this.settings, PlayerSettings.toSettings())
               .insert(this.firstcoo, LocalDateTime.now().format(format))
               .execute();
    		result = tablePlayer.select().where(this.uuid, uuid.toString().replace("-", "")).execute();
    		if(!result.next()){
    			ErrorLogger.addError("Can't load player : " + name);
    			result.close();
    			return null;
    		}
		}
		String temp = result.getString(this.nickname);
		if(playerPlus == null){
			load = true;
			int id = result.getInt(this.id);
			playerPlus = new VPPlayer(id, uuid);
			players.put(uuid, playerPlus);
			playerPlus.nick = new StringSession(temp);
		}
		playerPlus.nick.load(temp);
		playerPlus.setLanguage(Localizer.getByCode(result.getString(this.lang)));
		if(temp != null && temp.equals(name)){
			playerPlus.setRealName(result.getString(this.username));
		}else
			playerPlus.setRealName(name);
		playerPlus.upTitle(VanillaPlusCore.getTitleManager().get(result.getInt(this.title)));
		playerPlus.setSettings(PlayerSettings.toSettings(result.getInt(this.settings)));
		result.close();
		VanillaPlusCore.getStatManager().load(playerPlus);
		VanillaPlusCore.getTitleManager().load(playerPlus);
		VanillaPlusCore.getCurrencyManager().load(playerPlus);
		VanillaPlusCore.getAchievementManager().load(playerPlus);
		return new MediumEntry<VPPlayer, Boolean, Boolean>(playerPlus, newplayer, load);
	}
	@EventHandler
	public void playerLogin(PlayerLoginEvent event){
		VPPlayer player = preLoad(event.getPlayer());
		if(player == null){
			ErrorLogger.addError("Couldn't preload " + event.getPlayer());
			event.setResult(Result.KICK_OTHER);
			return;
		}
		if(player.isOnline()){
			event.setResult(Result.KICK_OTHER);
			//player.setOffline();
			return;
		}
		VanillaPlusCore.getChannelManager().init(player);
		VPPLoginEvent e = new VPPLoginEvent(player);
		e.setResult(Result.ALLOWED);
		Bukkit.getServer().getPluginManager().callEvent(e);
		event.setResult(e.getResult());
	}
	@SuppressWarnings("deprecation")
	private VPPlayer preLoad(Player player) {
		VPPlayer playerPlus = getPlayer(player);
		if(playerPlus == null){
			player.kickPlayer("§4CRITCAL ERROR");
			return null;
		}
		playerPlus.setRealName(player.getName());
		playerPlus.setPlayer(player);
		VanillaPlusCore.getChannelManager().init(playerPlus);
		VanillaPlusCore.getStatManager().load(playerPlus);
		VanillaPlusCore.getAchievementManager().load(playerPlus);
		VanillaPlusCore.getTitleManager().load(playerPlus);
		VanillaPlusCore.getCurrencyManager().load(playerPlus);
		return playerPlus;
	}
	@EventHandler
	public void playerJoin(PlayerJoinEvent event){
		event.setJoinMessage("");
		final VPPlayer player = getPlayer(event.getPlayer());
		player.setOnline();
		load(player);
		if(ErrorLogger.getSize()!=0)
			if(errorPerm.has(player)){
				ErrorLogger.sendError(event.getPlayer());
			}
		VPPJoinEvent e = new  VPPJoinEvent(player);
		if(!bypassSpawnTp.has(player)){
			e.setSpawnLocation(serverSpawn);
		}
		e.setMessage(join);
		e.setShowMessage(getMsgPerm().has(player));
		Bukkit.getServer().getPluginManager().callEvent(e);
		pjoin.sendTo(player);
		if(e.getSpawnLocation()!=null)
			player.teleport(e.getSpawnLocation());
		if(e.getMessage()!=null && e.showMessage())
			e.getMessage().addSReplacement(PlaceH.SENDER.get(), player).send();
	}
	
	private void load(VPPlayer playerPlus){
		refresh(playerPlus);
		refreshName(playerPlus);
		if(VanillaPlusCore.getMenuManager().getDefault() != null){
			playerPlus.setMenu(VanillaPlusCore.getMenuManager().getDefault());
			VanillaPlusCore.getMenuManager().getDefault().open(playerPlus, null, null);
		}
	}
	@EventHandler
	public void playerLeave(PlayerQuitEvent event){
		event.setQuitMessage("");
		VPPlayer player = getPlayer(event.getPlayer());
		player.setMenu(null);
		VPPLeaveEvent e = new  VPPLeaveEvent(player);
		if(getMsgPerm().has(player))
			e.setMessage(leave);
		e.save(player.save());
		player.setOffline();
		Bukkit.getServer().getPluginManager().callEvent(e);
		//TODO send removed event
		if(e.getMessage() != null)
			e.getMessage().addSReplacement(PlaceH.SENDER.get(), player).send();
		if(e.save())
			save(player, false);
	}
	public void save(final VPPlayer player, boolean force){
		BukkitRunnable r = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				IUpdateQuery query = tablePlayer.update();
				if(true)//TODO find a way to do this
					query.set(username, player.getRealName());
				if(true)//TODO find a way to do this
					query.set(lang, player.getLanguage().getCode());
				if(player.titleChanged())
					query.set(title, player.getTitle() == null ? 0 : player.getTitle().getID());
				if(player.nick.changed())
					query.set(nickname, player.getNick());
				int setting = PlayerSettings.toIntChange(player.getSettings());
				if(setting != 0)
					query.add(settings, setting);
				query.where(id, player.getID()).execute();
				if(true) // TODO allow disable
					VanillaPlusCore.getStatManager().save(player);
				if(true) // TODO allow disable
					VanillaPlusCore.getAchievementManager().save(player);
				if(true) // TODO allow disable
					VanillaPlusCore.getTitleManager().save(player);
				if(true) // TODO allow disable
					VanillaPlusCore.getCurrencyManager().save(player);
				player.saved();
			}};
		if(force)
			r.run();
		else
			r.runTaskLaterAsynchronously(core.getInstance(), 0);
		
	}
	public VPPlayer getPlayer(Player player){
		return players.get(player.getUniqueId());
	}
	public VPPlayer getPlayer(UUID uuid){
		return players.get(uuid);
	}
    private void startDataBase() {
		tablePlayer = connection.getTable("VPPlayer")
		.addColumn(id)			.addColumn(uuid)		.addColumn(username)	.addColumn(nickname)
		.addColumn(lang)		.addColumn(title)		.addColumn(settings)	.addColumn(firstcoo)
		.validate();
    }
	public List<VPPlayer> getOnlinePlayers() {
		List<VPPlayer>result = new ArrayList<VPPlayer>();
		for(VPPlayer p : this.players.values()){
			if(p.getPlayer() != null && p.isOnline())
				result.add(p);
		}
		return result;
	}
	public VPPlayer getPlayer(String string) {
		for(VPPlayer p : this.players.values()){
			if(p.getPlayer() != null && p.getName().equalsIgnoreCase(string))
				return p;
		}
		return null;
	}
	public void disable() {
		for(VPPlayer p : getOnlinePlayers()){
			if(p.isNick())
				CraftPlayerUtils.setName(p, p.getRealName());
			if(p.save())
				save(p, true);
			p.setMenu(null);
		}
	}
	public Location getServerSpawn(){
		return serverSpawn == null ? Bukkit.getWorlds().get(0).getSpawnLocation() : serverSpawn;
	}
	public void setServerSpawn(Location location) {
		serverSpawn = location;
	}
	public List<String> getPlayersList(String prefix, boolean online){
		prefix = prefix.toLowerCase();
		List<String>result = new ArrayList<String>();
		for(VPPlayer player : getPlayers(prefix, online))
			result.add(player.isNick() ? player.getName().replace('§', '&') : player.getName());
		return result;
	}
	public List<VPPlayer> getPlayers(String prefix, boolean online){
		prefix = prefix.toLowerCase();
		List<VPPlayer>result = new ArrayList<VPPlayer>();
		for(VPPlayer player : this.players.values()){
			if(player.getPlayer() != null && (player.isOnline() || !online))
				if(player.getName().toLowerCase().startsWith(prefix))
					result.add(player);
		}
		return result;
	}
	public void refresh(VPPlayer p){
		vppDisplay.refresh(p);
	}
	public void refreshTeam(VPPlayer player){
		Team t2 = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player.getPlayer());
		if(t2 != null && !t2.getName().startsWith("_VP_"))
			return;
		if(player.isNick()){
			nick.addPlayer(player.getPlayer());
		}else{
			String name = intFormat.format(1000-player.getGroupLevel());
			Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name);
			if(t == null)
				t = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(name);
			if(player.getPrefix().length()>16)
				t.setPrefix(player.getPrefix().substring(0, 16));
			else
				t.setPrefix(player.getPrefix());
			if(player.getSuffix().length()>16)
				t.setSuffix(player.getSuffix().substring(0, 16));
			else
				t.setSuffix(player.getSuffix());
			t.addPlayer(player.getPlayer());
		}
	}
	public void refreshName(final VPPlayer player){
		if((player.getRealName().equals(player.getPlayer().getName()) && !player.isNick()) ||
				player.getPlayer().getName().equals(player.getNick())){
			refreshTeam(player);
			return;
		}
		final List<Player>toUp = new ArrayList<Player>();
		for(Player play : Bukkit.getOnlinePlayers()){
			if ((!play.equals(player.getPlayer()))){
				if(play.canSee(player.getPlayer())){
					play.hidePlayer(player.getPlayer());
					toUp.add(play);
				}
			}
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				CraftPlayerUtils.setName(player, player.getName());
				for(Player play : toUp)
					play.showPlayer(player.getPlayer());
				refreshTeam(player);
				CraftPlayerUtils.updateSelf(player);
			}
		}.runTaskLater(core.getInstance(), 1);
	}
	public void refreshSkin(final VPPlayer player){
		final List<Player>toUp = new ArrayList<Player>();
		for(Player play : Bukkit.getOnlinePlayers()){
			if ((!play.equals(player.getPlayer()))){
				if(play.canSee(player.getPlayer())){
					play.hidePlayer(player.getPlayer());
					toUp.add(play);
				}
			}
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				CraftPlayerUtils.setSkin(player, player.getName());
				for(Player play : toUp)
					play.showPlayer(player.getPlayer());
				CraftPlayerUtils.updateSelf(player);
			}
		}.runTaskLater(core.getInstance(), 1);
	}
	public Requirement getBypassPM() {
		return bypassPM;
	}
	public static HashMap<Localizer, List<VPPlayer>> toHashMap(
			Collection<? extends VPPlayer> receiver) {
		HashMap<Localizer, List<VPPlayer>> result = new HashMap<Localizer, List<VPPlayer>>();
		if(VanillaPlusCore.getLangs().size()==1){
			List<VPPlayer> listLang = new ArrayList<VPPlayer>();
			listLang.addAll(receiver);
			result.put(VanillaPlusCore.getDefaultLang(), listLang);
			return result;
		}
		for(VPPlayer p : receiver){
			List<VPPlayer> listLang = result.get(p.getLanguage());
			if(listLang == null){
				listLang = new ArrayList<VPPlayer>();
				listLang.add(p);
				result.put(p.getLanguage(), listLang);
			}else{
				listLang.add(p);
			}
		}
		return result;
	}
	public List<VPPlayer> getNearbyPlayer(Location loc, int distance, boolean online, boolean ignoreWorld){
		List<VPPlayer>result = new ArrayList<VPPlayer>();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		for(VPPlayer player : players.values()){
			if(player.isOnline() == online){
				Location pLoc = player.getLocation();
				if( ( ignoreWorld || pLoc.getWorld() == loc.getWorld() ) && (Math.sqrt(
						((x-pLoc.getBlockX())*(x-pLoc.getBlockX())) +
						((y-pLoc.getBlockY())*(y-pLoc.getBlockY())) + 
						((z-pLoc.getBlockZ())*(z-pLoc.getBlockZ()))) <= distance))
					result.add(player);
			}
		}
		return result;
	}
	/**
	 * @return the msgPerm
	 */
	public Requirement getMsgPerm() {
		return msgPerm;
	}
}
