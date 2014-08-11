
package finalchatprogram;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class FinalChatProgram extends JFrame implements ActionListener
{
    // Instant Variables
    private JLabel Title;
    private JButton Connect;
    private JButton Host;
    
    public FinalChatProgram()
    {

        // Sets The frame settings
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLayout(null);
        this.setSize(600,400 );
        this.setVisible(true);
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        
        //Title settings
        String TitleS = "Simple Chat Program";
        Title = new JLabel(TitleS);
        Title.setFont(new Font("SANS_SERIF", Font.BOLD, 18));
        Title.setBounds(200,50,200,60);
        this.add(Title);
        
        //Connect Button
        Connect = new JButton("Connect");
        Connect.setBounds(150,200,120,30);
        Connect.addActionListener(this);
        this.add(Connect);
        
        // Host Button
        Host = new JButton("Host");
        Host.setBounds(300,200,120,30);
        Host.addActionListener(this);
        this.add(Host);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        System.out.println(e);
        if(e.getSource().equals(Connect))
        {
            // Starts the Client Frame
            ClientFrame insertFrame = new ClientFrame(); 
            this.dispose();// closes the current frame
        }
        
        if(e.getSource().equals(Host))
        {
            // Starts the Host server with frame
            HostFrame Host = new HostFrame(); 
            this.dispose();// closes the current frame
        }
    }



    public static void main(String[] args) 
    {
        
        try
        {
            // Changes the Defualt frame look
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        catch(Exception e){}

        // Starts the Entire Class
        FinalChatProgram frame = new FinalChatProgram();
    }
}
