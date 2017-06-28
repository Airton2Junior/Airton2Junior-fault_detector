package aal.net;

import aal.fd.net.FDClient;
import aal.fd.net.FDServer;
import aal.net.udpc.UdpcPack;

public class UdpcTest {

	public static void main(String[] args) {

		UdpcTest udpcTest = new UdpcTest();

		System .out . println ("Server Start");
		ServerTest serverTest = udpcTest.new ServerTest();
		serverTest.start();

		System .out . println ("Client 1 Start");
		ClientTest clientLspTest1 = udpcTest.new ClientTest(1);
		clientLspTest1.start();

//		int idx = 2;
//		System .out . println ("Client "+idx+" Start");
//		ClientTest clientLspTest2 = udpcTest.new ClientTest(idx);
//		clientLspTest2.start();
	}

	class ServerTest extends Thread {


		public ServerTest() {
		}

		@Override
		public void run() {
	        // Servidor fica apto a receber conexoes na porta 4455
	        FDServer server = new FDServer ();
	        while ( true ){
	            // Le proxima mensagem
	            UdpcPack p = server . read ();
	            // Exibe conteudo da mensagem
	            System .out . println ("SERVER |>  Data : "+ new String (p. getPayload ()));
	            // Devolve a mesma mensagem recebida ao cliente
	            server . write (p);
	        }
		}
	}

	class ClientTest extends Thread {
		
		private int Id;

		public ClientTest(int id) {
			this.Id = id;
		}

		@Override
		public void run() {
			int cont = 0;
	        // Cliente se conecta com um servidor
			FDClient client = new FDClient("localhost");
			for(int i=0; i<=10; i++) {
//			while(true){
		        // Define mensagem a enviar
		        String mensagem = "Olá!! - Cont:"+cont++;
		        // Envia mensagem
		        client . write ( mensagem . getBytes ());
		        // Recebe resposta do servidor de echo
		        byte [] payload = client . read ();
		        // Converte a resposta de bytes para string
		        if (payload != null){
			        // Exibe resposta
			        System .out . println ( "CLIENT "+this.Id+" |>  ECHO:"+ new String ( payload ) );   
		        }
			}
//			client.close();
			System.out.println("FIM!!!!!!!!!!!");
		}

	}

}
