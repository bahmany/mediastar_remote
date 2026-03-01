package mktvsmart.screen.dataconvert.parser;

import android.util.Xml;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.io.InputStream;
import java.io.StringWriter;
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
import mktvsmart.screen.vlc.TranscodeConstants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: classes.dex */
public class XmlParser implements DataParser {
    @Override // mktvsmart.screen.dataconvert.parser.DataParser
    public List<?> parse(InputStream is, int type) throws Exception {
        switch (type) {
            case 0:
                List<DataConvertChannelModel> models = null;
                DataConvertChannelModel model = null;
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(is, "UTF-8");
                for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                    switch (eventType) {
                        case 0:
                            models = new ArrayList<>();
                            break;
                        case 2:
                            if (parser.getName().equals("parm")) {
                                model = new DataConvertChannelModel();
                                break;
                            } else if (parser.getName().equals("SatIndexSelected")) {
                                parser.next();
                                GMScreenGlobalInfo.setmSatIndexSelected(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("ProgramId")) {
                                parser.next();
                                model.SetProgramId(parser.getText());
                                break;
                            } else if (parser.getName().equals("ProgramName")) {
                                parser.next();
                                model.setProgramName(parser.getText());
                                break;
                            } else if (parser.getName().equals("ProgramIndex")) {
                                parser.next();
                                model.SetProgramIndex(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("SatName")) {
                                parser.next();
                                model.SetSatName(parser.getText());
                                break;
                            } else if (parser.getName().equals("ProgramType")) {
                                parser.next();
                                model.SetIsProgramScramble(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("IsProgramHD")) {
                                parser.next();
                                model.setIsProgramHd(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("FavMark")) {
                                parser.next();
                                model.SetFavMark(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("LockMark")) {
                                parser.next();
                                model.setLockMark(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("HaveEPG")) {
                                parser.next();
                                model.SetHaveEPG(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("IsPlaying")) {
                                parser.next();
                                model.setIsPlaying(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("WillBePlayed")) {
                                parser.next();
                                model.setmWillBePlayed(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("ChannelType")) {
                                parser.next();
                                model.setChannelTpye(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("Frequency")) {
                                parser.next();
                                model.setFreq(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("Polar")) {
                                char pol = 'h';
                                parser.next();
                                String str = parser.getText();
                                if (str.equals("0")) {
                                    pol = 'h';
                                } else if (str.equals("1")) {
                                    pol = 'v';
                                } else if (str.equals(ContentTree.AUDIO_ID)) {
                                    pol = 'l';
                                } else if (str.equals(ContentTree.IMAGE_ID)) {
                                    pol = 'r';
                                }
                                model.setPol(pol);
                                break;
                            } else if (parser.getName().equals("ModulationSystem")) {
                                parser.next();
                                model.setModulationSystem(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("ModulationType")) {
                                parser.next();
                                model.setModulationType(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("RollOff")) {
                                parser.next();
                                model.setRollOff(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("PilotTones")) {
                                parser.next();
                                model.setPilotTones(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("SymbolRate")) {
                                parser.next();
                                model.setSymRate(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("Fec")) {
                                parser.next();
                                model.setFec(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("VideoPid")) {
                                parser.next();
                                model.setVideoPid(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("AudioPid")) {
                                parser.next();
                                model.setAudioPid(parser.getText());
                                break;
                            } else if (parser.getName().equals("TtxPid")) {
                                parser.next();
                                model.setTtxPid(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("SubPid")) {
                                parser.next();
                                model.setSubPid(parser.getText());
                                break;
                            } else if (parser.getName().equals("PmtPid")) {
                                parser.next();
                                model.setPmtPid(Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("IsTuner2")) {
                                parser.next();
                                model.setIsTuner2((short) Integer.parseInt(parser.getText()));
                                break;
                            } else if (parser.getName().equals("FavorGroupID")) {
                                parser.next();
                                String[] groupIDs = parser.getText().split(":");
                                if (groupIDs != null) {
                                    for (String str2 : groupIDs) {
                                        model.mfavGroupIDs.add(Integer.valueOf(Integer.parseInt(str2)));
                                    }
                                    break;
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        case 3:
                            if (parser.getName().equals("parm")) {
                                if (GMScreenGlobalInfo.isSdsOpen() == 0 || model.getIsTuner2() == 0) {
                                    models.add(model);
                                }
                                model = null;
                                break;
                            } else {
                                break;
                            }
                            break;
                    }
                }
                return models;
            case 1:
            case 4:
                List<DataConvertTimeModel> models2 = null;
                DataConvertTimeModel model2 = null;
                XmlPullParser parser2 = Xml.newPullParser();
                parser2.setInput(is, "UTF-8");
                for (int eventType2 = parser2.getEventType(); eventType2 != 1; eventType2 = parser2.next()) {
                    switch (eventType2) {
                        case 0:
                            models2 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser2.getName().equals("Confirm")) {
                                parser2.next();
                                DataConvertTimeModel.isConfirm = Integer.parseInt(parser2.getText());
                                break;
                            } else if (parser2.getName().equals("parm")) {
                                model2 = new DataConvertTimeModel();
                                break;
                            } else if (parser2.getName().equals("TimerProgramName")) {
                                parser2.next();
                                model2.SetTimeProgramName(parser2.getText());
                                break;
                            } else if (parser2.getName().equals("TimerProgramSatTpId")) {
                                parser2.next();
                                model2.setProgramId(parser2.getText());
                                break;
                            } else if (parser2.getName().equals("TimerMonth")) {
                                parser2.next();
                                model2.SetTimeMonth(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerDay")) {
                                parser2.next();
                                model2.SetTimeDay(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerStartHour")) {
                                parser2.next();
                                model2.SetStartHour(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerStartMin")) {
                                parser2.next();
                                model2.SetStartMin(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerEndHour")) {
                                parser2.next();
                                model2.SetEndHour(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerEndMin")) {
                                parser2.next();
                                model2.SetEndMin(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerRepeat")) {
                                parser2.next();
                                model2.SetTimerRepeat(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerStatus")) {
                                parser2.next();
                                model2.SetTimerStatus(Integer.parseInt(parser2.getText()));
                                break;
                            } else if (parser2.getName().equals("TimerEventID")) {
                                parser2.next();
                                model2.setEventId(Integer.parseInt(parser2.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser2.getName().equals("parm")) {
                                models2.add(model2);
                                model2 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models2;
            case 2:
                List<DataConvertControlModel> models3 = null;
                DataConvertControlModel model3 = null;
                XmlPullParser parser3 = Xml.newPullParser();
                parser3.setInput(is, "UTF-8");
                for (int eventType3 = parser3.getEventType(); eventType3 != 1; eventType3 = parser3.next()) {
                    switch (eventType3) {
                        case 0:
                            models3 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser3.getName().equals("parm")) {
                                model3 = new DataConvertControlModel();
                                model3.SetPowerOff(-1);
                                break;
                            } else if (parser3.getName().equals("Password")) {
                                parser3.next();
                                model3.SetPassword(parser3.getText());
                                break;
                            } else if (parser3.getName().equals("PasswordLock")) {
                                parser3.next();
                                model3.SetPswLockSwitch(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("ServiceLock")) {
                                parser3.next();
                                model3.SetServiceLockSwitch(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("InstallationLock")) {
                                parser3.next();
                                model3.SetInstallLockSwitch(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("EditChannelLock")) {
                                parser3.next();
                                model3.SetEditChannelLockSwitch(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("SettingsLock")) {
                                parser3.next();
                                model3.SetSettingsLockSwitch(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("NetworkLock")) {
                                parser3.next();
                                model3.SetNetworkLockSwitch(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("AgeRating")) {
                                parser3.next();
                                model3.SetAgeRatingSwitch(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("IsLockScreen")) {
                                parser3.next();
                                model3.SetIsLockScreen(Integer.parseInt(parser3.getText()));
                                break;
                            } else if (parser3.getName().equals("PowerMode")) {
                                parser3.next();
                                model3.SetPowerOff(Integer.parseInt(parser3.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser3.getName().equals("parm")) {
                                models3.add(model3);
                                model3 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models3;
            case 3:
                List<DataConvertUpdateModel> models4 = null;
                DataConvertUpdateModel model4 = null;
                XmlPullParser parser4 = Xml.newPullParser();
                parser4.setInput(is, "UTF-8");
                for (int eventType4 = parser4.getEventType(); eventType4 != 1; eventType4 = parser4.next()) {
                    switch (eventType4) {
                        case 0:
                            models4 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser4.getName().equals("parm")) {
                                model4 = new DataConvertUpdateModel();
                                break;
                            } else if (parser4.getName().equals("CustomerId")) {
                                parser4.next();
                                model4.SetCustomerId(Integer.parseInt(parser4.getText()));
                                break;
                            } else if (parser4.getName().equals("HardwareId")) {
                                parser4.next();
                                model4.SetModelId(Integer.parseInt(parser4.getText()));
                                break;
                            } else if (parser4.getName().equals("VersionId")) {
                                parser4.next();
                                model4.SetVersionId(Integer.parseInt(parser4.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser4.getName().equals("parm")) {
                                models4.add(model4);
                                model4 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models4;
            case 5:
            default:
                return null;
            case 6:
                int programEpgCurrentDayIndex = 0;
                int eventCurrentLanguageIndex = 0;
                List<GsEPGTableChannel> programEpgList = null;
                GsEPGTableChannel programEpg = null;
                GsEPGEvent epgEvent = null;
                XmlPullParser parser5 = Xml.newPullParser();
                parser5.setInput(is, "UTF-8");
                for (int eventType5 = parser5.getEventType(); eventType5 != 1; eventType5 = parser5.next()) {
                    switch (eventType5) {
                        case 0:
                            programEpgList = new ArrayList<>();
                            break;
                        case 2:
                            if (parser5.getName().equals("prog_epg")) {
                                programEpg = new GsEPGTableChannel();
                                break;
                            } else if (parser5.getName().equals("prog_no")) {
                                parser5.next();
                                programEpg.setProgNo(Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("original_net_id")) {
                                parser5.next();
                                programEpg.setOriginalNetworkID(Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("transport_stream_id")) {
                                parser5.next();
                                programEpg.setTransportStreamID(Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("today_date")) {
                                parser5.next();
                                programEpg.setTodayDate((byte) Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("current_epg_time")) {
                                parser5.next();
                                programEpg.setCurrentEpgTime(Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("prog_day_epg")) {
                                break;
                            } else if (parser5.getName().equals("event_valid_num")) {
                                parser5.next();
                                programEpg.setArrayEventFieldByIndex(programEpgCurrentDayIndex, Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("epg_event")) {
                                epgEvent = new GsEPGEvent();
                                break;
                            } else if (parser5.getName().equals("event_start_time")) {
                                parser5.next();
                                epgEvent.setStartTime(parser5.getText());
                                break;
                            } else if (parser5.getName().equals("event_end_time")) {
                                parser5.next();
                                epgEvent.setEndTime(parser5.getText());
                                break;
                            } else if (parser5.getName().equals("event_age_rating")) {
                                parser5.next();
                                epgEvent.setAgeRating(Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_timer_type")) {
                                parser5.next();
                                epgEvent.setEpgTimerType(Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_total_language")) {
                                parser5.next();
                                epgEvent.setTotalEpgLanguage(Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_titile_lang_code")) {
                                parser5.next();
                                epgEvent.setTitleLanCode(eventCurrentLanguageIndex, Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_sub_titile_lang_code")) {
                                parser5.next();
                                epgEvent.setSubtitleLanCode(eventCurrentLanguageIndex, Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_desc_lang_code")) {
                                parser5.next();
                                epgEvent.setDescLanCode(eventCurrentLanguageIndex, Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_titile_len")) {
                                parser5.next();
                                epgEvent.setTitleLen(eventCurrentLanguageIndex, Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_sub_titile_len")) {
                                parser5.next();
                                epgEvent.setSubtitleLen(eventCurrentLanguageIndex, Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_desc_len")) {
                                parser5.next();
                                epgEvent.setDescLen(eventCurrentLanguageIndex, Integer.parseInt(parser5.getText()));
                                break;
                            } else if (parser5.getName().equals("event_title")) {
                                parser5.next();
                                epgEvent.setEventTitle(eventCurrentLanguageIndex, parser5.getText());
                                break;
                            } else if (parser5.getName().equals("event_sub_title")) {
                                parser5.next();
                                epgEvent.setEventSubTitle(eventCurrentLanguageIndex, parser5.getText());
                                break;
                            } else if (parser5.getName().equals("event_desc")) {
                                parser5.next();
                                epgEvent.setEventDesc(eventCurrentLanguageIndex, parser5.getText());
                                eventCurrentLanguageIndex++;
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser5.getName().equals("prog_epg")) {
                                programEpgList.add(programEpg);
                                programEpg = null;
                                break;
                            } else if (parser5.getName().equals("prog_day_epg")) {
                                programEpgCurrentDayIndex++;
                                break;
                            } else if (parser5.getName().equals("epg_event")) {
                                programEpg.getEpgDayByIndex(programEpgCurrentDayIndex).getArrayList().add(epgEvent);
                                eventCurrentLanguageIndex = 0;
                                epgEvent = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return programEpgList;
            case 7:
                List<DataConvertTimeModel> models5 = null;
                DataConvertTimeModel model5 = null;
                XmlPullParser parser6 = Xml.newPullParser();
                parser6.setInput(is, "UTF-8");
                for (int eventType6 = parser6.getEventType(); eventType6 != 1; eventType6 = parser6.next()) {
                    switch (eventType6) {
                        case 0:
                            models5 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser6.getName().equals("parm")) {
                                model5 = new DataConvertTimeModel();
                                break;
                            } else if (parser6.getName().equals("Confirm")) {
                                parser6.next();
                                DataConvertTimeModel.isConfirm = Integer.parseInt(parser6.getText());
                                break;
                            } else if (parser6.getName().equals("TimerIndex")) {
                                parser6.next();
                                model5.SetTimerIndex(Integer.parseInt(parser6.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser6.getName().equals("parm")) {
                                models5.add(model5);
                                model5 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models5;
            case 8:
                List<DataConvertChannelModel> models6 = null;
                DataConvertChannelModel model6 = null;
                XmlPullParser parser7 = Xml.newPullParser();
                parser7.setInput(is, "UTF-8");
                for (int eventType7 = parser7.getEventType(); eventType7 != 1; eventType7 = parser7.next()) {
                    switch (eventType7) {
                        case 0:
                            models6 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser7.getName().equals("parm")) {
                                model6 = new DataConvertChannelModel();
                                break;
                            } else if (parser7.getName().equals("LockedChannelIndex")) {
                                parser7.next();
                                model6.SetProgramId(parser7.getText());
                                break;
                            } else if (parser7.getName().equals("TVState")) {
                                parser7.next();
                                model6.setChannelTpye(Integer.parseInt(parser7.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser7.getName().equals("parm")) {
                                models6.add(model6);
                                model6 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models6;
            case 9:
                List<DataConvertTimeModel> models7 = null;
                DataConvertTimeModel model7 = null;
                XmlPullParser parser8 = Xml.newPullParser();
                parser8.setInput(is, "UTF-8");
                for (int eventType8 = parser8.getEventType(); eventType8 != 1; eventType8 = parser8.next()) {
                    switch (eventType8) {
                        case 0:
                            models7 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser8.getName().equals("StbMonth")) {
                                parser8.next();
                                DataConvertTimeModel.stbMonth = Integer.parseInt(parser8.getText());
                                break;
                            } else if (parser8.getName().equals("StbDay")) {
                                parser8.next();
                                DataConvertTimeModel.stbDay = Integer.parseInt(parser8.getText());
                                break;
                            } else if (parser8.getName().equals("StbHour")) {
                                parser8.next();
                                DataConvertTimeModel.stbHour = Integer.parseInt(parser8.getText());
                                break;
                            } else if (parser8.getName().equals("StbMin")) {
                                parser8.next();
                                DataConvertTimeModel.stbMin = Integer.parseInt(parser8.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser8.getName().equals("parm")) {
                                models7.add(model7);
                                model7 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models7;
            case 10:
                List<DataConvertFavorModel> favorModels = null;
                DataConvertFavorModel model8 = null;
                XmlPullParser parser9 = Xml.newPullParser();
                parser9.setInput(is, "UTF-8");
                for (int eventType9 = parser9.getEventType(); eventType9 != 1; eventType9 = parser9.next()) {
                    switch (eventType9) {
                        case 0:
                            favorModels = new ArrayList<>();
                            break;
                        case 2:
                            if (parser9.getName().equals("favMaxNum")) {
                                parser9.next();
                                DataConvertFavorModel.favorNum = Integer.parseInt(parser9.getText());
                                break;
                            } else if (parser9.getName().equals("parm")) {
                                model8 = new DataConvertFavorModel();
                                break;
                            } else if (parser9.getName().equals("favorGroupName")) {
                                parser9.next();
                                model8.SetFavorName(parser9.getText());
                                break;
                            } else if (parser9.getName().equals("FavorGroupID")) {
                                parser9.next();
                                model8.setFavorTypeID(Integer.valueOf(parser9.getText()).intValue());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser9.getName().equals("parm")) {
                                favorModels.add(model8);
                                model8 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return favorModels;
            case 11:
                List<DataConvertControlModel> controlModels = null;
                DataConvertControlModel model9 = null;
                XmlPullParser parser10 = Xml.newPullParser();
                parser10.setInput(is, "UTF-8");
                for (int eventType10 = parser10.getEventType(); eventType10 != 1; eventType10 = parser10.next()) {
                    switch (eventType10) {
                        case 0:
                            controlModels = new ArrayList<>();
                            break;
                        case 2:
                            if (parser10.getName().equals("parm")) {
                                model9 = new DataConvertControlModel();
                                break;
                            } else if (parser10.getName().equals("SleepSwitch")) {
                                parser10.next();
                                model9.setSleepSwitch(Integer.parseInt(parser10.getText()));
                                break;
                            } else if (parser10.getName().equals("SleepTime")) {
                                parser10.next();
                                model9.setSleepTime(Integer.parseInt(parser10.getText()));
                                break;
                            } else if (parser10.getName().equals("ScreenLock")) {
                                parser10.next();
                                model9.SetIsLockScreen(Integer.parseInt(parser10.getText()));
                                break;
                            } else if (parser10.getName().equals("PowerMode")) {
                                parser10.next();
                                model9.SetPowerOff(Integer.parseInt(parser10.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser10.getName().equals("parm")) {
                                controlModels.add(model9);
                                model9 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return controlModels;
            case 12:
                List<DataConvertChannelTypeModel> models8 = null;
                DataConvertChannelTypeModel model10 = null;
                XmlPullParser parser11 = Xml.newPullParser();
                parser11.setInput(is, "UTF-8");
                for (int eventType11 = parser11.getEventType(); eventType11 != 1; eventType11 = parser11.next()) {
                    switch (eventType11) {
                        case 0:
                            models8 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser11.getName().equals("parm")) {
                                model10 = new DataConvertChannelTypeModel();
                                break;
                            } else if (parser11.getName().equals("CurChannelType")) {
                                parser11.next();
                                DataConvertChannelTypeModel.setCurrent_channel_tv_radio_type(Integer.parseInt(parser11.getText()));
                                break;
                            } else if (parser11.getName().equals("tv_radio_key_press")) {
                                parser11.next();
                                model10.setTvRadioKeyPress(Integer.parseInt(parser11.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser11.getName().equals("parm")) {
                                models8.add(model10);
                                model10 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models8;
            case 13:
                List<DataConvertSortModel> models9 = null;
                DataConvertSortModel model11 = null;
                XmlPullParser parser12 = Xml.newPullParser();
                parser12.setInput(is, "UTF-8");
                for (int eventType12 = parser12.getEventType(); eventType12 != 1; eventType12 = parser12.next()) {
                    switch (eventType12) {
                        case 0:
                            models9 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser12.getName().equals("parm")) {
                                model11 = new DataConvertSortModel();
                                break;
                            } else if (parser12.getName().equals("SortType")) {
                                parser12.next();
                                model11.setmSortType(Integer.parseInt(parser12.getText()));
                                break;
                            } else if (parser12.getName().equals("MacroFlag")) {
                                parser12.next();
                                model11.setmMacroFlag(Integer.parseInt(parser12.getText()));
                                break;
                            } else if (parser12.getName().equals("SortTypeList")) {
                                System.out.println("have SortTypeList");
                                parser12.next();
                                String[] list = parser12.getText().split(ClientInfo.SEPARATOR_BETWEEN_VARS);
                                if (list == null || list.length <= 0) {
                                    break;
                                } else {
                                    ArrayList<String> sortTypeList = new ArrayList<>();
                                    for (String str3 : list) {
                                        sortTypeList.add(str3);
                                    }
                                    model11.setSortTypeList(sortTypeList);
                                    break;
                                }
                            } else {
                                break;
                            }
                        case 3:
                            if (parser12.getName().equals("parm")) {
                                models9.add(model11);
                                model11 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models9;
            case 14:
                List<DataConvertStbInfoModel> stbInfoModels = null;
                DataConvertStbInfoModel model12 = null;
                XmlPullParser parser13 = Xml.newPullParser();
                parser13.setInput(is, "UTF-8");
                for (int eventType13 = parser13.getEventType(); eventType13 != 1; eventType13 = parser13.next()) {
                    switch (eventType13) {
                        case 0:
                            stbInfoModels = new ArrayList<>();
                            break;
                        case 2:
                            if (parser13.getName().equals("parm")) {
                                model12 = new DataConvertStbInfoModel();
                                break;
                            } else if (parser13.getName().equals("StbStatus")) {
                                parser13.next();
                                model12.setmStbStatus(Integer.parseInt(parser13.getText()));
                                break;
                            } else if (parser13.getName().equals("ProductName")) {
                                parser13.next();
                                model12.setmProductName(parser13.getText());
                                break;
                            } else if (parser13.getName().equals("SoftwareVersion")) {
                                parser13.next();
                                model12.setmSoftwareVersion(parser13.getText());
                                break;
                            } else if (parser13.getName().equals("SerialNumber")) {
                                parser13.next();
                                model12.setmSerialNumber(parser13.getText());
                                break;
                            } else if (parser13.getName().equals("ChannelNum")) {
                                parser13.next();
                                model12.setmChannelNum(Integer.parseInt(parser13.getText()));
                                break;
                            } else if (parser13.getName().equals("MaxNumOfPrograms")) {
                                parser13.next();
                                model12.setmMaxNumOfPrograms(Integer.parseInt(parser13.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser13.getName().equals("parm")) {
                                stbInfoModels.add(model12);
                                model12 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return stbInfoModels;
            case 15:
                XmlPullParser parser14 = Xml.newPullParser();
                parser14.setInput(is, "UTF-8");
                List<String> models10 = null;
                String model13 = null;
                for (int eventType14 = parser14.getEventType(); eventType14 != 1; eventType14 = parser14.next()) {
                    switch (eventType14) {
                        case 0:
                            models10 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser14.getName().equals("Data")) {
                                parser14.next();
                                model13 = new String(parser14.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser14.getName().equals("parm")) {
                                models10.add(model13);
                                model13 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models10;
            case 16:
                XmlPullParser parser15 = Xml.newPullParser();
                parser15.setInput(is, "UTF-8");
                List<Map<String, Object>> models11 = null;
                Map<String, Object> model14 = null;
                for (int eventType15 = parser15.getEventType(); eventType15 != 1; eventType15 = parser15.next()) {
                    switch (eventType15) {
                        case 0:
                            models11 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser15.getName().equals("success")) {
                                parser15.next();
                                model14 = new HashMap<>();
                                model14.put("success", Integer.valueOf(parser15.getText()));
                                break;
                            } else if (parser15.getName().equals("url")) {
                                parser15.next();
                                model14.put("url", parser15.getText());
                                break;
                            } else if (parser15.getName().equals("errormsg")) {
                                parser15.next();
                                model14.put("errormsg", parser15.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser15.getName().equals("parm")) {
                                models11.add(model14);
                                model14 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models11;
            case 17:
                XmlPullParser parser16 = Xml.newPullParser();
                parser16.setInput(is, "UTF-8");
                List<String> tempList = null;
                String temp = null;
                for (int eventType16 = parser16.getEventType(); eventType16 != 1; eventType16 = parser16.next()) {
                    switch (eventType16) {
                        case 0:
                            tempList = new ArrayList<>();
                            break;
                        case 2:
                            if (parser16.getName().equals("cur_channel_list_type")) {
                                parser16.next();
                                temp = new String(parser16.getText());
                                break;
                            } else if (parser16.getName().equals("max_password_num")) {
                                parser16.next();
                                GMScreenGlobalInfo.setmMaxPasswordNum(Integer.parseInt(parser16.getText()));
                                break;
                            } else if (parser16.getName().equals("cur_channel_type")) {
                                parser16.next();
                                DataConvertChannelTypeModel.setCurrent_channel_tv_radio_type(Integer.parseInt(parser16.getText()));
                                break;
                            } else if (parser16.getName().equals("is_support_pvr2ip_server")) {
                                parser16.next();
                                GMScreenGlobalInfo.setmPvr2ipServerSupport(Integer.parseInt(parser16.getText()));
                                System.out.println("GMScreenGlobalInfo.getmPvr2ipServerSupport()==" + GMScreenGlobalInfo.getmPvr2ipServerSupport());
                                break;
                            } else if (parser16.getName().equals("is_sds_open")) {
                                parser16.next();
                                GMScreenGlobalInfo.setSdsOpen(Integer.parseInt(parser16.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser16.getName().equals("parm")) {
                                tempList.add(temp);
                                temp = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return tempList;
            case 18:
                XmlPullParser parser17 = Xml.newPullParser();
                parser17.setInput(is, "UTF-8");
                List<DataConvertSatModel> models12 = null;
                DataConvertSatModel model15 = null;
                for (int eventType17 = parser17.getEventType(); eventType17 != 1; eventType17 = parser17.next()) {
                    switch (eventType17) {
                        case 0:
                            models12 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser17.getName().equals("parm")) {
                                model15 = new DataConvertSatModel();
                                break;
                            } else if (parser17.getName().equals("SatName")) {
                                parser17.next();
                                model15.setmSatName(parser17.getText());
                                break;
                            } else if (parser17.getName().equals("SatNo")) {
                                parser17.next();
                                model15.setmSatIndex(Integer.parseInt(parser17.getText()));
                                break;
                            } else if (parser17.getName().equals("SatAngle")) {
                                parser17.next();
                                model15.setmSatAngle(Integer.parseInt(parser17.getText()));
                                break;
                            } else if (parser17.getName().equals("SatDir")) {
                                parser17.next();
                                model15.setmSatDir(Integer.parseInt(parser17.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser17.getName().equals("parm")) {
                                models12.add(model15);
                                model15 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models12;
            case 19:
                XmlPullParser parser18 = Xml.newPullParser();
                parser18.setInput(is, "UTF-8");
                List<DataConvertTpModel> models13 = null;
                DataConvertTpModel model16 = null;
                for (int eventType18 = parser18.getEventType(); eventType18 != 1; eventType18 = parser18.next()) {
                    switch (eventType18) {
                        case 0:
                            models13 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser18.getName().equals("parm")) {
                                model16 = new DataConvertTpModel();
                                break;
                            } else if (parser18.getName().equals("TpIndex")) {
                                parser18.next();
                                model16.setTpIndex(Integer.parseInt(parser18.getText()));
                                break;
                            } else if (parser18.getName().equals("SatIndex")) {
                                parser18.next();
                                model16.setSatIndex(Integer.parseInt(parser18.getText()));
                                break;
                            } else if (parser18.getName().equals("SystemRate")) {
                                parser18.next();
                                model16.setSymRate(Integer.parseInt(parser18.getText()));
                                break;
                            } else if (parser18.getName().equals("Pol")) {
                                char pol2 = 'h';
                                parser18.next();
                                String str4 = parser18.getText();
                                if (str4.equals("0")) {
                                    pol2 = 'h';
                                } else if (str4.equals("1")) {
                                    pol2 = 'v';
                                } else if (str4.equals(ContentTree.AUDIO_ID)) {
                                    pol2 = 'l';
                                } else if (str4.equals(ContentTree.IMAGE_ID)) {
                                    pol2 = 'r';
                                }
                                model16.setPol(pol2);
                                break;
                            } else if (parser18.getName().equals("Fec")) {
                                parser18.next();
                                model16.setFec(Integer.parseInt(parser18.getText()));
                                break;
                            } else if (parser18.getName().equals("Freq")) {
                                parser18.next();
                                model16.setFreq(Integer.parseInt(parser18.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser18.getName().equals("parm")) {
                                models13.add(model16);
                                model16 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models13;
            case 20:
                XmlPullParser parser19 = Xml.newPullParser();
                parser19.setInput(is, "UTF-8");
                List<DataConvertPvrInfoModel> models14 = null;
                DataConvertPvrInfoModel model17 = null;
                for (int eventType19 = parser19.getEventType(); eventType19 != 1; eventType19 = parser19.next()) {
                    switch (eventType19) {
                        case 0:
                            models14 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser19.getName().equals("parm")) {
                                model17 = new DataConvertPvrInfoModel();
                                break;
                            } else if (parser19.getName().equals("pvr_name")) {
                                parser19.next();
                                model17.setProgramName(parser19.getText());
                                break;
                            } else if (parser19.getName().equals("pvr_uid")) {
                                parser19.next();
                                model17.setmPvrId(Integer.parseInt(parser19.getText()));
                                break;
                            } else if (parser19.getName().equals("pvr_duration")) {
                                parser19.next();
                                model17.setmPvrDuration(parser19.getText());
                                break;
                            } else if (parser19.getName().equals("Pvr_time")) {
                                parser19.next();
                                model17.setmPvrTime(parser19.getText());
                                break;
                            } else if (parser19.getName().equals("pvr_type")) {
                                parser19.next();
                                model17.setmPvrType(Integer.parseInt(parser19.getText()));
                                break;
                            } else if (parser19.getName().equals("crypto")) {
                                parser19.next();
                                model17.setmPvrCrypto(Integer.parseInt(parser19.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser19.getName().equals("parm")) {
                                models14.add(model17);
                                model17 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models14;
            case 21:
                XmlPullParser parser20 = Xml.newPullParser();
                parser20.setInput(is, "UTF-8");
                List<GsChatSetting> models15 = null;
                GsChatSetting model18 = null;
                for (int eventType20 = parser20.getEventType(); eventType20 != 1; eventType20 = parser20.next()) {
                    switch (eventType20) {
                        case 0:
                            models15 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser20.getName().equals("parm")) {
                                model18 = GsChatSetting.getInstance();
                            }
                            if (parser20.getName().equals("MySN")) {
                                parser20.next();
                                model18.setSerialNumber(parser20.getText());
                                break;
                            } else if (parser20.getName().equals("MyUsername")) {
                                parser20.next();
                                model18.setUsername(parser20.getText());
                                break;
                            } else if (parser20.getName().equals("USERID")) {
                                parser20.next();
                                model18.setUserId(Integer.parseInt(parser20.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser20.getName().equals("parm")) {
                                models15.add(model18);
                                model18 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models15;
            case 22:
                XmlPullParser parser21 = Xml.newPullParser();
                parser21.setInput(is, "UTF-8");
                List<GsChatRoomInfo> models16 = null;
                GsChatRoomInfo model19 = null;
                GsChatUser chatUser = null;
                for (int eventType21 = parser21.getEventType(); eventType21 != 1; eventType21 = parser21.next()) {
                    switch (eventType21) {
                        case 0:
                            models16 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser21.getName().equals("parm")) {
                                model19 = new GsChatRoomInfo();
                            }
                            if (parser21.getName().equals("EventTitle")) {
                                parser21.next();
                                model19.setEventTitle(parser21.getText());
                                break;
                            } else if (parser21.getName().equals("OnlineUserNum")) {
                                parser21.next();
                                model19.setOnlineNum(Integer.parseInt(parser21.getText()));
                                break;
                            } else if (parser21.getName().equals("RoomId")) {
                                parser21.next();
                                model19.setRoomID(Integer.parseInt(parser21.getText()));
                                break;
                            } else if (parser21.getName().equals("UserInfo")) {
                                chatUser = new GsChatUser();
                                break;
                            } else if (parser21.getName().equals("USERID")) {
                                parser21.next();
                                chatUser.setUserID(Integer.parseInt(parser21.getText()));
                                break;
                            } else if (parser21.getName().equals("Username")) {
                                parser21.next();
                                chatUser.setUsername(parser21.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser21.getName().equals("parm")) {
                                models16.add(model19);
                                model19 = null;
                                break;
                            } else if (parser21.getName().equals("UserInfo")) {
                                model19.getUserList().add(chatUser);
                                chatUser = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models16;
            case 23:
                XmlPullParser parser22 = Xml.newPullParser();
                parser22.setInput(is, "UTF-8");
                List<DataConvertChatMsgModel> models17 = null;
                DataConvertChatMsgModel model20 = null;
                for (int eventType22 = parser22.getEventType(); eventType22 != 1; eventType22 = parser22.next()) {
                    switch (eventType22) {
                        case 0:
                            models17 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser22.getName().equals("parm")) {
                                model20 = new DataConvertChatMsgModel();
                                break;
                            } else if (parser22.getName().equals("Timestamp")) {
                                parser22.next();
                                model20.setTimestamp(Long.parseLong(parser22.getText()));
                                break;
                            } else if (parser22.getName().equals("USERID")) {
                                parser22.next();
                                int userId = Integer.parseInt(parser22.getText());
                                model20.setUserID(userId);
                                if (userId == GsChatSetting.getInstance().getUserId()) {
                                    model20.setMsgType(1);
                                    break;
                                } else {
                                    model20.setMsgType(0);
                                    break;
                                }
                            } else if (parser22.getName().equals("Username")) {
                                parser22.next();
                                model20.setUsername(parser22.getText());
                                break;
                            } else if (parser22.getName().equals("Content")) {
                                parser22.next();
                                model20.setContent(parser22.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser22.getName().equals("parm")) {
                                models17.add(model20);
                                model20 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models17;
            case 24:
                XmlPullParser parser23 = Xml.newPullParser();
                parser23.setInput(is, "UTF-8");
                List<GsChatUser> models18 = null;
                GsChatUser model21 = null;
                for (int eventType23 = parser23.getEventType(); eventType23 != 1; eventType23 = parser23.next()) {
                    switch (eventType23) {
                        case 0:
                            models18 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser23.getName().equals("parm")) {
                                model21 = new GsChatUser();
                                break;
                            } else if (parser23.getName().equals("USERID")) {
                                parser23.next();
                                model21.setUserID(Integer.parseInt(parser23.getText()));
                                break;
                            } else if (parser23.getName().equals("Username")) {
                                parser23.next();
                                model21.setUsername(parser23.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser23.getName().equals("parm")) {
                                models18.add(model21);
                                model21 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models18;
            case 25:
                XmlPullParser parser24 = Xml.newPullParser();
                parser24.setInput(is, "UTF-8");
                List<GsChatSetting> models19 = null;
                GsChatSetting model22 = null;
                for (int eventType24 = parser24.getEventType(); eventType24 != 1; eventType24 = parser24.next()) {
                    switch (eventType24) {
                        case 0:
                            models19 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser24.getName().equals("parm")) {
                                model22 = GsChatSetting.getInstance();
                                break;
                            } else if (parser24.getName().equals("ShowWindow")) {
                                parser24.next();
                                model22.setShowWindow(Integer.parseInt(parser24.getText()));
                                break;
                            } else if (parser24.getName().equals("WindowSize")) {
                                parser24.next();
                                model22.setWindowSize(Integer.parseInt(parser24.getText()));
                                break;
                            } else if (parser24.getName().equals("WindowPosition")) {
                                parser24.next();
                                model22.setWindowPosition(Integer.parseInt(parser24.getText()));
                                break;
                            } else if (parser24.getName().equals("WindowTransparency")) {
                                parser24.next();
                                model22.setWindowTransparency(Integer.parseInt(parser24.getText()));
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser24.getName().equals("parm")) {
                                models19.add(model22);
                                model22 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models19;
            case 26:
                XmlPullParser parser25 = Xml.newPullParser();
                parser25.setInput(is, "UTF-8");
                List<DataConvertGChatChannelInfoModel> models20 = null;
                DataConvertGChatChannelInfoModel model23 = null;
                for (int eventType25 = parser25.getEventType(); eventType25 != 1; eventType25 = parser25.next()) {
                    switch (eventType25) {
                        case 0:
                            models20 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser25.getName().equals("parm")) {
                                model23 = new DataConvertGChatChannelInfoModel();
                                break;
                            } else if (parser25.getName().equals("Angle")) {
                                parser25.next();
                                model23.setSatAngle(parser25.getText());
                                break;
                            } else if (parser25.getName().equals("Tp")) {
                                parser25.next();
                                model23.setTp(Integer.parseInt(parser25.getText()));
                                break;
                            } else if (parser25.getName().equals("ServiceId")) {
                                parser25.next();
                                model23.setServiceId(Integer.parseInt(parser25.getText()));
                                break;
                            } else if (parser25.getName().equals("EPG")) {
                                parser25.next();
                                model23.setEpg(parser25.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser25.getName().equals("parm")) {
                                models20.add(model23);
                                model23 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models20;
            case 27:
                XmlPullParser parser26 = Xml.newPullParser();
                parser26.setInput(is, "UTF-8");
                List<DataConvertUsernameModel> models21 = null;
                DataConvertUsernameModel model24 = null;
                for (int eventType26 = parser26.getEventType(); eventType26 != 1; eventType26 = parser26.next()) {
                    switch (eventType26) {
                        case 0:
                            models21 = new ArrayList<>();
                            break;
                        case 2:
                            if (parser26.getName().equals("parm")) {
                                model24 = new DataConvertUsernameModel();
                                break;
                            } else if (parser26.getName().equals("Username")) {
                                parser26.next();
                                model24.setUsername(parser26.getText());
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (parser26.getName().equals("parm")) {
                                models21.add(model24);
                                model24 = null;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                return models21;
        }
    }

    public byte[] IntToByteArray(int index) {
        byte[] buff = new byte[8];
        for (int i = 0; i < 8; i++) {
            buff[i] = (byte) ((index >> ((7 - i) * 8)) & 65535);
        }
        return buff;
    }

    @Override // mktvsmart.screen.dataconvert.parser.DataParser
    public String serialize(List<?> models, int responseStyle) throws Exception {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "Command");
        if (models == null) {
            serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
        } else {
            Object o = models.get(0);
            if (o.getClass().getName() == DataConvertChannelModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                switch (responseStyle) {
                    case 0:
                        serializer.startTag("", "parm");
                        serializer.startTag("", "FromIndex");
                        serializer.text(new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).GetProgramIndex())).toString());
                        serializer.endTag("", "FromIndex");
                        serializer.startTag("", "ToIndex");
                        serializer.text(new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(1)).GetProgramIndex())).toString());
                        serializer.endTag("", "ToIndex");
                        serializer.endTag("", "parm");
                        break;
                    case 5:
                        Iterator<?> it = models.iterator();
                        while (it.hasNext()) {
                            DataConvertChannelModel model = (DataConvertChannelModel) it.next();
                            serializer.startTag("", "parm");
                            serializer.startTag("", "ProgramId");
                            serializer.text(model.GetProgramId());
                            serializer.endTag("", "ProgramId");
                            serializer.endTag("", "parm");
                        }
                        break;
                    case 104:
                        DataConvertChannelModel model2 = (DataConvertChannelModel) models.get(0);
                        serializer.startTag("", "parm");
                        serializer.startTag("", "ProgramId");
                        serializer.text(model2.GetProgramId());
                        serializer.endTag("", "ProgramId");
                        serializer.endTag("", "parm");
                        break;
                    case 1000:
                    case GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY /* 1009 */:
                        Iterator<?> it2 = models.iterator();
                        while (it2.hasNext()) {
                            DataConvertChannelModel model3 = (DataConvertChannelModel) it2.next();
                            serializer.startTag("", "parm");
                            serializer.startTag("", "TvState");
                            serializer.text(new StringBuilder(String.valueOf(model3.getChannelTpye())).toString());
                            serializer.endTag("", "TvState");
                            serializer.startTag("", "ProgramId");
                            serializer.text(model3.GetProgramId());
                            serializer.endTag("", "ProgramId");
                            int platformId = GMScreenGlobalInfo.getCurStbPlatform();
                            switch (platformId) {
                                case 32:
                                case 71:
                                case 72:
                                case 74:
                                    if (responseStyle == 1009) {
                                        serializer.startTag("", "iResolutionRatio");
                                        serializer.text(new StringBuilder(String.valueOf(TranscodeConstants.iCurResolution)).toString());
                                        serializer.endTag("", "iResolutionRatio");
                                        serializer.startTag("", "iBitrate");
                                        serializer.text(new StringBuilder(String.valueOf(TranscodeConstants.iCurBitrate)).toString());
                                        serializer.endTag("", "iBitrate");
                                        break;
                                    } else {
                                        break;
                                    }
                            }
                            serializer.endTag("", "parm");
                        }
                        break;
                    case 1001:
                        Iterator<?> it3 = models.iterator();
                        while (it3.hasNext()) {
                            DataConvertChannelModel model4 = (DataConvertChannelModel) it3.next();
                            serializer.startTag("", "parm");
                            serializer.startTag("", "TvState");
                            serializer.text(new StringBuilder(String.valueOf(model4.getChannelTpye())).toString());
                            serializer.endTag("", "TvState");
                            serializer.startTag("", "ProgramId");
                            serializer.text(model4.GetProgramId());
                            serializer.endTag("", "ProgramId");
                            serializer.startTag("", "ProgramName");
                            serializer.text(model4.getProgramName());
                            serializer.endTag("", "ProgramName");
                            serializer.endTag("", "parm");
                        }
                        break;
                    case 1002:
                        int num = 0;
                        serializer.startTag("", "TvState");
                        serializer.text(new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).getChannelTpye())).toString());
                        serializer.endTag("", "TvState");
                        Iterator<?> it4 = models.iterator();
                        while (it4.hasNext()) {
                            DataConvertChannelModel model5 = (DataConvertChannelModel) it4.next();
                            serializer.startTag("", "parm");
                            serializer.startTag("", "ProgramId");
                            serializer.text(model5.GetProgramId());
                            serializer.endTag("", "ProgramId");
                            num++;
                            serializer.endTag("", "parm");
                        }
                        serializer.startTag("", "TotalNum");
                        serializer.text(new StringBuilder(String.valueOf(num)).toString());
                        serializer.endTag("", "TotalNum");
                        break;
                    case GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK /* 1004 */:
                        int num2 = 0;
                        serializer.startTag("", "TvState");
                        serializer.text(new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).getChannelTpye())).toString());
                        serializer.endTag("", "TvState");
                        serializer.startTag("", "FavMark");
                        serializer.text(new StringBuilder(String.valueOf(((DataConvertChannelModel) models.get(0)).GetFavMark())).toString());
                        serializer.endTag("", "FavMark");
                        String mfavGroupIDs = "";
                        Iterator<Integer> it5 = ((DataConvertChannelModel) models.get(0)).mfavGroupIDs.iterator();
                        while (it5.hasNext()) {
                            int iFavGroupID = it5.next().intValue();
                            mfavGroupIDs = String.valueOf(mfavGroupIDs) + iFavGroupID + ":";
                        }
                        if (mfavGroupIDs.length() >= 0) {
                            serializer.startTag("", "FavorGroupID");
                            serializer.text(mfavGroupIDs);
                            serializer.endTag("", "FavorGroupID");
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
                                    DataConvertChannelModel model6 = (DataConvertChannelModel) it6.next();
                                    serializer.startTag("", "ProgramId");
                                    serializer.text(model6.GetProgramId());
                                    serializer.endTag("", "ProgramId");
                                }
                                break;
                            default:
                                Iterator<?> it7 = models.iterator();
                                while (it7.hasNext()) {
                                    DataConvertChannelModel model7 = (DataConvertChannelModel) it7.next();
                                    serializer.startTag("", "ProgramId");
                                    serializer.text(model7.GetProgramId());
                                    serializer.endTag("", "ProgramId");
                                    num2++;
                                }
                                serializer.startTag("", "TotalNum");
                                serializer.text(new StringBuilder(String.valueOf(num2)).toString());
                                serializer.endTag("", "TotalNum");
                                break;
                        }
                    case GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE /* 1005 */:
                        boolean isFirstTime = true;
                        int num3 = 0;
                        Iterator<?> it8 = models.iterator();
                        while (it8.hasNext()) {
                            DataConvertChannelModel model8 = (DataConvertChannelModel) it8.next();
                            serializer.startTag("", "parm");
                            if (isFirstTime) {
                                serializer.startTag("", "TvState");
                                serializer.text(new StringBuilder(String.valueOf(model8.getChannelTpye())).toString());
                                serializer.endTag("", "TvState");
                                serializer.startTag("", "MoveToPosition");
                                serializer.text(model8.getMoveToPosition());
                                serializer.endTag("", "MoveToPosition");
                                isFirstTime = false;
                            }
                            serializer.startTag("", "ProgramId");
                            serializer.text(new StringBuilder(String.valueOf(model8.GetProgramId())).toString());
                            serializer.endTag("", "ProgramId");
                            num3++;
                            serializer.endTag("", "parm");
                        }
                        serializer.startTag("", "TotalNum");
                        serializer.text(new StringBuilder(String.valueOf(num3)).toString());
                        serializer.endTag("", "TotalNum");
                        break;
                    case GlobalConstantValue.GMS_MSG_GCHAT_DO_START /* 1100 */:
                        DataConvertChannelModel model9 = (DataConvertChannelModel) models.get(0);
                        serializer.startTag("", "parm");
                        serializer.startTag("", "TvState");
                        serializer.text(String.valueOf(model9.getChannelTpye()));
                        serializer.endTag("", "TvState");
                        serializer.startTag("", "ProgramId");
                        serializer.text(model9.GetProgramId());
                        serializer.endTag("", "ProgramId");
                        serializer.endTag("", "parm");
                        break;
                }
            } else if (o.getClass().getName() == DataConvertFavChannelModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1011) {
                    serializer.startTag("", "TvState");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertFavChannelModel) models.get(0)).getChannelTpye())).toString());
                    serializer.endTag("", "TvState");
                    serializer.startTag("", "SelectListType");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertFavChannelModel) models.get(0)).getSelectListType())).toString());
                    serializer.endTag("", "SelectListType");
                    Iterator<?> it9 = models.iterator();
                    while (it9.hasNext()) {
                        DataConvertFavChannelModel model10 = (DataConvertFavChannelModel) it9.next();
                        serializer.startTag("", "parm");
                        serializer.startTag("", "ProgramId");
                        serializer.text(model10.GetProgramId());
                        serializer.endTag("", "ProgramId");
                        serializer.endTag("", "parm");
                    }
                }
            } else if (o.getClass().getName() == DataConvertEditChannelLockModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1003) {
                    int num4 = 0;
                    boolean isFirstTime2 = true;
                    serializer.startTag("", "parm");
                    Iterator<?> it10 = models.iterator();
                    while (it10.hasNext()) {
                        DataConvertEditChannelLockModel lockModel = (DataConvertEditChannelLockModel) it10.next();
                        if (isFirstTime2) {
                            serializer.startTag("", "TvState");
                            serializer.text(new StringBuilder(String.valueOf(lockModel.getmChannelType())).toString());
                            serializer.endTag("", "TvState");
                            isFirstTime2 = false;
                        }
                        serializer.startTag("", "ProgramId");
                        serializer.text(lockModel.getProgramId());
                        serializer.endTag("", "ProgramId");
                        num4++;
                    }
                    serializer.startTag("", "TotalNum");
                    serializer.text(new StringBuilder(String.valueOf(num4)).toString());
                    serializer.endTag("", "TotalNum");
                    serializer.endTag("", "parm");
                }
            } else if (o.getClass().getName() == DataConvertTimeModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1021 || responseStyle == 1022 || responseStyle == 1023 || responseStyle == 1020) {
                    Iterator<?> it11 = models.iterator();
                    while (it11.hasNext()) {
                        DataConvertTimeModel model11 = (DataConvertTimeModel) it11.next();
                        serializer.startTag("", "parm");
                        serializer.startTag("", "TimerIndex");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetTimerIndex())).toString());
                        serializer.endTag("", "TimerIndex");
                        serializer.startTag("", "TimerProgramId");
                        serializer.text(model11.getProgramId());
                        serializer.endTag("", "TimerProgramId");
                        serializer.startTag("", "TimerMonth");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetTimeMonth())).toString());
                        serializer.endTag("", "TimerMonth");
                        serializer.startTag("", "TimerDay");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetTimeDay())).toString());
                        serializer.endTag("", "TimerDay");
                        serializer.startTag("", "TimerStartHour");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetStartHour())).toString());
                        serializer.endTag("", "TimerStartHour");
                        serializer.startTag("", "TimerStartMin");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetStartMin())).toString());
                        serializer.endTag("", "TimerStartMin");
                        serializer.startTag("", "TimerEndHour");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetEndHour())).toString());
                        serializer.endTag("", "TimerEndHour");
                        serializer.startTag("", "TimerEndMin");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetEndMin())).toString());
                        serializer.endTag("", "TimerEndMin");
                        serializer.startTag("", "TimerRepeat");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetTimerRepeat())).toString());
                        serializer.endTag("", "TimerRepeat");
                        serializer.startTag("", "TimerStatus");
                        serializer.text(new StringBuilder(String.valueOf(model11.GetTimerStatus())).toString());
                        serializer.endTag("", "TimerStatus");
                        serializer.startTag("", "TimerEventID");
                        serializer.text(new StringBuilder(String.valueOf(model11.getEventId())).toString());
                        serializer.endTag("", "TimerEventID");
                        serializer.endTag("", "parm");
                    }
                }
            } else if (o.getClass().getName() == DataConvertControlModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1051) {
                    Iterator<?> it12 = models.iterator();
                    while (it12.hasNext()) {
                        DataConvertControlModel model12 = (DataConvertControlModel) it12.next();
                        serializer.startTag("", "parm");
                        serializer.startTag("", "PasswordLock");
                        serializer.text(new StringBuilder(String.valueOf(model12.GetPswLockSwitch())).toString());
                        serializer.endTag("", "PasswordLock");
                        serializer.startTag("", "ServiceLock");
                        serializer.text(new StringBuilder(String.valueOf(model12.GetServiceLockSwitch())).toString());
                        serializer.endTag("", "ServiceLock");
                        serializer.startTag("", "InstallLock");
                        serializer.text(new StringBuilder(String.valueOf(model12.GetInstallLockSwitch())).toString());
                        serializer.endTag("", "InstallLock");
                        serializer.startTag("", "EditLock");
                        serializer.text(new StringBuilder(String.valueOf(model12.GetEditChannelLockSwitch())).toString());
                        serializer.endTag("", "EditLock");
                        serializer.startTag("", "SettingsLock");
                        serializer.text(new StringBuilder(String.valueOf(model12.GetSettingsLockSwitch())).toString());
                        serializer.endTag("", "SettingsLock");
                        serializer.startTag("", "NetworkLock");
                        serializer.text(new StringBuilder(String.valueOf(model12.GetNetworkLockSwitch())).toString());
                        serializer.endTag("", "NetworkLock");
                        serializer.startTag("", "AgeRating");
                        serializer.text(new StringBuilder(String.valueOf(model12.GetAgeRatingSwitch())).toString());
                        serializer.endTag("", "AgeRating");
                        serializer.endTag("", "parm");
                    }
                } else if (responseStyle == 1052) {
                    serializer.startTag("", "parm");
                    serializer.startTag("", "OldPassword");
                    serializer.text(((DataConvertControlModel) models.get(0)).GetPassword());
                    serializer.endTag("", "OldPassword");
                    serializer.endTag("", "parm");
                    serializer.startTag("", "parm");
                    serializer.startTag("", "NewPassword");
                    serializer.text(((DataConvertControlModel) models.get(1)).GetPassword());
                    serializer.endTag("", "NewPassword");
                    serializer.endTag("", "parm");
                } else if (responseStyle == 1050) {
                    DataConvertControlModel model13 = (DataConvertControlModel) models.get(0);
                    serializer.startTag("", "parm");
                    serializer.startTag("", "SleepSwitch");
                    serializer.text(new StringBuilder(String.valueOf(model13.getSleepSwitch())).toString());
                    serializer.endTag("", "SleepSwitch");
                    if (model13.getSleepSwitch() == 1) {
                        serializer.startTag("", "SleepTime");
                        serializer.text(new StringBuilder(String.valueOf(model13.getSleepTime())).toString());
                        serializer.endTag("", "SleepTime");
                    }
                    serializer.endTag("", "parm");
                }
            } else if (o.getClass().getName() == DataConvertUpdateModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1010) {
                    Iterator<?> it13 = models.iterator();
                    while (it13.hasNext()) {
                        DataConvertUpdateModel model14 = (DataConvertUpdateModel) it13.next();
                        serializer.startTag("", "ChannelFileLen");
                        serializer.text(new StringBuilder(String.valueOf(model14.GetDataLen())).toString());
                        serializer.endTag("", "ChannelFileLen");
                    }
                }
            } else if (o.getClass().getName() == DataConvertDebugModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1054 || responseStyle == 9) {
                    Iterator<?> it14 = models.iterator();
                    while (it14.hasNext()) {
                        DataConvertDebugModel model15 = (DataConvertDebugModel) it14.next();
                        serializer.startTag("", "EnableDebug");
                        serializer.text(new StringBuilder(String.valueOf(model15.getDebugValue())).toString());
                        serializer.endTag("", "EnableDebug");
                        serializer.startTag("", "RequestDataFrom");
                        serializer.text(new StringBuilder(String.valueOf(model15.getRequestDataFrom())).toString());
                        serializer.endTag("", "RequestDataFrom");
                        serializer.startTag("", "RequestDataTo");
                        serializer.text(new StringBuilder(String.valueOf(model15.getRequestDataTo())).toString());
                        serializer.endTag("", "RequestDataTo");
                    }
                }
            } else if (o.getClass().getName() == DataConvertRcuModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1040) {
                    Iterator<?> it15 = models.iterator();
                    while (it15.hasNext()) {
                        DataConvertRcuModel model16 = (DataConvertRcuModel) it15.next();
                        serializer.startTag("", "KeyValue");
                        serializer.text(new StringBuilder(String.valueOf(model16.getKeyValue())).toString());
                        serializer.endTag("", "KeyValue");
                    }
                }
            } else if (o.getClass().getName() == DataConvertFavorModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1055) {
                    serializer.startTag("", "FavorRenamePos");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertFavorModel) models.get(0)).GetFavorIndex())).toString());
                    serializer.endTag("", "FavorRenamePos");
                    serializer.startTag("", "FavorNewName");
                    serializer.text(((DataConvertFavorModel) models.get(0)).GetFavorName());
                    serializer.endTag("", "FavorNewName");
                    serializer.startTag("", "FavorGroupID");
                    serializer.text(new StringBuilder().append(((DataConvertFavorModel) models.get(0)).getFavorTypeID()).toString());
                    serializer.endTag("", "FavorGroupID");
                }
            } else if (o.getClass().getName() == DataConvertChannelTypeModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1007) {
                    serializer.startTag("", "IsFavList");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertChannelTypeModel) models.get(0)).getIsFavList())).toString());
                    serializer.endTag("", "IsFavList");
                    serializer.startTag("", "SelectListType");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertChannelTypeModel) models.get(0)).getSelectListType())).toString());
                    serializer.endTag("", "SelectListType");
                }
            } else if (o.getClass().getName() == DataConvertInputMethodModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1059) {
                    serializer.startTag("", "KeyCode");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertInputMethodModel) models.get(0)).getKeyCode())).toString());
                    serializer.endTag("", "KeyCode");
                }
            } else if (o.getClass().getName() == DataConvertSortModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1006) {
                    serializer.startTag("", "SortType");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertSortModel) models.get(0)).getmSortType())).toString());
                    serializer.endTag("", "SortType");
                    serializer.startTag("", "TvState");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertSortModel) models.get(0)).getmTvState())).toString());
                    serializer.endTag("", "TvState");
                }
            } else if (o.getClass().getName() == DataConvertOneDataModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                serializer.startTag("", "data");
                serializer.text(((DataConvertOneDataModel) models.get(0)).getData());
                serializer.endTag("", "data");
            } else if (o.getClass().getName() == DataConvertSatModel.class.getName()) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                if (responseStyle == 1060) {
                    serializer.startTag("", "SatIndexSelected");
                    serializer.text(new StringBuilder(String.valueOf(((DataConvertSatModel) models.get(0)).getmSatIndex())).toString());
                    serializer.endTag("", "SatIndexSelected");
                }
            } else if (o instanceof Map) {
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                Iterator<?> it16 = models.iterator();
                while (it16.hasNext()) {
                    Map<String, String> map = (Map) it16.next();
                    Set<String> keys = map.keySet();
                    for (String key : keys) {
                        serializer.startTag("", key);
                        serializer.text(map.get(key));
                        serializer.endTag("", key);
                    }
                }
            } else if (o instanceof DataConvertChatMsgModel) {
                if (responseStyle == 1102) {
                    DataConvertChatMsgModel model17 = (DataConvertChatMsgModel) models.get(0);
                    serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                    serializer.startTag("", "parm");
                    serializer.startTag("", "Timestamp");
                    serializer.text(Long.toString(model17.getTimestamp()));
                    serializer.endTag("", "Timestamp");
                    serializer.startTag("", "Content");
                    serializer.text(model17.getContent());
                    serializer.endTag("", "Content");
                    serializer.endTag("", "parm");
                }
            } else if (o instanceof GsChatSetting) {
                if (responseStyle == 1104) {
                    GsChatSetting model18 = (GsChatSetting) models.get(0);
                    serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                    serializer.startTag("", "parm");
                    serializer.startTag("", "ShowWindow");
                    serializer.text(String.valueOf(model18.getSHowWindow()));
                    serializer.endTag("", "ShowWindow");
                    serializer.startTag("", "WindowSize");
                    serializer.text(String.valueOf(model18.getWindowSize()));
                    serializer.endTag("", "WindowSize");
                    serializer.startTag("", "WindowPosition");
                    serializer.text(String.valueOf(model18.getWindowPosition()));
                    serializer.endTag("", "WindowPosition");
                    serializer.startTag("", "WindowTransparency");
                    serializer.text(String.valueOf(model18.getWindowTransparency()));
                    serializer.endTag("", "WindowTransparency");
                    serializer.endTag("", "parm");
                }
            } else if (o instanceof GsChatUser) {
                if (responseStyle == 1103) {
                    serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                    Iterator<?> it17 = models.iterator();
                    while (it17.hasNext()) {
                        GsChatUser user = (GsChatUser) it17.next();
                        serializer.startTag("", "parm");
                        serializer.startTag("", "USERID");
                        serializer.text(Integer.toString(user.getUserID()));
                        serializer.endTag("", "USERID");
                        serializer.startTag("", "Username");
                        serializer.text(user.getUsername());
                        serializer.endTag("", "Username");
                        serializer.startTag("", "Action");
                        if (user.getBlock()) {
                            serializer.text("1");
                        } else {
                            serializer.text("0");
                        }
                        serializer.endTag("", "Action");
                        serializer.endTag("", "parm");
                    }
                }
            } else if ((o instanceof DataConvertUsernameModel) && responseStyle == 1105) {
                DataConvertUsernameModel model19 = (DataConvertUsernameModel) models.get(0);
                serializer.attribute("", "request", new StringBuilder(String.valueOf(responseStyle)).toString());
                serializer.startTag("", "parm");
                serializer.startTag("", "Username");
                serializer.text(model19.getUsername());
                serializer.endTag("", "Username");
                serializer.endTag("", "parm");
            }
        }
        serializer.endTag("", "Command");
        serializer.endDocument();
        return writer.toString();
    }
}
