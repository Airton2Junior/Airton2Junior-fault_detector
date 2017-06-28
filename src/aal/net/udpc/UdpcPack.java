package aal.net.udpc;

import java.net.InetAddress;

public class UdpcPack {
	// constantes de messId
	public static final short DATA = 1;
	public static final short ACK = 2;
	
	// constante de data para VAZIO
	public static final byte[] NIL = "NIL".getBytes();
	
	// constante de zero para short
	public static final short ZERO = (short) 0;

	// limite para nro Seq
	private static final short NRO_SEQ_LIMIT = 32767;
	
	private byte[] payload;
	private short nroSeq;
	private short messId;
	// informacoes sobre o host de origem/
	private InetAddress addr;
	private int port;

	public UdpcPack(InetAddress addr, int port, short messId, byte[] payload) {
		setAddr(addr);
		setPort(port);
		setMessId(messId);
		setPayload(payload);
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
		
	public byte[] getPayload() {
		return this.payload;
	}

	public short getNroSeq() {
		return nroSeq;
	}

	public Short getNroSeqObj() {
		return new Short(getNroSeq());
	}

	public void setNroSeq(short nroSeq) {
		this.nroSeq = nroSeq;
	}

	public short getMessId() {
		return messId;
	}

	public void setMessId(short messId) {
		this.messId = messId;
	}

	public InetAddress getAddr() {
		return addr;
	}

	private void setAddr(InetAddress addr) {
		this.addr = addr;
	}

	public int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	public static UdpcPack getUdpcPackAckByUdpcPack(UdpcPack packIn) {
		UdpcPack UdpcPackRet = new UdpcPack(packIn.getAddr(), packIn.getPort(), aal.net.udpc.UdpcPack.ACK, aal.net.udpc.UdpcPack.NIL);
		UdpcPackRet.setNroSeq(packIn.getNroSeq());
		return UdpcPackRet;
	}

	public static UdpcPack getUdpcPackAckByNroSeq(short nroSeq, UdpcClient udpcClient) {
		UdpcPack UdpcPackRet = new UdpcPack(udpcClient.getAddr(), udpcClient.getPort(), aal.net.udpc.UdpcPack.ACK, aal.net.udpc.UdpcPack.NIL);
		UdpcPackRet.setNroSeq(nroSeq);
		return UdpcPackRet;
	}

	public static UdpcPack getUdpcPackDataByUdpcPack(UdpcPack packIn, byte[] payload) {
		UdpcPack UdpcPackRet = new UdpcPack(packIn.getAddr(), packIn.getPort(), aal.net.udpc.UdpcPack.DATA, payload);
		UdpcPackRet.setNroSeq(packIn.getNroSeq());
		return UdpcPackRet;
	}

	// verificador de nro de seq para clientes
	public static short nextNroSeq(short nroSeq) {
		short nrSeqTmp = (short) ((nroSeq + 1) % NRO_SEQ_LIMIT);
		nrSeqTmp = (nrSeqTmp == 0 ? 1 : nrSeqTmp);
		return nrSeqTmp;
	}


	public String toString(){
		return "UdpcPack:[MessID:"+decodeMessID(this.getMessId())+"|NroSeq:"+this.getNroSeq()+"|Payload:"+new String(this.getPayload()).trim()+"]";
	}

	private String decodeMessID(short messId2) {
		String ret = "NULL";
		switch (messId2) {
		case DATA:
			ret = "DATA";
			break;
		case ACK:
			ret = "ACK";
			break;
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof UdpcPack) && (this.getNroSeqObj().equals(((UdpcPack) obj).getNroSeqObj()));
	}

	@Override
	public int hashCode() {
		return this.getNroSeqObj().hashCode();
	}

}
