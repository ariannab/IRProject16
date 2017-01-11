package gui;

import java.awt.EventQueue;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;

import model.User;
import twitter4j.TwitterException;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

public class GuiUtils {
	static String txtUser;
	private JFrame frame;
	private JPanel panel;
	private JLabel txtUsername;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	//------User
	private JPanel panel_1;
	private JLabel txtUsername_1;
	private JScrollPane scrollPane_1;
	private JTextArea textArea_1;
	//------Friends
	private JPanel panel_2;
	private JLabel txtUsername_2;
	private JScrollPane scrollPane_2;
	private JTextArea textArea_2;
	/**
	 * Show User details in GUI, as his timeline and his friends'
	 * 
	 * @throws IOException 
	 * @throws TwitterException 
	 */
	public static void printUserDetails(final User user) throws IOException, TwitterException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				txtUser = user.getName();
				GuiUtils window = new GuiUtils();
				window.frame.setVisible(true);
				
					System.out.println("-------------------");
					System.out.println(user.getTimelineUser());
					//TimelineUser.timelineuser(user);
					//timelineFriends.timelineFriends(user);
					window.txtUsername.setText(user.getName() + "'s suggested news:");
					int i = 1;
					for(String article : user.getRankingArticle() ){
						window.textArea.append(i + ") " + article + "\n");
						i++;
					}
					// User Timeline
					window.textArea_1.append(user.getTimelineUser());
					window.txtUsername_1.setText(user.getName() + window.txtUsername_1.getText());
					// User's Friends Timeline
					for(String status : user.getTimelineFriends() ){
						window.textArea_2.append(status);
					}
					window.txtUsername_2.setText(user.getName() + window.txtUsername_2.getText());			

			}
			
		});
		
	}

	/**
	 * Create the application.
	 */
	public GuiUtils() {
		System.out.println("Entro inizalizzazione");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(500, 500, 1200, 666);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 1180, 638);
		frame.getContentPane().add(tabbedPane);
		
		panel = new JPanel();
		tabbedPane.addTab("News", null, panel, null);
		panel.setLayout(null);
		
		txtUsername = new JLabel("");
		txtUsername.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtUsername.setBounds(38, 18, 273, 23);
		panel.add(txtUsername);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 52, 1147, 534);
		panel.add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		//------------Timeline User
		panel_1 = new JPanel();
		tabbedPane.addTab("User", null, panel_1, null);
		panel_1.setLayout(null);
		
		txtUsername_1 = new JLabel("'s timeline: ");
		txtUsername_1.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtUsername_1.setBounds(38, 18, 273, 23);
		panel_1.add(txtUsername_1);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 52, 1147, 534);
		panel_1.add(scrollPane_1);
		
		textArea_1 = new JTextArea();
		scrollPane_1.setViewportView(textArea_1);
		
		//------------Timeline Friends
		panel_2 = new JPanel();
		tabbedPane.addTab("Friends", null, panel_2, null);
		panel_2.setLayout(null);
				
		txtUsername_2 = new JLabel("'s friends timeline: ");
		txtUsername_2.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtUsername_2.setBounds(38, 18, 273, 23);
		panel_2.add(txtUsername_2);
				
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(6, 52, 1147, 534);
		panel_2.add(scrollPane_2);
				
		textArea_2 = new JTextArea();
		scrollPane_2.setViewportView(textArea_2);
	}
}
