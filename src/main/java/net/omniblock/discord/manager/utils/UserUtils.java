package net.omniblock.discord.manager.utils;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.omniblock.discord.manager.handler.type.RankType;

public class UserUtils {

	public static RankType getRank(Member member) {
		
		for(Role role : member.getRoles()) {
			
			for(RankType rank : RankType.values()) {
				
				if(rank.getTitle().equals(role.getName()))
					return rank;
				
			}
			
		}
		
		return RankType.NONE;
		
	}
	
}
