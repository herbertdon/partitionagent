package com.ibm.ito.partitionagent.model;


public class Constant {
	// request
	public final static byte FLAG_REQ = 1;
	// response
	public final static byte FLAG_RES = 2;

	// 请求执行的命令类别
	public final static byte TAG_CMD = 0;
	public final static byte TAG_NMON = 1;
	public final static byte TAG_IOSTAT = 2;
	public final static byte TAG_VMSTAT = 3;
	public final static byte TAG_ENTSTAT = 4;
	public final static byte TAG_FULLNMON = 5;

	// 如果是nmon命令，则需要休息几秒，等待文件生成,单位：毫秒
//	public final static long WAIT_TIME = 60 * 1000;
	
	// charset
//	public final static String CHARSET = "UTF-8";
	public final static String CHARSET = "ISO-8859-1";
	
	
	public final static String ENTER = "\n";
	
	
	// byte + byte + 36(uuid) + long(nmon wait time) + int
	public final static int LENGTH_HEAD = 50;
	public final static int LENGTH_UUID = 36;
	
	
	public final static String RESULT_EXIT_VALUE = "exitValue";
	public final static String RESULT_STD_OUT = "stdout";
	public final static String RESULT_STD_ERR = "stderr";
	
	public final static String RESULTLIST_KEYWORD = "resultList";
	
}
