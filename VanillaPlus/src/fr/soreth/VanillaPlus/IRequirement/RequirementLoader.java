package fr.soreth.VanillaPlus.IRequirement;

import fr.soreth.VanillaPlus.Node;

public class RequirementLoader {
	private static boolean init;

	public static void load(IRequirementManager manager) {
		if(init)return;
		init = true;
		manager.register(RequirementCurrency.class, 		Node.CURRENCY.get());
		manager.register(RequirementLang.class,				"LANG");
		manager.register(RequirementStat.class, 			"STAT");
		manager.register(RequirementTitle.class, 			"TITLE");		

		manager.register("NICK", 			new RequirementNick(), true);
	}
	
}