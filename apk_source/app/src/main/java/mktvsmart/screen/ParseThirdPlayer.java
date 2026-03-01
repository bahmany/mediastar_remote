package mktvsmart.screen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ParseThirdPlayer {
    private Context mContext;

    public ParseThirdPlayer(Context mContext) {
        this.mContext = mContext;
    }

    public List<PlayerModel> queryAppInfo() {
        List<PlayerModel> arrayList = new ArrayList<>();
        PackageManager pm = this.mContext.getPackageManager();
        Intent mainIntent = new Intent("android.intent.action.VIEW");
        mainIntent.setType("video/*");
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name;
            String pkgName = reInfo.activityInfo.packageName;
            String appLabel = (String) reInfo.loadLabel(pm);
            Drawable icon = reInfo.loadIcon(pm);
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(pkgName, activityName));
            if (!pkgName.equals("com.tencent.mobileqqi") && !pkgName.equals("com.tencent.mobileqq") && !pkgName.equals(this.mContext.getPackageName())) {
                PlayerModel thirdPlayermodel = new PlayerModel();
                thirdPlayermodel.setmActivityName(activityName);
                thirdPlayermodel.setmAppIcon(icon);
                thirdPlayermodel.setmAppLabel(appLabel);
                thirdPlayermodel.setmIntent(launchIntent);
                thirdPlayermodel.setmPkgName(pkgName);
                arrayList.add(thirdPlayermodel);
            }
        }
        return arrayList;
    }

    public PlayerModel getDefaultExternalPlayer() {
        PlayerModel player = new PlayerModel();
        player.setmAppLabel("MX Player");
        player.setmAppIcon(this.mContext.getResources().getDrawable(R.drawable.icon_mx_player));
        player.setmPkgName(GMScreenGlobalInfo.DEFAULT_EXTERNAL_PLAYER);
        return player;
    }

    public boolean containPlayerInfo(List<PlayerModel> thirdPlayerList, String playerPkgName) {
        for (PlayerModel model : thirdPlayerList) {
            if (model.getmPkgName().equals(playerPkgName)) {
                return true;
            }
        }
        return false;
    }

    public int getIndex(List<PlayerModel> thirdPlayerList, String playerPkgName) {
        for (int index = 0; index < thirdPlayerList.size(); index++) {
            if (thirdPlayerList.get(index).getmPkgName().equals(playerPkgName)) {
                return index;
            }
        }
        return 0;
    }
}
