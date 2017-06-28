package aal.net.udpc;

public interface UdpcBuffer {
	
	public void add(UdpcPack pack) throws InterruptedException;
	
	public UdpcPack read() throws InterruptedException;

	public boolean contains(UdpcPack pack);

	public UdpcPack remove(UdpcPack pack) throws InterruptedException;
	
}
