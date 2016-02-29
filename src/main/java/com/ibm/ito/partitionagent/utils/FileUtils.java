package com.ibm.ito.partitionagent.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class FileUtils {

	private static final Logger log = Logger.getLogger(FileUtils.class);
	
	public static String queryCurrentPath(){
		Properties property = System.getProperties();
		String str = property.getProperty("user.dir");
		return str;
	}
	
	
	/**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static String readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
//            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
        	FileInputStream fis = new FileInputStream(file);
            reader = new InputStreamReader(fis);
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                	sb.append((char)tempchar);
//                    System.out.print((char) tempchar);
                }
            }
            fis.close();
            reader.close();
        } catch (Exception e) {
            log.error("read file error, fileName is " + fileName, e);
        }
       return sb.toString();
    }	
    
    
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
//            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	sb.append(tempString+"\n");
//                log.info("line " + line + ": " + tempString);
//                line++;
            }
            reader.close();
        } catch (Exception e) {
        	log.warn("",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                	log.warn("", e1);
                }
            }
        }
        return sb.toString();
    }    
    
    
    public static String loadFromFile(String dir){
    	log.debug("Load From File:" + dir);
        List<String> contentList = new ArrayList<String>();
        
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(dir);
            BufferedReader bufferedReader  = new BufferedReader(fileReader);
            String line = null;
            while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {
                contentList.add(line);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException fnf) {
            log.error(fnf.getMessage(),fnf);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        log.debug("contentList.size=" + contentList.size());
        String result = StringUtils.join(contentList,"\n");
        return result;
    }
    
    /**
     * 从二进制文件读取字节数组
     * 
     * @param sourceFile
     * @return
     * @throws IOException
     */
	public static String readBinaryFile(String path) {
		File sourceFile = new File(path);
		byte[] b = null;
		if (sourceFile.isFile() && sourceFile.exists()) {
			long fileLength = sourceFile.length();
			log.debug("fileLength=" + fileLength); 
			if (fileLength > 0L) {
				try {
					BufferedInputStream fis = new BufferedInputStream(new FileInputStream(sourceFile));
					b = new byte[(int) fileLength];

					while (fis.read(b) != -1) {
					}

					fis.close();
					fis = null;

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return b == null ? StringUtils.EMPTY : new String(b);
	}
    
    public static void main(String[] args) {
		File f = new File("C:\\cpsweb.log");
		System.out.println(f.getPath());
		System.out.println(new Date(f.lastModified()));
		System.out.println(f.getName());
	}
    
}
