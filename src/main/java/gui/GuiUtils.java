package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;

import model.RankingArticle;
import model.User;
import twitter4j.TwitterException;

import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

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
	private JLabel txtUsername_2;
	private JTextPane fTextArea;
	private JTextPane textPane;
	private JScrollPane fScrollPane;
	private JButton timeline;
	
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
					    String title = article.getTitle().replaceAll("&#039;", "'").replaceAll("’?", "'");
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
				List<String> utimeline = user.getUstats().getTerms();
				List<String> ftimeline = user.getFstats().getTerms();
				List<String> intersection = new ArrayList<String>(utimeline); 
				intersection.retainAll(ftimeline);
				
				List<Integer> ufreq = user.getUstats().getFreq();
				List<Integer> ffreq = user.getFstats().getFreq();
				
				Style style = window.userTextArea.addStyle("Colored Style", null);
		        StyleConstants.setForeground(style, Color.decode("#0000cc"));
		        StyleConstants.setBold(style, true);
				
				StyledDocument udoc = window.userTextArea.getStyledDocument();
				for(String utag : utimeline){
					if(i==0){
						try {
							udoc.insertString(udoc.getLength(), String.format("%-11s%-30s%-4s%n%n", "Rank", "Term", "Frequency"), bold);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
					if(intersection.contains(utag))
						try {
							udoc.insertString(udoc.getLength(), String.format("%1s%-10d%-30s%-4s%n%n","#", i+1, utag, ufreq.get(i)), style);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					else
						try {
							udoc.insertString(udoc.getLength(), String.format("%1s%-10d%-30s%-4s%n%n","#", +i+1, utag, ufreq.get(i)), null);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					i++;
				}
				window.userTextArea.setCaretPosition(0);
				window.txtUsername_1.setText(user.getName() + window.txtUsername_1.getText());
				
				StyledDocument fdoc = window.fTextArea.getStyledDocument();
				i = 0;
				for(String ftag : ftimeline){
					if(i==0){
						try {
							fdoc.insertString(fdoc.getLength(), String.format("%-11s%-30s%-4s%n%n", "Rank", "Term", "Frequency"), bold);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
					if(intersection.contains(ftag))
						try {
							fdoc.insertString(fdoc.getLength(), String.format("%1s%-10d%-30s%-4s%n%n","#", i+1, ftag, ffreq.get(i)), style);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					else
						try {
							fdoc.insertString(fdoc.getLength(), String.format("%1s%-10d%-30s%-4s%n%n","#", i+1, ftag, ffreq.get(i)), null);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					i++;
				}
				window.fTextArea.setCaretPosition(0);
				window.txtUsername_2.setText("Friends ("+user.getTotFriends()+")"+window.txtUsername_2.getText());			
				
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
		frame.setBounds(500, 500, 1368, 768);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(new Color(192, 192, 192));
		tabbedPane.setBounds(0, 26, 1352, 697);
		frame.getContentPane().add(tabbedPane);
		
		panel = new JPanel();
		panel.setBackground(new Color(245, 245, 245));
		tabbedPane.addTab("Recommended News", null, panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {54, 450, 562, 50, 0};
		gbl_panel.rowHeights = new int[] {100, 488, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		timeline = new JButton("");
		timeline.setToolTipText("Click to open Twitter profile");
		timeline.setOpaque(false);
		timeline.setContentAreaFilled(false);
		timeline.setBorderPainted(false);
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		    Image img = ImageIO.read(classloader.getResource("twitter.png"));
			timeline.setIcon(new ImageIcon(img));
		} catch (Exception ex) {
		    System.out.println(ex);
		}
		GridBagConstraints gbc_timeline = new GridBagConstraints();
		gbc_timeline.anchor = GridBagConstraints.EAST;
		gbc_timeline.ipadx = 60;
		gbc_timeline.fill = GridBagConstraints.VERTICAL;
		gbc_timeline.insets = new Insets(0, 0, 5, 5);
		gbc_timeline.gridx = 0;
		gbc_timeline.gridy = 0;
		panel.add(timeline, gbc_timeline);
		timeline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					openWebpage(new URL("https://twitter.com/" + txtUser));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		});
		timeline.setBackground(new Color(178, 184, 255));
		
		txtUsername = new JLabel("");
		txtUsername.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		GridBagConstraints gbc_txtUsername = new GridBagConstraints();
		gbc_txtUsername.fill = GridBagConstraints.BOTH;
		gbc_txtUsername.insets = new Insets(0, 0, 5, 5);
		gbc_txtUsername.gridx = 1;
		gbc_txtUsername.gridy = 0;
		panel.add(txtUsername, gbc_txtUsername);
		
		JButton button = new JButton("");
		button.setIcon(new ImageIcon(GuiUtils.class.getResource("/com/sun/javafx/scene/control/skin/caspian/dialog-confirm.png")));
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File("./explanations"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setToolTipText("Click to open scores' explanations folder");
		button.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 14));
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.fill = GridBagConstraints.VERTICAL;
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.gridx = 4;
		gbc_button.gridy = 0;
		panel.add(button, gbc_button);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panel.add(scrollPane, gbc_scrollPane);
		
		textPane = new JTextPane();
		textPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
		scrollPane.setViewportView(textPane);
		
		//------------User's && friends' terms
		termsPanel = new JPanel();
		termsPanel.setBackground(new Color(245, 245, 245));
		tabbedPane.addTab("Terms Ranking", null, termsPanel, null);
		GridBagLayout gbl_termsPanel = new GridBagLayout();
		gbl_termsPanel.columnWidths = new int[]{672, 672, 0};
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
		
		txtUsername_2 = new JLabel(" most frequent terms:");
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
		userTextArea.setFont(new Font("Monospaced", userTextArea.getFont().getStyle(), 16));
		uScrollPane.setViewportView(userTextArea);
		
		fScrollPane = new JScrollPane();
		GridBagConstraints gbc_fScrollPane = new GridBagConstraints();
		gbc_fScrollPane.fill = GridBagConstraints.BOTH;
		gbc_fScrollPane.gridx = 1;
		gbc_fScrollPane.gridy = 1;
		termsPanel.add(fScrollPane, gbc_fScrollPane);
		
		fTextArea = new JTextPane();
		fTextArea.setFont(new Font("Monospaced", fTextArea.getFont().getStyle(), 16));
		fScrollPane.setViewportView(fTextArea);
		
	}
	public static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	public static void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}
}
