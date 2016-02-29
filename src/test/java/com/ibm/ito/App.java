package com.ibm.ito;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class App {
	public static void main(String args[]) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("cmd /c mvn archetype:generate");
//			String stdout = loadStream(proc.getInputStream());
//			String stderr = loadStream(proc.getErrorStream());
			int exitVal = proc.waitFor();
			System.out.println("Process exitValue: " + exitVal);
//			System.out.println("stdout:" + stdout);
//			System.out.println("stderr:" + stderr);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

    public static String loadStream(InputStream in) throws IOException {
        int ptr = 0;
        in = new BufferedInputStream(in);
        StringBuffer buffer = new StringBuffer();
        while( (ptr = in.read()) != -1 ) {
            buffer.append((char)ptr);
        }
        return buffer.toString();
    }
}
