package fr.soreth.VanillaPlus.StatType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class StatVanilla extends Stat{
	private Objective objective;
	public StatVanilla(Short id, ConfigurationSection section, MComponentManager manager) {
		super(id, section, manager);
		objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(getAlias());
		if(objective == null)
			objective = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(getAlias(), section.getString("SCORE_TYPE"));
	}
	public void update(VPPlayer player){
		Player p = player.getPlayer();
		if(p == null) p = Bukkit.getPlayer(player.getUUID());
		if(p == null) return;
		@SuppressWarnings("deprecation")
		Score score = objective.getScore(p);
		if(score != null){
			increase(player, score.getScore());
			score.setScore(0);
		}
	}
}
