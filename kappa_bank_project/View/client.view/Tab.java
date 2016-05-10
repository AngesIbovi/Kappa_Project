package client.view;

import java.net.Socket;

import javax.swing.JPanel;

@SuppressWarnings("serial") // Is not going to be serialized
public abstract class Tab extends JPanel {
	public final String name;
	public final int authorizationLevel;
	protected Socket S;
	
	public Tab(String name, int authorizationLevel) {
		this.name = name;
		this.authorizationLevel = authorizationLevel;
	}
	
	public void setSocket(Socket S) {
		this.S=S;
	}
}
