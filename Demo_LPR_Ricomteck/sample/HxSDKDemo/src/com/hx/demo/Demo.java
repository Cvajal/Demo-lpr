package com.hx.demo;

import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.border.LineBorder;

import ice_ipcsdk.LprResult;
import ice_ipcsdk.SDK;
import ice_ipcsdk.SDK.IDeviceEventCallback;
import ice_ipcsdk.SDK.IIOEventCallback;
import ice_ipcsdk.SDK.ILprResultCallback;

import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

public class Demo extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldIP1;
	private JTextField textFieldIP2;
	private JCheckBox chckbxEncrypte1, chckbxEncrypte2;
	private JLabel lblStatus1;
	private JLabel lblStatus2;
	private JButton btnVideo1, btnVideo2;
	private JButton btnRegLprEvent1, btnRegLprEvent2;
	private JButton btnRegIoEvent1, btnRegIoEvent2;
	private JButton btnRegRsEvent1, btnRegRsEvent2;
	private JPanel panel_video[] = new JPanel[2];
	private JPanel panel_fullImg[] = new JPanel[2];
	JTextArea textAreaInfo;
	private SDK[] sdk = new SDK[2];
	static LprResultCallback[] lprCallback = new LprResultCallback[2];
	static DeviceEventCallback[] deviceEventCallback = new DeviceEventCallback[2];
	static IOEventCallback[] ioEventCallback = new IOEventCallback[2];
	static mjpeg_callback_static[] mjpegCallback = new mjpeg_callback_static[2];
	static RS485EventCallback[] rs485Callback = new RS485EventCallback[2];
	static RS485EventCallback2[] rs485Callback2 = new RS485EventCallback2[2];
	static boolean[] bVideo = new boolean[2];
	static boolean[] bRegLpr = new boolean[2];
	static boolean[] bRegIO = new boolean[2];
	static boolean[] bRegRS485 = new boolean[2];
	static boolean bExit = false;
	Lock lock =  new ReentrantLock();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Demo frame = new Demo();
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
	public Demo() {
		init();
		setTitle("HxSDKDemo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 920, 829);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 10, 427, 556);
		contentPane.add(panel);
		panel.setLayout(null);
		
		chckbxEncrypte1 = new JCheckBox("Encrypted connection");
		chckbxEncrypte1.setBounds(6, 6, 155, 23);
		panel.add(chckbxEncrypte1);
		
		JLabel lblNewLabel = new JLabel("Entrance IP:");
		lblNewLabel.setBounds(6, 35, 79, 15);
		panel.add(lblNewLabel);
		
		textFieldIP1 = new JTextField();
		textFieldIP1.setText("192.168.55.100");
		textFieldIP1.setBounds(81, 32, 106, 21);
		panel.add(textFieldIP1);
		textFieldIP1.setColumns(10);
		
		JButton btnConnect1 = new JButton("connect");
		btnConnect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connect(0);
			}
		});
		btnConnect1.setBounds(207, 31, 93, 23);
		panel.add(btnConnect1);
		
		JButton btnDisconnect1 = new JButton("disconnect");
		btnDisconnect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect(0);
			}
		});
		btnDisconnect1.setBounds(310, 31, 109, 23);
		panel.add(btnDisconnect1);
		
		lblStatus1 = new JLabel("status:offline");
		lblStatus1.setBounds(6, 60, 93, 15);
		panel.add(lblStatus1);
		
		JButton btnOpenGate1 = new JButton("open gate");
		btnOpenGate1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openGate(0);
			}
		});
		btnOpenGate1.setBounds(104, 56, 93, 23);
		panel.add(btnOpenGate1);
		
		JButton btnSwtrigger1 = new JButton("swtrigger");
		btnSwtrigger1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				swtrigger(0);
			}
		});
		btnSwtrigger1.setBounds(207, 56, 93, 23);
		panel.add(btnSwtrigger1);
		
		btnVideo1 = new JButton("start video");
		btnVideo1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video(0);
			}
		});
		btnVideo1.setBounds(310, 56, 109, 23);
		panel.add(btnVideo1);
		
		btnRegLprEvent1 = new JButton("reg lpr event");
		btnRegLprEvent1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regLprEvent(0);
			}
		});
		btnRegLprEvent1.setBounds(6, 82, 124, 23);
		panel.add(btnRegLprEvent1);
		
		btnRegIoEvent1 = new JButton("reg IO event");
		btnRegIoEvent1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regIOEvent(0);
			}
		});
		btnRegIoEvent1.setBounds(132, 82, 135, 23);
		panel.add(btnRegIoEvent1);
		
		btnRegRsEvent1 = new JButton("reg RS485 event");
		btnRegRsEvent1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regRS485Event(0);
			}
		});
		btnRegRsEvent1.setBounds(270, 82, 149, 23);
		panel.add(btnRegRsEvent1);
		
		JLabel lblNewLabel_1 = new JLabel("Entrance video");
		lblNewLabel_1.setBounds(16, 115, 109, 15);
		panel.add(lblNewLabel_1);
		
		JPanel panel_video1 = new JPanel();
		panel_video1.setBackground(Color.LIGHT_GRAY);
		panel_video1.setBounds(16, 130, 390, 200);
		panel.add(panel_video1);
		panel_video[0] = panel_video1;
		
		JPanel panel_pic1 = new JPanel();
		panel_pic1.setBackground(Color.LIGHT_GRAY);
		panel_pic1.setBounds(16, 352, 390, 200);
		panel.add(panel_pic1);
		panel_fullImg[0] = panel_pic1;
		
		JLabel lblNewLabel_1_1 = new JLabel("Entrance picture");
		lblNewLabel_1_1.setBounds(16, 334, 109, 15);
		panel.add(lblNewLabel_1_1);
		
		passwordField1 = new JPasswordField();
		passwordField1.setBounds(167, 7, 206, 21);
		panel.add(passwordField1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(460, 10, 427, 556);
		contentPane.add(panel_1);
		
		chckbxEncrypte2 = new JCheckBox("Encrypted connection");
		chckbxEncrypte2.setBounds(6, 6, 155, 23);
		panel_1.add(chckbxEncrypte2);
		
		JLabel lblNewLabel_2 = new JLabel("Exit IP:");
		lblNewLabel_2.setBounds(6, 35, 79, 15);
		panel_1.add(lblNewLabel_2);
		
		textFieldIP2 = new JTextField();
		textFieldIP2.setText("192.168.55.101");
		textFieldIP2.setColumns(10);
		textFieldIP2.setBounds(81, 32, 106, 21);
		panel_1.add(textFieldIP2);
		
		JButton btnConnect2 = new JButton("connect");
		btnConnect2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connect(1);
			}
		});
		btnConnect2.setBounds(207, 31, 93, 23);
		panel_1.add(btnConnect2);
		
		JButton btnDisconnect12 = new JButton("disconnect");
		btnDisconnect12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect(1);
			}
		});
		btnDisconnect12.setBounds(310, 31, 109, 23);
		panel_1.add(btnDisconnect12);
		
		lblStatus2 = new JLabel("status:offline");
		lblStatus2.setBounds(6, 60, 93, 15);
		panel_1.add(lblStatus2);
		
		JButton btnOpenGate2 = new JButton("open gate");
		btnOpenGate2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openGate(1);
			}
		});
		btnOpenGate2.setBounds(104, 56, 93, 23);
		panel_1.add(btnOpenGate2);
		
		JButton btnSwtrigger2 = new JButton("swtrigger");
		btnSwtrigger2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				swtrigger(1);
			}
		});
		btnSwtrigger2.setBounds(207, 56, 93, 23);
		panel_1.add(btnSwtrigger2);
		
		btnVideo2 = new JButton("start video");
		btnVideo2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				video(1);
			}
		});
		btnVideo2.setBounds(310, 56, 109, 23);
		panel_1.add(btnVideo2);
		
		btnRegLprEvent2 = new JButton("reg lpr event");
		btnRegLprEvent2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regLprEvent(1);
			}
		});
		btnRegLprEvent2.setBounds(6, 82, 124, 23);
		panel_1.add(btnRegLprEvent2);
		
		btnRegIoEvent2 = new JButton("reg IO event");
		btnRegIoEvent2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regIOEvent(1);
			}
		});
		btnRegIoEvent2.setBounds(132, 82, 135, 23);
		panel_1.add(btnRegIoEvent2);
		
		btnRegRsEvent2 = new JButton("reg RS485 event");
		btnRegRsEvent2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regRS485Event(1);
			}
		});
		btnRegRsEvent2.setBounds(270, 82, 149, 23);
		panel_1.add(btnRegRsEvent2);
		
		JLabel lblNewLabel_1_2 = new JLabel("Exit video");
		lblNewLabel_1_2.setBounds(16, 115, 109, 15);
		panel_1.add(lblNewLabel_1_2);
		
		JPanel panel_video2 = new JPanel();
		panel_video2.setBackground(Color.LIGHT_GRAY);
		panel_video2.setBounds(16, 130, 390, 200);
		panel_1.add(panel_video2);
		panel_video[1] = panel_video2;
		
		JPanel panel_pic2 = new JPanel();
		panel_pic2.setBackground(Color.LIGHT_GRAY);
		panel_pic2.setBounds(16, 352, 390, 200);
		panel_1.add(panel_pic2);
		panel_fullImg[1] = panel_pic2;
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Exit picture");
		lblNewLabel_1_1_1.setBounds(16, 334, 109, 15);
		panel_1.add(lblNewLabel_1_1_1);
		
		passwordField2 = new JPasswordField();
		passwordField2.setBounds(160, 7, 206, 21);
		panel_1.add(passwordField2);
		
		JLabel lblNewLabel_3 = new JLabel("info");
		lblNewLabel_3.setBounds(10, 576, 54, 15);
		contentPane.add(lblNewLabel_3);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 602, 884, 164);
		contentPane.add(scrollPane);
		
		textAreaInfo = new JTextArea();
		textAreaInfo.setEditable(false);
		scrollPane.setViewportView(textAreaInfo);
		
	}
	
	public void init() {
		for (int i = 0; i < 2; i++) {
			sdk[i] = null;
			bVideo[i] = false;
			bRegLpr[i] = false;
			bRegIO[i] = false;
			bRegRS485[i] = false;
			deviceEventCallback[i] = new DeviceEventCallback(i);
			ioEventCallback[i] = new IOEventCallback(i);
			lprCallback[i] = new LprResultCallback(i);
			mjpegCallback[i] = new mjpeg_callback_static(i);
			rs485Callback[i] = new RS485EventCallback(i);
			rs485Callback2[i] = new RS485EventCallback2(i);
		}
	}
	
	static int count = 0;
	private JPasswordField passwordField1;
	private JPasswordField passwordField2;
	public void insertInfo(String info)
    {
		lock.lock();
		info += "\r\n";
		if (textAreaInfo.getLineCount() > 100) {
			textAreaInfo.setText("");
		}
		textAreaInfo.insert(info, 0);
		lock.unlock();
    }
	
	public void connect(int index) {
		if (null != sdk[index]){
		insertInfo("The camera has been connected");
		return;
		}
		sdk[index] = new SDK();
		int iRet = 0;
		sdk[index].ICE_IPCSDK_SetDeviceEventCallBack(deviceEventCallback[index]);
		if (0 == index) {
			//Encrypted connection
			if (chckbxEncrypte1.isSelected()) {
				iRet = sdk[index].ICE_IPCSDK_OpenDevice_Passwd(textFieldIP1.getText(), new String(passwordField1.getPassword()), null);
			}else {
				iRet = sdk[index].ICE_IPCSDK_OpenDevice(textFieldIP1.getText());
			}
			sdk[index].ICE_IPCSDK_LogConfig(1, textFieldIP1.getText());			
		}
		else {
			//Encrypted connection
			if (chckbxEncrypte2.isSelected()) {
				iRet = sdk[index].ICE_IPCSDK_OpenDevice_Passwd(textFieldIP2.getText(), new String(passwordField2.getPassword()), null);
			}else {
				iRet = sdk[index].ICE_IPCSDK_OpenDevice(textFieldIP2.getText());
			}
			sdk[index].ICE_IPCSDK_LogConfig(1, textFieldIP2.getText());
		}
		
		if (1 != iRet) {
			sdk[index].ICE_IPCSDK_Close();
			sdk[index] = null;
			insertInfo("Camera connection failed");
			return;
		}
		else {			
			sdk[index].ICE_IPCSDK_SetLprResultCallback(lprCallback[index]);
			sdk[index].ICE_ICPSDK_SetSerialPortCallback(rs485Callback[index]);
			sdk[index].ICE_ICPSDK_SetSerial232PortCallback(rs485Callback2[index]);
			sdk[index].ICE_IPCSDK_SetIOEventCallBack(ioEventCallback[index]);
			regLprEvent(index);
			regIOEvent(index);
			regRS485Event(index);
		}
	
	}
	
	public void disconnect(int index) {
		if (null == sdk[index]){
			insertInfo("The camera is not connected");
		return;
		}
		sdk[index].ICE_IPCSDK_Close();
		sdk[index] = null;
	
		if (0 == index) {
			lblStatus1.setText("status:offline");
			btnVideo1.setText("start video");
			btnRegLprEvent1.setText("reg lpr event");
			btnRegIoEvent1.setText("reg io event");
			btnRegRsEvent1.setText("reg RS485 event");
		}
		else {
			lblStatus2.setText("status:offline");
			btnVideo2.setText("start video");
			btnRegLprEvent2.setText("reg lpr event");
			btnRegIoEvent2.setText("reg io event");
			btnRegRsEvent2.setText("reg RS485 event");
		}
		bVideo[index] = false;
		bRegLpr[index] = false;
		bRegIO[index] = false;
		bRegRS485[index] = false;
	}
	
	public void regLprEvent(int index) {
		if (null == sdk[index]){
			insertInfo("The camera is not connected");
			return;
		}
		
		int iRet = 0;
		if (!bRegLpr[index]) {
			iRet = sdk[index].ICE_IPCSDK_RegLprEvent(true);
			if (1 == iRet) {
				bRegLpr[index] = true;
				insertInfo("Register lpr event successfully!");
				if (0 == index)
					btnRegLprEvent1.setText("unreg lpr event");
				else
					btnRegLprEvent2.setText("unreg lpr event");
			}else {
				insertInfo("Register lpr event failed!");
			}
		}else {
			iRet = sdk[index].ICE_IPCSDK_UnregLprEvent();
			if (1 == iRet) {
				bRegLpr[index] = false;
				insertInfo("Unregister lpr event successfully!");
				if (0 == index)
					btnRegLprEvent1.setText("reg lpr event");
				else
					btnRegLprEvent2.setText("reg lpr event");
			}else {
				insertInfo("Unregister lpr event failed!");
			}
		}
	}
	
	public void regIOEvent(int index) {
		if (null == sdk[index]){
			insertInfo("The camera is not connected");
			return;
		}
		
		int iRet = 0;
		if (!bRegIO[index]) {
			iRet = sdk[index].ICE_IPCSDK_RegGpioEvent();
			if (1 == iRet) {
				bRegIO[index] = true;
				insertInfo("Register gpio event successfully!");
				if (0 == index)
					btnRegIoEvent1.setText("unreg io event");
				else
					btnRegIoEvent2.setText("unreg io event");
			}else {
				insertInfo("Register gpio event failed!");
			}
		}else {
			iRet = sdk[index].ICE_IPCSDK_UnregGpioEvent();
			if (1 == iRet) {
				bRegIO[index] = false;
				insertInfo("Unregister gpio event successfully!");
				if (0 == index)
					btnRegIoEvent1.setText("reg io event");
				else
					btnRegIoEvent2.setText("reg io event");
			}else {
				insertInfo("unRegister gpio event failed!");
			}
		}
		
	}
	
	public void regRS485Event(int index) {
		if (null == sdk[index]){
			insertInfo("The camera is not connected");
			return;
		}
		
		int iRet = 0;
		if (!bRegRS485[index]) {
			iRet = sdk[index].ICE_IPCSDK_RegRS485Event(3);
			if (1 == iRet) {
				bRegRS485[index] = true;
				insertInfo("Register RS485 event successfully!");
				if (0 == index)
					btnRegRsEvent1.setText("unreg RS485 event");
				else
					btnRegRsEvent2.setText("unreg RS485 event");
			}else {
				insertInfo("Register RS485 event failed!");
			}
		}else {
			iRet = sdk[index].ICE_IPCSDK_UnregRS485Event();
			if (1 == iRet) {
				bRegRS485[index] = false;
				insertInfo("Unregister RS485 event successfully!");
				if (0 == index)
					btnRegRsEvent1.setText("reg RS485 event");
				else
					btnRegRsEvent2.setText("reg RS485 event");
			}else {
				insertInfo("Unregister RS485 event failed!");
			}
		}
		
	}
	
	public void swtrigger(int index) {
		if (null == sdk[index]){
			insertInfo("The camera is not connected");
			return;
		}
		int iRet = sdk[index].ICE_IPCSDK_TriggerExt();
		if (0 == iRet){
			insertInfo("Trigger failed");
		}
	}
	
	public void openGate(int index) {
		if (null == sdk[index]){
			insertInfo("The camera is not connected");
			return;
		}
		int iRet = sdk[index].ICE_IPCSDK_OpenGate();
		if (1 != iRet){
			insertInfo("Open gate failed");
		}
	}
	
	public void video(int index) {
		if (null == sdk[index]){
			insertInfo("The camera is not connected");
			return;
		}
		if (!bVideo[index]){
			sdk[index].ICE_IPCSDK_SetMJpegallback_Static(mjpegCallback[index]);
			if (0 == index)
				btnVideo1.setText("stop  video");
			else
				btnVideo2.setText("stop  video");
			bVideo[index] = true;
		}
		else {
			sdk[index].ICE_IPCSDK_SetMJpegallback_Static(null);
			if (0 == index)
				btnVideo1.setText("start  video");
			else
				btnVideo2.setText("start  video");
			bVideo[index] = false;
		}
	}
	
	public class LprResultCallback implements ILprResultCallback{
		 int index = 0;
		 public LprResultCallback(int index) {
			// TODO Auto-generated constructor stub
			 this.index = index;
		}
		 
		 public void savePic(String strIP, String strNumber, byte[] data, int off, int len, int type, long lTime)
			{
				String strPath = "D:/capture_java/";
				Date now = null;
				
				if(lTime == 0)
					now = new Date();
				else
					now = new Date(lTime);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String dateNow = dateFormat.format(now);
				String dateNow2 = dateFormat2.format(now);
				
				strPath += strIP + "/" + dateNow;
				File file = new File(strPath);
				if(!file.exists())
				{
					file.mkdirs();
				}
				String filename = strPath + "/" + dateNow2 + "_" + strNumber;
				filename = filename.trim();
				
				if(1 == type)//plate
				{
					filename += "_plate";
				}
				filename += ".jpg";
				
				
				try {
					DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
					out.write(data, off, len);
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		@Override
		public void ICE_IPCSDK_LprResult(String strIP, LprResult lprResult, byte[] bData, int iFullPicOffset, int iFullPicLen, 
				int iPlatePicOffset, int iPlatePicLen, int iReserve1, int iReserve2) {
			// TODO Auto-generated method stub
			String strText = (index == 0 ? "[Entrance]:" : "[Exit]:" ) + strIP + "," + lprResult.getStrPlateNum() + "," + lprResult.getStrPlateColor() 
			+ "," + lprResult.getStrLprState() + ",";
			strText += lprResult.getStrVehicleType() + ",false plate:" + lprResult.isbFalsePlate() + "," + lprResult.getfConfidence() + ",";
			strText += lprResult.getStrVehicleColor() + "," + lprResult.getStrVehicleDir() + "," + lprResult.getStrTriggerType() + "," + lprResult.getStrBwList() +
					"," + lprResult.getStrLogName() + "," + lprResult.getStrSubLogName() + "," + lprResult.getStrProductYearName();
			
			insertInfo(strText);
			
			if(iFullPicLen > 0){
				savePic(strIP, lprResult.getStrPlateNum(), bData, iFullPicOffset, iFullPicLen, 0, 0);
				//显示图片
				try {
					byte[] bFullPicData = new byte[iFullPicLen];
					System.arraycopy(bData, iFullPicOffset, bFullPicData, 0, iFullPicLen);
					InputStream inputStream = new ByteArrayInputStream(bFullPicData);
					BufferedImage bufferedImage = ImageIO.read(inputStream);
					Graphics g = panel_fullImg[index].getGraphics();
					g.drawImage(bufferedImage, 0, 0, 390, 200, null);
			
					g = null;
					bufferedImage = null;
					inputStream = null;
					bFullPicData = null;
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
	
			if(iPlatePicLen > 0){
				savePic(strIP, lprResult.getStrPlateNum(), bData, iPlatePicOffset, iPlatePicLen, 1, 0);
			}
		}
		
	}
	 
	 public class DeviceEventCallback implements IDeviceEventCallback{
		 int index = 0;
		 public DeviceEventCallback(int index) {
			// TODO Auto-generated constructor stub
			 this.index = index;
		}
		@Override
		public void ICE_IPCSDK_OnDeviceEvent(String strIP, int nEventType, int nEventData1,
				int nEventData2, int nEventData3, int nEventData4) {
			// TODO Auto-generated method stub
			if (0 == index) {
				if (0 == nEventType) {
					lblStatus1.setText("status:offline");
				}
				else if (1 == nEventType) {
					lblStatus1.setText("status:online ");
				}
			}else if (1 == index) {
				if (0 == nEventType) {
					lblStatus2.setText("status:offline");
				}
				else if (1 == nEventType) {
					lblStatus2.setText("status:online ");
				}
			}
		}
		 
	 }
	 
	 public class IOEventCallback implements IIOEventCallback{
		 int index = 0;
		 public IOEventCallback(int index) {
			// TODO Auto-generated constructor stub
			 this.index = index;
		}
		@Override
		public void ICE_IPCSDK_OnIOEvent(String strIP, int nEventType, int nEventData1,
				int nEventData2, int nEventData3, int nEventData4) {
			// TODO Auto-generated method stub
			if (0 == index) {
				if (0 == nEventType) {
					String strText = "[Entrance]:"+ strIP + " IO status changed,value is:" + nEventData1 + nEventData2 + nEventData3 + nEventData4;
					insertInfo(strText);
				}
			}else if (1 == index) {
				if (0 == nEventType) {
					String strText = "[Exit]:"+ strIP + " IO status changed,value is:" + nEventData1 + nEventData2 + nEventData3 + nEventData4;
					insertInfo(strText);
				}
			}
		}
		 
	 }
	 
	//获取mjpeg视频流回调
		public  class mjpeg_callback_static implements SDK.IMJpegCallback_Static {
			int index;
			public mjpeg_callback_static(int index) {
				// TODO Auto-generated constructor stub
				this.index = index;
			}
			public void ICE_IPCSDK_MJpeg(String strIP, byte[] bData, int length)
			{
					//显示视频
					try {
						byte[] bMjpegData = new byte[length];
						System.arraycopy(bData, 0, bMjpegData, 0, length);
						InputStream inputStream = new ByteArrayInputStream(bMjpegData);
						BufferedImage bufferedImage = ImageIO.read(inputStream);
						Graphics g = panel_video[index].getGraphics();
						g.drawImage(bufferedImage, 0, 0, 390, 210, null);
						
						g = null;
						bufferedImage = null;
						inputStream = null;
						bMjpegData = null;
					} catch (IOException e) {
						//e.printStackTrace();
					}
			}
		}
		
		public  String bytesToHexString(byte[] src, int nOffset, int nLen){   
		    StringBuilder stringBuilder = new StringBuilder("");   
		    if (src == null || nLen <= 0) {   
		        return null;   
		    }   
		    for (int i = nOffset; i < nLen; i++) {   
		        int v = src[i] & 0xFF;   
		        String hv = Integer.toHexString(v);   
		        if (hv.length() < 2) {   
		            stringBuilder.append(0);   
		        }   
		        stringBuilder.append(hv);   
		    }   
		    return stringBuilder.toString();   
		} 
		
		public class RS485EventCallback implements SDK.ISerialPortCallback{
			int index = 0;
			public RS485EventCallback(int index) {
				// TODO Auto-generated constructor stub
				this.index = index;
			}
			
			@Override
			public void ICE_IPCSDK_SerialPort(String strIP, byte[] bData,
					int nOffset, int nLen) {
				// TODO Auto-generated method stub
				String strText = (index == 0 ? "[Entrance]:" : "[Exit]:" ) + strIP + "Recv RS485 data," + bytesToHexString(bData, nOffset, nLen);
				insertInfo(strText);
			}
			
		}
		
		public class RS485EventCallback2 implements SDK.ISerialPortCallback{
			int index = 0;
			public RS485EventCallback2(int index) {
				// TODO Auto-generated constructor stub
				this.index = index;
			}
			
			@Override
			public void ICE_IPCSDK_SerialPort(String strIP, byte[] bData,
					int nOffset, int nLen) {
				// TODO Auto-generated method stub
				String strText = (index == 0 ? "[Entrance]:" : "[Exit]:" ) + strIP + "Recv RS485-2 data," + bytesToHexString(bData, nOffset, nLen);
				insertInfo(strText);
			}
			
		}
}
