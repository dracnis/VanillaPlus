package fr.soreth.VanillaPlus.IReward;

import fr.soreth.VanillaPlus.Node;

public class RewardLoader {
	private static boolean init;

	public static void load(IRewardManager manager) {
		if(init)return;
		init = true;
		manager.register(RewardAchievement.class,		Node.ACHIEVEMENT.get());
		manager.register(RewardCurrency.class,		 	Node.CURRENCY.get());
		manager.register(RewardDamage.class,		 	"DAMAGE");
		manager.register(RewardHeal.class,		 		"HEAL");
		manager.register(RewardStat.class, 				"STAT");
		manager.register(RewardTitle.class, 			"TITLE");
	}
	
}