package ch.get.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

import ch.get.common.ServerSplitCode;

public class Client extends Thread {
	
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
		this.socket = socket;
		this.clientId = clientId;
	}
	
	@Override
	public void run() {
		try {
			isr = new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8"));
			br = new BufferedReader(isr);
			
			osw = new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8"));
			pw = new PrintWriter(osw);
			
			String[] payLoad;
			Integer cmd;
			String msg;
			
			while (true) {
				payLoad = br.readLine().split(ServerSplitCode.SPLIT.getCode());
				cmd = Integer.parseInt(payLoad[0]);
				msg = payLoad[1];
				
				switch (cmd.intValue()) {
				case 0:
					// JOIN
					doSendMessage(" ���� ä�ù濡 ���� �ϼ̽��ϴ�.");
					break;
				case 1:
					// QUIT
					doQuit();
					doSendMessage(" ���� ä���� ���� �ϼ̽��ϴ�.");
					break;
				default:
					// SEND
					doSendMessage(msg);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			doQuit();
		}
	}
	
	private void doSendMessage(String msg) {
		ClientBucket.getClientBucket()
					.entrySet()
					.parallelStream() // ���� ó��
					.forEach(clientElem -> {
						clientElem.getValue().getPw().println(this.nickName + " : " + msg);
					});
	}
	
	private void doQuit() {
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
}