package edu.mit.media.bandicoot.metadata;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to read interaction metadata from call logs and SMS
 *
 * @author Brian Sweatt
 */
public class InteractionReader {
    private Cursor callLogCursor;
    private Cursor smsCursor;
    private Context context;
    private InteractionFactory interactionFactory;

    public InteractionReader(Context context, boolean hashNumbers) {
        // Creating the cursors here, so that we can get the counts from them prior to reading
        this.context = context;
        this.interactionFactory = new InteractionFactory(hashNumbers);
        createCursorsIfNecessary();
    }

    public List<Interaction> getAllInteractions(ProgressBar progressBar) {
        List<Interaction> entries = new ArrayList<Interaction>();

        createCursorsIfNecessary();

        if (progressBar != null) {
            progressBar.setMax((getCallLogCount() + getSmsCount()) * 2);
        }

        int i = 0;
        while (callLogCursor.moveToNext()) {
            entries.add(interactionFactory.getCallInteraction(callLogCursor));
            i++;
            if (progressBar != null && i % 10 == 0) {
                progressBar.setProgress(i);
            }
        }

        callLogCursor.close();

        while (smsCursor.moveToNext()) {
            entries.add(interactionFactory.getTextInteraction(smsCursor));
            i++;
            if (progressBar != null && i % 10 == 0) {
                progressBar.setProgress(i);
            }
        }

        smsCursor.close();

        Collections.sort(entries);
        return entries;
    }

    private void createCursorsIfNecessary() {
        if (callLogCursor == null || callLogCursor.isClosed()) {
            callLogCursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " ASC"
            );
        }

        if (smsCursor == null || smsCursor.isClosed()) {
            // NOTE: using a hard-coded content URI, since the SMS URI isn't public below KitKat
            smsCursor = context.getContentResolver().query(
                Uri.parse("content://sms"),
                null,
                null,
                null,
                null
            );
        }
    }

    public int getCallLogCount() {
        return callLogCursor.getCount();
    }

    public int getSmsCount() {
        return smsCursor.getCount();
    }

    public void setHashNumbers(boolean hashNumbers) {
        this.interactionFactory.setHashNumbers(hashNumbers);
    }
}
