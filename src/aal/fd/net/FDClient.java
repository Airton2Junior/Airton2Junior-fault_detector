package aal.fd.net;

import aal.net.AALClient;
import aal.util.Params;

public class FDClient extends AALClient{

	public FDClient(String serverIdPx) {
		super(serverIdPx, Params.PORT_FD);
	}
	
}
