package fr.soreth.VanillaPlus.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Data.Column;
import fr.soreth.VanillaPlus.Data.IConnection;
import fr.soreth.VanillaPlus.Data.IResultQuery;
import fr.soreth.VanillaPlus.Data.Table;
import fr.soreth.VanillaPlus.Data.Table.IUpdateQuery;
import fr.soreth.VanillaPlus.Data.Column.Type;
import fr.soreth.VanillaPlus.Data.SessionValue.DoubleSession;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class CurrencyManager implements Listener {
	private HashMap<Short, Currency>currency = new HashMap<>();
	private Table table; 
	private int bigger;
	private Message invalidAmount, locked, self, poor, poorPlayer, poorServer, rich, richServer, richPlayer, win, lose, sent, received;
	private Column id = new Column("id", Type.INTEGER, null, true, false, false, true);
	public void init(VanillaPlusCore core) {
		ConfigurationSection section = ConfigUtils.getYaml(core.getInstance(), "Currency", false);
		ErrorLogger.addPrefix("Currency.yml");
		ConfigurationSection settings = section == null ? null : section.getConfigurationSection(Node.SETTINGS.get());
		if(settings==null){
			Error.MISSING_NODE.add(Node.SETTINGS.get());
			invalidAmount = locked = lose = self = poor = poorPlayer = poorServer = rich = richServer = richPlayer = win = sent = received = core.getMessageManager().get(null);
			startDataBase(VanillaPlusCore.getIConnectionManager().get(null));
		}else{
			invalidAmount	= core.getMessageManager().get(settings.getString("INVALID_AMOUNT"));
			locked			= core.getMessageManager().get(settings.getString("LOCKED"));
			self			= core.getMessageManager().get(settings.getString("SELF"));
			poor			= core.getMessageManager().get(settings.getString("POOR"));
			poorPlayer		= core.getMessageManager().get(settings.getString("POOR_PLAYER"));
			poorServer		= core.getMessageManager().get(settings.getString("POOR_SERVER"));
			rich			= core.getMessageManager().get(settings.getString("RICH"));
			richServer		= core.getMessageManager().get(settings.getString("RICH_SERVER"));
			richPlayer		= core.getMessageManager().get(settings.getString("RICH_PLAYER"));
			win				= core.getMessageManager().get(settings.getString("WIN"));
			lose			= core.getMessageManager().get(settings.getString("LOSE"));
			sent			= core.getMessageManager().get(settings.getString("SENT"));
			received		= core.getMessageManager().get(settings.getString("RECEIVED"));
			startDataBase(VanillaPlusCore.getIConnectionManager().get(settings.getString("STORAGE")));
		}
		ErrorLogger.removePrefix();
	}
	public void init(VanillaPlusExtension extension) {
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Currency", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Currency.yml");
		ConfigurationSection currencySub = section.getConfigurationSection(Node.CURRENCY.getList());
		if(currencySub != null){
			ErrorLogger.addPrefix(Node.CURRENCY.getList());
			for(String key : currencySub.getKeys(false)){
				ConfigurationSection sub = currencySub.getConfigurationSection(key);
				ErrorLogger.addPrefix(key);
				if(sub == null){
					Error.INVALID.add();
				}else{
					int id = Utils.parseInt(key, 0, true);
					if(id < 1 || id > Short.MAX_VALUE || currency.containsKey((short)id)){
						Error.INVALID.add();
					}else{
						if(id>bigger)
							bigger = id;
						Currency title = new Currency(id, sub, extension.getMessageCManager());
						currency.put((short) id, title);
					}
				}
				ErrorLogger.removePrefix();
			}
			ErrorLogger.removePrefix();			
		}
		ErrorLogger.removePrefix();
	}
	private void startDataBase(IConnection connection){
		if(currency.isEmpty())return;
		table = connection.getTable("VPPlayer_currency");
		table.addColumn(id);
		for(short i = 1; i <= bigger ; i++){
			Currency c = get(i);
			table.addColumn(new Column(i+"", Type.DOUBLE, c == null ? 0 : c.getMin() < 0 ? 0 : c.getMin(), true, false, false, false));
		}
		table.validate();
		IResultQuery result = table.select().where(id, 0).execute();
		if(result.next()){
			for(Currency c : this.currency.values()){
				c.server = new DoubleSession(result.getDouble(table.getColumn(null, ""  +(c.getID()))));
			}
			result.close();
		}else{
			result.close();
			create(0);
			result = table.select().where(id, 0).execute();
			if(result.next()){
				for(Currency c : this.currency.values()){
					c.server = new DoubleSession(result.getDouble(table.getColumn(null, ""  +(c.getID()))));
				}
				result.close();
			}else{
				ErrorLogger.addError("Can't load server currency !");
			}
		}
	}
	void load(VPPlayer player){
		if(currency.isEmpty())return;
		IResultQuery result = table.select().where(id, player.getID()).execute();
		if(result.next()){
			Map<Integer, DoubleSession>playerBalance = player.getCurrency();
			for(int i : currency.keySet()){
				DoubleSession temp = playerBalance.get(i);
				Double amount = result.getDouble(table.getColumn(null, "" + i));
				if(temp == null){
					temp = new DoubleSession(amount);
					playerBalance.put(i, temp);
				}
				else
					temp.setLast(amount);
			}
			result.close();
		}else{
			result.close();
			create(player.getID());
			load(player);
		}
	}
	void save(VPPlayer player){
		if(currency.isEmpty())return;
		IUpdateQuery result = table.update();
		for(Entry<Integer, DoubleSession>entry : player.getCurrency().entrySet()){
			if(entry.getValue().changed()){
				result.add(table.getColumn(null, "" + entry.getKey()), entry.getValue().getChange());
				entry.getValue().save();
			}
		}
		result.where(id, player.getID()).execute();
	}
	void create(int i){
		if(currency.isEmpty())return;
		table.insert().insert(id, i).execute();
	}
	public Currency get(short id){
		return currency.get(id);
	}
	public Currency get(String name){
		for(Currency currency : this.currency.values())
			if(currency.getAlias().equalsIgnoreCase(name))
				return currency;
		return null;
	}
	public List<String> getCurrencyList(String prefix, boolean allowPay) {
		List<String>result = new ArrayList<String>();
		for(Currency currency : getCurrency(prefix, allowPay))
			result.add(currency.getAlias());
		return result;
	}
	public List<Currency> getCurrency(String prefix, boolean allowPay) {
		List<Currency>result = new ArrayList<Currency>();
		for(Currency currency : this.currency.values()){
			if(currency.allowPaiement() || !allowPay)
				if(currency.getAlias().toLowerCase().startsWith(prefix))
					result.add(currency);
		}
		return result;
	}
	public Message getInvalidAmount() {
		return invalidAmount;
	}
	public Message getLocked() {
		return locked;
	}
	public Message getLose() {
		return lose;
	}
	public Message getPoor() {
		return poor;
	}
	public Message getPoorPlayer() {
		return poorPlayer;
	}
	public Message getPoorServer() {
		return poorServer;
	}
	public Message getReceived() {
		return received;
	}
	public Message getRich() {
		return rich;
	}
	public Message getRichPlayer() {
		return richPlayer;
	}
	public Message getRichServer() {
		return richServer;
	}
	public Message getSelf() {
		return self;
	}
	public Message getSent() {
		return sent;
	}
	public Message getWin() {
		return win;
	}
}
