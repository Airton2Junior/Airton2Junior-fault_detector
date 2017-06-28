package aal.net.udpc;

import java.util.LinkedList;

import aal.util.Debug;

public class UdpcNonBlockBuffer implements UdpcBuffer {
	
	private LinkedList<UdpcPack> packQueue;
	
	public UdpcNonBlockBuffer(){
		this.packQueue = new LinkedList<UdpcPack>();
	}

	public void add(UdpcPack pack) throws InterruptedException {
		this.packQueue.add(pack);
	}

	public boolean contains(UdpcPack pack) {
		return this.packQueue.contains(pack);
	}

	public UdpcPack remove(UdpcPack pack) {
		UdpcPack packOut = null;
		int idx = this.packQueue.indexOf(pack);
		if (idx > -1){
			packOut = this.packQueue.get(idx);
			this.packQueue.remove(idx);		
		}
		return packOut;
	}

	public UdpcPack read() {
		UdpcPack pack = null;
		Debug.logUdpc("UdpcNonBlockQueue - Empty?"+this.packQueue.isEmpty());
		if (!this.packQueue.isEmpty()){
			pack = this.packQueue.poll();
			Debug.logUdpc("UdpcNonBlockQueue - Poll:"+pack);
		}	
		return pack;
	}
	

}
