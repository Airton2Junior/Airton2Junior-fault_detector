package aal.net.udpc;

import java.io.IOException;

import aal.util.Debug;

public abstract class UdpcProcess {
	
	public static void UdpcProcessPack(UdpcProxy udpcProxy, UdpcService udpcService) throws IOException, InterruptedException {
		UdpcPack pack = udpcProxy.recvUdpcPack();
		Debug.logUdpc("UdpcProcess - Mensagem recebida:"+pack);
		//read (server) DATA
		if (pack.getMessId() == UdpcPack.DATA) {
			//store in recv buffer
			udpcService.getUdpcRecvBuffer().add(pack);
			Debug.logUdpc("UdpcProcess - DATA - STORE in Recv Buffer:"+pack);
			//send ACK of READ
			udpcProxy.sendUdpcPackAck(pack);					
			Debug.logUdpc("UdpcProcess - SEND ACK of READ:"+pack);
		} else if (pack.getMessId() == UdpcPack.ACK) {
			//ACK of WRITE
			if (udpcService.getUdpcSentBuffer().contains(pack)){
				//remove of sent buffer
				UdpcPack dataPack = udpcService.getUdpcSentBuffer().remove(pack);
				Debug.logUdpc("UdpcProcess - ACK of WRITE - REMOVE of Sent Buffer:"+dataPack);
				//store in ackWrite buffer 
				udpcService.getUdpcAckWBuffer().add(dataPack);
				Debug.logUdpc("UdpcProcess - ACK of WRITE - STORE in AckW Buffer:"+dataPack);
				//send ACK of ACK READ
				udpcProxy.sendUdpcPackAck(dataPack);	
				Debug.logUdpc("UdpcProcess - ACK of WRITE - SEND Ack Buffer:"+dataPack);
			}
			//ACK of ACK READ
			if (udpcService.getUdpcRecvBuffer().contains(pack)){
				//remove of recv buffer
				UdpcPack dataPack = udpcService.getUdpcRecvBuffer().remove(pack);
				Debug.logUdpc("UdpcProcess - ACK of READ - REMOVE of Recv Buffer:"+dataPack);
				//store in buffer for read
				udpcService.getUdpcDataBuffer().add(dataPack);
				Debug.logUdpc("UdpcProcess - ACK of READ - STORE in Data Buffer:"+dataPack);
			}
		}
	}

}
