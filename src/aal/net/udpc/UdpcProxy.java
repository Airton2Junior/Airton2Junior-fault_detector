package aal.net.udpc;

import java.io.IOException;

public interface UdpcProxy {

	public UdpcPack recvUdpcPack() throws IOException, InterruptedException;

	public void sendUdpcPack(UdpcPack pack) throws InterruptedException, IOException;
	
	public void sendUdpcPackAck(UdpcPack pack) throws InterruptedException, IOException;
	
	public void close();

}
