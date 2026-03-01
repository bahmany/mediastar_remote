package com.hisilicon.multiscreen.protocol.message;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class Action {
    public static final int ACTION_ID_ACCESS_CONTROLLER_ACCESS_NOTIFY = 4608;
    public static final int ACTION_ID_ACCESS_CONTROLLER_BYE_FOR_REAVE = 4624;
    public static final int ACTION_ID_ACCESS_CONTROLLER_LEAVE_NOTIFY_BY_PHONE = 4640;
    public static final int ACTION_ID_ACCESS_CONTROLLER_LEAVE_NOTIFY_BY_STB = 4656;
    public static final int ACTION_ID_ACCESS_CONTROLLER_RET_ACCESS_NOTIFY = 4609;
    public static final int ACTION_ID_ACCESS_CONTROLLER_RET_BYE_FOR_REAVE = 4625;
    public static final int ACTION_ID_ACCESS_CONTROLLER_RET_LEAVE_NOTIFY_BY_PHONE = 4641;
    public static final int ACTION_ID_ACCESS_CONTROLLER_RET_LEAVE_NOTIFY_BY_STB = 4657;
    public static final int ACTION_ID_AUDIO_DATA_SEND = 16433;
    public static final int ACTION_ID_NO_RESPONSE = 0;
    public static final int ACTION_ID_REMOTE_APP_LAUNCH = 12305;
    public static final int ACTION_ID_REMOTE_APP_RET_LAUNCH = 12306;
    public static final int ACTION_ID_REMOTE_APP_RET_UPDATE_LIST = 12290;
    public static final int ACTION_ID_REMOTE_APP_UPDATE_LIST = 12289;
    public static final int ACTION_ID_RET_AUDIO_DATA_SEND = 16434;
    public static final int ACTION_ID_RET_START_SPEAKING = 16402;
    public static final int ACTION_ID_RET_STOP_SPEAKING = 16418;
    public static final int ACTION_ID_SPEECH_ERROR = 16449;
    public static final int ACTION_ID_SPEECH_ERROR_RET_SEND = 16450;
    public static final int ACTION_ID_SPEECH_TEXT_RET_SEND = 16386;
    public static final int ACTION_ID_SPEECH_TEXT_SEND = 16385;
    public static final int ACTION_ID_START_SPEAKING = 16401;
    public static final int ACTION_ID_STOP_SPEAKING = 16417;
    public static final int ACTION_ID_VIME_CALL_INPUT = 20481;
    public static final int ACTION_ID_VIME_CHECK = 4385;
    public static final int ACTION_ID_VIME_DISABLE = 4369;
    public static final int ACTION_ID_VIME_ENALBE = 4353;
    public static final int ACTION_ID_VIME_ENDINPUT_FROM_PHONE = 20513;
    public static final int ACTION_ID_VIME_ENDINPUT_FROM_STB = 20529;
    public static final int ACTION_ID_VIME_FAIL_FLAG = 0;
    public static final int ACTION_ID_VIME_RET_CALL_INPUT = 20482;
    public static final int ACTION_ID_VIME_RET_CHECK = 4386;
    public static final int ACTION_ID_VIME_RET_DISABLE = 4370;
    public static final int ACTION_ID_VIME_RET_ENABLE = 4354;
    public static final int ACTION_ID_VIME_RET_ENDINPUT_FROM_PHONE = 20514;
    public static final int ACTION_ID_VIME_RET_ENDINPUT_FROM_STB = 20530;
    public static final int ACTION_ID_VIME_RET_SEND_TEXT = 20498;
    public static final int ACTION_ID_VIME_SEND_TEXT = 20497;
    public static final int ACTION_ID_VIME_SUCCESS_FLAG = 1;
    public static final int ACTION_ID_XML_PARSE_ERROR_RESPONSE = 1;
    protected ArrayList<Argument> mArgumentList;
    protected int mId;
    protected String mName;
    protected String mResponseFlag;
    protected int mResponseId;

    public Action() {
        this.mArgumentList = new ArrayList<>();
        this.mId = 0;
        this.mName = null;
        this.mResponseId = 0;
        this.mResponseFlag = null;
    }

    public Action(int id, String name) {
        this.mArgumentList = new ArrayList<>();
        this.mId = 0;
        this.mName = null;
        this.mResponseId = 0;
        this.mResponseFlag = null;
        this.mId = id;
        this.mName = name;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getResponseId() {
        return this.mResponseId;
    }

    public void setResponseId(int responseId) {
        this.mResponseId = responseId;
    }

    public String getResponseFlag() {
        return this.mResponseFlag;
    }

    public void setResponseFlag(String responseFlag) {
        this.mResponseFlag = responseFlag;
    }

    public void addArgument(Argument argument) {
        this.mArgumentList.add(argument);
    }

    public Argument getArgument(int index) {
        if (index >= this.mArgumentList.size()) {
            return null;
        }
        return this.mArgumentList.get(index);
    }

    public ArrayList<Argument> getArgumentList() {
        return this.mArgumentList;
    }
}
