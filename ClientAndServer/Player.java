package ClientAndServer;

import java.io.Serializable;

public class Player implements Serializable{
   private static final long serialVersionUID = 1L;
   int x;
   int y;
   int id;
   
   public Player(int x, int y, int id){
      this.x = x;
      this.y = y;
      this.id = id;
   }
}