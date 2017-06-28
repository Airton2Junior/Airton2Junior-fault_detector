package aal.efd.net;

import aal.fd.entity.PxList;
import aal.fd.net.FDMessage;

public class EFDMessage extends FDMessage{


	public EFDMessage(String senderIdPx, PxList suspPx, PxList mistPx) {
		super(senderIdPx, suspPx, mistPx);
	}

	public EFDMessage(String message) {
		super(message);
	}


}
