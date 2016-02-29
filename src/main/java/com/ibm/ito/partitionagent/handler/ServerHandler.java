package com.ibm.ito.partitionagent.handler;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.ibm.ito.partitionagent.model.Constant;
import com.ibm.ito.partitionagent.model.Request;
import com.ibm.ito.partitionagent.model.Response;
import com.ibm.ito.partitionagent.utils.CmdExecutor;
import com.ibm.ito.partitionagent.utils.FileUtils;
import com.ibm.ito.partitionagent.utils.ResultParser;

public class ServerHandler extends IoHandlerAdapter {
	
    private static final Logger log = Logger.getLogger(ServerHandler.class);

    
    @Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
//    	log.warn(cause.getLocalizedMessage());
    	log.warn("handler exception", cause);
    	session.close(true);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		Response res = (Response) message;
		String toString = res == null ? null : res.brief();
		log.debug("sent message: " + toString);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		Request req = (Request) message;
		log.debug("receive message: " + req);
		
		String cmd = req.generateCommand();
		if (req.getType() == Constant.TAG_NMON || req.getType() == Constant.TAG_FULLNMON) {
			cleanAllNmonFile();
		}
		long now = System.currentTimeMillis();
		Map<String, Object> result = CmdExecutor.execImmediateCmd(cmd);
		// 如果是nmon命令，则需要休息几秒，等待文件生成
		if (req.getType() == Constant.TAG_NMON || req.getType() == Constant.TAG_FULLNMON) {
			String stdout = generateNmonResult(now, req.getWaitTime());
			if (StringUtils.isBlank(stdout)) {
				result.put(Constant.RESULT_EXIT_VALUE, 1);
				result.put(Constant.RESULT_STD_ERR, "The nmon file has not been generated yet.");
			} else {
				result.put(Constant.RESULT_EXIT_VALUE, 0);
				result.put(Constant.RESULT_STD_OUT, stdout);
			}
		}
		String res = ResultParser.parse(result, req.getType());
		Response response = new Response(req);
		response.setResult(res);
		session.write(response);
	}

	// 清除当前目录下所有nmon结尾的文件，防止有以前遗留的nmon累积
	private void cleanAllNmonFile(){
		String dirname = FileUtils.queryCurrentPath();
		File dir = new File(dirname);
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return StringUtils.endsWith(file.getName(), ".nmon");
			}
		});
		if (ArrayUtils.isNotEmpty(files)) {
			for (File file : files) {
				file.delete();
			}
		}
	}
	
	private String generateNmonResult(final long now, long waitTime){
		// sleep a while
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {

		}
		log.debug("generateNmonResult:" + now);
		String dirname = FileUtils.queryCurrentPath();

		File dir = new File(dirname);
		File[] files = dir.listFiles(new FileFilter() {
			// 过滤出nmon为扩展名并且最近更新的文件
			public boolean accept(File file) {
				if (file.lastModified() < now) {
					return false;
				}
				return StringUtils.endsWith(file.getName(), ".nmon");
			}
		});
		if (ArrayUtils.isEmpty(files)) {
			return null;
		}
		File file = files[0];
		String filename = file.getPath();
		log.debug("filepath=" + filename);
		String result = FileUtils.readFileByChars(filename);
//		try {
////			log.debug("filecont=" + result);
//			log.debug("fileSize=" + result.getBytes(Constant.CHARSET).length);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// 文件读取完之后就可以删除了
//		file.delete();
		return result;
	}

	
}