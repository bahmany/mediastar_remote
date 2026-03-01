package mktvsmart.screen;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertFavorModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;

/* loaded from: classes.dex */
public class GsEditFavorMenu extends Dialog {
    public static final String TAG = GsEditFavorMenu.class.getSimpleName();
    private FavorEditMenuAdapter adapter;
    private boolean[] choiceArray;
    private List<DataConvertChannelModel> editChannels;
    private GridView favGridView;
    private InputMethodManager inputManager;
    private boolean isSubmitModify;
    private AdapterView.OnItemClickListener itemClickListener;
    private AdapterView.OnItemLongClickListener itemLongClick;
    private Dialog mFavorRenameDialog;
    private LayoutInflater mInflater;
    private View.OnClickListener onClick;
    private DataParser parser;
    private Socket tcpSocket;

    public GsEditFavorMenu(Context context, List<DataConvertChannelModel> editChannels) {
        super(context, R.style.dialog);
        this.inputManager = null;
        this.itemClickListener = new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsEditFavorMenu.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GsEditFavorMenu.this.choiceArray[position] = !GsEditFavorMenu.this.choiceArray[position];
                GsEditFavorMenu.this.adapter.changeChoice(view, GsEditFavorMenu.this.choiceArray[position]);
            }
        };
        this.itemLongClick = new AdapterView.OnItemLongClickListener() { // from class: mktvsmart.screen.GsEditFavorMenu.2
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                LinearLayout favorRenameLayout = (LinearLayout) GsEditFavorMenu.this.mInflater.inflate(R.layout.input_rename_dialog, (ViewGroup) null);
                final EditText favRename = (EditText) favorRenameLayout.findViewById(R.id.input_name_edittext);
                Button renameSaveBtn = (Button) favorRenameLayout.findViewById(R.id.input_name_confirm_btn);
                Button renameCancelBtn = (Button) favorRenameLayout.findViewById(R.id.input_name_cancel_btn);
                favRename.setText(GMScreenGlobalInfo.favGroups.get(position).GetFavorName());
                favRename.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
                Selection.selectAll(favRename.getText());
                renameSaveBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsEditFavorMenu.2.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) throws UnsupportedEncodingException {
                        if (!favRename.getText().toString().equals("")) {
                            DataConvertFavorModel model = new DataConvertFavorModel();
                            List<DataConvertFavorModel> favorModel = new ArrayList<>();
                            GMScreenGlobalInfo.favGroups.get(position).SetFavorName(favRename.getText().toString());
                            model.SetFavorIndex(position);
                            model.setFavorTypeID(GMScreenGlobalInfo.favGroups.get(position).getFavorTypeID());
                            Log.d(GsEditFavorMenu.TAG, favRename.getText().toString());
                            model.SetFavorName(favRename.getText().toString());
                            favorModel.add(model);
                            GsEditFavorMenu.this.adapter.notifyDataSetChanged();
                            try {
                                byte[] dataCommand = GsEditFavorMenu.this.parser.serialize(favorModel, GlobalConstantValue.GMS_MSG_DO_FAV_GROUP_RENAME).getBytes("UTF-8");
                                GsSendSocket.sendSocketToStb(dataCommand, GsEditFavorMenu.this.tcpSocket, 0, dataCommand.length, GlobalConstantValue.GMS_MSG_DO_FAV_GROUP_RENAME);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        GsEditFavorMenu.this.inputManager.hideSoftInputFromWindow(favRename.getWindowToken(), 0);
                        GsEditFavorMenu.this.mFavorRenameDialog.dismiss();
                    }
                });
                renameCancelBtn.setOnClickListener(new View.OnClickListener() { // from class: mktvsmart.screen.GsEditFavorMenu.2.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        GsEditFavorMenu.this.inputManager.hideSoftInputFromWindow(favRename.getWindowToken(), 0);
                        GsEditFavorMenu.this.mFavorRenameDialog.dismiss();
                    }
                });
                GsEditFavorMenu.this.mFavorRenameDialog = new Dialog(GsEditFavorMenu.this.getContext(), R.style.dialog);
                GsEditFavorMenu.this.mFavorRenameDialog.setContentView(favorRenameLayout);
                GsEditFavorMenu.this.mFavorRenameDialog.setCanceledOnTouchOutside(false);
                GsEditFavorMenu.this.mFavorRenameDialog.show();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() { // from class: mktvsmart.screen.GsEditFavorMenu.2.3
                    @Override // java.util.TimerTask, java.lang.Runnable
                    public void run() {
                        GsEditFavorMenu.this.inputManager = (InputMethodManager) favRename.getContext().getSystemService("input_method");
                        GsEditFavorMenu.this.inputManager.showSoftInput(favRename, 0);
                    }
                }, 200L);
                return true;
            }
        };
        this.onClick = new View.OnClickListener() { // from class: mktvsmart.screen.GsEditFavorMenu.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) throws SocketException, UnsupportedEncodingException {
                switch (v.getId()) {
                    case R.id.favor_save /* 2131493465 */:
                        boolean bModify = false;
                        for (int position = 0; position < GsEditFavorMenu.this.choiceArray.length; position++) {
                            if (GsEditFavorMenu.this.choiceArray[position]) {
                                for (int i = 0; i < GsEditFavorMenu.this.editChannels.size(); i++) {
                                    if (!((DataConvertChannelModel) GsEditFavorMenu.this.editChannels.get(i)).mfavGroupIDs.contains(Integer.valueOf(GMScreenGlobalInfo.favGroups.get(position).getFavorTypeID()))) {
                                        ((DataConvertChannelModel) GsEditFavorMenu.this.editChannels.get(i)).mfavGroupIDs.add(Integer.valueOf(GMScreenGlobalInfo.favGroups.get(position).getFavorTypeID()));
                                        bModify = true;
                                    }
                                }
                            } else {
                                for (int i2 = 0; i2 < GsEditFavorMenu.this.editChannels.size(); i2++) {
                                    if (((DataConvertChannelModel) GsEditFavorMenu.this.editChannels.get(i2)).mfavGroupIDs.contains(Integer.valueOf(GMScreenGlobalInfo.favGroups.get(position).getFavorTypeID()))) {
                                        ((DataConvertChannelModel) GsEditFavorMenu.this.editChannels.get(i2)).mfavGroupIDs.remove(Integer.valueOf(GMScreenGlobalInfo.favGroups.get(position).getFavorTypeID()));
                                        bModify = true;
                                    }
                                }
                            }
                        }
                        if (bModify) {
                            try {
                                byte[] data_buff = GsEditFavorMenu.this.parser.serialize(GsEditFavorMenu.this.editChannels, GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK).getBytes("UTF-8");
                                GsEditFavorMenu.this.tcpSocket.setSoTimeout(3000);
                                GsSendSocket.sendSocketToStb(data_buff, GsEditFavorMenu.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            GsEditFavorMenu.this.setSubmitModify(true);
                        }
                        GsEditFavorMenu.this.dismiss();
                        break;
                    case R.id.favor_cancel /* 2131493466 */:
                        GsEditFavorMenu.this.dismiss();
                        break;
                }
            }
        };
        this.editChannels = editChannels;
        initData();
        buildView();
    }

    private void initData() {
        this.choiceArray = new boolean[GMScreenGlobalInfo.favGroups.size()];
        for (int position = 0; position < this.choiceArray.length; position++) {
            for (int i = 0; i < this.editChannels.size(); i++) {
                DataConvertChannelModel fav = this.editChannels.get(i);
                if (fav.mfavGroupIDs.contains(Integer.valueOf(GMScreenGlobalInfo.favGroups.get(position).getFavorTypeID())) && i == this.editChannels.size() - 1) {
                    this.choiceArray[position] = true;
                } else {
                    this.choiceArray[position] = false;
                }
            }
        }
        CreateSocket cSocket = new CreateSocket("", 0);
        try {
            this.tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parser = ParserFactory.getParser();
    }

    private void buildView() {
        this.mInflater = LayoutInflater.from(getContext());
        View view = this.mInflater.inflate(R.layout.set_favor_menu, (ViewGroup) null);
        setContentView(view);
        this.favGridView = (GridView) view.findViewById(R.id.favor_menu);
        this.favGridView.setSelector(new ColorDrawable(0));
        this.adapter = new FavorEditMenuAdapter();
        this.favGridView.setAdapter((ListAdapter) this.adapter);
        this.favGridView.setOnItemClickListener(this.itemClickListener);
        this.favGridView.setOnItemLongClickListener(this.itemLongClick);
        view.findViewById(R.id.favor_save).setOnClickListener(this.onClick);
        view.findViewById(R.id.favor_cancel).setOnClickListener(this.onClick);
    }

    public boolean isSubmitModify() {
        return this.isSubmitModify;
    }

    public void setSubmitModify(boolean isSubmitModify) {
        this.isSubmitModify = isSubmitModify;
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
        setSubmitModify(false);
    }

    class FavorEditMenuAdapter extends BaseAdapter {
        FavorEditMenuAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return GMScreenGlobalInfo.favGroups.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return GMScreenGlobalInfo.favGroups.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        public void changeChoice(View convertView, boolean bChoice) {
            ImageView favCheckBox = (ImageView) convertView.findViewById(R.id.favor_check);
            TextView favText = (TextView) convertView.findViewById(R.id.favor_text);
            if (bChoice) {
                favCheckBox.setImageResource(R.drawable.fav_checkbox);
                favText.setBackgroundResource(R.drawable.fav_text);
            } else {
                favCheckBox.setImageResource(R.drawable.fav_checkbox_grey);
                favText.setBackgroundResource(R.drawable.fav_text_grey);
            }
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup itemView;
            if (convertView == null) {
                itemView = (ViewGroup) GsEditFavorMenu.this.mInflater.inflate(R.layout.set_favor, parent, false);
            } else {
                itemView = (ViewGroup) convertView;
            }
            ImageView favCheckBox = (ImageView) itemView.findViewById(R.id.favor_check);
            ImageView favIcon = (ImageView) itemView.findViewById(R.id.fav_icon);
            TextView favText = (TextView) itemView.findViewById(R.id.favor_text);
            if (GsEditFavorMenu.this.choiceArray[position]) {
                favCheckBox.setImageResource(R.drawable.fav_checkbox);
                favText.setBackgroundResource(R.drawable.fav_text);
            } else {
                favCheckBox.setImageResource(R.drawable.fav_checkbox_grey);
                favText.setBackgroundResource(R.drawable.fav_text_grey);
            }
            String favName = GMScreenGlobalInfo.favGroups.get(position).GetFavorName();
            favText.setText(favName);
            String favNameLowerCase = favName.toLowerCase(Locale.US);
            if (favNameLowerCase.contains("movie")) {
                favIcon.setImageResource(R.drawable.movie);
            } else if (favNameLowerCase.contains("news")) {
                favIcon.setImageResource(R.drawable.news);
            } else if (favNameLowerCase.contains("music")) {
                favIcon.setImageResource(R.drawable.music);
            } else if (favNameLowerCase.contains("sport")) {
                favIcon.setImageResource(R.drawable.sport);
            } else if (favNameLowerCase.contains("education")) {
                favIcon.setImageResource(R.drawable.education);
            } else if (favNameLowerCase.contains("weather")) {
                favIcon.setImageResource(R.drawable.weather);
            } else if (favNameLowerCase.contains("children")) {
                favIcon.setImageResource(R.drawable.children);
            } else if (favNameLowerCase.contains("culture")) {
                favIcon.setImageResource(R.drawable.culture);
            } else if (favNameLowerCase.contains("social")) {
                favIcon.setImageResource(R.drawable.social);
            } else {
                favIcon.setImageResource(R.drawable.generalfav);
            }
            return itemView;
        }
    }
}
