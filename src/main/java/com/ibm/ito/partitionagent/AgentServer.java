package com.ibm.ito.partitionagent;

import org.apache.log4j.Logger;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Partition Agent Server
 * @author zhujunyong
 * 
 */
public class AgentServer {

	private static final Logger log = Logger.getLogger(AgentServer.class);

	public static void main(String[] args) throws Exception {
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-agent.xml");

		// listening port
		SocketAcceptor acceptor = (NioSocketAcceptor) ac.getBean("ioAcceptor");
		acceptor.getSessionConfig().setReuseAddress(true);
		acceptor.bind();
		log.info("Listening on port:" + acceptor.getDefaultLocalAddress().getPort());

	}
}
