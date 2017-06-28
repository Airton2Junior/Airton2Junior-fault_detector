package aal.net.udpc;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

public class UdpcPack2Udp {

	public static final int BUFFER_SIZE = 1000;
	private static final int POS_CONNID = 994;
	private static final int POS_NROSEQ = 996;
	private static final int POS_MESSID = 998;

	public static UdpcPack convert2UdpcPack(DatagramPacket udpPack) {
		ByteBuffer bb = ByteBuffer.wrap(udpPack.getData());
		byte[] bufPayload = new byte[POS_CONNID];
		bb.get(bufPayload);
//		short bufConnId = bb.getShort(POS_CONNID);
		short bufNroSeq = bb.getShort(POS_NROSEQ);
		short bufMessId = bb.getShort(POS_MESSID);
		UdpcPack pack = new UdpcPack(udpPack.getAddress(), udpPack.getPort(), bufMessId, bufPayload);
		pack.setNroSeq(bufNroSeq);
		return pack;
	}

	public static DatagramPacket convert2Udp(UdpcPack pack) {
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
		byte[] payload = pack.getPayload();
		bb.put(payload);
		bb.putShort(POS_CONNID, UdpcPack.ZERO);
		bb.putShort(POS_NROSEQ, pack.getNroSeq());
		bb.putShort(POS_MESSID, pack.getMessId());
		byte[] buffer = bb.array();
		DatagramPacket udpPack = new DatagramPacket(buffer, buffer.length, pack.getAddr(), pack.getPort());
		return udpPack;
	}

}
