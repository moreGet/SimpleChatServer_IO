package ch.get.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.get.contoller.ComponentController;
import ch.get.model.Client;
import ch.get.model.ClientBucket;
import ch.get.util.LoggerUtil;
import ch.get.util.UuidUtil;
import ch.get.view.RootLayoutController;
import javafx.scene.control.TextArea;

public class ServerInstance extends Thread {
	private ExecutorService ex;
	private ServerSocket serverSocket;
	private RootLayoutController rootLayoutController;
	private TextArea mainLog;
	private boolean serverStatus;
	
	public ServerInstance() {
		ex = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
		rootLayoutController = RootLayoutController.getInstance();
		mainLog = rootLayoutController.getMainLogTextArea();
		serverStatus = true;
	}
	
	public void serverStop() {
		serverStatus = false;
		
		try {
			int bucketSize = ClientBucket.getClientBucket().size();
			
			if (bucketSize >= 1) {
				ClientBucket.getClientBucket()
				.entrySet()
				.parallelStream() // ���� ó��
				.forEach(clientElem -> {
					Client client = clientElem.getValue();
//					client.getPw().println("������ ���� �Ǿ����ϴ�.");
					try {
						client.getSocket().close();
					} catch (IOException e) {
						System.out.println("Ŭ���̾�Ʈ ���� ����...");
					}
				});
				
				// CLEAR
				ClientBucket.getClientBucket().clear();
			}
			
			serverSocket.close();
			ex.shutdown();
		} catch (IOException e) {
			LoggerUtil.error(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(10000);
			
			while (serverStatus) {
				ComponentController.printServerLog(mainLog, "Ŭ���̾�Ʈ�� ��ٸ��� ��...");
				ComponentController.printServerLog(mainLog, "���� ���� Ŭ���̾�Ʈ [ " + ClientBucket.getClientBucket().size() + " ]");
				Socket socket = serverSocket.accept();
				
				String clientId = UuidUtil.getUuid();
				Client client = new Client(socket, clientId);
				ClientBucket.getClientBucket().put(clientId, client); // ����� �ڷ��� ��ȯ
				ex.submit(client); // ������ Ǯ
				LoggerUtil.info("CLIENT SOCKET CONNECTED...[ " + clientId + " ]");
			}
		} catch (IOException e) {
			ComponentController.printServerLog(mainLog, "���� ����...");
			LoggerUtil.info(e.getMessage());
		} catch (Exception e) {
			ComponentController.printServerLog(mainLog, "���� ������ ����...");
			LoggerUtil.error(e.getMessage());
		}
	}
}