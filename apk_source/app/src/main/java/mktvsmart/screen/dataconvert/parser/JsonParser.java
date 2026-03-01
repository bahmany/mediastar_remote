package mktvsmart.screen.dataconvert.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsEPGEvent;
import mktvsmart.screen.GsEPGTableChannel;
import mktvsmart.screen.common.tools.CommonHelper;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertChannelTypeModel;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.dataconvert.model.DataConvertControlModel;
import mktvsmart.screen.dataconvert.model.DataConvertDebugModel;
import mktvsmart.screen.dataconvert.model.DataConvertEditChannelLockModel;
import mktvsmart.screen.dataconvert.model.DataConvertFavChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertFavorModel;
import mktvsmart.screen.dataconvert.model.DataConvertGChatChannelInfoModel;
import mktvsmart.screen.dataconvert.model.DataConvertInputMethodModel;
import mktvsmart.screen.dataconvert.model.DataConvertOneDataModel;
import mktvsmart.screen.dataconvert.model.DataConvertPvrInfoModel;
import mktvsmart.screen.dataconvert.model.DataConvertRcuModel;
import mktvsmart.screen.dataconvert.model.DataConvertSatModel;
import mktvsmart.screen.dataconvert.model.DataConvertSortModel;
import mktvsmart.screen.dataconvert.model.DataConvertStbInfoModel;
import mktvsmart.screen.dataconvert.model.DataConvertTimeModel;
import mktvsmart.screen.dataconvert.model.DataConvertTpModel;
import mktvsmart.screen.dataconvert.model.DataConvertUpdateModel;
import mktvsmart.screen.dataconvert.model.DataConvertUsernameModel;
import mktvsmart.screen.gchat.bean.GsChatRoomInfo;
import mktvsmart.screen.gchat.bean.GsChatSetting;
import mktvsmart.screen.gchat.bean.GsChatUser;
import mktvsmart.screen.spectrum.bean.DataConvertSpectrumInfo;
import mktvsmart.screen.spectrum.bean.DataConvertSpectrumSetting;
import mktvsmart.screen.util.TypeConvertUtils;
import mktvsmart.screen.vlc.TranscodeConstants;

/* loaded from: classes.dex */
public class JsonParser implements DataParser {
    private static final String ARRAY = "array";
    private static final String TAG = "DataParser";

    @Override // mktvsmart.screen.dataconvert.parser.DataParser
    public List<?> parse(InputStream is, int type) throws Exception {
        int tpIndex;
        int satIndex;
        int symRate;
        int polInt;
        int fec;
        int freq;
        int SatNo;
        String[] list;
        String s = CommonHelper.getStrFromInputSteam(is);
        JSONArray jsonArray = JSON.parseArray(s);
        switch (type) {
            case 0:
                List<DataConvertChannelModel> models = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    DataConvertChannelModel channelModel = new DataConvertChannelModel();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.containsKey("SatIndexSelected")) {
                        GMScreenGlobalInfo.setmSatIndexSelected(jsonObject.getIntValue("SatIndexSelected"));
                    }
                    if (jsonObject.containsKey("ServiceID")) {
                        channelModel.SetProgramId(jsonObject.getString("ServiceID"));
                        channelModel.setProgramName(jsonObject.getString("ServiceName"));
                        channelModel.SetProgramIndex(jsonObject.getIntValue("ServiceIndex"));
                        if (jsonObject.containsKey("Scramble")) {
                            channelModel.SetIsProgramScramble(jsonObject.getIntValue("Scramble"));
                        }
                        channelModel.setIsProgramHd(jsonObject.getIntValue("HD"));
                        channelModel.SetFavMark(jsonObject.getIntValue("FavBit"));
                        channelModel.setLockMark(jsonObject.getIntValue("Lock"));
                        channelModel.SetHaveEPG(jsonObject.getIntValue("EPG"));
                        channelModel.setIsPlaying(jsonObject.getIntValue("Playing"));
                        channelModel.setChannelTpye(jsonObject.getIntValue("Radio"));
                        channelModel.setVideoPid(jsonObject.getIntValue("VideoPID"));
                        String audioPid = "";
                        JSONArray audioArray = jsonObject.getJSONArray("AudioArray");
                        for (int j = 0; j < audioArray.size(); j++) {
                            if (j > 0) {
                                audioPid = String.valueOf(audioPid) + ClientInfo.SEPARATOR_BETWEEN_VARS + audioArray.getJSONObject(j).getIntValue("PID");
                            } else {
                                audioPid = String.valueOf(audioPid) + audioArray.getJSONObject(j).getIntValue("PID");
                            }
                        }
                        channelModel.setAudioPid(audioPid);
                        channelModel.setTtxPid(jsonObject.getIntValue("TTXPID"));
                        String subtPid = "";
                        JSONArray subtArray = jsonObject.getJSONArray("SubtArray");
                        for (int j2 = 0; j2 < subtArray.size(); j2++) {
                            if (j2 > 0) {
                                subtPid = String.valueOf(subtPid) + ClientInfo.SEPARATOR_BETWEEN_VARS + subtArray.getJSONObject(j2).getIntValue("PID");
                            } else {
                                subtPid = String.valueOf(subtPid) + subtArray.getJSONObject(j2).getIntValue("PID");
                            }
                        }
                        if (subtPid.equals("")) {
                            subtPid = null;
                        }
                        channelModel.setSubPid(subtPid);
                        channelModel.setPmtPid(jsonObject.getIntValue("PMTPID"));
                    } else {
                        channelModel.SetProgramId(jsonObject.getString("ProgramId"));
                        channelModel.setProgramName(jsonObject.getString("ProgramName"));
                        channelModel.SetProgramIndex(jsonObject.getIntValue("ProgramIndex"));
                        if (jsonObject.containsKey("ProgramType")) {
                            channelModel.SetIsProgramScramble(jsonObject.getIntValue("ProgramType"));
                        }
                        channelModel.setIsProgramHd(jsonObject.getIntValue("IsProgramHD"));
                        channelModel.SetFavMark(jsonObject.getIntValue("FavMark"));
                        channelModel.setLockMark(jsonObject.getIntValue("LockMark"));
                        channelModel.SetHaveEPG(jsonObject.getIntValue("HaveEPG"));
                        channelModel.setIsPlaying(jsonObject.getIntValue("IsPlaying"));
                        channelModel.setChannelTpye(jsonObject.getIntValue("ChannelType"));
                        channelModel.setVideoPid(jsonObject.getIntValue("VideoPid"));
                        channelModel.setAudioPid(jsonObject.getString("AudioPid"));
                        channelModel.setTtxPid(jsonObject.getIntValue("TtxPid"));
                        String SubPid = jsonObject.getString("SubPid");
                        if (SubPid.equals("")) {
                            SubPid = null;
                        }
                        channelModel.setSubPid(SubPid);
                        channelModel.setPmtPid(jsonObject.getIntValue("PmtPid"));
                    }
                    if (jsonObject.containsKey("SatName")) {
                        channelModel.SetSatName(jsonObject.getString("SatName"));
                    }
                    channelModel.setmWillBePlayed(jsonObject.getIntValue("WillBePlayed"));
                    if (jsonObject.containsKey("Frequency")) {
                        channelModel.setFreq(jsonObject.getIntValue("Frequency"));
                    }
                    if (jsonObject.containsKey("Polar")) {
                        String str = jsonObject.getString("Polar");
                        char pol = 'h';
                        if (str.equals("0")) {
                            pol = 'h';
                        } else if (str.equals("1")) {
                            pol = 'v';
                        } else if (str.equals(ContentTree.AUDIO_ID)) {
                            pol = 'l';
                        } else if (str.equals(ContentTree.IMAGE_ID)) {
                            pol = 'r';
                        }
                        channelModel.setPol(pol);
                    }
                    channelModel.setModulationSystem(jsonObject.getIntValue("ModulationSystem"));
                    channelModel.setModulationType(jsonObject.getIntValue("ModulationType"));
                    channelModel.setRollOff(jsonObject.getIntValue("RollOff"));
                    channelModel.setPilotTones(jsonObject.getIntValue("PilotTones"));
                    if (jsonObject.containsKey("SymbolRate")) {
                        channelModel.setSymRate(jsonObject.getIntValue("SymbolRate"));
                    }
                    if (jsonObject.containsKey("Fec")) {
                        channelModel.setFec(jsonObject.getIntValue("Fec"));
                    }
                    channelModel.setIsTuner2((short) jsonObject.getIntValue("IsTuner2"));
                    if (jsonObject.containsKey("FavorGroupID")) {
                        JSONArray jsonArray1 = jsonObject.getJSONArray("FavorGroupID");
                        for (int j3 = 0; j3 < jsonArray1.size(); j3++) {
                            channelModel.mfavGroupIDs.add(Integer.valueOf(jsonArray1.getIntValue(i)));
                        }
                    }
                    if (GMScreenGlobalInfo.isSdsOpen() == 0 || channelModel.getIsTuner2() == 0) {
                        models.add(channelModel);
                    }
                }
                return models;
            case 1:
            case 4:
                List<DataConvertTimeModel> models2 = new ArrayList<>();
                for (int i2 = 0; i2 < jsonArray.size(); i2++) {
                    DataConvertTimeModel timeModel = new DataConvertTimeModel();
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i2);
                    if (jsonObject2.containsKey("Confirm")) {
                        String confirm = jsonObject2.getString("Confirm");
                        DataConvertTimeModel.isConfirm = Integer.parseInt(confirm);
                    }
                    String timerProgramName = jsonObject2.getString("TimerProgramName");
                    timeModel.SetTimeProgramName(timerProgramName);
                    String programId = jsonObject2.getString("TimerProgramSatTpId");
                    timeModel.setProgramId(programId);
                    int timerMonth = jsonObject2.getIntValue("TimerMonth");
                    timeModel.SetTimeMonth(timerMonth);
                    int timerDay = jsonObject2.getIntValue("TimerDay");
                    timeModel.SetTimeDay(timerDay);
                    int timerStartHour = jsonObject2.getIntValue("TimerStartHour");
                    timeModel.SetStartHour(timerStartHour);
                    int timerStartMin = jsonObject2.getIntValue("TimerStartMin");
                    timeModel.SetStartMin(timerStartMin);
                    int timerEndHour = jsonObject2.getIntValue("TimerEndHour");
                    timeModel.SetEndHour(timerEndHour);
                    int timerEndMin = jsonObject2.getIntValue("TimerEndMin");
                    timeModel.SetEndMin(timerEndMin);
                    int timerRepeat = jsonObject2.getIntValue("TimerRepeat");
                    timeModel.SetTimerRepeat(timerRepeat);
                    int timerStatus = jsonObject2.getIntValue("TimerStatus");
                    timeModel.SetTimerStatus(timerStatus);
                    if (jsonObject2.containsKey("TimerEventID")) {
                        int timerUniqID = jsonObject2.getIntValue("TimerEventID");
                        timeModel.setEventId(timerUniqID);
                    }
                    models2.add(timeModel);
                }
                return models2;
            case 2:
                List<DataConvertControlModel> models3 = new ArrayList<>();
                for (int i3 = 0; i3 < jsonArray.size(); i3++) {
                    DataConvertControlModel controlModel = new DataConvertControlModel();
                    controlModel.SetPowerOff(-1);
                    JSONObject jsonObject3 = jsonArray.getJSONObject(i3);
                    if (jsonObject3.containsKey("Password")) {
                        String password = jsonObject3.getString("Password");
                        controlModel.SetPassword(password);
                    }
                    int pswLockSwitch = jsonObject3.getIntValue("PasswordLock");
                    controlModel.SetPswLockSwitch(pswLockSwitch);
                    if (jsonObject3.containsKey("ServiceLock")) {
                        int serviceLock = jsonObject3.getIntValue("ServiceLock");
                        controlModel.SetServiceLockSwitch(serviceLock);
                    }
                    int installLock = jsonObject3.getIntValue("InstallationLock");
                    controlModel.SetInstallLockSwitch(installLock);
                    int editChannelLockSwitch = jsonObject3.getIntValue("EditChannelLock");
                    controlModel.SetEditChannelLockSwitch(editChannelLockSwitch);
                    int settingsLock = jsonObject3.getIntValue("SettingsLock");
                    controlModel.SetSettingsLockSwitch(settingsLock);
                    if (jsonObject3.containsKey("NetworkLock")) {
                        int networkLock = jsonObject3.getIntValue("NetworkLock");
                        controlModel.SetNetworkLockSwitch(networkLock);
                    }
                    int ageRating = jsonObject3.getIntValue("AgeRating");
                    controlModel.SetAgeRatingSwitch(ageRating);
                    if (jsonObject3.containsKey("IsLockScreen")) {
                        int isLockScreen = jsonObject3.getIntValue("IsLockScreen");
                        controlModel.SetIsLockScreen(isLockScreen);
                    }
                    int powerMode = jsonObject3.getIntValue("PowerMode");
                    controlModel.SetPowerOff(powerMode);
                    models3.add(controlModel);
                }
                return models3;
            case 3:
                List<DataConvertUpdateModel> models4 = new ArrayList<>();
                for (int i4 = 0; i4 < jsonArray.size(); i4++) {
                    DataConvertUpdateModel updateModel = new DataConvertUpdateModel();
                    JSONObject jsonObject4 = jsonArray.getJSONObject(i4);
                    int customerId = jsonObject4.getIntValue("CustomerId");
                    updateModel.SetCustomerId(customerId);
                    int modelId = jsonObject4.getIntValue("HardwareId");
                    updateModel.SetModelId(modelId);
                    int versionId = jsonObject4.getIntValue("VersionId");
                    updateModel.SetVersionId(versionId);
                    models4.add(updateModel);
                }
                return models4;
            case 5:
            default:
                return null;
            case 6:
                int programEpgCurrentDayIndex = 0;
                int eventCurrentLanguageIndex = 0;
                List<GsEPGTableChannel> programEpgList = new ArrayList<>();
                for (int m = 0; m < jsonArray.size(); m++) {
                    JSONObject proEpgJsonObject = jsonArray.getJSONObject(m);
                    GsEPGTableChannel programEpg = new GsEPGTableChannel();
                    int progNo = proEpgJsonObject.getIntValue("prog_no");
                    programEpg.setProgNo(progNo);
                    int original_net_id = proEpgJsonObject.getIntValue("original_net_id");
                    programEpg.setOriginalNetworkID(original_net_id);
                    int transport_stream_id = proEpgJsonObject.getIntValue("transport_stream_id");
                    programEpg.setTransportStreamID(transport_stream_id);
                    int today_date = proEpgJsonObject.getIntValue("today_date");
                    programEpg.setTodayDate((byte) today_date);
                    int current_epg_time = proEpgJsonObject.getIntValue("current_epg_time");
                    programEpg.setCurrentEpgTime(current_epg_time);
                    JSONArray epgListJsonArray = proEpgJsonObject.getJSONArray("daily_epg_list");
                    for (int i5 = 0; i5 < epgListJsonArray.size(); i5++) {
                        JSONObject epgListJsonObjects = epgListJsonArray.getJSONObject(i5);
                        int event_valid_num = epgListJsonObjects.getIntValue("event_valid_num");
                        programEpg.setArrayEventFieldByIndex(programEpgCurrentDayIndex, event_valid_num);
                        JSONArray eventsJsonArray = epgListJsonObjects.getJSONArray("event_list");
                        for (int j4 = 0; j4 < eventsJsonArray.size(); j4++) {
                            GsEPGEvent epgEvent = new GsEPGEvent();
                            JSONObject eventsJsonObjects = eventsJsonArray.getJSONObject(j4);
                            if (eventsJsonObjects.containsKey("event_start_time")) {
                                String event_start_time = eventsJsonObjects.getString("event_start_time");
                                epgEvent.setStartTime(event_start_time);
                            }
                            if (eventsJsonObjects.containsKey("event_end_time")) {
                                String event_end_time = eventsJsonObjects.getString("event_end_time");
                                epgEvent.setEndTime(event_end_time);
                            }
                            if (eventsJsonObjects.containsKey("event_age_rating")) {
                                int event_age_rating = eventsJsonObjects.getIntValue("event_age_rating");
                                epgEvent.setAgeRating(event_age_rating);
                            }
                            if (eventsJsonObjects.containsKey("event_timer_type")) {
                                int event_timer_type = eventsJsonObjects.getIntValue("event_timer_type");
                                epgEvent.setEpgTimerType(event_timer_type);
                            }
                            if (eventsJsonObjects.containsKey("event_total_language")) {
                                int event_total_language = eventsJsonObjects.getIntValue("event_total_language");
                                epgEvent.setTotalEpgLanguage(event_total_language);
                            }
                            if (eventsJsonObjects.containsKey("event_content_list")) {
                                JSONArray eventContentsJsonArray = eventsJsonObjects.getJSONArray("event_content_list");
                                for (int k = 0; k < eventContentsJsonArray.size(); k++) {
                                    JSONObject eventContentsJsonObjects = eventContentsJsonArray.getJSONObject(k);
                                    if (eventContentsJsonObjects.containsKey("event_titile_lang_code")) {
                                        int event_titile_lang_code = eventContentsJsonObjects.getIntValue("event_titile_lang_code");
                                        epgEvent.setTitleLanCode(eventCurrentLanguageIndex, event_titile_lang_code);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_sub_titile_lang_code")) {
                                        int event_sub_titile_lang_code = eventContentsJsonObjects.getIntValue("event_sub_titile_lang_code");
                                        epgEvent.setSubtitleLanCode(eventCurrentLanguageIndex, event_sub_titile_lang_code);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_desc_lang_code")) {
                                        int event_desc_lang_code = eventContentsJsonObjects.getIntValue("event_desc_lang_code");
                                        epgEvent.setDescLanCode(eventCurrentLanguageIndex, event_desc_lang_code);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_titile_len")) {
                                        int event_titile_len = eventContentsJsonObjects.getIntValue("event_titile_len");
                                        epgEvent.setTitleLen(eventCurrentLanguageIndex, event_titile_len);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_sub_titile_len")) {
                                        int event_sub_titile_len = eventContentsJsonObjects.getIntValue("event_sub_titile_len");
                                        epgEvent.setSubtitleLen(eventCurrentLanguageIndex, event_sub_titile_len);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_desc_len")) {
                                        int event_desc_len = eventContentsJsonObjects.getIntValue("event_desc_len");
                                        epgEvent.setDescLen(eventCurrentLanguageIndex, event_desc_len);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_title")) {
                                        String event_title = eventContentsJsonObjects.getString("event_title");
                                        epgEvent.setEventTitle(eventCurrentLanguageIndex, event_title);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_sub_title")) {
                                        String event_sub_title = eventContentsJsonObjects.getString("event_sub_title");
                                        epgEvent.setEventSubTitle(eventCurrentLanguageIndex, event_sub_title);
                                    }
                                    if (eventContentsJsonObjects.containsKey("event_desc")) {
                                        String event_desc = eventContentsJsonObjects.getString("event_desc");
                                        epgEvent.setEventDesc(eventCurrentLanguageIndex, event_desc);
                                        eventCurrentLanguageIndex++;
                                    }
                                }
                            }
                            programEpg.getEpgDayByIndex(programEpgCurrentDayIndex).getArrayList().add(epgEvent);
                            eventCurrentLanguageIndex = 0;
                        }
                        programEpgCurrentDayIndex++;
                    }
                    programEpgList.add(programEpg);
                }
                return programEpgList;
            case 7:
                List<DataConvertTimeModel> models5 = new ArrayList<>();
                for (int i6 = 0; i6 < jsonArray.size(); i6++) {
                    DataConvertTimeModel timeModel2 = new DataConvertTimeModel();
                    JSONObject jsonObject5 = jsonArray.getJSONObject(i6);
                    String confirm2 = jsonObject5.getString("Confirm");
                    DataConvertTimeModel.isConfirm = Integer.parseInt(confirm2);
                    timeModel2.SetTimerIndex(jsonObject5.getIntValue("TimerIndex"));
                    models5.add(timeModel2);
                }
                return models5;
            case 8:
                List<DataConvertChannelModel> models6 = new ArrayList<>();
                for (int i7 = 0; i7 < jsonArray.size(); i7++) {
                    DataConvertChannelModel model = new DataConvertChannelModel();
                    JSONObject jsonObject6 = jsonArray.getJSONObject(i7);
                    int programId2 = jsonObject6.getIntValue("LockedChannelIndex");
                    model.setIsProgramHd(programId2);
                    int ChannelType = jsonObject6.getIntValue("TVState");
                    model.setChannelTpye(ChannelType);
                    models6.add(model);
                }
                return models6;
            case 9:
                List<DataConvertTimeModel> models7 = new ArrayList<>();
                for (int i8 = 0; i8 < jsonArray.size(); i8++) {
                    DataConvertTimeModel timeModel3 = new DataConvertTimeModel();
                    JSONObject jsonObject7 = jsonArray.getJSONObject(i8);
                    int stbMonth = jsonObject7.getIntValue("StbMonth");
                    DataConvertTimeModel.stbMonth = stbMonth;
                    int stbDay = jsonObject7.getIntValue("StbDay");
                    DataConvertTimeModel.stbDay = stbDay;
                    int stbHour = jsonObject7.getIntValue("StbHour");
                    DataConvertTimeModel.stbHour = stbHour;
                    int stbMin = jsonObject7.getIntValue("StbMin");
                    DataConvertTimeModel.stbMin = stbMin;
                    models7.add(timeModel3);
                }
                return models7;
            case 10:
                List<DataConvertFavorModel> favorModels = new ArrayList<>();
                for (int i9 = 0; i9 < jsonArray.size(); i9++) {
                    JSONObject jsonObject8 = jsonArray.getJSONObject(i9);
                    int favMaxNum = jsonObject8.getIntValue("favMaxNum");
                    DataConvertFavorModel.favorNum = favMaxNum;
                    if (jsonObject8.containsKey("favGroupNames")) {
                        JSONArray favGroupNameJsonArray = jsonObject8.getJSONArray("favGroupNames");
                        for (int j5 = 0; j5 < favGroupNameJsonArray.size(); j5++) {
                            DataConvertFavorModel model2 = new DataConvertFavorModel();
                            String favorGroupName = favGroupNameJsonArray.getString(j5);
                            model2.SetFavorName(favorGroupName);
                            favorModels.add(model2);
                        }
                    }
                    if (jsonObject8.containsKey("favGroupIds")) {
                        JSONArray favGroupIdJsonArray = jsonObject8.getJSONArray("favGroupIds");
                        for (int k2 = 0; k2 < favGroupIdJsonArray.size(); k2++) {
                            int favorGroupId = favGroupIdJsonArray.getIntValue(k2);
                            favorModels.get(k2).setFavorTypeID(favorGroupId);
                        }
                    }
                }
                return favorModels;
            case 11:
                List<DataConvertControlModel> controlModels = new ArrayList<>();
                for (int i10 = 0; i10 < jsonArray.size(); i10++) {
                    JSONObject jsonObject9 = jsonArray.getJSONObject(i10);
                    DataConvertControlModel model3 = new DataConvertControlModel();
                    int SleepSwitch = jsonObject9.getIntValue("SleepSwitch");
                    model3.setSleepSwitch(SleepSwitch);
                    int SleepTime = jsonObject9.getIntValue("SleepTime");
                    model3.setSleepTime(SleepTime);
                    int ScreenLock = jsonObject9.getIntValue("ScreenLock");
                    model3.SetIsLockScreen(ScreenLock);
                    int PowerMode = jsonObject9.getIntValue("PowerMode");
                    model3.SetPowerOff(PowerMode);
                    controlModels.add(model3);
                }
                return controlModels;
            case 12:
                List<DataConvertChannelTypeModel> models8 = new ArrayList<>();
                for (int i11 = 0; i11 < jsonArray.size(); i11++) {
                    JSONObject jsonObject10 = jsonArray.getJSONObject(i11);
                    DataConvertChannelTypeModel model4 = new DataConvertChannelTypeModel();
                    int CurChannelType = jsonObject10.getIntValue("CurChannelType");
                    DataConvertChannelTypeModel.setCurrent_channel_tv_radio_type(CurChannelType);
                    int tv_radio_key_press = jsonObject10.getIntValue("tv_radio_key_press");
                    model4.setTvRadioKeyPress(tv_radio_key_press);
                    models8.add(model4);
                }
                return models8;
            case 13:
                List<DataConvertSortModel> models9 = new ArrayList<>();
                for (int i12 = 0; i12 < jsonArray.size(); i12++) {
                    JSONObject jsonObject11 = jsonArray.getJSONObject(i12);
                    DataConvertSortModel sortModel = new DataConvertSortModel();
                    sortModel.setmSortType(jsonObject11.getIntValue("SortType"));
                    sortModel.setmMacroFlag(jsonObject11.getIntValue("MacroFlag"));
                    if (jsonObject11.containsKey("SortTypeList") && (list = jsonObject11.get("SortTypeList").toString().split(ClientInfo.SEPARATOR_BETWEEN_VARS)) != null && list.length > 0) {
                        ArrayList<String> sortTypeList = new ArrayList<>();
                        for (String str2 : list) {
                            sortTypeList.add(str2);
                        }
                        sortModel.setSortTypeList(sortTypeList);
                    }
                    models9.add(sortModel);
                }
                return models9;
            case 14:
                List<DataConvertStbInfoModel> stbInfoModels = new ArrayList<>();
                for (int i13 = 0; i13 < jsonArray.size(); i13++) {
                    JSONObject jsonObject12 = jsonArray.getJSONObject(i13);
                    DataConvertStbInfoModel model5 = new DataConvertStbInfoModel();
                    int StbStatus = jsonObject12.getIntValue("StbStatus");
                    model5.setmStbStatus(StbStatus);
                    String ProductName = jsonObject12.getString("ProductName");
                    model5.setmProductName(ProductName);
                    String SoftwareVersion = jsonObject12.getString("SoftwareVersion");
                    model5.setmSoftwareVersion(SoftwareVersion);
                    String SerialNumber = jsonObject12.getString("SerialNumber");
                    model5.setmSerialNumber(SerialNumber);
                    int ChannelNum = jsonObject12.getIntValue("ChannelNum");
                    model5.setmChannelNum(ChannelNum);
                    int MaxNumOfPrograms = jsonObject12.getIntValue("MaxNumOfPrograms");
                    model5.setmMaxNumOfPrograms(MaxNumOfPrograms);
                    stbInfoModels.add(model5);
                }
                return stbInfoModels;
            case 15:
                List<String> models10 = new ArrayList<>();
                for (int i14 = 0; i14 < jsonArray.size(); i14++) {
                    String model6 = jsonArray.getJSONObject(i14).getString("Data");
                    if (model6 != null) {
                        models10.add(model6);
                    }
                }
                return models10;
            case 16:
                List<Map<String, Object>> models11 = new ArrayList<>();
                for (int i15 = 0; i15 < jsonArray.size(); i15++) {
                    JSONObject jsonObject13 = jsonArray.getJSONObject(i15);
                    Map<String, Object> model7 = new HashMap<>();
                    int success = jsonObject13.getIntValue("success");
                    model7.put("success", Integer.valueOf(success));
                    int url = jsonObject13.getIntValue("url");
                    model7.put("url", Integer.valueOf(url));
                    String errormsg = jsonObject13.getString("errormsg");
                    model7.put("errormsg", errormsg);
                    models11.add(model7);
                }
                return models11;
            case 17:
                List<String> tempList = new ArrayList<>();
                for (int i16 = 0; i16 < jsonArray.size(); i16++) {
                    JSONObject jsonObject14 = jsonArray.getJSONObject(i16);
                    String temp = new String(jsonObject14.getString("cur_channel_list_type"));
                    GMScreenGlobalInfo.setmMaxPasswordNum(jsonObject14.getIntValue("max_password_num"));
                    DataConvertChannelTypeModel.setCurrent_channel_tv_radio_type(jsonObject14.getIntValue("cur_channel_type"));
                    GMScreenGlobalInfo.setmPvr2ipServerSupport(jsonObject14.getIntValue("is_support_pvr2ip_server"));
                    GMScreenGlobalInfo.setSdsOpen(jsonObject14.getIntValue("is_sds_open"));
                    tempList.add(temp);
                }
                return tempList;
            case 18:
                List<DataConvertSatModel> models12 = new ArrayList<>();
                for (int i17 = 0; i17 < jsonArray.size(); i17++) {
                    DataConvertSatModel model8 = new DataConvertSatModel();
                    JSONObject jsonObject15 = jsonArray.getJSONObject(i17);
                    String SatName = jsonObject15.getString("SatName");
                    model8.setmSatName(SatName);
                    if (jsonObject15.containsKey("SatIndex")) {
                        SatNo = jsonObject15.getIntValue("SatIndex");
                    } else {
                        SatNo = jsonObject15.getIntValue("SatNo");
                    }
                    model8.setmSatIndex(SatNo);
                    int SatAngle = jsonObject15.getIntValue("SatAngle");
                    model8.setmSatAngle(SatAngle);
                    int SatDir = jsonObject15.getIntValue("SatDir");
                    model8.setmSatDir(SatDir);
                    models12.add(model8);
                }
                return models12;
            case 19:
                List<DataConvertTpModel> models13 = new ArrayList<>();
                for (int i18 = 0; i18 < jsonArray.size(); i18++) {
                    JSONObject jsonObject16 = jsonArray.getJSONObject(i18);
                    DataConvertTpModel model9 = new DataConvertTpModel();
                    if (jsonObject16.containsKey("SR")) {
                        tpIndex = jsonObject16.getIntValue("TPIndex");
                        satIndex = jsonObject16.getIntValue("SatIndex");
                        symRate = jsonObject16.getIntValue("SR");
                        polInt = jsonObject16.getIntValue("POL");
                        fec = jsonObject16.getIntValue("FEC");
                        freq = jsonObject16.getIntValue("Freq");
                    } else {
                        tpIndex = jsonObject16.getIntValue("TpIndex");
                        satIndex = jsonObject16.getIntValue("SatIndex");
                        symRate = jsonObject16.getIntValue("SystemRate");
                        polInt = jsonObject16.getIntValue("Pol");
                        fec = jsonObject16.getIntValue("Fec");
                        freq = jsonObject16.getIntValue("Freq");
                    }
                    char pol2 = 'h';
                    if (polInt == 0) {
                        pol2 = 'h';
                    } else if (polInt == 1) {
                        pol2 = 'v';
                    } else if (polInt == 2) {
                        pol2 = 'l';
                    } else if (polInt == 3) {
                        pol2 = 'r';
                    }
                    model9.setTpIndex(tpIndex);
                    model9.setSatIndex(satIndex);
                    model9.setSymRate(symRate);
                    model9.setPol(pol2);
                    model9.setFec(fec);
                    model9.setFreq(freq);
                    models13.add(model9);
                }
                return models13;
            case 20:
                List<DataConvertPvrInfoModel> models14 = new ArrayList<>();
                for (int i19 = 0; i19 < jsonArray.size(); i19++) {
                    JSONObject jsonObject17 = jsonArray.getJSONObject(i19);
                    DataConvertPvrInfoModel model10 = new DataConvertPvrInfoModel();
                    String pvr_name = jsonObject17.getString("pvrname");
                    model10.setProgramName(pvr_name);
                    int pvr_uid = jsonObject17.getIntValue("pvr_uid");
                    model10.setmPvrId(pvr_uid);
                    String pvr_duration = jsonObject17.getString("pvr_duration");
                    model10.setmPvrDuration(pvr_duration);
                    String Pvr_time = jsonObject17.getString("pvr_time");
                    model10.setmPvrTime(Pvr_time);
                    int pvr_type = jsonObject17.getIntValue("pvr_type");
                    model10.setmPvrType(pvr_type);
                    int crypto = jsonObject17.getIntValue("crypto");
                    model10.setmPvrCrypto(crypto);
                    models14.add(model10);
                }
                return models14;
            case 21:
                List<GsChatSetting> models15 = new ArrayList<>();
                for (int i20 = 0; i20 < jsonArray.size(); i20++) {
                    JSONObject jsonObject18 = jsonArray.getJSONObject(i20);
                    GsChatSetting model11 = GsChatSetting.getInstance();
                    String MySn = jsonObject18.getString("MySN");
                    model11.setSerialNumber(MySn);
                    String MyUserName = jsonObject18.getString("MyUsername");
                    model11.setUsername(MyUserName);
                    String UserId = jsonObject18.getString("USERID");
                    model11.setUserId(Integer.parseInt(UserId));
                    models15.add(model11);
                }
                return models15;
            case 22:
                List<GsChatRoomInfo> models16 = new ArrayList<>();
                GsChatUser chatUser = null;
                for (int i21 = 0; i21 < jsonArray.size(); i21++) {
                    GsChatRoomInfo model12 = new GsChatRoomInfo();
                    JSONObject jsonObject19 = jsonArray.getJSONObject(i21);
                    String EventTitle = jsonObject19.getString("EventTitle");
                    model12.setEventTitle(EventTitle);
                    String OnlineUserNum = jsonObject19.getString("OnlineUserNum");
                    model12.setOnlineNum(Integer.parseInt(OnlineUserNum));
                    String RoomId = jsonObject19.getString("RoomId");
                    model12.setRoomID(Integer.parseInt(RoomId));
                    if (jsonObject19.containsKey("UserInfo")) {
                        chatUser = new GsChatUser();
                        new JSONArray();
                        JSONArray jsonArrayTemp = jsonObject19.getJSONArray("UserInfo");
                        for (int j6 = 0; j6 < jsonArrayTemp.size(); j6++) {
                            new JSONObject(true);
                            JSONObject jsonObjectTemp = jsonArrayTemp.getJSONObject(j6);
                            String UserId2 = jsonObjectTemp.getString("USERID");
                            chatUser.setUserID(Integer.parseInt(UserId2));
                            String UserName = jsonObjectTemp.getString("Username");
                            chatUser.setUsername(UserName);
                        }
                    }
                    model12.getUserList().add(chatUser);
                    models16.add(model12);
                }
                return models16;
            case 23:
                List<DataConvertChatMsgModel> models17 = new ArrayList<>();
                for (int i22 = 0; i22 < jsonArray.size(); i22++) {
                    DataConvertChatMsgModel model13 = new DataConvertChatMsgModel();
                    JSONObject jsonObject20 = jsonArray.getJSONObject(i22);
                    String TimeStamp = jsonObject20.getString("Timestamp");
                    model13.setTimestamp(Long.parseLong(TimeStamp));
                    int UserId3 = Integer.parseInt(jsonObject20.getString("USERID"));
                    model13.setUserID(UserId3);
                    if (UserId3 == GsChatSetting.getInstance().getUserId()) {
                        model13.setMsgType(1);
                    } else {
                        model13.setMsgType(0);
                    }
                    String UserName2 = jsonObject20.getString("Username");
                    model13.setUsername(UserName2);
                    String Content = jsonObject20.getString("Content");
                    model13.setContent(Content);
                    models17.add(model13);
                }
                return models17;
            case 24:
                List<GsChatUser> models18 = new ArrayList<>();
                for (int i23 = 0; i23 < jsonArray.size(); i23++) {
                    GsChatUser model14 = new GsChatUser();
                    JSONObject jsonObject21 = jsonArray.getJSONObject(i23);
                    String UserId4 = jsonObject21.getString("USERID");
                    model14.setUserID(Integer.parseInt(UserId4));
                    String UserName3 = jsonObject21.getString("Username");
                    model14.setUsername(UserName3);
                    models18.add(model14);
                }
                return models18;
            case 25:
                List<GsChatSetting> models19 = new ArrayList<>();
                for (int i24 = 0; i24 < jsonArray.size(); i24++) {
                    JSONObject jsonObject22 = jsonArray.getJSONObject(i24);
                    GsChatSetting model15 = GsChatSetting.getInstance();
                    String ShowWindow = jsonObject22.getString("ShowWindow");
                    model15.setShowWindow(Integer.parseInt(ShowWindow));
                    String WindowSize = jsonObject22.getString("WindowSize");
                    model15.setWindowSize(Integer.parseInt(WindowSize));
                    String WindowPosition = jsonObject22.getString("WindowPosition");
                    model15.setWindowPosition(Integer.parseInt(WindowPosition));
                    String WindowTransparency = jsonObject22.getString("WindowTransparency");
                    model15.setWindowTransparency(Integer.parseInt(WindowTransparency));
                    models19.add(model15);
                }
                return models19;
            case 26:
                List<DataConvertGChatChannelInfoModel> models20 = new ArrayList<>();
                for (int i25 = 0; i25 < jsonArray.size(); i25++) {
                    DataConvertGChatChannelInfoModel model16 = new DataConvertGChatChannelInfoModel();
                    JSONObject jsonObject23 = jsonArray.getJSONObject(i25);
                    String Angle = jsonObject23.getString("Angle");
                    model16.setSatAngle(Angle);
                    String Tp = jsonObject23.getString("Tp");
                    model16.setTp(Integer.parseInt(Tp));
                    String ServiceId = jsonObject23.getString("ServiceId");
                    model16.setServiceId(Integer.parseInt(ServiceId));
                    String EPG = jsonObject23.getString("EPG");
                    model16.setEpg(EPG);
                    models20.add(model16);
                }
                return models20;
            case 27:
                List<DataConvertUsernameModel> models21 = new ArrayList<>();
                for (int i26 = 0; i26 < jsonArray.size(); i26++) {
                    DataConvertUsernameModel model17 = new DataConvertUsernameModel();
                    String UserName4 = jsonArray.getJSONObject(i26).getString("Username");
                    model17.setUsername(UserName4);
                    models21.add(model17);
                }
                return models21;
            case 28:
                List<DataConvertSpectrumInfo> models22 = new ArrayList<>();
                for (int i27 = 0; i27 < jsonArray.size(); i27++) {
                    DataConvertSpectrumInfo model18 = new DataConvertSpectrumInfo();
                    JSONObject jsonObject24 = jsonArray.getJSONObject(i27);
                    model18.setStartFre(jsonObject24.getIntValue(DataConvertSpectrumInfo.START_FRE));
                    model18.setEndFre(jsonObject24.getIntValue(DataConvertSpectrumInfo.END_FRE));
                    model18.setOsdLen(jsonObject24.getIntValue(DataConvertSpectrumInfo.OSDLEN));
                    int speInfoArrayLength = jsonObject24.getJSONArray(DataConvertSpectrumInfo.SPE_INFO_ARRAY).size();
                    Integer[] retDbuv = new Integer[speInfoArrayLength];
                    jsonObject24.getJSONArray(DataConvertSpectrumInfo.SPE_INFO_ARRAY).toArray(retDbuv);
                    model18.setRetDbuv(TypeConvertUtils.IntegerArrayToIntArray(retDbuv));
                    models22.add(model18);
                }
                return models22;
            case 29:
                List<DataConvertSpectrumSetting> models23 = new ArrayList<>();
                for (int i28 = 0; i28 < jsonArray.size(); i28++) {
                    JSONObject jsonObject25 = jsonArray.getJSONObject(i28);
                    DataConvertSpectrumSetting model19 = new DataConvertSpectrumSetting();
                    model19.setDvbsSpe22kOn(jsonObject25.getIntValue(DataConvertSpectrumSetting.DVBS_SPE_22K_ON));
                    model19.setDvbsSpeCentFre(jsonObject25.getIntValue(DataConvertSpectrumSetting.DVBS_SPE_CENT_FRE));
                    model19.setDvbsSpeDesecq(jsonObject25.getIntValue(DataConvertSpectrumSetting.DVBS_SPE_DESECQ));
                    model19.setDvbsSpeRef(jsonObject25.getIntValue(DataConvertSpectrumSetting.DVBS_SPE_REF));
                    model19.setDvbsSpeSpan(jsonObject25.getIntValue(DataConvertSpectrumSetting.DVBS_SPE_SPEN));
                    model19.setDvbsSpeV(jsonObject25.getIntValue(DataConvertSpectrumSetting.DVBS_SPE_V));
                    models23.add(model19);
                }
                return models23;
        }
    }

    @Override // mktvsmart.screen.dataconvert.parser.DataParser
    public String serialize(List<?> models, int responseStyle) throws Exception {
        JSONObject jSONObject = new JSONObject(true);
        JSONArray jsonArray = new JSONArray();
        if (models == null) {
            jSONObject.put("request", (Object) new StringBuilder(String.valueOf(responseStyle)).toString());
        } else {
            jSONObject.put("request", (Object) new StringBuilder(String.valueOf(responseStyle)).toString());
            Object o = models.get(0);
            if (o.getClass().getName() == DataConvertChannelModel.class.getName()) {
                switch (responseStyle) {
                    case 0:
                        jSONObject.put("FromIndex", (Object) new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).GetProgramIndex() + responseStyle)).toString());
                        jSONObject.put("ToIndex", (Object) new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(1)).GetProgramIndex())).toString());
                        break;
                    case 5:
                        Iterator<?> it = models.iterator();
                        while (it.hasNext()) {
                            DataConvertChannelModel model = (DataConvertChannelModel) it.next();
                            JSONObject jsonObjectTemp = new JSONObject();
                            jsonObjectTemp.put("ProgramId", (Object) model.GetProgramId());
                            jsonArray.add(jsonObjectTemp);
                        }
                        jSONObject.put(ARRAY, (Object) jsonArray);
                        break;
                    case 104:
                        jSONObject.put("ProgramId", (Object) ((DataConvertChannelModel) models.get(0)).GetProgramId());
                        break;
                    case 1000:
                    case GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY /* 1009 */:
                        Iterator<?> it2 = models.iterator();
                        while (it2.hasNext()) {
                            DataConvertChannelModel model2 = (DataConvertChannelModel) it2.next();
                            JSONObject jsonObjectTemp2 = new JSONObject(true);
                            jsonObjectTemp2.put("TvState", (Object) new StringBuilder(String.valueOf(model2.getChannelTpye())).toString());
                            jsonObjectTemp2.put("ProgramId", (Object) model2.GetProgramId());
                            int platformId = GMScreenGlobalInfo.getCurStbPlatform();
                            switch (platformId) {
                                case 32:
                                case 71:
                                case 72:
                                case 74:
                                    if (responseStyle == 1009) {
                                        jsonObjectTemp2.put("iResolutionRatio", (Object) new StringBuilder(String.valueOf(TranscodeConstants.iCurResolution)).toString());
                                        jsonObjectTemp2.put("iBitrate", (Object) new StringBuilder(String.valueOf(TranscodeConstants.iCurBitrate)).toString());
                                        break;
                                    } else {
                                        break;
                                    }
                            }
                            jsonArray.add(jsonObjectTemp2);
                        }
                        jSONObject.put(ARRAY, (Object) jsonArray);
                        break;
                    case 1001:
                        Iterator<?> it3 = models.iterator();
                        while (it3.hasNext()) {
                            DataConvertChannelModel model3 = (DataConvertChannelModel) it3.next();
                            JSONObject jsonObjectTemp3 = new JSONObject(true);
                            jsonObjectTemp3.put("TvState", (Object) new StringBuilder(String.valueOf(model3.getChannelTpye())).toString());
                            jsonObjectTemp3.put("ProgramId", (Object) model3.GetProgramId());
                            jsonObjectTemp3.put("ProgramName", (Object) model3.getProgramName());
                            jsonArray.add(jsonObjectTemp3);
                        }
                        jSONObject.put(ARRAY, (Object) jsonArray);
                        break;
                    case 1002:
                        int num = 0;
                        jSONObject.put("TvState", (Object) new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).getChannelTpye())).toString());
                        Iterator<?> it4 = models.iterator();
                        while (it4.hasNext()) {
                            DataConvertChannelModel model4 = (DataConvertChannelModel) it4.next();
                            JSONObject jsonObjectTemp4 = new JSONObject(true);
                            jsonObjectTemp4.put("ProgramId", (Object) model4.GetProgramId());
                            jsonArray.add(jsonObjectTemp4);
                            num++;
                        }
                        jSONObject.put(ARRAY, (Object) jsonArray);
                        jSONObject.put("TotalNum", (Object) new StringBuilder(String.valueOf(num)).toString());
                        break;
                    case GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK /* 1004 */:
                        int num2 = 0;
                        jSONObject.put("TvState", (Object) new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).getChannelTpye())).toString());
                        jSONObject.put("FavMark", (Object) new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).GetFavMark())).toString());
                        String mfavGroupIDs = "";
                        Iterator<Integer> it5 = ((DataConvertChannelModel) models.get(0)).mfavGroupIDs.iterator();
                        while (it5.hasNext()) {
                            int iFavGroupID = it5.next().intValue();
                            mfavGroupIDs = String.valueOf(mfavGroupIDs) + iFavGroupID + ":";
                        }
                        if (mfavGroupIDs.length() >= 0) {
                            jSONObject.put("FavorGroupID", (Object) mfavGroupIDs);
                        }
                        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
                            case 30:
                            case 31:
                            case 32:
                            case 71:
                            case 72:
                            case 74:
                                Iterator<?> it6 = models.iterator();
                                while (it6.hasNext()) {
                                    DataConvertChannelModel model5 = (DataConvertChannelModel) it6.next();
                                    JSONObject jsonObjectTemp5 = new JSONObject(true);
                                    jsonObjectTemp5.put("ProgramId", (Object) model5.GetProgramId());
                                    jsonArray.add(jsonObjectTemp5);
                                }
                                jSONObject.put(ARRAY, (Object) jsonArray);
                                break;
                            default:
                                Iterator<?> it7 = models.iterator();
                                while (it7.hasNext()) {
                                    DataConvertChannelModel model6 = (DataConvertChannelModel) it7.next();
                                    JSONObject jsonObjectTemp6 = new JSONObject(true);
                                    jsonObjectTemp6.put("ProgramId", (Object) model6.GetProgramId());
                                    jsonArray.add(jsonObjectTemp6);
                                    num2++;
                                }
                                jSONObject.put(ARRAY, (Object) jsonArray);
                                jSONObject.put("TotalNum", (Object) new StringBuilder(String.valueOf(num2)).toString());
                                break;
                        }
                    case GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE /* 1005 */:
                        boolean isFirstTime = true;
                        int num3 = 0;
                        Iterator<?> it8 = models.iterator();
                        while (it8.hasNext()) {
                            DataConvertChannelModel model7 = (DataConvertChannelModel) it8.next();
                            JSONObject jsonObjectTemp7 = new JSONObject(true);
                            if (isFirstTime) {
                                jsonObjectTemp7.put("TvState", (Object) new StringBuilder(String.valueOf(model7.getChannelTpye())).toString());
                                jsonObjectTemp7.put("MoveToPosition", (Object) model7.getMoveToPosition());
                                isFirstTime = false;
                            }
                            jsonObjectTemp7.put("ProgramId", (Object) new StringBuilder(String.valueOf(model7.GetProgramId())).toString());
                            jsonArray.add(jsonObjectTemp7);
                            num3++;
                        }
                        jSONObject.put("TotalNum", (Object) new StringBuilder(String.valueOf(num3)).toString());
                        jSONObject.put(ARRAY, (Object) jsonArray);
                        break;
                    case GlobalConstantValue.GMS_MSG_GCHAT_DO_START /* 1100 */:
                        DataConvertChannelModel model8 = (DataConvertChannelModel) models.get(0);
                        jSONObject.put("ProgramId", (Object) model8.GetProgramId());
                        jSONObject.put("TvState", (Object) String.valueOf(model8.getChannelTpye()));
                        break;
                }
            } else if (o.getClass().getName() == DataConvertFavChannelModel.class.getName()) {
                if (responseStyle == 1011) {
                    jSONObject.put("TvState", (Object) new StringBuilder(String.valueOf(((DataConvertFavChannelModel) models.get(0)).getChannelTpye())).toString());
                    jSONObject.put("SelectListType", (Object) new StringBuilder(String.valueOf(((DataConvertFavChannelModel) models.get(0)).getSelectListType())).toString());
                    Iterator<?> it9 = models.iterator();
                    while (it9.hasNext()) {
                        DataConvertFavChannelModel model9 = (DataConvertFavChannelModel) it9.next();
                        JSONObject jsonObjectTemp8 = new JSONObject(true);
                        jsonObjectTemp8.put("ProgramId", (Object) model9.GetProgramId());
                        jsonArray.add(jsonObjectTemp8);
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                }
            } else if (o.getClass().getName() == DataConvertEditChannelLockModel.class.getName()) {
                if (responseStyle == 1003) {
                    int num4 = 0;
                    boolean isFirstTime2 = true;
                    Iterator<?> it10 = models.iterator();
                    while (it10.hasNext()) {
                        DataConvertEditChannelLockModel lockModel = (DataConvertEditChannelLockModel) it10.next();
                        JSONObject jsonObjectTemp9 = new JSONObject(true);
                        if (isFirstTime2) {
                            jsonObjectTemp9.put("TvState", (Object) new StringBuilder(String.valueOf(lockModel.getmChannelType())).toString());
                            isFirstTime2 = false;
                        }
                        jsonObjectTemp9.put("ProgramId", (Object) lockModel.getProgramId());
                        jsonArray.add(jsonObjectTemp9);
                        num4++;
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                    jSONObject.put("TotalNum", (Object) new StringBuilder(String.valueOf(num4)).toString());
                }
            } else if (o.getClass().getName() == DataConvertTimeModel.class.getName()) {
                if (responseStyle == 1021 || responseStyle == 1022 || responseStyle == 1023 || responseStyle == 1020) {
                    Iterator<?> it11 = models.iterator();
                    while (it11.hasNext()) {
                        DataConvertTimeModel model10 = (DataConvertTimeModel) it11.next();
                        JSONObject jsonObjectTemp10 = new JSONObject(true);
                        jsonObjectTemp10.put("TimerIndex", (Object) new StringBuilder(String.valueOf(model10.GetTimerIndex())).toString());
                        jsonObjectTemp10.put("TimerProgramId", (Object) model10.getProgramId());
                        jsonObjectTemp10.put("TimerMonth", (Object) new StringBuilder(String.valueOf(model10.GetTimeMonth())).toString());
                        jsonObjectTemp10.put("TimerDay", (Object) new StringBuilder(String.valueOf(model10.GetTimeDay())).toString());
                        jsonObjectTemp10.put("TimerStartHour", (Object) new StringBuilder(String.valueOf(model10.GetStartHour())).toString());
                        jsonObjectTemp10.put("TimerStartMin", (Object) new StringBuilder(String.valueOf(model10.GetStartMin())).toString());
                        jsonObjectTemp10.put("TimerEndHour", (Object) new StringBuilder(String.valueOf(model10.GetEndHour())).toString());
                        jsonObjectTemp10.put("TimerEndMin", (Object) new StringBuilder(String.valueOf(model10.GetEndMin())).toString());
                        jsonObjectTemp10.put("TimerRepeat", (Object) new StringBuilder(String.valueOf(model10.GetTimerRepeat())).toString());
                        jsonObjectTemp10.put("TimerStatus", (Object) new StringBuilder(String.valueOf(model10.GetTimerStatus())).toString());
                        jsonObjectTemp10.put("TimerEventID", (Object) new StringBuilder(String.valueOf(model10.getEventId())).toString());
                        jsonArray.add(jsonObjectTemp10);
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                }
            } else if (o.getClass().getName() == DataConvertControlModel.class.getName()) {
                if (responseStyle == 1051) {
                    Iterator<?> it12 = models.iterator();
                    while (it12.hasNext()) {
                        DataConvertControlModel model11 = (DataConvertControlModel) it12.next();
                        JSONObject jsonObjectTemp11 = new JSONObject(true);
                        jsonObjectTemp11.put("PasswordLock", (Object) new StringBuilder(String.valueOf(model11.GetPswLockSwitch())).toString());
                        jsonObjectTemp11.put("ServiceLock", (Object) new StringBuilder(String.valueOf(model11.GetServiceLockSwitch())).toString());
                        jsonObjectTemp11.put("InstallLock", (Object) new StringBuilder(String.valueOf(model11.GetInstallLockSwitch())).toString());
                        jsonObjectTemp11.put("EditLock", (Object) new StringBuilder(String.valueOf(model11.GetEditChannelLockSwitch())).toString());
                        jsonObjectTemp11.put("SettingsLock", (Object) new StringBuilder(String.valueOf(model11.GetSettingsLockSwitch())).toString());
                        jsonObjectTemp11.put("NetworkLock", (Object) new StringBuilder(String.valueOf(model11.GetNetworkLockSwitch())).toString());
                        jsonObjectTemp11.put("AgeRating", (Object) new StringBuilder(String.valueOf(model11.GetAgeRatingSwitch())).toString());
                        jsonArray.add(jsonObjectTemp11);
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                } else if (responseStyle == 1052) {
                    jSONObject.put("OldPassword", (Object) ((DataConvertControlModel) models.get(0)).GetPassword());
                    jSONObject.put("NewPassword", (Object) ((DataConvertControlModel) models.get(1)).GetPassword());
                } else if (responseStyle == 1050) {
                    DataConvertControlModel model12 = (DataConvertControlModel) models.get(0);
                    jSONObject.put("SleepSwitch", (Object) new StringBuilder(String.valueOf(model12.getSleepSwitch())).toString());
                    if (model12.getSleepSwitch() == 1) {
                        jSONObject.put("SleepTime", (Object) new StringBuilder(String.valueOf(model12.getSleepTime())).toString());
                    }
                }
            } else if (o.getClass().getName() == DataConvertUpdateModel.class.getName()) {
                if (responseStyle == 1010) {
                    Iterator<?> it13 = models.iterator();
                    while (it13.hasNext()) {
                        DataConvertUpdateModel model13 = (DataConvertUpdateModel) it13.next();
                        JSONObject jsonObjectTemp12 = new JSONObject(true);
                        jsonObjectTemp12.put("ChannelFileLen", (Object) new StringBuilder(String.valueOf(model13.GetDataLen())).toString());
                        jsonArray.add(jsonObjectTemp12);
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                }
            } else if (o.getClass().getName() == DataConvertDebugModel.class.getName()) {
                if (responseStyle == 1054 || responseStyle == 9) {
                    Iterator<?> it14 = models.iterator();
                    while (it14.hasNext()) {
                        DataConvertDebugModel model14 = (DataConvertDebugModel) it14.next();
                        JSONObject jsonObjectTemp13 = new JSONObject(true);
                        jsonObjectTemp13.put("EnableDebug", (Object) new StringBuilder(String.valueOf(model14.getDebugValue())).toString());
                        jsonObjectTemp13.put("RequestDataFrom", (Object) new StringBuilder(String.valueOf(model14.getRequestDataFrom())).toString());
                        jsonObjectTemp13.put("RequestDataTo", (Object) new StringBuilder(String.valueOf(model14.getRequestDataTo())).toString());
                        jsonArray.add(jsonObjectTemp13);
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                }
            } else if (o.getClass().getName() == DataConvertRcuModel.class.getName()) {
                if (responseStyle == 1040) {
                    Iterator<?> it15 = models.iterator();
                    while (it15.hasNext()) {
                        DataConvertRcuModel model15 = (DataConvertRcuModel) it15.next();
                        JSONObject jsonObjectTemp14 = new JSONObject(true);
                        jsonObjectTemp14.put("KeyValue", (Object) new StringBuilder(String.valueOf(model15.getKeyValue())).toString());
                        jsonArray.add(jsonObjectTemp14);
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                }
            } else if (o.getClass().getName() == DataConvertFavorModel.class.getName()) {
                if (responseStyle == 1055) {
                    jSONObject.put("FavorRenamePos", (Object) new StringBuilder(String.valueOf(((DataConvertFavorModel) models.get(0)).GetFavorIndex())).toString());
                    jSONObject.put("FavorNewName", (Object) new StringBuilder(String.valueOf(((DataConvertFavorModel) models.get(0)).GetFavorName())).toString());
                    jSONObject.put("FavorGroupID", (Object) new StringBuilder(String.valueOf(((DataConvertFavorModel) models.get(0)).getFavorTypeID())).toString());
                }
            } else if (o.getClass().getName() == DataConvertChannelTypeModel.class.getName()) {
                if (responseStyle == 1007) {
                    jSONObject.put("IsFavList", (Object) new StringBuilder(String.valueOf(((DataConvertChannelTypeModel) models.get(0)).getIsFavList())).toString());
                    jSONObject.put("SelectListType", (Object) new StringBuilder(String.valueOf(((DataConvertChannelTypeModel) models.get(0)).getSelectListType())).toString());
                }
            } else if (o.getClass().getName() == DataConvertInputMethodModel.class.getName()) {
                if (responseStyle == 1059) {
                    jSONObject.put("KeyCode", (Object) new StringBuilder(String.valueOf(((DataConvertInputMethodModel) models.get(0)).getKeyCode())).toString());
                }
            } else if (o.getClass().getName() == DataConvertSortModel.class.getName()) {
                if (responseStyle == 1006) {
                    jSONObject.put("SortType", (Object) new StringBuilder(String.valueOf(((DataConvertSortModel) models.get(0)).getmSortType())).toString());
                    jSONObject.put("TvState", (Object) new StringBuilder(String.valueOf(((DataConvertSortModel) models.get(0)).getmTvState())).toString());
                }
            } else if (o.getClass().getName() == DataConvertOneDataModel.class.getName()) {
                jSONObject.put("data", (Object) ((DataConvertOneDataModel) models.get(0)).getData());
            } else if (o.getClass().getName() == DataConvertSatModel.class.getName()) {
                if (responseStyle == 1060) {
                    JSONObject jsonObjectTemp15 = new JSONObject(true);
                    jsonObjectTemp15.put("SatIndexSelected", (Object) new StringBuilder(String.valueOf(((DataConvertSatModel) models.get(0)).getmSatIndex())).toString());
                    jsonArray.add(jsonObjectTemp15);
                }
                jSONObject.put(ARRAY, (Object) jsonArray);
            } else if (o instanceof Map) {
                Iterator<?> it16 = models.iterator();
                while (it16.hasNext()) {
                    Map<String, String> map = (Map) it16.next();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        JSONObject jsonObjectTemp16 = new JSONObject(true);
                        jsonObjectTemp16.put(key, (Object) map.get(key));
                        jsonArray.add(jsonObjectTemp16);
                    }
                }
                jSONObject.put(ARRAY, (Object) jsonArray);
            } else if (o instanceof DataConvertChatMsgModel) {
                if (responseStyle == 1102) {
                    DataConvertChatMsgModel model16 = (DataConvertChatMsgModel) models.get(0);
                    jSONObject.put("Timestamp", (Object) Long.toString(model16.getTimestamp()));
                    jSONObject.put("Content", (Object) model16.getContent());
                }
            } else if (o instanceof GsChatSetting) {
                if (responseStyle == 1104) {
                    GsChatSetting model17 = (GsChatSetting) models.get(0);
                    jSONObject.put("ShowWindow", (Object) String.valueOf(model17.getSHowWindow()));
                    jSONObject.put("WindowSize", (Object) String.valueOf(model17.getWindowSize()));
                    jSONObject.put("WindowPosition", (Object) String.valueOf(model17.getWindowPosition()));
                    jSONObject.put("WindowTransparency", (Object) String.valueOf(model17.getWindowTransparency()));
                }
            } else if (o instanceof GsChatUser) {
                if (responseStyle == 1103) {
                    Iterator<?> it17 = models.iterator();
                    while (it17.hasNext()) {
                        GsChatUser user = (GsChatUser) it17.next();
                        JSONObject jsonObjectTemp17 = new JSONObject(true);
                        jsonObjectTemp17.put("USERID", (Object) Integer.toString(user.getUserID()));
                        jsonObjectTemp17.put("Username", (Object) user.getUsername());
                        if (user.getBlock()) {
                            jsonObjectTemp17.put("Action", (Object) "1");
                        } else {
                            jsonObjectTemp17.put("Action", (Object) "0");
                        }
                        jsonArray.add(jsonObjectTemp17);
                    }
                    jSONObject.put(ARRAY, (Object) jsonArray);
                }
            } else if (o instanceof DataConvertUsernameModel) {
                if (responseStyle == 1105) {
                    jSONObject.put("Username", (Object) String.valueOf(((DataConvertUsernameModel) models.get(0)).getUsername()));
                }
            } else if (o instanceof DataConvertSpectrumInfo) {
                DataConvertSpectrumInfo model18 = (DataConvertSpectrumInfo) models.get(0);
                if (responseStyle == 301) {
                    jSONObject.put(DataConvertSpectrumInfo.OSDLEN, (Object) String.valueOf(model18.getOsdLen()));
                }
            } else if (o instanceof DataConvertSpectrumSetting) {
                DataConvertSpectrumSetting model19 = (DataConvertSpectrumSetting) models.get(0);
                switch (responseStyle) {
                    case GlobalConstantValue.GMS_MSG_SPE_DO_SET_SPAN_AND_CENT_FRE /* 1301 */:
                        jSONObject.put(DataConvertSpectrumSetting.DVBS_SPE_CENT_FRE, (Object) String.valueOf(model19.getDvbsSpeCentFre()));
                        jSONObject.put(DataConvertSpectrumSetting.DVBS_SPE_SPEN, (Object) String.valueOf(model19.getDvbsSpeSpan()));
                        break;
                    case GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_VH /* 1302 */:
                        jSONObject.put(DataConvertSpectrumSetting.DVBS_SPE_V, (Object) String.valueOf(model19.getDvbsSpeV()));
                        break;
                    case GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_22K /* 1303 */:
                        jSONObject.put(DataConvertSpectrumSetting.DVBS_SPE_22K_ON, (Object) String.valueOf(model19.getDvbsSpe22kOn()));
                        break;
                    case GlobalConstantValue.GMS_MSG_SPE_DO_SET_STATE_DISEQC /* 1304 */:
                        jSONObject.put(DataConvertSpectrumSetting.DVBS_SPE_DESECQ, (Object) String.valueOf(model19.getDvbsSpeDesecq()));
                        break;
                    case GlobalConstantValue.GMS_MSG_SPE_DO_SET_REF /* 1305 */:
                        jSONObject.put(DataConvertSpectrumSetting.DVBS_SPE_REF, (Object) String.valueOf(model19.getDvbsSpeRef()));
                        break;
                }
            }
        }
        return jSONObject.toString();
    }
}
