package aal.ft.entity;

import aal.util.MQTTConfig;

public class MQTTServer {
	
	public static void updateMQTTServer(NodeX nodeX){
		MQTTConfig.changeConfig(nodeX);	
		MQTTConfig.resetService(nodeX);
	}

}
