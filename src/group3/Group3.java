package group3;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class Group3 extends JFrame {
	private JTextField txtUserName;
	private JTextField txtFriend;
	private JTextField txtGroup;
	private JTextField txtJoinGroup;
	private JTextField txtMessage;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Group3 frame = new Group3();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Group3() {
		setTitle("ICT2107 Project 1 - Group 3");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 760, 610);
		getContentPane().setLayout(null);
		
		JLabel lblUserName = new JLabel("User Name");
		lblUserName.setBounds(8, 8, 100, 26);
		getContentPane().add(lblUserName);
		
		txtUserName = new JTextField();
		txtUserName.setBounds(108, 8, 240, 26);
		getContentPane().add(txtUserName);
		//txtUserName.setColumns(10);
		
		JLabel lblFriend = new JLabel("Friend");
		lblFriend.setBounds(8, 54, 100, 26);
		getContentPane().add(lblFriend);
		
		txtFriend = new JTextField();
		txtFriend.setBounds(108, 54, 240, 26);
		getContentPane().add(txtFriend);
		//txtFriend.setColumns(10);
		
		JLabel lblGroup = new JLabel("Group");
		lblGroup.setBounds(8, 100, 100, 26);
		getContentPane().add(lblGroup);
		
		txtGroup = new JTextField();
		txtGroup.setBounds(108, 100, 240, 26);
		getContentPane().add(txtGroup);
		//txtGroup.setColumns(10);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.setBounds(356, 8, 120, 26);
		getContentPane().add(btnRegister);
		
		JButton btnAddFriend = new JButton("Add");
		btnAddFriend.setBounds(356, 54, 120, 26);
		getContentPane().add(btnAddFriend);
		
		JButton btnDeleteFriend = new JButton("Delete");
		btnDeleteFriend.setBounds(484, 54, 120, 26);
		getContentPane().add(btnDeleteFriend);
		
		JButton btnAddGroup = new JButton("Add");
		btnAddGroup.setBounds(356, 100, 120, 26);
		getContentPane().add(btnAddGroup);
		
		JButton btnEditGroup = new JButton("Edit");
		btnEditGroup.setBounds(484, 100, 120, 26);
		getContentPane().add(btnEditGroup);

		JLabel lblFriendList = new JLabel("Friend List");
		lblFriendList.setBounds(8, 150, 166, 26);
		getContentPane().add(lblFriendList);

		JLabel lblGroupList = new JLabel("Group List");
		lblGroupList.setBounds(182, 150, 166, 26);
		getContentPane().add(lblGroupList);

		txtJoinGroup = new JTextField();
		txtJoinGroup.setBounds(356, 150, 120, 26);
		getContentPane().add(txtJoinGroup);
		//txtJoinGroup.setColumns(10);
		
		JButton btnJoinGroup = new JButton("Join");
		btnJoinGroup.setBounds(484, 150, 120, 26);
		getContentPane().add(btnJoinGroup);
		
		JButton btnLeaveGroup = new JButton("Leave");
		btnLeaveGroup.setBounds(612, 150, 120, 26);
		getContentPane().add(btnLeaveGroup);
		
		JTextArea taFriendList = new JTextArea();
		JScrollPane spFriendList = new JScrollPane(taFriendList);
		spFriendList.setBounds(8, 184, 166, 300);
		getContentPane().add(spFriendList);
		
		JTextArea taGroupList = new JTextArea();
		JScrollPane spGroupList = new JScrollPane(taGroupList);
		spGroupList.setBounds(182, 184, 166, 300);
		getContentPane().add(spGroupList);
		
		JTextArea taMessage = new JTextArea();
		JScrollPane spMessage = new JScrollPane(taMessage);
		spMessage.setBounds(356, 184, 376, 300);
		getContentPane().add(spMessage);
		
		JLabel lblMessge = new JLabel("Message");
		lblMessge.setBounds(8, 508, 100, 26);
		getContentPane().add(lblMessge);

		txtMessage = new JTextField();
		txtMessage.setBounds(108, 508, 496, 26);
		getContentPane().add(txtMessage);
		
		JButton btnSend = new JButton("Leave");
		btnSend.setBounds(612, 508, 120, 26);
		getContentPane().add(btnSend);
		
		
	}
}
