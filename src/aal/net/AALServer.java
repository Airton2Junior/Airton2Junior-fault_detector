package aal.net;

import aal.net.udpc.UdpcPack;
import aal.net.udpc.UdpcServer;

public class AALServer {
	private UdpcServer udpcServer;

	public AALServer(int port) {
		udpcServer = new UdpcServer(port);
	}

	public void write(UdpcPack p) {
		udpcServer.write(p);
	}

	public UdpcPack read() {
		return udpcServer.read();
	}
	
	public void close(){
		this.udpcServer.close();
	}

}
