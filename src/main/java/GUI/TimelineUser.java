package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;

import model.User;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JTextArea;

public class TimelineUser {

	private JFrame frame;
	JLabel lblTimelineUtente;
	JTextArea txtTimeline;
	

	/**
	 * Launch the application.
	 */
	public static void timelineuser(final User user) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TimelineUser window = new TimelineUser();
					window.frame.setVisible(true);
					window.txtTimeline.append(user.getTimelineUser());
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
	public TimelineUser() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100,1000, 600);
		frame.getContentPane().setLayout(null);
		
		lblTimelineUtente = new JLabel("Timeline user: ");
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
