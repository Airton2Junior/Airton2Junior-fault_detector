package aal.efd.logic;


import aal.efd.entity.EFDStatus;
import aal.efd.entity.SnapshotEFDStatus;
import aal.efd.net.EFDClient;
import aal.efd.net.EFDMessage;
import aal.efd.net.EFDServer;
import aal.fd.entity.Pi;
import aal.fd.entity.Px;
import aal.ft.entity.NodeX;
import aal.net.Network;
import aal.net.udpc.UdpcPack;
import aal.util.Debug;
import aal.util.Params;

public class AALEvaluationFaultDetector {

	// Task T1
	public class PropagateFDStatus extends Thread {

		private EFDStatus efdStatus;

		public PropagateFDStatus(EFDStatus efdStatus) {
			this.efdStatus = efdStatus;
		}

		@Override
		public void run() {
			while (true) {
				EFDClient fdAgent = null;
				try {
					NodeX parent = this.efdStatus.getParent();
					if (parent != null){
						fdAgent = new EFDClient(parent.getIdNodeX());
						Pi pi = this.efdStatus.getPi();
						EFDMessage efdMessage = new EFDMessage( pi.getIdPx(), pi.getSuspPx(), pi.getMistPx() );
						byte[] payload = efdMessage.toString().getBytes();
						Debug.logEFD("-------------------------------------------------------------------------");
						fdAgent.write(payload);
						Debug.logEFD("PropagateFDStatus - Send efdMessage to Sensor("+parent.getIdNodeX()+"):"+efdMessage.toString() +" - WAIT DELAY for ACK WRITE/READ:" + parent.getIdNodeX());
						if (this.efdStatus.hasNodeXChildren()){
							try{
								for (NodeX nodeX : efdStatus.getNodeXChildList()) {
									Px px = efdStatus.getStatusNodeXChild(nodeX);
									if (px != null){
										EFDMessage efdMessageChild = new EFDMessage(px.getIdPx(), px.getSuspPx(), px.getMistPx());
										payload = efdMessageChild.toString().getBytes();
										Debug.logEFD("PropagateFDStatus - Send efdMessageChild to Sensor("+parent.getIdNodeX()+"):"+efdMessageChild.toString() +" - WAIT DELAY for ACK WRITE/READ:" + parent.getIdNodeX());
										fdAgent.write(payload);
									}
								}
							}catch(Exception e){
								Debug.logException(e);
							}
						}
						Debug.logEFD("-------------------------------------------------------------------------");
					}
					Network.waitDelayEFD("PropagateFDStatus - WAIT DELAY - "+Params.DELAY_PROPAGATE_FD_STATUS+" ms !", Params.DELAY_PROPAGATE_FD_STATUS);
				} catch (Exception e) {
					Debug.logException(e);
				} finally {
					if (fdAgent != null) fdAgent.close();
				}
			}
		}
	}

	// Task T2
	public class SensorFDStatus extends Thread {

		private EFDStatus efdStatus;

		public SensorFDStatus(EFDStatus efdStatus) {
			this.efdStatus = efdStatus;
		}

		@Override
		public void run() {
			EFDServer fdSensor = new EFDServer();
			Debug.logEFD("SensorFDStatus - STARTED! ");
			while (true) {
				UdpcPack pack = fdSensor.read();
				String messageStatus = new String(pack.getPayload());
				messageStatus = messageStatus.trim();
				Debug.logEFD("##########################################################################");
				Debug.logEFD("SensorFDStatus - MENSAGEM RECEBIDA! |>" + messageStatus + "<| ");
				Debug.logEFD("##########################################################################");
				EFDMessage efdMessage = new EFDMessage(messageStatus);
				this.efdStatus.setStatusNodeXChild(efdMessage);
			}
		}

	}

	// Task T3
	public class EvaluateFDStatus extends Thread {

		private EFDStatus efdStatus;

		public EvaluateFDStatus(EFDStatus efdStatus) {
			this.efdStatus = efdStatus;
		}

		@Override
		public void run() {
			Debug.logEFDFile(SnapshotEFDStatus.toStringHeader());
			while (true) {
				Debug.logEFD("/////////////////////////////////////////////////////////////////////////");
				Debug.logEFD("EvaluateFDStatus - StatusOfParentNodeX("+this.efdStatus.getNodeX().getIdNodeX()+"):"+this.efdStatus.getNodeX().getPi());
				if (this.efdStatus.hasNodeXChildren()){
					for (NodeX nodeX : efdStatus.getNodeXChildList()) {
						Px px = efdStatus.getStatusNodeXChild(nodeX);
						Debug.logEFD("EvaluateFDStatus - StatusOfChildNodeX("+nodeX.getIdNodeX()+"):"+px);
					}
				}
				SnapshotEFDStatus resEFDStatus = efdStatus.takeSnapshotEFDStatus();
				Debug.logEFD(resEFDStatus.toString());
				Debug.logEFDFile(resEFDStatus.toStringLine());
				Debug.logEFD("/////////////////////////////////////////////////////////////////////////");
				Debug.logEFD("EvaluateFDStatus - resetStatusNodeXChild");
				this.efdStatus.resetStatusNodeXChildren();
				Network.waitDelayEFD("EvaluateFDStatus - WAIT DELAY - "+Params.DELAY_EVALUATE_FD_STATUS+" ms !", Params.DELAY_EVALUATE_FD_STATUS);
			}
		}

	}

}
