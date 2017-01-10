package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class Dettails {

	private JFrame frame;
	static JLabel txtUsername;

	/**
	 * Launch the application.
	 */
	public static void dettails(final String user) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dettails window = new Dettails();
					window.frame.setVisible(true);
					txtUsername.setText(user);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Dettails() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		txtUsername = new JLabel("");
		frame.getContentPane().add(txtUsername, BorderLayout.NORTH);
	}

}
