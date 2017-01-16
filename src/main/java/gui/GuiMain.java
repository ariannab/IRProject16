package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import model.User;
import twitter4j.TwitterException;

import javax.swing.JButton;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JOptionPane;
import org.apache.lucene.analysis.custom.CustomAnalyzer;

import analysis.CustomAnalyzerFactory;
import analysis.Indexing;
import analysis.Querying;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Window.Type;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridLayout;

public class GuiMain {

	JFrame frameMain;
	List<String> users;
	java.awt.List listUsers;
	JTextField txtAddUser;
	JButton btnAddUser;
	private JButton btnSearchNews;
	private JPanel panel_1;

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
					System.out.println("Entered GUI");
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
		frameMain.setBounds(100, 100, 467, 319);
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMain.getContentPane().setLayout(null);
		
		btnSearchNews = new JButton("Search News");
		btnSearchNews.setBounds(264, 86, 161, 32);
		frameMain.getContentPane().add(btnSearchNews);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Users List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 68, 224, 199);
		frameMain.getContentPane().add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		
		listUsers = new java.awt.List();
		panel.add(listUsers);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Add Twitter Account", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(59, 59, 59)));
		panel_1.setBounds(244, 130, 201, 137);
		frameMain.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		txtAddUser = new JTextField();
		txtAddUser.setBounds(20, 28, 160, 32);
		panel_1.add(txtAddUser);
		txtAddUser.setToolTipText("Username");
		txtAddUser.setColumns(10);
		
		btnAddUser = new JButton("Add User");
		btnAddUser.setBounds(20, 78, 160, 32);
		panel_1.add(btnAddUser);
		
		JLabel lblNewLabel = new JLabel("Select user in the list below, then search:");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		lblNewLabel.setToolTipText("Select a user from \"Users' List\", than click the \"Search News\" button");
		lblNewLabel.setBounds(17, 28, 337, 28);
		frameMain.getContentPane().add(lblNewLabel);
		btnAddUser.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				listUsers.add(txtAddUser.getText());
				txtAddUser.setText("");
				
			}
		});
		btnSearchNews.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.out.println("Selected: "+listUsers.getSelectedIndex());
				if(listUsers.getSelectedIndex()== -1){
					JOptionPane.showMessageDialog(frameMain,
						    "Select an user",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
				else{
					JOptionPane.showMessageDialog(frameMain,
						    "Retrieving data: may take a few seconds",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
					String txtUser = listUsers.getSelectedItem().toString();
					try {						
//						Path artIndex = Indexing.buildNewsIndex();
						Path artIndex = Paths.get("./indexes/article_index");
						User user = null;
						try {
							//found the user locally: read his profile
							user = Indexing.readUserIndex(txtUser);
						} catch (Exception e1) {
							try {
								//user not found locally: build his profile
								user = Indexing.buildUserIndex(txtUser);
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
						
						CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
						try {
							user.setRankingArticle(Querying.makeQuery(user.getUser_index_path(), artIndex));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						analyzer.close();
						GuiUtils.printUserDetails(user);
					} catch (IOException e2) {
						e2.printStackTrace();
					} catch (TwitterException e1) {
						e1.printStackTrace();
					}					
				}
			}
		});		
	}
}
