package net.omniblock.discord.manager.utils.wrapper;

import java.util.Map;

public class ImmutableMapWrapper<K, T> {

	protected Map<K, T> map;
	
	public ImmutableMapWrapper(Map<K, T> map) {
		
		this.map = map;
		
	}
	
	public ImmutableMapWrapper<K, T> put(K k, T t) {
		map.put(k, t);
		return this;
	}
	
	public void complete() {
		return;
	}
	
}
