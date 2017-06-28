package aal.net.udpc;

import java.util.concurrent.LinkedBlockingQueue;

public class UdpcBlockBuffer implements UdpcBuffer {
	
	private LinkedBlockingQueue<UdpcPack> packQueue;
	
	public UdpcBlockBuffer(){
		this.packQueue = new LinkedBlockingQueue<UdpcPack>();
	}

	public void add(UdpcPack pack) throws InterruptedException {
		this.packQueue.put(pack);
	}

	public boolean contains(UdpcPack pack) {
		return this.packQueue.contains(pack);
	}

	public UdpcPack read() throws InterruptedException {
		return this.packQueue.take();
	}

	@Override
	public UdpcPack remove(UdpcPack pack) throws InterruptedException {
		return this.packQueue.take();
	}
	


}
