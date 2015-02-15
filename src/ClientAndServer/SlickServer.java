package ClientAndServer;

import java.net.*;
import java.util.Vector;
import java.io.*;
import javax.swing.*;

public class SlickServer extends JFrame{

	private static final long serialVersionUID = 1L;

	JTextArea textArea;
	JScrollPane scrollPane;

	public SlickServer(){
		super("Test Online Server - by William Starkovich");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 100);
		setLocationRelativeTo(null);

		textArea = new JTextArea();

		scrollPane = new JScrollPane(textArea);

		//getContentPane().add(textArea);
		getContentPane().add(scrollPane);
	}

	public static void main(String[] args) throws IOException {

		Vector<Player> player = new Vector<Player>();

		SlickServer ss = new SlickServer();
		ss.setVisible(true);

		ServerSocket serverSocket = new ServerSocket(4444);
		//serverSocket.setSoTimeout(10000);
		boolean listening = true;

		int playerID = 0;

		while(listening){
			ss.textArea.append("Waiting to connect with player: " + playerID  + "\n");
			new ServerClientThread(serverSocket.accept(), player, playerID, ss.textArea).start();
			playerID++;
			ss.textArea.append("Players: " + player.size() + "\n");
		}

		serverSocket.close();
		System.exit(0);
	}
}