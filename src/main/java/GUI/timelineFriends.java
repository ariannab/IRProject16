package GUI;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import model.User;
import javax.swing.JScrollPane;

public class timelineFriends {

	private JFrame frame;
	JLabel lblTimelineUtente;
	JTextArea txtTimeline;

	/**
	 * Launch the application.
	 */
	public static void timelineFriends(final User user) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					timelineFriends window = new timelineFriends();
					window.frame.setVisible(true);
					for(String status : user.getTimelineFriends() ){
						window.txtTimeline.append(status);
					}
					window.lblTimelineUtente.setText(window.lblTimelineUtente.getText() + user.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public timelineFriends() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 600);
		frame.getContentPane().setLayout(null);
		
		lblTimelineUtente = new JLabel("Timeline friend's user :  ");
		lblTimelineUtente.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		lblTimelineUtente.setBounds(258, 36, 469, 16);
		frame.getContentPane().add(lblTimelineUtente);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 64, 988, 495);
		frame.getContentPane().add(scrollPane);
		
		txtTimeline = new JTextArea();
		scrollPane.setViewportView(txtTimeline);
		txtTimeline.setLineWrap(true);
		txtTimeline.setEditable(false);
	}
}
