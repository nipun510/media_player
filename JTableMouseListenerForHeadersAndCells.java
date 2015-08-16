package media;

import javax.swing.table.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JTableMouseListenerForHeadersAndCells extends JFrame 
{ 
	JTable table;
	private JPanel lowerPanel;
    private JScrollPane scroll;
    private JTableHeader header;
    String filepath;
    String table_name;
    
	JFrame frame=new JFrame();
    
	SwingLayout s=new SwingLayout();
 
    public JTableMouseListenerForHeadersAndCells(JTable table,String table_name)	//Constructor 
    {     
    	this.table=table;
    	this.table_name=table_name;
        initializeInventory();
    }
 
    private void initializeInventory() 
    {
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new BorderLayout());
        header = table.getTableHeader();
        scroll = new JScrollPane(table);
        lowerPanel.add(scroll);
        
        frame.getContentPane().add(lowerPanel);
        frame.setTitle("Inventory Window");
        frame.setLocationRelativeTo(null);
        
        header.addMouseListener(new MyMouseAdapter());
        table.addMouseListener(new MyMouseAdapter());
        
        frame.setSize(500,300);
        frame.setVisible(true);
    }
    private class MyMouseAdapter extends MouseAdapter 
    {
    	public void mousePressed(MouseEvent e) 
        {
        	frame.setVisible(false);
        	if (table.equals(e.getSource())) 
        	{
                int colIdx = table.columnAtPoint(e.getPoint())+1;
                int rowIdx = table.rowAtPoint(e.getPoint())+1;
                String num_of_rows="select * from "+table_name+";";
                try
                {
                	Class.forName("com.mysql.jdbc.Driver");
                	Connection con=DriverManager.getConnection("jdbc:mysql://localhost/playlist","root","root");
    				Statement stat=con.createStatement();
    				ResultSet rs = stat.executeQuery(num_of_rows);
    				while(rs.next())
    				{
    					String p=rs.getString("path");
    				}
    				con.close();
                }
                catch(Exception ee)
                {
                	System.out.println(ee);
                }        
                try
                {
                	String sql = "select path from "+table_name+" where  sr_no =" + rowIdx;
                	Class.forName("com.mysql.jdbc.Driver");
                	Connection con=DriverManager.getConnection("jdbc:mysql://localhost/playlist","root","root");
               		Statement stat=con.createStatement();
                	ResultSet rs = stat.executeQuery( sql );
                	while(rs.next())
                	{
                		filepath=rs.getString("path");
                	}
                	s.text.setText(filepath);
                	s.setsong(filepath);
                }
                catch(Exception el)
                {
                	System.out.println(el);
                	System.out.println("connection failed what is this");
                }
            }
            else if (header.equals(e.getSource())) 
            {
                int selectedColumnIdx = header.columnAtPoint(e.getPoint());
                String colName = table.getColumnName(header.columnAtPoint(e.getPoint()));
            }
        }
    }
}