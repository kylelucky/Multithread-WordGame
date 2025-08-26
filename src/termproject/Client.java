package termproject;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client {
    public boolean char_sensed;
    
    Client(){
        char_sensed = false;
    }
    
    public synchronized void notify_sensed() {
        char_sensed = true;
        notify();
    }
    
    public synchronized void wait_sensed() {
        while(!char_sensed) {
            try {
                System.out.println("OutputHandler is going to sleep...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) 
    {
        try 
        {
        	Client client = new Client();
//            Socket clientsocket = new Socket("172.20.10.2", 8189);
            Socket clientsocket = new Socket("127.0.0.1", 8189);
            ObjectInputStream is;
            is = new ObjectInputStream(clientsocket.getInputStream());

            JFrame frame = new JFrame("Term Project");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            graphic g = new graphic(client, frame);
            
            ThreadedInputHandler t = new ThreadedInputHandler(clientsocket, g, client);
            t.start();
            
            new ThreadedOutputHandler(clientsocket, g, client).start();
            try {
				SharedData inputData = (SharedData) is.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            frame.getContentPane().add(g);
            frame.pack();
            frame.setVisible(true);
            
            
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ThreadedInputHandler extends Thread 
{
    ObjectInputStream is;
    graphic g;
    Client client;
    
    ThreadedInputHandler(Socket s, graphic g, Client client) {
        try {
            this.is = new ObjectInputStream(s.getInputStream());
            this.g = g;
            this.client = client;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {        	
            try {
                SharedData inputData = (SharedData) is.readObject();
                System.out.println(inputData);
                	
                g.updateData(inputData);
                g.repaint();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ThreadedOutputHandler extends Thread {
    DataOutputStream os;
    graphic g;
    Client client;
    
    ThreadedOutputHandler(Socket s, graphic g, Client client) {
        try {
            this.os = new DataOutputStream(s.getOutputStream());
            this.client = client;
            this.g = g;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
        	if(g.sharedData.whoami == 0) {
        		client.wait_sensed();
        	}
        	else { // 컴퓨터일때
        		try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	
        	System.out.println("OutputHandler wake up...");
        	
        	if(g.sharedData.whoami == 0) {
            	try {
            		String guess = g.getGuess();
    	            System.out.println("guess : " + guess);
    	            
    	            if (guess != null && !guess.isEmpty()) {
    	               os.writeBytes(guess + "\n");
    	            }
    	            client.char_sensed = false;
    	         } catch (IOException e) {e.printStackTrace();}
        	}
        	else {
        		try {
					os.writeBytes('a' + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}

        }
    }
}

class graphic extends JPanel {
	private JButton[] alphabetButtons = new JButton[26];
    private String guess;
    private JButton scorebutton;

    private String word = " "; 

    private int[] answer_score; 
    private JLabel result_label; 
    private JLabel scoredLabel1,scoredLabel2;

    private int total_count1 = 0; 
    private int total_count2 = 0; 

    private int professor_count1 = 0; 
    private int professor_count2 = 0; 
    public SharedData sharedData;
    
    Client client;
    JFrame frame;
    
    private int id;

    public graphic(Client client, JFrame frame)
    {
        
       this.client = client;
       this.frame = frame;
       
       setBackground(Color.darkGray);
       setPreferredSize(new Dimension(900, 770));
       setLayout(null); // Absolute positioning

        result_label = new JLabel();
        result_label.setBounds(350, 450, 200, 200);
        add(result_label);
        
        scorebutton = new JButton("Show Scores");
        scorebutton.setBounds(90, 220, 150, 40);
        add(scorebutton);
        
        scoredLabel1 = new JLabel();
        scoredLabel2 = new JLabel();
        
        scoredLabel1.setBounds(30, 240, 500, 100);
        scoredLabel1.setForeground(Color.white);
        
        scoredLabel2.setBounds(30, 270, 500, 100);
        scoredLabel2.setForeground(Color.white);

        scoredLabel1.setVisible(false);
        scoredLabel2.setVisible(false);

        add(scoredLabel1);
        add(scoredLabel2);

        
        for (int i = 0; i < 9; i++) 
        {
        	char letter = (char) ('a' + i);
        	alphabetButtons[i] = new JButton(String.valueOf(letter));
        	alphabetButtons[i].setBounds(205 + 55*i, 570, 45, 45);
        	alphabetButtons[i].addActionListener(new AlphabetButtonListener());
            add(alphabetButtons[i]);
        }
        
        for (int i = 0; i < 10; i++) 
        {
        	char letter = (char) ('j' + i);
        	alphabetButtons[i] = new JButton(String.valueOf(letter));
        	alphabetButtons[i].setBounds(178 + 55*i, 625, 45, 45);
        	alphabetButtons[i].addActionListener(new AlphabetButtonListener());
            add(alphabetButtons[i]);
        }
        
        for (int i = 0; i <7 ; i++) 
        {
        	char letter = (char) ('t' + i);
        	alphabetButtons[i] = new JButton(String.valueOf(letter));
        	alphabetButtons[i].setBounds(258 + 55*i, 680, 45, 45);
        	alphabetButtons[i].addActionListener(new AlphabetButtonListener());
            add(alphabetButtons[i]);
        }
        
        scorebutton.addActionListener(new scoreListener());
    }
    


    public void updateData(SharedData data) {
        this.sharedData = data;
        this.word = data.current_target_word[data.whoami]; 
        this.total_count1 = data.sum[0][0];
        this.total_count2 = data.sum[1][0];
        this.professor_count1 = data.sum[0][1];
        this.professor_count2 = data.sum[1][1];
        this.answer_score = data.correct_character_index[data.whoami];
        this.id = data.whoami;
        
        frame.setTitle("Term Project - Player " + (sharedData.whoami+1));
    }

    public String getGuess() {
        return guess;
    }
    

    public void paintComponent(Graphics page)
    {
        super.paintComponent(page);

        page.setColor(Color.yellow);
        
        int c = 380;
        int x = (c/5)*4;
        int y = (c/5)*3;
       
        page.drawLine(450 - x, 300-y, 450, 300); //교수1
        page.drawLine(450 + x, 300-y, 450, 300); //교수2
        page.drawLine(450 - x, 300 +y, 450, 300); //player1
        page.drawLine(450 + x, 300+y, 450, 300); //player2

        page.setColor(Color.white);

       	if (word != null) 
       	{
       		int start_x = 450 - (50*(word.length())-20)/2;
       		for (int i = 0; i < word.length(); i++) 
       		{
       			page.drawLine(start_x + 50 * i, 540, (start_x+35) + 50 * i, 540);
       		}
       		
       		for (int i = 0; i < word.length(); i++)
       		{
       			if (answer_score != null && answer_score[i] == 1) 
       			{
       				page.drawString(String.valueOf(word.charAt(i)), (start_x+13) + 50 * i, 530);
       			}
       		}
        }
       	
       	Professor pf1 = new Professor("pic/professor.png", 50, 40, 0, professor_count1, 430 - x, 280-y);
       	Professor pf2 = new Professor("pic/professor.png", 50, 40, 1, professor_count2, 420 + x, 280-y);
       	
       	Player p1 = new Player("pic/mach.png", 50, 70, 0, total_count1, 430 - x, 260 +y);
       	Player p2 = new Player("pic/feet.png", 50, 120, 1, total_count2, 420 + x, 220+y);
       	
        ImageIcon Professor1 = new ImageIcon("pic/professor.png"); 
        Image test = Professor1.getImage();
       	
        page.drawImage(pf1.getimg(), pf1.update_x(), pf1.update_y(), pf1.width, pf1.height,this);
        page.drawImage(pf2.getimg(), pf2.update_x(),pf2.update_y(), pf2.width, pf2.height,this);
        page.drawImage(p1.getimg(), p1.update_x(), p1.update_y(), p1.width, p1.height,this);
        page.drawImage(p2.getimg(), p2.update_x(),p2.update_y(), p2.width, p2.height,this);
       
        ImageIcon school = new ImageIcon("pic/school.png"); 
        Image img = school.getImage();
        page.drawImage(img, 410,250,80,80,this);
      
        if(sharedData != null) 
        {
            if (sharedData.whoami == 0) 
            {
                if (total_count1 >= 30 || professor_count2 >= 30) 
                {
                    ImageIcon result = new ImageIcon("pic/a.jpg");
                    Image resultImage = result.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    result_label.setIcon(new ImageIcon(resultImage));          
                    System.out.println("win");
                    new Timer(2000, e -> System.exit(0)).start(); // 2초 후 종료
                } 
                else if (total_count2 >= 30 || professor_count1 >= 30) 
                {
                    ImageIcon result = new ImageIcon("pic/f.jpg");
                    Image resultImage = result.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    result_label.setIcon(new ImageIcon(resultImage));
                    System.out.println("lose");
                    new Timer(2000, e -> System.exit(0)).start(); // 2초 후 종료
                }
            } 
            
            else if (sharedData.whoami == 1)
            {
                if (total_count1 >= 30 || professor_count2 >= 30)
                {
                    ImageIcon result = new ImageIcon("pic/f.jpg");
                    Image resultImage = result.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    result_label.setIcon(new ImageIcon(resultImage));          
                    System.out.println("lose");
                    new Timer(2000, e -> System.exit(0)).start(); // 2초 후 종료
                }
                
                else if (total_count2 >= 30 || professor_count1 >= 30) 
                {
                    ImageIcon result = new ImageIcon("pic/a.jpg");
                    Image resultImage = result.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    result_label.setIcon(new ImageIcon(resultImage));
                    System.out.println("win");
                    new Timer(2000, e -> System.exit(0)).start(); // 2초 후 종료
                }
            }
        }
        
        if (sharedData != null) {
            String scores1 = String.format("Player 1: Correct = %d, Wrong = %d, Finshed left = %d \n",
                sharedData.sum[0][0], sharedData.sum[0][1], 30-sharedData.sum[0][0]);
            String scores2 = String.format("Player 2: Correct = %d, Wrong = %d, Finshed left = %d", 
                    sharedData.sum[1][0], sharedData.sum[1][1],30-sharedData.sum[1][0]);
            scoredLabel1.setText(scores1);
            scoredLabel2.setText(scores2);                        

        } else {
            scoredLabel1.setText("No data available");
            scoredLabel2.setText("No data available");
        }
    }
   
    
    private class AlphabetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JButton source = (JButton) event.getSource();
            guess = source.getText();
            client.notify_sensed();
        }
    }
    
    private class scoreListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
        	scoredLabel1.setVisible(!scoredLabel1.isVisible());
            scoredLabel2.setVisible(!scoredLabel2.isVisible());          
        }
    }

}
