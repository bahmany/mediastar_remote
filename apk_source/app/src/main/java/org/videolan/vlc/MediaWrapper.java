package org.videolan.vlc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.util.Extensions;

/* loaded from: classes.dex */
public class MediaWrapper implements Parcelable {
    public static final Parcelable.Creator<MediaWrapper> CREATOR = new Parcelable.Creator<MediaWrapper>() { // from class: org.videolan.vlc.MediaWrapper.1
        AnonymousClass1() {
        }

        @Override // android.os.Parcelable.Creator
        public MediaWrapper createFromParcel(Parcel in) {
            return new MediaWrapper(in);
        }

        @Override // android.os.Parcelable.Creator
        public MediaWrapper[] newArray(int size) {
            return new MediaWrapper[size];
        }
    };
    public static final String TAG = "VLC/MediaWrapper";
    public static final int TYPE_ALL = -1;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_DIR = 3;
    public static final int TYPE_GROUP = 2;
    public static final int TYPE_PLAYLIST = 5;
    public static final int TYPE_SUBTITLE = 4;
    public static final int TYPE_VIDEO = 0;
    private String mAlbum;
    private String mAlbumArtist;
    private String mArtist;
    private String mArtworkURL;
    private String mCopyright;
    private String mDate;
    private String mDescription;
    private int mDiscNumber;
    private String mEncodedBy;
    private String mFilename;
    private String mGenre;
    private boolean mIsPictureParsed;
    private final String mLocation;
    private String mNowPlaying;
    private Bitmap mPicture;
    private String mPublisher;
    private String mRating;
    private String mSettings;
    protected String mTitle;
    private String mTrackID;
    private int mTrackNumber;
    private int mType;
    private long mTime = 0;
    private int mAudioTrack = -1;
    private int mSpuTrack = -2;
    private long mLength = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mFlags = 0;
    private long mLastModified = 0;

    public MediaWrapper(String mrl) {
        if (mrl == null) {
            throw new NullPointerException("mrl was null");
        }
        this.mLocation = mrl;
        init(null);
    }

    public MediaWrapper(Media media) {
        if (media == null) {
            throw new NullPointerException("media was null");
        }
        this.mLocation = media.getMrl();
        init(media);
    }

    private void init(Media media) {
        int dotIndex;
        this.mType = -1;
        if (media != null) {
            if (media.isParsed()) {
                this.mLength = media.getDuration();
                for (int i = 0; i < media.getTrackCount(); i++) {
                    Media.Track track = media.getTrack(i);
                    if (track != null) {
                        if (track.type == 1) {
                            Media.VideoTrack videoTrack = (Media.VideoTrack) track;
                            this.mType = 0;
                            this.mWidth = videoTrack.width;
                            this.mHeight = videoTrack.height;
                        } else if (this.mType == -1 && track.type == 0) {
                            this.mType = 1;
                        }
                    }
                }
            }
            updateMeta(media);
            if (this.mType == -1 && media.getType() == 2) {
                this.mType = 3;
            }
        }
        if (this.mType == -1 && (dotIndex = this.mLocation.lastIndexOf(".")) != -1) {
            String fileExt = this.mLocation.substring(dotIndex).toLowerCase(Locale.ENGLISH);
            if (Extensions.VIDEO.contains(fileExt)) {
                this.mType = 0;
                return;
            }
            if (Extensions.AUDIO.contains(fileExt)) {
                this.mType = 1;
            } else if (Extensions.SUBTITLES.contains(fileExt)) {
                this.mType = 4;
            } else if (Extensions.PLAYLIST.contains(fileExt)) {
                this.mType = 5;
            }
        }
    }

    private void init(long time, long length, int type, Bitmap picture, String title, String artist, String genre, String album, String albumArtist, int width, int height, String artworkURL, int audio, int spu, int trackNumber, int discNumber, long lastModified) {
        this.mFilename = null;
        this.mTime = time;
        this.mAudioTrack = audio;
        this.mSpuTrack = spu;
        this.mLength = length;
        this.mType = type;
        this.mPicture = picture;
        this.mWidth = width;
        this.mHeight = height;
        this.mTitle = title;
        this.mArtist = artist;
        this.mGenre = genre;
        this.mAlbum = album;
        this.mAlbumArtist = albumArtist;
        this.mArtworkURL = artworkURL;
        this.mTrackNumber = trackNumber;
        this.mDiscNumber = discNumber;
        this.mLastModified = lastModified;
    }

    public MediaWrapper(String location, long time, long length, int type, Bitmap picture, String title, String artist, String genre, String album, String albumArtist, int width, int height, String artworkURL, int audio, int spu, int trackNumber, int discNumber, long lastModified) {
        this.mLocation = location;
        init(time, length, type, picture, title, artist, genre, album, albumArtist, width, height, artworkURL, audio, spu, trackNumber, discNumber, lastModified);
    }

    public String getLocation() {
        return this.mLocation;
    }

    private static String getMetaId(Media media, int id, boolean trim) {
        String meta = media.getMeta(id);
        if (meta != null) {
            return trim ? meta.trim() : meta;
        }
        return null;
    }

    public void updateMeta(Media media) {
        this.mTitle = getMetaId(media, 0, true);
        this.mArtist = getMetaId(media, 1, true);
        this.mAlbum = getMetaId(media, 4, true);
        this.mGenre = getMetaId(media, 2, true);
        this.mAlbumArtist = getMetaId(media, 23, true);
        this.mArtworkURL = getMetaId(media, 15, false);
        this.mNowPlaying = getMetaId(media, 12, false);
        String trackNumber = getMetaId(media, 5, false);
        if (!TextUtils.isEmpty(trackNumber)) {
            try {
                this.mTrackNumber = Integer.parseInt(trackNumber);
            } catch (NumberFormatException e) {
            }
        }
        String discNumber = getMetaId(media, 24, false);
        if (!TextUtils.isEmpty(discNumber)) {
            try {
                this.mDiscNumber = Integer.parseInt(discNumber);
            } catch (NumberFormatException e2) {
            }
        }
        Log.d(TAG, "Title " + this.mTitle);
        Log.d(TAG, "Artist " + this.mArtist);
        Log.d(TAG, "Genre " + this.mGenre);
        Log.d(TAG, "Album " + this.mAlbum);
    }

    public void updateMeta(LibVLC libVLC) {
        this.mTitle = libVLC.getMeta(0);
        this.mArtist = libVLC.getMeta(1);
        this.mGenre = libVLC.getMeta(2);
        this.mAlbum = libVLC.getMeta(4);
        this.mAlbumArtist = libVLC.getMeta(23);
        this.mNowPlaying = libVLC.getMeta(12);
        this.mArtworkURL = libVLC.getMeta(15);
    }

    public String getFileName() {
        if (this.mFilename == null) {
            this.mFilename = LibVlcUtil.URItoFileName(this.mLocation);
        }
        return this.mFilename;
    }

    public long getTime() {
        return this.mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public int getAudioTrack() {
        return this.mAudioTrack;
    }

    public void setAudioTrack(int track) {
        this.mAudioTrack = track;
    }

    public int getSpuTrack() {
        return this.mSpuTrack;
    }

    public void setSpuTrack(int track) {
        this.mSpuTrack = track;
    }

    public long getLength() {
        return this.mLength;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public Bitmap getPicture() {
        return this.mPicture;
    }

    public void setPicture(Bitmap p) {
        this.mPicture = p;
    }

    public boolean isPictureParsed() {
        return this.mIsPictureParsed;
    }

    public void setPictureParsed(boolean isParsed) {
        this.mIsPictureParsed = isParsed;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        if (!TextUtils.isEmpty(this.mTitle)) {
            return this.mTitle;
        }
        String fileName = getFileName();
        if (fileName == null) {
            return "";
        }
        int end = fileName.lastIndexOf(".");
        return end > 0 ? fileName.substring(0, end) : fileName;
    }

    public String getReferenceArtist() {
        return this.mAlbumArtist == null ? this.mArtist : this.mAlbumArtist;
    }

    public String getArtist() {
        return this.mArtist;
    }

    public Boolean isArtistUnknown() {
        return this.mArtist == null;
    }

    public String getGenre() {
        if (this.mGenre == null) {
            return null;
        }
        if (this.mGenre.length() > 1) {
            return String.valueOf(Character.toUpperCase(this.mGenre.charAt(0))) + this.mGenre.substring(1).toLowerCase(Locale.getDefault());
        }
        return this.mGenre;
    }

    public String getCopyright() {
        return this.mCopyright;
    }

    public String getAlbum() {
        return this.mAlbum;
    }

    public String getAlbumArtist() {
        return this.mAlbumArtist;
    }

    public Boolean isAlbumUnknown() {
        return this.mAlbum == null;
    }

    public int getTrackNumber() {
        return this.mTrackNumber;
    }

    public int getDiscNumber() {
        return this.mDiscNumber;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getRating() {
        return this.mRating;
    }

    public String getDate() {
        return this.mDate;
    }

    public String getSettings() {
        return this.mSettings;
    }

    public String getNowPlaying() {
        return this.mNowPlaying;
    }

    public String getPublisher() {
        return this.mPublisher;
    }

    public String getEncodedBy() {
        return this.mEncodedBy;
    }

    public String getTrackID() {
        return this.mTrackID;
    }

    public String getArtworkURL() {
        return this.mArtworkURL;
    }

    public long getLastModified() {
        return this.mLastModified;
    }

    public void setLastModified(long mLastModified) {
        this.mLastModified = mLastModified;
    }

    public void addFlags(int flags) {
        this.mFlags |= flags;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    public int getFlags() {
        return this.mFlags;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MediaWrapper(Parcel in) {
        this.mLocation = in.readString();
        init(in.readLong(), in.readLong(), in.readInt(), (Bitmap) in.readParcelable(Bitmap.class.getClassLoader()), in.readString(), in.readString(), in.readString(), in.readString(), in.readString(), in.readInt(), in.readInt(), in.readString(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readLong());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getLocation());
        dest.writeLong(getTime());
        dest.writeLong(getLength());
        dest.writeInt(getType());
        dest.writeParcelable(getPicture(), flags);
        dest.writeString(getTitle());
        dest.writeString(getArtist());
        dest.writeString(getGenre());
        dest.writeString(getAlbum());
        dest.writeString(getAlbumArtist());
        dest.writeInt(getWidth());
        dest.writeInt(getHeight());
        dest.writeString(getArtworkURL());
        dest.writeInt(getAudioTrack());
        dest.writeInt(getSpuTrack());
        dest.writeInt(getTrackNumber());
        dest.writeInt(getDiscNumber());
        dest.writeLong(getLastModified());
    }

    /* renamed from: org.videolan.vlc.MediaWrapper$1 */
    class AnonymousClass1 implements Parcelable.Creator<MediaWrapper> {
        AnonymousClass1() {
        }

        @Override // android.os.Parcelable.Creator
        public MediaWrapper createFromParcel(Parcel in) {
            return new MediaWrapper(in);
        }

        @Override // android.os.Parcelable.Creator
        public MediaWrapper[] newArray(int size) {
            return new MediaWrapper[size];
        }
    }
}
