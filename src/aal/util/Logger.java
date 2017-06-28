package aal.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class Logger {
	
	private File file;
	private FileWriter writer; 
	
	public Logger(String fileName){
		try {
	        File fileDir = new File(Params.PATH_LOG);
	        if ( !fileDir.exists() )
	        	fileDir.mkdir();
	        this.file = new File(Params.PATH_LOG+fileName+"-"+getTimestamp());
	        if ( !this.file.exists() )
				this.file.createNewFile();
	        this.writer = new FileWriter(file);
		} catch (IOException e) {
			Debug.logException(e);
		}
	}
	
	public void log(String content){
		try {
		    this.writer.write(content+"\n"); 
		    this.writer.flush();
		} catch (IOException e) {
			Debug.logException(e);
		}
	}
	
	public void close(){
		try {
			this.writer.close();
		} catch (IOException e) {
			Debug.logException(e);
		}
	}
	
	public void finalize(){
		close();
	}
	
	private static String getTimestamp() {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
		return fmt.format(System.currentTimeMillis());
	}


}
