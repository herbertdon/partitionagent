package com.ibm.ito.partitionagent.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.ito.partitionagent.model.Constant;

public class ResultParser {

	private static final Logger log = Logger.getLogger(ResultParser.class);
	
	public static String parse(Map<String, Object> map, byte tag){
		if (MapUtils.isEmpty(map)) {
			return StringUtils.EMPTY;
		}
		Integer exitValue = (Integer) map.get(Constant.RESULT_EXIT_VALUE);
		String stdout = (String)map.get(Constant.RESULT_STD_OUT);
		String stderr = (String)map.get(Constant.RESULT_STD_ERR);
		if (exitValue != 0) {
			return stderr;
		}
		String result = null;
		switch (tag) {
		case Constant.TAG_IOSTAT:
			result = parseIostat(stdout);
			break;
		case Constant.TAG_VMSTAT:
			result = parseVmstat(stdout);
			break;
		case Constant.TAG_ENTSTAT:
			result = parseEntstat(stdout);
			break;
		case Constant.TAG_NMON:
			result = parseNmon(stdout);
			break;
		case Constant.TAG_FULLNMON:
			result = parseFullNmon(stdout);
			break;
		case Constant.TAG_CMD:
			result = parseCmd(stdout);
			break;
		default:
			break;
		}
		return result;
	}

	private static String parseCmd(String stdout){
		// TODO 
		
		return stdout;
	}

	private static String parseFullNmon(String stdout){
		// 把nmon文件的所有内容都返回
		return stdout;
	}
	
	public static String parseNmon(String stdout){
		List<String> list = String2List(stdout);
		// 找出networkname的前缀
		String prefix = null;
		for (String line : list) {
			if (StringUtils.contains(line, ",NetworkName,MTU,Mbits,Name")) {
				String[] network = StringUtils.split(line, ",");
				prefix = network[0];
				break;
			}
		}
		// 如果找不到network信息
		if (StringUtils.isBlank(prefix)) {
			log.warn("Can not found network keyword in nmon file.");
			return StringUtils.EMPTY;
		}
		// 根据networkname的前缀，找出所有的网卡
		List<String> networkList = new ArrayList<String> ();
		for (String line : list) {
			if (StringUtils.startsWith(line, prefix)) {
				networkList.add(line);
			}
		}
		// 把网卡关键字找出来
		List<String> networkKeyList = new ArrayList<String> ();
		for (int i = 0; i < networkList.size(); i++) {
			String line = networkList.get(i);
			// 第一行是title，不用筛选
			if (i > 0) {
				String[] split = StringUtils.split(line, ",");
				if (ArrayUtils.isNotEmpty(split) && split.length > 3)
					networkKeyList.add(split[2]);
				
			}
		}
		// 通过网卡关键字(例如：en0，lo0)找到还有ifconfig的那一行，取下一行就可包含ip和netmask等信息
		List<String> result = new ArrayList<String> ();
		result.add(networkList.get(0));
		log.debug("networkKeyList=" + networkKeyList);
		log.debug("list.size=" + list.size());
		for (int j = 0; j < networkKeyList.size(); j++) {
			String key = networkKeyList.get(j);
			String containKey = ",ifconfig,\"" + key;
			for (int i = 0; i < list.size(); i++) {
				String line = list.get(i);
				if (StringUtils.contains(line, containKey) && i < list.size() - 1) {
					result.add(networkList.get(j+1));
					result.add(list.get(i+1));
					break;
				}
			}
		}
		// 加入磁盘信息，同时包含"lsconf"和"m hdisk"的行
		for (String line : list) {
			if (StringUtils.contains(line, ",lsconf,") && StringUtils.contains(line, "m hdisk")) {
				result.add(line);
			}
		}
		return StringUtils.join(result,Constant.ENTER);
	}

	private static String parseIostat(String stdout){
		List<String> stdList = String2List(stdout);
		// 把所有hdisk开头的行截取下来
		List<String> result = new ArrayList<String> ();
		for (String std : stdList) {
			if (StringUtils.startsWith(std, "hdisk")) {
				result.add(std);
			}
		}
		return StringUtils.join(result, Constant.ENTER);
	}

	private static String parseVmstat(String stdout){
		// TODO 
		
		return stdout;
	}
	
	public static void main(String[] args) {
//		String stdout = FileUtils.readFileByChars("C:\\ssmtest_140717_1800.nmon");
//		String stdout = FileUtils.readFileByLines("C:\\ssmtest_140717_1800.nmon");
		String stdout = FileUtils.readBinaryFile("C:\\ssmtest_140717_1800.nmon");
		
//		String stdout = FileUtils.readFileByChars("C:\\box\\workspace\\stg_51\\partitionagent\\target\\ssmtest_140728_1618.nmon");
		String s = parseNmon(stdout);
		System.out.println(s);
	}
	
	private static List<String> String2List(String stdout){
		List<String> result = new ArrayList<String> ();
		if (StringUtils.isBlank(stdout)) {
			return result;
		}
		String[] split = StringUtils.split(stdout, Constant.ENTER);
		for (String s : split) {
			result.add(s);
		}
		return result;
	}
	
	private static String parseEntstat(String stdout){
//		String regex = "Packets: [\\d]+[ ]+Packets: [\\d]+[ ]*\nBytes: [\\d]+[ ]+Bytes: [\\d]+";
//		boolean isMatch = stdout.matches(regex);
//		log.debug("isMatch = " + isMatch);
		String temp = StringUtils.substringAfter(stdout, "Packets: ");
		String transmit1Packets = StringUtils.substringBefore(temp, " ");
		String temp1 = StringUtils.substringAfter(temp, "Packets: ");
		String receive1Packets = StringUtils.substringBefore(temp1, Constant.ENTER);
		
		temp = StringUtils.substringAfter(stdout, "Bytes: ");
		String transmit1Bytes = StringUtils.substringBefore(temp, " ");
		temp1 = StringUtils.substringAfter(temp, "Bytes: ");
		String receive1Bytes = StringUtils.substringBefore(temp1, Constant.ENTER);
		
		stdout = StringUtils.substringAfterLast(stdout, "-------------------------------------------------------------");
		temp = StringUtils.substringAfter(stdout, "Packets: ");
		String transmit2Packets = StringUtils.substringBefore(temp, " ");
		temp1 = StringUtils.substringAfter(temp, "Packets: ");
		String receive2Packets = StringUtils.substringBefore(temp1, Constant.ENTER);
		
		temp = StringUtils.substringAfter(stdout, "Bytes: ");
		String transmit2Bytes = StringUtils.substringBefore(temp, " ");
		temp1 = StringUtils.substringAfter(temp, "Bytes: ");
		String receive2Bytes = StringUtils.substringBefore(temp1, Constant.ENTER);

		log.debug("transmit1Packets="+transmit1Packets);
		log.debug("receive1Packets="+receive1Packets);
		log.debug("transmit1Bytes="+transmit1Bytes);
		log.debug("receive1Bytes="+receive1Bytes);
		log.debug("transmit2Packets="+transmit2Packets);
		log.debug("receive2Packets="+receive2Packets);
		log.debug("transmit2Bytes="+transmit2Bytes);
		log.debug("receive2Bytes="+receive2Bytes);
		
		Long transmit1PacketsLong = Long.parseLong(transmit1Packets);
		Long receive1PacketsLong = Long.parseLong(receive1Packets);
		Long transmit1BytesLong = Long.parseLong(transmit1Bytes);
		Long receive1BytesLong = Long.parseLong(receive1Bytes);
		Long transmit2PacketsLong = Long.parseLong(transmit2Packets);
		Long receive2PacketsLong = Long.parseLong(receive2Packets);
		Long transmit2BytesLong = Long.parseLong(transmit2Bytes);
		Long receive2BytesLong = Long.parseLong(receive2Bytes);
		
		Long transmitPackets = transmit2PacketsLong - transmit1PacketsLong;
		Long transmitBytes = transmit2BytesLong - transmit1BytesLong;
		Long receivePackets = receive2PacketsLong - receive1PacketsLong;
		Long receiveBytes = receive2BytesLong - receive1BytesLong;
		
		log.debug("transmitPackets=" + (transmit2PacketsLong - transmit1PacketsLong));
		log.debug("transmitBytes=" + (transmit2BytesLong - transmit1BytesLong));
		log.debug("receivePackets=" + (receive2PacketsLong - receive1PacketsLong));
		log.debug("receiveBytes=" + (receive2BytesLong - receive1BytesLong));

		
		String result = "transmitPackets=" + transmitPackets + ",transmitBytes=" + transmitBytes + ",receivePackets=" + receivePackets + ",receiveBytes="
				+ receiveBytes;
		//		String result = "transmit1Packets=" + transmit1Packets + ";"
//				+ "receive1Packets=" + receive1Packets + ";"
//				+ "transmit1Bytes=" + transmit1Bytes + ";"
//				+ "receive1Bytes=" + receive1Bytes + ";"
//				+ "transmit2Packets=" + transmit2Packets + ";"
//				+ "receive2Packets=" + receive2Packets + ";"
//				+ "transmit2Bytes=" + transmit2Bytes + ";"
//				+ "receive2Bytes=" + receive2Bytes;

		return result;
	}
	
	

}
