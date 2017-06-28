package aal.ft.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import aal.fd.entity.Pi;
import aal.fd.entity.Px;
import aal.ft.net.FTClient;
import aal.ft.net.FTMessage;
import aal.net.Network;
import aal.util.Debug;

public class NodeX {
	
	public static final String POS_NODEX_ANCESTRAL = "0";
	
	private Pi pi;
	private NodeX antParent;
	private NodeX parent;
	private String idNodeX;
	private String posNodeX;
	private HashMap<NodeX, Px> hMapChild = new HashMap<NodeX, Px>(); 
	
	public NodeX(String id){
		this.idNodeX = id;
		this.posNodeX = POS_NODEX_ANCESTRAL;
		this.parent = null;
		this.antParent = null;
		this.pi = null;
		this.hMapChild = new HashMap<NodeX, Px>(); 
	}
	
	public NodeX(Pi pi){
		this(pi.getIdPx());
		this.pi = pi;		
	}
	
	public Integer getFDRound(){
		return this.pi.getRound();
	}
	
	public String getIdNodeX(){
		return this.idNodeX;
	}	


	public NodeX getParent() {
		return parent;
	}
	
	public Pi getPi(){
		return this.pi;
	}

	public void setParent(NodeX nParent) {
		this.antParent = this.parent; 
		this.parent = nParent;
		if (nParent == null)
			this.posNodeX = POS_NODEX_ANCESTRAL;
	}

	public NodeX getAntParent() {
		return antParent;
	}

	public String getPosNodeX(){
		return this.posNodeX;
	}	

	public void setPosNodeXParent(String newPosNodeXParent){
		this.posNodeX = newPosNodeXParent + posNodeX.substring(posNodeX.length()-1);
	}

	public void removeChild(NodeX nodeX){
		if (this.hMapChild.containsKey(nodeX)){
			this.hMapChild.remove(nodeX);
			Debug.logFT("FTServer - removeChild - Child:"+nodeX.getIdNodeX());
		}
	}
	
	public String getNewPosNodeXChild(NodeX nodeX) {
		if (!this.hMapChild.containsKey(nodeX))
			this.hMapChild.put(nodeX, null);
		ArrayList<NodeX> arrListChild = hMap2ArrList(this.hMapChild);
		return this.posNodeX+(arrListChild.indexOf(nodeX)+1);		
	}
	
	private ArrayList<NodeX> hMap2ArrList(Map<NodeX, Px> hMap){
		ArrayList<NodeX> arrList = new ArrayList<NodeX>();
		for (NodeX nodeX : hMap.keySet()) {
			arrList.add(nodeX);
		}
		return arrList;
	}
	
	//avalia se ha necessidade de mudanca de parent node
	public boolean hasParentInSuspPx() {
		boolean ret = false;
		if (getParent() != null)
			ret = this.pi.hasParentInSuspPx(getParent().getIdNodeX());
		return ret;
	}

	//busca por novo parent node e atualiza atributo parent
	public void locateNewParentNodeX() {
		NodeX ascendenteNodeX = null;
		while ((this.posNodeX.length() > 1) && (ascendenteNodeX == null)){
			//localizando node ascendente
			this.posNodeX = this.posNodeX.substring(0, this.posNodeX.length()-1);
			Debug.logFT("FTClient - locateNewParentNodeX - search:"+this.posNodeX);
			ascendenteNodeX = locateAscendenteNodeX(this.posNodeX);
		}
		if (this.posNodeX.equals(POS_NODEX_ANCESTRAL)){
			Debug.logFT("FTClient - BECOME POS_NODEX_ANCESTRAL AGAIN! :"+this.posNodeX);
			setParent(null);
		}else{
			this.posNodeX = queryNewPosNodeXChild(ascendenteNodeX);
			Debug.logFT("FTClient - NEW posNodeX:"+this.posNodeX);
			if (!this.posNodeX.equals(POS_NODEX_ANCESTRAL)){
				Debug.logFT("FTClient - ascendenteNodeX:"+ascendenteNodeX);
				setParent(ascendenteNodeX);
			} else {
				Debug.logFT("FTClient - BECOME POS_NODEX_ANCESTRAL AGAIN! :"+this.posNodeX);
				setParent(null);
			}
		}
		propagateLostParentNodeXToChildren();
		//update MQTTServer
		MQTTServer.updateMQTTServer(this);
	}

	private void propagateLostParentNodeXToChildren() {
		try {
			for (NodeX nodeXChild : this.hMapChild.keySet()) {
				notifyLostParentToChild(nodeXChild);
			}	
		} catch (Exception e) {
			Debug.logException(e);
		} finally {
			try {
				this.hMapChild.clear();		
			} catch (Exception e2) {
				Debug.logException(e2);
			}
		}
	}

	private String queryNewPosNodeXChild(NodeX ascendenteNodeX) {
		String ret = POS_NODEX_ANCESTRAL;
		FTClient client = new FTClient(ascendenteNodeX.getIdNodeX());
		if (client != null){
			Debug.logFT("FTClient - queryNewPosNodeXChild to new parent:"+ascendenteNodeX.getIdNodeX());
			String strMessage = FTMessage.queryNewPosNodeXChild(this.getIdNodeX());
			Debug.logFT("FTClient - WAIT DELAY for Response queryNewPosNodeXChild");
			client.write(strMessage.getBytes());
			ret = FTMessage.getResponseNewPosNodeXChildOf(client);
			Debug.logFT("FTClient - queryNewPosNodeXChild - GET RESPONSE - newPosNodeXChild is:"+ret);
			client.close();
		}
		return ret;
	}

	private NodeX locateAscendenteNodeX(String posAscendenteNodeX) {
		NodeX ascendenteNodeX = null;
		if (posAscendenteNodeX.length() > 0){
			String strMessage = FTMessage.queryIfPosNodeXIs(posAscendenteNodeX);
			ArrayList<Px> pxArray = Network.getNeighborsWithoutMe(this.getIdNodeX());
			ArrayList<Px> queriedPxArray = new ArrayList<Px>();
			HashMap<Px, FTClient> hMapClient = new HashMap<Px, FTClient>();
			for (Px px : pxArray) {
				FTClient client = new FTClient(px.getIdPx());
				if (client != null){
					hMapClient.put(px, client);
					Debug.logFT("FTClient - locateAscendenteNodeX ("+posAscendenteNodeX+") - QUERY:" + px.getIdPx());
					Debug.logFT("FTClient - locateAscendenteNodeX ("+posAscendenteNodeX+") - WAIT DELAY for ACK WRITE/READ:" + px.getIdPx());
					short nroSeq = client.write(strMessage.getBytes());
					if (client.isPackAckedByNroSeq(nroSeq)){
						queriedPxArray.add(px);
						Debug.logFT("FTClient - locateAscendenteNodeX ("+posAscendenteNodeX+") - Adicionando " + px.getIdPx() + " em QueriedPxArray");				
					}
				}
			}
			Debug.logFT("FTClient - locateAscendenteNodeX ("+posAscendenteNodeX+") - GET_RESPONSE of QueriedPxArray");
			Iterator<Px> it = queriedPxArray.iterator();
			while (ascendenteNodeX == null && it.hasNext()){
				Px px = it.next();
				FTClient client = hMapClient.get(px);
				Debug.logFT("FTClient - getResponsePosNodeXOf:" + px.getIdPx() + " - Client: "+client);
				if (client != null){
					byte[] message = client.read();
					if (message != null){
						Debug.logFT("FTClient - getResponsePosNodeXOf:" + px.getIdPx() + " - Message: "+message);
						String response = FTMessage.getResponsePosNodeXOf(message);
						Debug.logFT("FTClient - getResponsePosNodeXOf:" + px.getIdPx() + " - Response: "+response);
						if (response.equals(posAscendenteNodeX)){
							ascendenteNodeX = new NodeX(px.getIdPx());
							Debug.logFT("FTClient - Ascendente NodeX ("+posAscendenteNodeX+") FOUND:" + px.getIdPx()+"!");
						}	
					}
				}
			}
			//desmobilizando os clientes
			for (Px px : hMapClient.keySet()){
				FTClient client = hMapClient.get(px);
				client.close();
			}
		}
		return ascendenteNodeX;
	}

	public boolean isNodeAncestral() {
		return getParent() == null;
	}

	public void locateAnotherNodeAncestral() {
		NodeX ancestralNodeX = locateAscendenteNodeX(POS_NODEX_ANCESTRAL);
		if (ancestralNodeX != null && ancestralNodeX.getIdNodeX().compareTo(this.getIdNodeX()) < 0){
			Debug.logFT("FTClient - locateAnotherNodeAncestral - ANCESTRAL FOUND:" + ancestralNodeX.getIdNodeX()+"!");
			this.posNodeX = queryNewPosNodeXChild(ancestralNodeX);
			Debug.logFT("FTClient - locateAnotherNodeAncestral - Query new PosNodeX:" + this.posNodeX );
			if (!this.posNodeX.equals(POS_NODEX_ANCESTRAL)){
				setParent(ancestralNodeX);
				Debug.logFT("FTClient - ascendenteNodeX:"+ancestralNodeX);
			}else{
				Debug.logFT("FTClient - BECOME POS_NODEX_ANCESTRAL AGAIN! :"+this.posNodeX);
				setParent(null);
			}
			//propagate Lost Parent to Children
			propagateLostParentNodeXToChildren();
			//update MQTTServer
			MQTTServer.updateMQTTServer(this);
		}		
	}
	
	
	public boolean changeParent() {
		return ( (this.getAntParent() != this.getParent()) ||
				( (this.getAntParent() == this.getParent()) &&
						(this.getAntParent() == null) ) );
	}

	
	public String toString(){
		String strListChild = "(";
		for (NodeX nodeX : this.hMapChild.keySet()) {
			strListChild += nodeX.getIdNodeX()+";"; 
		}
		strListChild += ")";
		return "[IdNodeX:"+this.idNodeX+"|ParentNodeX:"+(isNodeAncestral()?"null":parent.getIdNodeX())+"|posNodeX:"+this.posNodeX+"|listChild:"+strListChild+"|Pi:"+this.pi+"]";
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NodeX) && (this.getIdNodeX().equals(((NodeX) obj).getIdNodeX()));
	}

	@Override
	public int hashCode() {
		return this.getIdNodeX().hashCode();
	}

	public boolean hasNodeXChildren() {
		return !this.hMapChild.isEmpty();
	}

	public boolean hasNodeXChild(NodeX nodeXChild) {
		return this.hMapChild.containsKey(nodeXChild);
	}
	
	public void setStatusNodeXChild(NodeX nodeXChild, Px px){
		if ((nodeXChild != null) && (this.hMapChild.containsKey(nodeXChild))){
			this.hMapChild.put(nodeXChild, px);
		}
	}

	public Px getStatusNodeXChild(NodeX nodeXChild){
		Px ret = null;
		if ((nodeXChild != null) && (this.hMapChild.containsKey(nodeXChild))){
			ret = this.hMapChild.get(nodeXChild);
		}
		return ret;
	}

	public void notifyLostParentToChild(NodeX nodeXChild) {
		FTClient client = new FTClient(nodeXChild.getIdNodeX());
		if (client != null){
			String strMessage = FTMessage.notifyLostParentNodeXToChild(this.getIdNodeX());
			Debug.logFT("FTClient - propagate LostParent to Child:"+nodeXChild.getIdNodeX()+" - Message:"+strMessage);
			client.write(strMessage.getBytes());
			client.close();
		}
	}

	public Iterable<NodeX> getNodeXChildList() {
		return this.hMapChild.keySet();
	}

	public void resetStatusNodeXChildren() {
		for (NodeX nodeXChild : this.hMapChild.keySet()) {
			this.hMapChild.put(nodeXChild, null);
		}		
	}


	
}
