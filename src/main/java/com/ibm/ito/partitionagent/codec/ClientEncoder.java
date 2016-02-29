package com.ibm.ito.partitionagent.codec;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.ibm.ito.partitionagent.model.Constant;
import com.ibm.ito.partitionagent.model.Request;

public class ClientEncoder extends ProtocolEncoderAdapter {

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ClientEncoder.class);
	
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

		Request req = (Request) message;
		IoBuffer buf = IoBuffer.allocate(Constant.LENGTH_HEAD).setAutoExpand(true);
		buf.put(Constant.FLAG_REQ);
		buf.put(req.getType());
		String uuid = req.getUuid();
		String parameters = req.getParameters() == null ? StringUtils.EMPTY : req.getParameters();
		
		int length = (parameters == null) ? 0 : parameters.getBytes(Constant.CHARSET).length;
		byte[] data = parameters.getBytes(Constant.CHARSET);
		byte[] udata = uuid.getBytes(Constant.CHARSET);
		
		
		buf.put(udata);
		buf.putLong(req.getWaitTime());
		buf.putInt(length);
		buf.put(data);
		buf.flip();
		out.write(buf);
		
		
	}

}
