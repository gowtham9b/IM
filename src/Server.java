import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Server extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public Server(){
		super("Gowtham's Instant Messenger");
		userText=new JTextField();
		userText.setEditable(false);		//before connection, no typing allowed
		userText.addActionListener(
			new ActionListener() {		
				@Override
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText(""); 		//clears the messaging area.
				}
			}
		);
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(400,200);
		setVisible(true);
	}
	
	//set up and run the server
	public void startRunning() throws IOException {
		
		server =new ServerSocket(5223,100);
		while(true){
			try{
				waitForConnection();
				setUpStreams();
				whileChatting();
			}catch(EOFException eofException){
				showMessage("\n Server ended the connection");
			}finally{
				closeAll();
			}
		}
	}
	
	//wait for connection, then display connection information
	private void waitForConnection() throws IOException {
		showMessage("Waiting for connection...\n");
		connection=server.accept();
		showMessage("Connection complete with "+connection.getInetAddress().getHostName());
	}
	
	//get streams to send and receive data
	private void setUpStreams() throws IOException {
		output=new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are now set-up!\n");
	}
	
	
	//during the chat conversation
	private void whileChatting() throws IOException {
		String message="You're now connected!";
		sendMessage(message);
		ableToType(true);
		do{
			//have a conversation
			try{
				message=(String) input.readObject();
				showMessage("\n"+ message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n idk wtf user sent!");
			}
		}while(!message.equals(("CLIENT - END")));
	}
	
	//house-keeping method. Close all the stuff
	private void closeAll(){
		showMessage("\nClosing connections...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send a message to client
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n ERROR : I CANT SEND THAT MESSAGE");
		}
	}
	
	//updates chatWindow
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(text);
				}
			}
		);
	}
	
	//let the user type into the box
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
}
