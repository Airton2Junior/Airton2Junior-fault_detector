package aal.net.udpc;

import java.io.IOException;
import java.net.SocketException;

import aal.util.Debug;

public class UdpcServer extends Thread implements UdpcService{

	private UdpProxyServer udpProxyServer;
	private UdpcBlockBuffer udpcDataBuffer;
	private UdpcNonBlockBuffer udpcRecvBuffer, udpcSentBuffer, udpcAckWBuffer;
	private boolean endService = false;

	public UdpcServer(int portSrv) {
		try {
			this.udpProxyServer = new UdpProxyServer(portSrv);
			this.udpcDataBuffer = new UdpcBlockBuffer();
			this.udpcRecvBuffer = new UdpcNonBlockBuffer();
			this.udpcSentBuffer = new UdpcNonBlockBuffer();
			this.udpcAckWBuffer = new UdpcNonBlockBuffer();
			this.start();
		} catch (SocketException e) {
			Debug.logException(e);
		}
	}

	public void write(UdpcPack pack) {
		try {
			//adicionando na fila de enviado
			this.udpcSentBuffer.add(pack);
			//enviando pela rede
			this.udpProxyServer.sendUdpcPack(pack);		
		} catch (IOException | InterruptedException e) {
			Debug.logException(e);
		}
	}

	public UdpcPack read() {
		UdpcPack pack = null;
		try {
			//leitura da lista de entrada confirmada, BLOQUEANTE
			pack = this.udpcDataBuffer.read();
		} catch (InterruptedException e) {
			Debug.logException(e);
		}
		return pack;
	}
	
	public void close(){
		this.endService  = true;
		this.udpProxyServer.close();
	}
	
	@Override
	public UdpcBuffer getUdpcRecvBuffer() {
		return this.udpcRecvBuffer;
	}

	@Override
	public UdpcBuffer getUdpcDataBuffer() {
		return this.udpcDataBuffer;
	}

	@Override
	public UdpcBuffer getUdpcSentBuffer() {
		return this.udpcSentBuffer;
	}

	@Override
	public UdpcBuffer getUdpcAckWBuffer() {
		return this.udpcAckWBuffer;
	}

	@Override
	public void run() {
		while (!this.endService) {
			try {
				UdpcProcess.UdpcProcessPack(udpProxyServer, this);
			} catch (IOException | InterruptedException ex) {
				Debug.logException(ex);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize(); 
		close();		
	}
}
