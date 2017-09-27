package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Player.Currency;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;


/**
 * This command allow you to pay other player.
 * TYPE: CURRENCY_PAY
 * CURRENCY: id of currency // log if invalid. If [currency's alias] not specified, this one will be the default currency.
 * 
 * Usage : <label> <receiver> <amount> [currency's alias]
 *
 * @author Soreth.
 */

public class CPCurrencyPay extends CPSimple{
	private final Currency defaultCurrency;
	public CPCurrencyPay(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPCurrencyPay(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		defaultCurrency = VanillaPlusCore.getCurrencyManager().get((short) section.getInt(Node.CURRENCY.get()));
		if(defaultCurrency == null){
			ErrorLogger.addError(Node.CURRENCY.get());
			Error.INVALID.add();
		}
		argumentRequired = 2;
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args) {
		VPPlayer toSend = VanillaPlusCore.getPlayerManager().getPlayer(args.get(0));
		double amount = Utils.parseDouble(args.get(1), 0.0, false);
		Currency currency = args.size() > 2 ? VanillaPlusCore.getCurrencyManager().get(args.get(2)) : defaultCurrency;
		if(currency == null){
			return CommandResult.FAIL;
		}
		if(receiver instanceof VPPlayer)
			amount = currency.settlement((VPPlayer) receiver, toSend, amount);
		else
			amount = currency.deposit(toSend, amount, true, receiver);
		if(amount == 0)
			return CommandResult.CANCELED_OTHER;
		success.addCReplacement(PlaceH.CURRENCY.get(), currency.getName(amount))
		.addReplacement("amount", currency.format(amount))
		.addSReplacement(PlaceH.RECEIVER.get(), toSend);
		return CommandResult.SUCCESS;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args) {
		List<String>result = new ArrayList<String>();
		switch (args.size()) {
		case 1:
			result.addAll(VanillaPlusCore.getPlayerManager().getPlayersList(args.get(0), false));
			if(sender instanceof VPPlayer)
				result.remove(sender.getName());
			break;
		case 3:
			result.addAll(VanillaPlusCore.getCurrencyManager().getCurrencyList(args.get(2).toLowerCase(), true));
			break;
		default:
			break;
		}
		return result;
	}
	
}
