package aal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Algoritm {	
	
	public static int getAlgoritm() {
		int ret = Params.ALGORITM_TWO;
		String strRet = "";
		FileReader reader = null;
		FileWriter writer = null;
        BufferedReader bReader = null;
		try {
			File fileReader = new File(Params.ALGORITM_FILE);
			if (fileReader.exists()){
		        reader = new FileReader(fileReader);
		        bReader = new BufferedReader(reader);
				String sCurrentLine;
				while ((sCurrentLine = bReader.readLine()) != null) {
					strRet = sCurrentLine.trim();
				}
				if (strRet.equals("")){
					ret = Params.ALGORITM_TWO;
					Debug.logFD("getAlgoritm - Algoritm DEFAULT :"+ret+"|<");
				}else{
					ret = Integer.parseInt(strRet); 
					Debug.logFD("getAlgoritm - Algoritm recuperado do ARQUIVO("+Params.ALGORITM_FILE+"):"+ret+"|<");
				}
			}else{
				File fileWriter = new File(Params.ALGORITM_FILE);
				fileWriter.createNewFile();
				writer = new FileWriter(fileWriter);
			    writer.write(Params.ALGORITM_TWO+"\n"); 
			    writer.flush();			    
			}
		} catch (Exception e) {
			ret = Params.ALGORITM_TWO;
			Debug.logException(e);
		} finally {
			try {
				if (bReader!= null) bReader.close();
				if (reader!= null) reader.close();
				if (writer!= null) writer.close();
			} catch (IOException e) {
				Debug.logException(e);
			}
		}
		return ret;
	}

}
