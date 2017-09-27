package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.IReward.Reward;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

/**
 * This command allow you some teleport action. 
 * TYPE: REWARD
 * REWARD: Reward
 * AMOUNT: Requirement to chose amount
 *
 * Usage : <label> [player]
 * 
 * @author Soreth.
 */
public class CPReward extends CPOther{
	private final Reward reward;
	private final Requirement amount;
	public CPReward(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPReward(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		reward = new Reward(section.get(Node.REWARD.get()), manager.getComponentManager());
		amount = new Requirement(Node.AMOUNT.get(), manager.getComponentManager());
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		int amount = 1;
		if(!args.isEmpty() && this.amount.has(sender)) {
			amount = Utils.parseInt(args.get(0), -1, false);
			if( amount < 1 )
				return CommandResult.FAIL;
		}
		if(receiver instanceof VPPlayer) {
			VPPlayer player = (VPPlayer) receiver;
			reward.give(player, amount);
			return CommandResult.SUCCESS;
		}
		return CommandResult.FAIL;
	}
}
