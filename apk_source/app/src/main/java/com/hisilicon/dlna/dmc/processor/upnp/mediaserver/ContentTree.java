package com.hisilicon.dlna.dmc.processor.upnp.mediaserver;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.hisilicon.dlna.dmc.gui.activity.AppPreference;
import com.hisilicon.dlna.dmc.processor.impl.PlaylistManager;
import com.hisilicon.dlna.dmc.processor.model.LocalAudio;
import com.hisilicon.dlna.dmc.processor.model.LocalPhoto;
import com.hisilicon.dlna.dmc.processor.model.LocalVideo;
import com.hisilicon.dlna.dmc.processor.model.PlaylistItem;
import com.hisilicon.dlna.dmc.utility.Utility;
import com.hisilicon.multiscreen.mybox.HiMultiscreen;
import com.hisilicon.multiscreen.protocol.message.MessageDef;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.support.model.DIDLAttribute;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.AudioItem;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;

/* loaded from: classes.dex */
public class ContentTree {
    public static final String ALL_AUDIO_ID = "5";
    public static final String ALL_IMAGE_ID = "6";
    public static final String ALL_VIDEO_ID = "4";
    public static final String AUDIO_FOLDER_ID = "8";
    public static final String AUDIO_FOLDER_PREFIX = "audio_folder_";
    public static final String AUDIO_ID = "2";
    public static final String IMAGE_FOLDER_ID = "9";
    public static final String IMAGE_FOLDER_PREFIX = "image_folder_";
    public static final String IMAGE_ID = "3";
    public static final String PLAYLIST_ID = "10";
    public static final String ROOT_ID = "0";
    public static final String VIDEO_FOLDER_ID = "7";
    public static final String VIDEO_FOLDER_PREFIX = "video_folder_";
    public static final String VIDEO_ID = "1";
    private static HashMap<String, ContentNode> contentMap = new HashMap<>();
    private static ContentNode rootNode = createRootNode();
    private static List<LocalPhoto> photos = new ArrayList();
    private static List<LocalVideo> videos = new ArrayList();
    private static List<LocalAudio> audios = new ArrayList();
    private static volatile boolean isStopLoading = false;

    public static void clear() {
        rootNode = createRootNode();
    }

    protected static ContentNode createRootNode() {
        contentMap.clear();
        Container root = new Container();
        root.setClazz(new DIDLObject.Class("object.container"));
        root.setId("0");
        root.setParentID(MessageDef.DEVICE_NAME_PORT);
        root.setTitle(String.valueOf(AppPreference.getDLNACreater()) + " root directory");
        root.setCreator(AppPreference.getDLNACreater());
        root.setRestricted(true);
        root.setSearchable(true);
        root.setWriteStatus(WriteStatus.NOT_WRITABLE);
        root.setChildCount(0);
        ContentNode rootNode2 = new ContentNode("0", root);
        contentMap.put("0", rootNode2);
        addRootContainer(rootNode2);
        return rootNode2;
    }

    private static void addRootContainer(ContentNode rootNode2) {
        Container imagesContainer = createImagesRootContainer();
        rootNode2.getContainer().addContainer(imagesContainer);
        rootNode2.getContainer().setChildCount(Integer.valueOf(rootNode2.getContainer().getChildCount().intValue() + 1));
        Container videosContainer = createVideosRootContainer();
        rootNode2.getContainer().addContainer(videosContainer);
        rootNode2.getContainer().setChildCount(Integer.valueOf(rootNode2.getContainer().getChildCount().intValue() + 1));
        Container audiosContainer = createAudiosRootContainer();
        rootNode2.getContainer().addContainer(audiosContainer);
        rootNode2.getContainer().setChildCount(Integer.valueOf(rootNode2.getContainer().getChildCount().intValue() + 1));
    }

    private static Container createImagesRootContainer() {
        Container imageContainer = new Container(IMAGE_ID, "0", "Image", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
        imageContainer.setRestricted(true);
        imageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(IMAGE_ID, new ContentNode(IMAGE_ID, imageContainer));
        addImageRootContainer(imageContainer);
        return imageContainer;
    }

    private static void addImageRootContainer(Container container) {
        Container imagesContainer = getOrCreateImageFolderLevelContainer();
        container.addContainer(imagesContainer);
        container.setChildCount(Integer.valueOf(container.getChildCount().intValue() + 1));
        Container allImagesFolderContainer = createAllImageLevelContainer();
        container.addContainer(allImagesFolderContainer);
        container.setChildCount(Integer.valueOf(container.getChildCount().intValue() + 1));
    }

    private static Container getOrCreateImageFolderLevelContainer() {
        Container imageContainer = getContainer(IMAGE_FOLDER_ID);
        if (imageContainer == null) {
            Container imageContainer2 = new Container(IMAGE_FOLDER_ID, IMAGE_ID, "Folder", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
            imageContainer2.setRestricted(true);
            imageContainer2.setWriteStatus(WriteStatus.NOT_WRITABLE);
            addNode(IMAGE_FOLDER_ID, new ContentNode(IMAGE_FOLDER_ID, imageContainer2));
            return imageContainer2;
        }
        return imageContainer;
    }

    private static Container createAllImageLevelContainer() {
        Container allPhotosContainer = new Container(ALL_IMAGE_ID, IMAGE_ID, "All Image", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container"), (Integer) 0);
        allPhotosContainer.setRestricted(true);
        allPhotosContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(ALL_IMAGE_ID, new ContentNode(ALL_IMAGE_ID, allPhotosContainer));
        return allPhotosContainer;
    }

    private static Container createVideosRootContainer() {
        Container videoContainer = new Container();
        videoContainer.setClazz(new DIDLObject.Class("object.container"));
        videoContainer.setId("1");
        videoContainer.setParentID("0");
        videoContainer.setTitle("Video");
        videoContainer.setRestricted(true);
        videoContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        videoContainer.setChildCount(0);
        addNode("1", new ContentNode("1", videoContainer));
        addVideoRootContainer(videoContainer);
        return videoContainer;
    }

    private static void addVideoRootContainer(Container container) {
        Container videosContainer = getOrCreateVideoFolderLevelContainer();
        container.addContainer(videosContainer);
        container.setChildCount(Integer.valueOf(container.getChildCount().intValue() + 1));
        Container allVideosFolderContainer = createAllVideoLevelContainer();
        container.addContainer(allVideosFolderContainer);
        container.setChildCount(Integer.valueOf(container.getChildCount().intValue() + 1));
    }

    private static Container getOrCreateVideoFolderLevelContainer() {
        Container videoContainer = getContainer(VIDEO_FOLDER_ID);
        if (videoContainer == null) {
            Container videoContainer2 = new Container(VIDEO_FOLDER_ID, "1", "Folder", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
            videoContainer2.setRestricted(true);
            videoContainer2.setWriteStatus(WriteStatus.NOT_WRITABLE);
            addNode(VIDEO_FOLDER_ID, new ContentNode(VIDEO_FOLDER_ID, videoContainer2));
            return videoContainer2;
        }
        return videoContainer;
    }

    private static Container createAllVideoLevelContainer() {
        Container allVideosContainer = new Container(ALL_VIDEO_ID, "1", "All Video", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container"), (Integer) 0);
        allVideosContainer.setRestricted(true);
        allVideosContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(ALL_VIDEO_ID, new ContentNode(ALL_VIDEO_ID, allVideosContainer));
        return allVideosContainer;
    }

    private static Container createAudiosRootContainer() {
        Container audioContainer = new Container(AUDIO_ID, "0", "Music", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container"), (Integer) 0);
        audioContainer.setRestricted(true);
        audioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(AUDIO_ID, new ContentNode(AUDIO_ID, audioContainer));
        addAudioRootContainer(audioContainer);
        return audioContainer;
    }

    private static void addAudioRootContainer(Container container) {
        Container audiosContainer = getOrCreateMusicFolderLevelContainer();
        container.addContainer(audiosContainer);
        container.setChildCount(Integer.valueOf(container.getChildCount().intValue() + 1));
        Container allAudiosFolderContainer = createAllMusicLevelContainer();
        container.addContainer(allAudiosFolderContainer);
        container.setChildCount(Integer.valueOf(container.getChildCount().intValue() + 1));
    }

    private static Container getOrCreateMusicFolderLevelContainer() {
        Container audioContainer = getContainer(AUDIO_FOLDER_ID);
        if (audioContainer == null) {
            Container imageContainer = new Container(AUDIO_FOLDER_ID, AUDIO_ID, "Folder", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
            imageContainer.setRestricted(true);
            imageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
            addNode(AUDIO_FOLDER_ID, new ContentNode(AUDIO_FOLDER_ID, imageContainer));
            return imageContainer;
        }
        return audioContainer;
    }

    private static Container createAllMusicLevelContainer() {
        Container allAudioContainer = new Container(ALL_AUDIO_ID, AUDIO_ID, "All Music", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container"), (Integer) 0);
        allAudioContainer.setRestricted(true);
        allAudioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(ALL_AUDIO_ID, new ContentNode(ALL_AUDIO_ID, allAudioContainer));
        return allAudioContainer;
    }

    public static ContentNode getRootNode() {
        return rootNode;
    }

    public static ContentNode getNode(String id) {
        if (contentMap.containsKey(id)) {
            return contentMap.get(id);
        }
        return null;
    }

    public static boolean hasNode(String id) {
        return contentMap.containsKey(id);
    }

    public static void addNode(String id, ContentNode node) {
        contentMap.put(id, node);
    }

    public static Container getContainer(String id) {
        ContentNode contentNode = getNode(id);
        if (contentNode != null) {
            return contentNode.getContainer();
        }
        return null;
    }

    public static void initAllMedia() throws MediaInitException {
        isStopLoading = false;
        try {
            initPhotos();
            initVideos();
            initAudios();
        } catch (MediaInitException ex) {
            photos.clear();
            videos.clear();
            audios.clear();
            throw new MediaInitException(ex);
        }
    }

    public static void initAudioAndMusic() throws MediaInitException {
        isStopLoading = false;
        try {
            initPhotos();
            initVideos();
            initAudios();
        } catch (MediaInitException ex) {
            photos.clear();
            videos.clear();
            audios.clear();
            throw new MediaInitException(ex);
        }
    }

    public static void initPhotos() throws MediaInitException {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        try {
            createPhotos(uri);
        } catch (MediaInitException exception) {
            throw new MediaInitException(exception);
        } catch (Exception exception2) {
            Log.w("photo init", "Could not load phone photos from " + uri.toString(), exception2);
        }
    }

    public static void createPhotos(Uri uri) throws MediaInitException {
        photos.clear();
        Calendar calendar = Calendar.getInstance();
        String[] imageColumns = {"_id", "title", "_data", "mime_type", "_size", "_display_name", "date_added"};
        Cursor cursor = HiMultiscreen.getResolver().query(uri, imageColumns, null, null, "datetaken COLLATE LOCALIZED DESC");
        if (cursor.moveToFirst()) {
            do {
                whetherStopLoading();
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow("mime_type"));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow("_size"));
                LocalPhoto photo = new LocalPhoto();
                photo.setId(id);
                photo.setTitle(title);
                photo.setMimeType(mimeType);
                photo.setPath(filePath);
                photo.setSize(size);
                File file = new File(photo.getPath());
                if (file.exists() && file.isFile() && file.getParentFile() != null) {
                    photo.setDateCreated(file.lastModified());
                    photo.setFolder(file.getParentFile().getName());
                    calendar.setTimeInMillis(photo.getDateCreated());
                    photo.setYear(String.valueOf(calendar.get(1)));
                    photo.setMonth(String.format("%02d", Integer.valueOf(calendar.get(2))));
                    photo.setDay(String.valueOf(calendar.get(5)));
                } else {
                    photo.setYear("1970");
                    photo.setMonth("01");
                    photo.setFolder("[Unknown]");
                }
                createPhotoAlbumDir(photo);
                createAllPhotoDir(photo);
                photos.add(photo);
            } while (cursor.moveToNext());
        }
    }

    public static void initVideos() throws MediaInitException {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        try {
            createVideos(uri);
        } catch (MediaInitException exception) {
            throw new MediaInitException(exception);
        } catch (Exception exception2) {
            Log.w("audio init", "Could not load phone audios from " + uri.toString(), exception2);
        }
    }

    private static void createVideos(Uri uri) throws MediaInitException {
        videos.clear();
        Calendar calendar = Calendar.getInstance();
        String[] videoColumns = {"_id", "title", "_data", "artist", "mime_type", "_size", "duration", "resolution", "_display_name", "date_added"};
        Cursor cursor = HiMultiscreen.getResolver().query(uri, videoColumns, null, null, "datetaken COLLATE LOCALIZED DESC");
        if (cursor.moveToFirst()) {
            do {
                whetherStopLoading();
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                String creator = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow("mime_type"));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow("_size"));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow("duration"));
                String resolution = cursor.getString(cursor.getColumnIndexOrThrow("resolution"));
                LocalVideo localVideo = new LocalVideo();
                localVideo.setId(id);
                localVideo.setTitle(title);
                localVideo.setMimeType(mimeType);
                localVideo.setArtist(creator);
                localVideo.setPath(filePath);
                localVideo.setSize(size);
                localVideo.setResolution(resolution);
                localVideo.setDuration(duration);
                File file = new File(localVideo.getPath());
                if (file.exists() && file.isFile() && file.getParentFile() != null) {
                    localVideo.setDateCreated(file.lastModified());
                    localVideo.setFolder(file.getParentFile().getName());
                    calendar.setTimeInMillis(localVideo.getDateCreated());
                    localVideo.setYear(String.valueOf(calendar.get(1)));
                    int mon = calendar.get(2);
                    String month = String.valueOf(mon);
                    if (mon < 9) {
                        month = "0" + mon;
                    }
                    localVideo.setMonth(month);
                    localVideo.setDay(String.valueOf(calendar.get(5)));
                } else {
                    if (localVideo.getYear() == null) {
                        localVideo.setYear("1970");
                    }
                    if (localVideo.getMonth() == null) {
                        localVideo.setMonth("01");
                    }
                    if (localVideo.getFolder() == null) {
                        localVideo.setFolder("[Unknown]");
                    }
                }
                String[] thumbnail = HttpServerUtil.queryVideoThumbIdAndData(new String[]{new StringBuilder(String.valueOf(id)).toString()});
                if (thumbnail[1] != null) {
                    File thumbnailFile = new File(thumbnail[1]);
                    if (thumbnailFile.exists() && thumbnailFile.isFile()) {
                        localVideo.setThumbPath(thumbnail[1]);
                        try {
                            localVideo.setThumbId(Long.parseLong(thumbnail[0]));
                        } catch (Exception e) {
                        }
                    }
                }
                createVideoAlbumDir(localVideo);
                createAllVideoDir(localVideo);
                videos.add(localVideo);
            } while (cursor.moveToNext());
        }
    }

    public static void initAudios() throws MediaInitException {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        try {
            createAudios(uri);
        } catch (MediaInitException exception) {
            throw new MediaInitException(exception);
        } catch (Exception exception2) {
            Log.w("audio init", "Could not load phone audios from " + uri.toString(), exception2);
        }
    }

    private static void createAudios(Uri uri) throws MediaInitException {
        audios.clear();
        String[] audioColumns = {"_id", "title", "_data", "artist", "mime_type", "_size", "duration", "album", "album_id", "_display_name", "date_added"};
        Cursor cursor = HiMultiscreen.getResolver().query(uri, audioColumns, null, null, "date_added COLLATE LOCALIZED DESC");
        if (cursor.moveToFirst()) {
            do {
                whetherStopLoading();
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                String creator = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow("mime_type"));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow("_size"));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow("duration"));
                String album = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                long album_id = cursor.getLong(cursor.getColumnIndex("album_id"));
                LocalAudio localAudio = new LocalAudio();
                localAudio.setId(id);
                localAudio.setTitle(title);
                localAudio.setMimeType(mimeType);
                localAudio.setPath(filePath);
                localAudio.setSize(size);
                localAudio.setDuration(duration);
                localAudio.setArtist(creator);
                localAudio.setAlbum(album);
                localAudio.setAlbumId(album_id);
                File file = new File(localAudio.getPath());
                if (file.exists() && file.isFile() && file.getParentFile() != null) {
                    localAudio.setFolder(file.getParentFile().getName());
                } else {
                    localAudio.setFolder("[Unknown]");
                }
                String genres = getGenres(id);
                localAudio.setGenre(genres);
                String albumArtPath = getAlbumArt((int) album_id);
                if (albumArtPath != null) {
                    File albumArtFile = new File(albumArtPath);
                    if (albumArtFile.exists() && albumArtFile.isFile()) {
                        localAudio.setAlbumArt(albumArtPath);
                    }
                }
                createAudioAlbumDir(localAudio);
                createAllAudioDir(localAudio);
                audios.add(localAudio);
            } while (cursor.moveToNext());
        }
    }

    public static String getGenres(long audioId) {
        Uri uri = Uri.parse("content://media/external/audio/media/" + audioId + "/genres");
        Cursor c = HiMultiscreen.getResolver().query(uri, new String[]{"name"}, null, null, null);
        String genre = null;
        if (c.moveToFirst()) {
            genre = c.getString(c.getColumnIndex("name"));
        }
        if (c != null) {
            c.close();
        }
        return genre;
    }

    private static String getAlbumArt(int albumid) {
        String[] projection = {"album_art"};
        Cursor cur = HiMultiscreen.getResolver().query(Uri.parse(String.valueOf("content://media/external/audio/albums") + ServiceReference.DELIMITER + Integer.toString(albumid)), projection, null, null, null);
        String strPath = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            strPath = cur.getString(0);
        }
        if (cur != null) {
            cur.close();
        }
        return strPath;
    }

    public static void initPlaylist() {
        List<PlaylistItem> playlistItems = PlaylistManager.getAllPlaylistItem();
        System.out.println("The playlistItems size is:" + playlistItems.size());
        Container playlistContainer = getOrCreatePlaylistContainer();
        for (PlaylistItem playlistItem : playlistItems) {
            try {
                DIDLContent didlObject = Utility.createDIDL(playlistItem.getMetaData());
                Item item = didlObject.getItems().get(0);
                Res res = item.getFirstResource();
                if (playlistItem.fromLocalType()) {
                    res.setValue(playlistItem.getUrl());
                }
                playlistContainer.addItem(item);
                playlistContainer.setChildCount(Integer.valueOf(playlistContainer.getChildCount().intValue() + 1));
            } catch (Exception e) {
            }
        }
    }

    private static Container getOrCreatePlaylistContainer() {
        Container playlistContainer = getContainer(PLAYLIST_ID);
        if (playlistContainer == null) {
            Container playlistContainer2 = new Container(PLAYLIST_ID, AUDIO_FOLDER_ID, "Play List", AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
            playlistContainer2.setRestricted(true);
            playlistContainer2.setWriteStatus(WriteStatus.NOT_WRITABLE);
            addNode(PLAYLIST_ID, new ContentNode(PLAYLIST_ID, playlistContainer2));
            Container audioFolder = getOrCreateMusicFolderLevelContainer();
            audioFolder.addContainer(playlistContainer2);
            audioFolder.setChildCount(Integer.valueOf(audioFolder.getChildCount().intValue() + 1));
            return playlistContainer2;
        }
        return playlistContainer;
    }

    public static void createPhotosAlbumsDir() throws MediaInitException {
        isStopLoading = false;
        System.out.println("Come into createPhotosAlbumsDir start--><");
        for (LocalPhoto localPhoto : photos) {
            whetherStopLoading();
            createPhotoAlbumDir(localPhoto);
        }
        System.out.println("Come into createPhotosAlbumsDir end--><");
    }

    public static void createAllPhotoDir(LocalPhoto localPhoto) {
        Container allPhotosContainer = getOrAddNewAllImageFolderContainer();
        createPhoto(allPhotosContainer, localPhoto);
    }

    private static Container getOrAddNewAllImageFolderContainer() {
        Container folderContainer = getContainer(ALL_IMAGE_ID);
        if (folderContainer == null) {
            Container container = createAllImageLevelContainer();
            Container imagesContainer = getNode(IMAGE_ID).getContainer();
            if (imagesContainer == null) {
                imagesContainer = createImagesRootContainer();
            }
            imagesContainer.addContainer(container);
            imagesContainer.setChildCount(Integer.valueOf(imagesContainer.getChildCount().intValue() + 1));
            return container;
        }
        return folderContainer;
    }

    private static void createPhotoAlbumDir(LocalPhoto localPhoto) {
        String folderStr = localPhoto.getFolder();
        String folder_id = IMAGE_FOLDER_PREFIX + folderStr;
        Container folderContainer = getOrAddNewImageFolderContainer(folder_id, folderStr);
        createPhoto(folderContainer, localPhoto);
    }

    private static void createPhoto(Container folderContainer, LocalPhoto localPhoto) {
        long photo_id = localPhoto.getId();
        String item_id = HttpServerUtil.makeMediaId(localPhoto.getPath());
        String link = HttpServerUtil.createLinkWithId(item_id);
        Res res = new Res(MimeType.valueOf(localPhoto.getMimeType()), Long.valueOf(localPhoto.getSize()), link);
        ImageItem imageItem = new ImageItem(new StringBuilder(String.valueOf(photo_id)).toString(), folderContainer.getId(), localPhoto.getTitle(), "unkonwn", res);
        String thumb_id = HttpServerUtil.makeMediaId(localPhoto.getThumbPath());
        if (thumb_id != null) {
            String thumbLink = HttpServerUtil.createLinkWithId(thumb_id);
            addObjectAlbumArtProperty(imageItem, thumbLink);
        }
        folderContainer.addItem(imageItem);
        folderContainer.setChildCount(Integer.valueOf(folderContainer.getChildCount().intValue() + 1));
        addNode(new StringBuilder(String.valueOf(localPhoto.getPath())).toString(), new ContentNode(new StringBuilder(String.valueOf(localPhoto.getPath())).toString(), imageItem, localPhoto.getPath()));
    }

    private static Container getOrAddNewImageFolderContainer(String id, String title) {
        Container folderContainer = getContainer(id);
        return folderContainer != null ? folderContainer : addNewImageFolderContainer(id, title);
    }

    private static Container addNewImageFolderContainer(String id, String title) {
        Container folderContainer = new Container(id, IMAGE_FOLDER_ID, title, AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
        folderContainer.setRestricted(true);
        folderContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(id, new ContentNode(id, folderContainer));
        Container imageContainer = getNode(IMAGE_FOLDER_ID).getContainer();
        imageContainer.addContainer(folderContainer);
        imageContainer.setChildCount(Integer.valueOf(imageContainer.getChildCount().intValue() + 1));
        return folderContainer;
    }

    public static void createVideosAlbumsDir() throws MediaInitException {
        isStopLoading = false;
        for (LocalVideo localVideo : videos) {
            whetherStopLoading();
            createVideoAlbumDir(localVideo);
            createAllVideoDir(localVideo);
        }
    }

    private static void createVideoAlbumDir(LocalVideo localVideo) {
        String folderStr = localVideo.getFolder();
        String folder_id = VIDEO_FOLDER_PREFIX + folderStr;
        Container folderContainer = getOrAddNewVideoFolderContainer(folder_id, folderStr);
        createVideo(folderContainer, localVideo);
    }

    private static Container getOrAddNewVideoFolderContainer(String id, String title) {
        Container folderContainer = getContainer(id);
        return folderContainer != null ? folderContainer : addNewVideoFolderContainer(id, title);
    }

    private static Container addNewVideoFolderContainer(String id, String title) {
        Container folderContainer = new Container(id, VIDEO_FOLDER_ID, title, AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
        folderContainer.setRestricted(true);
        folderContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(id, new ContentNode(id, folderContainer));
        Container videoContainer = getNode(VIDEO_FOLDER_ID).getContainer();
        videoContainer.addContainer(folderContainer);
        videoContainer.setChildCount(Integer.valueOf(videoContainer.getChildCount().intValue() + 1));
        return folderContainer;
    }

    public static void createAllVideoDir(LocalVideo localVideo) {
        Container allVideosContainer = getOrAddNewAllVideoFolderContainer();
        createVideo(allVideosContainer, localVideo);
    }

    private static Container getOrAddNewAllVideoFolderContainer() {
        Container folderContainer = getContainer(ALL_VIDEO_ID);
        if (folderContainer == null) {
            Container container = createAllVideoLevelContainer();
            Container imagesContainer = getNode("1").getContainer();
            if (imagesContainer == null) {
                imagesContainer = createImagesRootContainer();
            }
            imagesContainer.addContainer(container);
            imagesContainer.setChildCount(Integer.valueOf(imagesContainer.getChildCount().intValue() + 1));
            return container;
        }
        return folderContainer;
    }

    private static void createVideo(Container videoContainer, LocalVideo localVideo) {
        long video_id = localVideo.getId();
        String item_id = HttpServerUtil.makeMediaId(localVideo.getPath());
        String link = HttpServerUtil.createLinkWithId(item_id);
        Res res = new Res(MimeType.valueOf(localVideo.getMimeType()), Long.valueOf(localVideo.getSize()), ModelUtil.toTimeString(localVideo.getDuration() / 1000), (Long) 0L, link);
        VideoItem videoItem = new VideoItem(new StringBuilder(String.valueOf(video_id)).toString(), videoContainer.getId(), localVideo.getTitle(), localVideo.getArtist(), res);
        String thumb_id = HttpServerUtil.makeMediaId(localVideo.getThumbPath());
        if (thumb_id != null) {
            String thumbLink = HttpServerUtil.createLinkWithId(thumb_id);
            addObjectAlbumArtProperty(videoItem, thumbLink);
        }
        videoContainer.addItem(videoItem);
        videoContainer.setChildCount(Integer.valueOf(videoContainer.getChildCount().intValue() + 1));
        addNode(new StringBuilder(String.valueOf(localVideo.getPath())).toString(), new ContentNode(new StringBuilder(String.valueOf(localVideo.getPath())).toString(), videoItem, localVideo.getPath()));
    }

    public static void createAudiosAlbumsDir() throws MediaInitException {
        isStopLoading = false;
        for (LocalAudio localAudio : audios) {
            whetherStopLoading();
            createAudioAlbumDir(localAudio);
            createAllAudioDir(localAudio);
        }
    }

    private static void createAudioAlbumDir(LocalAudio localAudio) {
        String folderStr = localAudio.getFolder();
        String folder_id = AUDIO_FOLDER_PREFIX + folderStr;
        Container folderContainer = getOrAddNewAudioFolderContainer(folder_id, folderStr);
        createAudio(folderContainer, localAudio);
    }

    private static void createAudio(Container folderContainer, LocalAudio localAudio) {
        long audio_id = localAudio.getId();
        String item_id = HttpServerUtil.makeMediaId(localAudio.getPath());
        String link = HttpServerUtil.createLinkWithId(item_id);
        Res res = new Res(MimeType.valueOf(localAudio.getMimeType()), Long.valueOf(localAudio.getSize()), ModelUtil.toTimeString(localAudio.getDuration() / 1000), (Long) 0L, link);
        AudioItem audioItem = new AudioItem(new StringBuilder(String.valueOf(audio_id)).toString(), folderContainer.getId(), localAudio.getTitle(), localAudio.getArtist(), res);
        audioItem.addProperty(new DIDLObject.Property.UPNP.ARTIST(new PersonWithRole(localAudio.getArtist())));
        audioItem.addProperty(new DIDLObject.Property.UPNP.ALBUM(localAudio.getAlbum()));
        audioItem.addProperty(new DIDLObject.Property.UPNP.GENRE(localAudio.getGenre()));
        String thumb_id = HttpServerUtil.makeMediaId(localAudio.getAlbumArt());
        if (thumb_id != null) {
            String thumbLink = HttpServerUtil.createLinkWithId(thumb_id);
            addObjectAlbumArtProperty(audioItem, thumbLink);
        }
        folderContainer.addItem(audioItem);
        folderContainer.setChildCount(Integer.valueOf(folderContainer.getChildCount().intValue() + 1));
        addNode(new StringBuilder(String.valueOf(localAudio.getPath())).toString(), new ContentNode(new StringBuilder(String.valueOf(localAudio.getPath())).toString(), audioItem, localAudio.getPath()));
    }

    private static Container getOrAddNewAudioFolderContainer(String id, String title) {
        Container folderContainer = getContainer(id);
        return folderContainer != null ? folderContainer : addNewAudioFolderContainer(id, title);
    }

    private static Container addNewAudioFolderContainer(String id, String title) {
        Container folderContainer = new Container(id, AUDIO_FOLDER_ID, title, AppPreference.getDLNACreater(), new DIDLObject.Class("object.container.storageFolder"), (Integer) 0);
        folderContainer.setRestricted(true);
        folderContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
        addNode(id, new ContentNode(id, folderContainer));
        Container audioContainer = getNode(AUDIO_FOLDER_ID).getContainer();
        audioContainer.addContainer(folderContainer);
        audioContainer.setChildCount(Integer.valueOf(audioContainer.getChildCount().intValue() + 1));
        return folderContainer;
    }

    public static void createAllAudioDir(LocalAudio localAudio) {
        Container allAudiosContainer = getOrAddNewAllAudioFolderContainer();
        createAudio(allAudiosContainer, localAudio);
    }

    private static Container getOrAddNewAllAudioFolderContainer() {
        Container folderContainer = getContainer(ALL_AUDIO_ID);
        if (folderContainer == null) {
            Container container = createAllMusicLevelContainer();
            Container imagesContainer = getNode(AUDIO_ID).getContainer();
            if (imagesContainer == null) {
                imagesContainer = createImagesRootContainer();
            }
            imagesContainer.addContainer(container);
            imagesContainer.setChildCount(Integer.valueOf(imagesContainer.getChildCount().intValue() + 1));
            return container;
        }
        return folderContainer;
    }

    private static void addObjectAlbumArtProperty(DIDLObject didlobject, String link) {
        try {
            URI uri = new URI(link);
            ArrayList arraylist = new ArrayList();
            arraylist.add(new DIDLObject.Property.DLNA.PROFILE_ID(new DIDLAttribute(DIDLObject.Property.DLNA.NAMESPACE.URI, Descriptor.Device.DLNA_PREFIX, "PNG_TN")));
            didlobject.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(uri, arraylist));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static DIDLObject getImageItemFromPath(String path) {
        if (path.startsWith("/sdcard")) {
            path = "/mnt/sdcard" + path.substring(7);
        }
        File subFile = new File(path);
        String item_id = HttpServerUtil.makeMediaId(path);
        String link = HttpServerUtil.createLinkWithId(item_id);
        ImageItem imageItem = new ImageItem("0/3/" + subFile.getName(), IMAGE_FOLDER_PREFIX + subFile.getParent(), subFile.getName(), AppPreference.getDLNACreater(), new Res(new ProtocolInfo("http-get:*:image/*:*"), Long.valueOf(subFile.length()), link));
        addNode(subFile.getAbsolutePath(), new ContentNode(subFile.getAbsolutePath(), imageItem, subFile.getAbsolutePath()));
        return imageItem;
    }

    public static DIDLObject getVideoItemFromPath(String path) {
        if (path.startsWith("/sdcard")) {
            path = "/mnt/sdcard" + path.substring(7);
        }
        File subFile = new File(path);
        String item_id = HttpServerUtil.makeMediaId(path);
        String link = HttpServerUtil.createLinkWithId(item_id);
        VideoItem videoItem = new VideoItem("0/2/" + subFile.getName(), VIDEO_FOLDER_PREFIX + subFile.getParent(), subFile.getName(), AppPreference.getDLNACreater(), new Res(new ProtocolInfo("http-get:*:video/*:*"), Long.valueOf(subFile.length()), link));
        addNode(subFile.getAbsolutePath(), new ContentNode(subFile.getAbsolutePath(), videoItem, subFile.getAbsolutePath()));
        return videoItem;
    }

    public static DIDLObject getAudioItemFromPath(String path) {
        if (path.startsWith("/sdcard")) {
            path = "/mnt/sdcard" + path.substring(7);
        }
        File subFile = new File(path);
        String item_id = HttpServerUtil.makeMediaId(path);
        String link = HttpServerUtil.createLinkWithId(item_id);
        AudioItem audioItem = new AudioItem("0/1/" + subFile.getName(), AUDIO_FOLDER_PREFIX + subFile.getParent(), subFile.getName(), AppPreference.getDLNACreater(), new Res(new ProtocolInfo("http-get:*:audio/*:*"), Long.valueOf(subFile.length()), link));
        addNode(subFile.getAbsolutePath(), new ContentNode(subFile.getAbsolutePath(), audioItem, subFile.getAbsolutePath()));
        return audioItem;
    }

    public static void stopLoading() {
        isStopLoading = true;
    }

    private static void whetherStopLoading() throws MediaInitException {
        if (isStopLoading) {
            throw new MediaInitException("media stop loading");
        }
    }
}
