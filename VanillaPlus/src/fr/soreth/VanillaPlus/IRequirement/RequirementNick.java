package fr.soreth.VanillaPlus.IRequirement;

import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RequirementNick extends SimpleRequirement{
	@Override
	public boolean has(VPPlayer player){
		return player.isNick();
	}
}
