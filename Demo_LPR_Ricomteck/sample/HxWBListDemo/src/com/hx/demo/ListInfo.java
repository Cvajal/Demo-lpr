package com.hx.demo;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.hx.demo.callback.IListCtrlCallback;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;

public class ListInfo extends JFrame {
	private JTextField textFieldPlate;
	private JTextField textFieldBeginDate;
	private JTextField textFieldEndDate;
	private JTextField textFieldBeginTime;
	private JTextField textFieldEndTime;
	private JCheckBox checkBoxBlack;
	private String strCtrl;
	private IListCtrlCallback callback = null;
	private JTextField textFieldNote;
	/**
	 * Create the frame.
	 */
	public ListInfo() {
		setResizable(false);
		setTitle("B/W List Info");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 265, 385);
		getContentPane().setLayout(null);
		
		JLabel lblLp = new JLabel("LP:");
		lblLp.setBounds(26, 13, 54, 15);
		getContentPane().add(lblLp);
		
		textFieldPlate = new JTextField();
		textFieldPlate.setBounds(110, 10, 120, 21);
		getContentPane().add(textFieldPlate);
		textFieldPlate.setColumns(10);
		
		JLabel lblStartDate = new JLabel("Start Date:");
		lblStartDate.setBounds(26, 47, 67, 15);
		getContentPane().add(lblStartDate);
		
		JLabel lblEndDate = new JLabel("End Date:");
		lblEndDate.setBounds(26, 86, 67, 15);
		getContentPane().add(lblEndDate);
		
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setBounds(26, 125, 67, 15);
		getContentPane().add(lblStartTime);
		
		JLabel lblEndTime = new JLabel("End Time:");
		lblEndTime.setBounds(26, 167, 67, 15);
		getContentPane().add(lblEndTime);
		
		checkBoxBlack = new JCheckBox("Black");
		checkBoxBlack.setBounds(94, 240, 103, 23);
		getContentPane().add(checkBoxBlack);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textFieldPlate.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(ListInfo.this, "Plate can not be empty.", "info",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if (!isDate(textFieldBeginDate.getText()) || !isDate(textFieldEndDate.getText())) {
					JOptionPane.showMessageDialog(ListInfo.this, "Incorrect date format.", "info",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if (!isTime(textFieldBeginTime.getText()) || !isTime(textFieldEndTime.getText())) {
					JOptionPane.showMessageDialog(ListInfo.this, "Incorrect time format.", "info",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if (!compare_date(textFieldBeginDate.getText(), textFieldEndDate.getText())){
					JOptionPane.showMessageDialog(ListInfo.this, "The end date is earlier than the start date.", "info",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if(!compare_time(textFieldBeginTime.getText(), textFieldEndTime.getText())) {
					JOptionPane.showMessageDialog(ListInfo.this, "The end time is earlier than the start time.", "info",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				callback.ListInfo(textFieldPlate.getText(), textFieldBeginDate.getText(), 
						textFieldEndDate.getText(), textFieldBeginTime.getText(), 
						textFieldEndTime.getText(), (checkBoxBlack.isSelected() ? "B" : "W"),
						textFieldNote.getText());
				setVisible(false);
				dispose();
			}
		});
		btnOK.setBounds(26, 288, 93, 23);
		getContentPane().add(btnOK);
		
		JButton btnCancle = new JButton("Cancle");
		btnCancle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		btnCancle.setBounds(143, 288, 93, 23);
		getContentPane().add(btnCancle);
		
		textFieldBeginDate = new JTextField();
		textFieldBeginDate.setBounds(110, 44, 120, 21);
		getContentPane().add(textFieldBeginDate);
		textFieldBeginDate.setColumns(10);
		
		textFieldEndDate = new JTextField();
		textFieldEndDate.setBounds(110, 83, 120, 21);
		getContentPane().add(textFieldEndDate);
		textFieldEndDate.setColumns(10);
		
		textFieldBeginTime = new JTextField();
		textFieldBeginTime.setBounds(110, 122, 120, 21);
		getContentPane().add(textFieldBeginTime);
		textFieldBeginTime.setColumns(10);
		
		textFieldEndTime = new JTextField();
		textFieldEndTime.setBounds(110, 164, 120, 21);
		getContentPane().add(textFieldEndTime);
		textFieldEndTime.setColumns(10);
		
		JLabel lblNote = new JLabel("Note:");
		lblNote.setBounds(26, 205, 67, 15);
		getContentPane().add(lblNote);
		
		textFieldNote = new JTextField();
		textFieldNote.setColumns(10);
		textFieldNote.setBounds(110, 202, 120, 21);
		getContentPane().add(textFieldNote);
		
		this.setVisible(true);
	}
	
	public void setValue(String strCtrl, String strPlate, String strBDate, String strEDate, String strBTime, String strETime, String strType, String strRemark) {
		if (strCtrl.equals("edit")) {
			textFieldPlate.setEditable(false);
		}else {
			textFieldPlate.setEditable(true);
		}
		textFieldPlate.setText(strPlate);
		textFieldBeginDate.setText(strBDate);
		textFieldEndDate.setText(strEDate);
		textFieldBeginTime.setText(strBTime);
		textFieldEndTime.setText(strETime);
		textFieldNote.setText(strRemark);
		if (strType.equals("B")) {
			checkBoxBlack.setSelected(true);
		}
		this.strCtrl = strCtrl;
	}
	
	public void SetCallback(IListCtrlCallback callback) {
		this.callback = callback;
	}
	

	private boolean isDate(String str) {
		String[] date = new String[3];
		String str_year = null;
		String str_month = null;
		String str_day = null;

		int year = 0;
		int month = 0;
		int day = 0;

		if ((str.split("/").length) != 3)
			return false;

		date = str.split("/");

		for (int i = 0; i < date.length; i++) {
			if (!isNumeric(date[i]))
				return false;
		}

		str_year = date[0];
		str_month = date[1];
		str_day = date[2];

		try {
			year = Integer.parseInt(str_year);
			month = Integer.parseInt(str_month);
			day = Integer.parseInt(str_day);
		} catch (NumberFormatException e) {
			return false;
		}

		if (month > 12 || day > 31)
			return false;

		if (month == 4 || month == 6 || month == 9 || month == 11) {
			if (day > 30)
				return false;
		}

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			if (month == 2) {
				if (day > 29)
					return false;
			}
		} else {
			if (month == 2) {
				if (day > 28)
					return false;
			}

		}
		return true;
	}


	private boolean isTime(String str) {
		String time[] = new String[3];
		String str_hour = null;
		String str_min = null;
		String str_sec = null;

		int hour = 0;
		int min = 0;
		int sec = 0;

		if ((str.split(":").length) != 3)
			return false;
		time = str.split(":");

		for (int i = 0; i < time.length; i++) {
			if (!isNumeric(time[i]))
				return false;
		}

		str_hour = time[0];
		str_min = time[1];
		str_sec = time[2];

		try {
			hour = Integer.parseInt(str_hour);
			min = Integer.parseInt(str_min);
			sec = Integer.parseInt(str_sec);
		} catch (NumberFormatException e) {
			return false;
		}

		if (hour > 23 || min > 59 || sec > 59)
			return false;

		return true;
	}


	private boolean compare_date(String strDateBegin, String strDateEnd) {
		String[] date = new String[3];
		date = strDateBegin.split("/|-");
		int dateBegin = Integer.parseInt(date[0]) * 10000
				+ Integer.parseInt(date[1]) * 100 + Integer.parseInt(date[2]);
		date = strDateEnd.split("/|-");
		int dateEnd = Integer.parseInt(date[0]) * 10000
				+ Integer.parseInt(date[1]) * 100 + Integer.parseInt(date[2]);
		if (dateBegin > dateEnd)
			return false;

		return true;
	}


	private boolean compare_time(String strTimeBegin, String strTimeEnd) {
		String[] time = new String[3];
		time = strTimeBegin.split(":");
		int timeBegin = Integer.parseInt(time[0]) * 3600
				+ Integer.parseInt(time[1]) * 60 + Integer.parseInt(time[2]);
		time = strTimeEnd.split(":");
		int timeEnd = Integer.parseInt(time[0]) * 3600
				+ Integer.parseInt(time[1]) * 60 + Integer.parseInt(time[2]);
		if (timeBegin >= timeEnd)
			return false;

		return true;
	}
	

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
}
