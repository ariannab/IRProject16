package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import analyzers.CustomAnalyzerFactory;
import analyzers.Indexing;
import analyzers.Querying;
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

import javax.swing.JTextField;

public class GuiMain {

	JFrame frameMain;
	JLabel lblNewLabel_listOfUsers;
	List<String> users;
	java.awt.List listUsers;
	JTextField txtAddUser;
	JButton btnAddUser;
	private JButton btnSearchNews;

	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws TwitterException 
	 */
	public static void main(String[] args) throws TwitterException, IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
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
		frameMain.setBounds(100, 100, 600, 400);
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMain.getContentPane().setLayout(null);
		
		lblNewLabel_listOfUsers = new JLabel("List of users");
		lblNewLabel_listOfUsers.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		lblNewLabel_listOfUsers.setBounds(46, 38, 131, 22);
		frameMain.getContentPane().add(lblNewLabel_listOfUsers);
		
		
		
		listUsers = new java.awt.List();
		listUsers.setBounds(46, 88, 179, 171);
		frameMain.getContentPane().add(listUsers);
		
		txtAddUser = new JTextField();
		txtAddUser.setToolTipText("Username");
		txtAddUser.setBounds(289, 139, 134, 28);
		frameMain.getContentPane().add(txtAddUser);
		txtAddUser.setColumns(10);
		
		btnAddUser = new JButton("Add user");
		btnAddUser.setBounds(306, 98, 117, 29);
		frameMain.getContentPane().add(btnAddUser);
		btnAddUser.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				listUsers.add(txtAddUser.getText());
				txtAddUser.setText("");
				
			}
		});
		
		btnSearchNews = new JButton("Search News");
		btnSearchNews.setBounds(449, 98, 117, 29);
		frameMain.getContentPane().add(btnSearchNews);
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
						    "Wait few seconds for result",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
					String txtuser = listUsers.getSelectedItem().toString();
					try {
						
						Path artIndex = Indexing.buildNewsIndex();
//						Path artIndex = Paths.get("./indexes/article_index");
						User user = Indexing.buildUserIndex(txtuser);
						CustomAnalyzer analyzer = CustomAnalyzerFactory.buildTweetAnalyzer();
						user.setRankingArticle(Querying.makeQuery(user.getUser_index_path(), artIndex));
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
