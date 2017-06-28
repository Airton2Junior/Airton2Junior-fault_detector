package aal.efd.entity;

import aal.efd.net.EFDMessage;
import aal.fd.entity.Pi;
import aal.fd.entity.Px;
import aal.fd.entity.PxList;
import aal.ft.entity.NodeX;

public class EFDStatus {
	
	private static final int MAX_PERC_CONSENSUS = 100;
	private NodeX nodeX;
	private Integer round = new Integer(0);
	
	public EFDStatus(NodeX nodeX){
		this.nodeX = nodeX;
	}
	
	private Integer incRound(){
		this.round = new Integer(this.round.intValue()+1);
		return round;
	}
	
	public NodeX getParent() {
		return nodeX.getParent();
	}

	public NodeX getNodeX() {
		return nodeX;
	}

	public Pi getPi() {
		return this.nodeX.getPi();
	}

	public void resetStatusNodeXChildren() {
		this.nodeX.resetStatusNodeXChildren();
	}
	
	public void setStatusNodeXChild(EFDMessage fdAgentMessage) {
		NodeX nodeXChild = new NodeX(fdAgentMessage.getSenderIdPx());
		if (this.nodeX.hasNodeXChild(nodeXChild)){
			Px px = new Px(fdAgentMessage.getSenderIdPx());
			px.setSuspPx(fdAgentMessage.getSuspPx());
			px.setMistPx(fdAgentMessage.getMistPx());
			this.nodeX.setStatusNodeXChild(nodeXChild, px);		
		}else{			
			this.nodeX.notifyLostParentToChild(nodeXChild);
		}		
	}
	
	public boolean hasNodeXChildren(){
		return this.nodeX.hasNodeXChildren();
	}

	public Iterable<NodeX> getNodeXChildList() {
		return this.nodeX.getNodeXChildList();
	}

	public Px getStatusNodeXChild(NodeX nodeX) {
		return this.nodeX.getStatusNodeXChild(nodeX);
	}
	
	public SnapshotEFDStatus takeSnapshotEFDStatus(){
		Integer roundEval = incRound();
		Px parent = this.nodeX.getPi();
		//recuperando o status de consenso
		SnapshotEFDStatus res = takeSnapshotEFDStatus(parent, parent, roundEval);
		//buscando o no com maior consenso
		if (res.percConsensus() < MAX_PERC_CONSENSUS){
			for (NodeX nodeX : this.getNodeXChildList()) {
				Px px = this.getStatusNodeXChild(nodeX);
				if (px != null){
					SnapshotEFDStatus resChild = takeSnapshotEFDStatus(px, parent, roundEval);
					if (resChild.percConsensus() > res.percConsensus()){
						res = resChild;
					}
				}
			}
		}
		return res;
	}
	
	private SnapshotEFDStatus takeSnapshotEFDStatus(Px reference, Px parent, Integer roundEval) {
		SnapshotEFDStatus ret = new SnapshotEFDStatus(reference);
		//lista dos nos avaliados
		PxList pxList = new PxList();
		pxList.put(parent, roundEval);
		if (this.hasNodeXChildren()){
			for (NodeX nodeX : this.getNodeXChildList()) {
				Px px = this.getStatusNodeXChild(nodeX);
				if (px != null)
					pxList.put(px, roundEval);
			}
		}
		//calculando a quantidade de consenso
		int contSimilar = 0;
		for (Px px : pxList.keySet()) {
			if (reference.getSuspPx().isSimilar(px.getSuspPx())){
				contSimilar++;
			}
		}
		//calculando a quantidade de enganos
		int contMistakes = 0;
		for (Px px : pxList.keySet()) {
			if (pxList.isMistake(px.getSuspPx())){
				contMistakes++;
			}
		}
		ret.setQtdeSimilar(contSimilar);
		ret.setQtdeMistakes(contMistakes);
		ret.setQtdeElements(pxList.size());
		ret.setRound(roundEval.intValue());
		return ret;
	}

}
