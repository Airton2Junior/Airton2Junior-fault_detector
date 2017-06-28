package aal.efd.entity;

import aal.fd.entity.Px;

public class SnapshotEFDStatus {
	
	private Px px;
	private int qtdeSimilar = 0;
	private int qtdeElements = 0;
	private int qtdeMistakes = 0;
	private int round = 0;
	
	public int getQtdeMistakes() {
		return qtdeMistakes;
	}

	public void setQtdeMistakes(int qtdeMistakes) {
		this.qtdeMistakes = qtdeMistakes;
	}

	public SnapshotEFDStatus(Px px){
		this.px = px;
	}
	
	public void setQtdeSimilar(int contSimilar) {
		this.qtdeSimilar = contSimilar;		
	}

	public void setQtdeElements(int contElements) {
		this.qtdeElements = contElements;		
	}
	
	public void setRound(int round) {
		this.round = round;		
	}
	
	public int getRound(){
		return this.round;
	}

	public int percConsensus(){
		int ret = 0;
		if (this.qtdeElements > 0)
			ret = Math.round((float) (((float)this.qtdeSimilar/this.qtdeElements)*100));
		return ret;
	}
	
	public int percMistakes(){
		int ret = 0;
		if (this.qtdeElements > 0)
			ret = Math.round((float) (((float)this.qtdeMistakes/this.qtdeElements)*100));
		return ret;
	}

	@Override
	public String toString() {
		return "[Px:"+px.getIdPx()+"|qtdeElements:"+qtdeElements+"|qtdeSimilar:"+qtdeSimilar+"|percConsensus:"+percConsensus()+"|qtdeMistakes:"+qtdeMistakes+"|percMistakes:"+percMistakes()+"|round:"+round+"]";
	}
	
	public static String toStringHeader() {
		return "Px;qtdeElements;qtdeSimilar;percConsensus;qtdeMistakes;percMistakes;round";
	}

	public String toStringLine() {
		return px.getIdPx()+";"+qtdeElements+";"+qtdeSimilar+";"+percConsensus()+";"+qtdeMistakes+";"+percMistakes()+";"+round;
	}
}
