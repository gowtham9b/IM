import java.io.IOException;

import javax.swing.JFrame;

public class ServerTest {

	public static void main(String[] args) throws IOException{
		Server s=new Server();
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.startRunning();
	}
}
