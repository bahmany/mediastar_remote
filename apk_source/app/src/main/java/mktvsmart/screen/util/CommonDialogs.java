package mktvsmart.screen.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class CommonDialogs {
    public static AlertDialog deleteMedia(Context context, String addressMedia, final GRunnable runnable) {
        URI adressMediaUri = null;
        try {
            URI adressMediaUri2 = new URI(addressMedia);
            adressMediaUri = adressMediaUri2;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final File fileMedia = new File(adressMediaUri);
        return confirmDialog(context, context.getResources().getString(R.string.confirm_delete, fileMedia.getName()), new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.util.CommonDialogs.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                fileMedia.delete();
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    public static AlertDialog confirmDialog(Context context, String confirmationString, DialogInterface.OnClickListener callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(R.string.validation).setMessage(confirmationString).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, callback).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).create();
        return alertDialog;
    }
}
