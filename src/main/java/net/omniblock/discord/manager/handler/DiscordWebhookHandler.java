package net.omniblock.discord.manager.handler;

import java.io.IOException;
import java.util.Scanner;

import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import net.omniblock.discord.config.TokenConfig;
import net.omniblock.discord.manager.DHandlers;
import net.omniblock.discord.manager.handler.object.GitUpdate;
import net.omniblock.discord.manager.handler.object.GitUpdate.UpdateType;

public class DiscordWebhookHandler extends NanoHTTPD {
    
    public DiscordWebhookHandler() throws IOException {
    	
        super(8090);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        
    }

    @Override
    public Response serve(IHTTPSession session) {
    	
    	if(session.getHeaders().containsKey("x-gitlab-token")){
    		
    		if(session.getHeaders().get("x-gitlab-token").equals(TokenConfig.GITLAB_TOKEN)) {
    			
    			StringBuffer buffer = new StringBuffer();
    			
    			try(Scanner scanner = new Scanner(session.getInputStream())) {
    			    while (scanner.hasNextLine()) {
    			        buffer.append(scanner.nextLine());
    			    }
    			}
				
				JSONObject base = new JSONObject(buffer.toString());
				
				if(base.getString("object_kind").equals("push") &&
						!base.isNull("commits") && (base.getJSONArray("commits").length() > 0)) {
					
					GitUpdate update = new GitUpdate(UpdateType.PUSH, UpdateType.PUSH.getMapper().extractValues(base)); 
					DHandlers.BOT.handleGitUpdate(update);
					
				}
    			
    			return newFixedLengthResponse(Response.Status.ACCEPTED, MIME_HTML, "");
    			
    		}
    		
    	}
    	
    	Response response = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "");
        response.addHeader("Location", "http://omniblock.net");
        return response;
        
    }
    
}