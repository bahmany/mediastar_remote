package org.videolan.libvlc.util;

import java.util.ArrayList;
import java.util.Iterator;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaDiscoverer;
import org.videolan.libvlc.MediaList;
import org.videolan.libvlc.VLCObject;

/* loaded from: classes.dex */
public class MediaBrowser {
    private static final String[] DISCOVERER_LIST = {"dsm", "upnp"};
    private static final String TAG = "LibVLC/util/MediaBrowser";
    private MediaList mBrowserMediaList;
    private EventListener mEventListener;
    private LibVLC mLibVlc;
    private Media mMedia;
    private ArrayList<MediaDiscoverer> mMediaDiscoverers = new ArrayList<>();
    private ArrayList<Media> mDiscovererMediaArray = new ArrayList<>();
    private VLCObject.EventListener mBrowserMediaListEventListener = new VLCObject.EventListener() { // from class: org.videolan.libvlc.util.MediaBrowser.1
        @Override // org.videolan.libvlc.VLCObject.EventListener
        public void onEvent(VLCObject.Event event) {
            if (MediaBrowser.this.mEventListener != null) {
                MediaList.Event mlEvent = (MediaList.Event) event;
                switch (mlEvent.type) {
                    case 512:
                        MediaBrowser.this.mEventListener.onMediaAdded(mlEvent.index, mlEvent.media);
                        break;
                    case VLCObject.Events.MediaListItemDeleted /* 514 */:
                        MediaBrowser.this.mEventListener.onMediaRemoved(mlEvent.index, mlEvent.media);
                        break;
                    case VLCObject.Events.MediaListEndReached /* 516 */:
                        MediaBrowser.this.mEventListener.onBrowseEnd();
                        break;
                }
            }
        }
    };
    private VLCObject.EventListener mDiscovererMediaListEventListener = new VLCObject.EventListener() { // from class: org.videolan.libvlc.util.MediaBrowser.2
        @Override // org.videolan.libvlc.VLCObject.EventListener
        public void onEvent(VLCObject.Event event) {
            int index;
            if (MediaBrowser.this.mEventListener != null) {
                MediaList.Event mlEvent = (MediaList.Event) event;
                int index2 = -1;
                switch (mlEvent.type) {
                    case 512:
                        synchronized (MediaBrowser.this) {
                            boolean found = false;
                            Iterator it = MediaBrowser.this.mDiscovererMediaArray.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    Media media = (Media) it.next();
                                    if (media.getMrl().equals(mlEvent.media.getMrl())) {
                                        found = true;
                                    }
                                }
                            }
                            if (!found) {
                                MediaBrowser.this.mDiscovererMediaArray.add(mlEvent.media);
                                index2 = MediaBrowser.this.mDiscovererMediaArray.size() - 1;
                            }
                        }
                        if (index2 != -1) {
                            MediaBrowser.this.mEventListener.onMediaAdded(index2, mlEvent.media);
                            return;
                        }
                        return;
                    case 513:
                    case 515:
                    default:
                        return;
                    case VLCObject.Events.MediaListItemDeleted /* 514 */:
                        synchronized (MediaBrowser.this) {
                            index = MediaBrowser.this.mDiscovererMediaArray.indexOf(mlEvent.media);
                            if (index != -1) {
                                MediaBrowser.this.mDiscovererMediaArray.remove(index);
                            }
                        }
                        if (index != -1) {
                            MediaBrowser.this.mEventListener.onMediaRemoved(index, mlEvent.media);
                            return;
                        }
                        return;
                    case VLCObject.Events.MediaListEndReached /* 516 */:
                        MediaBrowser.this.mEventListener.onBrowseEnd();
                        return;
                }
            }
        }
    };

    public interface EventListener {
        void onBrowseEnd();

        void onMediaAdded(int i, Media media);

        void onMediaRemoved(int i, Media media);
    }

    public MediaBrowser(LibVLC libvlc, EventListener listener) {
        this.mLibVlc = libvlc;
        this.mEventListener = listener;
    }

    private synchronized void reset() {
        Iterator<MediaDiscoverer> it = this.mMediaDiscoverers.iterator();
        while (it.hasNext()) {
            MediaDiscoverer md = it.next();
            md.release();
        }
        this.mMediaDiscoverers.clear();
        this.mDiscovererMediaArray.clear();
        if (this.mMedia != null) {
            this.mMedia.release();
            this.mMedia = null;
        }
        this.mBrowserMediaList = null;
    }

    public synchronized void release() {
        reset();
    }

    private void startMediaDiscoverer(String discovererName) {
        MediaDiscoverer md = new MediaDiscoverer(this.mLibVlc, discovererName);
        this.mMediaDiscoverers.add(md);
        MediaList ml = md.getMediaList();
        ml.setEventListener(this.mDiscovererMediaListEventListener);
        md.start();
    }

    public synchronized void discoverNetworkShares() {
        reset();
        for (String discovererName : DISCOVERER_LIST) {
            startMediaDiscoverer(discovererName);
        }
    }

    public synchronized void discoverNetworkShares(String discovererName) {
        reset();
        startMediaDiscoverer(discovererName);
    }

    public synchronized void browse(String mrl) {
        Media media = new Media(this.mLibVlc, mrl);
        browse(media);
        media.release();
    }

    public synchronized void browse(Media media) {
        media.retain();
        reset();
        this.mBrowserMediaList = media.subItems();
        this.mBrowserMediaList.setEventListener(this.mBrowserMediaListEventListener);
        media.parseAsync(1);
        this.mMedia = media;
    }

    public synchronized int getMediaCount() {
        return this.mBrowserMediaList != null ? this.mBrowserMediaList.getCount() : this.mDiscovererMediaArray.size();
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x001e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized org.videolan.libvlc.Media getMediaAt(int r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            if (r2 < 0) goto L1e
            int r0 = r1.getMediaCount()     // Catch: java.lang.Throwable -> L20
            if (r2 >= r0) goto L1e
            org.videolan.libvlc.MediaList r0 = r1.mBrowserMediaList     // Catch: java.lang.Throwable -> L20
            if (r0 == 0) goto L15
            org.videolan.libvlc.MediaList r0 = r1.mBrowserMediaList     // Catch: java.lang.Throwable -> L20
            org.videolan.libvlc.Media r0 = r0.getMediaAt(r2)     // Catch: java.lang.Throwable -> L20
        L13:
            monitor-exit(r1)
            return r0
        L15:
            java.util.ArrayList<org.videolan.libvlc.Media> r0 = r1.mDiscovererMediaArray     // Catch: java.lang.Throwable -> L20
            java.lang.Object r0 = r0.get(r2)     // Catch: java.lang.Throwable -> L20
            org.videolan.libvlc.Media r0 = (org.videolan.libvlc.Media) r0     // Catch: java.lang.Throwable -> L20
            goto L13
        L1e:
            r0 = 0
            goto L13
        L20:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.videolan.libvlc.util.MediaBrowser.getMediaAt(int):org.videolan.libvlc.Media");
    }
}
