package mktvsmart.screen.filebroswer;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import mktvsmart.screen.R;
import org.videolan.libvlc.Media;

/* loaded from: classes.dex */
public class MediaInfoAdapter extends ArrayAdapter<Media.Track> {
    public MediaInfoAdapter(Context context) {
        super(context, 0);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) throws Resources.NotFoundException {
        ViewHolder holder;
        String title;
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            v = inflater.inflate(R.layout.media_info_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) v.findViewById(R.id.title);
            holder.text = (TextView) v.findViewById(R.id.artist);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Media.Track track = getItem(position);
        StringBuilder textBuilder = new StringBuilder(1024);
        Resources res = getContext().getResources();
        switch (track.type) {
            case 0:
                title = res.getString(R.string.track_audio);
                appendCommon(textBuilder, res, track);
                appendAudio(textBuilder, res, (Media.AudioTrack) track);
                break;
            case 1:
                title = res.getString(R.string.track_video);
                appendCommon(textBuilder, res, track);
                appendVideo(textBuilder, res, (Media.VideoTrack) track);
                break;
            case 2:
                title = res.getString(R.string.track_text);
                appendCommon(textBuilder, res, track);
                break;
            default:
                title = res.getString(R.string.track_unknown);
                break;
        }
        holder.title.setText(title);
        holder.text.setText(textBuilder.toString());
        return v;
    }

    private void appendCommon(StringBuilder textBuilder, Resources res, Media.Track track) {
        textBuilder.append(res.getString(R.string.track_codec_info, track.codec));
        if (track.language != null && !track.language.equalsIgnoreCase("und")) {
            textBuilder.append(res.getString(R.string.track_language_info, track.language));
        }
    }

    private void appendAudio(StringBuilder textBuilder, Resources res, Media.AudioTrack track) {
        textBuilder.append(res.getQuantityString(R.plurals.track_channels_info_quantity, track.channels, Integer.valueOf(track.channels)));
        textBuilder.append(res.getString(R.string.track_samplerate_info, Integer.valueOf(track.rate)));
    }

    private void appendVideo(StringBuilder textBuilder, Resources res, Media.VideoTrack track) {
        double framerate = track.frameRateNum / track.frameRateDen;
        if (track.width != 0 && track.height != 0) {
            textBuilder.append(res.getString(R.string.track_resolution_info, Integer.valueOf(track.width), Integer.valueOf(track.height)));
        }
        if (!Double.isNaN(framerate)) {
            textBuilder.append(res.getString(R.string.track_framerate_info, Double.valueOf(framerate)));
        }
    }

    static class ViewHolder {
        TextView text;
        TextView title;

        ViewHolder() {
        }
    }
}
