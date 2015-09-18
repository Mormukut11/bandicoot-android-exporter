package edu.mit.media.bandicoot;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import edu.mit.media.bandicoot.metadata.Interaction;
import edu.mit.media.bandicoot.metadata.InteractionReader;

/**
 * Asynchronous task for reading logs and writing the output to a CSV.
 * Optionally updates a progress bar, if one is provided.
 *
 * Returns a file handle to the aforementioned CSV containing all interactions.
 *
 * @author Brian Sweatt
 */
public class LogReaderTask extends AsyncTask<Void, Void, File> {

    private InteractionReader reader;
    private Context context;
    private ProgressBar progressBar;

    public LogReaderTask(Context context, InteractionReader reader) {
        this(context, reader, null);
    }

    public LogReaderTask(Context context, InteractionReader reader, ProgressBar progressBar) {
        // Creating the cursors here, so that we can get the counts from them prior to reading
        this.context = context.getApplicationContext();
        this.reader = reader;
        this.progressBar = progressBar;
    }

    @Override
    protected File doInBackground(Void... nothing) {

        List<Interaction> entries = reader.getAllInteractions(progressBar);

        try {
            FileOutputStream os = context.openFileOutput("metadata.csv", Context.MODE_PRIVATE);
            os.write("interaction,direction,correspondent_id,datetime,call_duration,antenna_id\n".getBytes());
            for (Interaction entry : entries) {
                os.write((entry.toString() + "\n").getBytes());
            }
            if (progressBar != null) {
                // A Handler ensures that the progressBar methods run on the UI thread...
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progressBar.getMax());
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return context.getFileStreamPath("metadata.csv");
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
    }
}
