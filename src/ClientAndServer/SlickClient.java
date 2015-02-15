package ClientAndServer;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import java.io.*;
import java.net.*;
import java.util.Vector;

import javax.swing.JOptionPane;

public class SlickClient extends BasicGame{

	ClientThread ct;
	Vector<Player> players;
	Player me;
	int ALL_KEYS = 0xFF;
	boolean keys[];

	public SlickClient()
	{
		super("Test Online Client - by William Starkovich");
	}

	public void init(GameContainer gc) throws SlickException {
		try{
			keys = new boolean[ALL_KEYS];

			for(int i = 0; i < ALL_KEYS; i++){
				keys[i] = false;
			}

			players = new Vector<Player>();

			connect();
		}

		catch(Exception e){

		}
	}

	public void connect(){
		String ip = JOptionPane.showInputDialog("Input server IP.");
		ct = new ClientThread(ip, this);
		ct.start();
		//ct.setPriority(Thread.MAX_PRIORITY);

		me = ct.me;
		players = ct.players;
	}

	public void update(GameContainer gc, int delta)throws SlickException{
		controls();

		players = new Vector<Player>();

		System.out.println("ct size: " + ct.players.size());

		me = ct.me;
		players = ct.players;
	}

	public void render(GameContainer gc, Graphics g) throws SlickException{
		g.setColor(Color.black);
		g.fillRect(0,0,640,480);

		for(int i = 0; i < players.size(); i++){
			g.setColor(Color.cyan);
			System.out.println("From loop\t" + players.get(i).x + ", " + players.get(i).y);
			g.fillRect(players.get(i).x, players.get(i).y, 50, 50);
		}

		g.drawString("Players: " + players.size(), 50, 10);
	}

	public void keyPressed(int key, char c) {
		keys[key] = true;
	}

	public void keyReleased(int key, char c) {
		keys[key] = false;
	}

	public void controls(){
		if(keys[Input.KEY_UP]){
			me.y--;
		}

		else if(keys[Input.KEY_DOWN]){
			me.y++;
		}

		else if(keys[Input.KEY_LEFT]){
			me.x--;
		}

		else if(keys[Input.KEY_RIGHT]){
			me.x++;
		}
	}

	public static void main(String[] args) throws SlickException{
		AppGameContainer app =
				new AppGameContainer( new SlickClient() );

		app.setShowFPS(false);
		app.setAlwaysRender(true);
		app.setTargetFrameRate(60);
		app.setDisplayMode(800, 600, false);
		app.start();
	}
}

class ClientThread extends Thread implements Runnable{
	Socket socket;
	Vector<Player> players;
	int playerID;
	Player me;
	DataOutputStream out;
	ObjectOutputStream out2;
	ObjectInputStream in;
	boolean loop = true;
	SlickClient parent;

	@SuppressWarnings("unchecked")
	public ClientThread(String ip, SlickClient parent){
		super("ClientThread");

		try{
			this.parent = parent;
			players = new Vector<Player>();
			socket = new Socket(ip, 4444);
			socket.setTcpNoDelay(true);
			out2 = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			playerID = in.readInt(); 
			players = (Vector<Player>) in.readObject();
			//me = (Player) in.readObject();

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
		}

		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void run(){
		try{
			while(loop){
				try{
					if(!socket.isClosed() && socket.isConnected()){
						System.out.println("From run()\t" + me.x + ", " + me.y);
						//out.writeInt(me.x);
						//out.writeInt(me.y);

						//out.flush();

						System.out.println("Output");
						out2.writeObject(me);
						out2.flush();
						//Player temp = (Player) in.readObject();
						//System.out.println("Temp pos: " + temp.x + ", " + temp.y);
						//players = new Vector<Player>();
						players = (Vector<Player>) in.readObject();

						System.out.println("size" + players.size());
						sleep(15);
					}

					else{
						loop = false;
						System.out.println("ClientThread loop set to false");
					}
				}
				catch(Exception e){
					e.printStackTrace();
					socket.close();
				}  
			}



		}

		catch(Exception e){
			e.printStackTrace();
		}
	}
}