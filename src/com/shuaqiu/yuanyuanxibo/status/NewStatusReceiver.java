/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.MainActivity;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu May 27, 2013
 */
public class NewStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "statusreceiver";

    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received broadcast that downloaded some new status");
        int count = intent.getIntExtra("count", 0);
        if (count == 0) {
            return;
        }

        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        builder.setContentTitle(context.getString(R.string.new_status, count));
        builder.setContentText(context.getString(R.string.to_show));
        builder.setNumber(count);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_launcher);

        PendingIntent pendingIntent = createPendingIntent(context);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        manager.notify(NOTIFICATION_ID, notification);
    }

    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Defs.Action.NEW_STATUS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // // Adds the back stack
        // stackBuilder.addParentStack(MainActivity.class);
        // // Adds the Intent to the top of the stack
        // stackBuilder.addNextIntent(intent);
        // // Gets a PendingIntent containing the entire back stack
        // return stackBuilder.getPendingIntent(0,
        // PendingIntent.FLAG_UPDATE_CURRENT);

        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
