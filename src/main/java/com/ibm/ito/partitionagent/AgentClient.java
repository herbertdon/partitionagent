package com.ibm.ito.partitionagent;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.ibm.ito.partitionagent.codec.AgentCodecFactory;
import com.ibm.ito.partitionagent.handler.ClientHandler;
import com.ibm.ito.partitionagent.model.Constant;
import com.ibm.ito.partitionagent.model.Request;

public class AgentClient {

	private static final Logger log = Logger.getLogger(AgentClient.class);
	
	private String ip;
	private int port;
	private IoSession session;
	private NioSocketConnector connector;
	
	public AgentClient(String _ip, int _port) {
		ip = _ip;
		port = _port;
		
		try {
			connector = new NioSocketConnector();
			// 设置日志记录器  
	        connector.getFilterChain().addLast("logger", new LoggingFilter());
	        // 设置编码过滤器  
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new AgentCodecFactory(false)));
			// 设置事件处理器  
			connector.setHandler(new ClientHandler());
			// Set connect timeout.
			connector.setConnectTimeoutMillis(60 * 1000L);
			// 连结到服务器:
			ConnectFuture cf = connector.connect(new InetSocketAddress(ip, port));
			// Wait for the connection attempt to be finished.
			cf.awaitUninterruptibly();
//			if (cf.isDone()) {
//				if (cf.isConnected()) {
//					String errmsg = "fail to connect ip=" + _ip + ";port=" + _port;
//					log.warn(errmsg);
//					connector.dispose();
//					throw new Exception(errmsg);
//				}
//			}
			session = cf.getSession();
			session.setAttribute(Constant.RESULTLIST_KEYWORD, new HashMap<String, String>());
		} catch(Exception e) {
			log.warn("create connection failed, ip=" + _ip + ";port=" + _port);
			if (connector != null) {
				connector.dispose();
			}
		}
	}
	
	public String sendFullnmonRequest(long waitTime) {
		Request req = new Request();
		req.setType(Constant.TAG_FULLNMON);
		req.setWaitTime(waitTime);
		if (session != null && session.isConnected()) {
			session.write(req);
			return req.getUuid();
		} else {
			return null;
		}
	}

	public String sendNmonRequest(long waitTime){
		Request req = new Request();
		req.setType(Constant.TAG_NMON);
		req.setWaitTime(waitTime);
		if (session != null && session.isConnected()) {
			session.write(req);
			return req.getUuid();
		} else {
			return null;
		}
	}
	
	public String sendIostatRequest(){
		Request req = new Request();
//		req.setType(Constant.TAG_IOSTAT);
//		req.setParameters("1 1");
		req.setType(Constant.TAG_CMD);
		req.setParameters("iostat 1 1");
		if (session != null && session.isConnected()) {
			session.write(req);
			return req.getUuid();
		} else {
			return null;
		}
	}
	
	
	
	public String sendEntstatRequest(String adaptName){
		Request req = new Request();
		req.setType(Constant.TAG_ENTSTAT);
		req.setParameters(adaptName + " 1");
		if (session != null && session.isConnected()) {
			session.write(req);
			return req.getUuid();
		} else {
			return null;
		}
	}

	public String queryResult(String uuid) {
		if (session == null || !session.isConnected()) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, String> resultList = (Map<String, String>)session.getAttribute(Constant.RESULTLIST_KEYWORD);
		return resultList == null ? null : resultList.remove(uuid);
	}

	
	public String queryResult(String uuid, long maxWaitTime) {
		if (session == null || !session.isConnected()) {
			return null;
		}
		String result = null;
		int i = 0;
		do {
			// 500毫秒
			int interval = 500;
			result = queryResult(uuid);
			if (StringUtils.isNotBlank(result)) {
				break;
			} else {
				i += interval;
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
			}
		} while (i <= maxWaitTime);
		return result;
	}
	
	public Map<String, String> queryResult(){
		if (session == null || !session.isConnected()) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, String> resultList = (Map<String, String>)session.getAttribute(Constant.RESULTLIST_KEYWORD);
		return resultList == null ? new HashMap<String, String>() : resultList;
	}
	
	public void close() {
		if (session != null && session.isConnected()) {
			session.close(true);
		}
		if (connector != null) {
			connector.dispose();
		}
	}
	
	public boolean isActive(){
		if (connector == null) {
			return false;
		}
		return connector.isActive();
	}
	
	public boolean[] queryConnectorStatus(){
		if (connector == null) {
			return null;
		}
		boolean isActive = connector.isActive();
		boolean isDisposed = connector.isDisposed();
		boolean isDisposing = connector.isDisposing();
		return new boolean[]{isActive, isDisposed, isDisposing};
	}

	public static void main(String[] args) throws Exception {
		AgentClient client = null;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			System.out.println("i="+i);
			client = new AgentClient("9.123.126.121", 5600);
			testClient(client);
			client.close();
		}
		
	}
	
	public static void testClient(AgentClient client){
		String uuid = client.sendNmonRequest(5*1000);
		String uuid2 = client.sendIostatRequest();
		String uuid3 = client.sendEntstatRequest("en0");
		String result = client.queryResult(uuid,10000);
		String result2 = client.queryResult(uuid2, 10000);
		String result3 = client.queryResult(uuid3, 10000);
		log.info("=================");
		log.info(result);
		log.info("---------------");
		log.info(result2);
		log.info("---------------");
		log.info(result3);
	}
	
	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) throws Exception {
//		NioSocketConnector connector = new NioSocketConnector();
//		
//		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new AgentCodecFactory(false)));  
//        connector.setHandler(new ClientHandler());
//        // Set connect timeout.
//        connector.setConnectTimeoutMillis(60000L);
//        connector.setConnectTimeoutCheckInterval(30);
//        // 连结到服务器:
//        ConnectFuture cf = connector.connect(new InetSocketAddress("9.123.126.121", 5600));
//        // Wait for the connection attempt to be finished.
//        cf.awaitUninterruptibly();
//        
////        cf.getSession().write(iostat());
////        
////        Thread.sleep(5000);
////        cf.getSession().write(vmstat());
////        Thread.sleep(5000);
////        cf.getSession().write(entstat());
////        Thread.sleep(5000);
//
////        cf.getSession().write(fullnmon());
////        Request req1 = getFullNmonRequest();
////        Request req2 = getFullNmonRequest();
////        cf.getSession().write(req1);
////        cf.getSession().write(req2);
////        cf.getSession().write(getIostat());
////        cf.getSession().write(getEntstat());
////        cf.getSession().write(getVmstat());
////        cf.getSession().write(getIostat());
////        cf.getSession().write(getEntstat());
////        cf.getSession().write(getVmstat());
//        cf.getSession().write(getNmon());
//        
////        Request req = new Request();
////        req.setType(Constant.TAG_CMD);
////        req.setParameters("/bin/sh -c ls");
////        cf.getSession().write(req);
//        
////        if (cf.isConnected()) {
////        	cf.getSession().close(false);
////        }
//        
////        cf.getSession().close(false);
//	}
//	
//	private static Request getIostat(){
//		Request req = new Request();
//		req.setType(Constant.TAG_IOSTAT);
//		req.setParameters("1 1");
//		return req;
//	}
//
//	
//	private static Request getVmstat(){
//		Request req = new Request();
//		req.setType(Constant.TAG_VMSTAT);
//		req.setParameters("");
//		return req;
//	}
//
//	
//	private static Request getEntstat() {
//		Request req = new Request();
//		req.setType(Constant.TAG_ENTSTAT);
//		req.setParameters("en0 1");
//		return req;
//	}
//	
//	
//	private static Request getNmon() {
//		Request req = new Request();
//		req.setType(Constant.TAG_NMON);
//		req.setParameters("-f -s 0 -c 1");
//		req.setWaitTime(5*1000);
//		return req;
//	}
//
//	private static Request getFullNmonRequest() {
//		Request req = new Request();
//		req.setType(Constant.TAG_FULLNMON);
//		req.setParameters("-f -s 0 -c 1");
//		req.setWaitTime(20*1000);
//		return req;
//	}

}
