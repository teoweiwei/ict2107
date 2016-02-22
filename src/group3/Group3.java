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

public class Group3 extends JFrame {
	private static String BROADCAST_ADDRESS = "235.1.1.1";
	private static String Group_ADDRESS = "235.1.1.";
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

	/*
	 * MulticastSocket multicastSocket = null; InetAddress multicastGroup =
	 * null;
	 * 
	 * MulticastSocket multicastSocketCommon = null; InetAddress
	 * multicastGroupCommon = null; String ipAddressCommon = "228.1.1.1"; String
	 * ipAddress = "228.1.1."; String room=""; int port = 6789; String name =
	 * "";
	 */

	private JTextField txtUserName;
	private JTextField txtFriend;
	private JTextField txtGroup;
	private JTextField txtJoinGroup;
	private JTextField txtMessage;
	public JTextArea taFriendList = new JTextArea();
	public JTextArea taGroupList = new JTextArea();
	JButton btnAddFriend = new JButton("Add");

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
		/*
		 * try{ multicastGroupCommon = InetAddress.getByName(ipAddressCommon);
		 * multicastSocketCommon = new MulticastSocket(port);
		 * 
		 * //Join multicastSocketCommon.joinGroup(multicastGroupCommon);
		 * 
		 * //Create a new thread to keep listening for packets from the group
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run(){ byte buf1[] = new byte[1000];
		 * DatagramPacket dgpReceived = new DatagramPacket(buf1, buf1.length);
		 * while(true){ try{ multicastSocketCommon.receive(dgpReceived); byte[]
		 * receivedData = dgpReceived.getData(); int length =
		 * dgpReceived.getLength();
		 * 
		 * //Assumed we received string String msg = new String(receivedData, 0,
		 * length); room += msg; //System.out.println(room); }catch(IOException
		 * ex){ ex.printStackTrace(); } } } }).start();
		 * 
		 * }catch(IOException ex){ ex.printStackTrace(); }
		 */
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
		JButton btnAddFriend = new JButton("Add");

		JLabel lblGroup = new JLabel("Group");
		lblGroup.setBounds(8, 100, 100, 26);
		getContentPane().add(lblGroup);

		txtGroup = new JTextField();
		txtGroup.setBounds(108, 100, 240, 26);
		getContentPane().add(txtGroup);
		// txtGroup.setColumns(10);

		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/*
				 * try{ String msg = txtUserName.getText(); msg = name +
				 * "change name to " + msg; byte[] buf = msg.getBytes();
				 * DatagramPacket dgpSend = new DatagramPacket(buf, buf.length,
				 * multicastGroup, port); multicastSocket.send(dgpSend);
				 * }catch(IOException ex){ ex.printStackTrace(); }
				 */

				String tentativeName = txtUserName.getText();
				System.out.println();

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
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup,
								6789);
						multicastBroadcastSocket.send(dgpSend);

						byte receiveBuf[] = new byte[1000];
						DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);
						multicastBroadcastSocket.receive(dgpReceived); // discard
																		// own
																		// broadcast
																		// message

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

							if (receivedMessage.equals("NameTaken|")) {
								System.out.println(tentativeName + " already taken!");
								//txtUserName.setText("The name " + tentativeName + " is already taken!");
								
								JOptionPane.showMessageDialog(null, tentativeName + " is already taken!");
							}

						} catch (SocketTimeoutException ex) {
							registeredName = tentativeName;
							System.out.println(registeredName + " you are registered!");
							JOptionPane.showMessageDialog(null, "You have been registered");
							btnRegister.setEnabled(false);
							btnAddFriend.setEnabled(true);
							multicastBroadcastSocket.setSoTimeout(0); // Disable
																		// time
																		// out

							new Thread(new Runnable() {
								@Override
								public void run() {

									while (true) {
										byte receiveBuf[] = new byte[1000];
										DatagramPacket dgpReceived = new DatagramPacket(receiveBuf, receiveBuf.length);

										try {
											System.out.println("listening..");
											multicastBroadcastSocket.receive(dgpReceived);
// add friend
											if (broadcastFindFriend) {
												// byte receiveBuf[] = new
												// byte[1000];
												// DatagramPacket dgpReceived =
												// new
												// DatagramPacket(receiveBuf,
												// receiveBuf.length);
												// multicastBroadcastSocket.receive(dgpReceived);
												// // discard own broadcast
												// message

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

													if (receivedMessage.equals("FriendExists|")) {
														taFriendList.setText(txtFriend.getText());
														friendList.add(txtFriend.getText());
														// System.out.println(tentativeName
														// + " already taken!");
														// txtUserName.setText("The
														// name " +
														// tentativeName + " is
														// already taken!");
													}
													else if(receivedMessage.equals("FriendDoNotExists|"))
													{
														
														JOptionPane.showMessageDialog(null, txtFriend.getText()+"doesn't wish to be your friend");
													}

												} catch (SocketTimeoutException ex) {
													// registeredName =
													// tentativeName;
													System.out.println(
															txtFriend.getText().toString() + " does not exists");
													// btnRegister.setEnabled(false);
													multicastBroadcastSocket.setSoTimeout(0); // Disable
																								// time
																								// out

												}

												// multicastBroadcastSocket.receive(dgpReceived);
												broadcastFindFriend = false;
											} else {
												byte[] receivedData = dgpReceived.getData();
												int length = dgpReceived.getLength();

												String message = new String(receivedData, 0, length);
												System.out.println("Received broadcast message: " + message);

												DoAction(message);
											}
// add group
											if (broadcastAddGroup) {


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
														JOptionPane.showMessageDialog(null, txtGroup.getText()+"already exist");
													}

												} catch (SocketTimeoutException ex) {
													// registeredName =
													// tentativeName;
													System.out.println(txtGroup.getText().toString() + " group does not exists");
													// btnRegister.setEnabled(false);
													GroupList.add(txtGroup.getText());
													taGroupList.setText(txtGroup.getText());
													System.out.println(GroupList);
													multicastBroadcastSocket.setSoTimeout(0); // Disable
																								// time
																								// out
												}

												// multicastBroadcastSocket.receive(dgpReceived);
												broadcastFindFriend = false;
											} else {
												byte[] receivedData = dgpReceived.getData();
												int length = dgpReceived.getLength();

												String message = new String(receivedData, 0, length);
												System.out.println("Received broadcast message: " + message);

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
		btnRegister.setBounds(356, 8, 120, 26);
		getContentPane().add(btnRegister);

		// JButton btnAddFriend = new JButton("Add");
		btnAddFriend.setEnabled(false);
		btnAddFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tentativeFriendName = txtFriend.getText();

//				if (registeredUser.contains(tentativeFriendName)) {
//					taFriendList.setText(tentativeFriendName);
//					// System.out.println(tentativeFriendName + " already in
//					// your friend list|");
//				} else {
					try {
						// if(!broadcastConnect)
						// {
						// multicastBroadcastGroup =
						// InetAddress.getByName(BROADCAST_ADDRESS);
						// multicastBroadcastSocket = new MulticastSocket(PORT);
						// multicastBroadcastSocket.joinGroup(multicastBroadcastGroup);
						// broadcastConnect = true;
						// }
						broadcastFindFriend = true;
						String friendName = "CheckFriendName|" + tentativeFriendName;
						byte[] sendBuf = friendName.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup,
								6789);
						multicastBroadcastSocket.send(dgpSend);

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				//}
			}
		});
		btnAddFriend.setBounds(356, 54, 120, 26);
		getContentPane().add(btnAddFriend);

		JButton btnDeleteFriend = new JButton("Delete");
		btnDeleteFriend.setBounds(484, 54, 120, 26);
		getContentPane().add(btnDeleteFriend);

		JButton btnAddGroup = new JButton("Add");
		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tentativeGroupName = txtGroup.getText();

				if (GroupList.contains(tentativeGroupName)) {
					taGroupList.setText(tentativeGroupName);
					System.out.println(tentativeGroupName + " already in your group list|");
				} else {
					try {
						broadcastAddGroup = true;
						String friendName = "CheckAddGroup|" + tentativeGroupName;
						byte[] sendBuf = friendName.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup,
								6789);
						multicastBroadcastSocket.send(dgpSend);

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
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
		// txtJoinGroup.setColumns(10);

		JButton btnJoinGroup = new JButton("Join");
		btnJoinGroup.setBounds(484, 150, 120, 26);
		getContentPane().add(btnJoinGroup);

		JButton btnLeaveGroup = new JButton("Leave");
		btnLeaveGroup.setBounds(612, 150, 120, 26);
		getContentPane().add(btnLeaveGroup);

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
			if (message.equals(registeredName)) {
				try {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(null,"Someone would like to add you","Friend Request",dialogButton);
					if(dialogResult ==0)
					{
						String sendMessage = "FriendExists|";
						byte[] sendBuf = sendMessage.getBytes();
						DatagramPacket dgpSend = new DatagramPacket(sendBuf, sendBuf.length, multicastBroadcastGroup, PORT);
						multicastBroadcastSocket.send(dgpSend);
						System.out.println(sendMessage);
					}
					else
					{
						String sendMessage = "FriendDoNotExists|";
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
