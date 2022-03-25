package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
	private static Client mainClient;
    private static Socket mainSocket;
    
	public static void main(String[] args) throws IOException {
        mainClient = new Client("127.0.0.1", 10666);
        
        try {
			System.out.println("Clients port: " +  mainSocket.getLocalPort());
		} catch (Exception e) {
			e.printStackTrace();
		}

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mainClient.say();
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        ).start();
    }

	public Client(String adress, int port) throws IOException {
        mainSocket = new Socket(adress, port);
    }
    
    void say() throws IOException, InterruptedException {
        PrintWriter mainPrintWriter = new PrintWriter(mainSocket.getOutputStream(), true);
        BufferedReader consoleBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String mainCommandFlag = "";

        
        while(!mainCommandFlag.equals("Exit")) {
        	System.out.print("Podaj czynność którą chcesz wykonać: ");
            mainCommandFlag = consoleBufferedReader.readLine();

            mainPrintWriter.println(mainCommandFlag);
            
            Thread.sleep(500);
        }
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
                                System.out.println(mainCommandFlag);
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
