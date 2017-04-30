package pt.ulisboa.tecnico.cmu.locmess.session;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Random;

import pt.ulisboa.tecnico.cmu.locmess.R;

public class MyService extends Service {
    public MyService() { }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service was Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        new Thread(runnable).start();

        return START_STICKY;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                Thread.sleep(15000);
            }
            catch(Exception e){ }

            createNotification();
        }
    };

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();

    }

    public void getLocations(){

    }

    public void createNotification() {
        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.common_ic_googleplayservices)
                    .setContentTitle("New message received!")
                    .setContentText("Message content...")
                    .setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), mBuilder.build());
    }
}
