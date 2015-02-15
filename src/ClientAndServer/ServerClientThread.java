package ClientAndServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Vector;
import java.io.*;

import javax.swing.*;

public class ServerClientThread extends Thread implements Runnable{
	int playerID;
	Socket acceptedSocket;
	JTextArea textArea;
	Vector<Player> players;
	Player me;

	public ServerClientThread(Socket acceptedSocket, Vector<Player> players, int playerID, JTextArea textArea){
		super("ClientThread");
		this.acceptedSocket = acceptedSocket;
		this.players = players;
		players.add(new Player(50,50, playerID));

		if(players != null)
			System.out.println("Not Null: " + players.size());

		boolean b = false;
		for(int i = 0; i < players.size(); i++){
			if(!b){
				if(players.get(i).id == playerID){
					me = players.get(i);
					b = true;
				}
			}
		}

		this.playerID = playerID;
		this.textArea = textArea;
	}

	public void removeMeAndClose(){
		boolean b = false;
		for(int i = 0; i < players.size(); i++){
			if(!b){
				if(players.get(i).id == playerID){
					players.remove(i);
					b = true;
				}
			}
		}

		try{
			acceptedSocket.close();
		}

		catch(Exception ee){
			ee.printStackTrace();
		}
	}

	public void run(){
		try{
			Socket clientSocket = acceptedSocket;
			clientSocket.setTcpNoDelay(true);
			textArea.append("Accepted. Now creating I/O.\n");
			textArea.append("0");
			ObjectInputStream in2 = new ObjectInputStream(clientSocket.getInputStream());
			textArea.append("1");
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			textArea.append("2");
			textArea.append("player connected. \n");
			out.writeInt(me.id);
			out.writeObject(players);
			out.flush();

			while(!clientSocket.isClosed() && clientSocket.isConnected()){
				try{

					System.out.println("Trying"); 
					textArea.append(in2.readObject().getClass() + " read object class"); //TODO This call to readObject() fixes ONE thing for some reason
					me = (Player) in2.readObject();


					//me.x = in.readInt();
					//me.y = in.readInt();

					if(players != null)
						System.out.println("Not Null: " + players.size());


					//textArea.append("PlayerID: " + playerID + " Players: " + players.size() + " me.x: " + me.x + " me.y: " + me.y + "\n");

					out.reset();
					out.writeObject(players);
					out.flush();

					sleep(15);
				}

				catch(Exception e){
					removeMeAndClose();
				}
			}

		}

		catch(Exception e){
			removeMeAndClose();
		}


	}

}
