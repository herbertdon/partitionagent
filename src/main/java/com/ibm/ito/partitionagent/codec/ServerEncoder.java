package com.ibm.ito.partitionagent.codec;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.ibm.ito.partitionagent.model.Constant;
import com.ibm.ito.partitionagent.model.Response;

public class ServerEncoder extends ProtocolEncoderAdapter {

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ServerEncoder.class);
	
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		Response res = (Response) message;
		String result = res.getResult();
		int length = (result == null) ? 0 : result.getBytes(Constant.CHARSET).length;
		String uuid = res.getUuid();
		byte[] data = result.getBytes(Constant.CHARSET);
		byte[] udata = uuid.getBytes(Constant.CHARSET);
		IoBuffer buf = IoBuffer.allocate(Constant.LENGTH_HEAD).setAutoExpand(true);
		buf.put(Constant.FLAG_RES);
		buf.put(res.getType());
		buf.put(udata);
		buf.putInt(length);
		log.debug("length=" + length);
		buf.put(data);
		buf.flip();
		out.write(buf);
	}

}
