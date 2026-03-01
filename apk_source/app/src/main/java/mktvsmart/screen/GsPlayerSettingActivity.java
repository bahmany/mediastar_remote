package mktvsmart.screen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GsPlayerSettingActivity extends Activity implements View.OnClickListener {
    private String mSettingPlayerPkgName;
    private final String BUILT_IN_PLAYER_NAME = "Built-in Player";
    private List<PlayerModel> mPlayerList = new ArrayList();
    private List<View> mCheckBoxList = new ArrayList();
    private boolean mSettingPlayerExist = false;
    private View.OnClickListener mCheckBoxListener = new View.OnClickListener() { // from class: mktvsmart.screen.GsPlayerSettingActivity.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            for (View view : GsPlayerSettingActivity.this.mCheckBoxList) {
                if (((String) view.getTag()).equals((String) v.getTag())) {
                    new EditPlayerSettingFile(GsPlayerSettingActivity.this).setPlayerPkgName(v.getTag().toString());
                    ((ImageView) view.findViewById(R.id.check_box)).setImageResource(R.drawable.icon_player_select);
                } else {
                    ((ImageView) view.findViewById(R.id.check_box)).setImageResource(R.drawable.icon_player_unselect);
                }
            }
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        this.mPlayerList = new ParseThirdPlayer(this).queryAppInfo();
        addBuiltInPlayerAndMXPlayer(this.mPlayerList);
        this.mSettingPlayerPkgName = new EditPlayerSettingFile(this).getPlayerPkgName();
        this.mSettingPlayerExist = new ParseThirdPlayer(this).containPlayerInfo(this.mPlayerList, this.mSettingPlayerPkgName);
    }

    private void initView() {
        setContentView(R.layout.activity_player_setting);
        LinearLayout scrollSettingLayout = (LinearLayout) findViewById(R.id.scroll_setting_item);
        Button backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        for (PlayerModel model : this.mPlayerList) {
            View subItemView = View.inflate(this, R.layout.layout_player_item, null);
            ImageView playerIconImage = (ImageView) subItemView.findViewById(R.id.player_icon_image);
            TextView playerNameText = (TextView) subItemView.findViewById(R.id.player_name_text);
            playerIconImage.setBackgroundDrawable(model.getmAppIcon());
            subItemView.setTag(model.getmPkgName());
            subItemView.setOnClickListener(this.mCheckBoxListener);
            if (model.getmPkgName().equals(GMScreenGlobalInfo.getDefaultPlayer())) {
                playerNameText.setText(String.valueOf(model.getmAppLabel()) + " (" + getResources().getString(R.string.str_recommend) + ")");
            } else {
                playerNameText.setText(model.getmAppLabel());
            }
            if (!this.mSettingPlayerExist) {
                if (model.getmPkgName().equals(GMScreenGlobalInfo.getDefaultPlayer())) {
                    ((ImageView) subItemView.findViewById(R.id.check_box)).setImageResource(R.drawable.icon_player_select);
                } else {
                    ((ImageView) subItemView.findViewById(R.id.check_box)).setImageResource(R.drawable.icon_player_unselect);
                }
            } else if (model.getmPkgName().equals(this.mSettingPlayerPkgName)) {
                ((ImageView) subItemView.findViewById(R.id.check_box)).setImageResource(R.drawable.icon_player_select);
            } else {
                ((ImageView) subItemView.findViewById(R.id.check_box)).setImageResource(R.drawable.icon_player_unselect);
            }
            this.mCheckBoxList.add(subItemView);
            scrollSettingLayout.addView(subItemView);
        }
    }

    private PlayerModel getBuiltInPlayer() {
        PlayerModel player = new PlayerModel();
        player.setmAppIcon(getResources().getDrawable(R.drawable.icon_built_in_player));
        player.setmAppLabel("Built-in Player");
        player.setmPkgName(GMScreenGlobalInfo.DEFAULT_BUILT_IN_PLAYER);
        return player;
    }

    private void addBuiltInPlayerAndMXPlayer(List<PlayerModel> thirdPlayerList) {
        PlayerModel defaultPlayer = new ParseThirdPlayer(this).getDefaultExternalPlayer();
        int index = 0;
        while (true) {
            if (index >= thirdPlayerList.size()) {
                break;
            }
            if (!thirdPlayerList.get(index).getmPkgName().equals(GMScreenGlobalInfo.DEFAULT_EXTERNAL_PLAYER)) {
                index++;
            } else {
                thirdPlayerList.remove(index);
                break;
            }
        }
        thirdPlayerList.add(0, getBuiltInPlayer());
        thirdPlayerList.add(1, defaultPlayer);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn /* 2131492959 */:
                onBackPressed();
                break;
        }
    }
}
