package GUI;

import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.lucene.analysis.custom.CustomAnalyzer;

import analyzers.CustomAnalyzerFactory;
import analyzers.Indexing;
import analyzers.Querying;
import model.User;
import twitter4j.TwitterException;

import java.awt.BorderLayout;
import javax.swing.JTextArea;

public class Dettails {

	private JFrame frame;
	static JLabel txtUsername;
	User user;
	static String txtUser;
	static JTextArea txtTimeline;
	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws TwitterException 
	 */
	public static void dettails(final User user) throws IOException, TwitterException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Dettails window = new Dettails();
				window.frame.setVisible(true);
				
					System.out.println("-------------------");
					System.out.println(user.getTimelineUser());
					TimelineUser.timelineuser(user);
					txtTimeline.append("Qua articoli suggeriti");
			}
			
		});
		
	}

	/**
	 * Create the application.
	 */
	public Dettails() {
		System.out.println("Entro inizalizzazione");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(500, 500, 1000, 1000);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtUsername = new JLabel("");
		txtUsername.setBounds(29, 21, 235, 25);
		frame.getContentPane().add(txtUsername);
		System.out.println("Creo username");
		txtUsername.setText(txtUser);
		
		JLabel lblLink = new JLabel("Link");
		lblLink.setBounds(297, 30, 61, 16);
		frame.getContentPane().add(lblLink);
		
		txtTimeline = new JTextArea();
		txtTimeline.setBounds(6, 81, 988, 878);
		frame.getContentPane().add(txtTimeline);
		System.out.println("Finito username");

	}
}
