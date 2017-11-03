package fr.soreth.VanillaPlus.Channel;

public class ChannelLoader {
	private static boolean init;

	public static void load(ChannelManager manager) {
		if(init)return;
		init = true;
	}
	
}