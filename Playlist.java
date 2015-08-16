package media;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Playlist extends JFrame implements ActionListener
{
	SwingLayout s1;
	int i=1;
	JFrame playlistframe=new JFrame();
	
	ArrayList columnNames = new ArrayList();
    ArrayList data = new ArrayList();
    
    JButton createplaylist=new JButton("create playlist");
	JButton existingplaylist=new JButton("existing playlist");
	JButton create=new JButton("create");
	JButton add=new JButton("add");
	
	JLabel label=new JLabel("playlist name: ");
	
	JTextField text=new JTextField();
	
	JPanel panel1=new JPanel();
	JPanel panel2=new JPanel();
	
	public Playlist(SwingLayout s)		//Constructor
	{
		s1=s;
		
		panel1.add(createplaylist);
		panel1.add(existingplaylist);
		
		label.setBounds(50,70,150,25);
		text.setBounds(210,70,100,25);
		add.setBounds(150,140,100,25);
		create.setBounds(150,110,100,25);
		
		playlistframe.add(label);
		playlistframe.add(text);
		playlistframe.add(add);
		playlistframe.add(create);
		playlistframe.getContentPane().add(BorderLayout.CENTER,panel1);
		playlistframe.setSize(500,700);
		playlistframe.setVisible(true);
		
		createplaylist.addActionListener(this);
		existingplaylist.addActionListener(this);
		add.addActionListener(this);
		create.addActionListener(this);
		text.addActionListener(this);
		
		label.setVisible(false);
		text.setVisible(false);
		add.setVisible(false);
		create.setVisible(false);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		String command=ae.getActionCommand();
		
		if(command.equals("create playlist"))
		{
			label.setVisible(true);
			text.setVisible(true);
			create.setVisible(true);
		}
		else if(command.equals("existing playlist"))
		{
			label.setVisible(true);
			text.setVisible(true);
			create.setVisible(false);
		}
		else if(command.equals("create"))
		{
			String sql = "create table " + text.getText() +
        			"(sr_no integer primary key," +
        			"name varchar(100)," +
        			"path varchar(100));";
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost/playlist","root","");
				Statement stat=con.createStatement();
				stat.executeUpdate(sql);
				con.close();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
			create.setVisible(false);
			add.setVisible(true);
		}
		else if(command.equals("add"))
		{
			try
	        {
	        	Class.forName("com.mysql.jdbc.Driver");
	        	Connection con=DriverManager.getConnection("jdbc:mysql://localhost/playlist","root","root");
	        	Statement stat=con.createStatement();
	        	
	        	String rows_no=" SELECT COUNT(*) FROM "+text.getText();
	        	ResultSet r = stat.executeQuery( rows_no );
	        	
	        	r.next();
	        	i=r.getInt(1)+1;
	        	
	        	JFileChooser filechooser=new JFileChooser();
	        	filechooser.showOpenDialog(this);
	        	String sb=new String(filechooser.getSelectedFile().getAbsolutePath());
	        	int length=sb.length();
	        	int n=sb.lastIndexOf(".");
	        	String sub=sb.substring(n-10, length);
	        	ResultSet rs=stat.executeQuery("select * from "+text.getText()+";");
	        	String rb=sb.replaceAll("\\\\","/");
	        	System.out.println(rb);
	        	String sql="insert into "+ text.getText() +" values("+i+",'" + sub + "','" + rb + "');";
	        	stat.executeUpdate(sql);
	        	con.close();
	        }
	        catch(Exception e)
	        {
	        	System.out.println(e);
	        }
		}
	    else
		{
			String url = "jdbc:mysql://localhost:3306/playlist";
	        String userid = "root";
	        String password = "root";
	        String filepath=ae.getActionCommand();
	        String rows_no=" SELECT COUNT(*) FROM "+ae.getActionCommand();
	        String sql = "SELECT * FROM " + ae.getActionCommand();
	        
	        try {
	        		Connection connection = DriverManager.getConnection( url, userid, password );
	        		Statement stmt = connection.createStatement();
	        		ResultSet rs = stmt.executeQuery( sql );
	        		int count = 0;
	        		ResultSetMetaData md = rs.getMetaData();
	            	int columns = md.getColumnCount();
	           	    for (int i = 1; i <= columns; i++)
	            	{
	           			columnNames.add( md.getColumnName(i) );
	            	}
	            	while (rs.next())
	            	{
	            		count++;
	            		ArrayList row = new ArrayList(columns);
	            		for (int i = 1; i <= columns; i++)
	            		{
	            			row.add( rs.getObject(i) );
	            		}
	            		data.add( row );
	            	}
	            	System.out.println(count);
	        	}
	        	catch (SQLException e)
	        	{
	        		System.out.println( e.getMessage() +"so this is the error");
	        	}
	        
	        	Vector columnNamesVector = new Vector();
	        	Vector dataVector = new Vector();
	        	for (int i = 0; i < data.size(); i++)
	        	{
	        		ArrayList subArray = (ArrayList)data.get(i);
	        		Vector subVector = new Vector();
	        		for (int j = 0; j < subArray.size(); j++)
	        		{
	        			subVector.add(subArray.get(j));
	        		}
	        		dataVector.add(subVector);
	        	}
	        	for (int i = 0; i < columnNames.size(); i++ )
	        		columnNamesVector.add(columnNames.get(i));
	        	String s=new String();
	        	JTable table = new JTable(dataVector,columnNamesVector);
	        	JScrollPane scrollPane = new JScrollPane( table );
	        	JPanel buttonPanel=new JPanel();
	        	buttonPanel.add(scrollPane);
	        	
	        	playlistframe.setVisible(false);
	        	add.setVisible(true);
	        	table.setVisible(true);
	        	s1.close();
	        	s1.frame.dispose();
	        
	        	MyThread myThread = new MyThread(table,ae.getActionCommand());
	        	myThread.start();
	    	}
		}
	}
	class MyThread extends Thread 
	{
		JTable table;
		String table_name;
		
		MyThread(JTable table,String table_name)
		{
			this.table=table;
			this.table_name=table_name;
		}
		public void run()
		{
			JTableMouseListenerForHeadersAndCells obj=new JTableMouseListenerForHeadersAndCells(table,table_name);
		}
	}