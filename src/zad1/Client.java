package zad1;

import java.io.*;
import java.net.*;

public class Client {
	public static int port;
	public static String server;
	public static int timeout;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader mainBufferedReader;
	private ServerSocket mainServerSocket;

	public Client(String server, int port, int timeout) {
		Client.server = server;
		Client.port = port;
		Client.timeout = timeout;
	}

	public String SendRequestForTransaction(String word, String language) {
		try {
			clientSocket = new Socket(server, port);
			out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF8"), true);

			System.out.println(word + "," + language + "," + clientSocket.getLocalPort());
			out.println(word + "," + language + "," + clientSocket.getLocalPort());

			mainServerSocket = new ServerSocket(clientSocket.getLocalPort());
			String returnValue;
			Socket mainSocket = mainServerSocket.accept();
			try {
				String mainCommandFlag = "";
				mainBufferedReader = new BufferedReader(new InputStreamReader(mainSocket.getInputStream()));

				while (true) {
					mainCommandFlag = mainBufferedReader.readLine();

					if (mainCommandFlag != null) {
						System.out.println(mainCommandFlag);
						String[] dataMessage = mainCommandFlag.split(",");
						returnValue = dataMessage[0];
						System.out.println(returnValue);
						Close();
						return returnValue;
					}
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			clientSocket.setSoTimeout(timeout);
			Close();
			return "-1";

		} catch (UnknownHostException exc) {
			System.err.println("Uknown host " + server);
			System.exit(2);
		} catch (Exception exc) {
			exc.printStackTrace();
			System.exit(3);
		}
		Close();
		return "-2";
	}

	private void Close() {
		out.close();
		try {
			mainBufferedReader.close();
			mainServerSocket.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		ClientGUI.launch(ClientGUI.class, args);
	}
}
