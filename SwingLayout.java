package media;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javazoom.jl.decoder.*;
import java.io.*;
import org.jaudiotagger.audio.*;
import javazoom.jl.player.*;

public class SwingLayout extends JFrame implements ActionListener  
{
	private final static int NOTSTARTED = 0;
    private final static int PLAYING = 1;
    private final static int PAUSED = 2;
    private final static int FINISHED = 3;
    
    private int playerStatus = NOTSTARTED ; 
	
    JFrame frame=new JFrame("MUSIC PLAYER");
	
    JPanel panel1=new JPanel();
	JPanel panel2=new JPanel();
	JPanel panel3=new JPanel();
	JPanel panel4=new JPanel();
	
	JButton playlist=new JButton("playlist");
	JButton buttonplay=new JButton("play");
	JButton buttonpause=new JButton("pause");
	JButton buttonstop=new JButton("stop");
	JButton buttonopen=new JButton("..");
	
	JTextField text=new JTextField(50);
	
	JLabel label=new JLabel("00:00");
	
	JProgressBar progressbar=new JProgressBar(0,100);
	
	ImageIcon iconplay=new ImageIcon("Play.gif");
	ImageIcon iconpause=new ImageIcon("Pause.png");
	ImageIcon iconstop=new ImageIcon("Stop.gif");
	ImageIcon iconopen=new ImageIcon("Open.png");
	ImageIcon iconback=new ImageIcon("2.jpeg");
	
	JLabel liconback=new JLabel(iconback);
	
	Player player;
	FileInputStream is,of;
	int duration;
    private final Object playerLock = new Object();
	
    
    SwingLayout()	//Constructor
	{
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		text.setText("C:/Users/Rajat Goyal/Desktop");
		
		buttonplay.setIcon(iconplay);
		buttonpause.setIcon(iconpause);
		buttonstop.setIcon(iconstop);
		buttonopen.setIcon(iconopen);
		
		panel1.add(buttonplay);
		panel1.add(buttonpause);
		panel1.add(buttonstop);
		panel1.add(label);
		panel2.add(text);
		panel2.add(buttonopen);
		panel4.add(liconback);
		panel3.setLayout(new BorderLayout());
		panel3.add(BorderLayout.SOUTH,progressbar);
		panel3.add(BorderLayout.CENTER,panel4);
		
		playlist.setBounds(1260,658,80,26);
		liconback.setBounds(0,0,1350,725);
		
		panel1.setBackground(Color.GRAY);
		panel2.setBackground(Color.GRAY);
		label.setBackground(Color.GRAY);
	    label.setForeground(Color.WHITE);
	    progressbar.setForeground(Color.GRAY);
	    progressbar.setBackground(Color.WHITE);
		
	    frame.add(playlist);
		frame.getContentPane().add(BorderLayout.SOUTH,panel1);
		frame.getContentPane().add(BorderLayout.NORTH,panel2);
		frame.getContentPane().add(BorderLayout.CENTER,panel3);
		
		frame.setSize(1366,728);
		
		frame.setVisible(true);
		
		buttonopen.addActionListener(this);
		buttonplay.addActionListener(this);
		buttonpause.addActionListener(this);
		buttonstop.addActionListener(this);
		playlist.addActionListener(this);
		text.addActionListener(this);
		frame.addWindowListener(new listener());
		
		buttonpause.setEnabled(false);
		buttonplay.setEnabled(false);
		buttonstop.setEnabled(false);
		
		progressbar.setValue(0);
	}
	
	class listener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	};
	
	public void actionPerformed(ActionEvent ae)
	{
		JFileChooser filechooser=new JFileChooser();
		String command=ae.getActionCommand();
		
		if(command.equals(".."))
		{
			filechooser.showOpenDialog(this);
			String filepath=filechooser.getSelectedFile().getAbsolutePath();
			try
			{
				is = new FileInputStream( filepath);
				text.setText(filechooser.getSelectedFile().getAbsolutePath());
				player=new Player(is);
				playerStatus = NOTSTARTED ;  
				
				File myfile=new File(filepath);
				AudioFile audioFile = AudioFileIO.read(myfile);
				duration= audioFile.getAudioHeader().getTrackLength();
				
				buttonplay.setEnabled(true);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		else if(command.equals("play"))
		{
			try 
			{
				String h=text.getText();
				int l=h.lastIndexOf('\\')+1;
				String substr=h.substring(0,l);
				String p=substr+"1.jpg";
				ImageIcon icon=new ImageIcon(p);
				JLabel label_icon=new JLabel(icon);
				label_icon.setBounds(200,100,450,375);
				liconback.add(label_icon);
				label_icon.setVisible(true);
				
				String substr1=h.substring(l, h.length());
				JLabel label9=new JLabel(substr1);
				liconback.add(label9);
				label9.setForeground(Color.RED);
				label9.setBounds(70,20,250,200);
				label9.setVisible(true);
				
				play();
				
				buttonplay.setEnabled(false);
				buttonpause.setEnabled(true);
				buttonopen.setEnabled(false);
				buttonstop.setEnabled(true);
				text.setEnabled(false);
			} 
			catch (JavaLayerException e) 
			{
				e.printStackTrace();
			}
		}
		else if(command.equals("pause"))
		{
			pause();
			buttonplay.setEnabled(true);
			buttonpause.setEnabled(false);
		}
		else if(command.equals("stop"))
		{
			if(player!=null)
			{		
				close();
				panel4.setVisible(true);
				buttonopen.setEnabled(true);
				buttonplay.setEnabled(false);
				buttonpause.setEnabled(false);
				text.setEnabled(true);
			}
		}
		else if(command.equals("playlist"))
		{
			Playlist p=new Playlist(this);
		}
		else
		{
			String filepath;	
	        filepath = ((JTextField)ae.getSource()).getText();
	        setsong(filepath);
		}
	}
	public void setsong(String filepath)
	{
		 try 
			{
				is = new FileInputStream( filepath );
				player=new Player(is);
				
				File myfile=new File(filepath);
				AudioFile audioFile = AudioFileIO.read(myfile);
				duration= audioFile.getAudioHeader().getTrackLength();
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
			playerStatus = NOTSTARTED ; 
			buttonplay.setEnabled(true);
			buttonpause.setEnabled(false);
	}
	public void play() throws JavaLayerException 
	{
        synchronized (playerLock) 
        {
            switch (playerStatus) 
            {
                case NOTSTARTED:    	
                    final Runnable r = new Runnable() 
                    {
                        public void run() 
                        {
                            playInternal();
                        }
                    };
                    final Thread t = new Thread(r);
                    playerStatus = PLAYING;
                    t.start();
                    break;
                
                case PAUSED:
                    resume();
                    break;
                
                default:
                    break;
            }
        }
    }
	
	private void playInternal() 
	{
		int min,sec,nrem,percent,mdur,sdur;
		String time,smin,srem,ftime;
	    while (playerStatus != FINISHED) 
	    {
	    	try 
	    	{
	    		if(!player.play(1)) 
	    		{
	    			break;	
	            }
	        } 
	    	catch (final JavaLayerException e) 
	    	{
	    		break;
	        }
	        synchronized (playerLock) 
	        {
	        	while (playerStatus == PAUSED) 
	        	{
	        		try 
	        		{
	        			playerLock.wait();
	                } 
	        		catch (final InterruptedException e) 
	        		{
	        			break;
	                }
	             }
	         }
	         sec=player.getPosition()/1000;
	         min=sec/60;
	         nrem=sec%60;
	         if(min<10)
	          	 smin="0"+String.valueOf(min);
	         else
	        	 smin=String.valueOf(min);
	         if(nrem<10)
	        	 srem="0"+String.valueOf(nrem);
	         else
	        	 srem=String.valueOf(nrem);
	         time=smin+":"+srem;
	         mdur=duration/60;
	         sdur=duration%60;
	         ftime=time + " / " + String.valueOf(mdur)+":"+String.valueOf(sdur);
	         label.setText(ftime);
	         percent=(sec*100)/duration;
	         progressbar.setValue(percent);  
	       }
	       close(); 
	    }
	 	public void close() 
	 	{
	 		synchronized (playerLock) 
	 		{
	            playerStatus = FINISHED;
	        }
	        try 
	        {
	            player.close();
	        } 
	        catch (final Exception e) 
	        {
	         System.out.println(e);
	        }
	    }
	 	public boolean pause() 
	 	{
	        synchronized (playerLock) 
	        {
	           if (playerStatus == PLAYING) 
	           {
	               playerStatus = PAUSED;
	           }
	           return playerStatus == PAUSED;
	        }
	    }

	    public boolean resume() 
	    {
	        synchronized (playerLock) 
	        {
	            if (playerStatus == PAUSED) 
	            {
	                playerStatus = PLAYING;
	                playerLock.notifyAll();
	            }
	            return playerStatus == PLAYING;
	        }
	    }

	    public void stop() 
	    {
	        synchronized (playerLock) 
	        {
	            playerStatus = FINISHED;
	            playerLock.notifyAll();
	        }
	    }
	
	    public static void main(String args[])
	    {
	    	SwingLayout s=new SwingLayout();
	    }
	}

