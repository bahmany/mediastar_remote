package org.videolan.vlc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.videolan.vlc.util.BitmapUtil;

/* loaded from: classes.dex */
public class MediaGroup extends MediaWrapper {
    public static final int MIN_GROUP_LENGTH = 6;
    public static final String TAG = "VLC/MediaGroup";
    private ArrayList<MediaWrapper> mMedias;

    public MediaGroup(MediaWrapper media) {
        super(media.getLocation(), media.getTime(), media.getLength(), 2, BitmapUtil.getPictureFromCache(media), media.getTitle(), media.getArtist(), media.getGenre(), media.getAlbum(), media.getAlbumArtist(), media.getWidth(), media.getHeight(), media.getArtworkURL(), media.getAudioTrack(), media.getSpuTrack(), media.getTrackNumber(), media.getDiscNumber(), 0L);
        this.mMedias = new ArrayList<>();
        this.mMedias.add(media);
    }

    public void add(MediaWrapper media) {
        this.mMedias.add(media);
    }

    public MediaWrapper getMedia() {
        return this.mMedias.size() == 1 ? this.mMedias.get(0) : this;
    }

    public MediaWrapper getFirstMedia() {
        return this.mMedias.get(0);
    }

    public int size() {
        return this.mMedias.size();
    }

    public void merge(MediaWrapper media, String title) {
        this.mMedias.add(media);
        this.mTitle = title;
    }

    public static List<MediaGroup> group(List<MediaWrapper> mediaList) {
        ArrayList<MediaGroup> groups = new ArrayList<>();
        for (MediaWrapper media : mediaList) {
            insertInto(groups, media);
        }
        return groups;
    }

    private static void insertInto(ArrayList<MediaGroup> groups, MediaWrapper media) {
        Iterator<MediaGroup> it = groups.iterator();
        while (it.hasNext()) {
            MediaGroup mediaGroup = it.next();
            String group = mediaGroup.getTitle();
            String item = media.getTitle();
            int commonLength = 0;
            int minLength = Math.min(group.length(), item.length());
            while (commonLength < minLength && group.charAt(commonLength) == item.charAt(commonLength)) {
                commonLength++;
            }
            if (commonLength >= 6) {
                if (commonLength == group.length()) {
                    mediaGroup.add(media);
                    return;
                } else {
                    mediaGroup.merge(media, group.substring(0, commonLength));
                    return;
                }
            }
        }
        groups.add(new MediaGroup(media));
    }
}
