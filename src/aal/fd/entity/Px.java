package aal.fd.entity;

public class Px {

	// IP address of Px
	private String idPx;

	private PxList mistPx = new PxList();

	private PxList suspPx = new PxList();

	public Px(String idPx) {
		this.idPx = idPx;
	}

	public String getIdPx() {
		return idPx;
	}

	public void setIdPx(String idPx) {
		this.idPx = idPx;
	}

	public PxList getMistPx() {
		return mistPx;
	}

	public void setMistPx(PxList mistPx) {
		this.mistPx = mistPx;
	}

	public PxList getSuspPx() {
		return suspPx;
	}

	public void setSuspPx(PxList suspPx) {
		this.suspPx = suspPx;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Px) && (this.getIdPx().equals(((Px) obj).getIdPx()));
	}

	@Override
	public int hashCode() {
		return this.getIdPx().hashCode();
	}

	@Override
	public String toString() {
		return "[IdPx:" + this.idPx+"|SuspPx:"+this.suspPx+"|MistPx:"+this.mistPx+"]";
	}
}
