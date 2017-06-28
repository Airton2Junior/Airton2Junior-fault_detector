package aal.fd.logic;

import aal.fd.entity.Pi;
import aal.fd.entity.Px;
import aal.fd.net.FDMessage;
import aal.fd.net.FDServer;
import aal.net.Network;
import aal.net.udpc.UdpcPack;
import aal.util.Debug;

public class AALFaultDetector {

	public static void main(String[] args) {

		Pi pi = new Pi(Network.getMyIP());
		AALFaultDetector fd = new AALFaultDetector();

		Debug.logFD("Task T2 - Started");
		PropagatingSuspicionsAndMistakes propSuspAndMist = fd.new PropagatingSuspicionsAndMistakes(pi);
		propSuspAndMist.start();

		Debug.logFD("Task T1 - Started");
		GeneratingSuspicions genSusp = fd.new GeneratingSuspicions(pi);
		genSusp.start();

	}

	// Task T1
	public class GeneratingSuspicions extends Thread {

		private Pi pi;

		public GeneratingSuspicions(Pi pi) {
			this.pi = pi;
		}

		@Override
		public void run() {
			while (true) {
				Debug.logFD("T1 - Broadcast Query and Wait Alfai Responses - Round:" + pi.getRound());
				pi.broadcastQueryAndWaitAlfaiResponse();
				Debug.logFD("T1 - Detects new suspicions - Round:" + pi.getRound());
				pi.detectNewSuspicions();
				Debug.logFD("T1 - Evaluate unRound to reset KnowPi - unRound:" + pi.getUnRound());
				pi.evaluateUnRoundToResetKnowPi();				
			}
		}
	}

	// Task T2
	public class PropagatingSuspicionsAndMistakes extends Thread {

		private Pi pi;

		public PropagatingSuspicionsAndMistakes(Pi pi) {
			this.pi = pi;
		}

		@Override
		public void run() {
			FDServer server = new FDServer();
			while (true) {
				Debug.logFD("T2 - Upon reception of Query from Pj - Round:" + pi.getRound());
				UdpcPack pack = server.read();
				String messageQ = new String(pack.getPayload());
				messageQ = messageQ.trim();
				Debug.logFD("T2 - MENSAGEM RECEBIDA! |>" + messageQ + "<| - Round:" + pi.getRound());
				FDMessage fdMessageQ = new FDMessage(messageQ);
				Px pj = new Px(fdMessageQ.getSenderIdPx());
				pj.setSuspPx(fdMessageQ.getSuspPx());
				pj.setMistPx(fdMessageQ.getMistPx());
				pi.addKnowPi(pj);
				Debug.logFD("T2 - Adicionando " + pj + " em KnowPi" + pi.getKnowPi() + " - Round:" + pi.getRound());
				Debug.logFD("T2 - Propagating Suspicions - Round:" + pi.getRound());
				pi.propagatingSuspicions(pj);
				Debug.logFD("T2 - Propagating Mistakes - Round:" + pi.getRound());
				pi.propagatingMistakes(pj);
				Debug.logFD("T2 - Send Response to Pj - Round:" + pi.getRound());
				FDMessage fdMessageR = new FDMessage(pi.getIdPx(), pi.getSuspPx(), pi.getMistPx());
				byte[] payload = fdMessageR.toString().getBytes();
				Debug.logFD("T2 - MENSAGEM DEVOLVIDA! |>"+fdMessageR.toString()+"<| - Round:" + pi.getRound());
				pack.setPayload(payload);
				server.write(pack);
			}
		}

	}

}
