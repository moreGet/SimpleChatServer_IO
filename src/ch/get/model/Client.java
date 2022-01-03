package ch.get.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ch.get.common.ServerSplitCode;
import ch.get.contoller.ComponentController;
import ch.get.view.RootLayoutController;
import javafx.scene.control.TextArea;

public class Client implements Runnable {
	
	private TextArea mainLog;
	private Socket socket;
	private String clientId;
	private String nickName;
	
	/*
	 * INIT SOCKET I/O
	 */
	private InputStreamReader isr;
	private BufferedReader br;
	
	private OutputStreamWriter osw;
	private PrintWriter pw;
	
	public Client(Socket socket, String clientId) {
		this.mainLog = RootLayoutController.getInstance().getMainLogTextArea();
		this.socket = socket;
		this.clientId = clientId;
	}
	
	@Override
	public void run() {
		try {
			isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
			br = new BufferedReader(isr);
			
			osw = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
			pw = new PrintWriter(osw, true);
			
			String payLoad;
			String[] payLoadElem;
			Integer cmd;
			String msg;
			
			ComponentController.printServerLog(mainLog, "[ " + clientId + " ]" + " SOCKET : " + socket.isConnected());
			
			while (true) {
				payLoad = br.readLine();
				
				if (payLoad == null) {
					throw new IOException();
				}
				
				payLoadElem = payLoad.split(ServerSplitCode.SPLIT.getCode());
				ComponentController.printServerLog(mainLog, "[ " + clientId + " ]" + " SERVER CODE : [" + payLoadElem[0] + "] CONTENS : [ " + payLoadElem[1] + " ]");
				
				cmd = Integer.parseInt(payLoadElem[0]);
				msg = payLoadElem[1];
				
				switch (cmd.intValue()) {
				case 0:
					// JOIN
					doJoin(msg);
					doSendMessage("[ " + msg + " ] ���� ä�ù濡 ���� �ϼ̽��ϴ�.");
					ComponentController.printServerLog(mainLog, "[ " + clientId + " ]" + " NICK_NAME : " + msg);
					break;
				case 1:
					// QUIT
					doSendMessage(" ���� ä���� ���� �ϼ̽��ϴ�.");
					doQuit();
					break;
				default:
					// SEND
					doSendMessage(msg);
					break;
				}
			}
		} catch (IOException e) {
			// ���� ����
			ComponentController.printServerLog(mainLog, "[ " + clientId + " ]" + " ���� ����...");
			doQuit();
		}
	}
	
	private void doJoin(String nickName) {
		this.nickName = nickName;
	}
	
	private void doSendMessage(String msg) {
		int bucketSize = ClientBucket.getClientBucket().size();
		
		if (bucketSize >= 1) {
			ClientBucket.getClientBucket()
			.entrySet()
			.parallelStream() // ���� ó��
			.filter(clientElem -> {return !clientElem.getKey().equals(this.clientId);})
			.forEach(clientElem -> {
				clientElem.getValue().getPw().println(this.nickName + " : " + msg);
			});
		}
	}
	
	public void doQuit() {
		ClientBucket.getClientBucket().remove(this.clientId);
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	/*
	 * I/O GETTER
	 */
	// �б�
	public BufferedReader getBr() {
		return br;
	}
	
	// ����
	public PrintWriter getPw() {
		return pw;
	}
	
	public Socket getSocket() {
		return socket;
	}
}