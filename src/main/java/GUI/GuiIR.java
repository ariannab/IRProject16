package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;

import analyzers.Indexing;
import twitter4j.TwitterException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JTextField;

public class GuiIR {

	static JFrame frameMain;
	static JLabel lblNewLabel_listOfUsers;
	static List<String> users;
	static java.awt.List listUsers;
	static JTextField txtAddUser;
	static JButton btnAddUser;

	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws TwitterException 
	 */
	public static void main(String[] args) throws TwitterException, IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiIR window = new GuiIR();
					window.frameMain.setVisible(true);
					users = new ArrayList<String>();
					users = utils.TwitterBootUtils.loadUsernames();
					DefaultListModel dlm = new DefaultListModel();
					System.out.println("Entra");
					for(String user : users){
						System.out.println(user);
						listUsers.add(user);
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
	public GuiIR() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameMain = new JFrame();
		frameMain.setBounds(100, 100, 800, 800);
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
	}
}
