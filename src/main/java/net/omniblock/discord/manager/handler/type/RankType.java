package net.omniblock.discord.manager.handler.type;

public enum RankType {

	NONE("NONE"),
	EVERYTHING("Everything"),
	ADMIN("Equipo de Administración"),
	MODERATOR("Equipo de Moderación"),
	DESIGNER("Equipo de Diseño"),
	BUILDER("Equipo de Construcción"),
	DEVELOPER("Equipo de Desarrollo"),
	
	;
	
	private String title;
	
	RankType(String title){
		
		this.title = title;
		
	}

	public String getTitle() {
		return title;
	}
	
}
