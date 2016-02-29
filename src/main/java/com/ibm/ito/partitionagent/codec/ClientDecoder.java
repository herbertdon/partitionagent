package com.ibm.ito.partitionagent.codec;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.ibm.ito.partitionagent.model.Constant;
import com.ibm.ito.partitionagent.model.Response;

public class ClientDecoder extends CumulativeProtocolDecoder {

	private static final Logger log = Logger.getLogger(ClientDecoder.class);

	private static final String RESULT_LENGTH = "resultLength";
	private static final String RESPONSE_KEY = "response";

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		Integer resultLength = (Integer) session.getAttribute(RESULT_LENGTH);
		Response response = (Response) session.getAttribute(RESPONSE_KEY);

		if (response == null) {
			response = new Response();
			session.setAttribute(RESPONSE_KEY, response);
		}
		if (StringUtils.isBlank(response.getUuid())) {
			if (in.remaining() < Constant.LENGTH_HEAD) {
				return false;
			}
			// head
			@SuppressWarnings("unused")
			byte flag = in.get();
			// type
			byte type = in.get();
			byte[] udata = new byte[Constant.LENGTH_UUID];
			in.get(udata);
			String uuid = new String(udata, Constant.CHARSET);
			// 取body长度
			int length = in.getInt();

			response.setType(type);
			response.setUuid(uuid);
			session.setAttribute(RESPONSE_KEY, response);
			resultLength = length;
			session.setAttribute(RESULT_LENGTH, length);
		}

		if (StringUtils.isNotBlank(response.getUuid())) {
			if (in.remaining() < resultLength.intValue()) {
				log.debug("remaining : " + in.remaining() + " < " + resultLength.intValue());
				return false;
			}

			byte[] data = new byte[resultLength];
			in.get(data);
			String result = new String(data, Constant.CHARSET);

			response.setResult(result);
			out.write(response);
			session.setAttribute(RESPONSE_KEY, null);
			return true;
		}
		return false;
	}

}
