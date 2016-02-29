package com.ibm.ito.partitionagent.model;

import org.apache.commons.lang3.StringUtils;

public class Response extends BaseObject {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4882718430667569589L;

	private byte type;
	private String uuid;
	private String result;
	
	public Response() {
	}
	
	public String brief(){
		String toString = String.valueOf(toString());
		if (toString.length() > 1024) {
			return StringUtils.substring(toString, 0, 1024) + "...";
		} else {
			return toString;
		}
	}
	
	
	public Response(Request req) {
		if (req == null) {
			return;
		}
		type = req.getType();
		uuid = req.getUuid();
	}
	
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
}
