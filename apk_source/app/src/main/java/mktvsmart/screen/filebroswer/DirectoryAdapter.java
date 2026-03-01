package mktvsmart.screen.filebroswer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;
import mktvsmart.screen.R;
import org.teleal.cling.model.ServiceReference;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.util.Extensions;
import org.videolan.vlc.util.AndroidDevices;
import org.videolan.vlc.util.Strings;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.VLCInstance;

/* loaded from: classes.dex */
public class DirectoryAdapter extends BaseAdapter {
    public static final String TAG = "DirectoryAdapter";
    private int mAlignMode;
    private String mCurrentDir;
    private Node mCurrentNode;
    private String mCurrentRoot;
    private LayoutInflater mInflater;
    private Node mRootNode;

    public static boolean acceptedPath(String f) {
        StringBuilder sb = new StringBuilder();
        sb.append(".+(\\.)((?i)(");
        boolean first = true;
        Iterator<String> it = Extensions.VIDEO.iterator();
        while (it.hasNext()) {
            String ext = it.next();
            if (!first) {
                sb.append('|');
            } else {
                first = false;
            }
            sb.append(ext.substring(1));
        }
        Iterator<String> it2 = Extensions.AUDIO.iterator();
        while (it2.hasNext()) {
            String ext2 = it2.next();
            sb.append('|');
            sb.append(ext2.substring(1));
        }
        sb.append("))");
        return Pattern.compile(sb.toString(), 2).matcher(f).matches();
    }

    public class Node implements Comparable<Node> {
        public ArrayList<Node> children;
        public Boolean isFile;
        String name;
        public Node parent;
        String visibleName;

        public Node(DirectoryAdapter directoryAdapter, String _name) {
            this(_name, null);
        }

        public Node(String _name, String _visibleName) {
            this.name = _name;
            this.visibleName = _visibleName;
            this.children = new ArrayList<>();
            this.isFile = false;
            this.parent = null;
        }

        public void addChildNode(Node n) {
            n.parent = this;
            this.children.add(n);
        }

        public Node getChildNode(String directoryName) {
            Iterator<Node> it = this.children.iterator();
            while (it.hasNext()) {
                Node n = it.next();
                if (n.name.equals(directoryName)) {
                    return n;
                }
            }
            Node n2 = new Node(DirectoryAdapter.this, directoryName);
            addChildNode(n2);
            return n2;
        }

        public Boolean isFile() {
            return this.isFile;
        }

        public void setIsFile() {
            this.isFile = true;
        }

        public Boolean existsChild(String _n) {
            Iterator<Node> it = this.children.iterator();
            while (it.hasNext()) {
                Node n = it.next();
                if (Strings.nullEquals(n.name, _n)) {
                    return true;
                }
            }
            return false;
        }

        public int getChildPosition(Node child) {
            if (child == null) {
                return -1;
            }
            ListIterator<Node> it = this.children.listIterator();
            while (it.hasNext()) {
                Node node = it.next();
                if (child.equals(node)) {
                    return it.previousIndex();
                }
            }
            return -1;
        }

        public Node ensureExists(String _n) {
            Iterator<Node> it = this.children.iterator();
            while (it.hasNext()) {
                Node n = it.next();
                if (Strings.nullEquals(n.name, _n)) {
                    return n;
                }
            }
            Node nn = new Node(DirectoryAdapter.this, _n);
            this.children.add(nn);
            return nn;
        }

        public int subfolderCount() {
            int c = 0;
            Iterator<Node> it = this.children.iterator();
            while (it.hasNext()) {
                Node n = it.next();
                if (!n.isFile().booleanValue() && !n.name.equals("..")) {
                    c++;
                }
            }
            return c;
        }

        public int subfilesCount() {
            int c = 0;
            Iterator<Node> it = this.children.iterator();
            while (it.hasNext()) {
                Node n = it.next();
                if (n.isFile().booleanValue()) {
                    c++;
                }
            }
            return c;
        }

        public String getVisibleName() {
            return this.visibleName != null ? this.visibleName : this.name;
        }

        @Override // java.lang.Comparable
        public int compareTo(Node arg0) {
            if (this.isFile.booleanValue() && !arg0.isFile.booleanValue()) {
                return 1;
            }
            if (!this.isFile.booleanValue() && arg0.isFile.booleanValue()) {
                return -1;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(this.name, arg0.name);
        }
    }

    static class DirectoryViewHolder {
        ImageView icon;
        View layout;
        TextView text;
        TextView title;

        DirectoryViewHolder() {
        }
    }

    private void populateNode(Node n, String path) throws Throwable {
        populateNode(n, path, 0);
    }

    private void populateNode(Node n, String path, int depth) throws Throwable {
        if (path == null) {
            String[] storages = AndroidDevices.getStorageDirectories();
            for (String storage : storages) {
                File f = new File(storage);
                Node child = new Node(f.getName(), getVisibleName(f));
                child.isFile = false;
                populateNode(child, storage, 0);
                n.addChildNode(child);
            }
            return;
        }
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            ArrayList<String> files = new ArrayList<>();
            LibVLC.nativeReadDirectory(path, files);
            StringBuilder sb = new StringBuilder(100);
            if (files != null && files.size() >= 1) {
                for (int i = 0; i < files.size(); i++) {
                    String filename = files.get(i);
                    if (!filename.equals(".") && !filename.equals("..") && !filename.startsWith(".")) {
                        Node nss = new Node(this, filename);
                        nss.isFile = false;
                        sb.append(path);
                        sb.append(ServiceReference.DELIMITER);
                        sb.append(filename);
                        String newPath = sb.toString();
                        sb.setLength(0);
                        if (LibVLC.nativeIsPathDirectory(newPath) && depth < 10) {
                            ArrayList<String> files_int = new ArrayList<>();
                            LibVLC.nativeReadDirectory(newPath, files_int);
                            if (files_int.size() < 8) {
                                String mCurrentDir_old = this.mCurrentDir;
                                this.mCurrentDir = path;
                                populateNode(nss, newPath, depth + 1);
                                this.mCurrentDir = mCurrentDir_old;
                            }
                        } else if (acceptedPath(newPath)) {
                            nss.setIsFile();
                        }
                        n.addChildNode(nss);
                    }
                }
                Collections.sort(n.children);
            }
            Node up = new Node(this, "..");
            n.children.add(0, up);
        }
    }

    public DirectoryAdapter(Context context) throws Throwable {
        DirectoryAdapter_Core(context, null);
    }

    private void DirectoryAdapter_Core(Context activityContext, String rootDir) throws Throwable {
        if (rootDir != null) {
            rootDir = Strings.stripTrailingSlash(rootDir);
        }
        Log.v(TAG, "rootMRL is " + rootDir);
        this.mInflater = LayoutInflater.from(activityContext);
        this.mRootNode = new Node(this, rootDir);
        this.mCurrentDir = rootDir;
        populateNode(this.mRootNode, rootDir);
        this.mCurrentNode = this.mRootNode;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activityContext);
        this.mAlignMode = Integer.valueOf(preferences.getString("audio_title_alignment", "0")).intValue();
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mCurrentNode.children.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int arg0) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int arg0) {
        return 0L;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        DirectoryViewHolder holder;
        Node selectedNode = this.mCurrentNode.children.get(position);
        View v = convertView;
        Context context = VLCInstance.getAppContext();
        if (v == null) {
            v = this.mInflater.inflate(R.layout.folder_view_item, parent, false);
            holder = new DirectoryViewHolder();
            holder.layout = v.findViewById(R.id.layout_item);
            holder.title = (TextView) v.findViewById(R.id.title);
            holder.title.setSelected(true);
            Util.setAlignModeByPref(this.mAlignMode, holder.title);
            holder.text = (TextView) v.findViewById(R.id.text);
            holder.icon = (ImageView) v.findViewById(R.id.dvi_icon);
            v.setTag(holder);
        } else {
            holder = (DirectoryViewHolder) v.getTag();
        }
        String holderText = "";
        holder.title.setText(selectedNode.getVisibleName());
        if (selectedNode.name.equals("..")) {
            holderText = context.getString(R.string.parent_folder);
        } else if (!selectedNode.isFile().booleanValue()) {
            int folderCount = selectedNode.subfolderCount();
            int mediaFileCount = selectedNode.subfilesCount();
            holderText = folderCount > 0 ? String.valueOf("") + context.getResources().getQuantityString(R.plurals.subfolders_quantity, folderCount, Integer.valueOf(folderCount)) : "";
            if (folderCount > 0 && mediaFileCount > 0) {
                holderText = String.valueOf(holderText) + ", ";
            }
            if (mediaFileCount > 0) {
                holderText = String.valueOf(holderText) + context.getResources().getQuantityString(R.plurals.mediafiles_quantity, mediaFileCount, Integer.valueOf(mediaFileCount));
            }
        }
        holder.text.setText(holderText);
        if (selectedNode.isFile().booleanValue()) {
            holder.icon.setImageResource(R.drawable.video_icon);
        } else {
            holder.icon.setImageResource(R.drawable.ic_menu_folder);
        }
        return v;
    }

    public int browse(int position) {
        Node selectedNode = this.mCurrentNode.children.get(position);
        if (selectedNode.isFile().booleanValue()) {
            return -1;
        }
        return browse(selectedNode.name);
    }

    public int browse(String directoryName) throws Throwable {
        if (this.mCurrentDir == null) {
            String[] storages = AndroidDevices.getStorageDirectories();
            int length = storages.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String storage = Strings.stripTrailingSlash(storages[i]);
                if (!storage.endsWith(directoryName)) {
                    i++;
                } else {
                    this.mCurrentRoot = storage;
                    this.mCurrentDir = Strings.stripTrailingSlash(storage);
                    break;
                }
            }
        } else {
            try {
                this.mCurrentDir = new URI(LibVLC.PathToURI(String.valueOf(this.mCurrentDir) + ServiceReference.DELIMITER + directoryName)).normalize().getPath();
                this.mCurrentDir = Strings.stripTrailingSlash(this.mCurrentDir);
                if (this.mCurrentDir.equals(getParentDir(this.mCurrentRoot))) {
                    this.mCurrentDir = null;
                    this.mCurrentRoot = null;
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException in browse()", e);
                return -1;
            } catch (URISyntaxException e2) {
                Log.e(TAG, "URISyntaxException in browse()", e2);
                return -1;
            }
        }
        Log.d(TAG, "Browsing to " + this.mCurrentDir);
        int currentDirPosition = 0;
        if (directoryName.equals("..")) {
            currentDirPosition = this.mCurrentNode.parent.getChildPosition(this.mCurrentNode);
            this.mCurrentNode = this.mCurrentNode.parent;
        } else {
            this.mCurrentNode = this.mCurrentNode.getChildNode(directoryName);
            if (this.mCurrentNode.subfolderCount() < 1) {
                this.mCurrentNode.children.clear();
                populateNode(this.mCurrentNode, this.mCurrentDir);
            }
        }
        notifyDataSetChanged();
        if (currentDirPosition == -1) {
            return 0;
        }
        return currentDirPosition;
    }

    public boolean isChildFile(int position) {
        Node selectedNode = this.mCurrentNode.children.get(position);
        return selectedNode.isFile().booleanValue();
    }

    public String getMediaLocation(int position) {
        if (position >= this.mCurrentNode.children.size()) {
            return null;
        }
        return LibVLC.PathToURI(String.valueOf(this.mCurrentDir) + ServiceReference.DELIMITER + this.mCurrentNode.children.get(position).name);
    }

    public boolean isRoot() {
        return this.mCurrentDir == null;
    }

    public String getmCurrentDir() {
        return this.mCurrentDir;
    }

    public ArrayList<String> getAllMediaLocations() {
        ArrayList<String> a = new ArrayList<>();
        for (int i = 1; i < this.mCurrentNode.children.size(); i++) {
            if (this.mCurrentNode.children.get(i).isFile.booleanValue()) {
                a.add(getMediaLocation(i));
            }
        }
        return a;
    }

    public void refresh() throws Throwable {
        Iterator<Node> it = this.mCurrentNode.children.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            n.children.clear();
        }
        this.mCurrentNode.children.clear();
        populateNode(this.mCurrentNode, this.mCurrentDir);
        notifyDataSetChanged();
    }

    private String getParentDir(String path) {
        try {
            path = new URI(LibVLC.PathToURI(String.valueOf(path) + "/..")).normalize().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return Strings.stripTrailingSlash(path);
    }

    private String getVisibleName(File file) {
        return (Build.VERSION.SDK_INT < 17 || !file.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getPath())) ? file.getName() : VLCInstance.getAppContext().getString(R.string.internal_memory);
    }
}
