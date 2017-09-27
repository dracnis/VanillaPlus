package fr.soreth.VanillaPlus.Utils.Packet;
import org.bukkit.scoreboard.Scoreboard;

import fr.soreth.VanillaPlus.Utils.ReflectionObject;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Packet.PacketUtils;
public class ScoreboardUtil {
	private static Class<?> craftScoreboard;
	private static Class<?> Scoreboard;
	private static Class<?> ScoreboardObjective;
	private static Class<?> ScoreboardScore;
	
	private static ReflectionObject toRemove = PacketUtils.craftPacket("PacketPlayOutScoreboardScore", String.class);
	private static ReflectionObject toAdd;
	private static ReflectionObject scoreboardScore;
	private Object ScoreboardNMS;
	private Object scoreboardObjective;
	public ScoreboardUtil(Scoreboard scoreboard , String objectiveName){
		if(craftScoreboard==null){
			craftScoreboard = ReflectionUtils.getBukkitClass("scoreboard.CraftScoreboard");
			Scoreboard = ReflectionUtils.getServerClass("Scoreboard");
			ScoreboardObjective = ReflectionUtils.getServerClass("ScoreboardObjective");
			ScoreboardScore = ReflectionUtils.getServerClass("ScoreboardScore");
			scoreboardScore = new ReflectionObject(ScoreboardScore, Scoreboard, ScoreboardObjective ,String.class);
			toAdd = PacketUtils.craftPacket("PacketPlayOutScoreboardScore", scoreboardScore.getClazz());
		}
		if(scoreboard != null && objectiveName != null){
			Object cs = craftScoreboard.cast(scoreboard);
			 ScoreboardNMS = ReflectionUtils.invoke("getHandle", cs);
			 scoreboardObjective = ReflectionUtils.invoke("getObjective", ScoreboardNMS, objectiveName);
		}
		
	}
	public ReflectionObject getAddScorePacket(String scoreName, int score){
		scoreboardScore.instance(ScoreboardNMS, scoreboardObjective, scoreName);
		scoreboardScore.setDeclaredField("score", score);
		toAdd.instance(scoreboardScore.getObject());
		return toAdd;
	}
	public static ReflectionObject getRemoveScorePacket(String name){
		toRemove.instance(name);
		return toRemove;
		
	}
}
