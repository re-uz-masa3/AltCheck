package net.masa3mc.altcheck.api;

import java.util.List;

import net.masa3mc.altcheck.Util;

public class AltCheckAPI {
	
	/*
	 *
	 * added AltCheck v1.8
	 *
	 */
	
	private Util util = new Util();
	private String ip;

	public AltCheckAPI(String ip) {
		this.ip = ip.trim().replace(".", "_");
	}
	
	//v1.8 ~
	public List<String> getAccounts() {
		return util.getDataYml().getStringList(ip);
	}
	
}