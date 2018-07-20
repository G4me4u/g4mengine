package com.g4mesoft.net;

public enum NetworkSide {

	CLIENT(0, "client"),
	SERVER(1, "server");
	
	private final int index;
	private final String name;
	
	private NetworkSide(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public NetworkSide fromIndex(int index) {
		if (index == CLIENT.index)
			return CLIENT;
		if (index == SERVER.index)
			return SERVER;
		return null;
	}
}
