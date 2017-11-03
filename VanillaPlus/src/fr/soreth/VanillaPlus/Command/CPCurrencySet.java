package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.Currency;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

/**
 * This command allow you to give or remove money to player.
 * 
 * TYPE: CURRENCY_SET
 * CURRENCY: id of currency // log if invalid, will be the default currency if not specified.
 * REMOVE: true // default false, if true will remove money.
 * SUCCESS: Message's path // default VOID, will send to sender's players as result if successfully executed, 'receiver' for receiver placeholder, '%currency% for currency format.
 * ALREADY: Message's path // default VOID, will send to sender's players as result if set is true and player have already value, 'receiver' for receiver placeholder, '%currency% for currency format.
 * 
 * Usage : <label> [receiver] <amount> [currency's alias]
 *
 * @author Soreth.
 */

public class CPCurrencySet extends CPSimple{
	private final Currency defaultCurrency;
	private final Message alreadyOther;
	private final boolean remove, set;
	public CPCurrencySet(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPCurrencySet(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		defaultCurrency = VanillaPlusCore.getCurrencyManager().get((short) section.getInt(Node.CURRENCY.get()));
		if(defaultCurrency == null){
			ErrorLogger.addError(Node.CURRENCY.get());
			Error.INVALID.add();
		}
		set  = section.getBoolean("SET", false);
		if(!set) remove = section.getBoolean(Node.REMOVE.get(), false);
		else remove = false;
		alreadyOther = manager.get(section.getString(Node.ALREADY.getOther()));
		argumentRequired = 1;
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args) {
		VPPlayer toSend;
		double amount;
		Currency currency;
		if(Utils.isValidDouble(args.get(0))) {
			toSend = (VPPlayer) (( receiver instanceof VPPlayer ) ? receiver : null);
			amount = Utils.parseDouble(args.get(0), 0.0, false);
			currency = args.size() > 1 ? VanillaPlusCore.getCurrencyManager().get(args.get(1)) : defaultCurrency;
		}else if(args.size() > 1) {
			toSend = VanillaPlusCore.getPlayerManager().getPlayer(args.get(0));
			if(toSend == null){
				return CommandResult.FAIL;
			}
			amount = Utils.parseDouble(args.get(1), 0.0, false);
			currency = args.size() > 2 ? VanillaPlusCore.getCurrencyManager().get(args.get(2)) : defaultCurrency;
		}else {
			return CommandResult.FAIL;
		}
		if(amount <= 0){
			return CommandResult.FAIL;
		} 
		if(currency == null){
			return CommandResult.FAIL;
		}
		if(set){
			double value = toSend == null ? currency.getServer() : toSend.getCurrency(currency.getID());
			if(value < amount){
				value = ( toSend == null ) ? currency.addServer(amount) : currency.deposit(toSend, amount - value, receiver);
			}else if(value != amount){
				value = ( toSend == null ) ? currency.addServer(amount) : currency.paiement(toSend, value - amount, receiver);
			}else {
				if(toSend == null || receiver.equals(toSend)) {
					already.addCReplacement(PlaceH.CURRENCY.get(), currency.getName(amount))
					.addReplacement("amount", currency.format(amount));
					return CommandResult.CANCELED;
				}else {
					alreadyOther.addCReplacement(PlaceH.CURRENCY.get(), currency.getName(amount))
					.addReplacement("amount", currency.format(amount))
					.addSReplacement(PlaceH.RECEIVER.get(), toSend == null ? VanillaPlusCore.getVPConsole() : toSend)
					.sendTo(receiver);
					return CommandResult.CANCELED_OTHER;
				}
			}
			if(value == 0)
				return CommandResult.CANCELED_OTHER;
		}else if(remove){
			amount = ( toSend == null ) ? currency.addServer(amount) : currency.paiement(toSend, amount, receiver);
		}else{
			amount = ( toSend == null ) ? currency.addServer(amount) : currency.deposit(toSend, amount, receiver);
		}
		if(amount == 0 || receiver.equals(toSend))
			return CommandResult.CANCELED_OTHER;
		success.addCReplacement(PlaceH.CURRENCY.get(), currency.getName(amount))
		.addReplacement("amount", currency.format(amount))
		.addSReplacement(PlaceH.RECEIVER.get(), toSend == null ? VanillaPlusCore.getVPConsole() : toSend);
		return CommandResult.SUCCESS;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		if(args == null || args.isEmpty())
			return null;
		boolean isDouble = Utils.isValidDouble(args.get(0));
		if( ( isDouble && args.size() == 2 ) || ( !isDouble && args.size() == 3 ) ) {
			return VanillaPlusCore.getCurrencyManager().getCurrencyList(args.get( args.size() - 1 ).toLowerCase(), false);
		}else if(!isDouble && args.size() == 1) {
			return VanillaPlusCore.getPlayerManager().getPlayersList(args.get(0), false);
		}else {
			return null;
		}
	}
	
}
