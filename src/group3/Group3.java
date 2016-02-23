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
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class Group3 extends JFrame {
	private static String BROADCAST_ADDRESS = "236.1.1.1";
	private static String BROADCAST_ADDRESS_NO_GROUP = "236.1.2.1";
	private static int PORT = 6789;
	
	//Multicast Broadcast Socket
	MulticastSocket multicastBroadcastSocket = null;
	InetAddress multicastBroadcastGroup = null;

	//Multicast Chat Socket
	MulticastSocket multicastChatSocket = null;
	InetAddress multicastChatGroup = null;

	String registeredName = "";		//Registered User Name

	ArrayList<String> registeredUser = new ArrayList();			//List of registered users in the broadcast group
	ArrayList<String> friendList = new ArrayList();				//List of friends added my user
	ArrayList<String> groupFriendList = new ArrayList();		//List of friends within a group chat
	ArrayList<String> ownCreatedGroupList = new ArrayList();	//List of group create by user
	ArrayList<String> groupList = new ArrayList();				//List of group added by user (Inclusive of own created)

	boolean broadcastCreateGroup = false;
	boolean chatGroupThreadCreated = false;
	
	private JTextField txtUserName;
	private JTextField txtFriend;
	private JTextField txtGroup;
	private JTextField txtMessage;
	private JTextArea taGroupFriendList;
	private JButton btnAddFriend;
	private JButton btnDeleteFriend;
	private JComboBox cobGroupList;
	private JComboBox cobFriendList;
	private JTextArea taMessage;
	private JLabel lblMessageBox;
	
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
		//Connect to Broadcast Group
		try{
			multicastBroadcastGroup = InetAddress.getByName(BROADCAST_ADDRESS);
			multicastBroadcastSocket = new MulticastSocket(PORT);
			multicastBroadcastSocket.joinGroup(multicastBroadcastGroup);
			System.out.println("Connected to Broadcast Group: " + BROADCAST_ADDRESS);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		setTitle("ICT2107 Project 1 - Group 3");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 760, 700);
		getContentPane().setLayout(null);

		JLabel lblUserName = new JLabel("User Name");
		txtUserName = new JTextField();
		JLabel lblFriend = new JLabel("Friend");
		txtFriend = new JTextField();
		JLabel lblGroup = new JLabel("New Group");
		btnDeleteFriend = new JButton("Delete");
		txtGroup = new JTextField();
		JButton btnRegister = new JButton("Register");
		btnAddFriend = new JButton("Add");
		JButton btnAddGroup = new JButton("Create");
		JButton btnAddGroupFriend = new JButton("Add Friend to Selected Group");
		JLabel lblGroupFriendList = new JLabel("Group Friend List");
		JLabel lblGroupList = new JLabel("Group List");
		JButton btnJoinGroup = new JButton("Join");
		JButton btnLeaveGroup = new JButton("Leave");
		taGroupFriendList = new JTextArea();
		JScrollPane spFriendList = new JScrollPane(taGroupFriendList);
		taMessage = new JTextArea();
		JScrollPane spMessage = new JScrollPane(taMessage);
		JLabel lblMessge = new JLabel("Message");
		txtMessage = new JTextField();
		JButton btnSend = new JButton("Send");
		cobGroupList = new JComboBox();
		lblMessageBox = new JLabel("Current Chat: None");
		JLabel lblFriendList = new JLabel("Friend List");
		cobFriendList = new JComboBox();
		
		lblUserName.setBounds(8, 8, 100, 26);
		txtUserName.setBounds(108, 8, 240, 26);
		lblFriend.setBounds(8, 54, 100, 26);
		txtFriend.setBounds(108, 54, 240, 26);
		lblGroup.setBounds(8, 100, 100, 26);
		btnDeleteFriend.setBounds(484, 54, 120, 26);
		txtGroup.setBounds(108, 100, 240, 26);
		btnRegister.setBounds(356, 8, 120, 26);
		btnAddFriend.setBounds(356, 54, 120, 26);
		btnAddGroup.setBounds(356, 100, 120, 26);
		btnAddGroupFriend.setBounds(356, 192, 248, 26);
		lblGroupFriendList.setBounds(8, 250, 166, 26);
		lblGroupList.setBounds(8, 146, 100, 26);
		btnJoinGroup.setBounds(356, 146, 120, 26);
		btnLeaveGroup.setBounds(484, 146, 120, 26);
		spFriendList.setBounds(8, 280, 166, 300);
		spMessage.setBounds(182, 280, 550, 300);
		lblMessge.setBounds(8, 598, 100, 26);
		txtMessage.setBounds(108, 598, 496, 26);
		btnSend.setBounds(612, 598, 120, 26);
		cobGroupList.setBounds(108, 146, 240, 26);
		lblMessageBox.setBounds(182, 250, 550, 26);
		lblFriendList.setBounds(8, 192, 100, 26);
		cobFriendList.setBounds(108, 192, 240, 26);
		
		btnDeleteFriend.setEnabled(false);
		taGroupFriendList.setEditable(false);
		taMessage.setEditable(false);

		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeName = txtUserName.getText();

				try {
					//Broadcast tentative user name to check any other user has the same name
					String userName = "CheckRegisterName|" + tentativeName;
					byte[] sendBuf = userName.getBytes();
					DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
					multicastBroadcastSocket.send(dgpSend);
					System.out.println("Send: CheckRegisterName|" + tentativeName);
					
					
					byte receiveBuf[] = new byte[1000];
					DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);
					multicastBroadcastSocket.receive(dgpReceived);	//Discard own broadcast message

					multicastBroadcastSocket.setSoTimeout(3000);	//Set timeout to 3 seconds
					//This section run when there is a similar user name
					try {
						System.out.println("Awaiting for user name reply");
						multicastBroadcastSocket.receive(dgpReceived);
						multicastBroadcastSocket.setSoTimeout(0);	//Clear timeout
						
						//Receive reply 
						byte[] receivedData = dgpReceived.getData();
						int length = dgpReceived.getLength();
						String receivedMessage = new String(receivedData, 0, length);
						System.out.println("Receive Message: " + receivedMessage);
						
						if (receivedMessage.equals("NameTaken|")) {
							JOptionPane.showMessageDialog(null, tentativeName + " is already taken!");
						}
					} 
					//This section run when user name does not exist
					catch (SocketTimeoutException ex) {
						multicastBroadcastSocket.setSoTimeout(0);
												
						registeredName = tentativeName;		//Set user name
						JOptionPane.showMessageDialog(null, registeredName + ", you have been registered!");
						
						btnRegister.setEnabled(false);
						txtUserName.setEditable(false);
						btnAddFriend.setEnabled(true);
						
						//Run thread to listen broadcast group message
						new Thread(new Runnable() {
							@Override
							public void run() {
								
								while (true) {
									byte receiveBuf[] = new byte[1000];
									DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);

									try {
										System.out.println("Listening to broadcast group...");
										multicastBroadcastSocket.receive(dgpReceived);
										
										//This condition run when user is creating a new chat group
										if (broadcastCreateGroup) {
											multicastBroadcastSocket.setSoTimeout(3000);
											
											//This section run when chat group already exist
											try {
												multicastBroadcastSocket.receive(dgpReceived);
												multicastBroadcastSocket.setSoTimeout(0);

												byte[] receivedData = dgpReceived.getData();
												int length = dgpReceived.getLength();
												String receivedMessage = new String(receivedData, 0, length);

												if (receivedMessage.equals("GroupExists|")) {
													// Dialogue alert
													JOptionPane.showMessageDialog(null, txtGroup.getText() + " already exist");
												}

											} 
											//This section run when chat group does not exist
											catch (SocketTimeoutException ex) {
												String groupName = txtGroup.getText();
												
												//Generate a randomise IP address of a new chat group
												Random rand = new Random();
												String groupIP = "235.1" + "." + (rand.nextInt(254)+2) + "." + (rand.nextInt(254)+2);
												System.out.println("New IP address: " + groupIP + " generated for group " + groupName);
												
												//Add group into own created list
												ownCreatedGroupList.add(txtGroup.getText() + "|" + groupIP);
												
												//Add group into current active group (To be display in the drop down list)
												groupList.add(txtGroup.getText() + "|" + groupIP);
												UpdateGroupList();	//Update group drop down list
												
												//Setup chat group multicast
												multicastChatGroup = InetAddress.getByName(groupIP);
												multicastChatSocket = new MulticastSocket(PORT);
												multicastChatSocket.joinGroup(multicastChatGroup);
												System.out.println("Multicast Chat Group IP: " + groupIP);
												
												lblMessageBox.setText("Current Chat: " + txtGroup.getText());
												
												//Check whether chat group thread was created
												if(!chatGroupThreadCreated)
												{
													//Create a new thread to keep listening for packets from the group
													new Thread(new Runnable(){
														@Override
														public void run(){
															byte receiveBuf[] = new byte[1000];
															DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);
															
															chatGroupThreadCreated = true;
															while(true){
																try{
																	System.out.println("Listening to chat group...");
																	multicastChatSocket.receive(dgpReceived);
																	byte[] receivedData = dgpReceived.getData();
																	int length = dgpReceived.getLength();
																	
																	//Receive message from chat group
																	String msg = new String(receivedData, 0, length);
																	
																	//Do necessary message action in DoMessageAction()
																	DoMessageAction(msg);
																}catch(IOException ex){
																	ex.printStackTrace();
																}
															}
														}
													}).start();
												}
												
												multicastBroadcastSocket.setSoTimeout(0);	//Clear timeout
											}

											broadcastCreateGroup = false;
										}
										else {
											byte[] receivedData = dgpReceived.getData();
											int length = dgpReceived.getLength();
											String message = new String(receivedData, 0, length);
											System.out.println("Received broadcast message: " + message);
											
											//Do necessary message action in DoMessageAction()
											DoAction(message);
										}
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
		});
		
		btnAddFriend.setEnabled(false);
		btnAddFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeFriendName = txtFriend.getText();

				if (friendList.contains(tentativeFriendName)) {
					JOptionPane.showMessageDialog(null, tentativeFriendName + " is already your friend!");
				} else {
					try {
						//Send friend Request
						String friendName = "CheckFriendName|" + tentativeFriendName + "|" + registeredName;
						byte[] sendBuf = friendName.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		
		btnDeleteFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (friendList.contains(txtFriend.getText())) {
					//Remove friend from friend list
					friendList.remove(txtFriend.getText());
					UpdateFriendList();
					JOptionPane.showMessageDialog(null, txtFriend.getText().toString() + " has been removed");
					
					if (friendList.isEmpty()) {
						btnDeleteFriend.setEnabled(false);
					}
					
					txtFriend.setText("");
				} else {
					JOptionPane.showMessageDialog(null, txtFriend.getText().toString() + " doesnt exists in your friend list!");
					txtFriend.setText("");
				}
			}
		});

		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeGroupName = txtGroup.getText();
				
				if (CheckGroupListExist(tentativeGroupName)) {
					JOptionPane.showMessageDialog(null, tentativeGroupName + " group already exists");
				} else {
					try {
						//Broadcast group name to check any other group of similar name
						broadcastCreateGroup = true;
						String checkGroupName = "CheckCreateGroupName|" + tentativeGroupName;
						byte[] sendBuf = checkGroupName.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						System.out.println("Check for chat group: " + checkGroupName);

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		btnAddGroupFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String groupName = cobGroupList.getSelectedItem().toString();
				String friendName = cobFriendList.getSelectedItem().toString();
				
				try {
					//Add friend of a select group chat
					String joinGroupRequest = friendName + "|JoinGroupRequest|" + registeredName + "|" + groupName + "|" + GetGroupIP(groupName);
					byte[] sendBuf = joinGroupRequest.getBytes();
					DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
					multicastBroadcastSocket.send(dgpSend);
					System.out.println("Request " + friendName +  " to join chat group " + groupName);

				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
			}
		});
		
		btnJoinGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String groupName = cobGroupList.getSelectedItem().toString();
				String groupIP = GetGroupIP(groupName);
				taMessage.setText("");
				if(multicastChatSocket != null)
				{
					try{
						//Broadcast to current group chat member that user is leaving
						String msg = txtMessage.getText();
						msg = "LeftGroup|" + registeredName;
						byte[] buf = msg.getBytes();
						
						DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
						multicastChatSocket.send(dgpSend);
					}catch(IOException ex){
						ex.printStackTrace();
					}
				}
				
				try {
					multicastChatGroup = InetAddress.getByName(groupIP);
					multicastChatSocket = new MulticastSocket(PORT);
					multicastChatSocket.joinGroup(multicastChatGroup);

					lblMessageBox.setText("Current Chat: " + groupName);
					
					groupFriendList.clear();	//Clear previous group chat friend list
					
					try{
						//Broadcast to know who are current online
						String whoIsThere = "WhoIsThere|"+registeredName;
						
						byte[] buf = whoIsThere.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
						multicastChatSocket.send(dgpSend);
					}catch(IOException ex){
						ex.printStackTrace();
					}			
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		btnLeaveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					multicastChatGroup = InetAddress.getByName(BROADCAST_ADDRESS_NO_GROUP);
					try {
						multicastChatSocket = new MulticastSocket(PORT);
						multicastChatSocket.joinGroup(multicastChatGroup);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				taMessage.setText("");
				taGroupFriendList.setText("");
				lblMessageBox.setText("Current Chat: None");								
			
			}
		});
		
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					String msg = txtMessage.getText();
					msg = "Message|" + registeredName + ": " + msg;
					byte[] buf = msg.getBytes();
					
					DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
					multicastChatSocket.send(dgpSend);
					System.out.println("Send test message: " + msg);
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		});
		
		getContentPane().add(lblUserName);
		getContentPane().add(txtUserName);
		getContentPane().add(lblFriend);
		getContentPane().add(txtFriend);
		getContentPane().add(lblGroup);
		getContentPane().add(btnDeleteFriend);
		getContentPane().add(txtGroup);
		getContentPane().add(btnRegister);
		getContentPane().add(btnAddFriend);
		getContentPane().add(btnAddGroup);
		getContentPane().add(btnAddGroupFriend);
		getContentPane().add(lblGroupFriendList);
		getContentPane().add(lblGroupList);
		getContentPane().add(btnJoinGroup);
		getContentPane().add(btnLeaveGroup);
		getContentPane().add(spFriendList);
		getContentPane().add(spMessage);
		getContentPane().add(lblMessge);
		getContentPane().add(txtMessage);
		getContentPane().add(btnSend);
		getContentPane().add(cobGroupList);
		getContentPane().add(lblMessageBox);
		getContentPane().add(lblFriendList);
		getContentPane().add(cobFriendList);
	}

	public void DoAction(String message) {
		String action = message.substring(0, message.indexOf("|"));		//Retrieve action

		message = message.substring(message.indexOf("|") + 1, message.length());	//Discard action string 
		System.out.println("Message : " + message);
		
		//Action to check for similar user name
		if (action.equals("CheckRegisterName")) {
			if (message.equals(registeredName)) {
				try {
					//Send name taken message
					String sendMessage = "NameTaken|";
					byte[] sendBuf = sendMessage.getBytes();
					DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
					multicastBroadcastSocket.send(dgpSend);
					System.out.println(sendMessage);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else {
				//Add user to registered user list (Eventually the receive name will be registered)
				registeredUser.add(message);
			}
		}
		//Action to accept friend request
		else if (action.equals("CheckFriendName")) {
			String nameToCheck = message.substring(0, message.indexOf("|"));	//Name being request
			message = message.substring(message.indexOf("|") + 1, message.length());
			
			//Condition run will requested name in this user
			if (nameToCheck.equals(registeredName)) {
				try {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					String personAdding = message;
					
					//Dialog to accept or reject friend request
					int dialogResult = JOptionPane.showConfirmDialog(null, personAdding + " would like to add you", "Friend Request", dialogButton);
					
					//Respond when accept friend request
					if (dialogResult == 0) {
						String sendMessage = personAdding + "|FriendRequestAccepted|" + registeredName;
						byte[] sendBuf = sendMessage.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						
						friendList.add(message);	//Add to friend list
						UpdateFriendList();			//Update friend drop down list
						
						if(friendList.isEmpty()) {
							btnDeleteFriend.setEnabled(false);
						} else {
							btnDeleteFriend.setEnabled(true);
						}
						
						System.out.println("Accept friend request: " + sendMessage);
					}
					//Respond when reject friend request
					else {
						String sendMessage = personAdding + "|FriendRequestRejected|" + registeredName;
						byte[] sendBuf = sendMessage.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						
						System.out.println("Reject friend request: " + sendMessage);
					}

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} 
		//Action when receive request reply
		else if(action.equals(registeredName))
		{
			String replyAction = message.substring(0, message.indexOf("|"));	//Retrieve reply action
			message = message.substring(message.indexOf("|") + 1, message.length());
			
			//When friend request is accepted
			if(replyAction.equals("FriendRequestAccepted"))
			{
				friendList.add(message);
				UpdateFriendList();
				
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
			//When friend request is rejected
			else if(replyAction.equals("FriendRequestRejected"))
			{
				JOptionPane.showMessageDialog(null, message + " doesn't wish to be your friend");
			}
			//When group name exist
			else if(replyAction.equals("GroupExists"))
			{
				JOptionPane.showMessageDialog(null, message + " group already exists");
			}
			//When a friend request to join a group chat
			else if(replyAction.equals("JoinGroupRequest"))
			{
				try {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					String friendName = message.substring(0, message.indexOf("|"));
					message = message.substring(message.indexOf("|") + 1, message.length());
					String groupName = message.substring(0, message.indexOf("|"));
					message = message.substring(message.indexOf("|") + 1, message.length());
					String groupIP = message;
					
					int dialogResult = JOptionPane.showConfirmDialog(null, friendName + " would like you to join " + groupName + " chat group!", "Join Chat Group", dialogButton);
					
					if (dialogResult == 0) {
						if(multicastChatGroup != null)
						{
							//Broadcast to other member that user is leaving the current group chat
							try{
								String msg = txtMessage.getText();
								msg = "LeftGroup|" + registeredName;
								byte[] buf = msg.getBytes();
								
								DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
								multicastChatSocket.send(dgpSend);
							}catch(IOException ex){
								ex.printStackTrace();
							}
						}
						
						multicastChatGroup = InetAddress.getByName(groupIP);
						multicastChatSocket = new MulticastSocket(PORT);
						multicastChatSocket.joinGroup(multicastChatGroup);
						
						lblMessageBox.setText("Current Chat: " + groupName);
						
						//Add requested group into group list
						groupList.add(groupName + "|" + groupIP);
						UpdateGroupList();	//Update group drop down list
						
						if(!chatGroupThreadCreated)
						{
							//Create a new thread to keep listening for packets from the group
							new Thread(new Runnable(){
								@Override
								public void run(){
									chatGroupThreadCreated = true;
									
									byte receiveBuf[] = new byte[1000];
									DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);
									
									//Broadcast to find who is currently online
									try{
										String whoIsThere = "WhoIsThere|"+registeredName;
										byte[] buf = whoIsThere.getBytes();
										
										DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
										multicastChatSocket.send(dgpSend);
									}catch(IOException ex){
										ex.printStackTrace();
									}
									
									//Clear previous group friend list
									groupFriendList.clear();
									
									while(true){
										try{
											multicastChatSocket.receive(dgpReceived);
											byte[] receivedData = dgpReceived.getData();
											int length = dgpReceived.getLength();
											String msg = new String(receivedData, 0, length);
											
											//Do necessary action
											DoMessageAction(msg);
										}catch(IOException ex){
											ex.printStackTrace();
										}
									}
								}
							}).start();
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		//Action to respond group name already exist
		else if (action.equals("CheckCreateGroupName")) {
			//Check name against own created group list
			if (CheckGroupNameExist(message)) {
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
	
	//Function to check if input group name exist inside own created group chat list
	public boolean CheckGroupNameExist(String group)
	{
		if(ownCreatedGroupList.isEmpty())
		{
			return false;
		}
		
		for(int i=0; i<ownCreatedGroupList.size(); i++)
		{
			String groupName = ownCreatedGroupList.get(i);
			groupName = groupName.substring(0, groupName.indexOf("|"));
			
			if(groupName.equals(group))
			{
				return true;
			}
		}
		
		return false;
	}
	
	//Function to check if input group name exist inside group chat list
	public boolean CheckGroupListExist(String group)
	{
		if(groupList.isEmpty())
		{
			return false;
		}
		
		for(int i=0; i<groupList.size(); i++)
		{
			String groupName = groupList.get(i);
			groupName = groupName.substring(0, groupName.indexOf("|"));
			
			if(groupName.equals(group))
			{
				return true;
			}
		}
		
		return false;
	}
	
	//Function to get input group name IP address
	public String GetGroupIP(String group)
	{		
		for(int i=0; i<groupList.size(); i++)
		{
			String groupItem = groupList.get(i);
			String groupName = groupItem.substring(0, groupItem.indexOf("|"));
			
			System.out.println(groupName);
			
			if(groupName.equals(group))
			{
				System.out.println("return IP:" + groupItem.substring(groupItem.indexOf("|")+1, groupItem.length()));
				return groupItem.substring(groupItem.indexOf("|")+1, groupItem.length());
			}
		}
		
		return null;
	}
	
	//Function to update group drop down list
	public void UpdateGroupList()
	{
		cobGroupList.removeAllItems();
		
		for(int i=0; i<groupList.size(); i++)
		{
			String group = groupList.get(i);
			cobGroupList.addItem(group.substring(0, group.indexOf("|")));
			System.out.println("Upddate : " + group.substring(0, group.indexOf("|")));
		}
	}
	
	//Function to update friend drop down list
	public void UpdateFriendList()
	{
		cobFriendList.removeAllItems();
		
		for(int i=0; i<friendList.size(); i++)
		{
			cobFriendList.addItem(friendList.get(i));
		}
	}
	
	//Function to update group chat friend list
	public void UpdateGroupFriendList()
	{
		taGroupFriendList.setText("");
		
		for(int i=0; i<groupFriendList.size(); i++)
		{
			taGroupFriendList.append(groupFriendList.get(i) + "\n");
		}
	}
	
	//Function to manage chat group action
	public void DoMessageAction(String message)
	{
		String action = message.substring(0, message.indexOf("|"));
		message = message.substring(message.indexOf("|") + 1, message.length());
		
		//Reply requester of this user identity for group chat
		if(action.equals("WhoIsThere") && (!message.equals(registeredName)))
		{			
			try{
				String whoIsThere = message + "|WhoIsThereReply|" + registeredName;
				byte[] buf = whoIsThere.getBytes();
				
				DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
				multicastChatSocket.send(dgpSend);
			}catch(IOException ex){
				ex.printStackTrace();
			}
			
			
			groupFriendList.add(message);	//Add requester user name
			UpdateGroupFriendList();		//Update friend drop down list
		}
		//Receive reply from request
		else if(action.equals(registeredName))
		{
			action = message.substring(0, message.indexOf("|"));
			
			//Reply of who is online for the current chat group
			if(action.equals("WhoIsThereReply"))
			{
				message = message.substring(message.indexOf("|") + 1, message.length());
				System.out.println("Add Group List : " + message);

				if(!message.equals(registeredName))
				{
					groupFriendList.add(message);
				}

				UpdateGroupFriendList();
			}
		}
		//Receive message that a user has left the current chat group
		else if(action.equals("LeftGroup"))
		{
			if(groupFriendList.contains(message))
			{
				groupFriendList.remove(message);
				System.out.println(message + " has left group");
			}
			
			UpdateGroupFriendList();
		}
		//Normal message
		else if(action.equals("Message"))
		{
			taMessage.append(message + "\n");
		}
	}
}
