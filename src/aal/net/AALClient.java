package aal.net;

import aal.net.udpc.UdpcClient;

public class AALClient {
	private UdpcClient udpcClient;

	public AALClient(String serverIdPx, int port) {
		this.udpcClient = new UdpcClient(serverIdPx, port);
	}

	public short write(byte[] payload) {
		short nroSeq = this.udpcClient.write(payload);
		Network.waitDelayUDPC();
		return nroSeq;
	}

	public byte[] read() {
		return udpcClient.read();
	}

	public void close(){
		this.udpcClient.close();
	}

	public boolean isPackAckedByNroSeq(short nroSeq) {
		return this.udpcClient.isPackAckedByNroSeq(nroSeq);
	}
	
}
