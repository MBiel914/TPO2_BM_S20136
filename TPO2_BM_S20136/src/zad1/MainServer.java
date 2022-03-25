package zad1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class MainServer {
	private final ServerSocket mainServerSocket;
	private final String mainServerAdress;
	private final Map<String, String> translationServers;
	
	public static void main(String[] args){
        try {
            System.out.println("Start");
            new MainServer(10666).waitForClient();
            System.out.println("Stop");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public MainServer(int port) throws IOException {
        this.mainServerSocket = new ServerSocket(port);
        String[] inetAddress = InetAddress.getLocalHost().toString().split("/");

        mainServerAdress = inetAddress[1] + ":" + port;
        
        System.out.println(mainServerAdress);

        translationServers = new TreeMap<>();
    }
	
	void waitForClient() throws IOException {
        while (true){
            Socket mainSocket = mainServerSocket.accept();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String mainCommandFlag;

                    try {
                        BufferedReader mainBufferedReader = new BufferedReader(new InputStreamReader(mainSocket.getInputStream()));
                        PrintWriter mainPrintWriter = new PrintWriter(mainSocket.getOutputStream(), true);
                        DataInputStream mainDataInputStream = new DataInputStream(new BufferedInputStream(mainSocket.getInputStream()));
                        while(true) {
                            try {
                                mainCommandFlag = mainBufferedReader.readLine();

                                if (mainCommandFlag != null)
                                {	
                                	String[] dataMessage = mainCommandFlag.split(","); 
                                	if (dataMessage.length == 3)
                                	{
                                		if(dataMessage[0].equals("Init"))
                                		{
                                			translationServers.put(dataMessage[1], dataMessage[2]);
                                		}
                                		else 
                                		{
                                			System.out.println(translationServers);
                                			
                                			if (!translationServers.containsKey(dataMessage[1]))
                                			{
                                				try (Socket socket = new Socket("127.0.0.1", Integer.parseInt(dataMessage[2]))) {
													PrintWriter serverPrintWriter = new PrintWriter(socket.getOutputStream(), true);

													serverPrintWriter.println("Error - no translation server.");
												}
                                			}
                                			
                                			try (Socket socket = new Socket("127.0.0.1", Integer.parseInt(translationServers.get(dataMessage[1])))) {
												PrintWriter serverPrintWriter = new PrintWriter(socket.getOutputStream(), true);

												serverPrintWriter.println("Trans," + dataMessage[0] + "," + dataMessage[2]);
											}	
                                		}
                                	}
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mainPrintWriter.println("Error");
                                mainPrintWriter.println(e.getMessage());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            ).start();
        }
    }
}
