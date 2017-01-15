package gui;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

import model.RankingArticle;
import model.User;
import twitter4j.TwitterException;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GuiUtils {
	static String txtUser;
	private JFrame frame;
	private JPanel panel;
	private JLabel txtUsername;
	private JScrollPane scrollPane;
	//------User
	private JPanel panel_1;
	private JLabel txtUsername_1;
	private JScrollPane scrollPane_1;
	private JTextArea userTextArea;
	//------Friends
	private JPanel panel_2;
	private JLabel txtUsername_2;
	private JScrollPane scrollPane_2;
	private JTextArea fTextArea;
	private static JTextPane textPane;
	
	/**
	 * Show User details in GUI: recommended news, his timeline and his friends'
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
				window.frame.setLocationRelativeTo(null);
				
//				System.out.println("-------------------");
//				System.out.println(user.getTimelineUser());
				//TimelineUser.timelineuser(user);
				//timelineFriends.timelineFriends(user);
				window.txtUsername.setText(user.getName() + "'s suggested news:");
				int i = 1;
				StyledDocument doc = textPane.getStyledDocument();
				for(RankingArticle article : user.getRankingArticle() ){
//					window.textArea.append(i + ") " + article.replaceAll("â€™?‹?", "'").replace("Â", "") + "\n");
					SimpleAttributeSet bold = new SimpleAttributeSet();
					StyleConstants.setBold(bold, true);
					try
					{
					    String title = article.getTitle().replaceAll("&#039;", "'");
					    doc.insertString(doc.getLength(), i + ")"+" Title: ",bold );
					    doc.insertString(doc.getLength(), "<"+title+">", null );
					    doc.insertString(doc.getLength(), " *** Source: ", bold );
					    doc.insertString(doc.getLength(), "<"+article.getSource()+">", null );
					    doc.insertString(doc.getLength(), " *** Score: ", bold );
					    doc.insertString(doc.getLength(), "<"+article.getScore()+">"+"\n\n", null );
					    i++;
					}
					catch(Exception e) { System.out.println(e); }
					
					/*window.textPane.append(i + ") ");						
					String title = article.getTitle().replaceAll("â€™?‹?", "'").replace("Â", "");
					window.textPane.append("Title: " + title);
					window.textPane.append("	Source: " + article.getSource());
					window.textPane.append("	Score: " + article.getScore() + "\n\n");	*/
					window.textPane.setSize(1100, 1000);
					window.textPane.setCaretPosition(0);
				}
				i = 1;
				for(String utag : user.getTimelineUser() ){
					window.userTextArea.append("#"+i+" : "+utag+"\n\n");
					i++;
				}
				window.userTextArea.setCaretPosition(0);
//					window.textArea_1.append(user.getTimelineUser());
				window.txtUsername_1.setText(user.getName() + window.txtUsername_1.getText());
				i = 1;
				for(String ftag : user.getTimelineFriends() ){
					window.fTextArea.append("#"+i+" : "+ftag+"\n\n");
					i++;
				}
				window.fTextArea.setCaretPosition(0);
				window.txtUsername_2.setText(user.getName() + window.txtUsername_2.getText());			
				
			}
			
		});
		
	}

	/**
	 * Create the application.
	 */
	public GuiUtils() {
		System.out.println("Enter initialization");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(500, 500, 1200, 670);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 1180, 638);
		frame.getContentPane().add(tabbedPane);
		
		panel = new JPanel();
		tabbedPane.addTab("News", null, panel, null);
		panel.setLayout(null);
		
		txtUsername = new JLabel("");
		txtUsername.setBounds(38, 18, 457, 23);
		txtUsername.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		panel.add(txtUsername);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 52, 1147, 534);
		panel.add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(6, 31, 10, 10);
		panel.add(panel_3);
		//------------Timeline User
		panel_1 = new JPanel();
		tabbedPane.addTab("User", null, panel_1, null);
		panel_1.setLayout(null);
		
		txtUsername_1 = new JLabel("'s most frequent tags:");
		txtUsername_1.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtUsername_1.setBounds(38, 18, 642, 23);
		panel_1.add(txtUsername_1);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 52, 1147, 534);
		panel_1.add(scrollPane_1);
		
		userTextArea = new JTextArea();
		scrollPane_1.setViewportView(userTextArea);
		
		//------------Timeline Friends
		panel_2 = new JPanel();
		tabbedPane.addTab("Friends", null, panel_2, null);
		panel_2.setLayout(null);
				
		txtUsername_2 = new JLabel("'s friends most frequent tags:");
		txtUsername_2.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtUsername_2.setBounds(38, 18, 679, 23);
		panel_2.add(txtUsername_2);
				
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(6, 52, 1147, 534);
		panel_2.add(scrollPane_2);
				
		fTextArea = new JTextArea();
		scrollPane_2.setViewportView(fTextArea);
	}
}
