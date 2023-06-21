package mm.battery.information;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    public TextView top_current_now = null;
    public TextView charge_counter  = null;
    public TextView current_now     = null;
    public TextView health          = null;
    public TextView percentage      = null;
    public TextView plugged_in      = null;
    public TextView present         = null;
    public TextView status          = null;
    public TextView technology      = null;
    public TextView temperature     = null;
    public TextView voltage         = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), StatusUpdater.class));

        top_current_now = findViewById(R.id.top_current_now);
        charge_counter  = findViewById(R.id.charge_counter);
        current_now     = findViewById(R.id.current_now);
        health          = findViewById(R.id.health);
        percentage      = findViewById(R.id.percentage);
        plugged_in      = findViewById(R.id.plugged_in);
        present         = findViewById(R.id.present);
        status          = findViewById(R.id.status);
        technology      = findViewById(R.id.technology);
        temperature     = findViewById(R.id.temperature);
        voltage         = findViewById(R.id.voltage);
    }

    private final BroadcastReceiver m_status_receiver = new BroadcastReceiver()
    {
        private void setTextViewValue(TextView view, String value)
        {
            if (view == null || value == null)
                return;
            view.setText(value);
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent == null)
                return;
            if (!intent.getAction().equals("mm.battery.information.status.update"))
                return;
            Bundle bundle = intent.getBundleExtra("BatteryStatusBundle");
            if (bundle == null)
                return;
            setTextViewValue(top_current_now, bundle.getString("current_now", ""));
            setTextViewValue(charge_counter, bundle.getString("charge_counter", ""));
            setTextViewValue(current_now, bundle.getString("current_now", ""));
            setTextViewValue(health, bundle.getString("health", ""));
            setTextViewValue(percentage, bundle.getString("percentage", ""));
            setTextViewValue(plugged_in, bundle.getString("plugged_in", "No"));
            setTextViewValue(present, bundle.getString("present", ""));
            setTextViewValue(status, bundle.getString("status", ""));
            setTextViewValue(technology, bundle.getString("technology", ""));
            setTextViewValue(temperature, bundle.getString("temperature", ""));
            setTextViewValue(voltage, bundle.getString("voltage", ""));
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(m_status_receiver, new IntentFilter("mm.battery.information.status.update"));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(m_status_receiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), StatusUpdater.class));
    }
}