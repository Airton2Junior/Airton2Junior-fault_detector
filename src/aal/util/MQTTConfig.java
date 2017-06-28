package aal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import aal.ft.entity.NodeX;
import aal.net.Network;

public abstract class MQTTConfig {

	public static void changeConfig(NodeX nodeX){
		FileReader reader = null;
        BufferedReader bReader = null;
        FileWriter writer = null; 
        if (nodeX.changeParent())
		try {
			File fileReader = new File(Params.CONFIG_SOURCE);
			File fileWriter = new File(Params.CONFIG_OUTPUT);
	        if ( !fileWriter.exists() )
				fileWriter.createNewFile();
	        reader = new FileReader(fileReader);
	        bReader = new BufferedReader(reader);
	        writer = new FileWriter(fileWriter);
			String sCurrentLine;
			if (nodeX.getParent() != null){
				while ((sCurrentLine = bReader.readLine()) != null) {
					if (sCurrentLine.contains(Params.ADDRESS_MARK)){
						sCurrentLine = sCurrentLine.replace(Params.ADDRESS_MARK, nodeX.getParent().getIdNodeX());
						Debug.logFT("FTClient - MQTTConfig - CHANGED Main Address");
					}
				    writer.write(sCurrentLine+"\n"); 
				    writer.flush();
				}
			}else{
				while ((sCurrentLine = bReader.readLine()) != null) {
					if (sCurrentLine.contains(Params.CONNECTION)){
						Debug.logFT("FTClient - MQTTConfig - EXCLUDE Main Address");
						break;
					}
				    writer.write(sCurrentLine+"\n"); 
				    writer.flush();
				}
			}			
		} catch (IOException e) {
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
	}

	public static void resetService(NodeX nodeX) {
		FileReader reader = null;
        BufferedReader bReader = null;
        if (nodeX.changeParent())
		try {
			Network.waitDelayAPP("FTClient - MQTTConfig - WAIT DELAY for STOP MQTT Server");
			File fileReader = new File(Params.PID_FILE);
			if (fileReader.exists()){
		        reader = new FileReader(fileReader);
		        bReader = new BufferedReader(reader);
				String sCurrentLine = bReader.readLine();
				if (sCurrentLine != null) {
					String cmdStopMQTT = Params.CMD_KILL_MQTT+sCurrentLine;
					Runtime.getRuntime().exec(cmdStopMQTT);
					Debug.logFT("FTClient - MQTTConfig - STOPED MQTT Server");
				}
			}else{
				Runtime.getRuntime().exec(Params.CMD_STOP_MQTT);
				Debug.logFT("FTClient - MQTTConfig - STOPED MQTT Server");
			}
			Network.waitDelayAPP("FTClient - MQTTConfig - WAIT DELAY for STOP MQTT Server");
			Runtime.getRuntime().exec(Params.CMD_STOP_MQTT);
			Debug.logFT("FTClient - MQTTConfig - STOPED MQTT Server");
			Network.waitDelayAPP("FTClient - MQTTConfig - WAIT DELAY for RESTART MQTT Server");
			Runtime.getRuntime().exec(Params.CMD_START_MQTT);
			Debug.logFT("FTClient - MQTTConfig - RESTARTED MQTT Server");
			Network.waitDelayAPP("FTClient - MQTTConfig - WAIT DELAY for RESTART APP Openhab");
			Runtime.getRuntime().exec(Params.CMD_RESTART_APP);
			Debug.logFT("FTClient - MQTTConfig - RESTARTED APP Openhab");
		} catch (IOException e) {
			Debug.logException(e);
		} finally {
			try {
				if (bReader!= null) bReader.close();
				if (reader!= null) reader.close();
			} catch (IOException e) {
				Debug.logException(e);
			}
		}
		
	}


	
}
