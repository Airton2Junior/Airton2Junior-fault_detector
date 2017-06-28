package aal.net.udpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import aal.util.Debug;

public class UdpProxyClient implements UdpcProxy {

	protected DatagramSocket ds;

	public UdpProxyClient()
			throws SocketException, UnknownHostException {
		this.ds = new DatagramSocket();
		// definindo o timeout para o receive
		this.ds.setSoTimeout(UdpcPack.ZERO);// sem timeout
	}

	public void sendUdpcPack(UdpcPack pack) throws InterruptedException, IOException {
		DatagramPacket udpPack = UdpcPack2Udp.convert2Udp(pack);
		ds.send(udpPack);
		Debug.logUdpc("UdpProxyClient - Mensagem enviada: "+pack);
	}

	public void sendUdpcPackAck(UdpcPack pack) throws InterruptedException, IOException {
		this.sendUdpcPack(UdpcPack.getUdpcPackAckByUdpcPack(pack));
	}

	public UdpcPack recvUdpcPack() throws IOException, InterruptedException {
		byte[] buffer = new byte[UdpcPack2Udp.BUFFER_SIZE];
		DatagramPacket udpPack = new DatagramPacket(buffer, buffer.length);
		ds.receive(udpPack);// BLOQUEANTE - para sincronizacao cliente/servidor
		UdpcPack pack = UdpcPack2Udp.convert2UdpcPack(udpPack);
		Debug.logUdpc("UdpProxyClient - Mensagem recebida: "+pack);
		return pack;
	}

	public void close() {
		ds.close();
	}


}
