package com.example.simon.sdktest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Environment;
import java.io.File;

import ice_ipcsdk.SDK;
import ice_ipcsdk.LprResult;

public class MainActivity extends AppCompatActivity {

    SDK sdk = null;
    public TextView ipText;
    public static TextView plateText;
    public static TextView statusText;
    public static ImageView plateImage;
    public static ImageView videoStream;
   public static  int nTotalNum = 0;

    public static class PlateInfo {
        public String number;
        public String color;
        public byte[] picdata;
    }

    //lpr result
    public static  class lprCallback implements SDK.ILprResultCallback{
        public void ICE_IPCSDK_LprResult(String strIP, LprResult lprResult, byte[] bData, int iFullPicOffset, int iFullPicLen,
                                         int iPlatePicOffset, int iPlatePicLen, int iReserve1, int iReserve2){
            Log.i("sdk", "callback");
            PlateInfo info = new PlateInfo();
            info.number = lprResult.getStrPlateNum();
            info.color = lprResult.getStrPlateColor();

            info.picdata = new byte[iFullPicLen];
            System.arraycopy(bData, iFullPicOffset, info.picdata, 0, iFullPicLen);

            Message msg = new Message();
            msg.obj = info;
            msg.what = 5;

            myHandler.sendMessage(msg);
        }
    }
    //mjpeg video
    public static class mjpeg_callback implements SDK.IMJpegCallback_Static {
        public void ICE_IPCSDK_MJpeg(String strIP, byte[] bData, int length)
        {
            byte[] bMjpegData = new byte[length];
            System.arraycopy(bData, 0, bMjpegData, 0, length);
            Message msg = new Message();
            msg.obj = bMjpegData;
            msg.what = 1;
            myHandler.sendMessage(msg);
        }
    }

    public static class devEvent_callback implements SDK.IDeviceEventCallback {
        public void ICE_IPCSDK_OnDeviceEvent(String strIP, int nEventType,
                                             int nEventData1, int nEventData2, int nEventData3,
                                             int nEventData4){
            Message msg = new Message();
            msg.obj = nEventType;
            msg.what = 2;
            myHandler.sendMessage(msg);
        }
    }

    public static Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i("sdk", "msg:" + msg.what);
            switch (msg.what) {
                case 5://lpr result
                    nTotalNum++;
                    PlateInfo info = (PlateInfo) msg.obj;
                    plateText.setText(nTotalNum + ":" + info.number + ", " + info.color);
                    info.number = null;
                    info.color = null;

                    Bitmap bmp = BitmapFactory.decodeByteArray(info.picdata, 0, info.picdata.length);
                    plateImage.setImageBitmap(bmp);
                    plateImage.invalidate();
                    bmp = null;

                    msg.obj = null;
                    msg = null;
                    break;
                case 1://mjpeg video stream data
                    byte[] data = (byte[])msg.obj;
                    Bitmap bmpStream = BitmapFactory.decodeByteArray(data, 0, data.length);
                    videoStream.setImageBitmap(bmpStream);
                    videoStream.invalidate();
                    bmpStream = null;

                    msg.obj = null;
                    msg = null;
                    break;
                case 2://connect status
                    int nEventType = (int)msg.obj;
                    strEventType = strEventType + nEventType;
                   if (nEventType == 0)
                       statusText.setText("offline");
                   else if (nEventType == 1)
                       statusText.setText("online");
                   Log.i("sdk", "status changed:" + nEventType);
                   break;
                case 3:
                    statusText.setText("connect failed");
                    break;
                case 4:
                    statusText.setText("trigger failed");
                    break;
                case 6:
                    statusText.setText("offline");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    lprCallback callback;
    mjpeg_callback mjpegCallback;
    devEvent_callback devEventCallback;
    static String strEventType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipText = (TextView)findViewById(R.id.editText);
        plateText = (TextView)findViewById(R.id.test);
        statusText = (TextView)findViewById(R.id.textView_status);
        plateImage = (ImageView)findViewById(R.id.imageView);
        videoStream = (ImageView)findViewById(R.id.imageView_video);

        callback = new lprCallback();
        mjpegCallback = new mjpeg_callback();
        devEventCallback = new devEvent_callback();
    }

    public void OnClick(View v) {
        new Thread(){
            public void run(){
                if (sdk != null) {
                    return;
                }

                Message msg = new Message();
                sdk = new SDK();
                sdk.ICE_IPCSDK_SetDeviceEventCallBack(devEventCallback);
                int iRet = sdk.ICE_IPCSDK_OpenDevice(ipText.getText().toString());//connect
                if (1 != iRet){
                    msg.obj = "Connect failed";
                    msg.what = 3;
                    myHandler.sendMessage(msg);
                    return;
                }

                for (int i = 0; i < 3; i++) {
                    iRet = sdk.ICE_IPCSDK_RegLprEvent(true);
                    if (1 == iRet)
                        break;
                }
                if (1 != iRet){
                    msg.obj = "Connect failed";
                    msg.what = 3;
                    myHandler.sendMessage(msg);
                    sdk.ICE_IPCSDK_Close();
                    sdk = null;
                    return;
                }

                sdk.ICE_IPCSDK_SetLprResultCallback(callback);
                sdk.ICE_IPCSDK_SetMJpegallback_Static(mjpegCallback);
                sdk.ICE_IPCSDK_LogConfig(1, Environment.getExternalStorageDirectory()+  File.separator+ "Logs");
            }
        }.start();

    }

    public void OnClick_Close(View v){
        new Thread(){
            public void run(){
                Log.i("sdk", "close");
                if (sdk != null){
                    sdk.ICE_IPCSDK_Close();
                    sdk = null;
                    Log.i("sdk", "close end");
                    Message msg = new Message();
                    msg.what = 6;
                    myHandler.sendMessage(msg);
                }
            }
        }.start();

    }

    public void OnClick_Trigger(View v){
        new Thread(){
            public void run(){
                if (sdk != null){
                    Message msg = new Message();
                    int iRet = sdk.ICE_IPCSDK_TriggerExt();
                    if (1 != iRet){
                        msg.obj = "Trigger failed";
                        msg.what = 4;
                    }

                    myHandler.sendMessage(msg);
                }
            }
        }.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
