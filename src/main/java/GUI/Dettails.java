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
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;

public class Dettails {

	private JFrame frame;
	private JLabel txtUsername;
	User user;
	private JScrollPane scrollPane;
	private JTextArea textArea;

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
					timelineFriends.timelineFriends(user);
					window.txtUsername.setText("User: " + user.getName());
					int i = 1;
					for(String article : user.getRankingArticle() ){
						window.textArea.append(i + ") " + article + "\n");
						i++;
					}
					

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
		frame.setBounds(500, 500, 1000, 666);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtUsername = new JLabel("");
		txtUsername.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtUsername.setBounds(29, 21, 235, 25);
		frame.getContentPane().add(txtUsername);
		System.out.println("Creo username");
		
		JLabel lblLink = new JLabel("Link");
		lblLink.setBounds(297, 30, 61, 16);
		frame.getContentPane().add(lblLink);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 77, 988, 534);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		

	}
}
