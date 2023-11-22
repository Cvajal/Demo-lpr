package com.hx.demo;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.hx.demo.callback.IListCtrlCallback;

import ice_ipcsdk.SDK;
import ice_ipcsdk.struct.WhiteListItem;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;

public class WBListDemo extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldSum;
	private JTextField textFieldPlate;
	private JTextField textFieldIP;
	private  DefaultListModel<String> listModel;
	private DefaultTableModel tableModel;   
	private WBListTable tableWBList;
	private SDK sdk;
	private boolean bGet = false;
	private GetListThread getListThread = null;
	private AddListCallback addListCallback;
	private EditListCallback editListCallback;
	private int nSelPos = -1;
	private int nListCount = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WBListDemo frame = new WBListDemo();
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
	public WBListDemo() {
		setTitle("B/W List Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 580);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		addListCallback = new AddListCallback();
		editListCallback = new EditListCallback();
		
		JLabel lblBwList = new JLabel("B/W list:");
		lblBwList.setBounds(21, 65, 93, 15);
		contentPane.add(lblBwList);
		
		JButton btnGet = new JButton("Refresh");
		btnGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!bGet) {
					getListThread = new GetListThread();
					getListThread.start();
				}
			}
		});
		btnGet.setBounds(21, 91, 93, 23);
		contentPane.add(btnGet);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addWBList();
			}
		});
		btnAdd.setBounds(116, 91, 93, 23);
		contentPane.add(btnAdd);
		
		JButton btnDel = new JButton("Delete");
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delWBList();
			}
		});
		btnDel.setBounds(211, 91, 93, 23);
		contentPane.add(btnDel);
		
		JButton btnDelAll = new JButton("Delete all");
		btnDelAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delAllWBList();
			}
		});
		btnDelAll.setBounds(305, 91, 93, 23);
		contentPane.add(btnDelAll);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editWBList();
			}
		});
		btnEdit.setBounds(401, 91, 93, 23);
		contentPane.add(btnEdit);
		
		JLabel lblTotalNumber = new JLabel("Total number:");
		lblTotalNumber.setBounds(504, 94, 93, 15);
		contentPane.add(lblTotalNumber);
		
		textFieldSum = new JTextField();
		textFieldSum.setText("0");
		textFieldSum.setEditable(false);
		textFieldSum.setColumns(10);
		textFieldSum.setBounds(602, 92, 83, 21);
		contentPane.add(textFieldSum);
		
		JButton btnFind = new JButton("Find");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				findWBList();
			}
		});
		btnFind.setBounds(211, 120, 93, 23);
		contentPane.add(btnFind);
		
		textFieldPlate = new JTextField();
		textFieldPlate.setColumns(10);
		textFieldPlate.setBounds(126, 121, 83, 21);
		contentPane.add(textFieldPlate);
		
		JLabel lblNewLabel_3 = new JLabel("Plate number:");
		lblNewLabel_3.setBounds(21, 124, 93, 15);
		contentPane.add(lblNewLabel_3);
		
		String[] cols = {"LP", "Start Date", "End Date", "Start Time", "End Time", "Type", "Note"};
		String[][] tableValues = null;
		tableModel = new DefaultTableModel(tableValues, cols);
		tableWBList = new WBListTable(tableModel);		
		JScrollPane scrollPaneList = new JScrollPane(tableWBList);
		scrollPaneList.setBounds(21, 149, 853, 382);
		contentPane.add(scrollPaneList);
		
		textFieldIP = new JTextField();
		textFieldIP.setText("192.168.55.100");
		textFieldIP.setBounds(60, 34, 103, 21);
		contentPane.add(textFieldIP);
		textFieldIP.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (null != sdk) {
					return;
				}
				sdk = new SDK();
				int iRet = sdk.ICE_IPCSDK_OpenDevice(textFieldIP.getText());
				if (1 != iRet) {
					JOptionPane.showMessageDialog(WBListDemo.this, "Connect failed!");
					sdk.ICE_IPCSDK_Close();
					sdk = null;
				}else {
					JOptionPane.showMessageDialog(WBListDemo.this, "Connect successfully!");
				}
			}
		});
		btnConnect.setBounds(173, 33, 93, 23);
		contentPane.add(btnConnect);
		
		JLabel lblNewLabel = new JLabel("IP:");
		lblNewLabel.setBounds(22, 36, 28, 15);
		contentPane.add(lblNewLabel);
		
		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (null == sdk) {
					JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (null != getListThread) {
					try {
						getListThread.join();
						getListThread = null;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				sdk.ICE_IPCSDK_Close();
				sdk = null;
				tableModel.setRowCount(0);
				nListCount = 0;
				textFieldSum.setText(String.valueOf(nListCount));
			}
		});
		btnDisconnect.setBounds(277, 33, 108, 23);
		contentPane.add(btnDisconnect);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				if (null != sdk) {
					sdk.ICE_IPCSDK_Close();
					sdk = null;
				}
			}
		});
		
	}
	
	private class GetListThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (null == sdk) {
				JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
				return;
			}
			tableModel.setRowCount(0);
			bGet = true;
			nListCount = sdk.ICE_IPCSDK_WhiteListGetCount();
			if (-1 == nListCount) {
				JOptionPane.showMessageDialog(WBListDemo.this, "Refresh failed!", "info",JOptionPane.WARNING_MESSAGE);
				bGet = false;
				return;
			}
			textFieldSum.setText(String.valueOf(nListCount));
			if (nListCount > 0) {				
				for (int i = 0; i < nListCount; i++) {
					WhiteListItem listItem = sdk.ICE_IPCSDK_WhiteListGetItem(i);
					if (null != listItem) {
						Object[] strList = new Object[]{listItem.getStrPlate(), listItem.getStrDateBegin(), 
								listItem.getStrDateEnd(), listItem.getStrTimeBegin(), listItem.getStrTimeEnd(), 
								listItem.getStrType(), listItem.getStrRemark()};
						tableModel.addRow(strList);
					}
				}
			}
			bGet = false;
		}
		
	}
	
	private void delWBList() {
		if (null == sdk) {
			JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int row = tableWBList.getSelectedRow();
		if (-1 != row) {
			String strPlate = (String) tableModel.getValueAt(row, 0);
			boolean bRet = sdk.ICE_IPCSDK_WhiteListDeleteItem_ByNumber(strPlate);
			if (bRet) {
				tableModel.removeRow(row);
				nListCount--;
				textFieldSum.setText(String.valueOf(nListCount));
				JOptionPane.showMessageDialog(WBListDemo.this, "Delete successfully", "info",JOptionPane.WARNING_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(WBListDemo.this, "Delete failed", "info",JOptionPane.WARNING_MESSAGE);
			}
		}else {
			JOptionPane.showMessageDialog(WBListDemo.this, "Please choose a W/B list item", "info",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void delAllWBList() {
		if (null == sdk) {
			JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
			return;
		}
		boolean bRet = sdk.ICE_IPCSDK_WhiteListDeleteAllItems();
		if (bRet) {
			tableModel.setRowCount(0);
			nListCount = 0;
			textFieldSum.setText(String.valueOf(nListCount));
			JOptionPane.showMessageDialog(WBListDemo.this, "Delete successfully", "info",JOptionPane.WARNING_MESSAGE);
		}else {
			JOptionPane.showMessageDialog(WBListDemo.this, "Delete failed", "info",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void findWBList() {
		if (null == sdk) {
			JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
			return;
		}
		WhiteListItem listItem = sdk.ICE_IPCSDK_WhiteListFindItem_ByNumber(textFieldPlate.getText());
		if (null == listItem) {
			JOptionPane.showMessageDialog(WBListDemo.this, "Find failed", "info",JOptionPane.WARNING_MESSAGE);
		}else {
			tableModel.setRowCount(0);
			Object[] strList = new Object[]{listItem.getStrPlate(), listItem.getStrDateBegin(), 
					listItem.getStrDateEnd(), listItem.getStrTimeBegin(), listItem.getStrTimeEnd(), 
					listItem.getStrType(), listItem.getStrRemark()};
			tableModel.addRow(strList);
		}
	}
	
	private void addWBList() {
		if (null == sdk) {
			JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (nListCount >= 20000) {
			JOptionPane.showMessageDialog(WBListDemo.this, "The number of black and white lists exceeds the maximum of 20000!", "info",JOptionPane.WARNING_MESSAGE);
			return;
		}
		ListInfo listInfo = new ListInfo();
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String dateNow = dateFormat.format(now);
		listInfo.setValue("add", "", dateNow, dateNow, "00:00:00", "23:59:59", "W", "");
		listInfo.SetCallback(addListCallback);
	}
	
	private class AddListCallback implements IListCtrlCallback{

		@Override
		public void ListInfo(String strPlate, String strBDate, String strEDate, String strBTime, String strETime,
				String strType, String strRemark) {
			// TODO Auto-generated method stub
			if (null == sdk) {
				JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
				return;
			}
			boolean bRet = sdk.ICE_IPCSDK_WhiteListInsertItem_ByNumber(strPlate, strBDate, strEDate, strBTime, strETime, strType, strRemark, "", "");
			if (bRet) {
				String[] strData = new String[] {strPlate, strBDate, strEDate, strBTime, strETime, strType, strRemark};
				tableModel.addRow(strData);
				nListCount++;
				textFieldSum.setText(String.valueOf(nListCount));
				JOptionPane.showMessageDialog(WBListDemo.this, "Add successfully", "info",JOptionPane.WARNING_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(WBListDemo.this, "Add failed", "info",JOptionPane.WARNING_MESSAGE);
			}
		}
		
	}
	
	private void editWBList() {
		if (null == sdk) {
			JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int row = tableWBList.getSelectedRow();
		if (-1 != row) {
			nSelPos = row;
			String strPlate = (String) tableModel.getValueAt(row, 0);
			String strBDate = (String)tableModel.getValueAt(row, 1);
			String strEDate = (String)tableModel.getValueAt(row, 2);
			String strBTime = (String)tableModel.getValueAt(row, 3);
			String strETime = (String)tableModel.getValueAt(row, 4);
			String strType = (String)tableModel.getValueAt(row, 5);
			String strRemark = (String)tableModel.getValueAt(row, 6);
			ListInfo listInfo = new ListInfo();
			listInfo.setValue("edit", strPlate, strBDate, strEDate, strBTime, strETime, strType, strRemark);
			
			listInfo.SetCallback(editListCallback);			
		}else {
			JOptionPane.showMessageDialog(WBListDemo.this, "Please choose a W/B list item", "info",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private class EditListCallback implements IListCtrlCallback{

		@Override
		public void ListInfo(String strPlate, String strBDate, String strEDate, String strBTime, String strETime,
				String strType, String strRemark) {
			// TODO Auto-generated method stub
			if (null == sdk) {
				JOptionPane.showMessageDialog(WBListDemo.this, "Not connected", "info",JOptionPane.WARNING_MESSAGE);
				return;
			}
			boolean bRet = sdk.ICE_IPCSDK_WhiteListEditItem_ByNumber(strPlate, strBDate, strEDate, strBTime, strETime, strType, strRemark, "", "");
			if (bRet) {
				if (nSelPos != -1) {
					tableModel.setValueAt(strBDate, nSelPos, 1);
					tableModel.setValueAt(strEDate, nSelPos, 2);
					tableModel.setValueAt(strBTime, nSelPos, 3);
					tableModel.setValueAt(strETime, nSelPos, 4);
					tableModel.setValueAt(strType, nSelPos, 5);
					tableModel.setValueAt(strRemark, nSelPos, 6);
				}
				JOptionPane.showMessageDialog(WBListDemo.this, "Edit successfully", "info",JOptionPane.WARNING_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(WBListDemo.this, "Edit failed", "info",JOptionPane.WARNING_MESSAGE);
			}
		}
		
	}
	
	private class WBListTable extends JTable{
	    public WBListTable(DefaultTableModel tableModel) {//Vector rowData, Vector columnNames
	        super(tableModel);                      
	    }

	    public JTableHeader getTableHeader() {                  
	        JTableHeader tableHeader = super.getTableHeader();  
	        tableHeader.setReorderingAllowed(false);                                        
	        return tableHeader;
	    }

	    public boolean isCellEditable(int row, int column) {               
	        return false;
	    }
	}
}
