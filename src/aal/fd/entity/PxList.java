package aal.fd.entity;

import java.util.HashMap;

public class PxList {

	private HashMap<Px, Integer> list = new HashMap<Px, Integer>();

	public Iterable<Px> keySet() {
		return this.list.keySet();
	}

	public Integer get(Px px) {
		return this.list.get(px);
	}

	public void put(Px px, Integer round) {
		this.list.put(px, round);
	}

	public void clear() {
		this.list.clear();
	}

	public int size() {
		return this.list.values().size();
	}

	public boolean contains(Px px) {
		return this.list.containsKey(px);
	}

	public void remove(Px px) {
		this.list.remove(px);
	}

	public void putAll(PxList pxList) {
		this.list.putAll(pxList.list);
	}

	public static PxList union(PxList pxList1, PxList pxList2) {
		PxList resList = new PxList();
		resList.putAll(pxList1);
		resList.putAll(pxList2);
		return resList;
	}

	public static PxList minus(PxList pxList1, PxList pxList2) {
		PxList resList = new PxList();
		for (Px px : pxList1.keySet()) {
//			Debug.log("T1 - RECFROMPI CONTAINS PX: " + px + "?");
			if (!pxList2.contains(px)) {
//				Debug.log("T1 - RECFROMPI NOT CONTAINS PX: " + px + "!");
				Integer round = pxList1.get(px);
//				Debug.log("T1 - ADD PX: " + px + "!");
				resList.put(px, round);
			}
		}
		return resList;
	}

	public boolean isSimilar(PxList pxList) {
		boolean ret = false;
		if (this.size() == pxList.size()){
			int contSuccess = 0;
			for (Px px : pxList.keySet()) {
				if ( this.contains(px) ){
					contSuccess++;
				}
			}
			ret = (contSuccess == this.size());
		}
		return ret;
	}

		
	@Override
	public String toString() {
		String ret = "{";
		for (Px px : this.keySet()) {
			ret += "(" + px.getIdPx() + "," + this.get(px) + ")-";
		}
		ret += "}";
		return ret;
	}

	public boolean isMistake(PxList suspPx) {
		for (Px px : suspPx.keySet()) {
			if ( this.contains(px) ){
				return true;
			}
		}
		return false;
	}

}
