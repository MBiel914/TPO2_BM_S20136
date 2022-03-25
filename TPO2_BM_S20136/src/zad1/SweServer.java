package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class SweServer {
	private static SweServer mainClient;
    private static Socket mainSocket;
    private static Map<String, String> dictionary;
    
	public static void main(String[] args) throws IOException, InterruptedException {
        mainClient = new SweServer("127.0.0.1", 10666);
        LoadDictionary();
        
        try {
			System.out.println("Swedish server port: " +  mainSocket.getLocalPort());
		} catch (Exception e) {
			e.printStackTrace();
		}

        mainClient.say();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainClient.listen();
                	
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        ).start();
    }
	
    private static void LoadDictionary() {
    	dictionary = new TreeMap<>();
    	dictionary.putIfAbsent("dom", "hus");
    	dictionary.putIfAbsent("kot", "katt");
    	dictionary.putIfAbsent("pies", "hund");
	}
	
    public SweServer(String adress, int port) throws IOException {
        mainSocket = new Socket(adress, port);
    }
    
    void say() throws IOException, InterruptedException {
        PrintWriter mainPrintWriter = new PrintWriter(mainSocket.getOutputStream(), true);
        mainPrintWriter.println("Init,SWE," + mainSocket.getLocalPort());
    }
    
    private void listen() throws IOException {
        ServerSocket mainServerSocket = new ServerSocket(mainSocket.getLocalPort());
        while (true){
            Socket mainSocket = mainServerSocket.accept();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String mainCommandFlag = "";
                        BufferedReader mainBufferedReader = new BufferedReader(new InputStreamReader(mainSocket.getInputStream()));
                        
                        while(true) {
                            mainCommandFlag = mainBufferedReader.readLine();

                            if (mainCommandFlag != null) {
                            	String[] dataMessage = mainCommandFlag.split(","); 
                            	if (dataMessage.length == 3)
                            	{
                            		if(dataMessage[0].equals("Trans"))
                            		{
                            			try (Socket socket = new Socket("127.0.0.1", Integer.parseInt(dataMessage[2]))) {
											PrintWriter serverPrintWriter = new PrintWriter(socket.getOutputStream(), true);
											
											if (!dictionary.containsKey(dataMessage[1]))
												serverPrintWriter.println("Error - no \"" + dataMessage[1] + "\" translation on Swe server.");
											

											serverPrintWriter.println("Trans," + dictionary.get(dataMessage[1]));
										}
                            		}
                            	}
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }
            }
            ).start();
        }
    }
}
