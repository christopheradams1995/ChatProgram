/**
 * The host class for the chatprogram
 * Author: Christopher Adams
 */
package finalchatprogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
/*
 * This host server will recieve string from the clients and store it
 * in the server. It will then send the chatlog string out to each client.
 * 
 * *Note: To close the host you need to type "System.Exit()" in the client message.*
 */

public class HostFrame extends JFrame implements ActionListener
{
    private JLabel Title;
    private JButton HostB;
    private JLabel HostData;
    private JTextField portTF;
    int port = 16822;
    //private String ChatLog ;
    ArrayList<String> ChatLog = new ArrayList<String>();
    Thread t;//Input thread
    Thread t2;//output thread
    Socket[] socket = new Socket[50];
    ServerSocket srv;
    BufferedReader rd = null;
    InputStreamReader isr = null;
    int numberOfUsers = 0;
    int CurrentUser = 0;
    int[] Slots = new int[50];
    boolean Error = false;
    
    public HostFrame()
    {
        this.setLayout(null);
        this.setSize(600,400 );
        this.setVisible(true);
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        
        //       x , y , w ,h
        String TitleS = "Host!";
        Title = new JLabel(TitleS);
        Title.setFont(new Font("SANS_SERIF", Font.BOLD, 18));
        Title.setBounds(50,5,200,60);
        this.add(Title);

        HostB = new JButton("Start Hosting");
        HostB.setBounds(20,300,100,30);
        this.add(HostB);
        HostB.addActionListener(this);
        String hostname = "";
        String hostAddress = "";
        
        JLabel PortL = new JLabel("Enter Port:");
        PortL.setBounds(20,200,100,30);
        this.add(PortL);
        
        portTF = new JTextField("16822");
        portTF.setBounds(120,200,100,30);
        this.add(portTF);
        
        try
        {
            //Sets the client info
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            hostAddress = addr.getHostAddress();
            //Server port config
            port = Integer.parseInt(portTF.getText());
            srv = new ServerSocket(port);
        }
        catch(Exception ere)
        {
            ere.printStackTrace();
        }
        String LabelString ="<html>" +"Local Computer Name : " + hostname + "<br> Local Computer Address : " + hostAddress + "</html>";
        HostData = new JLabel(LabelString);
        HostData.setBounds(20,30,200,200);
        this.add(HostData);
        
    }
    
    public static void main(String[] args)
    {
        ClientFrame frame = new ClientFrame();
        
    }
    
    public void HostSocket()
    {

        try
        {
            
            //Will switch through the 20 available slots to see which is null then use that socket
            int CurrentSlot = -1;
            for(int i=0; i<Slots.length;i++)
            {
                if(Slots[i] != 1)
                {
                    System.out.println(port);
                    socket[i] = srv.accept(); // Waits for a client to connect
                    Slots[i] = 1; // declares the slot as used
                    CurrentSlot = i; // Selects the socket to use
                    break;
                }
            }
            
            //Checks to see if there is a slot free
            if(CurrentSlot != -1)
            {
                // Starts the Input stream
                InputS IS = new InputS(CurrentSlot, this);
                t = new Thread(IS);
                t.start();
                // Starts the output stream
                OutputS OS = new OutputS(CurrentSlot);
                t2 = new Thread(OS);
                t2.start();
                numberOfUsers++;
            }
            HostSocket(); // repeats the entire method as a loop
            
        }
        catch(Exception e)
        {
          e.printStackTrace();  
        }
    }
    private String newLine = "\n";
// Saves to the chatlog string

public class InputS implements Runnable
{
    
    private int CurrentUser = 0;
    HostFrame main;
    int[] Slots;
    
    InputS(int C, HostFrame main)
    {
        CurrentUser = C;
        this.main = main;
    }
    
    public void run()
    {
        while(true)
        {
            try
            {
                t.sleep(20);
                isr = new InputStreamReader(socket[CurrentUser].getInputStream());
                rd = new BufferedReader(isr);
                String rel;
                /*
                 * This will read in the message and add it to the Chatlog
                 */
                if((rel = rd.readLine()) != null && !rel.equals("null"))
                {
                    if(rel.contains("System.Exit()"))
                    {
                        System.exit(0);
                    }
                    
                    ChatLog.add(rel);
                    System.out.println("Client : " + rel);
                }
                Thread.yield();
                
            }
            catch(SocketException se)
            {
                //Closes the socket if the client disconnects
                    try 
                    {
                        isr.close();
                        rd.close();
                        main.Slots[CurrentUser] = 0;
                        main.socket[CurrentUser] = null;
                        
                    } catch (IOException ex) 
                    {
                        
                    }
            }
            catch(Exception rer)
            {
            }
        }
        
    }
}

int i = 0;
/**
 * This method will send out the chatlog to each client that is connected.
 */
public class OutputS implements Runnable
{
    private int CurrentUser = 0;
    OutputStreamWriter osw = null;
    BufferedWriter wr = null;
    
    OutputS(int C)
    {
        CurrentUser = C;
    }
    
    public void run()
    {
        while(true)
        {
            try
            {
                // Waits then sends out the updated chatlog
                t2.sleep(20);
                osw = new OutputStreamWriter(socket[CurrentUser].getOutputStream());
                wr = new BufferedWriter(osw);
                wr.write(ChatLog + "\n");
                wr.flush();
                //System.out.println("Host: OutInput : end");
                Thread.yield();
            }
            catch(SocketException se)
            {
                    try {
                        osw.close();
                        wr.close();
                    } catch (IOException ex) 
                    {
                        
                    }
                
            }
            catch(Exception asd)
            {
                //System.out.println("Current user = " + CurrentUser);
                //asd.printStackTrace();
            }
        }
    }
}

    
    
    
    public void actionPerformed(ActionEvent e)
    {
        // Starts the hosting when the button is clicked and is stopped when the
        // message "System.Exit()" is typed in the client. This allows for a convienient and remote
        // Server configuration.
        if(e.getSource().equals(HostB))
        {
            HostSocket();
        }
    }
}
