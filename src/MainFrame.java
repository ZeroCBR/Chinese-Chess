


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;


public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final int FRAME_WIDTH = 565;
	public static final int FRAME_HEIGHT = 700;
	Image offScreenImage = null;
	
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	
	// "red" is used for red side chess
	private ChessGroup red = null;  //  @jve:decl-index=0:
	// "black" is used for black side chess
	private ChessGroup black = null;  //  @jve:decl-index=0:
	// "cb" is used for chess board
	private ChessBoard cb = null;  //  @jve:decl-index=0:
	// for judging whether it's red side's turn to move now 
	private boolean RedPlay = false;
	// for judging game over
	private boolean isOver = false;
	//public boolean isClick = false;
	
	ArrayList<Chess> record=new ArrayList();
	Chess beEaten=null;
	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
		// for double buffering
		new Thread(new PaintThread()).start();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(565, 700);
		this.setContentPane(getJContentPane());
		this.setTitle("Chinese Chess");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
	}
	
	// for double buffering
	public void update(Graphics g) {
		if(offScreenImage == null) {
			offScreenImage = this.createImage(FRAME_WIDTH, FRAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	// for double buffering
	private class PaintThread implements Runnable {

		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//System.out.println("This PaintThread is triggered!");
			}
		}
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJPanel(), null);
			jContentPane.add(getJPanel1(), null);
		}
		// draw chess board background image
		jLabel = new JLabel();
		jLabel.setIcon(new ImageIcon("images/main.gif"));
		jLabel.setBounds(0, 0, 582, 620);
		jLabel.setVisible(true);
		jPanel.add(jLabel,null);
		
		
		
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.setBounds(new Rectangle(0, 0, 558, 620));
			jPanel.addMouseListener(new Moniter());
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(384, 2, 171, 36));
			jLabel1.setText("Black side walks first.");
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.setBounds(new Rectangle(0, 622, 558, 41));
			jPanel1.add(getJButton(), null);
			jPanel1.add(getJButton1(), null);
			jPanel1.add(getJButton2(), null);
			jPanel1.add(jLabel1, null);
		}
		return jPanel1;
	}
	
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(3, 2, 123, 37));
			jButton.setText("New Game");
			//jButton.setToolTipText("Start a new game!");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					
					jLabel.setVisible(false);
					jPanel.remove(jLabel);
					newGame();
					jLabel.setVisible(true);
					jPanel.add(jLabel,null);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setBounds(new Rectangle(128, 2, 123, 37));
			jButton1.setText("Play Back");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(beEaten!=null){	
						beEaten.calPosition(beEaten.getIndX(), beEaten.getIndY());
						jPanel.remove(jLabel);
						beEaten.draw();
						
						beEaten.setVisible(true);
						jPanel.add(beEaten,null);
						jPanel.add(jLabel,null);
						
						beEaten=null;
					}
														
					record.get(record.size()-1).setIndX(record.get(record.size()-1).getPre_x());
					record.get(record.size()-1).setIndY(record.get(record.size()-1).getPre_y());
				      
					record.get(record.size()-1).calPosition(record.get(record.size()-1).getIndX() , record.get(record.size()-1).getIndY());
				    record.get(record.size()-1).draw();
				    record.remove(record.size()-1);
				    
				    if(RedPlay){
				    	jButton1.setEnabled(false);
					    RedPlay = false;
					    jLabel1.setText("Now is Black side's turn to move");
					}
					else{
						jButton1.setEnabled(false);
						RedPlay = true;
						jLabel1.setText("Now is Red side's turn to move");
					}
				}
			});
		}
		return jButton1;
	}
	
	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setBounds(new Rectangle(254, 2, 123, 37));
			jButton2.setText("Draw");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); 
					// TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return jButton2;
	}
	
	/**
	 * start a new game
	 */
	public void newGame(){
		//1. clear the content in "red", "black" and "cb"
		// write your code here!
		
		//check whether red chess group is empty
		if(red != null)
		{
			ListIterator lit = red.getChess().listIterator();
			//remove every content in red chess group
			while( lit.hasNext()){
				Chess c = (Chess)lit.next();
				jPanel.remove(c);
			}
			red = null;
		}
		//check whether black chess group is empty
		if(black != null)
		{
			ListIterator lit = black.getChess().listIterator();
			//remove every content in black chess group
			while( lit.hasNext()){
				Chess c = (Chess)lit.next();
				jPanel.remove(c);
			}
			black = null;
		}
		//check whether chess board is empty
		if(cb != null)
		{
			cb = null;
		}
		
		// 2. read the image folder to get all Chess name list
		// and assign the new value for "red", "black", and "cb".
		// and draw chess on the chess board.
		// write your code here!
		String str[] = readImageFolder("images/");//read the name of image
		for(int i = 0; i < str.length; i++)
		{
			System.out.println(str[i]);
		}
		
		red = new ChessGroup(str, "red");//red chess group
		drawChess(red);//draw red chess group
		black = new ChessGroup(str, "black");//black chess group
		drawChess(black);//draw black chess group
		cb = new ChessBoard(9, 10);//initialize chess board
		cb.SetPositionTaken(red);//set all the red chess that are set taken on the board
		cb.SetPositionTaken(black);//set all the black chess that are set taken on the board
		
		// 3. start the ChessDestory thread
		new ChessDestroy(red,black);
	}
	
	
	public static String[] readImageFolder(String url){
		String tmp = new String();
		String[] str = null;
		Pattern p = Pattern.compile("[a-z]{3,5}-[a-z]{2,5}.gif");
		HashSet<String> hs = new HashSet<String>();
		File f = new File(url);
		if(!f.exists()|| !f.isDirectory()){
			System.out.println("please input an valid directory name!");
			return null;
		}
		File[] fArray = f.listFiles();
		for(int i=0; i<fArray.length; i++){
			if(fArray[i].isFile()){
				tmp = fArray[i].getName();
				//System.out.println(tmp);
				Matcher m = p.matcher(tmp);
				if(m.matches()){
					hs.add(tmp);
				}
			}
		}
		if(str == null){
			str = new String[hs.size()];
			Iterator<String> it = hs.iterator();
			int j = 0;
			while(it.hasNext()){
				tmp = it.next();
				str[j] = new String(tmp);
				j++;
				//System.out.println(j);
			}
		}
		return str;
	}
	
	public void drawChess(ChessGroup cg){
		
		Iterator<Chess> it = cg.getChess().iterator();
		while(it.hasNext()){
			Chess c = it.next();
			jPanel.add(c,null);
		}
	}
	

	/**
	 * This method is used to convert the coordinates of mouse click to be the {row, column} pair on the chess board
	 * @param mx: horizontal coordinate of mouse click
	 * @param my: vertical coordinate of mouse click
	 * @return: return {-1,-1} if the mouse click outside the chess board, or the position is too far away from the intersection point on the chess board.
	 * otherwise, it will return the {row, column} pair.
	 */
	public int[] convertIndex(int mx, int my){
	    // 0. local variables declaration
		int[] back= new int[2];//record the {row,column} pair
		int range = 15;//mouse click range
		int index_x = -1;
		int index_y = -1;
		int start_x = 51;//the pixel x coordinate of first point on the chess board
		int start_y = 53;//the pixel y coordinate of first point on the chess board
		int reminder_x = (mx - start_x)%Chess.UNIT;//take the consideration in a unit of chess board
		int reminder_y = (my - start_y)%Chess.UNIT;//take the consideration in a unit of chess board
		// 1. if mouse clicking at the outside of the chess board, return {-1,-1}
		// write your code here!
		//the condition of mouse clicking not in chess board 
		if(!(mx>51-range && mx<51+8*Chess.UNIT+range && my>53 && my<53+9*Chess.UNIT))
		{
			back[0] = -1;
			back[1] = -1;
			return back;
		}
		
		// 2. if mouse clicking position is within 15x15 area which has one intersection as its center, then return the {row, column} pair of that intersection point.
		// otherwise, return {-1,-1}.
		// only response in 10 X 10 local area
		// write your code here!
		
		//decide whether the mouse click point is within a chess point in range 15
		if((reminder_x <= range || reminder_x >= 57 - range) && (reminder_y <= range || reminder_y >= 57 - range))
        {
            if(reminder_x <= range){
                index_x = (mx - start_x) / 57;
            }
            else{ 
            	if(reminder_x >= 57 - range)
            		index_x = ((mx - start_x) + range) / 57;
            }
            if(reminder_y <= range){
                index_y = (my - start_y) / 57;
            }
            else{ 
            	if(reminder_y >= 57 - range)
            		index_y = ((my - start_y) + range) / 57;
            }
        }
        back[0] = index_x;
        back[1] = index_y;
        return back;
    }
	
	/**
	 * Inner class. Used to respond mouse click on the Panel
	 *
	 */
	class Moniter extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println(e.getX());
			System.out.println(e.getY());
			// 1. get the mouse clicking coordinates, and convert it to {row, column} pair
			int[] tmp = new int[2];//record the {row,column} pair
			tmp = convertIndex(e.getX(),e.getY());
			// 2. if tmp dose not equal to {-1,-1}, we will continue with our work
			if(tmp[0] != -1 && tmp[1] != -1)
			{
				// 2.1. if it is red side's turn to move, and the given destination follows the rule of playing chess, the red chess will move
			    // otherwise, it will stop blinking.
			    // change the value of "RedPlay" to indicate the other side is going to move now
			    // change the displayed message on the panel
			    // write your code here!
				
				//red side play
				if(RedPlay)
				{
					System.out.println("Red side move");
					red.CalIndex();//calculate how many red chess are chosen
					black.CalIndex();//calculate how many black chess are chosen
					if(red.getIndex() != -1)
					{
						Chess c = (Chess)red.getChess().get(red.getIndex());//get the chosen red chessf
						int old_x = c.getIndX();//save the coordinate before play
						int old_y = c.getIndY();//save the coordinate before play					
						
						ChessRule.AllRules(cb, c, tmp[0], tmp[1]);
						red.disableChosen();
						
						//Check whether the black chess has moved
						if(old_x != c.getIndX() || old_y != c.getIndY())
						{
							RedPlay = false;
							jButton1.setEnabled(true);
							jLabel1.setText("Now is Black side's turn to move");
						}
						record.add(c);
					}
					if(black.getIndex() != -1)
						black.disableChosen();
				}
			
				// 2.2. if it is black side's turn to move, and the given destination follows the rule of playing chess, the black chess will move
			    // otherwise, it will stop blinking.
				// change the value of "RedPlay" to indicate the other side is going to move now
				// change the displayed message on the panel
				// write your code here!
				else
				{
					System.out.println("Black side move!");
					red.CalIndex();//calculate how many red chess are chosen
					black.CalIndex();//calculate how many black chess are chosen
					if(red.getIndex() != -1)
						red.disableChosen();
					if(black.getIndex() != -1)
					{
						Chess c = (Chess)black.getChess().get(black.getIndex());//get the black chosen chess
						int old_x = c.getIndX();//save the coordinate before play
						int old_y = c.getIndY();//save the coordinate before play
												
						ChessRule.AllRules(cb, c, tmp[0], tmp[1]);
						black.disableChosen();
						//Check whether the black chess has moved
						if(old_x != c.getIndX() || old_y != c.getIndY())
						{
							RedPlay = true;
							jButton1.setEnabled(true);
							jLabel1.setText("Now is Red side's turn to move");
						}
						record.add(c);
					}
				}
			}
		}

		
	}
	
	/**
	 * Inner class to handle chess destroy
	 */
	class ChessDestroy implements Runnable{
		private ChessGroup cg1 = null;
		private ChessGroup cg2 = null;
		
		ChessDestroy(ChessGroup cg1,ChessGroup cg2 ){			
			this.cg1 = cg1;
			this.cg2 = cg2;
			(new Thread(this)).start();
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!isOver){
				
			// 1, count how many chess have been chosen for each side 
			// write your code here!
			//	System.out.println("subthread running!");
				cg1.CalIndex();
				cg2.CalIndex();
				
				// 2. if the number of chosen chess for one side is larger than 1, then we will change the status of these chess from chosen to be unchosen
				// actually, after changing the status of chess to be unchosen, those chess will stop blinking.
				// write your code here!
				
				//decide whether the number of chosen chess is more than 1
				if(cg1.getNumTaken() > 1)
					cg1.disableChosen();//set all the chess disable chosen
				if(cg2.getNumTaken() > 1)
					cg2.disableChosen();//set all the chess disable chosen
				
				// 3. if the number of chosen chess for both side equals to 1, we need to consider eating chess problem
				// 3.1. if it is red side's turn to move, then the chosen chess from black side will be eaten. 
				// And the movement of the chess should following the rule of playing this chess.
				// 3.2. if it is black side's turn to move, then the chosen chess from red side will be eaten.
				// And the movement of the chess should following the rule of playing this chess.
				// write your code here!
				
				//when both sides have chosen one chess, it start to destroy
				if(cg1.getNumTaken() == 1 && cg2.getNumTaken() == 1)
				{
					
					// 3.3. after eating chess, you should do some clean up work
					
					//red side's turn and red eat black
					if(RedPlay)
					{
						Chess c = (Chess)cg1.getChess().get(cg1.getIndex());//get the red chosen chess 
						Chess cd = (Chess)cg2.getChess().get(cg2.getIndex());//get the black chosen chess
						int black_x = cd.getIndX();
						int black_y = cd.getIndY();
						int old_x = c.getIndX();//save the coordinate before play
						int old_y = c.getIndY();//save the coordinate before play
												
						//check whether the step is correct with the chess rule
						if(c.getRank().equals("jiang") && cd.getRank().equals("jiang"))
							ChessRule.flyGeneralRule(cb, c, cd);
						if(c.getRank().equals("pao"))
							ChessRule.cannonRule(cb, c, cd);
						else
							ChessRule.AllRules(cb, c, black_x, black_y);
						
												
						//3.3.1 remove the eaten chess from the chess board
						
						//Check whether the red chess has moved
						if(old_x != c.getIndX() || old_y != c.getIndY())
						{
							//3.3.2 change the value of "RedPlay" to indicate the other side is going to move now
							RedPlay = false;

							//3.3.3 change the displayed message on the panel
							jLabel1.setText("Now is Black sied's turn to move!");
							jButton1.setEnabled(true);
							beEaten=cd;
							cd.dead();
							jPanel.remove(cd);//remove the chess label
						}
						record.add(c);
					}
					//black side's turn and black eat red
					else
					{
						Chess c = (Chess)cg1.getChess().get(cg1.getIndex());//get the red chosen chess 
						Chess cd = (Chess)cg2.getChess().get(cg2.getIndex());//get the black chosen chess
						int red_x = c.getIndX();
						int red_y = c.getIndY();
						int old_x = cd.getIndX();//save the coordinate before play
						int old_y = cd.getIndY();//save the coordinate before play
												
						//check whether the step is correct with the chess rule
						if(c.getRank().equals("jiang") && cd.getRank().equals("jiang"))
							ChessRule.flyGeneralRule(cb, cd, c);
						if(cd.getRank().equals("pao"))
							ChessRule.cannonRule(cb, cd, c);
						else
							ChessRule.AllRules(cb, cd, red_x, red_y);
						
						
						//Check whether the red chess has moved
						if(old_x != cd.getIndX() || old_y != cd.getIndY())
						{
							RedPlay = true;
							jLabel1.setText("Now is Red side's turn to move");
							jButton1.setEnabled(true);
							beEaten=c;
							c.dead();
							jPanel.remove(c);//remove the chess label
						}
						record.add(cd);
					}
				}
						
				// 4. judge cg1 and cg2 still alive or not. if one of the side doesn't alive any more, a pop out dialogue will show up.
				// after you click the dialogue, the program will exit.
				// write your code here!
				if(!cg1.isAlive() || !cg2.isAlive())
				{
					isOver = true;
					System.out.println("Game Over!");
					if(!cg1.isAlive())
					{
						JOptionPane.showMessageDialog(null, "Black side Win", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
					if(!cg2.isAlive())
					{
						JOptionPane.showMessageDialog(null,"Red side Win", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
				}
				
				try
				{
					Thread.sleep(3000L);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}	
				
			}
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainFrame mf = new MainFrame();
		mf.setVisible(true);
	}
	
	
}  //  @jve:decl-index=0:visual-constraint="10,10"


