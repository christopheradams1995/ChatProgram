
package finalchatprogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import javax.swing.*;
import javax.swing.text.DefaultCaret;


public class ClientFrame extends JFrame implements ActionListener
{
    private JLabel Title;
    private JTextArea MainTF;
    private JTextField SendTF;
    private JButton SendB;
    private JTextField IpNameTF;
    private JLabel IpNameL;
    private JButton ConnectB;
    private String name;
    private JTextField NameTF;
    private JLabel NameB;
    private JLabel PortL;
    private JTextField PortTF;
    
    int ErrorLog = 0; // Logs the amount of errors and closes after 3
    int port = 16822;
    Thread t;
    Thread t2;
    Socket sock;
    boolean IsSend;
    InputStreamReader isr;
    String ClientName = "NewUser";
    
    public ClientFrame()
    {

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLayout(null);
        this.setSize(600,400 );
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int ScreenW = (int)dim.getWidth();
        int ScreenH = (int)dim.getHeight();
        this.setLocation(ScreenW/2,ScreenH/2);
        this.setResizable(false);
        setLocationRelativeTo(null);
        //       x , y , w ,h
        String TitleS = "Connect!";
        Title = new JLabel(TitleS);
        Title.setFont(new Font("SANS_SERIF", Font.BOLD, 18));
        Title.setBounds(350,5,200,60);
        this.add(Title);
        
        MainTF = new JTextArea(16,58);
        MainTF.setBounds(20,20,300,280);
        MainTF.setEditable(false);
        String MainS = "Blawr: testing \nme:hehe \nDave:I hope this works";
        MainTF.setText(MainS);
        JScrollPane sbrText = new JScrollPane(MainTF);
        sbrText.setBounds(20,20,300,280);
        sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.add(sbrText);
        
        SendTF = new JTextField();
        SendTF.setBounds(20,300,270,30);
        SendTF.setForeground(Color.red);
        this.add(SendTF);
        
        SendB = new JButton();
        SendB.setBounds(290,300,30,30);
        SendB.setBackground(Color.red);
        SendB.addActionListener(this);
        this.add(SendB);
        
        IpNameTF = new JTextField();
        IpNameTF.setBounds(400,60,80,25);
        this.add(IpNameTF);
        
        IpNameL = new JLabel("Ip:");
        IpNameL.setBounds(350,60,80,25);
        this.add(IpNameL);
        
        ConnectB = new JButton("Connect");
        ConnectB.setBounds(490,300,80,30);
        ConnectB.addActionListener(this);
        this.add(ConnectB);
        
        NameTF = new JTextField();
        NameB = new JLabel("Name:");
        NameTF.setBounds(400,160,80,25);
        NameB.setBounds(350,160,80,30);
        this.add(NameB);
        this.add(NameTF);
        
        PortL = new JLabel("Port:");
        PortL.setBounds(350,110,80,25);
        this.add(PortL);
        
        PortTF = new JTextField("16822");
        PortTF.setBounds(400,110,80,25);
        this.add(PortTF);
        
        LoadIP();
        
    }
    
    public static void main(String[] args)
    {
        ClientFrame frame = new ClientFrame();
        
    }
    
    /**
     * Starts a client socket and connects using the socket listed. It creates
     * two threads. One for output and one for input. When a message is sent it's sent
     * to the host thread which adds it to the chatlog which is distrubted to each
     * client.
     * 
     */
    public void ClientSocket(String name)
    {
        MainTF.append("\n...Attempting to connect");
        MainTF.revalidate();//refreshes the page
        try
        {
            
            InetAddress addr = InetAddress.getByName(name);
            port = Integer.parseInt(PortTF.getText());
            
            SocketAddress socketaddr = new InetSocketAddress(addr, port);
            sock = new Socket();
            int timeOut = 2000;
            sock.connect(socketaddr, timeOut);
            MainTF.append("\nConnection was successfull with port" + port);
            SaveIP(name);
            OutputS OS = new OutputS();
            
            t = new Thread(OS);
            t.start();
            
            InputS IS = new InputS();
            t2 = new Thread(IS);
            t2.start();
            
        }
        catch(Exception e)
        {
            MainTF.append("\nConnection has failed. Please try again");
        }
    }
    
//Sends the clients messages to the server thread.
public class OutputS implements Runnable
{
    public void run()
    {
        
        while(true)
        {
            try
            {
                System.out.println(">>Client start output");
                t.sleep(200);
                OutputStreamWriter osw = new OutputStreamWriter(sock.getOutputStream());
                BufferedWriter wr = new BufferedWriter(osw);
                
                if(SendTF.getText() != null & IsSend == true)
                {
                    wr.write(ClientName + ":" + SendTF.getText()+ "\n");
                    wr.flush();
                    
                    IsSend = false;
                    System.out.println("Client : " + SendTF.getText());
                    SendTF.setText("");
                    //osw.close();
                }
                 System.out.println("   >>Client end output");
                 Thread.yield();
                //System.out.println("Client: Output : end");
            }
            catch(Exception reare)
            {
                if(ErrorLog < 3)
                {
                    JOptionPane.showMessageDialog(null, "-Error with connection. Attempting to reconnect-", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
                    ClientSocket(name);
                    reare.printStackTrace();
                    ErrorLog++;
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Too many errors...restarting client. Please reconnect", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        }
    }
}
// This method writes the chatlog to the textarea. It's updated regulary by a 
// server thread sending back their chatlog
public class InputS implements Runnable
{
    public void run()
    {
        while(true)
        {
            try
            {
                System.out.println("<<Client start input");
                // error : socket is closed
                t.sleep(200);
                isr = new InputStreamReader(sock.getInputStream());
                BufferedReader rd = new BufferedReader(isr);
                String rsa;
                if((rsa = rd.readLine()) != null && !rsa.equals("null"))
                {
                String[] Splitting = rsa.split(",");
                String FinalChat = "";
                for(int i=0;i<Splitting.length;i++)
                {
                    FinalChat += Splitting[i] + "\n";
                }
                MainTF.setText(FinalChat);
                }
                //System.out.println("Client: Input : end");
                System.out.println("    <<Client end input");
                Thread.yield();
            }
            catch(Exception rer)
            {
                rer.printStackTrace();
            }
        }
        
    }
}


    
    public void actionPerformed(ActionEvent e)
    {
        //Connects and sends out it's data to connect via textfields.
        if(e.getSource().equals(ConnectB))
        {
            name  = IpNameTF.getText();
            ClientSocket(name);
            ClientName = NameTF.getText();
            System.out.println(name);
        }
        
        // Sends the message
        if(e.getSource().equals(SendB))
        {
            IsSend = true;
        }
    }
    
    /*This method will save the IP address to a textfile so the user doesn't
     * have to type it out every time. It's independent from the server connection.
     */
    public void SaveIP(String IP)
    {
        BufferedWriter out = null;
        try
            {
                if(!new File("IP.txt").exists())
                {
                    File filetxt = new File("IP.txt");
                    filetxt.createNewFile();
                }
                out = new BufferedWriter(new FileWriter("IP.txt"));
                //Checks if all the fields are empty
                    //Writes to the file from the textFields using "," as separaters
                out.write(IP);
                out.newLine();
                out.close();
            }
            catch(Exception ef)
            {
                System.out.println("Trouble Saving IP Address");
                ef.printStackTrace();
                try
                {
                    out.close();
                }
                catch(Exception er){}
            }
    }
    
    /**
     * Loads the IPAddress from the last used Server so the client doesn't have
     * to type it out again. This method is independent from the server connection.
     */
    public void LoadIP()
    {
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new FileReader("IP.txt"));
            String read = in.readLine();
            IpNameTF.setText(read);
            in.close();
        }
        catch(Exception er)
        {
            er.printStackTrace();
            try
            {
                in.close();
            }
            catch(Exception ear){}
        }
    }
}
