package aal.ft.net;

import aal.net.AALClient;
import aal.util.Params;

public class FTClient extends AALClient{

	public FTClient(String serverIdPx) {
		super(serverIdPx, Params.PORT_FT);
	}
	
}
