package fr.soreth.VanillaPlus.SPH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.Currency;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

public class SPHCurrencyAmount extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_currency_amount_([0-9]+)%");
	private static List<SPHCurrencyAmount> instance = new LinkedList<>();
	private short currency;
	public String getPH(String key){
		return "%" + key + "_currency_" + currency + "%";
	}
	public HashMap<String, List<SPH>> getPHs(String message){
		Matcher m = pattern.matcher(message);
		HashMap<String, List<SPH>>result = new HashMap<String, List<SPH>>();
		while (m.find()) {
			String key = m.group(1);
			List<SPH>s = result.get(key);
			if(s==null){
				s=new ArrayList<SPH>();
				result.put(key, s);
			}
			int i = 1;
			String value = m.group(2);
			i = Utils.parseInt(value, 1, true);
			if(!String.valueOf(i).equals(value)) {
				ErrorLogger.addError(m.group() + " has invalid value, " + value + " should be " + String.valueOf(i));
			}else if(i < 1 || i > Short.MAX_VALUE)
				Error.INVALID.add();
			else
				s.add(getInstance((short) i));
		}
		return result;
	}
	private static SPHCurrencyAmount getInstance(short currency) {
		for(SPHCurrencyAmount sphc : instance) {
			if(sphc.currency == currency)
				return sphc;
		}
		SPHCurrencyAmount sphta = new SPHCurrencyAmount();
		sphta.currency = currency;
		instance.add(sphta);
		return sphta;
	}
	@Override
	public String getReplacement(Localizer lang, VPSender sender){
		Currency currency = VanillaPlusCore.getCurrencyManager().get(this.currency);
		double amount = sender instanceof VPPlayer ? ((VPPlayer) sender).getCurrency(this.currency) : currency.getServer();
		return currency.format(amount);
	}
}
