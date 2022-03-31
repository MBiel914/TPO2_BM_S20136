package zad1;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class SweServer extends Thread {
	private ServerSocket ss = null;
	private static Map<String, String> dictionary;

	private volatile boolean serverRunning = true;

	private String serverTID;

	public SweServer(String serverTID, ServerSocket ss) {
		this.serverTID = serverTID;
		this.ss = ss;
		System.out.println("Server " + serverTID + " started");
		System.out.println("listening at port: " + ss.getLocalPort());
		System.out.println("bind address: " + ss.getInetAddress());

		start();
	}

	public void run() {
		while (serverRunning) {
			try {
				Socket conn = ss.accept();

				System.out.println("Connection established by " + serverTID);

				serviceRequests(conn);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		try {
			ss.close();
		} catch (Exception exc) {
		}
	}

	private static void LoadDictionary() {
		dictionary = new TreeMap<>();
		dictionary.putIfAbsent("dom", "hus");
		dictionary.putIfAbsent("kot", "katt");
		dictionary.putIfAbsent("pies", "hund");
	}

	private void serviceRequests(Socket connection) throws InterruptedException, IOException {
		try (BufferedReader clientBufferedReader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()))) {
			try {
				String clientMessage = clientBufferedReader.readLine();
				System.out.println(clientMessage);
				String[] dataMessage = clientMessage.split(",");
				if (dataMessage.length == 3) {
					if (dataMessage[0].equals("Trans")) {
						try (Socket socket = new Socket("127.0.0.1", Integer.parseInt(dataMessage[2]))) {
							PrintWriter serverPrintWriter = new PrintWriter(socket.getOutputStream(), true);

							if (!dictionary.containsKey(dataMessage[1]))
								serverPrintWriter
										.println("Error - no \"" + dataMessage[1] + "\" translation on EN server.");

							System.out.println(dictionary.get(dataMessage[1]));
							serverPrintWriter.println(dictionary.get(dataMessage[1]));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				try (PrintWriter clientPrintWriter = new PrintWriter(connection.getOutputStream(), true)) {
					clientPrintWriter.println("Error - " + e.getMessage());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		final int SERVERS_NUM = 4;
		ServerSocket ss = null;
		try {
			String host = "127.0.0.1";
			int port = 10668;
			InetSocketAddress isa = new InetSocketAddress(host, port);
			ss = new ServerSocket();
			ss.bind(isa);
		} catch (Exception exc) {
			exc.printStackTrace();
			System.exit(1);
		}

		LoadDictionary();

		Socket clientSocket;
		try {
			clientSocket = new Socket("127.0.0.1", 10666);
			PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF8"), true);
			System.out.println("Init,SWE," + ss.getLocalPort());
			out.println("Init,SWE," + ss.getLocalPort());
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}

		for (int i = 1; i <= SERVERS_NUM; i++) {
			new SweServer("serv thread " + i, ss);
		}
	}
}
