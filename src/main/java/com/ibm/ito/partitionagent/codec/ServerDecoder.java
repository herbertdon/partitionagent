package com.ibm.ito.partitionagent.codec;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.ibm.ito.partitionagent.model.Constant;
import com.ibm.ito.partitionagent.model.Request;

public class ServerDecoder extends ProtocolDecoderAdapter {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ServerDecoder.class);

	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// head
		in.get();  // skip 'FLAG'
		byte type = in.get();
		byte[] udata = new byte[Constant.LENGTH_UUID];
		in.get(udata);
		String uuid = new String(udata, Constant.CHARSET);
		long waitTime = in.getLong();
		// 取body长度
		int length = in.getInt();

		// body
		byte[] data = new byte[length];
		in.get(data);
		String content = new String(data, Constant.CHARSET);
//		log.info("request: length=" + length + "; content=" + content);

		Request req = new Request();
		req.setType(type);
		req.setUuid(uuid);
		req.setWaitTime(waitTime);
		req.setParameters(content);
		out.write(req);
	}

}
