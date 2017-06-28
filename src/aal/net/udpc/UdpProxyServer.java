package aal.net.udpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import aal.util.Debug;

public class UdpProxyServer implements UdpcProxy {

	protected DatagramSocket ds;

	public UdpProxyServer(int portSrv) throws SocketException {
		this.ds = new DatagramSocket(portSrv);
		this.ds.setSoTimeout(UdpcPack.ZERO);// sem timeout
	}

	public void sendUdpcPack(UdpcPack pack) throws InterruptedException, IOException {
		DatagramPacket udpPack = UdpcPack2Udp.convert2Udp(pack);
		ds.send(udpPack);
		Debug.logUdpc("UdpProxyServer - Mensagem enviada: "+pack);
	}
	
	public void sendUdpcPackAck(UdpcPack pack) throws InterruptedException, IOException {
		this.sendUdpcPack(UdpcPack.getUdpcPackAckByUdpcPack(pack));
	}
	public UdpcPack recvUdpcPack() throws IOException, InterruptedException {
		byte[] buffer = new byte[UdpcPack2Udp.BUFFER_SIZE];
		DatagramPacket udpPack = new DatagramPacket(buffer, buffer.length);
		ds.receive(udpPack);// BLOQUEANTE - para sincronizacao cliente/servidor
		UdpcPack pack = UdpcPack2Udp.convert2UdpcPack(udpPack);
		Debug.logUdpc("UdpProxyServer - Mensagem recebida: "+pack);
		return pack;
	}

	public void close() {
		ds.close();
	}

}
