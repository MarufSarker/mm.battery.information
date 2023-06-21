package mm.battery.information;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class StatusUpdater extends Service
{
    private final BatteryStatus m_status = new BatteryStatus();
    private final Handler m_update_handler = new Handler();
    private Runnable m_update_runnable = null;
    private final long m_update_delay = 2 * 1000;
    private static boolean m_service_running = false;

    private NotificationChannel m_notification_channel = null;
    private NotificationManager m_notification_manager = null;
    private NotificationCompat.Builder m_notification_builder = null;
    private PendingIntent m_main_activity_pending_intent = null;
    boolean m_notification_running = false;
    final int m_notification_id = 1000;

    public StatusUpdater() {}

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (m_update_runnable == null)
            m_update_runnable = () ->
            {
                m_status.update(getApplicationContext());
                updateNotification();
                Intent broadcast_intent = new Intent("mm.battery.information.status.update");
                broadcast_intent.putExtra("BatteryStatusBundle", m_status.bundle);
                sendBroadcast(broadcast_intent);
                m_update_handler.postDelayed(m_update_runnable, m_update_delay);
            };

        if (!m_service_running)
        {
            m_update_handler.postDelayed(m_update_runnable, m_update_delay);
            Toast.makeText(this, "Started Battery Status Updater [" + android.os.Process.myPid() + "]", Toast.LENGTH_SHORT).show();
            m_service_running = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        m_update_handler.removeCallbacks(m_update_runnable);
        m_update_runnable = null;
        m_service_running = false;
        m_notification_running = false;
        if (m_notification_manager != null)
        {
            m_notification_manager.cancel(m_notification_id);
            m_notification_manager.cancelAll();
        }
        stopForeground(true);
        Toast.makeText(this, "Stopped Battery Status Updater [" + android.os.Process.myPid() + "]", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void updateNotification()
    {
        final String channel_id = "mm.battery.information.notification";
        final String channel_name = "Current Now";

        if (m_notification_channel == null)
        {
            m_notification_channel = new NotificationChannel(channel_id, channel_name,
                NotificationManager.IMPORTANCE_DEFAULT);
            m_notification_channel.enableLights(false);
            m_notification_channel.enableVibration(false);
        }

        if (m_notification_manager == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                m_notification_manager = getApplicationContext().getSystemService(NotificationManager.class);
            else
                m_notification_manager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (m_notification_manager == null)
                return;
            m_notification_manager.createNotificationChannel(m_notification_channel);
        }

        if (m_main_activity_pending_intent == null)
            m_main_activity_pending_intent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        if (m_notification_builder == null)
            m_notification_builder = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.battery_information)
                .setContentTitle(getText(R.string.app_name))
                .setContentIntent(m_main_activity_pending_intent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false);

        String str = m_status.current_now
            + " [" + m_status.charge_counter + "] [" + m_status.percentage + "]";

        m_notification_builder.setContentText(str);

        boolean neg = m_status.current_now_int < 0;
        int max_range = 1000;
        int val = Math.min(Math.abs(m_status.current_now_int), max_range);
        int icon_id;

        if (val < (max_range * 0.10))
            icon_id = neg ? R.drawable.charge_negative_0 : R.drawable.charge_positive_0;
        else if (val < (max_range * 0.20))
            icon_id = neg ? R.drawable.charge_negative_10 : R.drawable.charge_positive_10;
        else if (val < (max_range * 0.30))
            icon_id = neg ? R.drawable.charge_negative_20 : R.drawable.charge_positive_20;
        else if (val < (max_range * 0.40))
            icon_id = neg ? R.drawable.charge_negative_30 : R.drawable.charge_positive_30;
        else if (val < (max_range * 0.50))
            icon_id = neg ? R.drawable.charge_negative_40 : R.drawable.charge_positive_40;
        else if (val < (max_range * 0.60))
            icon_id = neg ? R.drawable.charge_negative_50 : R.drawable.charge_positive_50;
        else if (val < (max_range * 0.70))
            icon_id = neg ? R.drawable.charge_negative_60 : R.drawable.charge_positive_60;
        else if (val < (max_range * 0.80))
            icon_id = neg ? R.drawable.charge_negative_70 : R.drawable.charge_positive_70;
        else if (val < (max_range * 0.90))
            icon_id = neg ? R.drawable.charge_negative_80 : R.drawable.charge_positive_80;
        else if (val < (max_range * 1.00))
            icon_id = neg ? R.drawable.charge_negative_90 : R.drawable.charge_positive_90;
        else
            icon_id = neg ? R.drawable.charge_negative_100 : R.drawable.charge_positive_100;

        m_notification_builder.setSmallIcon(icon_id);

        if (m_notification_running)
            m_notification_manager.notify(m_notification_id, m_notification_builder.build());
        else
            startForeground(m_notification_id, m_notification_builder.build());

        m_notification_running = true;
    }
}