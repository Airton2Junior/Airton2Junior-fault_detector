package aal.fd.entity;

import java.util.ArrayList;
import java.util.HashMap;

import aal.fd.net.FDClient;
import aal.fd.net.FDMessage;
import aal.net.Network;
import aal.util.Algoritm;
import aal.util.Debug;
import aal.util.Params;

public class Pi extends Px {

	private static final Integer IN_ZERO = new Integer(0);

	private PxList recFromPi = new PxList();

	private PxList knowPi = new PxList();

	private PxList queriedPi = new PxList();

	private Integer round = IN_ZERO;
	
	private Integer unRound = IN_ZERO;
	
	private int algoritm = Algoritm.getAlgoritm();

	public Pi(String idPi) {
		super(idPi);
		// no inicio o knowPi possui apenas o proprio Pi
		resetKnowPi();
	}

	public Integer getRound() {
		return this.round;
	}

	public Integer getUnRound() {
		return this.unRound;
	}

	private Integer getProxRoundPx(Integer roundPx) {
		return new Integer(roundPx.intValue() + 1);
	}

	private Integer getProxRound() {
		return getProxRoundPx(this.round);
	}

	private Integer getProxUnRound() {
		return getProxRoundPx(this.unRound);
	}

	private void incRound() {
		this.round = getProxRound();
	}

	private void incUnRound() {
		this.unRound = getProxUnRound();
	}
	
	private void resetUnRound() {
		this.unRound = IN_ZERO;
	}

	private int fi() {
		// fi (qtde máxima de Px FALTOSOS: (|knowPi|/2)-1)
		int fi = Math.round((float) this.di() / 2) - 1;
		return (fi > 0) ? fi : 0;
	}

	private int di() {
		// di (qtde de Px na vizinhanca de Pi: |knowPi|)
		return this.knowPi.size();
	}

	private int alfai() {
		// alfai (qtde mínima de Px RESPONDENTES: |di - fi| )
		int alfai = (this.di() - this.fi());
		return (alfai > 0) ? alfai : 0;
	}

	public void addKnowPi(Px pj) {
		this.knowPi.put(pj, getRound());
	}

	private void resetKnowPi() {
		this.knowPi.clear();
		addKnowPi(this);
		Debug.logFD("T1 - Adicionando " + this + " em KnowPi:" + this.getKnowPi());
	}

	public PxList getKnowPi() {
		return this.knowPi;
	}

	private void removeKnowPi(Px pj) {
		this.knowPi.remove(pj);
	}

	private int qtRecFromPi() {
		return this.recFromPi.size();
	}

	private PxList getRecFromPi() {
		return this.recFromPi;
	}

	private void addRecFromPi(Px pj) {
		this.recFromPi.put(pj, getRound());
	}

	private void resetRecFromPi() {
		this.recFromPi.clear();
	}

	private PxList getQueriedPi() {
		return this.queriedPi;
	}

	private void addQueriedPi(Px pj) {
		this.queriedPi.put(pj, getRound());
	}

	private void resetQueriedPi() {
		this.queriedPi.clear();
	}

	// Task 1
	// query about suspicions and mistakes
	public void broadcastQueryAndWaitAlfaiResponse() {
		Debug.logFD("T1 - NEW_BROADCAST_QUERY");
		
		// recuperando o comparador para novo broadcast
		this.algoritm = Algoritm.getAlgoritm();
		Debug.logFD("T1 - ALGORITM:"+this.algoritm+"<|");

		// levantando a lista de vizinhos
		ArrayList<Px> pxArray = Network.getNeighborsWithMe();
		// reiniciando lista de response recebidos (rec_from)
		this.resetRecFromPi();
		Debug.logFD("T1 - Reset RecFromPi:" + this.getRecFromPi());
		// reiniciando lista de queried (queriedPi)
		this.resetQueriedPi();
		Debug.logFD("T1 - Reset QueriedPi:" + this.getQueriedPi());
		Debug.logFD("T1 - Broadcast Query - Round:" + getRound());
		HashMap<Px, FDClient> hMapClient = new HashMap<Px, FDClient>();
		// publicando query em cada vizinho Px
		for (Px px : pxArray) {
			Debug.logFD("T1 - Query:" + px.getIdPx() + " - Round:" + getRound());
			// Task 1
			// query about suspicions and mistakes
			// publicando query em Px
			try {
				FDMessage fdMessage = new FDMessage(this.getIdPx(), this.getSuspPx(), this.getMistPx());
				Debug.logFD("T1 - getLSPClient:" + px.getIdPx() + " - Round:" + getRound());
				FDClient client = new FDClient(Network.decodeMyIdPx(this.getIdPx(), px.getIdPx()));
				if (client != null) {
					//armazenando a conexao
					hMapClient.put(px, client);
					//enviando a mensagem de query e recuperando o nroSeq da mensagem
					Debug.logFD("T1 -------------- WRITE MESSAGE:" + fdMessage.toString() + " - Round:" + getRound());
					Debug.logFD("T1 - WAIT DELAY for ACK WRITE of:"+px.getIdPx());
					short nroSeq = client.write(fdMessage.toString().getBytes());
					// adicionando em queriedPi os confirmados para recuperar response...
					if (client.isPackAckedByNroSeq(nroSeq)){
						this.addQueriedPi(px);
						Debug.logFD("T1 - Adicionando " + px + " em QueriedPi");				
					}
				}			
			} catch (Exception e) {
				Debug.logException(e);
			}
		}		
		Debug.logFD("T1 - QueriedPi:" + this.getQueriedPi());
		//Recuperando response de vizinhos inquiridos
		Debug.logFD("T1 - Wait for least alfa(i) responses - Round:" + getRound());
		int tentativa = 0;
		PxList pxListResp = PxList.minus(this.getQueriedPi(), this.getRecFromPi());
		Debug.logFD("T1 - Lista a recuperar response:" + pxListResp + " this.qtResponseLastQuery():"
				+ this.qtRecFromPi() + " < this.alfai():" + this.alfai() + "="
				+ (this.qtRecFromPi() < this.alfai()) + " - Round:" + getRound());
		while ((pxListResp.size() > 0) && (this.qtRecFromPi() < this.alfai()) && (tentativa < Params.MAX_TENTATIVAS)) {
			tentativa++;
			Debug.logFD("T1 - GET_RESPONSE (tentativa:" + tentativa + ")");
			for (Px px : pxListResp.keySet()) {
				FDClient client = hMapClient.get(px);
				if (client != null) {
					byte[] payload = client.read();
					if (payload != null) {
						String strMessage = new String(payload);
						strMessage = strMessage.trim();
						Debug.logFD("T1 - GET RESPONSE FROM:" + px.getIdPx() + " |>" + strMessage + "<| - Round:"
								+ getRound());
						FDMessage fdMessage = new FDMessage(strMessage);
						px.setSuspPx(fdMessage.getSuspPx());
						px.setMistPx(fdMessage.getMistPx());
						this.addRecFromPi(px);
						Debug.logFD("T1 - Adicionando " + px + " em RecFromPi:" + this.getRecFromPi());
					}
				}
			}
			pxListResp = PxList.minus(this.getQueriedPi(), this.getRecFromPi());
			Debug.logFD("T1 - Lista a recuperar response:" + pxListResp + " this.qtResponseLastQuery():"
					+ this.qtRecFromPi() + " < this.alfai():" + this.alfai() + "="
					+ (this.qtRecFromPi() < this.alfai()) + " - Round:" + getRound());
		}
		// houve a qtde de responses necessarios (alfai), INCREMENTO DE ROUND
		if (this.qtRecFromPi() >= this.alfai()) {
			// incrementando round de Pi
			this.incRound();
		} else {
			//incrementando unRound de Pi
			this.incUnRound();
		}
		//desmobilizando os clientes
		for (Px px : hMapClient.keySet()){
			FDClient client = hMapClient.get(px);
			client.close();
		}
	}


	// Task 1
	public void detectNewSuspicions() {
		Debug.logFD("T1 - Detect New Suspicions! KNOWPI " + this.getKnowPi() + " MINUS RECFROMPI " + this.getRecFromPi()
				+ " - Round:" + getRound());
		PxList newSuspPx = PxList.minus(this.getKnowPi(), this.getRecFromPi());
		Debug.logFD("T1 - KNOWPI MINUS RECFROMPI " + newSuspPx + " - Round:" + getRound());
		for (Px px : newSuspPx.keySet()) {
//			if (!px.equals(this) && !this.getSuspPx().contains(px)) {
			if (!this.getSuspPx().contains(px)) {
				Debug.logFD("T1 - Detect New Suspicions! - Round:" + getRound());
				if (this.getMistPx().contains(px)) {
					Debug.logFD("T1 - EXISTS A MISTAKE! " + px + " - Round:" + getRound());
					Integer proxRoundPx = getProxRoundPx(this.getMistPx().get(px));
					this.getSuspPx().put(px, proxRoundPx);
					Debug.logFD("T1 - ADD A SUSPECT! " + px + " - WITH Round:" + proxRoundPx + " - Round:" + getRound());
					this.getMistPx().remove(px);
					Debug.logFD("T1 - REMOVE A MISTAKE! " + px + " - Round:" + getRound());
				} else {
					this.getSuspPx().put(px, Pi.IN_ZERO);
					Debug.logFD("T1 - ADD A SUSPECT! " + px + " WITH Round: "+Pi.IN_ZERO+" - Round:" + getRound());
				}
			}
		}
	}

	//	Task 2
	public void propagatingSuspicions(Px pj) {
		PxList suspPj = pj.getSuspPx();
		for (Px px : suspPj.keySet()) {
			Integer roundPx = suspPj.get(px);
			//comportamento original
			if (this.algoritm == Params.ALGORITM_ONE){
				if (isMostRecentInformationAboutPxStatus(px, roundPx, Params.ZERO)) {
					Debug.logFD("T2 - Propagating Suspicions! - Round:" + getRound());
					if (px.equals(this)) {
						Debug.logFD("T2 - IT'S MEEEE! " + px + " - Round:" + getRound());
						Integer proxRoundPx = getProxRoundPx(roundPx);
						this.getMistPx().put(this, proxRoundPx);
						Debug.logFD("T2 - ADD A MISTAKE! " + px + " - WITH Round:" + proxRoundPx);
					} else {
						Debug.logFD("T2 - IT'S NOT ME! " + px + " - Round:" + getRound());
						this.getSuspPx().put(px, roundPx);
						Debug.logFD("T2 - ADD A SUSPECT! " + px + " - WITH Round:" + roundPx);
						this.getMistPx().remove(px);
						Debug.logFD("T2 - REMOVE A MISTAKE! " + px + " - Round:" + getRound());
					}
				}
			} else {
			//comportamento modificado	
				Debug.logFD("T2 - Propagating Suspicions! - Round:" + getRound());
				if (px.equals(this)) {
					Debug.logFD("T2 - IT'S MEEEE! " + px + " - Round:" + getRound());
					Integer proxRoundPx = getProxRoundPx(roundPx);
					this.getMistPx().put(this, proxRoundPx);
					Debug.logFD("T2 - ADD A MISTAKE MEEEE! " + px + " - WITH Round:" + proxRoundPx);
					this.getSuspPx().remove(px);
					Debug.logFD("T2 - REMOVE A SUSPECT MEEEE! " + px + " - Round:" + getRound());
				} else if (isMostRecentInformationAboutPxStatus(px, roundPx, Params.ZERO)) {
					Debug.logFD("T2 - IT'S NOT ME! " + px + " - Round:" + getRound());
					this.getSuspPx().put(px, roundPx);
					Debug.logFD("T2 - ADD A SUSPECT! " + px + " - WITH Round:" + roundPx);
					this.getMistPx().remove(px);
					Debug.logFD("T2 - REMOVE A MISTAKE! " + px + " - Round:" + getRound());
				}
			}
		}
	}

	// Task 2
	private boolean isMostRecentInformationAboutPxStatus(Px px, Integer roundPx, int comparator) {
		boolean ret = false;
		PxList suspPiUnionMistPi = PxList.union(this.getSuspPx(), this.getMistPx());
		if (suspPiUnionMistPi.contains(px)){
			Integer roundPi = suspPiUnionMistPi.get(px);
			if (roundPi.compareTo(roundPx) < comparator){
				Debug.logFD("T2 - isMostRecentInformationAboutPxStatus! roundPi:"+roundPi+" "+(comparator==0?"<":"<=")+" roundPx:"+roundPx+" - Round:" + getRound());
				ret = true;
			}
		}else{
			Debug.logFD("T2 - isMostRecentInformationAboutPxStatus! UNION NO CONTAINS! - Round:" + getRound());
			ret = true;
		}
		return ret;
	}

	// Task 2
	public void propagatingMistakes(Px pj) {
		PxList mistPj = pj.getMistPx();
		for (Px px : mistPj.keySet()) {
			Integer roundPx = mistPj.get(px);
			if (isMostRecentInformationAboutPxStatus(px, roundPx, Params.ZERO)) {
				Debug.logFD("T2 - Propagating Mistakes! - Round:" + getRound());
				this.getMistPx().put(px, roundPx);
				Debug.logFD("T2 - ADD A MISTAKE! " + px + " - WITH Round:" + roundPx);
				this.getSuspPx().remove(px);
				Debug.logFD("T2 - REMOVE A SUSPECT! " + px + " - Round:" + getRound());
				if (!px.equals(pj)) {
					Debug.logFD("T2 - PX IS NOT A PJ:" + pj + "! - Round:" + getRound());
					this.removeKnowPi(px);
					Debug.logFD("T2 - REMOVE PX THE KNOWPI! " + px + " - Round:" + getRound());
				}
			}
		}
	}

	public boolean hasParentInSuspPx(String idNodeX) {
		Px px = new Px(idNodeX);
		return ((this.getSuspPx() != null) && (this.getSuspPx().size() > 0) && this.getSuspPx().contains(px));
	}
	
	public String toString(){
		return "[IdPx:"+this.getIdPx()+"|SuspPx:"+this.getSuspPx()+"|MistPx:"+this.getMistPx()+"|Round:"+this.getRound()+"|unRound:"+this.getUnRound()+"]";
	}

	public void evaluateUnRoundToResetKnowPi() {
		if (this.getUnRound().intValue() > Params.MAX_TENTATIVAS){
			Debug.logFD("T1 - this.getUnRound().intValue() > Params.MAX_TENTATIVAS - unRound:"+this.getUnRound());
			//reset knowPi to me
			this.resetKnowPi();
			Debug.logFD("T1 - ResetKnowPi");
			this.resetUnRound();
			Debug.logFD("T1 - ResetUnRound");
		}		
	}

}
