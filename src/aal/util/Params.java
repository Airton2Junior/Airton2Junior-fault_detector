package aal.util;

public class Params {

	public static final int ZERO = 0; 
	public static final int SECOND = 1000; 
	public static final int DELAY_UDPC = 3 * SECOND; 
	public static final int DELAY_APP = 7 * SECOND; 
	public static final int DELAY_LOCATE_PARENT = 7 * SECOND; 
	public static final int DELAY_PROPAGATE_FD_STATUS = 13 * SECOND;
	public static final int DELAY_EVALUATE_FD_STATUS = 30 * SECOND;

	public static final int MAX_TENTATIVAS = 3;

	public static final int PORT_FD  = 4455;
	public static final int PORT_FT  = 4456;
	public static final int PORT_EFD = 4457;

	public static final String PATH_LOG = "/etc/openhab/logs/";
	
	public static final String LOCALHOST = "localhost";
	public static final int INIT_IP = 20;
	public static final int END_IP = 60;
	public static final int IP_DEF = 169;

	public static final String CONFIG_SOURCE = "/etc/mosquitto/mosquitto.conf.sample";
	public static final String CONFIG_OUTPUT = "/etc/mosquitto/mosquitto.conf";
	public static final CharSequence ADDRESS_MARK = "XXX.XXX.XXX";
	public static final String CONNECTION = "connection main";
	
	
	public static final String PID_FILE = "/var/run/mosquitto.pid";
	public static final String IP_FILE = "/etc/openhab/ip";

	public static final String ALGORITM_FILE = "/etc/openhab/algoritm";
	public static final int ALGORITM_ONE = 1;
	public static final int ALGORITM_TWO = 2;

	public static final String CMD_KILL_MQTT = "sudo kill -9 ";
	public static final String CMD_STOP_MQTT = "sudo /etc/init.d/mosquitto stop";
	public static final String CMD_START_MQTT = "sudo /etc/init.d/mosquitto start"; //"sudo /usr/sbin/mosquitto -c /etc/mosquitto/mosquitto.conf -d";
	public static final String CMD_RESTART_APP = "sudo /etc/init.d/openhab restart";
	
}
