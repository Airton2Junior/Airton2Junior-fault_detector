package aal.net.udpc;

public interface UdpcService {
	
	public UdpcBuffer getUdpcRecvBuffer();

	public UdpcBuffer getUdpcDataBuffer();

	public UdpcBuffer getUdpcSentBuffer();

	public UdpcBuffer getUdpcAckWBuffer();
	
	public void close();

}
