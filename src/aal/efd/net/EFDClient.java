package aal.efd.net;

import aal.net.AALClient;
import aal.util.Params;

public class EFDClient extends AALClient {

	public EFDClient(String serverIdPx) {
		super(serverIdPx, Params.PORT_EFD);
	}
	
}
