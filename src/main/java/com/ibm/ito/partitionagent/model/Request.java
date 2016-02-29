package com.ibm.ito.partitionagent.model;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class Request extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -761671341303195265L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Request.class);
	
	private byte type;
	private String uuid;
	private long waitTime;
	private String parameters;
	
	public Request() {
		uuid = UUID.randomUUID().toString();
	}
	
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public String generateCommand(){
		String result = "";
		String prefixC = "/bin/sh -c ";
		String prefix  = "/bin/sh ";
		switch (type) {
		case Constant.TAG_NMON:
			//result = prefix + "\"/usr/bin/nmon";
			result = "./nmon.sh";
			break;
		case Constant.TAG_FULLNMON:
			//result = prefix + "nmon";
			result = "./nmon.sh";
			break;
		case Constant.TAG_IOSTAT:
			result = prefixC + "iostat";
			break;
		case Constant.TAG_VMSTAT:
			result = prefixC + "vmstat";
			break;
		case Constant.TAG_ENTSTAT:
			result = "./entstat.sh";
			break;
		case Constant.TAG_CMD:
			result = "";
			break;
		default:
			break;
		}
		
		
		if(type==Constant.TAG_NMON || type==Constant.TAG_FULLNMON){
			//result += " " + parameters + "\"";
		}else{
			result += " " + parameters;
		}
		return StringUtils.trim(result);
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}
	
}
