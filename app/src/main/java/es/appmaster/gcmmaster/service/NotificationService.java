package es.appmaster.gcmmaster.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;

import es.appmaster.gcmmaster.MainActivity;
import es.appmaster.gcmmaster.R;
import es.appmaster.gcmmaster.receiver.GcmReceiver;

public class NotificationService extends IntentService {

    private NotificationManager notificationManager;

    public NotificationService() {
        super("NotificationWorkerThread");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Starting service", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        showNotification(extras);

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(Bundle data) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String content = data.getString("message");
        String title = data.getString("user") + " says:";

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pendingIntent);
        notificationManager.notify(1, mBuilder.build());
    }

}
