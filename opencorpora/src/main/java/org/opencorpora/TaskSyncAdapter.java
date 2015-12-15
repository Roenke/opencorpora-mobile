package org.opencorpora;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class TaskSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "TaskSyncAdapter";
    public TaskSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Start sync");

        // ToDo: sync all

        Log.d(LOG_TAG, "Sync is completed");
    }
}
