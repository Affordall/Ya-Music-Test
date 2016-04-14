package com.realjamapps.yamusicapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.realjamapps.yamusicapp.database.DatabaseHandler;
import com.realjamapps.yamusicapp.parsers.GetAllDataParser;
import com.realjamapps.yamusicapp.utils.Utils;


public class DownloadServiceIntent extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    DatabaseHandler handler;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadServiceIntent(String name) {
        super(name);
    }

    public DownloadServiceIntent() {
        super(DownloadServiceIntent.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        handler = DatabaseHandler.getInstance(this);

        Bundle bundle = new Bundle();

        if (Utils.isNetworkUnavailable()) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                new GetAllDataParser().getDataPlease(getApplicationContext());
                /* Sending result back to activity */
                if (!handler.isDBlocked()) {
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {
                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        this.stopSelf();
    }
}
