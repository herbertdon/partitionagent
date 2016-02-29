package com.ibm.ito.partitionagent.handler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.ibm.ito.partitionagent.model.Constant;
import com.ibm.ito.partitionagent.model.Request;
import com.ibm.ito.partitionagent.model.Response;
 
/**
 * for test only
 * @author zhujunyong
 *
 */
public class ClientHandler extends IoHandlerAdapter {
	
	private static final Logger log = Logger.getLogger(ClientHandler.class);
    // 当一个客端端连结到服务器后
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    	log.debug("session open");
    }
 
    // 当一个客户端关闭时
    @Override
    public void sessionClosed(IoSession session) {
        log.debug("I'm Client &&  I closed!");
    }
 
    // 当服务器端发送的消息到达时:
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	@SuppressWarnings("unchecked")
		Map<String, String> resultList = (Map<String, String>)session.getAttribute(Constant.RESULTLIST_KEYWORD);
        Response res = (Response) message;
        // log
        String toString = res == null ? null : res.brief();
        log.debug("I received a message:" + toString);
        // put result into session;
        resultList.put(res.getUuid(), res.getResult());
    }
    
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	log.error("handler exception", cause);
    	session.close(true);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		Request req = (Request) message;
		log.debug("I am client, I sent " + req);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    	log.error("timeout now, close it");
    	session.close(true);
	}
}