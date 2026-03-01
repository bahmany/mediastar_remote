package mktvsmart.screen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.List;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class FindPlayerAndPlayChannel {
    private Context mContext;
    private Intent mIntent;
    private PlayByDesignatedPlayer mPlayChannel;

    public interface PlayByDesignatedPlayer {
        void designatedBuiltInPlayer(int i);

        void designatedExternalPlayer(int i, Intent intent);

        void playerNotExist();
    }

    public FindPlayerAndPlayChannel(Context mContext) {
        this.mContext = mContext;
    }

    public static String getRtspPipeFilePath(Context mContext) {
        String cacheDir = mContext.getFilesDir().getAbsolutePath();
        return String.valueOf(cacheDir) + ServiceReference.DELIMITER + mContext.getResources().getString(R.string.app_name) + ".ts";
    }

    public void selectPlayer(int position) {
        String settingPlayerPkgName = new EditPlayerSettingFile(this.mContext).getPlayerPkgName();
        if (settingPlayerPkgName.equals(GMScreenGlobalInfo.DEFAULT_BUILT_IN_PLAYER)) {
            this.mPlayChannel.designatedBuiltInPlayer(position);
            return;
        }
        List<PlayerModel> arrayList = new ParseThirdPlayer(this.mContext).queryAppInfo();
        boolean settingPlayerExist = new ParseThirdPlayer(this.mContext).containPlayerInfo(arrayList, settingPlayerPkgName);
        Uri uri = Uri.parse(getRtspPipeFilePath(this.mContext));
        if (settingPlayerExist) {
            this.mIntent = new Intent("android.intent.action.VIEW", uri);
            this.mIntent.setDataAndType(uri, "video/*");
            this.mIntent.setPackage(settingPlayerPkgName);
            this.mIntent.setFlags(268435456);
            this.mPlayChannel.designatedExternalPlayer(position, this.mIntent);
            return;
        }
        String defaultPlayer = GMScreenGlobalInfo.getDefaultPlayer();
        new EditPlayerSettingFile(this.mContext).setPlayerPkgName(defaultPlayer);
        this.mIntent = new Intent("android.intent.action.VIEW", uri);
        this.mIntent.setDataAndType(uri, "video/*");
        this.mIntent.setPackage(defaultPlayer);
        this.mIntent.setFlags(268435456);
        if (defaultPlayer.equals(GMScreenGlobalInfo.DEFAULT_BUILT_IN_PLAYER)) {
            this.mPlayChannel.designatedBuiltInPlayer(position);
        } else if (!new ParseThirdPlayer(this.mContext).containPlayerInfo(arrayList, defaultPlayer)) {
            this.mPlayChannel.playerNotExist();
        } else {
            this.mPlayChannel.designatedExternalPlayer(position, this.mIntent);
        }
    }

    public void implementPlayByDesignatedPlayer(PlayByDesignatedPlayer l) {
        this.mPlayChannel = l;
    }
}
