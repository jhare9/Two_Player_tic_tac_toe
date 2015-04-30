/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package two.player.tic.tac.toe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author jon
 */
public class Game extends JPanel implements Runnable,MouseListener,KeyListener {
    
    private int board[][];
    private static final int WIDTH = 405;
    private static final int HEIGHT = 405; 
    private static final int BOX_WIDTH =135; 
    private static final int BOX_HEIGHT =135; 
    private int player_one; 
    private int player_two; 
    private int player_turn;
    private boolean gameOver;
    private Thread gameThread; 
    private BufferedImage o; 
    private BufferedImage x; 
    private int mouseX; 
    private int mouseY; 
    private String winner;
    private boolean newGame;
    private boolean mouseClicked; 
    
    public Game(){ 
         // see init method for details. 
            init(); 
            
         // set the pefered screen size and set to focusable for input. 
           setPreferredSize(new Dimension(WIDTH,HEIGHT)); 
           setFocusable(true);
           
        // add a mouse listener
           addMouseListener(this); 
           
        // add a keyboard listener.
           addKeyListener(this); 
           
        // set the players two x and o. 
        player_one = 1; // 1 indicates x. 
        player_two = 0; // 0 indicates o. 
        
            
       // intialize the buffered images.
         try {     
            x = ImageIO.read(new File("x_tic.png"));
            o = ImageIO.read(new File("o_tic.png"));
             } catch (IOException ex) {
            System.out.println("could not find Images"); 
        }
            
       // intilize the thread and start the game.
         gameThread = new Thread(this,"game thread");
         gameThread.start();
    }
    
    // method to initialize the vars need for the game. 
    public void init(){
        
          // create a new board.
        board = new int[3][3]; 
        // fill the board with a number that is not going to be needed I chose 2. 
        for(int i = 0; i< board.length; i++)
            for(int y = 0; y<board.length; y++)
                board[i][y] = 2;
           
       // intilize the players turns.
           player_turn = 0;
           
       // intilize the mouse x and y to a position that is not on the board.
           mouseX =  0;
           mouseY = 0; 
           
           mouseClicked = false; 
           
       // intilize the winner status to nothing.
           winner = " "; 
           
       // intilize a new game. 
           newGame = true; 
    }
    
    // method paint the board,x's and o's to the screen 
    @Override
     public void paint(Graphics g){
         
         // clear the screen. 
          g.setColor(Color.black);
          g.fillRect(0,0,WIDTH,HEIGHT);
         
         // draw the verticle lines.
         g.setColor(Color.blue);
       for(int x = 0; x < (WIDTH / BOX_WIDTH); x++)
           g.drawLine(x * BOX_WIDTH,0, x * BOX_WIDTH, HEIGHT);
       
       // draw the horizontal lines 
       for(int y = 0; y < (HEIGHT / BOX_HEIGHT); y++)
           g.drawLine(0,y * BOX_HEIGHT, WIDTH, y * BOX_HEIGHT);
       
      // draw the x's and o's to the screen.
       for(int x_1 = 0; x_1 < board.length; x_1++)
           for(int y = 0; y <board.length; y++){
               if(board[x_1][y] == 1)
                    g.drawImage(x, x_1 * BOX_WIDTH,y * BOX_HEIGHT,BOX_WIDTH,BOX_HEIGHT,null);
               else if(board[x_1][y] == 0)
                   g.drawImage(o,x_1 * BOX_WIDTH,y * BOX_HEIGHT,BOX_WIDTH,BOX_HEIGHT, null); 
           }
       
         // draw the winning status of the game.
        if(!winner.isEmpty()){
             g.setColor(Color.white);
             g.drawString(winner, (WIDTH - (BOX_WIDTH /2) ) / 2, HEIGHT / 2);
        }
        
        // free up graphic resources.    
        g.dispose();
     }
    
    // method for the thread to start running. 
    @Override
    public void run() {
            
            gameOver = false;
            // enter the game loop if the game is not over. 
            while(!gameOver){
               
              
                update();
                repaint();
                
                // sleep for .2 seconds then update and redraw lower the cpu usage.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
              
            }
            
        try {
            gameThread.join();
        } catch (InterruptedException ex) {
            System.out.println("thread failed to join");
        }
    }
    
    public void  update(){
     
      //make sure a click has been made before continuing.
     if(mouseClicked && newGame){
         
        // set the mouse click back to false
         mouseClicked = false; 
         
        // check if a spot is open.
        if(board[mouseX][mouseY] != 2)
            return; 
        
        System.out.println(player_turn);
        // check to see what letter is put on the board based on players.
        switch(player_turn){
            case 0: 
                board[mouseX][mouseY] = player_one;
                player_turn = 1;
                break;
            case 1: 
                board[mouseX][mouseY] = player_two;
                player_turn = 0;
                break;
        }
        // end of the players turns 
        
        // check to see if there is a winner.
        /* board for x winning and vice versa for o winning. 
            |x o x|    |x o x|    |x o x|   |x x x|    |x o o|    |x x o|    |x o o|    |  o x
            |o x o| || |o x o| || |x o o| |||o   o| || |x x x| || |o x x| || |o o x| || |o x x 
            |x o  |    |o   x|    |x x o|   |x o x|    |o o x|    |o x o|    |x x x|    |o o x 
        */ 
        
            boolean player_one_wins = (board[0][2] == 1 && board[1][1] == 1 && board[2][0] == 1)||
                                      (board[0][0] == 1 && board[1][1] == 1 && board[2][2] == 1)||
                                      (board[0][0] == 1 && board[1][0] == 1 && board[2][0] == 1)||
                                      (board[0][0] == 1 && board[0][1] == 1 && board[0][2] == 1)||
                                      (board[1][0] == 1 && board[1][1] == 1 && board[1][2] == 1)||
                                      (board[0][1] == 1 && board[1][1] == 1 && board[2][1] == 1)||
                                      (board[2][0] == 1 && board[2][1] == 1 && board[2][2] == 1)||
                                      (board[0][2] == 1 && board[1][2] == 1 && board[2][2] == 1);
            
            boolean player_two_wins = (board[0][2] == 0 && board[1][1] == 0 && board[2][0] == 0)||
                                      (board[0][0] == 0 && board[1][1] == 0 && board[2][2] == 0)||
                                      (board[0][0] == 0 && board[1][0] == 0 && board[2][0] == 0)||
                                      (board[0][0] == 0 && board[0][1] == 0 && board[0][2] == 0)||
                                      (board[1][0] == 0 && board[1][1] == 0 && board[1][2] == 0)||
                                      (board[0][1] == 0 && board[1][1] == 0 && board[2][1] == 0)||
                                      (board[2][0] == 0 && board[2][1] == 0 && board[2][2] == 0)||
                                      (board[0][2] == 0 && board[1][2] == 0 && board[2][2] == 0);
            
           if(player_one_wins){
               winner = "player one won!!!";
               newGame = false;
           }
           else if(player_two_wins){
               winner = "player two won!!!";
               newGame  = false;
           }
           // end of the players winning.
           
           // dectected a draw.
            int count = 0;
            for(int x = 0; x < board.length; x++)
                for(int y = 0; y < board.length; y++)
                    if(board[x][y] != 2)
                        count++;
            
           if(count == (board.length * board.length)&& !player_one_wins && !player_two_wins){
              winner = "draw!!!";
              newGame = false; 
           }
           // end of the draw dection. 
           
     }// end of the second if statement
    }
    // not used.
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        
         // set the mouse x and y base on the position clicked.
         mouseX = (e.getX() / BOX_WIDTH); 
         mouseY = (e.getY() / BOX_HEIGHT);
         mouseClicked = true; 
    }
    // not used. 
    @Override
    public void mouseReleased(MouseEvent e) {}
    // not used. 
    @Override
    public void mouseEntered(MouseEvent e) {}
    // not used. 
    @Override
    public void mouseExited(MouseEvent e) {}

    // not used. 
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e){
        // switch to end the game or restart the game.
        switch(e.getKeyCode()){
            case KeyEvent.VK_SPACE:
                init();
                break; 
            case KeyEvent.VK_ESCAPE:
                gameOver = true; 
                System.exit(0);
        }
    }

    // not used. 
    @Override
    public void keyReleased(KeyEvent e) {}
  
}
