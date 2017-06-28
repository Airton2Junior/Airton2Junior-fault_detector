package aal.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import aal.fd.entity.Px;
import aal.util.Debug;
import aal.util.Params;

public class Network {


	public static String getMyIP() {
		String ret = "";
		FileReader reader = null;
		FileWriter writer = null;
        BufferedReader bReader = null;
		try {
			File fileReader = new File(Params.IP_FILE);
			if (fileReader.exists()){
		        reader = new FileReader(fileReader);
		        bReader = new BufferedReader(reader);
				String sCurrentLine;
				while ((sCurrentLine = bReader.readLine()) != null) {
					ret = sCurrentLine.trim();
				}
				Debug.logFT("IP recuperado do ARQUIVO("+Params.IP_FILE+"):"+ret+"|<");
				if (ret.equals("")){
					ret = getMyIPAPI();
					Debug.logFT("IP recuperado da API:"+ret+"<|");
				}
			}else{
				File fileWriter = new File(Params.IP_FILE);
				fileWriter.createNewFile();
				writer = new FileWriter(fileWriter);
				ret = getMyIPAPI();
			    writer.write(ret+"\n"); 
			    writer.flush();			    
			}
		} catch (Exception e) {
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

	public static String getMyIPAPI() {
		String ret = "";
		try {
			ret = getHost4Address(0);
			int netaddr = Integer.parseInt(ret.substring(0, ret.indexOf('.')));
			int host = Integer.parseInt(ret.substring(ret.lastIndexOf('.') + 1));
			if ((netaddr == Params.IP_DEF) || (host < Params.INIT_IP) || (host > Params.END_IP)) {
				ret = getHost4Address(1);
			}
		} catch (Exception e) {
			Debug.logException(e);
		}
		return ret;
	}

	public static void waitDelayUDPC(){
		try {
			Thread.sleep(Params.DELAY_UDPC);
		} catch (InterruptedException e) {
			Debug.logException(e);
		}		        
	}

	public static void waitDelayFT(String message, int seconds){
		try {
			Debug.logFT(message);
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
			Debug.logException(e);
		}		        
	}

	public static void waitDelayAPP(String message){
		try {
			Debug.logFT(message);
			Thread.sleep(Params.DELAY_APP);
		} catch (InterruptedException e) {
			Debug.logException(e);
		}		        
	}

	public static void waitDelayEFD(String message, int seconds){
		try {
			Debug.logEFD(message);
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
			Debug.logException(e);
		}		        
	}

	public static ArrayList<Px> getNeighborsWithoutMe(String idNodeX) {
		ArrayList<Px> pxArray = new ArrayList<Px>();
		// TODO fazer o levantamento CORRETO! dos vizinhos
		String rede = "192.168.1.";
		for (int host = Params.INIT_IP; host <= Params.END_IP; host++) {
			Px px = new Px(new String(rede + host));
			if (!px.getIdPx().equals(idNodeX))
				pxArray.add(px);
		}
		return pxArray;
	}

	public static ArrayList<Px> getNeighborsWithMe() {
		ArrayList<Px> pxArray = new ArrayList<Px>();
		// TODO fazer o levantamento CORRETO! dos vizinhos
		String rede = "192.168.1.";
		for (int host = Params.INIT_IP; host <= Params.END_IP; host++) {
			Px px = new Px(new String(rede + host));
			pxArray.add(px);
		}
		return pxArray;
	}
	
	public static String decodeMyIdPx(String myIdPx, String serverIdPx){
		String ret = serverIdPx;
		if (serverIdPx.equals(myIdPx))
			ret = Params.LOCALHOST;
		return ret;
	}

	public static String getBroadcastAddress() {
		String found_bcast_address = null;
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
			while (niEnum.hasMoreElements()) {
				NetworkInterface ni = niEnum.nextElement();
				if (!ni.isLoopback()) {
					for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
						found_bcast_address = interfaceAddress.getBroadcast().toString();
						found_bcast_address = found_bcast_address.substring(1);
					}
				}
			}
		} catch (SocketException e) {
			Debug.logException(e);
			;
		}
		return found_bcast_address;
	}

	/**
	 * Returns this host's non-loopback IPv4 addresses.
	 */
	public static List<Inet4Address> getInet4Addresses() throws SocketException {
		List<Inet4Address> ret = new ArrayList<Inet4Address>();

		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets)) {
			Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAddresses)) {
				if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
					ret.add((Inet4Address) inetAddress);
				}
			}
		}

		return ret;
	}

	/**
	 * Returns this host's first non-loopback IPv4 address string in textual
	 * representation.
	 */
	public static String getHost4Address(int idx) throws SocketException {
		List<Inet4Address> inet4 = getInet4Addresses();
		return !inet4.isEmpty() ? inet4.get(idx).getHostAddress() : null;
	}


//	public ArrayList<Px> getNeighborsToChange() {
//		Debug.logFD("T1 - Get Neighbors!");
//		ArrayList<Px> pxArray = new ArrayList<Px>();
//		String broadcastAdrr = getBroadcastAddress();
//		String netAdrr = broadcastAdrr.substring(0, broadcastAdrr.lastIndexOf('.') + 1);
//		//Debug.log("T1 - NET ADRR:" + netAdrr);
//		int firstAdrr = 1;
//		int lastAdrr = Integer.parseInt(broadcastAdrr.substring(broadcastAdrr.lastIndexOf('.') + 1));
//		//Debug.log("T1 - LAST ADRR:" + lastAdrr);
//		for (int host = firstAdrr; host < lastAdrr; host++) {
//			Px px = new Px(new String(netAdrr + host));
//			pxArray.add(px);
//			//Debug.log("T1 - ADD HOST:" + px);
//		}
//		return pxArray;
//	}


}
