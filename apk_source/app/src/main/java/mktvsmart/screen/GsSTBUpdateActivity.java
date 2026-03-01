package mktvsmart.screen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import mktvsmart.screen.dataconvert.model.DataConvertUpdateModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;

/* loaded from: classes.dex */
public class GsSTBUpdateActivity extends Activity {
    private String filePath = Environment.getExternalStorageDirectory() + "/Data.sdx";
    private List<DataConvertUpdateModel> models;
    private DataParser parser;
    private Socket tcpSocket;
    private ListView updateView;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws IOException {
        super.onCreate(savedInstanceState);
        try {
            CreateSocket cSocket = new CreateSocket("", 0);
            this.tcpSocket = cSocket.GetSocket();
            InputStream in = this.tcpSocket.getInputStream();
            GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, 25);
            byte[] recv_data = new byte[10240];
            int ilength = in.read(recv_data);
            InputStream istream = new ByteArrayInputStream(recv_data, 0, ilength);
            this.models = this.parser.parse(istream, 3);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        this.updateView = new ListView(this);
        this.updateView.setAdapter((ListAdapter) new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, GetData()));
        setContentView(this.updateView);
        this.updateView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: mktvsmart.screen.GsSTBUpdateActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) throws IOException {
                switch (position) {
                    case 0:
                        try {
                            File updateFile = new File(GsSTBUpdateActivity.this.filePath);
                            DataConvertUpdateModel updataNode = new DataConvertUpdateModel();
                            updataNode.SetDataLen((int) updateFile.length());
                            List<DataConvertUpdateModel> upadteList = new ArrayList<>();
                            upadteList.add(updataNode);
                            GsSTBUpdateActivity.this.parser = ParserFactory.getParser();
                            String data = GsSTBUpdateActivity.this.parser.serialize(upadteList, GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_UPDATE);
                            byte[] data_buff = data.getBytes("UTF-8");
                            FileInputStream fis = new FileInputStream(updateFile);
                            DataInputStream din = new DataInputStream(new BufferedInputStream(fis));
                            DataOutputStream dout = new DataOutputStream(GsSTBUpdateActivity.this.tcpSocket.getOutputStream());
                            dout.write(data_buff);
                            dout.flush();
                            byte[] file_buff = new byte[1024];
                            while (true) {
                                int len = din.read(file_buff);
                                if (len == -1) {
                                    break;
                                } else {
                                    dout.write(file_buff, 0, len);
                                    dout.flush();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            return;
                        }
                }
            }
        });
    }

    private List<String> GetData() {
        List<String> data = new ArrayList<>();
        data.add("Channel list update");
        data.add("software update");
        return data;
    }
}
