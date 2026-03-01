package mktvsmart.screen;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.dataconvert.model.DataConvertRcuModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.hisientry.MirrorPageView;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.view.BitmapTouchChecker;
import mktvsmart.screen.view.GsViewPager;
import mktvsmart.screen.view.IrregularButton;
import mktvsmart.screen.view.OrientationButton;
import mktvsmart.screen.view.RemoteControlTouchPad;

/* loaded from: classes.dex */
public class GsRemoteControlActivity extends Activity implements View.OnClickListener {
    private static final int ON_SCROLL_TIME_PERIOD = 100;
    private static boolean isMuted;
    private ImageButton[] buttonArray;
    private ImageView dot0;
    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;
    private MessageProcessor msgProc;
    private ImageButton muteButton;
    private IrregularButton okButton;
    private OrientationButton orientationButton;
    private View remotePage1;
    private View remotePage2;
    private View remotePage3;
    private GsViewPager remoteViewPager;
    private ArrayList<View> remoteViews;
    private View remotepage0;
    private Timer scrollTimer;
    private Socket tcpSocket;
    private RemoteControlTouchPad touchPad;
    private Map<Integer, Integer> keyMapInPage1 = new HashMap<Integer, Integer>() { // from class: mktvsmart.screen.GsRemoteControlActivity.1
        private static final long serialVersionUID = -1798187777433430490L;

        AnonymousClass1() {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                put(Integer.valueOf(R.id.power_button), 44);
                put(Integer.valueOf(R.id.exit_button), 21);
                put(Integer.valueOf(R.id.menu_button), 20);
                put(Integer.valueOf(R.id.red_button), 16);
                put(Integer.valueOf(R.id.green_button), 17);
                put(Integer.valueOf(R.id.yellow_button), 18);
                put(Integer.valueOf(R.id.blue_button), 19);
                return;
            }
            put(Integer.valueOf(R.id.power_button), 42);
            put(Integer.valueOf(R.id.exit_button), 7);
            put(Integer.valueOf(R.id.menu_button), 6);
            put(Integer.valueOf(R.id.red_button), 8);
            put(Integer.valueOf(R.id.green_button), 9);
            put(Integer.valueOf(R.id.yellow_button), 10);
            put(Integer.valueOf(R.id.blue_button), 11);
        }
    };
    private Map<Integer, Integer> keyMapInPage2 = new HashMap<Integer, Integer>() { // from class: mktvsmart.screen.GsRemoteControlActivity.2
        private static final long serialVersionUID = 4309794455137147134L;

        AnonymousClass2() {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                put(Integer.valueOf(R.id.digit_0_button), 10);
                put(Integer.valueOf(R.id.digit_1_button), 1);
                put(Integer.valueOf(R.id.digit_2_button), 2);
                put(Integer.valueOf(R.id.digit_3_button), 3);
                put(Integer.valueOf(R.id.digit_4_button), 4);
                put(Integer.valueOf(R.id.digit_5_button), 5);
                put(Integer.valueOf(R.id.digit_6_button), 6);
                put(Integer.valueOf(R.id.digit_7_button), 7);
                put(Integer.valueOf(R.id.digit_8_button), 8);
                put(Integer.valueOf(R.id.digit_9_button), 9);
                put(Integer.valueOf(R.id.recall_button), 58);
                put(Integer.valueOf(R.id.tv_radio_button), 11);
                put(Integer.valueOf(R.id.page_up_button), 34);
                put(Integer.valueOf(R.id.page_down_button), 35);
                put(Integer.valueOf(R.id.epg_button), 29);
                put(Integer.valueOf(R.id.txt_button), 30);
                put(Integer.valueOf(R.id.sub_button), 28);
                put(Integer.valueOf(R.id.usb_button), 46);
                put(Integer.valueOf(R.id.record_button), 43);
                put(Integer.valueOf(R.id.play_button), 38);
                put(Integer.valueOf(R.id.stop_button), 39);
                put(Integer.valueOf(R.id.pause_button), 42);
                put(Integer.valueOf(R.id.previous_button), 40);
                put(Integer.valueOf(R.id.next_button), 41);
                put(Integer.valueOf(R.id.fast_back_button), 36);
                put(Integer.valueOf(R.id.fast_go_button), 37);
                return;
            }
            put(Integer.valueOf(R.id.digit_0_button), 12);
            put(Integer.valueOf(R.id.digit_1_button), 13);
            put(Integer.valueOf(R.id.digit_2_button), 14);
            put(Integer.valueOf(R.id.digit_3_button), 15);
            put(Integer.valueOf(R.id.digit_4_button), 16);
            put(Integer.valueOf(R.id.digit_5_button), 17);
            put(Integer.valueOf(R.id.digit_6_button), 18);
            put(Integer.valueOf(R.id.digit_7_button), 19);
            put(Integer.valueOf(R.id.digit_8_button), 20);
            put(Integer.valueOf(R.id.digit_9_button), 21);
            put(Integer.valueOf(R.id.recall_button), 29);
            put(Integer.valueOf(R.id.tv_radio_button), 22);
            put(Integer.valueOf(R.id.page_up_button), 37);
            put(Integer.valueOf(R.id.page_down_button), 38);
            put(Integer.valueOf(R.id.epg_button), 32);
            put(Integer.valueOf(R.id.txt_button), 34);
            put(Integer.valueOf(R.id.sub_button), 31);
            put(Integer.valueOf(R.id.usb_button), 43);
            put(Integer.valueOf(R.id.record_button), 58);
            put(Integer.valueOf(R.id.play_button), 61);
            put(Integer.valueOf(R.id.stop_button), 62);
            put(Integer.valueOf(R.id.pause_button), 63);
            put(Integer.valueOf(R.id.previous_button), 64);
            put(Integer.valueOf(R.id.next_button), 65);
            put(Integer.valueOf(R.id.fast_back_button), 59);
            put(Integer.valueOf(R.id.fast_go_button), 60);
        }
    };
    private int currentIndex = 0;

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$1 */
    class AnonymousClass1 extends HashMap<Integer, Integer> {
        private static final long serialVersionUID = -1798187777433430490L;

        AnonymousClass1() {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                put(Integer.valueOf(R.id.power_button), 44);
                put(Integer.valueOf(R.id.exit_button), 21);
                put(Integer.valueOf(R.id.menu_button), 20);
                put(Integer.valueOf(R.id.red_button), 16);
                put(Integer.valueOf(R.id.green_button), 17);
                put(Integer.valueOf(R.id.yellow_button), 18);
                put(Integer.valueOf(R.id.blue_button), 19);
                return;
            }
            put(Integer.valueOf(R.id.power_button), 42);
            put(Integer.valueOf(R.id.exit_button), 7);
            put(Integer.valueOf(R.id.menu_button), 6);
            put(Integer.valueOf(R.id.red_button), 8);
            put(Integer.valueOf(R.id.green_button), 9);
            put(Integer.valueOf(R.id.yellow_button), 10);
            put(Integer.valueOf(R.id.blue_button), 11);
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$2 */
    class AnonymousClass2 extends HashMap<Integer, Integer> {
        private static final long serialVersionUID = 4309794455137147134L;

        AnonymousClass2() {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                put(Integer.valueOf(R.id.digit_0_button), 10);
                put(Integer.valueOf(R.id.digit_1_button), 1);
                put(Integer.valueOf(R.id.digit_2_button), 2);
                put(Integer.valueOf(R.id.digit_3_button), 3);
                put(Integer.valueOf(R.id.digit_4_button), 4);
                put(Integer.valueOf(R.id.digit_5_button), 5);
                put(Integer.valueOf(R.id.digit_6_button), 6);
                put(Integer.valueOf(R.id.digit_7_button), 7);
                put(Integer.valueOf(R.id.digit_8_button), 8);
                put(Integer.valueOf(R.id.digit_9_button), 9);
                put(Integer.valueOf(R.id.recall_button), 58);
                put(Integer.valueOf(R.id.tv_radio_button), 11);
                put(Integer.valueOf(R.id.page_up_button), 34);
                put(Integer.valueOf(R.id.page_down_button), 35);
                put(Integer.valueOf(R.id.epg_button), 29);
                put(Integer.valueOf(R.id.txt_button), 30);
                put(Integer.valueOf(R.id.sub_button), 28);
                put(Integer.valueOf(R.id.usb_button), 46);
                put(Integer.valueOf(R.id.record_button), 43);
                put(Integer.valueOf(R.id.play_button), 38);
                put(Integer.valueOf(R.id.stop_button), 39);
                put(Integer.valueOf(R.id.pause_button), 42);
                put(Integer.valueOf(R.id.previous_button), 40);
                put(Integer.valueOf(R.id.next_button), 41);
                put(Integer.valueOf(R.id.fast_back_button), 36);
                put(Integer.valueOf(R.id.fast_go_button), 37);
                return;
            }
            put(Integer.valueOf(R.id.digit_0_button), 12);
            put(Integer.valueOf(R.id.digit_1_button), 13);
            put(Integer.valueOf(R.id.digit_2_button), 14);
            put(Integer.valueOf(R.id.digit_3_button), 15);
            put(Integer.valueOf(R.id.digit_4_button), 16);
            put(Integer.valueOf(R.id.digit_5_button), 17);
            put(Integer.valueOf(R.id.digit_6_button), 18);
            put(Integer.valueOf(R.id.digit_7_button), 19);
            put(Integer.valueOf(R.id.digit_8_button), 20);
            put(Integer.valueOf(R.id.digit_9_button), 21);
            put(Integer.valueOf(R.id.recall_button), 29);
            put(Integer.valueOf(R.id.tv_radio_button), 22);
            put(Integer.valueOf(R.id.page_up_button), 37);
            put(Integer.valueOf(R.id.page_down_button), 38);
            put(Integer.valueOf(R.id.epg_button), 32);
            put(Integer.valueOf(R.id.txt_button), 34);
            put(Integer.valueOf(R.id.sub_button), 31);
            put(Integer.valueOf(R.id.usb_button), 43);
            put(Integer.valueOf(R.id.record_button), 58);
            put(Integer.valueOf(R.id.play_button), 61);
            put(Integer.valueOf(R.id.stop_button), 62);
            put(Integer.valueOf(R.id.pause_button), 63);
            put(Integer.valueOf(R.id.previous_button), 64);
            put(Integer.valueOf(R.id.next_button), 65);
            put(Integer.valueOf(R.id.fast_back_button), 59);
            put(Integer.valueOf(R.id.fast_go_button), 60);
        }
    }

    public static void setMuted(boolean isMuted2) {
        isMuted = isMuted2;
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.setOnMessageProcess(19, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsRemoteControlActivity.3
            AnonymousClass3() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws NumberFormatException {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    DataParser parser = ParserFactory.getParser();
                    List<?> list = null;
                    try {
                        InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        list = parser.parse(instream, 15);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int mute_state = Integer.parseInt((String) list.get(0));
                    if (mute_state == 1) {
                        GsRemoteControlActivity.this.muteButton.setBackgroundResource(R.drawable.remote_btn_muted);
                        GsRemoteControlActivity.isMuted = true;
                    } else {
                        GsRemoteControlActivity.this.muteButton.setBackgroundResource(R.drawable.remote_btn_no_mute);
                        GsRemoteControlActivity.isMuted = false;
                    }
                }
            }
        });
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsRemoteControlActivity.4
            AnonymousClass4() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                Toast.makeText(GsRemoteControlActivity.this, R.string.return_login_list_reason, 0).show();
                Intent intent = new Intent();
                intent.setClass(GsRemoteControlActivity.this, GsLoginListActivity.class);
                GsRemoteControlActivity.this.startActivity(intent);
                GsRemoteControlActivity.this.finish();
            }
        });
        this.msgProc.setOnMessageProcess(2003, this, new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.GsRemoteControlActivity.5
            AnonymousClass5() {
            }

            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GsRemoteControlActivity.this.tcpSocket, 19);
            }
        });
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$3 */
    class AnonymousClass3 implements MessageProcessor.PerformOnForeground {
        AnonymousClass3() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) throws NumberFormatException {
            if (msg.arg1 > 0) {
                Bundle data = msg.getData();
                byte[] recvData = data.getByteArray("ReceivedData");
                DataParser parser = ParserFactory.getParser();
                List<?> list = null;
                try {
                    InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
                    list = parser.parse(instream, 15);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int mute_state = Integer.parseInt((String) list.get(0));
                if (mute_state == 1) {
                    GsRemoteControlActivity.this.muteButton.setBackgroundResource(R.drawable.remote_btn_muted);
                    GsRemoteControlActivity.isMuted = true;
                } else {
                    GsRemoteControlActivity.this.muteButton.setBackgroundResource(R.drawable.remote_btn_no_mute);
                    GsRemoteControlActivity.isMuted = false;
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$4 */
    class AnonymousClass4 implements MessageProcessor.PerformOnForeground {
        AnonymousClass4() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) {
            Toast.makeText(GsRemoteControlActivity.this, R.string.return_login_list_reason, 0).show();
            Intent intent = new Intent();
            intent.setClass(GsRemoteControlActivity.this, GsLoginListActivity.class);
            GsRemoteControlActivity.this.startActivity(intent);
            GsRemoteControlActivity.this.finish();
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$5 */
    class AnonymousClass5 implements MessageProcessor.PerformOnForeground {
        AnonymousClass5() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) throws UnsupportedEncodingException {
            GsSendSocket.sendOnlyCommandSocketToStb(GsRemoteControlActivity.this.tcpSocket, 19);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.remote_control);
        setMessageProcess();
        this.buttonArray = new ImageButton[this.keyMapInPage1.size() + this.keyMapInPage2.size()];
        this.dot0 = (ImageView) findViewById(R.id.remote_page_dot_0);
        this.dot1 = (ImageView) findViewById(R.id.remote_page_dot_1);
        this.dot2 = (ImageView) findViewById(R.id.remote_page_dot_2);
        this.dot3 = (ImageView) findViewById(R.id.remote_page_dot_3);
        LayoutInflater inflater = LayoutInflater.from(this);
        this.remotepage0 = inflater.inflate(R.layout.remote_control_page_0, (ViewGroup) null);
        this.remotePage1 = inflater.inflate(R.layout.remote_control_page_1, (ViewGroup) null);
        this.remotePage2 = inflater.inflate(R.layout.remote_control_page_2, (ViewGroup) null);
        this.remoteViews = new ArrayList<>();
        this.remoteViews.add(this.remotepage0);
        this.remoteViews.add(this.remotePage1);
        this.remoteViews.add(this.remotePage2);
        if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
            this.dot3.setVisibility(0);
            this.remotePage3 = new MirrorPageView(this);
            ((MirrorPageView) this.remotePage3).mControl_close.setBackgroundResource(R.drawable.ic_crop_circle);
            ((MirrorPageView) this.remotePage3).setFullScreenListner(new MirrorPageView.OnFullScreenListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.6
                AnonymousClass6() {
                }

                @Override // mktvsmart.screen.hisientry.MirrorPageView.OnFullScreenListener
                public void onFullScreenRequest(boolean needFullScreen) {
                    Activity parentActivity = GsRemoteControlActivity.this.getParent();
                    if (parentActivity != null && (parentActivity instanceof GsMainTabHostActivity)) {
                        if (needFullScreen) {
                            GsRemoteControlActivity.this.findViewById(R.id.remote_layout_title).setVisibility(8);
                            GsRemoteControlActivity.this.findViewById(R.id.dot_group_layout).setVisibility(8);
                        } else {
                            GsRemoteControlActivity.this.findViewById(R.id.remote_layout_title).setVisibility(0);
                            GsRemoteControlActivity.this.findViewById(R.id.dot_group_layout).setVisibility(0);
                        }
                        ((GsMainTabHostActivity) parentActivity).setNavigationVisible(!needFullScreen);
                    }
                    if (parentActivity.getRequestedOrientation() == 1) {
                        parentActivity.setRequestedOrientation(0);
                    } else {
                        parentActivity.setRequestedOrientation(1);
                    }
                    GsRemoteControlActivity.this.remoteViewPager.setScrollable(needFullScreen ? false : true);
                }
            });
            this.remoteViews.add(this.remotePage3);
        } else {
            this.dot3.setVisibility(8);
        }
        this.remoteViewPager = (GsViewPager) findViewById(R.id.remote_pager);
        this.remoteViewPager.setAdapter(new RemotePagerAdapter(this, null));
        this.remoteViewPager.setCurrentItem(0);
        this.remoteViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.7
            AnonymousClass7() {
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                Animation animation = null;
                switch (position) {
                    case 0:
                        GsRemoteControlActivity.this.dot0.setBackgroundResource(R.drawable.view_pager_dot_selected);
                        GsRemoteControlActivity.this.dot1.setBackgroundResource(R.drawable.view_pager_dot);
                        if (GsRemoteControlActivity.this.currentIndex == position + 1) {
                            animation = new TranslateAnimation(position + 1, position, 0.0f, 0.0f);
                            break;
                        }
                        break;
                    case 1:
                        GsRemoteControlActivity.this.dot0.setBackgroundResource(R.drawable.view_pager_dot);
                        GsRemoteControlActivity.this.dot1.setBackgroundResource(R.drawable.view_pager_dot_selected);
                        GsRemoteControlActivity.this.dot2.setBackgroundResource(R.drawable.view_pager_dot);
                        if (GsRemoteControlActivity.this.currentIndex != position - 1) {
                            if (GsRemoteControlActivity.this.currentIndex == position + 1) {
                                animation = new TranslateAnimation(position + 1, position, 0.0f, 0.0f);
                                break;
                            }
                        } else {
                            animation = new TranslateAnimation(position - 1, position, 0.0f, 0.0f);
                            break;
                        }
                        break;
                    case 2:
                        GsRemoteControlActivity.this.dot1.setBackgroundResource(R.drawable.view_pager_dot);
                        GsRemoteControlActivity.this.dot2.setBackgroundResource(R.drawable.view_pager_dot_selected);
                        if (GsRemoteControlActivity.this.currentIndex != position - 1) {
                            if (GsRemoteControlActivity.this.currentIndex == position + 1) {
                                animation = new TranslateAnimation(position + 1, position, 0.0f, 0.0f);
                            }
                        } else {
                            animation = new TranslateAnimation(position - 1, position, 0.0f, 0.0f);
                        }
                        if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                            GsRemoteControlActivity.this.dot3.setBackgroundResource(R.drawable.view_pager_dot);
                            break;
                        }
                        break;
                    case 3:
                        GsRemoteControlActivity.this.dot2.setBackgroundResource(R.drawable.view_pager_dot);
                        GsRemoteControlActivity.this.dot3.setBackgroundResource(R.drawable.view_pager_dot_selected);
                        if (GsRemoteControlActivity.this.currentIndex == position - 1) {
                            animation = new TranslateAnimation(position - 1, position, 0.0f, 0.0f);
                        }
                        if (GsRemoteControlActivity.this.remotePage3 != null && (GsRemoteControlActivity.this.remotePage3 instanceof MirrorPageView)) {
                            ((MirrorPageView) GsRemoteControlActivity.this.remotePage3).resume();
                            break;
                        }
                        break;
                }
                if (GsRemoteControlActivity.this.currentIndex == 3 && GsRemoteControlActivity.this.remotePage3 != null && (GsRemoteControlActivity.this.remotePage3 instanceof MirrorPageView)) {
                    ((MirrorPageView) GsRemoteControlActivity.this.remotePage3).pause();
                }
                GsRemoteControlActivity.this.currentIndex = position;
                animation.setFillAfter(true);
                animation.setDuration(300L);
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                GsRemoteControlActivity.this.orientationButton.setBackgroundResource(R.drawable.remote_btn_orientation);
                GsRemoteControlActivity.this.orientationButton.setLongPressable(false);
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    GsRemoteControlActivity.this.orientationButton.setLongPressable(true);
                }
            }
        });
        findView();
        try {
            this.tcpSocket = new CreateSocket(null, 0).GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$6 */
    class AnonymousClass6 implements MirrorPageView.OnFullScreenListener {
        AnonymousClass6() {
        }

        @Override // mktvsmart.screen.hisientry.MirrorPageView.OnFullScreenListener
        public void onFullScreenRequest(boolean needFullScreen) {
            Activity parentActivity = GsRemoteControlActivity.this.getParent();
            if (parentActivity != null && (parentActivity instanceof GsMainTabHostActivity)) {
                if (needFullScreen) {
                    GsRemoteControlActivity.this.findViewById(R.id.remote_layout_title).setVisibility(8);
                    GsRemoteControlActivity.this.findViewById(R.id.dot_group_layout).setVisibility(8);
                } else {
                    GsRemoteControlActivity.this.findViewById(R.id.remote_layout_title).setVisibility(0);
                    GsRemoteControlActivity.this.findViewById(R.id.dot_group_layout).setVisibility(0);
                }
                ((GsMainTabHostActivity) parentActivity).setNavigationVisible(!needFullScreen);
            }
            if (parentActivity.getRequestedOrientation() == 1) {
                parentActivity.setRequestedOrientation(0);
            } else {
                parentActivity.setRequestedOrientation(1);
            }
            GsRemoteControlActivity.this.remoteViewPager.setScrollable(needFullScreen ? false : true);
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$7 */
    class AnonymousClass7 implements ViewPager.OnPageChangeListener {
        AnonymousClass7() {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int position) {
            Animation animation = null;
            switch (position) {
                case 0:
                    GsRemoteControlActivity.this.dot0.setBackgroundResource(R.drawable.view_pager_dot_selected);
                    GsRemoteControlActivity.this.dot1.setBackgroundResource(R.drawable.view_pager_dot);
                    if (GsRemoteControlActivity.this.currentIndex == position + 1) {
                        animation = new TranslateAnimation(position + 1, position, 0.0f, 0.0f);
                        break;
                    }
                    break;
                case 1:
                    GsRemoteControlActivity.this.dot0.setBackgroundResource(R.drawable.view_pager_dot);
                    GsRemoteControlActivity.this.dot1.setBackgroundResource(R.drawable.view_pager_dot_selected);
                    GsRemoteControlActivity.this.dot2.setBackgroundResource(R.drawable.view_pager_dot);
                    if (GsRemoteControlActivity.this.currentIndex != position - 1) {
                        if (GsRemoteControlActivity.this.currentIndex == position + 1) {
                            animation = new TranslateAnimation(position + 1, position, 0.0f, 0.0f);
                            break;
                        }
                    } else {
                        animation = new TranslateAnimation(position - 1, position, 0.0f, 0.0f);
                        break;
                    }
                    break;
                case 2:
                    GsRemoteControlActivity.this.dot1.setBackgroundResource(R.drawable.view_pager_dot);
                    GsRemoteControlActivity.this.dot2.setBackgroundResource(R.drawable.view_pager_dot_selected);
                    if (GsRemoteControlActivity.this.currentIndex != position - 1) {
                        if (GsRemoteControlActivity.this.currentIndex == position + 1) {
                            animation = new TranslateAnimation(position + 1, position, 0.0f, 0.0f);
                        }
                    } else {
                        animation = new TranslateAnimation(position - 1, position, 0.0f, 0.0f);
                    }
                    if (GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74) {
                        GsRemoteControlActivity.this.dot3.setBackgroundResource(R.drawable.view_pager_dot);
                        break;
                    }
                    break;
                case 3:
                    GsRemoteControlActivity.this.dot2.setBackgroundResource(R.drawable.view_pager_dot);
                    GsRemoteControlActivity.this.dot3.setBackgroundResource(R.drawable.view_pager_dot_selected);
                    if (GsRemoteControlActivity.this.currentIndex == position - 1) {
                        animation = new TranslateAnimation(position - 1, position, 0.0f, 0.0f);
                    }
                    if (GsRemoteControlActivity.this.remotePage3 != null && (GsRemoteControlActivity.this.remotePage3 instanceof MirrorPageView)) {
                        ((MirrorPageView) GsRemoteControlActivity.this.remotePage3).resume();
                        break;
                    }
                    break;
            }
            if (GsRemoteControlActivity.this.currentIndex == 3 && GsRemoteControlActivity.this.remotePage3 != null && (GsRemoteControlActivity.this.remotePage3 instanceof MirrorPageView)) {
                ((MirrorPageView) GsRemoteControlActivity.this.remotePage3).pause();
            }
            GsRemoteControlActivity.this.currentIndex = position;
            animation.setFillAfter(true);
            animation.setDuration(300L);
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            GsRemoteControlActivity.this.orientationButton.setBackgroundResource(R.drawable.remote_btn_orientation);
            GsRemoteControlActivity.this.orientationButton.setLongPressable(false);
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                GsRemoteControlActivity.this.orientationButton.setLongPressable(true);
            }
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.remoteViewPager.getCurrentItem() == 3) {
            ((MirrorPageView) this.remotePage3).resume();
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        if (this.remoteViewPager.getCurrentItem() == 3 && this.remotePage3 != null && (this.remotePage3 instanceof MirrorPageView)) {
            ((MirrorPageView) this.remotePage3).pause();
        }
        Log.d("AAA", "onPause");
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        Log.d("AAA", "onStop");
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.remotePage3 != null && (this.remotePage3 instanceof MirrorPageView)) {
            ((MirrorPageView) this.remotePage3).doFinishWork();
        }
        super.onDestroy();
    }

    private void findView() {
        int loop = 0;
        for (Integer keyId : this.keyMapInPage1.keySet()) {
            this.buttonArray[loop] = (ImageButton) this.remotePage1.findViewById(keyId.intValue());
            loop++;
        }
        for (Integer keyId2 : this.keyMapInPage2.keySet()) {
            this.buttonArray[loop] = (ImageButton) this.remotePage2.findViewById(keyId2.intValue());
            loop++;
        }
        this.muteButton = (ImageButton) this.remotePage1.findViewById(R.id.mute_button);
        this.orientationButton = (OrientationButton) this.remotePage1.findViewById(R.id.orientation_button);
        this.okButton = (IrregularButton) this.remotePage1.findViewById(R.id.ok_button);
        this.touchPad = (RemoteControlTouchPad) this.remotepage0.findViewById(R.id.touch_pad);
        for (int loop2 = 0; loop2 < this.keyMapInPage1.size() + this.keyMapInPage2.size(); loop2++) {
            this.buttonArray[loop2].setOnClickListener(this);
        }
        this.muteButton.setOnTouchListener(new View.OnTouchListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.8
            AnonymousClass8() {
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case 0:
                        if (GsRemoteControlActivity.isMuted) {
                            v.setBackgroundResource(R.drawable.remote_btn_muted_selected);
                            break;
                        } else {
                            v.setBackgroundResource(R.drawable.remote_btn_no_mute_selected);
                            break;
                        }
                    case 1:
                        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                        if (login.getPlatform_id() == 30) {
                            GsRemoteControlActivity.this.sendKeyValue(12);
                        } else {
                            GsRemoteControlActivity.this.sendKeyValue(23);
                        }
                        if (GsRemoteControlActivity.isMuted) {
                            v.setBackgroundResource(R.drawable.remote_btn_muted);
                            break;
                        } else {
                            v.setBackgroundResource(R.drawable.remote_btn_no_mute);
                            break;
                        }
                }
                return true;
            }
        });
        this.orientationButton.setTouchChecker(new BitmapTouchChecker(((BitmapDrawable) getResources().getDrawable(R.drawable.remote_btn_orientation)).getBitmap()));
        this.okButton.setTouchChecker(new BitmapTouchChecker(((BitmapDrawable) getResources().getDrawable(R.drawable.remote_btn_ok)).getBitmap()));
        this.okButton.setOnActionUpListener(new IrregularButton.OnActionUpListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.9
            AnonymousClass9() {
            }

            @Override // mktvsmart.screen.view.IrregularButton.OnActionUpListener
            public void onAtionUp(View v, int orientation) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(27);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(5);
                }
            }
        });
        this.orientationButton.setOnActionDownListener(new IrregularButton.OnActionDownListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.10
            AnonymousClass10() {
            }

            @Override // mktvsmart.screen.view.IrregularButton.OnActionDownListener
            public void onActionDown(View v, int orientation) {
                int drawableId = R.drawable.remote_btn_orientation;
                switch (orientation) {
                    case 1:
                    case 11:
                    case 12:
                        drawableId = R.drawable.remote_btn_orientation_up_selected;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        drawableId = R.drawable.remote_btn_orientation_right_selected;
                        break;
                    case 5:
                    case 6:
                    case 7:
                        drawableId = R.drawable.remote_btn_orientation_down_selected;
                        break;
                    case 8:
                    case 9:
                    case 10:
                        drawableId = R.drawable.remote_btn_orientation_left_selected;
                        break;
                }
                v.setBackgroundResource(drawableId);
            }
        });
        this.orientationButton.setOnActionUpListener(new IrregularButton.OnActionUpListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.11
            AnonymousClass11() {
            }

            @Override // mktvsmart.screen.view.IrregularButton.OnActionUpListener
            public void onAtionUp(View v, int orientation) {
                int keyValue = 0;
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                switch (orientation) {
                    case 1:
                    case 11:
                    case 12:
                        if (login.getPlatform_id() == 30) {
                            keyValue = 24;
                            break;
                        } else {
                            keyValue = 1;
                            break;
                        }
                    case 2:
                    case 3:
                    case 4:
                        if (login.getPlatform_id() == 30) {
                            keyValue = 23;
                            break;
                        } else {
                            keyValue = 4;
                            break;
                        }
                    case 5:
                    case 6:
                    case 7:
                        if (login.getPlatform_id() == 30) {
                            keyValue = 25;
                            break;
                        } else {
                            keyValue = 2;
                            break;
                        }
                    case 8:
                    case 9:
                    case 10:
                        if (login.getPlatform_id() == 30) {
                            keyValue = 22;
                            break;
                        } else {
                            keyValue = 3;
                            break;
                        }
                }
                v.setBackgroundResource(R.drawable.remote_btn_orientation);
                if (!((OrientationButton) v).isLongPressed()) {
                    GsRemoteControlActivity.this.sendKeyValue(keyValue);
                }
                GsRemoteControlActivity.this.remoteViewPager.setScrollable(true);
            }
        });
        this.orientationButton.setOnActionMoveListener(new IrregularButton.OnActionMoveListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.12
            AnonymousClass12() {
            }

            @Override // mktvsmart.screen.view.IrregularButton.OnActionMoveListener
            public void onActionMove(View v, int orientation) {
                int drawableId = R.drawable.remote_btn_orientation;
                boolean flag = true;
                switch (orientation) {
                    case -1:
                        flag = false;
                        break;
                }
                if (((OrientationButton) v).isLongPressed()) {
                    switch (orientation) {
                        case 1:
                            drawableId = R.drawable.remote_btn_orientation_long_press_1;
                            break;
                        case 2:
                            drawableId = R.drawable.remote_btn_orientation_long_press_2;
                            break;
                        case 3:
                            drawableId = R.drawable.remote_btn_orientation_long_press_3;
                            break;
                        case 4:
                            drawableId = R.drawable.remote_btn_orientation_long_press_4;
                            break;
                        case 5:
                            drawableId = R.drawable.remote_btn_orientation_long_press_5;
                            break;
                        case 6:
                            drawableId = R.drawable.remote_btn_orientation_long_press_6;
                            break;
                        case 7:
                            drawableId = R.drawable.remote_btn_orientation_long_press_7;
                            break;
                        case 8:
                            drawableId = R.drawable.remote_btn_orientation_long_press_8;
                            break;
                        case 9:
                            drawableId = R.drawable.remote_btn_orientation_long_press_9;
                            break;
                        case 10:
                            drawableId = R.drawable.remote_btn_orientation_long_press_10;
                            break;
                        case 11:
                            drawableId = R.drawable.remote_btn_orientation_long_press_11;
                            break;
                        case 12:
                            drawableId = R.drawable.remote_btn_orientation_long_press_12;
                            break;
                    }
                } else {
                    flag = false;
                    switch (orientation) {
                        case 1:
                        case 11:
                        case 12:
                            drawableId = R.drawable.remote_btn_orientation_up_selected;
                            break;
                        case 2:
                        case 3:
                        case 4:
                            drawableId = R.drawable.remote_btn_orientation_right_selected;
                            break;
                        case 5:
                        case 6:
                        case 7:
                            drawableId = R.drawable.remote_btn_orientation_down_selected;
                            break;
                        case 8:
                        case 9:
                        case 10:
                            drawableId = R.drawable.remote_btn_orientation_left_selected;
                            break;
                    }
                }
                v.setBackgroundResource(drawableId);
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (flag) {
                    int keyValue = 0;
                    switch (((OrientationButton) v).rotateOrientation()) {
                        case 13:
                            if (login.getPlatform_id() == 30) {
                                keyValue = 56;
                                break;
                            } else {
                                keyValue = 35;
                                break;
                            }
                        case 14:
                            if (login.getPlatform_id() == 30) {
                                keyValue = 57;
                                break;
                            } else {
                                keyValue = 36;
                                break;
                            }
                    }
                    GsRemoteControlActivity.this.sendKeyValue(keyValue);
                }
            }
        });
        this.orientationButton.setOnLongPressListener(new OrientationButton.OnLongPressListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.13
            AnonymousClass13() {
            }

            @Override // mktvsmart.screen.view.OrientationButton.OnLongPressListener
            public void onLongPress(View v, int orientation) {
                GsRemoteControlActivity.this.remoteViewPager.setScrollable(false);
                int drawableId = R.drawable.remote_btn_orientation;
                switch (orientation) {
                    case 1:
                        drawableId = R.drawable.remote_btn_orientation_long_press_1;
                        break;
                    case 2:
                        drawableId = R.drawable.remote_btn_orientation_long_press_2;
                        break;
                    case 3:
                        drawableId = R.drawable.remote_btn_orientation_long_press_3;
                        break;
                    case 4:
                        drawableId = R.drawable.remote_btn_orientation_long_press_4;
                        break;
                    case 5:
                        drawableId = R.drawable.remote_btn_orientation_long_press_5;
                        break;
                    case 6:
                        drawableId = R.drawable.remote_btn_orientation_long_press_6;
                        break;
                    case 7:
                        drawableId = R.drawable.remote_btn_orientation_long_press_7;
                        break;
                    case 8:
                        drawableId = R.drawable.remote_btn_orientation_long_press_8;
                        break;
                    case 9:
                        drawableId = R.drawable.remote_btn_orientation_long_press_9;
                        break;
                    case 10:
                        drawableId = R.drawable.remote_btn_orientation_long_press_10;
                        break;
                    case 11:
                        drawableId = R.drawable.remote_btn_orientation_long_press_11;
                        break;
                    case 12:
                        drawableId = R.drawable.remote_btn_orientation_long_press_12;
                        break;
                }
                v.setBackgroundResource(drawableId);
            }
        });
        this.touchPad.setGestureLibrary(getGestureLibrary());
        this.touchPad.setOnPressDownListener(new RemoteControlTouchPad.OnPressDownListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.14
            AnonymousClass14() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnPressDownListener
            public void onPressDown(View v) {
                GsRemoteControlActivity.this.remoteViewPager.setScrollable(false);
            }
        });
        this.touchPad.setOnPressUpListener(new RemoteControlTouchPad.OnPressUpListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.15
            AnonymousClass15() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnPressUpListener
            public void onPressUp(View v) {
                GsRemoteControlActivity.this.remoteViewPager.setScrollable(true);
                if (GsRemoteControlActivity.this.scrollTimer != null) {
                    GsRemoteControlActivity.this.scrollTimer.cancel();
                    GsRemoteControlActivity.this.scrollTimer = null;
                }
            }
        });
        this.touchPad.setOnDoublePressListener(new RemoteControlTouchPad.OnDoublePressListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.16
            AnonymousClass16() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnDoublePressListener
            public void onDoublePress(View v) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(27);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(5);
                }
            }
        });
        this.touchPad.setOnFlingDownListener(new RemoteControlTouchPad.OnFlingDownListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.17
            AnonymousClass17() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingDownListener
            public void onFlingDown(View v) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(25);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(2);
                }
            }
        });
        this.touchPad.setOnFlingLeftListener(new RemoteControlTouchPad.OnFlingLeftListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.18
            AnonymousClass18() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingLeftListener
            public void onFlingLeft(View v) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(22);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(3);
                }
            }
        });
        this.touchPad.setOnFlingRightListener(new RemoteControlTouchPad.OnFlingRightListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.19
            AnonymousClass19() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingRightListener
            public void onFlingRight(View v) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(23);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(4);
                }
            }
        });
        this.touchPad.setOnFlingUpListener(new RemoteControlTouchPad.OnFlingUpListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.20
            AnonymousClass20() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingUpListener
            public void onFlingUp(View v) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(24);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(1);
                }
            }
        });
        this.touchPad.setOnGestureMenuListener(new RemoteControlTouchPad.OnGestureMenuListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.21
            AnonymousClass21() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnGestureMenuListener
            public void onGestureMenu(View v) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(20);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(6);
                }
            }
        });
        this.touchPad.setOnGestureExitListener(new RemoteControlTouchPad.OnGestureExitListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.22
            AnonymousClass22() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnGestureExitListener
            public void onGestureExit(View v) {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(21);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(7);
                }
            }
        });
        this.touchPad.setOnScrollUpListener(new RemoteControlTouchPad.OnScrollUpListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.23
            AnonymousClass23() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollUpListener
            public void onScrollUp(View v) {
                GsRemoteControlActivity.this.scrollTimer = new Timer();
                GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.23.1
                    AnonymousClass1() {
                    }

                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                        if (login.getPlatform_id() == 30) {
                            GsRemoteControlActivity.this.sendKeyValue(24);
                        } else {
                            GsRemoteControlActivity.this.sendKeyValue(1);
                        }
                    }
                }, 0L, 100L);
            }

            /* renamed from: mktvsmart.screen.GsRemoteControlActivity$23$1 */
            class AnonymousClass1 extends TimerTask {
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(24);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(1);
                    }
                }
            }
        });
        this.touchPad.setOnScrollRightListener(new RemoteControlTouchPad.OnScrollRightListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.24
            AnonymousClass24() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollRightListener
            public void onScrollRight(View v) {
                GsRemoteControlActivity.this.scrollTimer = new Timer();
                GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.24.1
                    AnonymousClass1() {
                    }

                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                        if (login.getPlatform_id() == 30) {
                            GsRemoteControlActivity.this.sendKeyValue(23);
                        } else {
                            GsRemoteControlActivity.this.sendKeyValue(4);
                        }
                    }
                }, 0L, 100L);
            }

            /* renamed from: mktvsmart.screen.GsRemoteControlActivity$24$1 */
            class AnonymousClass1 extends TimerTask {
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(23);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(4);
                    }
                }
            }
        });
        this.touchPad.setOnScrollDownListener(new RemoteControlTouchPad.OnScrollDownListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.25
            AnonymousClass25() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollDownListener
            public void onScrollDown(View v) {
                GsRemoteControlActivity.this.scrollTimer = new Timer();
                GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.25.1
                    AnonymousClass1() {
                    }

                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                        if (login.getPlatform_id() == 30) {
                            GsRemoteControlActivity.this.sendKeyValue(25);
                        } else {
                            GsRemoteControlActivity.this.sendKeyValue(2);
                        }
                    }
                }, 0L, 100L);
            }

            /* renamed from: mktvsmart.screen.GsRemoteControlActivity$25$1 */
            class AnonymousClass1 extends TimerTask {
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(25);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(2);
                    }
                }
            }
        });
        this.touchPad.setOnScrollLeftListener(new RemoteControlTouchPad.OnScrollLeftListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.26
            AnonymousClass26() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollLeftListener
            public void onScrollLeft(View v) {
                GsRemoteControlActivity.this.scrollTimer = new Timer();
                GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.26.1
                    AnonymousClass1() {
                    }

                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                        if (login.getPlatform_id() == 30) {
                            GsRemoteControlActivity.this.sendKeyValue(22);
                        } else {
                            GsRemoteControlActivity.this.sendKeyValue(3);
                        }
                    }
                }, 0L, 100L);
            }

            /* renamed from: mktvsmart.screen.GsRemoteControlActivity$26$1 */
            class AnonymousClass1 extends TimerTask {
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(22);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(3);
                    }
                }
            }
        });
        this.touchPad.setOnScrollCancelListener(new RemoteControlTouchPad.OnScrollCancelListener() { // from class: mktvsmart.screen.GsRemoteControlActivity.27
            AnonymousClass27() {
            }

            @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollCancelListener
            public void onScrollCancel(View v) {
                if (GsRemoteControlActivity.this.scrollTimer != null) {
                    GsRemoteControlActivity.this.scrollTimer.cancel();
                    GsRemoteControlActivity.this.scrollTimer = null;
                }
            }
        });
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$8 */
    class AnonymousClass8 implements View.OnTouchListener {
        AnonymousClass8() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case 0:
                    if (GsRemoteControlActivity.isMuted) {
                        v.setBackgroundResource(R.drawable.remote_btn_muted_selected);
                        break;
                    } else {
                        v.setBackgroundResource(R.drawable.remote_btn_no_mute_selected);
                        break;
                    }
                case 1:
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(12);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(23);
                    }
                    if (GsRemoteControlActivity.isMuted) {
                        v.setBackgroundResource(R.drawable.remote_btn_muted);
                        break;
                    } else {
                        v.setBackgroundResource(R.drawable.remote_btn_no_mute);
                        break;
                    }
            }
            return true;
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$9 */
    class AnonymousClass9 implements IrregularButton.OnActionUpListener {
        AnonymousClass9() {
        }

        @Override // mktvsmart.screen.view.IrregularButton.OnActionUpListener
        public void onAtionUp(View v, int orientation) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(27);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(5);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$10 */
    class AnonymousClass10 implements IrregularButton.OnActionDownListener {
        AnonymousClass10() {
        }

        @Override // mktvsmart.screen.view.IrregularButton.OnActionDownListener
        public void onActionDown(View v, int orientation) {
            int drawableId = R.drawable.remote_btn_orientation;
            switch (orientation) {
                case 1:
                case 11:
                case 12:
                    drawableId = R.drawable.remote_btn_orientation_up_selected;
                    break;
                case 2:
                case 3:
                case 4:
                    drawableId = R.drawable.remote_btn_orientation_right_selected;
                    break;
                case 5:
                case 6:
                case 7:
                    drawableId = R.drawable.remote_btn_orientation_down_selected;
                    break;
                case 8:
                case 9:
                case 10:
                    drawableId = R.drawable.remote_btn_orientation_left_selected;
                    break;
            }
            v.setBackgroundResource(drawableId);
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$11 */
    class AnonymousClass11 implements IrregularButton.OnActionUpListener {
        AnonymousClass11() {
        }

        @Override // mktvsmart.screen.view.IrregularButton.OnActionUpListener
        public void onAtionUp(View v, int orientation) {
            int keyValue = 0;
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            switch (orientation) {
                case 1:
                case 11:
                case 12:
                    if (login.getPlatform_id() == 30) {
                        keyValue = 24;
                        break;
                    } else {
                        keyValue = 1;
                        break;
                    }
                case 2:
                case 3:
                case 4:
                    if (login.getPlatform_id() == 30) {
                        keyValue = 23;
                        break;
                    } else {
                        keyValue = 4;
                        break;
                    }
                case 5:
                case 6:
                case 7:
                    if (login.getPlatform_id() == 30) {
                        keyValue = 25;
                        break;
                    } else {
                        keyValue = 2;
                        break;
                    }
                case 8:
                case 9:
                case 10:
                    if (login.getPlatform_id() == 30) {
                        keyValue = 22;
                        break;
                    } else {
                        keyValue = 3;
                        break;
                    }
            }
            v.setBackgroundResource(R.drawable.remote_btn_orientation);
            if (!((OrientationButton) v).isLongPressed()) {
                GsRemoteControlActivity.this.sendKeyValue(keyValue);
            }
            GsRemoteControlActivity.this.remoteViewPager.setScrollable(true);
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$12 */
    class AnonymousClass12 implements IrregularButton.OnActionMoveListener {
        AnonymousClass12() {
        }

        @Override // mktvsmart.screen.view.IrregularButton.OnActionMoveListener
        public void onActionMove(View v, int orientation) {
            int drawableId = R.drawable.remote_btn_orientation;
            boolean flag = true;
            switch (orientation) {
                case -1:
                    flag = false;
                    break;
            }
            if (((OrientationButton) v).isLongPressed()) {
                switch (orientation) {
                    case 1:
                        drawableId = R.drawable.remote_btn_orientation_long_press_1;
                        break;
                    case 2:
                        drawableId = R.drawable.remote_btn_orientation_long_press_2;
                        break;
                    case 3:
                        drawableId = R.drawable.remote_btn_orientation_long_press_3;
                        break;
                    case 4:
                        drawableId = R.drawable.remote_btn_orientation_long_press_4;
                        break;
                    case 5:
                        drawableId = R.drawable.remote_btn_orientation_long_press_5;
                        break;
                    case 6:
                        drawableId = R.drawable.remote_btn_orientation_long_press_6;
                        break;
                    case 7:
                        drawableId = R.drawable.remote_btn_orientation_long_press_7;
                        break;
                    case 8:
                        drawableId = R.drawable.remote_btn_orientation_long_press_8;
                        break;
                    case 9:
                        drawableId = R.drawable.remote_btn_orientation_long_press_9;
                        break;
                    case 10:
                        drawableId = R.drawable.remote_btn_orientation_long_press_10;
                        break;
                    case 11:
                        drawableId = R.drawable.remote_btn_orientation_long_press_11;
                        break;
                    case 12:
                        drawableId = R.drawable.remote_btn_orientation_long_press_12;
                        break;
                }
            } else {
                flag = false;
                switch (orientation) {
                    case 1:
                    case 11:
                    case 12:
                        drawableId = R.drawable.remote_btn_orientation_up_selected;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        drawableId = R.drawable.remote_btn_orientation_right_selected;
                        break;
                    case 5:
                    case 6:
                    case 7:
                        drawableId = R.drawable.remote_btn_orientation_down_selected;
                        break;
                    case 8:
                    case 9:
                    case 10:
                        drawableId = R.drawable.remote_btn_orientation_left_selected;
                        break;
                }
            }
            v.setBackgroundResource(drawableId);
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (flag) {
                int keyValue = 0;
                switch (((OrientationButton) v).rotateOrientation()) {
                    case 13:
                        if (login.getPlatform_id() == 30) {
                            keyValue = 56;
                            break;
                        } else {
                            keyValue = 35;
                            break;
                        }
                    case 14:
                        if (login.getPlatform_id() == 30) {
                            keyValue = 57;
                            break;
                        } else {
                            keyValue = 36;
                            break;
                        }
                }
                GsRemoteControlActivity.this.sendKeyValue(keyValue);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$13 */
    class AnonymousClass13 implements OrientationButton.OnLongPressListener {
        AnonymousClass13() {
        }

        @Override // mktvsmart.screen.view.OrientationButton.OnLongPressListener
        public void onLongPress(View v, int orientation) {
            GsRemoteControlActivity.this.remoteViewPager.setScrollable(false);
            int drawableId = R.drawable.remote_btn_orientation;
            switch (orientation) {
                case 1:
                    drawableId = R.drawable.remote_btn_orientation_long_press_1;
                    break;
                case 2:
                    drawableId = R.drawable.remote_btn_orientation_long_press_2;
                    break;
                case 3:
                    drawableId = R.drawable.remote_btn_orientation_long_press_3;
                    break;
                case 4:
                    drawableId = R.drawable.remote_btn_orientation_long_press_4;
                    break;
                case 5:
                    drawableId = R.drawable.remote_btn_orientation_long_press_5;
                    break;
                case 6:
                    drawableId = R.drawable.remote_btn_orientation_long_press_6;
                    break;
                case 7:
                    drawableId = R.drawable.remote_btn_orientation_long_press_7;
                    break;
                case 8:
                    drawableId = R.drawable.remote_btn_orientation_long_press_8;
                    break;
                case 9:
                    drawableId = R.drawable.remote_btn_orientation_long_press_9;
                    break;
                case 10:
                    drawableId = R.drawable.remote_btn_orientation_long_press_10;
                    break;
                case 11:
                    drawableId = R.drawable.remote_btn_orientation_long_press_11;
                    break;
                case 12:
                    drawableId = R.drawable.remote_btn_orientation_long_press_12;
                    break;
            }
            v.setBackgroundResource(drawableId);
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$14 */
    class AnonymousClass14 implements RemoteControlTouchPad.OnPressDownListener {
        AnonymousClass14() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnPressDownListener
        public void onPressDown(View v) {
            GsRemoteControlActivity.this.remoteViewPager.setScrollable(false);
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$15 */
    class AnonymousClass15 implements RemoteControlTouchPad.OnPressUpListener {
        AnonymousClass15() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnPressUpListener
        public void onPressUp(View v) {
            GsRemoteControlActivity.this.remoteViewPager.setScrollable(true);
            if (GsRemoteControlActivity.this.scrollTimer != null) {
                GsRemoteControlActivity.this.scrollTimer.cancel();
                GsRemoteControlActivity.this.scrollTimer = null;
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$16 */
    class AnonymousClass16 implements RemoteControlTouchPad.OnDoublePressListener {
        AnonymousClass16() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnDoublePressListener
        public void onDoublePress(View v) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(27);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(5);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$17 */
    class AnonymousClass17 implements RemoteControlTouchPad.OnFlingDownListener {
        AnonymousClass17() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingDownListener
        public void onFlingDown(View v) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(25);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(2);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$18 */
    class AnonymousClass18 implements RemoteControlTouchPad.OnFlingLeftListener {
        AnonymousClass18() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingLeftListener
        public void onFlingLeft(View v) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(22);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(3);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$19 */
    class AnonymousClass19 implements RemoteControlTouchPad.OnFlingRightListener {
        AnonymousClass19() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingRightListener
        public void onFlingRight(View v) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(23);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(4);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$20 */
    class AnonymousClass20 implements RemoteControlTouchPad.OnFlingUpListener {
        AnonymousClass20() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnFlingUpListener
        public void onFlingUp(View v) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(24);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(1);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$21 */
    class AnonymousClass21 implements RemoteControlTouchPad.OnGestureMenuListener {
        AnonymousClass21() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnGestureMenuListener
        public void onGestureMenu(View v) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(20);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(6);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$22 */
    class AnonymousClass22 implements RemoteControlTouchPad.OnGestureExitListener {
        AnonymousClass22() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnGestureExitListener
        public void onGestureExit(View v) {
            GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
            if (login.getPlatform_id() == 30) {
                GsRemoteControlActivity.this.sendKeyValue(21);
            } else {
                GsRemoteControlActivity.this.sendKeyValue(7);
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$23 */
    class AnonymousClass23 implements RemoteControlTouchPad.OnScrollUpListener {
        AnonymousClass23() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollUpListener
        public void onScrollUp(View v) {
            GsRemoteControlActivity.this.scrollTimer = new Timer();
            GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.23.1
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(24);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(1);
                    }
                }
            }, 0L, 100L);
        }

        /* renamed from: mktvsmart.screen.GsRemoteControlActivity$23$1 */
        class AnonymousClass1 extends TimerTask {
            AnonymousClass1() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(24);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(1);
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$24 */
    class AnonymousClass24 implements RemoteControlTouchPad.OnScrollRightListener {
        AnonymousClass24() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollRightListener
        public void onScrollRight(View v) {
            GsRemoteControlActivity.this.scrollTimer = new Timer();
            GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.24.1
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(23);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(4);
                    }
                }
            }, 0L, 100L);
        }

        /* renamed from: mktvsmart.screen.GsRemoteControlActivity$24$1 */
        class AnonymousClass1 extends TimerTask {
            AnonymousClass1() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(23);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(4);
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$25 */
    class AnonymousClass25 implements RemoteControlTouchPad.OnScrollDownListener {
        AnonymousClass25() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollDownListener
        public void onScrollDown(View v) {
            GsRemoteControlActivity.this.scrollTimer = new Timer();
            GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.25.1
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(25);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(2);
                    }
                }
            }, 0L, 100L);
        }

        /* renamed from: mktvsmart.screen.GsRemoteControlActivity$25$1 */
        class AnonymousClass1 extends TimerTask {
            AnonymousClass1() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(25);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(2);
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$26 */
    class AnonymousClass26 implements RemoteControlTouchPad.OnScrollLeftListener {
        AnonymousClass26() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollLeftListener
        public void onScrollLeft(View v) {
            GsRemoteControlActivity.this.scrollTimer = new Timer();
            GsRemoteControlActivity.this.scrollTimer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsRemoteControlActivity.26.1
                AnonymousClass1() {
                }

                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                    if (login.getPlatform_id() == 30) {
                        GsRemoteControlActivity.this.sendKeyValue(22);
                    } else {
                        GsRemoteControlActivity.this.sendKeyValue(3);
                    }
                }
            }, 0L, 100L);
        }

        /* renamed from: mktvsmart.screen.GsRemoteControlActivity$26$1 */
        class AnonymousClass1 extends TimerTask {
            AnonymousClass1() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                GsMobileLoginInfo login = GMScreenGlobalInfo.getCurStbInfo();
                if (login.getPlatform_id() == 30) {
                    GsRemoteControlActivity.this.sendKeyValue(22);
                } else {
                    GsRemoteControlActivity.this.sendKeyValue(3);
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.GsRemoteControlActivity$27 */
    class AnonymousClass27 implements RemoteControlTouchPad.OnScrollCancelListener {
        AnonymousClass27() {
        }

        @Override // mktvsmart.screen.view.RemoteControlTouchPad.OnScrollCancelListener
        public void onScrollCancel(View v) {
            if (GsRemoteControlActivity.this.scrollTimer != null) {
                GsRemoteControlActivity.this.scrollTimer.cancel();
                GsRemoteControlActivity.this.scrollTimer = null;
            }
        }
    }

    public void sendKeyValue(int keyValue) {
        if (GMScreenGlobalInfo.getCurStbPlatform() == 32 || GMScreenGlobalInfo.getCurStbPlatform() == 71 || GMScreenGlobalInfo.getCurStbPlatform() == 74 || GMScreenGlobalInfo.getCurStbPlatform() == 72) {
            RemoteVibrate.Vibrate(this, 100L);
        }
        DataParser parser = ParserFactory.getParser();
        List<DataConvertRcuModel> rcuModels = new ArrayList<>();
        DataConvertRcuModel rcuModel = new DataConvertRcuModel();
        if (keyValue > 0) {
            rcuModel.setKeyValue(keyValue);
            rcuModels.add(rcuModel);
            try {
                byte[] byteArrays = parser.serialize(rcuModels, GlobalConstantValue.GMS_MSG_DO_REMOTE_CONTROL).getBytes();
                GsSendSocket.sendSocketToStb(byteArrays, this.tcpSocket, 0, byteArrays.length, GlobalConstantValue.GMS_MSG_DO_REMOTE_CONTROL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private GestureLibrary getGestureLibrary() {
        File gestureFile = new File(Environment.getExternalStorageDirectory() + getResources().getString(R.string.app_name), "gestures");
        return gestureFile.exists() ? GestureLibraries.fromFile(gestureFile) : GestureLibraries.fromRawResource(this, R.raw.gestures);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int keyValue;
        if (this.keyMapInPage1.get(Integer.valueOf(v.getId())) != null) {
            keyValue = this.keyMapInPage1.get(Integer.valueOf(v.getId())).intValue();
        } else {
            keyValue = this.keyMapInPage2.get(Integer.valueOf(v.getId())).intValue();
        }
        sendKeyValue(keyValue);
    }

    private class RemotePagerAdapter extends PagerAdapter {
        private RemotePagerAdapter() {
        }

        /* synthetic */ RemotePagerAdapter(GsRemoteControlActivity gsRemoteControlActivity, RemotePagerAdapter remotePagerAdapter) {
            this();
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return GsRemoteControlActivity.this.remoteViews.size();
        }

        @Override // android.support.v4.view.PagerAdapter
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override // android.support.v4.view.PagerAdapter
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView((View) GsRemoteControlActivity.this.remoteViews.get(position));
            return GsRemoteControlActivity.this.remoteViews.get(position);
        }

        @Override // android.support.v4.view.PagerAdapter
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) GsRemoteControlActivity.this.remoteViews.get(position));
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("GsRemoteControlActivity", "onConfigurationChanged");
        if (this.remoteViewPager.getCurrentItem() == 3) {
            ((MirrorPageView) this.remotePage3).initMirrorLayout();
        }
    }
}
