package aal.ft.net;

import aal.ft.entity.NodeX;
import aal.util.Debug;

public abstract class FTMessage {

	private static final String SEPARATOR = "=";
	private static final String IDNODEX_SEPARATOR = "%";;
	
	public static final String YOUR_POS_NODEX_IS = "YOUR_POS_NODEX_IS";
	private static final String YES_MY_POS_NODEX_IS = "YES_MY_POS_NODEX_IS";
	public static final String WHAT_YOUR_NEW_POS_NODEX_CHILD = "WHAT_YOUR_NEW_POS_NODEX_CHILD";
	private static final String MY_NEW_POS_NODEX_CHILD = "MY_NEW_POS_NODEX_CHILD";
//	public static final String NOTIFY_NEW_POS_NODEX = "NOTIFY_NEW_POS_NODEX";
//	public static final String YOU_ARENT_MY_PARENT = "YOU_ARENT_MY_PARENT";
	public static final String NOTIFY_LOST_PARENT_NODEX = "NOTIFY_LOST_PARENT_NODEX";

	private FTMessage() {
	}

	public static String queryIfPosNodeXIs(String posNodeX) {
		return YOUR_POS_NODEX_IS+SEPARATOR+posNodeX; 		
	}
	
	public static String responseYesMyPosNodeXIs(String posNodeX) {
		return YES_MY_POS_NODEX_IS+SEPARATOR+posNodeX; 		
	}

	public static String getResponsePosNodeXOf(byte[] message) {
		String ret = NodeX.POS_NODEX_ANCESTRAL;
		String strMessage = new String(message);
		Debug.logFT("FTClient - getResponsePosNodeXOf - strMessage:"+strMessage);
		if (strMessage.contains(FTMessage.SEPARATOR)){
			ret = strMessage.substring(strMessage.indexOf(SEPARATOR)+1).trim();	
			Debug.logFT("FTClient - getResponsePosNodeXOf - posNode:"+ret+"<|");
		}
		return ret;
	}

	public static String queryNewPosNodeXChild(String idNodeXQuery) {
		return idNodeXQuery+IDNODEX_SEPARATOR+WHAT_YOUR_NEW_POS_NODEX_CHILD;
	}

	public static String getResponseNewPosNodeXChildOf(FTClient ftClient) {
		String ret = NodeX.POS_NODEX_ANCESTRAL;
		byte[] message = ftClient.read();
		while ( (message != null) && ret.equals(NodeX.POS_NODEX_ANCESTRAL) ) {
			String strMessage = new String(message);
			if (strMessage.contains(FTMessage.MY_NEW_POS_NODEX_CHILD)){
				Debug.logFT("FTClient - getResponseNewPosNodeXChildOf - strMessage:"+strMessage);
				ret = strMessage.substring(strMessage.indexOf(SEPARATOR)+1).trim();	
			}
			message = ftClient.read();
		}
		return ret;
	}

	public static String responseMyNewPosNodeXChild(NodeX parentNodeX, NodeX childNodeX) {
		return MY_NEW_POS_NODEX_CHILD+SEPARATOR+parentNodeX.getNewPosNodeXChild(childNodeX); 	
	}

	public static String getIdNodeXOfMessage(String strMessage) {
		String ret = null;
		if (strMessage.contains(IDNODEX_SEPARATOR)){
			ret = strMessage.substring(0, strMessage.indexOf(IDNODEX_SEPARATOR)).trim();	
		}
		return ret;
	}

//	public static String notifyNewPosNodeXToChild(String idNodeX, String posNodeX) {
//		return idNodeX+IDNODEX_SEPARATOR+NOTIFY_NEW_POS_NODEX+SEPARATOR+posNodeX; 	
//	}
//
//	public static String getNewPosNodeXParent(String strMessage, NodeX nodeX) {
//		String ret = null;
//		if (strMessage.contains(NOTIFY_NEW_POS_NODEX)){
//			String idNodeXParent = getIdNodeXOfMessage(strMessage);
//			if (idNodeXParent != null && idNodeXParent.equals(nodeX.getParent().getIdNodeX())){
//				ret = strMessage.substring(strMessage.indexOf(SEPARATOR)+1).trim();
//			}
//		}
//		return ret;
//	}
//
//	public static String responseYouArentMyParent(NodeX nodeX) {
//		return nodeX.getIdNodeX()+IDNODEX_SEPARATOR+YOU_ARENT_MY_PARENT; 	
//	}

	public static String getPosNodeXOfQuery(String strMessage) {
		String ret = null;
		if (strMessage.contains(FTMessage.SEPARATOR)){
			ret = strMessage.substring(strMessage.indexOf(FTMessage.SEPARATOR)+1).trim();
		}
		return ret;
	}

	public static String notifyLostParentNodeXToChild(String idNodeX) {
		return idNodeX+IDNODEX_SEPARATOR+NOTIFY_LOST_PARENT_NODEX;
	}

//	public static boolean getLostParentNodeX(String strMessage, NodeX nodeX) {
//		boolean ret = false;
//		if (strMessage.contains(NOTIFY_LOST_PARENT_NODEX)){
//			String idNodeXParent = getIdNodeXOfMessage(strMessage);
//			if (idNodeXParent != null && idNodeXParent.equals(nodeX.getParent().getIdNodeX())){
//				ret = true;
//			}
//		}
//		return ret;
//	}
}
