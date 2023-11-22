package ice_ipc_sdk_test;

import ice_ipcsdk.SDK;
import ice_ipcsdk.SDK.RelayOutputResult;
import ice_ipcsdk.common.ComFunc;
import ice_ipcsdk.struct.DNSParam;
import ice_ipcsdk.struct.NetworkParam;
import ice_ipcsdk.struct.WhiteListItem;
import ice_ipcsdk.struct.WhiteListParam;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	private static SDK sdk;
	public static boolean isContinue = true;
	private static String ip;

	public static void main(String[] args) {    
		String strJar = "ice_ipcsdk.jar";
		try {
			System.out.println("sdk Version:" + getVersion(strJar));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (args.length == 0) {
			System.out.println("Please enter IP. Note: if the IP format is wrong, the program will end automatically!!!");
			System.exit(0);
		} else {
			String strIp = args[0];
			String[] strDns = strIp.trim().split("\\.");
			if (strDns.length != 4) {
				System.out.println("IP address format error, the program ends, thank you!");
				System.exit(0);
			} else {
				for (int i = 0; i < strDns.length; i++) {
					if (!isNumeric(strDns[i]) || "".equals(strDns[i])) {
						System.out.println("IP address format error, the program ends, thank you!");
						System.exit(0);
					} else {
						int intDns = Integer.parseInt(strDns[i]);
						if (intDns < 0 || intDns > 255) {
							System.out.println("IP address format error, the program ends, thank you!");
							System.exit(0);
						}
					}
				}
			}
			ip = strIp;
		}

		sdk = new SDK();
		int iRet = sdk.ICE_IPCSDK_OpenDevice(ip);
		if (1 != iRet) {
			System.out.println("Connect failed!");
			System.exit(0);
		}

		do {
			isContinue = false;
			System.out.println("Please enter test item:");
			System.out.println("0:Log settings");
			System.out.println("1:Set DNS parameters");
			System.out.println("2:Get DNS parameters");
			System.out.println("3:Get ip,mask,geteWay");
			System.out.println("4:Set ip,mask,geteWay");
			System.out.println("5:Set IO output parameters");
			System.out.println("6:Get IO output parameters");
			System.out.println("7:Sync time");
			System.out.println("8:Get camera's time");
			System.out.println("9:Get RS485 parameters");
			System.out.println("10:Set RS485 parameters");		
			System.out.println("11:Get B/W list's parameters");
			System.out.println("12:Set B/W list's parameters");	
			System.out.println("13:Get camera's UID");
			System.out.println("14:Get IO status");
			System.out.println("15:Control alarmout");
			System.out.println("16:Get camera's mac");
			System.out.println("17:Send RS485 data");
			System.out.println("18:Send RS485-2 data");
			System.out.println("19:Capture");
			System.out.println("100:exit");

			int selection = 0;
			try {
				selection = new Scanner(System.in).nextInt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			 if (0 == selection) {
				iceLogConfig();
			}else if (1 == selection) {
				iceSetDNS();
			} else if (2 == selection) {
				iceGetDNS();
			} else if (3 == selection) {
				iceGetNetworkParam();
			} else if (4 == selection) {
				iceSetNetworkParam();
			}else if (5 == selection) {
				iceSetRelayOutputSettings();
			} else if (6 == selection) {
				iceGetRelayOutputSettings();
			} else if (7 == selection) {
				iceSyncTime();
			}else if (8 == selection) {
				iceGetTime();
			}else if (9 == selection) {
				iceGetUARTCfg();
			} else if (10 == selection) {
				iceSetUARTCfg();
			} else if (11 == selection){
				iceGetWhiteListParam();
			}else if (12 == selection){
				iceSetWhiteListParam();
			}else if (13 == selection) {
				iceGetUID();
			}else if (14 == selection) {
				iceGetIOState();
			}else if (15 == selection) {
				iceControlAlaramout();
			}else if (16 == selection) {
				iceGetMac();
			}else if (17 == selection) {
				iceSendRS485();
			}else if (18 == selection) {
				iceSendRS485_2();
			}else if (19 == selection) {
				iceCapture();
			}else if (100 == selection) {
				System.out.println("The program is over, thank you!");
				System.exit(0);
			} else {
				System.out.println("Please enter the correct number!");
				isContinue = true;
			}

		} while (isContinue);

	}

	private static void iceCapture() {
		isContinue = true;
		SDK.CaptureResult result = sdk.ICE_IPCSDK_Capture();
		if (null == result) {
			System.out.println("Capture failed!");
			return;
		}
		String filename = "./capture.jpg";
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
			out.write(result.picdata);
			out.close();
			System.out.println("The picture is saved on './capture.jpg'");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private static void iceGetUID() {
		String strUID = sdk.ICE_IPCSDK_GetUID();
		if (null == strUID) {
			System.out.println("Get failed!");
		}else {
			System.out.println("UID:" + strUID);
		}
		isContinue = true;
	}
	
	private static void iceSendRS485() {
		System.out.println("Please input serial port data (hexadecimal form, such as AA11DD)");
		String strData = new Scanner(System.in).nextLine();
		int iRet = sdk.ICE_IPCSDK_TransSerialPort(ComFunc.hexStringToBytes(strData));
		if (1 == iRet) {
			System.out.println("Send successfully!");
		}
		else {
			System.out.println("Send failed!");
		}
		isContinue = true;
	}
	
	private static void iceSendRS485_2() {
		System.out.println("Please input serial port data (hexadecimal form, such as AA11DD)");
		String strData = new Scanner(System.in).nextLine();
		int iRet = sdk.ICE_IPCSDK_TransSerial232Port(ComFunc.hexStringToBytes(strData));
		if (1 == iRet) {
			System.out.println("Send successfully!");
		}
		else {
			System.out.println("Send failed!");
		}
		isContinue = true;
	}
	
	private static void iceGetMac() {
		String strMac = sdk.ICE_IPCSDK_GetDevID();
		if (null == strMac) {
			System.out.println("Get failed!");
		}else {
			System.out.println("MAC:" + strMac);
		}
		isContinue = true;
	}
	
	private static void iceControlAlaramout() {
		boolean isIndex = true;
		int nIndex = 0;
		while (isIndex) {
			isIndex = false;
			int nMaxIO = 3;

			System.out.println("Please input output index,0:output1,1:output2, 2:output3,3:output4 (enter -1 to return the test item selection):");
			String index = null;
			try {
				index = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isNumeric(index)) {
				int Index = Integer.parseInt(index);
				if ((Index>=0) && (Index <= nMaxIO))  {
					nIndex = Index;
				} else {
					System.out.println("Please enter the correct parameters");
					isIndex = true;
					continue;
				}
			} else if ("-1".equals(index)) {
				isContinue = true;
				continue;
			} else {
				System.out.println("Please enter the correct parameters");
				isIndex = true;
				continue;
			}
		}
		
		int iRet = sdk.ICE_IPCSDK_ControlAlarmOut(nIndex);
		if (1 == iRet) {
			System.out.println("Successfully!");
		}else {
			System.out.println("failed!");
		}
		isContinue = true;
	}
	
	private static void iceSyncTime() {
		boolean isReset = true;
		while (isReset) {
			isReset = false;
			String strTime = GetUTCTimeUtil.getUTCTimeStr();
			//System.out.println(strTime);

			String[] strDate = strTime.split(" ");
			String date = strDate[0];
			String time = strDate[1];
			String[] splitDate = date.split("-");
			String[] splitTime = time.split(":");
			int sYear = Integer.parseInt(splitDate[0]);
			int bMonth = Integer.parseInt(splitDate[1]);
			int bDay = Integer.parseInt(splitDate[2]);
			int bHour = Integer.parseInt(splitTime[0]);
			int bMin = Integer.parseInt(splitTime[1]);
			int bSec = Integer.parseInt(splitTime[2]);

			// System.out.println("sYear:"+sYear+" bMonth:"+bMonth+" bDay:"+
			// bDay+" bHour:"+ bHour+" bMin:"+ bMin+" bSec:"+bSec);

			int nSuccess = sdk.ICE_IPCSDK_SyncTime(sYear, bMonth, bDay,
					bHour, bMin, bSec);
			if (0 == nSuccess) {
				System.out.println("Sync time failed");
			} else {
				System.out.println("Sync time successfully");
				isContinue = true;
				continue;
			}
		}
	}
	
	private static void iceGetRelayOutputSettings() {
		boolean isIndex = true;
		int nIndex = 0;
		while (isIndex) {
			isIndex = false;
			int nMaxIO = 3;

			System.out.println("Please input output index,0:output1,1:output2, 2:output3,3:output4 (enter -1 to return the test item selection):");
			String index = null;
			try {
				index = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isNumeric(index)) {
				int Index = Integer.parseInt(index);
				if ((Index>=0) && (Index <= nMaxIO))  {
					nIndex = Index;
				} else {
					System.out.println("Please enter the correct parameters");
					isIndex = true;
					continue;
				}
			} else if ("-1".equals(index)) {
				isContinue = true;
				continue;
			} else {
				System.out.println("Please enter the correct parameters");
				isIndex = true;
				continue;
			}
		}

		RelayOutputResult mRelayOutputResult = sdk.ICE_IPCSDK_GetRelayOutput(nIndex);
		if (mRelayOutputResult != null) {
			System.out.println("Status:" + (mRelayOutputResult.bIdleState == 0 ? "NO" : "NC"));
			System.out.println("Output takes:" + mRelayOutputResult.nDelayTime);
		} else {
			System.out.println("Get failed");
		}

		isContinue = true;
	}
	
	private static void iceSetRelayOutputSettings() {
		boolean isReset = true;
		boolean isDelay = false;
		boolean isIndex = false;
		while (isReset) {
			isReset = false;
			boolean isStateTrue = true;
			byte bIdleState = 0;
			while (isStateTrue) {
				isStateTrue = false;
				System.out.println("Please enter the status, 0:normally open, 1:normally closed (enter -1 to select the return test item):");
				String state = null;
				try {
					state = new Scanner(System.in).next();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if ("0".equals(state) || "1".equals(state)) {
					bIdleState = Byte.parseByte(state);
					isDelay = true;
				} else if ("-1".equals(state)) {
					isContinue = true;
					isDelay = false;
					isIndex = false;
					continue;
				} else {
					System.out.println("Please enter the correct parameters!");
					isStateTrue = true;
				}
			}

			int nDelayTime = 0;
			while (isDelay) {
				isDelay = false;
				System.out.println("Please enter the output takes (range: -1,1-10) (enter -2 to select the return test item):");
				String delay = null;
				try {
					delay = new Scanner(System.in).next();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (isNumeric(delay)) {
					int delayTime = Integer.parseInt(delay);
					if ((delayTime > 0) && (delayTime <= 10)) {
						nDelayTime = delayTime;
						isIndex = true;
					} else {
						System.out.println("Please enter the correct parameters!");
						isDelay = true;
					}
				} else if ("-2".equals(delay)) {
					isContinue = true;
					isIndex = false;
					continue;
				}else if ("-1".equals(delay)){
					nDelayTime = -1;
					isIndex = true;
				}else {
					System.out.println("Please enter the correct parameters!");
					isDelay = true;
				}
			}

			int nIndex = 0;
			while (isIndex) {
				int nMaxIO = 3;
				isIndex = false;
				System.out.println("Please input output index,0:output1,1:output2, 2:output3,3:output4 (enter -1 to return the test item selection):");
				nMaxIO = 3;
					
				String index = null;
				try {
					index = new Scanner(System.in).next();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (isNumeric(index)) {
					int Index = Integer.parseInt(index);
					if ((Index>=0) && (Index <= nMaxIO)) {
						nIndex = Index;
					} else {
						System.out.println("Please enter the correct number!");
						isIndex = true;
						continue;
					}
				} else if ("-1".equals(index)) {
					isContinue = true;
					continue;
				} else {
					System.out.println("Please enter the correct parameters!");
					isIndex = true;
					continue;
				}
			}
				

			int success = sdk.ICE_IPCSDK_SetRelayOutputSettings(
					bIdleState, nDelayTime, nIndex);
			if (0 == success) {
				System.out.println("Set failed!");
			} else {
				System.out.println("Set successfully!");
				isContinue = true;
			}
		}
	}
	
	private static void iceSetNetworkParam() {
		boolean isReset = true;
		boolean isReset1 = true;
		boolean isReset2 = false;
		boolean isSet = false;
		String strIP = null;
		outer0: while (isReset) {
			isReset = false;

			System.out.println("Please enter camera's ip (enter - 1 to return the test item selection):");
			String ip = null;
			try {
				ip = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(ip)) {
				isContinue = true;
				isReset1 = false;
				continue;
			} else {
				String[] strTmp = ip.trim().split("\\.");
				if (strTmp.length != 4) {
					System.out.println("Please enter ip in the correct format!!!");
					isReset = true;
					isReset1 = false;
					continue;
				} else {
					for (int i = 0; i < strTmp.length; i++) {
						if (!isNumeric(strTmp[i])
								|| "".equals(strTmp[i])) {
							System.out.println("Please enter ip in the correct format!!!");
							isReset = true;
							isReset1 = false;
							continue outer0;
						} else {
							int intDns = Integer.parseInt(strTmp[i]);
							if (intDns < 0 || intDns > 255) {
								System.out.println("Please enter ip in the correct format!!!");
								isReset = true;
								isReset1 = false;
								continue outer0;
							}
						}

					}
				}
				strIP = ip;
				isReset1 = true;
			}
		}

		String strMask = null;

		outer: while (isReset1) {
			isReset1 = false;
			System.out.println("Please enter camera's mask (enter - 1 to select for returning test items):");
			String mask = null;
			try {
				mask = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(mask)) {
				isContinue = true;
				continue;
			} else {
				String[] strTmp = mask.split("\\.");
				if (strTmp.length != 4) {
					System.out.println("Please enter mask in the correct format!!!");
					isReset1 = true;
					continue;
				} else {
					for (int i = 0; i < strTmp.length; i++) {
						if (!isNumeric(strTmp[i])
								|| "".equals(strTmp[i])) {
							System.out.println("Please enter mask in the correct format!!!");
							isReset1 = true;
							continue outer;
						} else {
							int intDns = Integer.parseInt(strTmp[i]);
							if (intDns < 0 || intDns > 255) {
								System.out.println("Please enter mask in the correct format!!!");
								isReset1 = true;
								continue outer;
							}
						}
					}
				}
				strMask = mask;
				isReset2 = true;
			}
		}
		
		String strGateway = null;

		outer: while (isReset2) {
			isReset2 = false;
			System.out.println("Please enter camera's gateway (enter - 1 to select for returning test items):");
			String gateway = null;
			try {
				gateway = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(gateway)) {
				isContinue = true;
				continue;
			} else {
				String[] strTmp = gateway.split("\\.");
				if (strTmp.length != 4) {
					System.out.println("Please enter gateway in the correct format!!!");
					isReset2 = true;
					continue;
				} else {
					for (int i = 0; i < strTmp.length; i++) {
						if (!isNumeric(strTmp[i])
								|| "".equals(strTmp[i])) {
							System.out.println("Please enter gateway in the correct format!!!");
							isReset2 = true;
							continue outer;
						} else {
							int intDns = Integer.parseInt(strTmp[i]);
							if (intDns < 0 || intDns > 255) {
								System.out.println("Please enter gateway in the correct format!!!");
								isReset2 = true;
								continue outer;
							}
						}
					}
				}
				strGateway = gateway;
				isSet = true;
			}
		}
		
		if (isSet) {
			int iRet = sdk.ICE_IPCSDK_SetIPAddr(strIP, strMask, strGateway);
			if (1 == iRet) {
				System.out.println("Set successfully!");
			}else {
				System.out.println("Set failed!");
			}
			isContinue = true;
		}
	}
	
	private static void iceGetNetworkParam() {
		NetworkParam mNetworkParam = sdk.ICE_IPCSDK_GetIPAddr();
		if (mNetworkParam != null) {
			System.out.println("ip:" + mNetworkParam.getIp());
			System.out.println("mask:" + mNetworkParam.getMask());
			System.out.println("gateway:" + mNetworkParam.getGateWay());
		} else {
			System.out.println("Get failed");
		}
		isContinue = true;
	}
	
	private static void iceGetDNS() {
		DNSParam mDNSParam = sdk.ICE_IPCSDK_GetDNSAddr();
		if (mDNSParam != null) {
			System.out.println("Preferred DNS:" + mDNSParam.getStrDNS());
			System.out.println("Alternate DNS:" + mDNSParam.getStrDNSReserve());
		} else {
			System.out.println("Get failed");
		}
		isContinue = true;
	}
	
	private static void iceSetDNS() {
		boolean isReset = true;
		boolean isReset1 = true;
		String strDNS = null;
		outer0: while (isReset) {
			isReset = false;

			System.out.println("Please enter your preferred DNS (enter - 1 to return the test item selection):");
			String dns = null;
			try {
				dns = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(dns)) {
				isContinue = true;
				isReset1 = false;
				continue;
			} else {
				String[] strDns = dns.trim().split("\\.");
				if (strDns.length != 4) {
					System.out.println("Please enter DNS in the correct format!!!");
					isReset = true;
					isReset1 = false;
					continue;
				} else {
					for (int i = 0; i < strDns.length; i++) {
						if (!isNumeric(strDns[i])
								|| "".equals(strDns[i])) {
							System.out.println("Please enter DNS in the correct format!!!");
							isReset = true;
							isReset1 = false;
							continue outer0;
						} else {
							int intDns = Integer.parseInt(strDns[i]);
							if (intDns < 0 || intDns > 255) {
								System.out.println("Please enter DNS in the correct format!!!");
								isReset = true;
								isReset1 = false;
								continue outer0;
							}
						}

					}
				}
				strDNS = dns;
				isReset1 = true;
			}
		}

		String strDNSReserve = null;

		outer: while (isReset1) {
			isReset1 = false;
			System.out.println("Please enter alternate DNS (enter - 1 to select for returning test items):");
			String dnsReserve = null;
			try {
				dnsReserve = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(dnsReserve)) {
				isContinue = true;
				continue;
			} else {
				String[] strDns = dnsReserve.split("\\.");
				if (strDns.length != 4) {
					System.out.println("Please enter DNS in the correct format!!!");
					isReset1 = true;
					continue;
				} else {
					for (int i = 0; i < strDns.length; i++) {
						if (!isNumeric(strDns[i])
								|| "".equals(strDns[i])) {
							System.out.println("Please enter DNS in the correct format!!!");
							isReset1 = true;
							continue outer;
						} else {
							int intDns = Integer.parseInt(strDns[i]);
							if (intDns < 0 || intDns > 255) {
								System.out.println("Please enter DNS in the correct format!!!");
								isReset1 = true;
								continue outer;
							}
						}
					}
				}
				strDNSReserve = dnsReserve;
			}
			
			int result = sdk.ICE_IPCSDK_SetDNSAddr(strDNS,
					strDNSReserve);
			if (0 == result) {
				System.out.println("Set failed");
				isReset = true;
			} else {
				System.out.println("Set successfully");
				isContinue = true;
			}
		}
	}
	
	private static int strlen(byte[] data, int offset, int len) {
		for (int i = 0; i < len; i++)
			if (data[offset + i] == '\0')
				return i;

		return len;
	}
	
	static boolean rangeCheck(int data, int lower, int upper){
		if (data >= lower && data <= upper)
			return true;
		return false;
	}
	
	public static void iceGetTime() {
		SDK.TimeCfgInfo param = sdk.ICE_IPCSDK_GetTime();
		if (null == param) {
			System.out.println("Get failed");
		}else {			
			System.out.println("camera time:" + param.iYear + "/" + param.iMon + "/" + param.iDay
					+ " " + param.iHour + ":" + param.iMin + ":" + param.iSec);
		}
		isContinue = true;
	}
	
	//Get list param
	private static void iceGetWhiteListParam() {
		WhiteListParam  param= new WhiteListParam();
		param = sdk.ICE_IPCSDK_GetWhiteListParam();
		if (null == param)
			System.out.println("Get failed!");
		else {
			System.out.print("White List:");
			if (param.getnWhiteListMode() == 1)
				System.out.println("Control gate alltime");
			else if (param.getnWhiteListMode() == 2)
				System.out.println("Uncontrol gate");
			else System.out.println("Control gate offline");
			
			System.out.print("Black List:");
			wbPrint(param.getnBlackListMode());
			
			System.out.print("Temp List:");
			wbPrint(param.getnTempListMode());
			
			boolean bFirPrecise = false;//100% match
			boolean bSecNormal = false;//Fuzzy match
			int nAllowNum = 1;//Fuzzy Match Num
			int nThrIgore = 0;
			if (0 == param.getNew_version()){//old version
				switch(param.getnWhiteListMatch()){
				case 100:
					bFirPrecise = true;
					bSecNormal = false;
					nAllowNum = 1;
					break;
				case 90:
					bFirPrecise = false;
					bSecNormal = true;
					nAllowNum = 1;
					break;
				case 80:
					bFirPrecise = false;
					bSecNormal = true;
					nAllowNum = 2;
					break;
				case 70:
					bFirPrecise = false;
					bSecNormal = true;
					nAllowNum = 3;
					break;
				}
				nThrIgore = 0;
			}else{
				if ((param.getAllow_unmatch_chars_cnt() == 0) && (param.getIgnoreHZ_flag() == 0)){
					if (100 == param.getnWhiteListMatch()){
						bFirPrecise = true;
						bSecNormal = false;
					}else{
						bFirPrecise = true;
						bSecNormal = false;
					}
					nThrIgore = 0;
				}else{//Fuzzy match
					bFirPrecise = false;
					if (0 == param.getAllow_unmatch_chars_cnt())
						bSecNormal = false;
					else
						bSecNormal = true;
					nThrIgore = param.getIgnoreHZ_flag();
				}
				if (0 == param.getAllow_unmatch_chars_cnt())
					nAllowNum = 1;
				else
					nAllowNum = param.getAllow_unmatch_chars_cnt();
			}
			System.out.println("Match Mode:");
			System.out.println("    100% Match:" + (bFirPrecise? "Yes":"No"));
			System.out.println("    Fuzzy Match:" + (bSecNormal? "Yes":"No"));
			System.out.println("        Fuzzy Match Num:" + nAllowNum);
		}
		isContinue = true;
	}
	
	private static void wbPrint(int nMode){
		if (nMode == 1)
			System.out.println("Control gate alltime");
		else System.out.println("Uncontrol gate");
	}
	
	//Set whitelist param
	private static void iceSetWhiteListParam() {
		WhiteListParam param = new WhiteListParam();
		boolean isMode = true;
		boolean isSet = false;
		String strItem = null;
		String[] array_Item = new String[7];
		int[] modeArray = new int[7];
		while (isMode){
			isMode = false;
			System.out.println("White List:0:Control gate offline,1:Control gate alltime,2:Uncontrol gate");
			System.out.println("Black List:0:Uncontrol gate,1:Control gate alltime");
			System.out.println("Temp List:0:Uncontrol gate,1:Control gate alltime");
			System.out.println("Please enter parameters:White List,Black List,Temp List(e.g. 0,0,0)(enter -1 to return to the test option):");
			try {
				strItem = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(strItem)) {
				isContinue = true;
				break;
			} else {
				if (strItem.split(",").length != 3) {
					System.out.println("Input is not 3 parameters.Please enter the correct parameters");
					isMode = true;
					continue;
				}
				array_Item = strItem.trim().split(",");

				for(int i=0; i<3; i++){
					if (!isNumeric(array_Item[i].trim())){
						System.out.println("The input is not a number.Please enter the correct parameters");
						isMode = true;
						continue;
					}
					modeArray[i] = Integer.parseInt(array_Item[i].trim());
					if (i == 0){
						if ((modeArray[i] < 0) || (modeArray[i] > 2)){
							{
								System.out.println("The input is out of range.Please enter the correct parameters");
								isMode = true;
								continue;
							}
						}
					}else if ((modeArray[i] < 0) || (modeArray[i] > 1)){
						{
							System.out.println("The input is out of range.Please enter the correct parameters");
							isMode = true;
							continue;
						}
					}
				}
			}
		}
		
		boolean bFirPrecise = true;//100% Match
		int nFirPrecise = 0;
		while (!isContinue && bFirPrecise){
			bFirPrecise = false;
			System.out.print("100% Match:0:No,1:yes(enter -1 to return to the test option):");
			try {
				strItem = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(strItem)) {
				isContinue = true;
				break;
			} else {
				if (!isNumeric(strItem.trim())){
					System.out.println("The input is not a number.Please enter the correct parameters");
					bFirPrecise = true;
					continue;
				}
				nFirPrecise = Integer.parseInt(strItem.trim());
				if ((nFirPrecise != 0) && (nFirPrecise != 1)){
					System.out.println("The input is out of range.Please enter the correct parameters");
					bFirPrecise = true;
					continue;
				}
				if (1 == nFirPrecise){
					isSet = true;
				}
			}
		}
		int nSecNormal = 0;
		boolean bSecNormal = true;//Fuzzy Match
		int nAllowNum = 0;//Fuzzy Match Num
		if (0 == nFirPrecise){//Not 100% Match
			while(!isContinue && bSecNormal){
				bSecNormal = false;
				System.out.println("Fuzzy Match Num:1,2");
				System.out.println("Please input:Fuzzy Match Num(enter -1 to return to the test option)");
				try {
					strItem = new Scanner(System.in).next();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if ("-1".equals(strItem)) {
					isContinue = true;
					break;
				} else {
					if (!isNumeric(strItem.trim())){
						System.out.println("The input is not a number.Please enter the correct parameters");
						bSecNormal = true;
						continue;
					}
					nSecNormal = 1;
					nAllowNum = Integer.parseInt(strItem.trim());
					if (((nSecNormal != 0) && (nSecNormal != 1)) || 
							((nAllowNum < 1)||(nAllowNum > 3))){
						System.out.println("The input is out of range.Please enter the correct parameters");
						bSecNormal = true;
						continue;
					}
					isSet = true;
				}
			}
		}
		
		
		if (isSet){
			param.setnWhiteListMode(modeArray[0]);
			param.setnBlackListMode(modeArray[1]);
			param.setnTempListMode(modeArray[2]);
			if (nSecNormal == 0)
				nAllowNum = 0;
			param.setAllow_unmatch_chars_cnt(nAllowNum);
			param.setNew_version(1);
			
			int result = sdk.ICE_IPCSDK_SetWhiteListParam(param);
			if (result == 0) {
				System.out.println("Set failed!");
			} else if (result == 1) {
				System.out.println("Set successfully!");
			}
			isContinue = true;
		}
	}
	
	// get IO state
	private static void iceGetIOState() {
		SDK.ICE_IOState stIO = sdk.new ICE_IOState();
		stIO = sdk.ICE_IPCSDK_GetIOState();
		if (null == stIO)
			System.out.println("Get failed!");
		else {
			System.out.println("IO status:" + stIO.nIOState[0] + stIO.nIOState[1]
					+ stIO.nIOState[2] + stIO.nIOState[3]);
		}
		isContinue = true;
	}

	public static String gb2312ToUtf8(String str) {

		String urlEncode = "";

		try {

			urlEncode = URLEncoder.encode(str, "UTF-8");

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

		}

		return urlEncode;

	}	

	/**
	 * Log setting
	 */
	private static void iceLogConfig() {

		boolean isEnableLog = true;
		boolean isFilePath = false;
		boolean isSet = false;

		String strEnableLog = null;
		String strFilePath = null;

		int nEnableLog = 0;
		String filePath = null;

		while (isEnableLog) {
			isEnableLog = false;
			System.out.println("Whether to record the log. 0 means not to record, 1 means record (enter - 1 to select the return test item):");
			try {
				strEnableLog = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(strEnableLog)) {
				isContinue = true;
				continue;
			} else if ("0".equals(strEnableLog)) {
				nEnableLog = Integer.parseInt(strEnableLog);
				isSet = true;
			} else if ("1".equals(strEnableLog)) {
				nEnableLog = Integer.parseInt(strEnableLog);
				isFilePath = true;
			} else {
				System.out.println("Please enter the correct parameters");
				isEnableLog = true;
			}
		}

		while (isFilePath) {
			isFilePath = false;
			System.out.println("Please enter the log storage path (enter - 1 to select for returning test items):");
			try {
				strFilePath = new Scanner(System.in).next();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("-1".equals(strFilePath)) {
				isContinue = true;
				continue;
			} else {
				filePath = strFilePath;
				isSet = true;
			}
		}

		if (isSet) {
			sdk.ICE_IPCSDK_LogConfig(nEnableLog, filePath);
			System.out.println("Setup complete");
			isContinue = true;
		}

	}
	
	/**
	 * Set serial port parameters
	 */
	private static void iceSetUARTCfg() {
		boolean isReSet = false;
		boolean isSelect = true;
		boolean isUartEn = false;
		boolean isUartWorkMode = false;
		boolean isBaudRate = false;
		boolean isParity = false;
		boolean isStopBits = false;
		boolean isFlowControl = false;
		boolean isUartProcOneReSendCnt = false;
		boolean isStart = false;

		SDK.UartParam uParam = null;
		int count = 0;
		int select = 0;

		while (uParam == null && count <= 21) {
			if (count <= 20) {
				uParam = sdk.ICE_IPCSDK_GetUARTCfg();
			} else {
				System.out.println("Get failed");
				isContinue = true;
				isUartEn = false;
				continue;
			}
			count++;
		}

		outer2: do {
			isReSet = false;
			while (isSelect) {
				isSelect = false;
				String strSelect = null;
				System.out.println("Please enter the serial port number to be set (enter - 1 to select the return test item):");
				System.out.println("Prompt: 1 means to set RS485 serial port, 2 means to set rs485-2 serial port, and 0 means to set the two together");
				try {
					strSelect = new Scanner(System.in).next();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if ("-1".equals(strSelect)) {
					isContinue = true;
					continue outer2;
				} else if ("1".equals(strSelect)) {
					select = 1;
					isUartEn = true;
				} else if ("2".equals(strSelect)) {
					select = 2;
					isUartEn = true;
				} else if ("0".equals(strSelect)) {
					select = 0;
					isUartEn = true;
				} else {
					System.out.println("Please enter the correct parameters");
					isSelect = true;
				}
			}

			if (select == 0 || select == 1 || select == 2) {
				if (select == 0 || select == 1) {
					while (isUartEn) {
						isUartEn = false;
						String strUartEn = null;
						System.out.println("Whether to enable RS485 serial port. 0:disable, 1:enable (input -1 to select the return test item):");
						try {
							strUartEn = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strUartEn)) {
							isContinue = true;
							continue outer2;
						} else if ("0".equals(strUartEn)
								|| "1".equals(strUartEn)) {
							uParam.uartParam1.uartEn = Integer
									.parseInt(strUartEn);
							isUartWorkMode = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isUartEn = true;
						}
					}

					while (isUartWorkMode) {
						isUartWorkMode = false;
						String strUartWorkMode = null;
						System.out.println("Port mode, 1:screen display control, 2:Pass-through control (input -1 is the selection of return test items):");
						try {
							strUartWorkMode = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strUartWorkMode)) {
							isContinue = true;
							continue outer2;
						} else if ("1".equals(strUartWorkMode)
								|| "2".equals(strUartWorkMode)) {
							uParam.uartParam1.screen_mode = (byte) Integer.parseInt(strUartWorkMode);
							isBaudRate = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isUartWorkMode = true;
						}
					}

					while (isBaudRate) {
						isBaudRate = false;
						String strBaudRate = null;
						System.out.println("Baud Rate(input -1 is the selection of return test items):");
						System.out
								.println("Parameter value:0:1200,1:2400,2:4800,3:9600,4:19200,5:38400,6:115200");
						try {
							strBaudRate = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strBaudRate)) {
							isContinue = true;
							continue outer2;
						} else if ("0".equals(strBaudRate)
								|| "1".equals(strBaudRate)
								|| "2".equals(strBaudRate)
								|| "3".equals(strBaudRate)
								|| "4".equals(strBaudRate)
								|| "5".equals(strBaudRate)
								|| "6".equals(strBaudRate)) {
							uParam.uartParam1.baudRate = Integer
									.parseInt(strBaudRate);
							isParity = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isBaudRate = true;
						}
					}

					while (isParity) {
						isParity = false;
						String strParity = null;
						System.out.println("Check Bit(input -1 is the selection of return test items):");
						System.out
								.println("Parameter value:0:none,1:odd,2:even");
						try {
							strParity = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strParity)) {
							isContinue = true;
							continue outer2;
						} else if ("0".equals(strParity)
								|| "1".equals(strParity)
								|| "2".equals(strParity)) {
							uParam.uartParam1.parity = Integer
									.parseInt(strParity);
							isStopBits = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isParity = true;
						}
					}

					while (isStopBits) {
						isStopBits = false;
						String strStopBit = null;
						System.out
								.println("Stop Bit:0:1,1:2(input -1 is the selection of return test items):");
						try {
							strStopBit = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strStopBit)) {
							isContinue = true;
							continue outer2;
						} else if ("0".equals(strStopBit)
								|| "1".equals(strStopBit)) {
							uParam.uartParam1.stopBits = Integer
									.parseInt(strStopBit);
							isFlowControl = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isStopBits = true;
						}
					}

					while (isFlowControl) {
						isFlowControl = false;
						String strFlowControl = null;
						System.out.println("Flow Control Mode(input -1 is the selection of return test items):");
						System.out.println("Parameter value:0:none,1:hardware,2:Xon,3:Xoff");
						try {
							strFlowControl = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strFlowControl)) {
							isContinue = true;
							continue outer2;
						} else if ("0".equals(strFlowControl)
								|| "1".equals(strFlowControl)
								|| "2".equals(strFlowControl)
								|| ("3".equals(strFlowControl))) {
							uParam.uartParam1.flowControl = Integer
									.parseInt(strFlowControl);
							// isStart = true;
							isUartProcOneReSendCnt = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isFlowControl = true;
						}
					}

					while (isUartProcOneReSendCnt) {
						isUartProcOneReSendCnt = false;
						String strUartProcOneReSendCnt = null;
						System.out.println("Retransmissions:0-3(input -1 is the selection of return test items):");
						try {
							strUartProcOneReSendCnt = new Scanner(System.in)
									.next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strUartProcOneReSendCnt)) {
							isContinue = true;
							continue outer2;
						} else if ("0".equals(strUartProcOneReSendCnt)
								|| "1".equals(strUartProcOneReSendCnt)
								|| "2".equals(strUartProcOneReSendCnt)
								|| "3".equals(strUartProcOneReSendCnt)) {
							uParam.uartParam1.u32UartProcOneReSendCnt = Integer
									.parseInt(strUartProcOneReSendCnt);
							isStart = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isUartProcOneReSendCnt = true;
						}
					}
				}

				if (select == 0 || select == 2) {
					isUartEn = true;
					isStart = false;
					while (isUartEn) {
						isUartEn = false;
						String strUartEn = null;
						System.out
								.println("Whether to enable RS485-2 serial port. 0:disable, 1:enable(input -1 is the selection of return test items):");
						try {
							strUartEn = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strUartEn)) {
							isContinue = true;
							continue;
						} else if ("0".equals(strUartEn)
								|| "1".equals(strUartEn)) {
							uParam.uartParam2.uartEn = Integer
									.parseInt(strUartEn);
							isUartWorkMode = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isUartEn = true;
						}
					}

					while (isUartWorkMode) {
						isUartWorkMode = false;
						String strUartWorkMode = null;
						System.out.println("Port mode, 1:screen display control, 2:Pass-through control(input -1 is the selection of return test items):");
						try {
							strUartWorkMode = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strUartWorkMode)) {
							isContinue = true;
							continue outer2;
						} else if ("1".equals(strUartWorkMode)
								|| "2".equals(strUartWorkMode)) {
							uParam.uartParam2.screen_mode = (byte) Integer.parseInt(strUartWorkMode);
							isBaudRate = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isUartWorkMode = true;
						}
					}

					while (isBaudRate) {
						isBaudRate = false;
						String strBaudRate = null;
						System.out.println("Baud Rate(input -1 is the selection of return test items):");
						System.out
								.println("Parameter value:0:1200,1:2400,2:4800,3:9600,4:19200,5:38400,6:115200");
						try {
							strBaudRate = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strBaudRate)) {
							isContinue = true;
							continue;
						} else if ("0".equals(strBaudRate)
								|| "1".equals(strBaudRate)
								|| "2".equals(strBaudRate)
								|| "3".equals(strBaudRate)
								|| "4".equals(strBaudRate)
								|| "5".equals(strBaudRate)
								|| "6".equals(strBaudRate)) {
							uParam.uartParam2.baudRate = Integer
									.parseInt(strBaudRate);
							isParity = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isBaudRate = true;
						}
					}

					while (isParity) {
						isParity = false;
						String strParity = null;
						System.out.println("Check Bits(input -1 is the selection of return test items):");
						System.out
								.println("Parameter value:0:none,1:odd,2:even");
						try {
							strParity = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strParity)) {
							isContinue = true;
							continue;
						} else if ("0".equals(strParity)
								|| "1".equals(strParity)
								|| "2".equals(strParity)) {
							uParam.uartParam2.parity = Integer
									.parseInt(strParity);
							isStopBits = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isParity = true;
						}
					}

					while (isStopBits) {
						isStopBits = false;
						String strStopBit = null;
						System.out
								.println("Stop Bit:0:1,1:2(input -1 is the selection of return test items):");
						try {
							strStopBit = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strStopBit)) {
							isContinue = true;
							continue;
						} else if ("0".equals(strStopBit)
								|| "1".equals(strStopBit)) {
							uParam.uartParam2.stopBits = Integer
									.parseInt(strStopBit);
							isFlowControl = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isStopBits = true;
						}
					}

					while (isFlowControl) {
						isFlowControl = false;
						String strFlowControl = null;
						System.out.println("Flow Control Mode(input -1 is the selection of return test items):");
						System.out.println("Parameter value:0:none,1:hardware,2:Xon,3:Xoff");
						try {
							strFlowControl = new Scanner(System.in).next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strFlowControl)) {
							isContinue = true;
							continue;
						} else if ("0".equals(strFlowControl)
								|| "1".equals(strFlowControl)
								|| "2".equals(strFlowControl)
								|| ("3".equals(strFlowControl))) {
							uParam.uartParam2.flowControl = Integer
									.parseInt(strFlowControl);
							isUartProcOneReSendCnt = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isFlowControl = true;
						}
					}

					while (isUartProcOneReSendCnt) {
						isUartProcOneReSendCnt = false;
						String strUartProcOneReSendCnt = null;
						System.out.println("Retransmissions:0-3(input -1 is the selection of return test items):");
						try {
							strUartProcOneReSendCnt = new Scanner(System.in)
									.next();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("-1".equals(strUartProcOneReSendCnt)) {
							isContinue = true;
							continue outer2;
						} else if ("0".equals(strUartProcOneReSendCnt)
								|| "1".equals(strUartProcOneReSendCnt)
								|| "2".equals(strUartProcOneReSendCnt)
								|| "3".equals(strUartProcOneReSendCnt)) {
							uParam.uartParam2.u32UartProcOneReSendCnt = Integer
									.parseInt(strUartProcOneReSendCnt);
							isStart = true;
						} else {
							System.out.println("Please enter the correct parameters");
							isUartProcOneReSendCnt = true;
						}
					}
				}

				while (isStart) {
					isStart = false;
					int result = sdk.ICE_IPCSDK_SetUARTCfg(uParam);
					if (result == 1) {
						System.out.println("Set successfully!");
						isContinue = true;
					} else {
						System.out.println("Set failed!");
						isReSet = true;
					}
				}

			}

		} while (isReSet);
	}

	/**
	 * Get serial port parameters
	 */
	private static void iceGetUARTCfg() {
		SDK.UartParam uParam = sdk.ICE_IPCSDK_GetUARTCfg();
		if (uParam != null) {
			String[] strScreenMode = new String[] { "Screen display control", "Pass-through control" };
			int[] nBaudrate = new int[] {1200, 2400, 4800, 9600, 19200, 38400, 115200};
			String[] strParity = new String[] {"none", "odd", "even"};
			String[] strFlowControl = new String[] {"none", "hardware", "Xon", "Xoff"};

			// RS485
			System.out.println("RS485:");
			if (uParam.uartParam1.uartEn == 0) {
				System.out.println("disable");
			} else {
				System.out.println("enable");
			}
			if (uParam.uartParam1.screen_mode >= 1
					&& uParam.uartParam1.screen_mode <= 2) {
				System.out.println("Port mode:" + strScreenMode[uParam.uartParam1.screen_mode - 1]);
			}
			else {
				System.out.println("Port mode:" + strScreenMode[0]);
			}
			
			System.out.println("Baud Rate:" + ((uParam.uartParam1.baudRate >= 0 && uParam.uartParam1.baudRate <=6) ? nBaudrate[uParam.uartParam1.baudRate] : 1200));
			System.out.println("Data Bits:8");
			System.out.println("Check Bit:" + ((uParam.uartParam1.parity >= 0 && uParam.uartParam1.parity <= 2) ? strParity[uParam.uartParam1.parity] : "none")); 
			if (uParam.uartParam1.stopBits == 0) {
				System.out.println("RS485 Stop Bit:1");
			} else {
				System.out.println("RS485 Stop Bit:2");
			}
			System.out.println("Flow control mode:" + ((uParam.uartParam1.flowControl >= 0 && uParam.uartParam1.flowControl <=3) ? strFlowControl[uParam.uartParam1.flowControl] : "none"));
			System.out.println("Retransmissions:" + uParam.uartParam1.u32UartProcOneReSendCnt);

			// RS485-2
			System.out.println("RS485-2:");
			if (uParam.uartParam2.uartEn == 0) {
				System.out.println("disable");
			} else {
				System.out.println("enable");
			}
			if (uParam.uartParam2.screen_mode >= 1
					&& uParam.uartParam2.screen_mode <= 2) {
				System.out.println("Port mode:" + strScreenMode[uParam.uartParam2.screen_mode - 1]);
			}
			else {
				System.out.println("Port mode:" + strScreenMode[0]);
			}
			System.out.println("Baud Rate:" + ((uParam.uartParam2.baudRate >= 0 && uParam.uartParam2.baudRate <=6) ? nBaudrate[uParam.uartParam2.baudRate] : 1200));
			System.out.println("Data Bits:8");
			System.out.println("Check Bit:" + ((uParam.uartParam2.parity >= 0 && uParam.uartParam2.parity <= 2) ? strParity[uParam.uartParam2.parity] : "none")); 
			if (uParam.uartParam2.stopBits == 0) {
				System.out.println("Stop Bit:1");
			} else {
				System.out.println("Stop Bit:2");
			}
			System.out.println("Flow control mode:" + ((uParam.uartParam2.flowControl >= 0 && uParam.uartParam2.flowControl <=3) ? strFlowControl[uParam.uartParam2.flowControl] : "none"));
			System.out.println("Retransmissions:" + uParam.uartParam2.u32UartProcOneReSendCnt);
		} else {
			System.out.println("Get failed");
		}
		isContinue = true;
	}

	/**
	 * Get sdk version
	 */
	public static String getVersion(String strJar) throws java.io.IOException {
		@SuppressWarnings("resource")
		JarFile jarFile = new JarFile(strJar);
		Manifest manifest = jarFile.getManifest();
		Attributes att = manifest.getMainAttributes();
		return att.getValue("Implementation-Version");
	}

	/**
	 * Judge whether it is a number
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	//The judgment string contains only English, numbers and-
	
	public static boolean strCheck(String str) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9\\\\-]+?");
		return pattern.matcher(str).matches();
	}

	//Judge whether the string contains Chinese
	public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
	/**
	 * Judge whether it is a date
	 */
	public static boolean isDate(String str) {
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

	/**
	 * Determine whether it is time
	 */
	public static boolean isTime(String str) {
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

		// time = str.split(":");
		// if(time.length != 3)
		// return false;

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

	/**
	 * Date comparison
	 */
	public static boolean compare_date(String strDateBegin, String strDateEnd) {
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

	/**
	 * Time comparison
	 */
	public static boolean compare_time(String strTimeBegin, String strTimeEnd) {
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

}
