package group3;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class Group3 extends JFrame {
	private static String BROADCAST_ADDRESS = "234.1.1.1";
	private static String GROUP_ADDRESS = "235.1.1.";
	private static int PORT = 6789;

	MulticastSocket multicastBroadcastSocket = null;
	InetAddress multicastBroadcastGroup = null;

	MulticastSocket multicastChatSocket = null;
	InetAddress multicastChatGroup = null;

	String registeredName = "";
	String processID = ManagementFactory.getRuntimeMXBean().getName();

	ArrayList<String> registeredUser = new ArrayList();

	ArrayList<String> friendList = new ArrayList();

	// Tentatively to be a 2D ArrayList [[groupName1, Grp1 friend1, Grp1
	// friend2], [groupName2, Grp2 friend1, Grp2 friend2]]
	ArrayList<String> GroupList = new ArrayList();

	boolean broadcastConnect = false;
	boolean broadcastFindFriend = false;
	boolean broadcastAddGroup = false;

	private JTextField txtUserName;
	private JTextField txtFriend;
	private JTextField txtGroup;
	private JTextField txtJoinGroup;
	private JTextField txtMessage;
	private JTextArea taFriendList;
	private JButton btnAddFriend;
	private JButton btnDeleteFriend;
	private JComboBox cobGroupList;
	
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
		// txtUserName.setColumns(10);

		JLabel lblFriend = new JLabel("Friend");
		lblFriend.setBounds(8, 54, 100, 26);
		getContentPane().add(lblFriend);

		txtFriend = new JTextField();
		txtFriend.setBounds(108, 54, 240, 26);
		getContentPane().add(txtFriend);
		// txtFriend.setColumns(10);

		JLabel lblGroup = new JLabel("New Group");
		lblGroup.setBounds(8, 100, 100, 26);
		getContentPane().add(lblGroup);

		btnDeleteFriend = new JButton("Delete");
		btnDeleteFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (friendList.contains(txtFriend.getText())) {
					friendList.remove(txtFriend.getText().toString());
					JOptionPane.showMessageDialog(null, txtFriend.getText().toString() + " has been removed");
					if (friendList.isEmpty()) {
						btnDeleteFriend.setEnabled(false);
					}
					taFriendList.setText(taFriendList.getText().replaceFirst(txtFriend.getText().toString() + "\n", ""));
					txtFriend.setText("");
				} else {
					JOptionPane.showMessageDialog(null, txtFriend.getText().toString() + " doesnt exists in your friend list!");
					txtFriend.setText("");
				}
			}
		});
		btnDeleteFriend.setEnabled(false);
		btnDeleteFriend.setBounds(484, 54, 120, 26);
		getContentPane().add(btnDeleteFriend);

		txtGroup = new JTextField();
		txtGroup.setBounds(108, 100, 240, 26);
		getContentPane().add(txtGroup);

		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeName = txtUserName.getText();

				if (registeredUser.contains(tentativeName)) {
					System.out.println(tentativeName + " already taken!");
				} else {
					try {
						if (!broadcastConnect) {
							multicastBroadcastGroup = InetAddress.getByName(BROADCAST_ADDRESS);
							multicastBroadcastSocket = new MulticastSocket(PORT);
							multicastBroadcastSocket.joinGroup(multicastBroadcastGroup);
							broadcastConnect = true;
						}

						String userName = "CheckRegisterName|" + tentativeName;
						byte[] sendBuf = userName.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, 6789);
						multicastBroadcastSocket.send(dgpSend);

						byte receiveBuf[] = new byte[1000];
						DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);
						multicastBroadcastSocket.receive(dgpReceived);

						System.out.println("Before Try");
						multicastBroadcastSocket.setSoTimeout(3000);
						try {
							multicastBroadcastSocket.receive(dgpReceived);
							multicastBroadcastSocket.setSoTimeout(0);
							System.out.println("received");

							byte[] receivedData = dgpReceived.getData();
							int length = dgpReceived.getLength();

							String receivedMessage = new String(receivedData, 0, length);

							if (receivedMessage.equals("NameTaken|")) {
								JOptionPane.showMessageDialog(null, tentativeName + " is already taken!");
							}

						} catch (SocketTimeoutException ex) {
							multicastBroadcastSocket.setSoTimeout(0);
							
							registeredName = tentativeName;
							JOptionPane.showMessageDialog(null, "You have been registered");
							
							btnRegister.setEnabled(false);
							btnAddFriend.setEnabled(true);
							txtUserName.setEditable(false);

							new Thread(new Runnable() {
								@Override
								public void run() {

									while (true) {
										byte receiveBuf[] = new byte[1000];
										DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);

										try {
											System.out.println("listening..");
											multicastBroadcastSocket.receive(dgpReceived);
											
											/*if (broadcastAddGroup) {

												System.out.println("Before Try");
												multicastBroadcastSocket.setSoTimeout(3000); // 3
																								// seconds
																								// timeout
												try {
													multicastBroadcastSocket.receive(dgpReceived);
													multicastBroadcastSocket.setSoTimeout(0); // Disable
																								// time
																								// out

													System.out.println("received");

													byte[] receivedData = dgpReceived.getData();
													int length = dgpReceived.getLength();

													String receivedMessage = new String(receivedData, 0, length);

													if (receivedMessage.equals("GroupExists|")) {
														// dialogue alert
														JOptionPane.showMessageDialog(null,
																txtGroup.getText() + " already exist");
													}

												} catch (SocketTimeoutException ex) {
													// registeredName =
													// tentativeName;
													System.out.println(
															txtGroup.getText().toString() + " group does not exists");
													// btnRegister.setEnabled(false);
													GroupList.add(txtGroup.getText());
													taGroupList.setText(txtGroup.getText());
													System.out.println(GroupList);
													multicastBroadcastSocket.setSoTimeout(0); // Disable
																								// time
																								// out
												}

												broadcastAddGroup = false;
											}
											else {*/
												byte[] receivedData = dgpReceived.getData();
												int length = dgpReceived.getLength();

												String message = new String(receivedData, 0, length);
												System.out.println("Received broadcast message: " + message);

												DoAction(message);
											//}
										} catch (IOException ex) {
											ex.printStackTrace();
										}
									}
								}
							}).start();
						}

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		btnRegister.setBounds(356, 8, 120, 26);
		getContentPane().add(btnRegister);

		btnAddFriend = new JButton("Add");
		btnAddFriend.setEnabled(false);
		btnAddFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeFriendName = txtFriend.getText();

				if (friendList.contains(tentativeFriendName)) {
					JOptionPane.showMessageDialog(null, tentativeFriendName + " is already your friend!");
				} else {
					try {
						broadcastFindFriend = true;
						String friendName = "CheckFriendName|" + tentativeFriendName + "|" + txtUserName.getText();
						byte[] sendBuf = friendName.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		btnAddFriend.setBounds(356, 54, 120, 26);
		getContentPane().add(btnAddFriend);

		JButton btnAddGroup = new JButton("Create");
		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeGroupName = txtGroup.getText();
				cobGroupList.addItem(tentativeGroupName);
				/*if (GroupList.contains(tentativeGroupName)) {
					cobGroupList.addItem(tentativeGroupName);
					System.out.println(tentativeGroupName + " already in your group list|");
				} else {
					try {
						broadcastAddGroup = true;
						String friendName = "CheckAddGroup|" + tentativeGroupName;
						byte[] sendBuf = friendName.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}*/
			}
		});
		btnAddGroup.setBounds(356, 100, 120, 26);
		getContentPane().add(btnAddGroup);

		JButton btnEditGroup = new JButton("Add Friend");
		btnEditGroup.setBounds(356, 146, 120, 26);
		getContentPane().add(btnEditGroup);

		JLabel lblFriendList = new JLabel("Friend List");
		lblFriendList.setBounds(8, 200, 166, 26);
		getContentPane().add(lblFriendList);

		JLabel lblGroupList = new JLabel("Group List");
		lblGroupList.setBounds(8, 146, 100, 26);
		getContentPane().add(lblGroupList);

		txtJoinGroup = new JTextField();
		txtJoinGroup.setBounds(564, 8, 120, 26);
		getContentPane().add(txtJoinGroup);

		JButton btnJoinGroup = new JButton("Join");
		btnJoinGroup.setBounds(484, 146, 120, 26);
		getContentPane().add(btnJoinGroup);

		JButton btnLeaveGroup = new JButton("Leave");
		btnLeaveGroup.setBounds(612, 146, 120, 26);
		getContentPane().add(btnLeaveGroup);
		
		taFriendList = new JTextArea();
		taFriendList.setEditable(false);
		JScrollPane spFriendList = new JScrollPane(taFriendList);
		spFriendList.setBounds(8, 230, 166, 254);
		getContentPane().add(spFriendList);

		JTextArea taMessage = new JTextArea();
		taMessage.setEditable(false);
		JScrollPane spMessage = new JScrollPane(taMessage);
		spMessage.setBounds(182, 230, 550, 254);
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
		
		cobGroupList = new JComboBox();
		cobGroupList.setBounds(108, 146, 240, 26);
		getContentPane().add(cobGroupList);
		
		JLabel lblMessageBox = new JLabel("Message Box");
		lblMessageBox.setBounds(182, 200, 550, 26);
		getContentPane().add(lblMessageBox);

	}

	public void DoAction(String message) {
		String action = message.substring(0, message.indexOf("|"));

		message = message.substring(message.indexOf("|") + 1, message.length());
		System.out.println("Sub: " + message);

		if (action.equals("CheckRegisterName")) {
			if (message.equals(registeredName)) {
				try {
					String sendMessage = "NameTaken|";
					byte[] sendBuf = sendMessage.getBytes();
					DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
					multicastBroadcastSocket.send(dgpSend);
					System.out.println(sendMessage);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else {
				registeredUser.add(message);
			}
		} else if (action.equals("CheckFriendName")) {
			String nameToCheck = message.substring(0, message.indexOf("|"));
			message = message.substring(message.indexOf("|") + 1, message.length());
			
			if (nameToCheck.equals(registeredName)) {
				try {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					String personAdding = message;
					
					int dialogResult = JOptionPane.showConfirmDialog(null, personAdding + " would like to add you", "Friend Request", dialogButton);
					
					if (dialogResult == 0) {
						String sendMessage = personAdding + "|FriendRequestAccepted|" + registeredName;
						byte[] sendBuf = sendMessage.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						
						taFriendList.setText(taFriendList.getText() + personAdding + "\n");
						friendList.add(message);
						
						if(friendList.isEmpty())
						{
							btnDeleteFriend.setEnabled(false);
							
						}
						else
						{
							btnDeleteFriend.setEnabled(true);
						}
						
						System.out.println(sendMessage);
					} else {
						String sendMessage = personAdding + "|FriendRequestRejected|" + registeredName;
						byte[] sendBuf = sendMessage.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						
						System.out.println(sendMessage);
					}

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		} 
		else if(action.equals(registeredName))
		{
			String receivedMessage = message.substring(0, message.indexOf("|"));
			message = message.substring(message.indexOf("|") + 1, message.length());
			
			if(receivedMessage.equals("FriendRequestAccepted"))
			{
				taFriendList.setText(taFriendList.getText() + message + "\n");
				friendList.add(message);
				
				if(friendList.isEmpty())
				{
					btnDeleteFriend.setEnabled(false);
					
				}
				else
				{
					btnDeleteFriend.setEnabled(true);
				}
				
				JOptionPane.showMessageDialog(null, message + " accepted your friend request");
			}
			else if(receivedMessage.equals("FriendRequestRejected"))
			{
				JOptionPane.showMessageDialog(null, message + " doesn't wish to be your friend");
			}
		}
		else if (action.equals("CheckAddGroup")) {
			if (GroupList.contains(message)) {
				try {
					String sendMessage = "GroupExists|";
					byte[] sendBuf = sendMessage.getBytes();
					DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
					multicastBroadcastSocket.send(dgpSend);
					System.out.println(sendMessage);

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
