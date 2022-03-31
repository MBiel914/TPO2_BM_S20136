package zad1;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class MainServer extends Thread {
	private ServerSocket ss = null;
	private static Map<String, String> translationServers;

	private volatile boolean serverRunning = true;

	private String serverTID;

	public MainServer(String serverTID, ServerSocket ss) {
		this.serverTID = serverTID;
		this.ss = ss;
		translationServers = new TreeMap<>();
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

	private void serviceRequests(Socket connection) throws InterruptedException {
		try (BufferedReader clientBufferedReader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()))) {
			try {
				String clientMessage = clientBufferedReader.readLine();
				System.out.println(clientMessage);
				String[] dataMessage = clientMessage.split(",");
				if (dataMessage.length == 3) {
					if (dataMessage[0].equals("Init")) {
						System.out.println("Add new server: " + dataMessage[1] + " - " + dataMessage[2]);
						translationServers.put(dataMessage[1], dataMessage[2]);
					} else {
						if (!translationServers.containsKey(dataMessage[1])) {
							try (Socket socket = new Socket("127.0.0.1", Integer.parseInt(dataMessage[2]))) {
								PrintWriter serverPrintWriter = new PrintWriter(socket.getOutputStream(), true);
								System.out.println("Trans," + dataMessage[0] + "," + dataMessage[2]);
								serverPrintWriter.println("Error - no translation server for " + dataMessage[1] + ".");
							}
						} else
							try (Socket socket = new Socket("127.0.0.1",
									Integer.parseInt(translationServers.get(dataMessage[1])))) {
								PrintWriter serverPrintWriter = new PrintWriter(socket.getOutputStream(), true);
								System.out.println("Trans," + dataMessage[0] + "," + dataMessage[2]);
								serverPrintWriter.println("Trans," + dataMessage[0] + "," + dataMessage[2]);
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
			int port = 10666;
			InetSocketAddress isa = new InetSocketAddress(host, port);
			ss = new ServerSocket();
			ss.bind(isa);
		} catch (Exception exc) {
			exc.printStackTrace();
			System.exit(1);
		}

		for (int i = 1; i <= SERVERS_NUM; i++) {
			new MainServer("serv thread " + i, ss);
		}
	}
}
