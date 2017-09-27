package fr.soreth.VanillaPlus.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Data.SessionValue.DoubleSession;
import fr.soreth.VanillaPlus.Event.VPPCurrencyChangeEvent;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;

public class Currency{
	private String alias;
	private MComponent name, single;
	private double min, max, booster, step;
	DoubleSession server;
	private boolean allowPay, useServer;
	private int id;
	private DecimalFormat format;
	public Currency(int id, ConfigurationSection section, MComponentManager manager){
		this.id = id;
		this.name = manager.get(section.getString(Node.NAME.get()));
		this.single = manager.get(section.getString("SINGLE"));
		this.alias = section.getString("ALIAS");
		int type = section.getInt("FORMAT_TYPE", 0);
		this.format = (DecimalFormat) NumberFormat.getNumberInstance( type == 0 ? Locale.GERMAN : type == 1 ? Locale.ENGLISH : Locale.FRENCH);
		format.applyPattern(section.getString("FORMAT", "###,###.### "));
		this.step = section.getDouble("STEP", 0.001);
		double temp = ((int)(step*1000))/1000.0;
		if(step < 0.001 || temp != step)
			ErrorLogger.addError("Invalid step amount : " + step);
		this.min = ((int)section.getDouble("MIN", 0)/step)*step;
		this.max = ((int)section.getDouble("MAX", 9999999999.999)/step)*step;
		this.allowPay = section.getBoolean("ALLOW_PAY", false);
		this.useServer = section.getBoolean("USE_SERVER", false);
		this.booster = 1.0;
	}
	public MComponent getName(){
		return this.name;
	}
	public String getAlias(){
		return this.alias;
	}
	public double getMin(){
		return this.min;
	}
	public double getMax(){
		return this.max;
	}
	public double getServer(){
		return this.server.get();
	}
	public double paiement(VPPlayer from, double amount, VPSender sender){
		if(amount <= 0){
			VanillaPlusCore.getCurrencyManager().getInvalidAmount()
			.addReplacement("amount", String.valueOf(amount))
			.addReplacement("step", String.valueOf(step))
			.sendTo(sender);
			return 0.0;
		}
		double temp = Math.floor( amount / step ) * step;
		if(temp == 0){
			VanillaPlusCore.getCurrencyManager().getInvalidAmount()
			.addReplacement("amount", String.valueOf(amount))
			.addReplacement("step", String.valueOf(step))
			.sendTo(sender);
			return 0.0;
		}
		amount = temp;
		double playerAmount = from.getCurrency(id);
		if(playerAmount - amount < min){
			VanillaPlusCore.getCurrencyManager().getPoorPlayer()
			.addCReplacement("currency", single)
			.addSReplacement("receiver", from)
			.sendTo(sender);
			return 0.0;
		}
		if(useServer)
			this.server.add(amount);
		Bukkit.getServer().getPluginManager().callEvent(new VPPCurrencyChangeEvent(from, this, -amount));
		from.withdraw(id, amount);
		VanillaPlusCore.getCurrencyManager().getLose()
		.addCReplacement("currency", getName(amount))
		.addReplacement("amount", format(amount))
		.sendTo(from);
		return amount;
	}
	public double deposit(VPPlayer to, double amount, boolean force, VPSender sender) {
		if(amount <= 0){
			VanillaPlusCore.getCurrencyManager().getInvalidAmount()
			.addReplacement("amount", String.valueOf(amount))
			.addReplacement("step", String.valueOf(step))
			.sendTo(sender);
			return 0;
		}
		amount = Math.floor( amount / step * booster ) * step;
		if(amount <= 0){
			VanillaPlusCore.getCurrencyManager().getInvalidAmount()
			.addReplacement("amount", String.valueOf(amount))
			.addReplacement("step", String.valueOf(step))
			.sendTo(sender);
			return 0;
		}
		double playerAmount = to.getCurrency(id);
		if(!force && playerAmount + amount > max){
			VanillaPlusCore.getCurrencyManager().getRich()
			.addCReplacement("currency", single)
			.sendTo(to);
			amount = max - playerAmount;
			if(amount == 0) {
				VanillaPlusCore.getCurrencyManager().getRichPlayer()
				.addCReplacement("currency", single)
				.addSReplacement("receiver", to)
				.sendTo(sender);
				return 0.0;
			}
		}
		if(useServer)
			if(this.server.get() - amount < min){
				VanillaPlusCore.getCurrencyManager().getPoorServer()
				.addCReplacement("currency", single)
				.sendTo(sender);
				return 0;
			}
			else
				this.server.add(-amount);
		Bukkit.getServer().getPluginManager().callEvent(new VPPCurrencyChangeEvent(to, this, amount));
		to.deposit(id, amount);
		VanillaPlusCore.getCurrencyManager().getWin()
		.addCReplacement("currency", getName(amount))
		.addReplacement("amount", format(amount))
		.sendTo(to);
		return amount;
	}
	public double deposit(VPPlayer to, double amount, VPSender sender) {
		return deposit(to, amount, false, sender);
	}
	public double settlement(VPPlayer from, VPPlayer to, double amount){
		if(!allowPay){
			VanillaPlusCore.getCurrencyManager().getLocked().addCReplacement("currency", name).sendTo(from);
			return 0;
		}
		if(from.equals(to)){
			VanillaPlusCore.getCurrencyManager().getSelf().sendTo(from);
			return 0;
		}
		if(amount <= 0){
			VanillaPlusCore.getCurrencyManager().getInvalidAmount()
			.addReplacement("amount", String.valueOf(amount))
			.addReplacement("step", String.valueOf(step))
			.sendTo(from);
			return 0;
		}
		double temp = Math.floor( amount / step ) * step;
		if(amount == 0){
			VanillaPlusCore.getCurrencyManager().getInvalidAmount()
			.addReplacement("amount", String.valueOf(amount))
			.addReplacement("step", String.valueOf(step))
			.sendTo(from);
			return 0;
		}
		amount = temp;
		double amountTo = to.getCurrency(id);
		if(amountTo + amount > max){
			VanillaPlusCore.getCurrencyManager().getRichPlayer()
			.addCReplacement("currency", single)
			.addSReplacement("receiver", to)
			.sendTo(from);
			amount = max - amountTo;
			if(amount == 0)
				return 0;
		}
		double amountFrom = from.getCurrency(id);
		if(amountFrom - amount < min){
			VanillaPlusCore.getCurrencyManager().getPoor()
			.addCReplacement("currency", single)
			.sendTo(from);
			return 0;
		}
		from.withdraw(id, amount);
		VanillaPlusCore.getCurrencyManager().getSent()
		.addCReplacement("currency", getName(amount))
		.addReplacement("amount", format(amount))
		.addSReplacement("receiver", to)
		.sendTo(from);
		to.deposit(id, amount);
		VanillaPlusCore.getCurrencyManager().getReceived()
		.addCReplacement("currency", getName(amount))
		.addReplacement("amount", format(amount))
		.addSReplacement("sender", from)
		.sendTo(to);
		return amount;
		
	}
	public boolean allowPaiement(){
		return this.allowPay;
	}
	public MComponent getName(double amount) {
		if(amount < 2.0)
			return single;
		return name;
	}
	public String format(double amount){
		if(amount < 2.0)
			return format.format(amount);
		return format.format(amount);
	}
	public int getID() {
		return id;
	}
	//TODO add command
	public double addServer(double amount){
		this.server.add(amount);
		return amount;
	}
}
