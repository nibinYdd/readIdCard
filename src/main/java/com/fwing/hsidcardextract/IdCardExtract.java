package com.fwing.hsidcardextract;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

import com.huashi.otg.sdk.GetImg;
import com.huashi.otg.sdk.HSIDCardInfo;
import com.huashi.otg.sdk.HandlerMsg;
import com.huashi.otg.sdk.HsOtgApi;
import com.huashi.otg.sdk.Test;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @Author: Fwing
 * @CreateDate: 2020/7/15 15:48
 */
public class IdCardExtract {
    private boolean m_Con = false;
    private boolean m_Auto = false;
    private long startTime;

    private HsOtgApi api;
    private MyHandler h ;

    private static IdCardExtract instance;
    public static IdCardExtract getInstance(){
        if(instance == null){
            synchronized (IdCardExtract.class){
                if(instance == null){
                    instance = new IdCardExtract();
                }
            }
        }
        return instance;
    }

    public void init(Context context){
        h = new MyHandler(context);
        api = new HsOtgApi(h, context);
        int ret = api.init();// 因为第一次需要点击授权，所以第一次点击时候的返回是-1所以我利用了广播接受到授权后用handler发送消息
        if (ret == 1) {
            if(idCardResultCallBack != null)
                idCardResultCallBack.active(true);
            m_Con = true;
        } else {
            if(idCardResultCallBack != null)
                idCardResultCallBack.active(false);
            m_Con = false;
        }
    }

    public void extractOnce(){
        if (!m_Con) {
            if(idCardResultCallBack != null)
                idCardResultCallBack.active(false);
            return;
        }
        startTime = System.currentTimeMillis();
        if (api.Authenticate(200, 200) != 1) {
            if(idCardResultCallBack != null)
                idCardResultCallBack.active(false);
            return;
        }
        HSIDCardInfo ici = new HSIDCardInfo();
        if (api.ReadCard(ici, 200, 1300) == 1) {
            Message msg = Message.obtain();
            msg.obj = ici;
            msg.what = HandlerMsg.READ_SUCCESS;
            h.sendMessage(msg);
        }
    }

    private Thread thread = null;
    public void extractAuto(){
        m_Auto = true;
        thread = new Thread(new CPUThread());
        thread.start();
    }

    public void stopExtractAuto(){
        m_Auto = false;
        if(thread != null)
            thread.interrupt();
    }

    private IDCardResultCallBack idCardResultCallBack = null;

    public void setIDCardResultCallBack(IDCardResultCallBack callBack){
        idCardResultCallBack = callBack;
    }

    public interface IDCardResultCallBack{
        void deviceOffline(String msg);
        void connectStatus(boolean success);
        void active(boolean ok);
        void extractError();
        void extractImgError(PersonInfo info);
        void success(PersonInfo info,boolean auto,long timeConsume);
    }
    private class MyHandler extends Handler{
        private final WeakReference<Context> mAct;
        private MyHandler(Context context){
            mAct =new WeakReference<Context>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            Context act=mAct.get();
            super.handleMessage(msg);
            if(act!=null){
                if (msg.what == 99 || msg.what == 100) {
                    if(idCardResultCallBack != null)
                        idCardResultCallBack.deviceOffline((String) msg.obj);
                }
                // 第一次授权时候的判断是利用handler判断，授权过后就不用这个判断了
                if (msg.what == HandlerMsg.CONNECT_SUCCESS) {
                    m_Con = true;
                    if(idCardResultCallBack != null)
                        idCardResultCallBack.connectStatus(true);
                    // sam.setText(api.GetSAMID());
                }
                if (msg.what == HandlerMsg.CONNECT_ERROR) {
                    if(idCardResultCallBack != null)
                        idCardResultCallBack.connectStatus(false);
                }
                if (msg.what == HandlerMsg.READ_ERROR) {
                    // cz();
                    // statu.setText("卡认证失败");
                    if(idCardResultCallBack != null)
                        idCardResultCallBack.extractError();
                }
                if (msg.what == HandlerMsg.READ_SUCCESS) {

                    HSIDCardInfo ic = (HSIDCardInfo) msg.obj;
                    byte[] fp = new byte[1024];
                    fp = ic.getFpDate();
                    String m_FristPFInfo = "";
                    String m_SecondPFInfo = "";
                    PersonInfo personInfo = new PersonInfo();
                    if (fp[4] == (byte) 0x01) {
                        personInfo.setFirstFpPosition(GetFPcode(fp[5]));
                        personInfo.setFirstFpQuality(fp[6]);
                    }
                    if (fp[512 + 4] == (byte) 0x01) {
                        personInfo.setSecondFpPosition(GetFPcode(fp[512 + 5]));
                        personInfo.setSecondFpQuality(fp[512 + 6]);
                    }
                    personInfo.setPeopleName(ic.getPeopleName());
                    personInfo.setSex(ic.getSex());
                    personInfo.setBirthDay(ic.getBirthDay());
                    personInfo.setAddr(ic.getAddr());
                    personInfo.setIDCard(ic.getIDCard());
                    personInfo.setDepartment(ic.getDepartment());
                    personInfo.setStartDate(ic.getStrartDate());
                    personInfo.setEndDate(ic.getEndDate());
                    if (ic.getcertType() == " ") {
                        personInfo.setIdType(PersonInfo.IDCARD_TYPE.SFZ);
                        personInfo.setPeople(ic.getPeople());
                    } else {
                        if (ic.getcertType() == "J") {
                            personInfo.setissuesNum(ic.getissuesNum());
                            personInfo.setPassCheckID(ic.getPassCheckID());
                            personInfo.setIdType(PersonInfo.IDCARD_TYPE.GATJZZ);
                        } else {
                            if (ic.getcertType() == "I") {
                                personInfo.setstrChineseName(ic.getstrChineseName());
                                personInfo.setIdType(PersonInfo.IDCARD_TYPE.WGRYJJLZ);
                                personInfo.setstrNationCode(ic.getstrNationCode());
                                personInfo.setstrCertVer(ic.getstrCertVer());
                            }
                        }
                    }
                    try {
                        byte[] bmpBuf = new byte[102 * 126 * 3 + 54 + 126 * 2]; // 照片头像bmp数据
                        String bmpPath = "";
                        int ret = api.unpack(ic.getwltdata(), bmpBuf, bmpPath);

                        if (ret != 1) {//
                            if(idCardResultCallBack != null)
                                idCardResultCallBack.extractImgError(personInfo);
                            return;
                        }

                        personInfo.setbmpdata(bmpBuf);
                        if (!m_Auto) {
                            startTime = System.currentTimeMillis() - startTime;
                            if(idCardResultCallBack != null)
                                idCardResultCallBack.success(personInfo,false,startTime);
                        }
                        else{
                            if(idCardResultCallBack != null)
                                idCardResultCallBack.success(personInfo,true,0);
                        }
                    } catch (Exception e) {
                        if(idCardResultCallBack != null)
                            idCardResultCallBack.extractImgError(personInfo);
                    }

                }
            }
        }
    }


    public class CPUThread extends Thread {
        public CPUThread() {
            super();
        }

        @Override
        public void run() {
            super.run();
            HSIDCardInfo ici;
            Message msg;
            while (m_Auto) {
                if (api.Authenticate(200, 200) != 1) {
                    msg = Message.obtain();
                    msg.what = HandlerMsg.READ_ERROR;
                    h.sendMessage(msg);
                } else {
                    ici = new HSIDCardInfo();
                    if (api.ReadCard(ici, 200, 1300) == 1) {
                        msg = Message.obtain();
                        msg.obj = ici;
                        msg.what = HandlerMsg.READ_SUCCESS;
                        h.sendMessage(msg);
                    }
                }
                SystemClock.sleep(300);
                msg = Message.obtain();
                msg.what = HandlerMsg.READ_ERROR;
                h.sendMessage(msg);
                SystemClock.sleep(300);
            }

        }
    }

    /**
     * 指纹 指位代码
     *
     * @param FPcode
     * @return
     */
    private String GetFPcode(int FPcode) {
        switch (FPcode) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "未知";
        }
    }
}
