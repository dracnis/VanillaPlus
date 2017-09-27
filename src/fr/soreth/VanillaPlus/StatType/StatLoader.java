package fr.soreth.VanillaPlus.StatType;

public class StatLoader {
	private static boolean init;

	public static void load(StatManager manager) {
		if(init)return;
		init = true;
		manager.register(StatDeath.class, 			"DEATH");
		manager.register(StatFood.class, 			"FOOD");
		manager.register(StatJoin.class, 			"JOIN");
		manager.register(StatKill.class, 			"KILL");
		manager.register(StatPVEDamage.class, 		"PVE_DAMAGE");
		manager.register(StatPVPDamage.class, 		"PVP_DAMAGE");
		manager.register(StatPVPDamageDone.class, 	"PVP_DAMAGE_DONE");
		manager.register(StatVanilla.class, 		"VANILLA");	
	}
	
}
