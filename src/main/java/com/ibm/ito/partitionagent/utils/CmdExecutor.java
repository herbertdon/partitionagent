package com.ibm.ito.partitionagent.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.ito.partitionagent.model.Constant;

public class CmdExecutor {

	private static final Logger log = Logger.getLogger(CmdExecutor.class);
	
	public static Map<String, Object> execImmediateCmd(String cmd){
		log.debug("execute:" + cmd);
		Map<String, Object> result = new HashMap<String, Object> ();
		if (StringUtils.isBlank(cmd)) {
			return result;
		}
		Runtime rt = Runtime.getRuntime();
		Process process = null;
		int exitValue = -1023;
		String stdout = null;
		String stderr = null;
		try {
			process = rt.exec(cmd);
			InputStream stdoutStream = process.getInputStream();
			InputStream stderrStream = process.getErrorStream();

			stdout = loadStream(stdoutStream);
			stderr = loadStream(stderrStream);
			
			exitValue = process.waitFor();
//			log.debug("exitValue:" + exitValue);
//			log.debug("stdout:" + stdout);
//			log.debug("stdout.length=" + stdout.getBytes(Constant.CHARSET).length);
//			log.debug("stderr:" + stderr);
			
			if (stdoutStream != null) {
				stdoutStream.close();
			}
			if (stderrStream != null) {
				stderrStream.close();
			}
			
		} catch (Exception e) {
			log.error("exception", e);
			stderr = e.getLocalizedMessage();
		}
		result.put(Constant.RESULT_EXIT_VALUE, new Integer(exitValue));
		result.put(Constant.RESULT_STD_OUT, stdout);
		result.put(Constant.RESULT_STD_ERR, stderr);
		return result;
	}
	


	
    // read an input-stream into a String
    private static String loadStream(InputStream in) throws IOException {
        int ptr = 0;
        in = new BufferedInputStream(in);
        StringBuffer buffer = new StringBuffer();
        while( (ptr = in.read()) != -1 ) {
            buffer.append((char)ptr);
        }
        return buffer.toString();
    }

}
