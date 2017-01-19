package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import model.User;
import twitter4j.TwitterException;

import javax.swing.JButton;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JOptionPane;
import analysis.Indexing;
import analysis.Querying;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;

public class GuiMain {

	JFrame frameMain;
	List<String> users;
	java.awt.List listUsers;
	JTextField txtAddUser;
	JButton btnAddUser;
	static JButton btnSearchNews;
	private JPanel panel_1;
	private JCheckBox refreshN;
	private JCheckBox refreshP;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException 
	 * @throws TwitterException 
	 */
	public static void main(String[] args) throws TwitterException, IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
					GuiMain window = new GuiMain();
					window.frameMain.setVisible(true);
					window.users = new ArrayList<String>();
					window.users = utils.TwitterBootUtils.loadUsernames();
					for(String user : window.users){
						window.listUsers.add(user);
					}					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameMain = new JFrame();
		frameMain.setFont(new Font("Dialog", Font.BOLD, 16));
		frameMain.setBackground(Color.LIGHT_GRAY);
		frameMain.setTitle("News Retriever");
		frameMain.setBounds(100, 100, 512, 426);
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMain.getContentPane().setLayout(null);
		
		btnSearchNews = new JButton("Search News");
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try {
			Image img = ImageIO.read(classloader.getResource("res/img/search.png"));
			btnSearchNews.setIcon(new ImageIcon(img));
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		btnSearchNews.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnSearchNews.setBounds(180, 339, 151, 42);
		frameMain.getContentPane().add(btnSearchNews);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Users List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 68, 241, 261);
		frameMain.getContentPane().add(panel);
		panel.setLayout(null);
		
		
		listUsers = new java.awt.List();
		listUsers.setFont(new Font("Courier New", Font.PLAIN, 14));
		listUsers.setBounds(10, 18, 221, 233);
		panel.add(listUsers);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Add Twitter Account", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		panel_1.setBounds(262, 199, 198, 130);
		frameMain.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		txtAddUser = new JTextField();
		txtAddUser.setBounds(20, 29, 160, 32);
		panel_1.add(txtAddUser);
		txtAddUser.setToolTipText("Username");
		txtAddUser.setColumns(10);
		
		btnAddUser = new JButton("Append");
		btnAddUser.setToolTipText("<html>\r\nIf you want to profile a different Twitter user,<br>\r\ntype his username and append it to the list\r\n</html>");
		btnAddUser.setBounds(20, 73, 160, 32);
		panel_1.add(btnAddUser);
		btnAddUser.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String newUser = txtAddUser.getText();
				if(!newUser.equals("")) {
					listUsers.add(newUser);
					txtAddUser.setText("");
				}else{
					JOptionPane.showMessageDialog(frameMain,
						    "Username can't be empty!",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		
		JLabel lblNewLabel = new JLabel("Select user in the list below, then search:");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		lblNewLabel.setToolTipText("Select a user from \"Users' List\", than click the \"Search News\" button");
		lblNewLabel.setBounds(16, 17, 321, 28);
		frameMain.getContentPane().add(lblNewLabel);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "User's Content Boost", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(259, 68, 201, 78);
		frameMain.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		final JComboBox<String> boostSelect = new JComboBox<String>();
		boostSelect.setBounds(18, 26, 160, 32);
		panel_2.add(boostSelect);
		boostSelect.setToolTipText("<html>\r\nBy default, user terms are ALWAYS on top of his <br>\r\nfriends' no matter what! Or, you can choose to   <br>\r\ngive user a slighter boost.\r\n</html>\r\r\n");
		boostSelect.setModel(new DefaultComboBoxModel<String>(new String[] {"Absolute", "3", "2"}));
		boostSelect.setSelectedIndex(0);
		
		refreshN = new JCheckBox("Refresh News");
		refreshN.setToolTipText("If checked, news index will be rebuilt");
		refreshN.setBounds(269, 146, 118, 18);
		frameMain.getContentPane().add(refreshN);
		
		refreshP = new JCheckBox("Refresh Profile");
		refreshP.setToolTipText("If checked, user profile will be rebuilt");
		refreshP.setBounds(269, 169, 104, 18);
		frameMain.getContentPane().add(refreshP);
		
		
		
		btnSearchNews.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {				
				if(listUsers.getSelectedIndex()== -1){
					JOptionPane.showMessageDialog(frameMain,
						    "Select an user",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
				else{					
					try {
						ClassLoader classloader = Thread.currentThread().getContextClassLoader();
					    Image img = ImageIO.read(classloader.getResource("res/img/refresh.png"));
					    btnSearchNews.setIcon(new ImageIcon(img));
					} catch (Exception ex) {
					}
					btnSearchNews.setText("Searching...");
					
					JOptionPane.showMessageDialog(frameMain,
						    "Retrieving data: may take a few seconds",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
					String txtUser = listUsers.getSelectedItem().toString();
					try {	
						//check if indexes already exist locally, if not, build them
						//...unless explicit refreshing is required
						
						Path artIndex = null;
						User user = null;
						if(!refreshN.isSelected())
							artIndex = Files.exists(Paths.get("./indexes/news_index")) ? Paths.get("./indexes/news_index") : Indexing.buildNewsIndex();
						else
							artIndex = Indexing.buildNewsIndex();						
							
						if(!refreshP.isSelected())
							user = Files.exists(Paths.get("./indexes/profiles/"+txtUser)) ? Indexing.readUserIndex(txtUser) : Indexing.buildUserIndex(txtUser);
						else
							user = Indexing.buildUserIndex(txtUser);						
						
						try {				
							boolean alwaysTop = boostSelect.getSelectedIndex()==0;
							if(!alwaysTop){
								float uboost= Float.valueOf((String) boostSelect.getSelectedItem());								
								Querying.setUboost(uboost);
							}								
							user.setRankingArticle(Querying.makeQuery(user.getUser_index_path(), artIndex, alwaysTop));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						GuiUtils.printUserDetails(user);
					} catch (IOException e2) {
						e2.printStackTrace();
					} catch (TwitterException e1) {
						e1.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}					
				}
			}
		});		
	}
}
