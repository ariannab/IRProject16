package gui;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GuiUtils {
	static String txtUser;
	private JFrame frame;
	private JPanel panel;
	private JLabel txtUsername;
	private JScrollPane scrollPane;
	//------User
	private JPanel termsPanel;
	private JLabel txtUsername_1;
	private JScrollPane uScrollPane;
	private JTextPane userTextArea;
	//------Friends
	private JPanel panel_2;
	private JLabel txtUsername_2;
	private JScrollPane scrollPane_3;
	private JTextPane fTextArea;
	private JTextPane textPane;
	private JLabel label;
	private JScrollPane fScrollPane;
	
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
				StyledDocument doc = window.textPane.getStyledDocument();
				SimpleAttributeSet bold = new SimpleAttributeSet();
				StyleConstants.setBold(bold, true);
				for(RankingArticle article : user.getRankingArticle() ){
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
					window.textPane.setSize(1100, 1000);
					window.textPane.setCaretPosition(0);
				}
				i = 0;
//				List<String> utimeline = user.getTimelineUser();
//				List<String> ftimeline = user.getTimelineFriends();
				
				List<String> utimeline = user.getUstats().getTerms();
				List<String> ftimeline = user.getFstats().getTerms();
				List<String> intersection = new ArrayList<String>(utimeline); 
				intersection.retainAll(ftimeline);
				
				List<Integer> ufreq = user.getUstats().getFreq();
				List<Integer> ffreq = user.getFstats().getFreq();
				
				StyledDocument udoc = window.userTextArea.getStyledDocument();
				for(String utag : utimeline){					
					if(intersection.contains(utag))
						try {
							udoc.insertString(udoc.getLength(), "#"+(i+1)+" : "+utag+"			"+ufreq.get(i)+"\n\n", bold);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else
						try {
							udoc.insertString(udoc.getLength(), "#"+(i+1)+" : "+utag+"			"+ufreq.get(i)+"\n\n", null);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					i++;
				}
				window.userTextArea.setCaretPosition(0);
//					window.textArea_1.append(user.getTimelineUser());
				window.txtUsername_1.setText(user.getName() + window.txtUsername_1.getText());
				
				StyledDocument fdoc = window.fTextArea.getStyledDocument();
				i = 0;
				for(String ftag : ftimeline){
					if(intersection.contains(ftag))
						try {
							fdoc.insertString(fdoc.getLength(), "#"+(i+1)+" : "+ftag+"			"+ffreq.get(i)+" \n\n", bold);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else
						try {
							fdoc.insertString(fdoc.getLength(), "#"+(i+1)+" : "+ftag+"			"+ffreq.get(i)+" \n\n", null);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
		//------------User's && friends' terms
		termsPanel = new JPanel();
		tabbedPane.addTab("User", null, termsPanel, null);
		GridBagLayout gbl_termsPanel = new GridBagLayout();
		gbl_termsPanel.columnWidths = new int[]{590, 590, 0};
		gbl_termsPanel.rowHeights = new int[] {100, 304, 0};
		gbl_termsPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_termsPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		termsPanel.setLayout(gbl_termsPanel);
		
		txtUsername_1 = new JLabel("'s most frequent terms:");
		txtUsername_1.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		GridBagConstraints gbc_txtUsername_1 = new GridBagConstraints();
		gbc_txtUsername_1.insets = new Insets(0, 0, 5, 5);
		gbc_txtUsername_1.gridx = 0;
		gbc_txtUsername_1.gridy = 0;
		termsPanel.add(txtUsername_1, gbc_txtUsername_1);
		
		txtUsername_2 = new JLabel("'s friends most frequent terms:");
		txtUsername_2.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		txtUsername_2.setBounds(38, 18, 679, 23);
		GridBagConstraints gbc_txtUsername_2 = new GridBagConstraints();
		gbc_txtUsername_2.insets = new Insets(0, 0, 5, 0);
		gbc_txtUsername_2.gridx = 1;
		gbc_txtUsername_2.gridy = 0;
		termsPanel.add(txtUsername_2, gbc_txtUsername_2);
		
		uScrollPane = new JScrollPane();
		GridBagConstraints gbc_uScrollPane = new GridBagConstraints();
		gbc_uScrollPane.fill = GridBagConstraints.BOTH;
		gbc_uScrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_uScrollPane.gridx = 0;
		gbc_uScrollPane.gridy = 1;   
		termsPanel.add(uScrollPane, gbc_uScrollPane);
		
		userTextArea = new JTextPane();
		uScrollPane.setViewportView(userTextArea);
		
		fScrollPane = new JScrollPane();
		GridBagConstraints gbc_fScrollPane = new GridBagConstraints();
		gbc_fScrollPane.fill = GridBagConstraints.BOTH;
		gbc_fScrollPane.gridx = 1;
		gbc_fScrollPane.gridy = 1;
		termsPanel.add(fScrollPane, gbc_fScrollPane);
		
		fTextArea = new JTextPane();
		fScrollPane.setViewportView(fTextArea);
		
	}
}
