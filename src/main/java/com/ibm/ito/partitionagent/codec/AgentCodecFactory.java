package com.ibm.ito.partitionagent.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class AgentCodecFactory implements ProtocolCodecFactory {

	private ProtocolDecoder agentDecoder = null;
	private ProtocolEncoder agentEncoder = null;
	
	public AgentCodecFactory(boolean isServer) {
		if (isServer) {
			agentEncoder = new ServerEncoder();
			agentDecoder = new ServerDecoder();
		} else {
			agentEncoder = new ClientEncoder();
			agentDecoder = new ClientDecoder();
		}
	}
	
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return agentEncoder;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return agentDecoder;
	}

}
