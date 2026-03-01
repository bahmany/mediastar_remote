package mktvsmart.screen.filebroswer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import master.flame.danmaku.danmaku.parser.IDataSource;
import mktvsmart.screen.R;
import mktvsmart.screen.util.CommonDialogs;
import mktvsmart.screen.util.GRunnable;
import mktvsmart.screen.vlc.LocalPlayActivity;
import org.videolan.vlc.util.Util;

/* loaded from: classes.dex */
public class FolderListFragment extends ListFragment implements ISortable, IRefreshable {
    public static final String TAG = "FolderListFragment";
    private DirectoryAdapter mDirectoryAdapter;
    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() { // from class: mktvsmart.screen.filebroswer.FolderListFragment.1
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) throws Throwable {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("android.intent.action.MEDIA_MOUNTED") || action.equalsIgnoreCase("android.intent.action.MEDIA_UNMOUNTED") || action.equalsIgnoreCase("android.intent.action.MEDIA_REMOVED") || action.equalsIgnoreCase("android.intent.action.MEDIA_EJECT")) {
                FolderListFragment.this.refresh();
            }
        }
    };

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mDirectoryAdapter = new DirectoryAdapter(getActivity());
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        filter.addAction("android.intent.action.MEDIA_EJECT");
        filter.addDataScheme(IDataSource.SCHEME_FILE_TAG);
        getActivity().registerReceiver(this.messageReceiver, filter);
    }

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folder, container, false);
        setListAdapter(this.mDirectoryAdapter);
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: mktvsmart.screen.filebroswer.FolderListFragment.2
            AnonymousClass2() {
            }

            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View v2, int position, long id) {
                return !FolderListFragment.this.mDirectoryAdapter.isChildFile(position);
            }
        });
        registerForContextMenu(listView);
        return v;
    }

    /* renamed from: mktvsmart.screen.filebroswer.FolderListFragment$2 */
    class AnonymousClass2 implements AdapterView.OnItemLongClickListener {
        AnonymousClass2() {
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> adapterView, View v2, int position, long id) {
            return !FolderListFragment.this.mDirectoryAdapter.isChildFile(position);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        ((GsFileBroswerActivity) getActivity()).updateOptionView();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(this.messageReceiver);
    }

    @Override // android.support.v4.app.Fragment, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        if (this.mDirectoryAdapter.isChildFile(position)) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.directory_view, menu);
        }
    }

    @Override // android.support.v4.app.Fragment
    public boolean onContextItemSelected(MenuItem item) {
        if (!getUserVisibleHint()) {
            return super.onContextItemSelected(item);
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) {
            return super.onContextItemSelected(item);
        }
        int id = item.getItemId();
        String mediaLocation = this.mDirectoryAdapter.getMediaLocation(info.position);
        if (mediaLocation == null) {
            return super.onContextItemSelected(item);
        }
        if (id == R.id.directory_view_play) {
            openMediaFile(info.position);
            return true;
        }
        if (id == R.id.directory_view_delete) {
            AlertDialog alertDialog = CommonDialogs.deleteMedia(getActivity(), mediaLocation, new GRunnable() { // from class: mktvsmart.screen.filebroswer.FolderListFragment.3
                AnonymousClass3() {
                }

                @Override // mktvsmart.screen.util.GRunnable
                public void run(Object o) throws Throwable {
                    FolderListFragment.this.refresh();
                }
            });
            alertDialog.show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /* renamed from: mktvsmart.screen.filebroswer.FolderListFragment$3 */
    class AnonymousClass3 extends GRunnable {
        AnonymousClass3() {
        }

        @Override // mktvsmart.screen.util.GRunnable
        public void run(Object o) throws Throwable {
            FolderListFragment.this.refresh();
        }
    }

    @Override // android.support.v4.app.ListFragment
    public void onListItemClick(ListView l, View v, int p, long id) {
        int success = this.mDirectoryAdapter.browse(p);
        if (success < 0) {
            openMediaFile(p);
        } else {
            setSelection(success);
        }
    }

    public boolean isRootDirectory() {
        return this.mDirectoryAdapter.isRoot();
    }

    public void showParentDirectory() throws Throwable {
        int success = this.mDirectoryAdapter.browse("..");
        if (success >= 0) {
            setSelection(success);
        }
    }

    private void openMediaFile(int p) {
        String location = this.mDirectoryAdapter.getMediaLocation(p);
        Intent intent = new Intent(getActivity(), (Class<?>) LocalPlayActivity.class);
        intent.setData(Uri.parse(location));
        startActivity(intent);
    }

    @Override // mktvsmart.screen.filebroswer.ISortable
    public void sortBy(int sortby) {
        Util.toaster(getActivity(), R.string.notavailable);
    }

    @Override // mktvsmart.screen.filebroswer.IRefreshable
    public void refresh() throws Throwable {
        if (this.mDirectoryAdapter != null) {
            this.mDirectoryAdapter.refresh();
        }
    }

    /* renamed from: mktvsmart.screen.filebroswer.FolderListFragment$1 */
    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) throws Throwable {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("android.intent.action.MEDIA_MOUNTED") || action.equalsIgnoreCase("android.intent.action.MEDIA_UNMOUNTED") || action.equalsIgnoreCase("android.intent.action.MEDIA_REMOVED") || action.equalsIgnoreCase("android.intent.action.MEDIA_EJECT")) {
                FolderListFragment.this.refresh();
            }
        }
    }
}
