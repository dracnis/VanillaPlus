package fr.soreth.VanillaPlus.Damage;

public class DamageLoader {
	private static boolean init;

	public static void load(DamageManager manager) {
		if(init)return;
		init = true;
		manager.register(DamageClassic.class, 		"CLASSIC");
	}
	
}