package mktvsmart.screen.channel;

import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertChannelTypeModel;
import mktvsmart.screen.dataconvert.model.DataConvertSatModel;
import mktvsmart.screen.dataconvert.model.DataConvertTpModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.exception.ProgramNotFoundException;

/* loaded from: classes.dex */
public class ChannelData {
    private static ChannelData INSTANCE = null;
    private static final String TAG = "ChannelDate";
    private List<DataConvertChannelModel> mTvChannelList = new ArrayList();
    private List<DataConvertChannelModel> mRadioChannelList = new ArrayList();
    private List<DataConvertTpModel> mAllTpList = new ArrayList();
    private List<DataConvertSatModel> mAllSatList = new ArrayList();

    public static synchronized ChannelData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChannelData();
        }
        return INSTANCE;
    }

    private ChannelData() {
    }

    public void releaseData() {
        if (this.mTvChannelList != null) {
            this.mTvChannelList.clear();
        }
        if (this.mRadioChannelList != null) {
            this.mRadioChannelList.clear();
        }
        if (this.mAllTpList != null) {
            this.mAllTpList.clear();
        }
        if (this.mAllSatList != null) {
            this.mAllSatList.clear();
        }
    }

    private Object readResolve() {
        return getInstance();
    }

    public List<DataConvertChannelModel> getmTvChannelList() {
        return this.mTvChannelList;
    }

    public void setmTvChannelList(List<DataConvertChannelModel> mTvChannelList) {
        this.mTvChannelList = mTvChannelList;
    }

    public List<DataConvertChannelModel> getmRadioChannelList() {
        return this.mRadioChannelList;
    }

    public void setmRadioChannelList(List<DataConvertChannelModel> mRadioChannelList) {
        this.mRadioChannelList = mRadioChannelList;
    }

    public List<DataConvertTpModel> getmAllTpList() {
        return this.mAllTpList;
    }

    public void setmAllTpList(List<DataConvertTpModel> mAllTpList) {
        this.mAllTpList = mAllTpList;
    }

    public List<DataConvertSatModel> getmAllSatList() {
        return this.mAllSatList;
    }

    public void setmAllSatList(List<DataConvertSatModel> mAllSatList) {
        this.mAllSatList = mAllSatList;
    }

    private int getIndexByModel(DataConvertChannelModel channelModel, List<DataConvertChannelModel> channelList) {
        int ret = 0;
        if (channelList == null) {
            return 0;
        }
        int index = 0;
        while (true) {
            if (index >= channelList.size()) {
                break;
            }
            if (channelModel.GetProgramIndex() != channelList.get(index).GetProgramIndex()) {
                index++;
            } else {
                ret = index;
                break;
            }
        }
        return ret;
    }

    private void addChannelToRadioChannelList(DataConvertChannelModel channelModel, List<DataConvertChannelModel> radioChannelList) {
        if (channelModel.GetProgramIndex() == GMScreenGlobalInfo.getmMaxProgramNumber() - 1) {
            radioChannelList.clear();
            radioChannelList.add(channelModel);
        } else if (radioChannelList.size() > 0) {
            if (channelModel.GetProgramIndex() == radioChannelList.get(radioChannelList.size() - 1).GetProgramIndex() - 1) {
                radioChannelList.add(channelModel);
            } else if (channelModel.GetProgramIndex() >= radioChannelList.get(radioChannelList.size() - 1).GetProgramIndex()) {
                int index = getIndexByModel(channelModel, radioChannelList);
                radioChannelList.set(index, channelModel);
            }
        }
    }

    private void addChannelToTvChannelList(DataConvertChannelModel channelModel, List<DataConvertChannelModel> tvChannelList) {
        if (channelModel.GetProgramIndex() == 0) {
            tvChannelList.clear();
            tvChannelList.add(channelModel);
        } else if (tvChannelList.size() > 0) {
            if (channelModel.GetProgramIndex() == tvChannelList.get(tvChannelList.size() - 1).GetProgramIndex() + 1) {
                tvChannelList.add(channelModel);
            } else if (channelModel.GetProgramIndex() <= tvChannelList.get(tvChannelList.size() - 1).GetProgramIndex()) {
                int index = getIndexByModel(channelModel, tvChannelList);
                tvChannelList.set(index, channelModel);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<DataConvertChannelModel> initChannelListData(byte[] recvData) {
        DataParser parser = ParserFactory.getParser();
        List arrayList = new ArrayList();
        try {
            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
            arrayList = parser.parse(istream, 0);
        } catch (Exception e) {
            Log.i(TAG, "[ChannelDate] initChannelListData Exception");
            e.printStackTrace();
        }
        separateRadioAndTv(arrayList, this.mTvChannelList, this.mRadioChannelList);
        return arrayList;
    }

    public void initTpList(byte[] recvData) {
        DataParser parser = ParserFactory.getParser();
        try {
            InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
            this.mAllTpList = parser.parse(instream, 19);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSatList(byte[] recvData) {
        DataParser parser = ParserFactory.getParser();
        try {
            InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
            this.mAllSatList = parser.parse(instream, 18);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void separateRadioAndTv(List<DataConvertChannelModel> tvRadioChannelList, List<DataConvertChannelModel> tvChannelList, List<DataConvertChannelModel> radioChannelList) {
        if (tvRadioChannelList != null && tvRadioChannelList.size() != 0) {
            if (isRadioProgramNumOrderOfSmallestToLargest()) {
                if (tvRadioChannelList.get(0).GetProgramIndex() == 0) {
                    radioChannelList.clear();
                    tvChannelList.clear();
                }
                for (int i = 0; i < tvRadioChannelList.size(); i++) {
                    if (tvRadioChannelList.get(i).getChannelTpye() == 1) {
                        radioChannelList.add(tvRadioChannelList.get(i));
                    } else {
                        tvChannelList.add(tvRadioChannelList.get(i));
                    }
                }
                return;
            }
            for (int i2 = 0; i2 < tvRadioChannelList.size(); i2++) {
                DataConvertChannelModel channelModel = tvRadioChannelList.get(i2);
                if (channelModel.getChannelTpye() == 1) {
                    addChannelToRadioChannelList(channelModel, radioChannelList);
                } else {
                    addChannelToTvChannelList(channelModel, tvChannelList);
                }
            }
        }
    }

    private boolean isRadioProgramNumOrderOfSmallestToLargest() {
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 12:
            case 30:
            case 31:
            case 32:
            case 71:
            case 72:
            case 74:
                return true;
            default:
                return false;
        }
    }

    public int getTotalProgramNum() {
        return this.mTvChannelList.size() + this.mRadioChannelList.size();
    }

    public List<DataConvertChannelModel> getChannelListByTvRadioType() {
        new ArrayList();
        if (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type() == 0) {
            List<DataConvertChannelModel> channelList = this.mTvChannelList;
            return channelList;
        }
        List<DataConvertChannelModel> channelList2 = this.mRadioChannelList;
        return channelList2;
    }

    public void channelListOfTvOrRadioChanged(List<DataConvertChannelModel> channelList) {
        if (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type() == 0) {
            this.mTvChannelList = channelList;
        } else {
            this.mRadioChannelList = channelList;
        }
    }

    private List<DataConvertChannelModel> GetChannelListByCHListType(List<DataConvertChannelModel> allChannelList, int channelListType) {
        if (allChannelList == null) {
            return null;
        }
        boolean bFavChannel = false;
        if (DataConvertChannelTypeModel.getCurrent_channel_tv_radio_type() == 0) {
            if (channelListType > 3) {
                bFavChannel = true;
            }
        } else if (channelListType > 2) {
            bFavChannel = true;
        }
        List<DataConvertChannelModel> retrunChannelList = new ArrayList<>();
        if (bFavChannel) {
            for (int index = 0; index < allChannelList.size(); index++) {
                DataConvertChannelModel tempChannel = allChannelList.get(index);
                if (isChannelBelongToSelectSat(tempChannel) && tempChannel.mfavGroupIDs.contains(Integer.valueOf(channelListType - 4))) {
                    retrunChannelList.add(tempChannel);
                }
            }
            return retrunChannelList;
        }
        for (int index2 = 0; index2 < allChannelList.size(); index2++) {
            boolean needAddToNewChannelList = false;
            DataConvertChannelModel tempChannel2 = allChannelList.get(index2);
            if (isChannelBelongToSelectSat(tempChannel2)) {
                switch (channelListType) {
                    case 0:
                        needAddToNewChannelList = true;
                        break;
                    case 1:
                        if (tempChannel2.GetIsProgramScramble() != 1) {
                            needAddToNewChannelList = true;
                            break;
                        }
                        break;
                    case 2:
                        if (tempChannel2.GetIsProgramScramble() == 1) {
                            needAddToNewChannelList = true;
                            break;
                        }
                        break;
                    case 3:
                        if (tempChannel2.getIsProgramHd() == 1) {
                            needAddToNewChannelList = true;
                            break;
                        }
                        break;
                }
                if (needAddToNewChannelList) {
                    retrunChannelList.add(tempChannel2);
                }
            }
        }
        return retrunChannelList;
    }

    private boolean isChannelBelongToSelectSat(DataConvertChannelModel channel) {
        if (GMScreenGlobalInfo.getCurStbInfo().getmSatEnable() != 1 || GMScreenGlobalInfo.getmSatIndexSelected() == GMScreenGlobalInfo.getIndexOfAllSat() || Integer.parseInt(GetSatSubStringByPrgoramId(channel.GetProgramId())) == GMScreenGlobalInfo.getmSatIndexSelected()) {
            return true;
        }
        return false;
    }

    public void clearTVRadioProgramList() {
        this.mTvChannelList.clear();
        this.mRadioChannelList.clear();
    }

    public List<DataConvertChannelModel> getChannelListByProgramType(List<DataConvertChannelModel> curChannelList, int programType) {
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 30:
            case 31:
            case 32:
            case 71:
            case 72:
            case 74:
                return GetChannelListByCHListType(curChannelList, programType);
            default:
                if (curChannelList == null || programType >= 12) {
                    return null;
                }
                int favGroup = programType - 4;
                List<DataConvertChannelModel> retrunChannelList = new ArrayList<>();
                for (int index = 0; index < curChannelList.size(); index++) {
                    boolean needAddToNewChannelList = false;
                    DataConvertChannelModel tempChannel = curChannelList.get(index);
                    if (isChannelBelongToSelectSat(tempChannel)) {
                        switch (programType) {
                            case 0:
                                needAddToNewChannelList = true;
                                break;
                            case 1:
                                if (tempChannel.GetIsProgramScramble() != 1) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 2:
                                if (tempChannel.GetIsProgramScramble() == 1) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 3:
                                if (tempChannel.getIsProgramHd() == 1) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 4:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 5:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 6:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 7:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 8:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 9:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 10:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                            case 11:
                                if ((tempChannel.GetFavMark() & (1 << favGroup)) > 0) {
                                    needAddToNewChannelList = true;
                                    break;
                                }
                                break;
                        }
                        if (needAddToNewChannelList) {
                            retrunChannelList.add(tempChannel);
                        }
                    }
                }
                return retrunChannelList;
        }
    }

    public static String GetSatSubStringByPrgoramId(String ProgramId) {
        return !IsProgramIdValid(ProgramId) ? "" : ProgramId.substring(0, 4);
    }

    public static boolean IsProgramIdValid(String ProgramId) {
        return ProgramId.length() == 14;
    }

    public static String GetTpSubStringByPrgoramId(String ProgramId) {
        return !IsProgramIdValid(ProgramId) ? "" : ProgramId.substring(4, 9);
    }

    public static String GetSatTpSubStringByPrgoramId(String ProgramId) {
        return !IsProgramIdValid(ProgramId) ? "" : ProgramId.substring(0, 9);
    }

    public static String GetProgSubStringByPrgoramId(String ProgramId) throws NumberFormatException {
        if (!IsProgramIdValid(ProgramId)) {
            return "";
        }
        int programId = Integer.parseInt(ProgramId.substring(9, 14));
        return new StringBuilder(String.valueOf(programId)).toString();
    }

    public boolean canSat2ipChannelPlay(DataConvertChannelModel playingProgram, DataConvertChannelModel sat2ipChannel) {
        Boolean canSat2ipPlay = true;
        if (playingProgram == null || sat2ipChannel == null) {
            return false;
        }
        if (playingProgram.getIsTuner2() == sat2ipChannel.getIsTuner2()) {
            if (GetSatTpSubStringByPrgoramId(sat2ipChannel.GetProgramId()).equals(GetSatTpSubStringByPrgoramId(playingProgram.GetProgramId()))) {
                canSat2ipPlay = true;
            } else {
                canSat2ipPlay = false;
            }
        }
        return canSat2ipPlay.booleanValue();
    }

    public DataConvertChannelModel getCurrentPlayingProgram() throws ProgramNotFoundException {
        DataConvertChannelModel currentPlayingProg = null;
        for (int index = 0; index < getTotalProgramNum(); index++) {
            currentPlayingProg = getProgramByIndex(index);
            if (currentPlayingProg.getIsPlaying() == 1) {
                break;
            }
        }
        return currentPlayingProg;
    }

    public DataConvertChannelModel getProgramByIndex(int index) {
        if (index >= this.mTvChannelList.size()) {
            DataConvertChannelModel tempChannelModel = this.mRadioChannelList.get(index - this.mTvChannelList.size());
            return tempChannelModel;
        }
        DataConvertChannelModel tempChannelModel2 = this.mTvChannelList.get(index);
        return tempChannelModel2;
    }

    public DataConvertChannelModel getProgramByProgramId(String programId) throws ProgramNotFoundException {
        DataConvertChannelModel tempChannelModel = null;
        for (int index = 0; index < getTotalProgramNum(); index++) {
            tempChannelModel = getProgramByIndex(index);
            if (tempChannelModel.GetProgramId().equals(programId)) {
                break;
            }
        }
        if (programId == null || tempChannelModel == null) {
            throw new ProgramNotFoundException("Not found the program: " + programId);
        }
        return tempChannelModel;
    }

    public int getIndexByProgIdInCurTvRadioProgList(String progId) {
        int size = getChannelListByTvRadioType().size();
        List<DataConvertChannelModel> channelList = getChannelListByTvRadioType();
        int index = 0;
        while (index < size && !progId.equals(channelList.get(index).GetProgramId())) {
            index++;
        }
        if (index == size) {
            return -1;
        }
        return index;
    }

    public static List<DataConvertSatModel> getSatList(List<DataConvertSatModel> allSatList, List<DataConvertChannelModel> curTypeChannelList, String str) throws NumberFormatException {
        List<DataConvertSatModel> satList = new ArrayList<>();
        if (curTypeChannelList == null) {
            return null;
        }
        DataConvertSatModel satModel = new DataConvertSatModel();
        satModel.setmSatIndex(GMScreenGlobalInfo.getIndexOfAllSat());
        satModel.setmSatName(str);
        satList.add(satModel);
        for (DataConvertChannelModel channelModel : curTypeChannelList) {
            int satIndex = Integer.parseInt(GetSatSubStringByPrgoramId(channelModel.GetProgramId()));
            int index = 0;
            while (index < satList.size() && satIndex != satList.get(index).getmSatIndex()) {
                index++;
            }
            if (index == satList.size()) {
                Iterator<DataConvertSatModel> it = allSatList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        DataConvertSatModel satModel2 = it.next();
                        if (satModel2.getmSatIndex() == satIndex) {
                            DataConvertSatModel model = new DataConvertSatModel();
                            model.setmSatName(satModel2.getmSatName());
                            model.setmSatIndex(satIndex);
                            model.setmSatAngle(satModel2.getmSatAngle());
                            model.setmSatDir(satModel2.getmSatDir());
                            satList.add(model);
                            break;
                        }
                    }
                }
            }
        }
        return satList;
    }

    public static List<DataConvertChannelModel> getSameTpListByProg(DataConvertChannelModel channelModel, List<DataConvertChannelModel> channelList) {
        List<DataConvertChannelModel> sameTpList = new ArrayList<>();
        for (DataConvertChannelModel model : channelList) {
            if (GetSatTpSubStringByPrgoramId(model.GetProgramId()).equals(GetSatTpSubStringByPrgoramId(channelModel.GetProgramId()))) {
                sameTpList.add(model);
            }
        }
        return sameTpList;
    }
}
