package pt.ulisboa.tecnico.cmu.locmess.session;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import pt.ulisboa.tecnico.cmu.locmess.R;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageModel;
import pt.ulisboa.tecnico.cmu.locmess.main.messages.MessageViewer;
import pt.ulisboa.tecnico.cmu.locmess.session.data.Locations;
import pt.ulisboa.tecnico.cmu.locmess.session.data.Messages;
import pt.ulisboa.tecnico.cmu.locmess.session.data.Profile;
import pt.ulisboa.tecnico.cmu.locmess.session.data.Wifis;
import pt.ulisboa.tecnico.cmu.locmess.session.wifidirect.WifiDirect;

public class LocMessService extends Service{

    public static SharedPreferences prefs;
    private static LocMessService singleton;

    private Locations locations;
    private Wifis wifis;
    private WifiDirect wifiDirect;
    private Messages messages;

    public static LocMessService getInstance(){
        return singleton;
    }

    public Locations LOCATIONS(){ return locations; }
    public Wifis WIFIS(){ return wifis; }
    public Messages MESSAGES(){ return messages; }

    private Runnable background = new Runnable() {
        @Override
        public void run() {
            while(true) {

                try { Thread.sleep(5000); }
                catch (Exception e) { }

                wifiDirect.getDevices();
                wifis.update();
                locations.give();
                locations.update();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        singleton = this;
        prefs = getSharedPreferences(Session.APP_NAME,getApplicationContext().MODE_PRIVATE);
        locations = new Locations(this);
        wifis = new Wifis(this);
        wifiDirect = new WifiDirect(this);
        messages = new Messages(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(background).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {}


    public void notification(MessageModel msg) {

        final Intent intent = new Intent(this, MessageViewer.class);
        intent.putExtra("id",msg.getId());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New message received!")
                        .setContentText("From: "+msg.getUser())
                        .setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), mBuilder.build());
    }

}
