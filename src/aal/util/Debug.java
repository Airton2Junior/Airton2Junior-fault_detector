package aal.util;

import java.text.SimpleDateFormat;

public class Debug {
	
//	private static Logger loggerUdpc = new Logger("UdpcLog");
//	private static Logger loggerErr = new Logger("ErrLog");
	private static Logger loggerFD = new Logger("FDLog");
	private static Logger loggerFT = new Logger("FTLog");
	private static Logger loggerEFD = new Logger("EFDLog");
	private static Logger loggerEFDFile = new Logger("EFDFile");

	public static void logUdpc(String msg) {
//		loggerUdpc.log(getDataHora() + " " + msg);
	}

	private static void logErr(String msg) {
//		loggerErr.log(getDataHora() + " " + msg);
	}

	public static void logFD(String msg) {
		loggerFD.log(getDataHora() + " " + msg);
	}

	public static void logFT(String msg) {
		loggerFT.log(getDataHora() + " " + msg);
	}

	public static void logEFD(String msg) {
		loggerEFD.log(getDataHora() + " " + msg);
	}

	public static void logEFDFile(String msg) {
		loggerEFDFile.log(getDataHora() + ";" + msg);
	}

	private static String getDataHora() {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return fmt.format(System.currentTimeMillis());
	}

	public static void logException(Exception e) {
//		logErr("reason "+me.getReasonCode());
		logErr("msg " + e.getMessage());
		logErr("loc " + e.getLocalizedMessage());
		logErr("cause " + e.getCause());
		logErr("excep " + e);
//		e.printStackTrace();
	}

}
