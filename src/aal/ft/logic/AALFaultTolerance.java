package aal.ft.logic;

import aal.efd.entity.EFDStatus;
import aal.efd.logic.AALEvaluationFaultDetector;
import aal.efd.logic.AALEvaluationFaultDetector.EvaluateFDStatus;
import aal.efd.logic.AALEvaluationFaultDetector.PropagateFDStatus;
import aal.efd.logic.AALEvaluationFaultDetector.SensorFDStatus;
import aal.fd.entity.Pi;
import aal.fd.logic.AALFaultDetector;
import aal.fd.logic.AALFaultDetector.GeneratingSuspicions;
import aal.fd.logic.AALFaultDetector.PropagatingSuspicionsAndMistakes;
import aal.ft.entity.MQTTServer;
import aal.ft.entity.NodeX;
import aal.ft.net.FTMessage;
import aal.ft.net.FTServer;
import aal.net.Network;
import aal.net.udpc.UdpcPack;
import aal.util.Debug;
import aal.util.Params;

public class AALFaultTolerance {

	public static void main(String[] args) {
		
		try{
			
			Pi pi = new Pi(Network.getMyIP());
			NodeX nodeX = new NodeX(pi);
			
			AALFaultTolerance ft = new AALFaultTolerance();
			AALFaultDetector fd = new AALFaultDetector();
			AALEvaluationFaultDetector efd = new AALEvaluationFaultDetector();

			PropagatingSuspicionsAndMistakes propSuspAndMist = fd.new PropagatingSuspicionsAndMistakes(pi);
			propSuspAndMist.start();
			Debug.logFT("FD Task T2 - Started");

			GeneratingSuspicions genSusp = fd.new GeneratingSuspicions(pi);
			genSusp.start();		
			Debug.logFT("FD Task T1 - Started");
			
			FaultToleranceServer ftServer = ft.new FaultToleranceServer(nodeX);
			ftServer.start();
			Debug.logFT("FT Server - Started");
			
			FaultToleranceClient ftClient = ft.new FaultToleranceClient(nodeX);
			ftClient.start();
			Debug.logFT("FT Client - Started");

			EFDStatus evalFDAgent = new EFDStatus(nodeX);
			
			SensorFDStatus sensorFDStatus = efd.new SensorFDStatus(evalFDAgent);
			sensorFDStatus.start();
			Debug.logFT("EFD SensorFDStatus - Started");

			PropagateFDStatus propFDStatus = efd.new PropagateFDStatus(evalFDAgent);
			propFDStatus.start();
			Debug.logFT("EFD PropagateFDStatus - Started");

			EvaluateFDStatus evalFDStatus = efd.new EvaluateFDStatus(evalFDAgent);
			evalFDStatus.start();
			Debug.logFT("EFD EvaluateFDStatus - Started");

		}catch (NullPointerException e){
			Debug.logException(e);
		}

		
	}
	
	class FaultToleranceClient extends Thread {

		private NodeX nodeX;

		public FaultToleranceClient(NodeX nodeX) {
			this.nodeX = nodeX;
			MQTTServer.updateMQTTServer(nodeX);
		}

		@Override
		public void run() {
			Debug.logFT("FTClient - NodeX status:"+this.nodeX);
			while (true) {
				try {
					//se perdeu o pai localiza ascendente
					if (nodeX.hasParentInSuspPx()){
						Debug.logFT("FTClient - I LOST PARENT node, searching ASCENDENT!");
						nodeX.locateNewParentNodeX();					
					}
					//se no ancestral localizar outro!?
					if (nodeX.isNodeAncestral()){
						Debug.logFT("FTClient - I am ANCESTRAL node, searching ANOTHER!");
						nodeX.locateAnotherNodeAncestral();
					}
					Network.waitDelayFT("FTClient - WAIT DELAY ("+Params.DELAY_LOCATE_PARENT+"s) to UPDATE node STATUS:"+this.nodeX, Params.DELAY_LOCATE_PARENT);
					Debug.logFT("-----------------------------------------------------------------");
				} catch (Exception e) {
					Debug.logException(e);
				}
			}
		}

	}	

	class FaultToleranceServer extends Thread {

		private FTServer ftServer;
		private NodeX nodeX;

		public FaultToleranceServer(NodeX nodeX) {
			this.ftServer = new FTServer();
			this.nodeX = nodeX;
		}

		@Override
		public void run() {
			while (true) {
	            // Le proxima mensagem
	            UdpcPack pMessage = ftServer.read();
	            String strMessage = new String(pMessage.getPayload());
	            // Exibe conteudo da mensagem
	            Debug.logFT("FTServer - Message received: "+ strMessage);
	            //processa a mensagem recebida
	            UdpcPack pResponse = processMessage(pMessage);
	            // Devolve, talvez, uma resposta ao cliente
	            if (pResponse != null){
	            	ftServer . write (pResponse);
	            }	
			}
		}

		private UdpcPack processMessage(UdpcPack pMessage) {
			UdpcPack pResponse = null;
			String strMessage = new String(pMessage.getPayload());
			Debug.logFT("FTServer - processMessage:"+strMessage+"<|");
			if (strMessage.contains(FTMessage.YOUR_POS_NODEX_IS)){
				String posNodeX = FTMessage.getPosNodeXOfQuery(strMessage);
				Debug.logFT("FTServer - MyPosNodeX:"+this.nodeX.getPosNodeX()+"<| THEN YOUR_POS_NODEX_IS:"+posNodeX+"?"+this.nodeX.getPosNodeX().equals(posNodeX)+"!<|");
				if (this.nodeX.getPosNodeX().equals(posNodeX)){
					String response = FTMessage.responseYesMyPosNodeXIs( this.nodeX.getPosNodeX() );
					Debug.logFT("FTServer - responseYesMyPosNodeXIs:"+posNodeX+"<| - response:"+response+"<|");
					pResponse = UdpcPack.getUdpcPackDataByUdpcPack(pMessage, response.getBytes());
				}
			} else if (strMessage.contains(FTMessage.WHAT_YOUR_NEW_POS_NODEX_CHILD)){
				String idNodeXMessage = FTMessage.getIdNodeXOfMessage(strMessage);
				NodeX queryNodeX = new NodeX(idNodeXMessage);
				Debug.logFT("FTServer - Receive WHAT_YOUR_NEW_POS_NODEX_CHILD - Query Child: "+idNodeXMessage);
				String response = FTMessage.responseMyNewPosNodeXChild( this.nodeX, queryNodeX );
				pResponse = UdpcPack.getUdpcPackDataByUdpcPack(pMessage, response.getBytes());
			} else if (strMessage.contains(FTMessage.NOTIFY_LOST_PARENT_NODEX) ){
				String idNodeXMessage = FTMessage.getIdNodeXOfMessage(strMessage);
				Debug.logFT("FTServer - Receive NOTIFY_LOST_PARENT_NODEX - Lost Parent: "+idNodeXMessage);
				this.nodeX.setParent(null);
			}
			return pResponse;
		}
	}	
}
