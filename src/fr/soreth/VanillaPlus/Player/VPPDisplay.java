package fr.soreth.VanillaPlus.Player;

import org.bukkit.permissions.PermissionAttachmentInfo;

import fr.soreth.VanillaPlus.Utils.Utils;

public class VPPDisplay {

	public void refresh(VPPlayer p){
		String prefix = "";
		String suffix = "";
		int prefixLevel = 0;
		int suffixLevel = 0;
		for(PermissionAttachmentInfo perm : p.getPlayer().getEffectivePermissions()){
			if(perm.getValue()){
				String temp = perm.getPermission();
				if(temp.startsWith("prefix.")){
					String[] nodes = temp.split("\\.", 3);
					if(nodes.length == 3){
						int level = Utils.parseInt(nodes[1], -1, true);
						if(level > prefixLevel){
							prefixLevel = level;
							prefix = nodes[2];
						}	
					}
				}else if(temp.startsWith("suffix.")){
					String[] nodes = temp.split("\\.", 3);
					if(nodes.length == 3){
						int level = Utils.parseInt(nodes[1], -1, true);
						if(level > suffixLevel){
							suffixLevel = level;
							suffix = nodes[2];
						}	
					}	
				}
			}
		}
		p.setPrefix(prefix);
		p.setSuffix(suffix);
		p.setGroupLevel(prefixLevel);
	}
}
