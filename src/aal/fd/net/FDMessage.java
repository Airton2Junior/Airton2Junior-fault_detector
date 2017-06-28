package aal.fd.net;

import java.util.StringTokenizer;

import aal.fd.entity.Px;
import aal.fd.entity.PxList;
import aal.util.Debug;

public class FDMessage {

	private final String IDPX_SEPARATOR = "%";
	private final String LIST_SEPARATOR = "$";
	private final String PAIR_SEPARATOR = "|";
	private final String ITEM_SEPARATOR = ";";
	private final String EMPTY_LIST = "#";

	private String senderIdPx;
	private PxList suspPx;
	private PxList mistPx;

	public FDMessage(String senderIdPx, PxList suspPx, PxList mistPx) {
		this.senderIdPx = senderIdPx;
		this.suspPx = suspPx;
		this.mistPx = mistPx;
	}

	public FDMessage(String message) {
		this.listsFromString(message);
	}

	public PxList getSuspPx() {
		return suspPx;
	}

	public void setSuspPx(PxList suspPx) {
		this.suspPx = suspPx;
	}

	public PxList getMistPx() {
		return mistPx;
	}

	public void setMistPx(PxList mistPx) {
		this.mistPx = mistPx;
	}

	public String getSenderIdPx() {
		return this.senderIdPx;
	}

	// String (IdPx%SuspPx$MistPx):
	// ip%px;round|px;round|...px;round|$px;round|px;round|...px;round|
	@Override
	public String toString() {
		String strFDMessage = this.senderIdPx + IDPX_SEPARATOR;
		// Convert List suspPx to String
		if (this.suspPx.size() > 0 ){
			for (Px px : this.suspPx.keySet()) {
				Integer round = this.suspPx.get(px);
				strFDMessage += px.getIdPx() + ITEM_SEPARATOR + round + PAIR_SEPARATOR;
			}
		}else{
			strFDMessage += EMPTY_LIST;
		}
		// Divisor de listas $
		strFDMessage += LIST_SEPARATOR;
		// Convert List mistPx to String
		if (this.mistPx.size() > 0 ){
			for (Px px : this.mistPx.keySet()) {
				Integer round = this.mistPx.get(px);
				strFDMessage += px.getIdPx() + ITEM_SEPARATOR + round + PAIR_SEPARATOR;
			}
		}else{
			strFDMessage += EMPTY_LIST;
		}
		Debug.logFD("FDMessage - Transformando listas SuspPx e MistPx em String: |>"+strFDMessage+"<|");
		return strFDMessage;
	}

	private void listsFromString(String message) {
		Debug.logFD("FDMessage - Recuperando listas de mensagem a partir de String: |>" + message + "<|");
		int idxIdPx = message.indexOf(IDPX_SEPARATOR);
		if (idxIdPx > 0) {
			this.senderIdPx = message.substring(0, idxIdPx);
			message = message.substring(idxIdPx + 1);
//			Debug.logFD("FDMessage - SuspPx e MistPx em String: |>"+message+"<|");
			// Split de listas
			if ( message.contains(LIST_SEPARATOR) ){
//				Debug.logFD("FDMessage - message.contains(LIST_SEPARATOR)");
				StringTokenizer stListas = new StringTokenizer(message, LIST_SEPARATOR);
				String[] listas = new String[2];
				for (int idxListas = 0; idxListas < 2; idxListas++) {
					if (stListas.hasMoreElements()) {
						listas[idxListas] = stListas.nextElement().toString();
					} else {
						listas[idxListas] = EMPTY_LIST;
					}
				}
				Debug.logFD("FDMessage - senderIdPx: |>" + this.senderIdPx + "<|");
				// Convert String (px;round|...px;round|) into List SuspPx
				Debug.logFD("FDMessage - strLista SuspPx: |>" + listas[0] + "<|");
				this.setSuspPx(listFromString(listas[0]));
				// Convert String (px;round|...px;round|) into List MistPx
				Debug.logFD("FDMessage - strLista MistPx: |>" + listas[1] + "<|");
				this.setMistPx(listFromString(listas[1]));
			}
		}
	}

	private PxList listFromString(String strPares) {
		PxList list = new PxList();
		if (!strPares.equals(EMPTY_LIST)){
			if (strPares.contains(PAIR_SEPARATOR)){
//				Debug.logFD("FDMessage - strPares.contains(PAIR_SEPARATOR)");
				StringTokenizer stPares = new StringTokenizer(strPares, PAIR_SEPARATOR);
				while (stPares.hasMoreElements()) {
					String par = stPares.nextElement().toString();
					if (par.contains(ITEM_SEPARATOR)){
//						Debug.logFD("FDMessage - par.contains(ITEM_SEPARATOR)");
						StringTokenizer stItens = new StringTokenizer(par, ITEM_SEPARATOR);
						String[] itens = new String[2];
						for (int idxItens = 0; idxItens < 2; idxItens++) {
							itens[idxItens] = stItens.nextElement().toString();
						}
						Px px = new Px(itens[0]);
						Integer round = Integer.parseInt(itens[1]);
						list.put(px, round);
					}
				}
			}
		}
		return list;
	}

}
