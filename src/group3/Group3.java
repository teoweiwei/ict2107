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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.awt.Font;
import java.awt.Color;

public class Group3 extends JFrame {
	private static String BROADCAST_ADDRESS = "235.1.1.1";
	private static String NO_GROUP = "235.1.1.2";
	private static int PORT = 6789;
	
	//Multicast Broadcast Socket
	MulticastSocket multicastBroadcastSocket = null;
	InetAddress multicastBroadcastGroup = null;

	//Multicast Chat Socket
	MulticastSocket multicastChatSocket = null;
	InetAddress multicastChatGroup = null;

	String registeredName = "";		//Registered User Name

	ArrayList<String> friendList = new ArrayList();				//List of friends added by user
	ArrayList<String> groupFriendList = new ArrayList();		//List of friends within the current group chat (Who is currently online)
	ArrayList<String> ownCreatedGroupList = new ArrayList();	//List of chat group created by this user
	ArrayList<String> groupList = new ArrayList();				//List of group added by user (Inclusive of own created groups)

	boolean broadcastCreateGroup = false;		//Use while in the process of creating a new chat group
	boolean chatGroupThreadCreated = false;		//Use to check whether any chat group thread is created
	
	//UI component
	private JTextField txtUserName;
	private JTextField txtFriend;
	private JTextField txtGroup;
	private JTextField txtMessage;
	private JTextArea taGroupFriendList;
	private JButton btnAddFriend;
	private JButton btnDeleteFriend;
	private JButton btnAddGroupFriend;
	private JButton btnJoinGroup;
	private JButton btnLeaveGroup;
	private JComboBox cobGroupList;
	private JComboBox cobFriendList;
	private JTextArea taMessage;
	private JLabel lblMessageBox;
	private JLabel lblStatus;
	private JButton btnPrivateChat;
	
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
		//Connecting to broadcast group
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
		setBounds(100, 100, 760, 740);
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
		btnAddGroupFriend = new JButton("Add Friend to Selected Group");
		JLabel lblGroupFriendList = new JLabel("Current Chat Friend(s)");
		JLabel lblGroupList = new JLabel("Group List");
		btnJoinGroup = new JButton("Join");
		btnLeaveGroup = new JButton("Leave");
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
		btnPrivateChat = new JButton("Private Chat");
		lblStatus = new JLabel("Please enter a user name to start");
		
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
		btnPrivateChat.setBounds(612, 192, 120, 26);
		lblStatus.setBounds(8, 640, 720, 26);
		
		btnDeleteFriend.setEnabled(false);
		taGroupFriendList.setEditable(false);
		taMessage.setEditable(false);
		btnAddGroup.setEnabled(false);
		btnAddGroupFriend.setEnabled(false);
		btnJoinGroup.setEnabled(false);
		btnLeaveGroup.setEnabled(false);
		btnSend.setEnabled(false);
		btnPrivateChat.setEnabled(false);
		btnAddFriend.setEnabled(false);
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 16));

		//Registration button
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblStatus.setText("Registering user name. Please wait");
				String tentativeName = txtUserName.getText().trim();
				
				//Check for empty input
				if(tentativeName.equals("")){
					JOptionPane.showMessageDialog(null, "Please enter a name");
					
				}else{
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
	
						multicastBroadcastSocket.setSoTimeout(3000);	//Set timeout to 3 seconds, so program will not stuck waiting for reply
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
							
							//Display dialog when user name is taken
							if (receivedMessage.equals("NameTaken|")) {
								JOptionPane.showMessageDialog(null, tentativeName + " is already taken!");
								lblStatus.setText(tentativeName + " is already taken!");
							}
						} 
						//This section run when user name does not exist
						catch (SocketTimeoutException ex) {
							multicastBroadcastSocket.setSoTimeout(0);
										
							registeredName = tentativeName;		//Set user name
							JOptionPane.showMessageDialog(null, registeredName + ", you have been successfully registered!");
							lblStatus.setText(registeredName + ", you have been successfully registered!");
							
							//Enable and disable necessary buttons
							btnRegister.setVisible(false);
							txtUserName.setEditable(false);
							taGroupFriendList.setEditable(true);
							taMessage.setEditable(true);
							btnAddGroup.setEnabled(true);
							btnSend.setEnabled(true);
							btnAddFriend.setEnabled(true);
							
							//Run thread to listen broadcast group message
							new Thread(new Runnable() {
								@Override
								public void run() {
									
									//Keep running to receive message 
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
													
													//Display dialog when chat group of the same name was created by other user
													if (receivedMessage.equals("GroupExists|")) {
														JOptionPane.showMessageDialog(null, txtGroup.getText() + " already exists");
														lblStatus.setText(txtGroup.getText() + " already exists");
													}
												} 
												//This section run when chat group does not exist
												catch (SocketTimeoutException ex) {
													multicastBroadcastSocket.setSoTimeout(0);	//Clear timeout
													String groupName = txtGroup.getText();
													
													//Generate a randomise IP address of a new chat group
													Random rand = new Random();
													String groupIP = "235.1" + "." + (rand.nextInt(254)+1) + "." + (rand.nextInt(254)+3);
													System.out.println("New IP address: " + groupIP + " generated for group " + groupName);
													
													//Add group into own created list
													ownCreatedGroupList.add(groupName + "|" + groupIP);
													
													//Add group into current active group (To be display in the drop down list)
													groupList.add(groupName + "|" + groupIP);
													UpdateGroupList();	//Update group drop down list
													
													JOptionPane.showMessageDialog(null, groupName + " chat group has been successfully created!");
													btnJoinGroup.setEnabled(true);
													btnLeaveGroup.setEnabled(true);
													lblStatus.setText(groupName + " chat group has been successfully created!");
												}
	
												broadcastCreateGroup = false;
											}
											//Normal operation of reading received message 
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
			}
		});
		
		//Add friend button
		btnAddFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeFriendName = txtFriend.getText().trim();
				
				//Check for empty input
				if(tentativeFriendName.equals("")){
					JOptionPane.showMessageDialog(null, "Please enter a name!");
				}else{	
					//Check if input friend name is already in the friend list
					if (friendList.contains(tentativeFriendName)) {
						JOptionPane.showMessageDialog(null, tentativeFriendName + " is already your friend.");
						lblStatus.setText(tentativeFriendName + " is already your friend");
					} 
					//Check if input friend name is the user name
					else if(tentativeFriendName.equals(registeredName))
					{
						JOptionPane.showMessageDialog(null, tentativeFriendName + " is your own name.");
						lblStatus.setText(tentativeFriendName + " is your own name");
					}
					else {
						try {
							//Broadcast friend request over the network, only the specific friend will process the request
							String friendName = "CheckFriendName|" + tentativeFriendName + "|" + registeredName;
							byte[] sendBuf = friendName.getBytes();
							DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
							multicastBroadcastSocket.send(dgpSend);
							txtFriend.setText("");
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
		//Remove friend button
		btnDeleteFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Check for empty input
				if(txtFriend.getText().trim().equals("")){					
						JOptionPane.showMessageDialog(null, "Please enter a name!");
				}else{						
						if (friendList.contains(txtFriend.getText())) {
							//Remove friend from friend list
							friendList.remove(txtFriend.getText());
							UpdateFriendList();
							JOptionPane.showMessageDialog(null, txtFriend.getText() + " has been removed");
							lblStatus.setText(txtFriend.getText() + " has been removed from your friend list");
							
							//Enable and disable necessary buttons
							if (friendList.isEmpty()) {
								btnDeleteFriend.setEnabled(false);
								btnAddGroupFriend.setEnabled(false);
								btnPrivateChat.setEnabled(false);
							}
							
							txtFriend.setText("");
						} else {
							JOptionPane.showMessageDialog(null, txtFriend.getText() + " doesnt exists in your friend list!");
							lblStatus.setText(txtFriend.getText() + " doesnt exists in your friend list");
							txtFriend.setText("");
						}
					}
			}
		});
		
		//Create new chat group button
		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tentativeGroupName = txtGroup.getText().trim();
				
				//Check for empty input
				if (tentativeGroupName.equals("")){
					JOptionPane.showMessageDialog(null, "Please enter a name! ");
				}else{
					//Check whether the chat group is already exist in the user group list (To prevent duplication)
					if (CheckGroupListExist(tentativeGroupName)) {
						JOptionPane.showMessageDialog(null, tentativeGroupName + " group already exist");
						lblStatus.setText(tentativeGroupName + " group already exist");
					} else {
						try {
							//Broadcast group name to everyone in the network to check any user own the name of the tentative group name (To prevent duplication)
							broadcastCreateGroup = true;
							chatGroupThreadCreated = false;
							
							String checkGroupName = "CheckCreateGroupName|" + tentativeGroupName;
							byte[] sendBuf = checkGroupName.getBytes();
							DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
							multicastBroadcastSocket.send(dgpSend);
							
							System.out.println("Check for chat group: " + checkGroupName);
							lblStatus.setText("Creating new chat group. Please Wait...");
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		
		//Add friend to chat group button
		btnAddGroupFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String groupName = cobGroupList.getSelectedItem().toString();
				String friendName = cobFriendList.getSelectedItem().toString();
				
				try {
					//Add the specific friend of the selected chat group
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
		
		//Join chat group button, to start chat in the selected group
		btnJoinGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String groupName = cobGroupList.getSelectedItem().toString();
				String groupIP = GetGroupIP(groupName);
				
				//Send a leaving message to other user in the chat group
				SendLeavingMessage();
				
				try {
					multicastChatGroup = InetAddress.getByName(groupIP);
					multicastChatSocket = new MulticastSocket(PORT);
					multicastChatSocket.joinGroup(multicastChatGroup);

					lblMessageBox.setText("Current Chat: " + groupName + " Chat Group");
					
					//Clear previous group friend list
					groupFriendList.clear();
					UpdateGroupFriendList();
					
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
									lblStatus.setText("You have joined " + groupName + " chat group");
								}catch(IOException ex){
									ex.printStackTrace();
								}
								
								//Run to receive message for group chat
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
					else
					{						
						try{
							//Broadcast to know who are current online in this particular chat group
							String whoIsThere = "WhoIsThere|"+registeredName;
							
							byte[] buf = whoIsThere.getBytes();
							DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
							multicastChatSocket.send(dgpSend);
							lblStatus.setText("You have joined " + groupName + " chat group");
						}catch(IOException ex){
							ex.printStackTrace();
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		//Private chat button
		btnPrivateChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String friendName = cobFriendList.getSelectedItem().toString();
				String chatIP = GeneratePrivateChatIP();
				
					try {
						//Send request to start a private chat with the selected friend
						String privateChatRequest = friendName + "|PrivateChatRequest|" + registeredName + "|" + chatIP;
						byte[] sendBuf = privateChatRequest.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						System.out.println("Request " + friendName +  " to start private chat at IP address" + chatIP);
					} catch (IOException ex) {
											
						ex.printStackTrace();
					}
				
			}
		});
		
		//Leave the selected chat group button (Offline from chat group)
		btnLeaveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Send a leaving message to other user in the chat group
				SendLeavingMessage();
				try{
					multicastChatGroup = InetAddress.getByName(NO_GROUP);
					multicastChatSocket = new MulticastSocket(PORT);
					multicastChatSocket.joinGroup(multicastChatGroup);;
				}catch(IOException ex){
					ex.printStackTrace();
				}
				
				groupFriendList.clear();
				UpdateGroupFriendList();
				lblMessageBox.setText("Current Chat: None");
				lblStatus.setText("You have left previous chat");
			}
		});
		
		//Send message button
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					String msg = txtMessage.getText();
					msg = "Message|" + registeredName + ": " + msg;
					byte[] buf = msg.getBytes();
					
					//Send text message
					DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
					multicastChatSocket.send(dgpSend);
					txtMessage.setText("");
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
		getContentPane().add(btnPrivateChat);
		getContentPane().add(lblStatus);
		
	}
	
	//This function handle all action need to perform based on the message action header receive from the broadcast group
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
					
					//Dialog to accept or reject friend request
					int dialogResult = JOptionPane.showConfirmDialog(null, message + " would hello like to add you", "Friend Request", dialogButton);
					
					//Respond when accept friend request
					if (dialogResult == 0) {
						String sendMessage = message + "|FriendRequestAccepted|" + registeredName;
						byte[] sendBuf = sendMessage.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						
						
						friendList.add(message);	//Add to friend list
						UpdateFriendList();			//Update friend drop down list
						
						if(friendList.isEmpty()) {
							btnDeleteFriend.setEnabled(false);
							btnPrivateChat.setEnabled(false);
						} else {
							btnDeleteFriend.setEnabled(true);
							btnPrivateChat.setEnabled(true);
						}
						
						if(groupList.isEmpty())
						{
							btnAddGroupFriend.setEnabled(false);
						}
						else
						{
							btnAddGroupFriend.setEnabled(true);
						}
						
						System.out.println("Accept friend request: " + sendMessage);
						lblStatus.setText("Accept friend request by " + message);;
					}
					//Respond when reject friend request
					else {
						String sendMessage = message + "|FriendRequestRejected|" + registeredName;
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
		//Action when receive reply from previous request
		else if(action.equals(registeredName))
		{
			String replyAction = message.substring(0, message.indexOf("|"));	//Retrieve reply action
			message = message.substring(message.indexOf("|") + 1, message.length());
			
			//When friend request is accepted
			if(replyAction.equals("FriendRequestAccepted"))
			{
				//Update and display friend name in the friend drop down list
				friendList.add(message);
				UpdateFriendList();
				
				if(friendList.isEmpty())
				{
					btnDeleteFriend.setEnabled(false);
					btnPrivateChat.setEnabled(false);
				}
				else
				{
					btnDeleteFriend.setEnabled(true);
					btnPrivateChat.setEnabled(true);
				}
				
				if(groupList.isEmpty())
				{
					btnAddGroupFriend.setEnabled(false);
				}
				else
				{
					btnAddGroupFriend.setEnabled(true);
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
				int dialogButton = JOptionPane.YES_NO_OPTION;
				String friendName = message.substring(0, message.indexOf("|"));
				message = message.substring(message.indexOf("|") + 1, message.length());
				String groupName = message.substring(0, message.indexOf("|"));
				message = message.substring(message.indexOf("|") + 1, message.length());
				String groupIP = message;
				
				int dialogResult = JOptionPane.showConfirmDialog(null, friendName + " would like you to join " + groupName + " chat group!", "Join Chat Group", dialogButton);
				
				//Add chat group name into the group chat drop down list when user accepted the request
				if (dialogResult == 0) {
					groupList.add(groupName + "|" + groupIP);
					UpdateGroupList();
					btnJoinGroup.setEnabled(true);
					btnLeaveGroup.setEnabled(true);
					btnAddGroupFriend.setEnabled(true);
					lblStatus.setText(groupName + " chat group has beed added to your group chat list!");
				}
			}
			//Action for accepting a group chat
			else if(replyAction.equals("PrivateChatRequest"))
			{
				try {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					String friendName = message.substring(0, message.indexOf("|"));
					message = message.substring(message.indexOf("|") + 1, message.length());
					
					int dialogResult = JOptionPane.showConfirmDialog(null, friendName + " would like to have a private chat with you now. Do you acccept?", "Private Chat Request", dialogButton);
					
					//Start the private chat when user accepted the request
					if (dialogResult == 0) {
						//Send leave chat group message for previous chat
						SendLeavingMessage();
						
						//Connect to new chat address
						multicastChatGroup = InetAddress.getByName(message);
						multicastChatSocket = new MulticastSocket(PORT);
						multicastChatSocket.joinGroup(multicastChatGroup);
						
						lblMessageBox.setText("Current Chat: Private Chat with " + friendName);
						
						//Clear group friend list
						groupFriendList.clear();
						UpdateGroupFriendList();
						
						//Reply friend that private chat is accepted
						String replyPrivateChatRequest = friendName + "|PrivateChatAccepted|" + registeredName + "|" + message;
						byte[] sendBuf = replyPrivateChatRequest.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						
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
									UpdateGroupFriendList();
									
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
			//Action for friend accepted private chat session
			else if(replyAction.equals("PrivateChatAccepted"))
			{
				String friendName = message.substring(0, message.indexOf("|"));
				message = message.substring(message.indexOf("|") + 1, message.length());
				
				try {
					JOptionPane.showMessageDialog(null, friendName + " has accepted your private chat request!");
					
					//Send leaving message to inform other user of leaving the previous chat
					SendLeavingMessage();
					
					//Connect to new chat address
					multicastChatGroup = InetAddress.getByName(message);
					multicastChatSocket = new MulticastSocket(PORT);
					multicastChatSocket.joinGroup(multicastChatGroup);
					
					lblMessageBox.setText("Current Chat: Private chat with " + friendName);
					
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
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		//Action to respond group name already exist
		else if (action.equals("CheckCreateGroupName")) {
			//Check group name against own created group list
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
	
	//Function to update group chat friend list (Who is currently online list)
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
			
			//Received reply from who is online message for the current chat group
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
			
			//Update group friend list
			UpdateGroupFriendList();
		}
		//Normal message
		else if(action.equals("Message"))
		{
			taMessage.append(message + "\n");
		}
	}
	
	//Generate a IP address for private chat
	public String GeneratePrivateChatIP()
	{
		Random rand = new Random();
		return "235.2" + "." + (rand.nextInt(254)+2) + "." + (rand.nextInt(254)+2);
	}
	
	//Function to send leaving message to notify other user
	public void SendLeavingMessage()
	{
		if(multicastChatGroup != null)
		{
			//Broadcast to other member that this user is leaving the current group chat
			try{
				String msg = txtMessage.getText();
				msg = "LeftGroup|" + registeredName;
				byte[] buf = msg.getBytes();
				
				DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastChatGroup, PORT);
				multicastChatSocket.send(dgpSend);
				
				//Leave current multicast chat group
				multicastChatSocket.leaveGroup(multicastChatGroup);
				chatGroupThreadCreated = false;
				taMessage.setText("");
				System.out.println("Left to join another group");
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}