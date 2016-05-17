package probcog.rosie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JPanel;

import edu.umich.rosie.language.*;
import edu.umich.rosie.language.LanguageConnector.MessageType;
import edu.umich.rosie.language.Message.MessageClient;
import edu.umich.rosie.language.IMessagePasser.IMessageListener;
import edu.umich.rosie.language.IMessagePasser.RosieMessage;
import edu.umich.rosie.soar.SoarAgent;

public class RemoteTerminal extends JFrame implements IMessagePasser.IMessageListener{
    public final int PORT = 7679;

    private final JPanel soarPanel;
    private final JTextField soarInputText;
    private final JButton soarSendButton;
    private final JTextPane soarTextField;
    private StyledDocument soarDoc;
    
    // OTHER
    private Object outputLock = new Object();

    private MessageClient client;
    private String server;
    

    /***********************************************
     * ChatHistory
     *   Manages the message history of a textField
     ***********************************************/
    class ChatHistory{
      public final JTextField textField;
      public ArrayList<String> history;
      public int index;
      public ChatHistory(JTextField field){
        textField = field;
        history = new ArrayList<String>();
        index = 0;
      }

      public void add(String message){
        history.add(message);
        index = history.size();
        textField.setText("");
      }

      public String getCurrent(){
          if(index < history.size()){
              return history.get(index);
          } else {
              return "";
          }
      }

      public void next(){
        if(index + 1 < history.size()){
          index++;
          textField.setText(history.get(index));
        } else {
          textField.setText("");
        }
      }

      public void prev(){
        if(index > 0){
          index--;
        }
        if(history.size() > index){
          textField.setText(history.get(index));
        }
      }
    }

    /*******************************************************
     * Handling Keyboard Events
     *   Up/Down - go up/down in history to repeat sentence
     *
     *******************************************************/
    
    class ChatKeyAdapter extends KeyAdapter{
      private ChatHistory history;
      public ChatKeyAdapter(ChatHistory history){
        this.history = history;
      }

      public void keyPressed(KeyEvent arg0) {
        if(arg0.getKeyCode() == KeyEvent.VK_UP) {
          history.prev();
        } else if(arg0.getKeyCode() == KeyEvent.VK_DOWN){
          history.next();
        } else if(arg0.getKeyCode() == KeyEvent.VK_RETURN){b

        }
      }

      public void keyReleased(KeyEvent arg0) {
      }
    }; 

    public RemoteTerminal(String server) {
      soarPanel = new JPanel();
      soarInputText = new JTextField();
      soarSendButton = new JButton("Send Command");
      soarTextField = new JTextPane();
      soarDoc = (StyledDocument)soarTextField.getDocument();
      setupStyles(soarDoc);

      setupGUI();
      createClient(server, PORT);
    }

    private void connectToClient(String server, int port){
        System.out.println("RemoteTerminal: Connecting to " + server);
        try {
            client = new MessageClient();
            client.newConnection(Inet4Address.getByName(server), port);
            System.out.println("RemoteTerminal: Successfully connected");
        } catch (UnknownHostException e){
            System.out.println("RemoteTerminal: Tried connecting to unknown host");
            client = null;
        } catch (IOException e){
            System.out.println("RemoteTerminal: Could not establish a server connection");
            client = null;
        }
  
        if (client != null){
          client.addMessageListener(this);
        }
    }
    
    /**********************************************************
     * Public Interface for interacting with the chat frame
     * 
     * registerNewMessage(String message, MessageSource src)
     *   Use to add a new message to the chat text field
     * clearMessages()
     *   Remove all messages from the text field
     */

    private void sendMessageToRosie(String message){
        if(client != null && client.isConnected()){
            client.sendMessage(message, LanguageConnector.MessageType.INSTRUCTOR_MESSAGE);
        }
    }
    
    @Override
    public void receiveMessage(RosieMessage message){
        synchronized(outputLock){
            StyledDocument doc;
            String message;
            String timestamp = (new SimpleDateFormat("mm:ss:SSS")).format(new Date());

            switch(message.type){
                case INSTRUCTOR_MESSAGE:
                    doc = chatDoc;
                    message = "I: " + message.message + "\n";
                    break;

                case AGENT_MESSAGE:
                    doc = chatDoc;
                    message = "R: " + message.message + "\n";
                    break;

                case SOAR_OUTPUT:
                    doc = soarDoc;
                    message = message.message;
                    break;

                default:
                    // Don't handle other messages
                    return;
            }

            addMessageToDocument(doc, message, message.type);
        }
    }

    private void addMessageToDocument(StyledDocument doc, String message, MessageType type){
        synchronized(doc){
            Style msgStyle = doc.getStyle(type.toString());
            if(msgStyle == null){
                return;
            }

            try{
                int origLength = doc.getLength();
                doc.insertString(origLength, message, msgStyle);
            } catch (BadLocationException e){
                // Should never encounter this
                System.err.println("Failed to add message to chat window");
            }

            // AM: Will make it auto scroll to bottom
            int end = doc.getLength();
            soarTextField.select(end, end);
        }
    }

    /**************************************************
     * Code for setting up the Panel and its GUI elements
     */
    
    private void setupGUI(){
        soarTextField.setEditable(false);
        soarInputText.setFont(new Font("Serif", Font.PLAIN, 18));

        DefaultCaret caret = (DefaultCaret)soarTextField.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane pane = new JScrollPane(soarTextField);
        pane.setViewportView(soarTextField);

        sendSoarButton.setBackground(new Color(150, 255, 150));

        ChatHistory soarHistory = new ChatHistory(soarInputText);
        ChatKeyAdapter keyAdapter = new ChatKeyAdapter(soarHistory);
        soarTextField.addKeyListener(keyAdapter);
        soarInputText.addKeyListener(keyAdapter);
        soarSendButton.addKeyListener(keyAdapter);
        this.getRootPane().setDefaultButton(soarSendButton);
              soarInputText.setText("");
              soarInputText.requestFocus();





        soarSendButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            String msg = textField.getText().trim();
            if(msg.length() == 0){
              return;
            }
            add(msg);
            sendMessageToRosie("CMD: " + msg);
          }
        });

        JSplitPane pane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                soarInputText, soarSendButton);
        JSplitPane pane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane,
                pane2);

        pane1.setDividerLocation(325);
        pane2.setDividerLocation(600);
        
        this.setLayout(new BorderLayout());
        this.add(pane1, BorderLayout.CENTER);
        this.setSize(800, 450);

        this.setVisible(true);

    }

    
    /*******************************
     * Setup Styles for how messages look
     */
    
    private void setupStyles(StyledDocument doc) {
      // defaultStyle - Base style used by others
      Style defaultStyle = doc.addStyle("DEFAULT", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);
        StyleConstants.setFontSize(defaultStyle, 24);
        StyleConstants.setFontFamily(defaultStyle, "SansSerif");
        StyleConstants.setLineSpacing(defaultStyle, 1f);

      // agentStyle - Messages produced by the agent
      Style agentStyle = doc.addStyle(MessageType.AGENT_MESSAGE.toString(), defaultStyle);
        StyleConstants.setForeground(agentStyle, Color.BLACK);
        StyleConstants.setItalic(agentStyle, true);
        StyleConstants.setFontFamily(agentStyle, "Serif");
      
      // instructorStyle - Messages typed by the user
        Style instructorStyle = doc.addStyle(MessageType.INSTRUCTOR_MESSAGE.toString(), defaultStyle);
        StyleConstants.setForeground(instructorStyle, Color.BLACK);

      // soarOutputStyle - output from soar
        Style soarOutputStyle = doc.addStyle(MessageType.SOAR_COMMAND.toString(), defaultStyle);
        StyleConstants.setForeground(soarOutputStyle,  Color.BLACK);
        StyleConstants.setFontSize(soarOutputStyle, 16);
    }    

    public static void main(String[] args){
      System.out.println(args.length);
      if(args.length < 1){
        new RemoteTerminal("localhost");
      } else {
        new RemoteTerminal(args[0]);
      }
    }
}