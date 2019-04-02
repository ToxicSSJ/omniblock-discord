package net.omniblock.discord.manager.handler.object;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import net.omniblock.discord.manager.utils.wrapper.ImmutableMapWrapper;

public class GitUpdate {

	protected Map<GitObject, String> data_values = new HashMap<GitObject, String>();
	protected UpdateType type;
	
	public GitUpdate(UpdateType type, String[] values) {
		
		this.type = type;
		type.mapper.putValues(data_values, values);
		return;
		
	}
	
	public UpdateType getType() {
		return type;
	}
	
	public Map<GitObject, String> getValues(){
		return data_values;
	}
	
	public static enum UpdateType {
		
		PUSH("push", new UpdateMapper() {

			@Override
			public void putValues(Map<GitObject, String> map, String[] values) {
				
				new ImmutableMapWrapper<GitObject, String>(map)
					.put(GitObject.COMMIT_TITLE, values[0])
					.put(GitObject.COMMIT_BODY, values[1])
					.put(GitObject.COMMIT_SHORT_ID, values[2])
					.put(GitObject.COMMIT_URL, values[3])
					.put(GitObject.REF_SHORT, values[4])
					.put(GitObject.AUTHOR_NAME, values[5])
					.put(GitObject.AUTHOR_AVATAR_URL, values[6])
					.put(GitObject.PROJECT_NAME, values[7]).complete();
				
			}

			@Override
			public String[] extractValues(JSONObject base) {
				
				String[] commit_message = base.getJSONArray("commits").getJSONObject(base.getJSONArray("commits").length() - 1).getString("message").split("\r\n|\r|\n");
				String commit_title = "$none", commit_body = "$none";
				
				if(commit_message.length == 1)
					commit_title = commit_message[0];
				
				if(commit_message.length >= 2) {
					
					commit_title = commit_message[0];
					commit_body = "";
					
					for(int i = 1; i < commit_message.length; i++)
						commit_body = commit_body + commit_message[i] + "\n";
					
				}
				
				return new String[] {
						commit_title,
						commit_body,
						base.getJSONArray("commits").getJSONObject(base.getJSONArray("commits").length() - 1).getString("id").substring(0, 7),
						base.getJSONArray("commits").getJSONObject(base.getJSONArray("commits").length() - 1).getString("url"),
						base.getString("ref").replaceAll("refs/heads/", ""),
						base.getString("user_name"),
						base.getString("user_avatar"),
						base.getJSONObject("project").getString("name") };
				
			}
			
		}),
		
		;
		
		private String type;
		private UpdateMapper mapper;
		
		UpdateType(String type, UpdateMapper mapper){
			this.type = type;
			this.mapper = mapper;
		}

		public String getType() {
			return type;
		}
		
		public UpdateMapper getMapper() {
			return mapper;
		}
		
		public static interface UpdateMapper {
			
			public void putValues(Map<GitObject, String> map, String[] values);
			
			public String[] extractValues(JSONObject base);
			
		}
		
	}
	
}
