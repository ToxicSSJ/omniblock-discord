package net.omniblock.discord.manager.handler;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import net.omniblock.discord.config.IdentitiesConfig;
import net.omniblock.discord.config.TokenConfig;
import net.omniblock.discord.config.VersionConfig;
import net.omniblock.discord.manager.DHandlers;
import net.omniblock.discord.manager.handler.object.GitObject;
import net.omniblock.discord.manager.handler.object.GitUpdate;
import net.omniblock.discord.manager.utils.ColorUtils;
import net.omniblock.discord.manager.utils.MessageUtils;

public class DiscordBotHandler implements EventListener {

	public static final String CMD_PREFIX = "::";
	
	private JDA client;

	public Guild staffGuild, publicGuild;
	public TextChannel cmdChannel,
						  patchChannel,
						  helpersChannel,
						  devsWorksChannel,
						  buildersWorksChannel,
						  registryChannel,
						  advancedRegistryChannel;
	
	public Map<String, Message> devsWorksMessages = new HashMap<String, Message>();
	public Message versionMessage;
	
	public File configFile;
	public JSONObject jsonConfigObject;
	
	@SuppressWarnings("deprecation")
	public void startBot() {
		
		this.configFile = configFile();
		this.configDefaults();
		
		try {
			
			this.client = new JDABuilder(AccountType.BOT).setStatus(OnlineStatus.ONLINE)
					.setGame(Game.of("Omniblock Network", "http://www.omniblock.net/")).setToken(TokenConfig.DISCORD_TOKEN)
					.buildAsync();
			
		} catch (LoginException | IllegalArgumentException | RateLimitedException e) {
			e.printStackTrace();
		}
		
		this.client.addEventListener(this);
		
	}

	public void configDefaults() {
		
		if (!this.configFile.exists()) {
			
			this.jsonConfigObject = new JSONObject();
			this.jsonConfigObject.put("version_msgid", "none");
			this.jsonConfigObject.put("status_msgid", "none");
			this.jsonConfigObject.put("gitlab_token", "none");
			
			saveConfig();
			return;

		}

		loadConfig();
		return;

	}
	
	private void saveConfig() {

		try {

			FileUtils.openOutputStream(this.configFile);

			try (FileWriter file = new FileWriter(this.configFile.getPath())) {
				file.write(jsonConfigObject.toString());
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	private void loadConfig() {

		try {

			InputStream stream = FileUtils.openInputStream(this.configFile);
			String jsonTxt = IOUtils.toString(stream, "UTF-8");

			this.jsonConfigObject = new JSONObject(jsonTxt);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void onEvent(Event event) {

		if(event instanceof MessageReceivedEvent) {
			
			if(((MessageReceivedEvent) event).getAuthor().getIdLong() == client.asBot().getApplicationInfo().complete().getIdLong())
				return;
			
			Long channel_id = ((MessageReceivedEvent) event).getMessage().getChannel().getIdLong();
			
			if(channel_id == cmdChannel.getIdLong()) {
				
				// Este mensaje se ha enviado en el canal de comandos
				// Se procesará la petición del usuario
				
				if(((MessageReceivedEvent) event).getMessage().getContent().startsWith(CMD_PREFIX)) {
					
					String[] content = ((MessageReceivedEvent) event).getMessage().getContent().replaceFirst(CMD_PREFIX, "").split(" ");
					((MessageReceivedEvent) event).getMessage().delete().complete();
					
					DHandlers.COMMAND.executeCommand(((MessageReceivedEvent) event).getMessage(), content);
					return;
					
				}
				
				((MessageReceivedEvent) event).getMessage().delete().complete();
				
				Message cache = cmdChannel.sendMessage(
						new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("💢 ¡Se ha encontrado un error en tu sintaxis!")
							.setDescription(
									((MessageReceivedEvent) event).getMessage().getAuthor().getAsMention() + " verifica y coloca el comando nuevamente, " + 
									"recuerda que debes utilizar el prefijo `::` para que se tome en cuenta como un comando valido.")
							.setFooter("💣 Este mensaje se autodestruirá en 30 segundos!", null)
							.build()).complete();
				
				MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 30);
				return;
				
			}
			
			if(channel_id == devsWorksChannel.getIdLong()
					|| channel_id == buildersWorksChannel.getIdLong()
					|| channel_id == registryChannel.getIdLong()
					|| channel_id == advancedRegistryChannel.getIdLong()) {
				
				((MessageReceivedEvent) event).getMessage().delete().complete();
				
				Message cache = ((MessageReceivedEvent) event).getTextChannel().sendMessage(
						new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("💢 ¡No puedes escribir mensajes en este Canal!")
							.setDescription(
									((MessageReceivedEvent) event).getMessage().getAuthor().getAsMention() + " este canal es exclusivo de uso " + 
									"y administración del bot.")
							.setFooter("💣 Este mensaje se autodestruirá en 15 segundos!", null)
							.build()).complete();
				
				MessageUtils.deleteMessageAfter(cache, TimeUnit.SECONDS, 15);
				return;
				
			}
			
		}
		
		if(event instanceof ReadyEvent) {

			staffGuild = client.getGuildById(IdentitiesConfig.STAFF_DISCORD_SERVER);
			publicGuild = client.getGuildById(IdentitiesConfig.PUBLIC_DISCORD_SERVER);
			
			patchChannel = client.getTextChannelById(IdentitiesConfig.PATCH_CHANNEL);
			
			cmdChannel = client.getTextChannelById(IdentitiesConfig.COMMAND_CHANNEL);
			helpersChannel = client.getTextChannelById(IdentitiesConfig.HELPERS_CHANNEL);
			registryChannel = client.getTextChannelById(IdentitiesConfig.REGISTRY_CHANNEL);
			advancedRegistryChannel = client.getTextChannelById(IdentitiesConfig.ADVANCED_REGISTRY_CHANNEL);
			
			devsWorksChannel = client.getTextChannelById(IdentitiesConfig.DEVSWORK_CHANNEL);
			buildersWorksChannel = client.getTextChannelById(IdentitiesConfig.BUILDERSWORK_CHANNEL);
			
			Runnable task = () -> {
				
				List<Color> colors = ColorUtils.getBrightColors();
				int index = 0;
				
	            while(true) {
	            	
	            	if(index >= colors.size())
	            		index = 0;
	            	
	            	for(Role role : client.getRolesByName("OMNIPLUS", true))
	    				role.getManager().setColor(colors.get(index++)).complete();
	            	
	            	try {
	            		
						TimeUnit.SECONDS.sleep(1);
						
					} catch (InterruptedException e) { e.printStackTrace(); }
	                
	            }
	            
	        };
	         
	        new Thread(task).run();
			
			
			
			hookMessages();
			clearMessages();
			return;
			
		}

	}
	
	public void clearMessages() {
		
		cmdChannel.getIterableHistory().forEach(message -> {
			
			if(message.getIdLong() != versionMessage.getIdLong()) {
				message.delete().complete();
			}
				
		});
		
		buildersWorksChannel.getIterableHistory().forEach(message -> {
			
			message.delete().complete();
			
		});
		
	}
	
	public void hookMessages() {
		
		MessageEmbed versionText = new EmbedBuilder()
				.setColor(Color.ORANGE)
				.setTitle("Sistema Omniblock Discord | Omniblock Network (c) : A-Plugin License | 2018")
				.setDescription(
						"\n\n    📖  **Version:** " + VersionConfig.LAST_COMPILATION +
						"    📨 **Último Commit:** " + VersionConfig.LAST_COMMIT + "\n \n" +
						"A continuación escriba un comando para ser ejecutado:")
				.build();
		
		if(this.jsonConfigObject.getString("version_msgid") == "none") {
			
			versionMessage = cmdChannel.sendMessage(versionText).complete();
			
			this.jsonConfigObject.put("version_msgid", versionMessage.getId());
			this.saveConfig();
			
		} else {
			
			try {
				
				versionMessage = cmdChannel.getMessageById(this.jsonConfigObject.getString("version_msgid")).complete();
				
			} catch(ErrorResponseException e) { versionMessage = null; }
			
			if(versionMessage == null) {
				
				versionMessage = cmdChannel.sendMessage(versionText).complete();
				
				this.jsonConfigObject.put("version_msgid", versionMessage.getId());
				this.saveConfig();
				
			} else { 
				
				versionMessage = cmdChannel.editMessageById(this.jsonConfigObject.getString("version_msgid"), versionText).complete();
				
			}
			
		}
		
	}
	
	public void handleGitUpdate(GitUpdate update) {
		
		String key = "gitcfg_" + update.getValues().get(GitObject.PROJECT_NAME).toLowerCase().replaceAll("\\s+", "") + "_msg";
		Message message = devsWorksMessages.containsKey(key) ? devsWorksMessages.get(key) : null;
		
		String title = update.getValues().get(GitObject.COMMIT_TITLE);
		String body = update.getValues().get(GitObject.COMMIT_BODY);
		
		MessageEmbed embed = new EmbedBuilder()
				.setTitle(title.equals("$none") ? "Titulo no definido" : title, update.getValues().get(GitObject.COMMIT_URL))
				.setDescription(body.equals("$none") ? null : body)
				.setFooter(update.getValues().get(GitObject.AUTHOR_NAME) +
						   " @" + update.getValues().get(GitObject.COMMIT_SHORT_ID) +
						   " P:" + update.getValues().get(GitObject.PROJECT_NAME) + 
						   " branch/" + update.getValues().get(GitObject.REF_SHORT),
						   update.getValues().get(GitObject.AUTHOR_AVATAR_URL))
				.build();
		
		if(message == null) {
			
			if(!jsonConfigObject.has(key)) {
				
				message = devsWorksChannel.sendMessage(embed).complete();
				
				this.jsonConfigObject.put(key, message.getId());
				this.saveConfig();
				
				return;
				
			}
			
			try {
				
				message = devsWorksChannel.getMessageById(jsonConfigObject.getString(key)).complete();
				
			} catch(ErrorResponseException e) { message = null; }
			
			if(message == null) message = devsWorksChannel.sendMessage(embed).complete();
			else {
				devsWorksChannel.deleteMessageById(this.jsonConfigObject.getString(key)).complete();
				message = devsWorksChannel.sendMessage(embed).complete();
			}
			
			this.jsonConfigObject.put(key, message.getId());
			this.saveConfig();
			
			return;
			
		}
		
	}
	
	
	
	public File configFile() {
		
		return new File("config/config.json");
		
	}
	
	public JDA getClient() {
		return client;
	}

	public void setClient(JDA client) {
		this.client = client;
	}
	
}
