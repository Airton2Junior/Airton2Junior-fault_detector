package aal.net.udpc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import aal.util.Debug;

public class UdpcClient extends Thread implements UdpcService{

	// numero de seq do gerador para cliente
	private short nroSeqCli = UdpcPack.ZERO;

	private InetAddress addrSrv;
	private int portSrv;
	private UdpProxyClient udpProxyClient;
	private UdpcNonBlockBuffer udpcDataBuffer;
	private UdpcNonBlockBuffer udpcRecvBuffer, udpcSentBuffer, udpcAckWBuffer;
	private boolean endService = false;

	public UdpcClient(String hostSrv, int portSrv) {
		try {
			this.addrSrv = InetAddress.getByName(hostSrv);
			this.portSrv = portSrv;
			this.udpProxyClient = new UdpProxyClient();
			this.udpcDataBuffer = new UdpcNonBlockBuffer();
			this.udpcRecvBuffer = new UdpcNonBlockBuffer();
			this.udpcSentBuffer = new UdpcNonBlockBuffer();
			this.udpcAckWBuffer = new UdpcNonBlockBuffer();
			this.start();			
		} catch (SocketException ex) {
			Debug.logException(ex);
		} catch (UnknownHostException ex) {
			Debug.logException(ex);
		}
	}

	public InetAddress getAddr() {
		return addrSrv;
	}

	public int getPort() {
		return portSrv;
	}

	// lendo o payload do primeiro pack da fila de entrada,
	public byte[] read() {
		byte[] payload = null;
		//recuperando da fila de entrada confirmada
		UdpcPack pack = this.udpcDataBuffer.read();//NÃO BLOQUEANTE
		if (pack != null){
			Debug.logUdpc("UdpcClient - Mensagem lida (NON BLOCK):"+pack);
			payload = pack.getPayload();			
		}
		return payload;
	}

	// escrevendo um pack na fila de saida
	public short write(byte[] payload) {
		UdpcPack pack;
		// cria-se o UdpcPack DATA
		pack = new UdpcPack(this.getAddr(), this.getPort(), UdpcPack.DATA, payload);
		// gerando-se e registrando o novo nro de Seq
		this.nroSeqCli = UdpcPack.nextNroSeq(this.nroSeqCli);
		pack.setNroSeq(this.nroSeqCli);
		try {
			// colocando na fila de enviado
			this.udpcSentBuffer.add(pack);
			//enviando pela rede
			this.udpProxyClient.sendUdpcPack(pack);		
		} catch (IOException | InterruptedException e) {
			Debug.logException(e);
		}
		return pack.getNroSeq();
	}
	
	public void close() {
		this.endService = true;
		this.udpProxyClient.close();
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
				UdpcProcess.UdpcProcessPack(udpProxyClient, this);
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

	public boolean isPackAckedByNroSeq(short nroSeq) {
		UdpcPack pack = UdpcPack.getUdpcPackAckByNroSeq(nroSeq, this);
		return this.udpcAckWBuffer.contains(pack);
	}

}
