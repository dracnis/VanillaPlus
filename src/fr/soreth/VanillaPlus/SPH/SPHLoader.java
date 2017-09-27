package fr.soreth.VanillaPlus.SPH;

public class SPHLoader {
	private static boolean init;

	public static void load(SPlaceHolderManager manager) {
		if(init)return;
		init = true;
		manager.register(SPHCurrency.class, 		"CURRENCY");
		manager.register(SPHCurrencyAmount.class, 	"CURRENCY_AMOUNT");
		manager.register(SPHId.class, 				"ID");
		manager.register(SPHName.class, 			"NAME");
		manager.register(SPHPrefix.class, 			"PREFIX");
		manager.register(SPHPrefixColor.class,		"PREFIX_COLOR");
		manager.register(SPHRealName.class, 		"REAL_NAME");
		manager.register(SPHRealPrefix.class, 		"REAL_PREFIX");
		manager.register(SPHRealPrefixColor.class,	"REAL_PREFIX_COLOR");
		manager.register(SPHRealSuffix.class, 		"REAL_SUFFIX");
		manager.register(SPHRealSuffixColor.class,	"REAL_SUFFIX_COLOR");
		manager.register(SPHStat.class, 			"STAT");
		manager.register(SPHStatSession.class, 		"STAT_SESSION");
		manager.register(SPHSetting.class, 			"SETTING");
		manager.register(SPHSuffix.class, 			"SUFFIX");
		manager.register(SPHSuffixColor.class, 		"SUFFIX_COLOR");
		manager.register(SPHTitle.class, 			"TITLE_NAME");
		manager.register(SPHTitleForm.class, 		"TITLE_NAME_FORM");
		manager.register(SPHTitleLore.class, 		"TITLE_DESCRIPTION");
	}
	
}