package mm.battery.information;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

public class BatteryStatus
{
    public String charge_counter     = "";
    public String charging           = "";
    public String current_average    = "";
    public String current_now        = "";
    public String energy             = "";
    public String health             = "";
    public String level              = "";
    public String percentage         = "";
    public String plugged_in         = "";
    public String present            = "";
    public String remaining_capacity = "";
    public String scale              = "";
    public String status             = "";
    public String technology         = "";
    public String temperature        = "";
    public String voltage            = "";
    public int current_now_int       = 0;
    public boolean charging_boolean  = false;
    public Bundle bundle             = new Bundle();

    private void reset()
    {
        charge_counter     = "";
        charging           = "";
        current_average    = "";
        current_now        = "";
        energy             = "";
        health             = "";
        level              = "";
        percentage         = "";
        plugged_in         = "";
        present            = "";
        remaining_capacity = "";
        scale              = "";
        status             = "";
        technology         = "";
        temperature        = "";
        voltage            = "";
        current_now_int    = 0;
        charging_boolean   = false;
        bundle.clear();
    }


    @SuppressLint("ObsoleteSdkInt")
    public void update(Context context)
    {
        reset();

        if (context == null)
            return;

        BatteryManager manager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            manager = context.getSystemService(BatteryManager.class);
        else
            manager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

        if (manager == null)
            return;

        // charge_counter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            charge_counter = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                + " mAh";
        else
            charge_counter = "";

        // charging
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            charging_boolean = manager.isCharging();
            charging         = charging_boolean ? "Yes" : "No";
        }
        else
        {
            charging_boolean = false;
            charging = "";
        }

        // current_average
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            current_average = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
                + " mAh";
        else
            current_average = "";

        // current_now
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            current_now_int = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            current_now = current_now_int + " mAh";
        }
        else
        {
            current_now_int = 0;
            current_now = "";
        }

        // energy
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            energy = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
                + " nWh";
        else
            energy = "";

        Intent intent = context.registerReceiver(null,
            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (intent == null)
            return;

        // health
        switch (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1))
        {
            case BatteryManager.BATTERY_HEALTH_COLD:
                health = "Cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health = "Overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                health = "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                health = "Unspecified Failure";
                break;
            default:
                health = "";
                break;
        }

        // level
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) + " %";

        // percentage
        percentage = (int) ((
            intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) /
                (float) intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        ) * 100) + " %";

        // plugged_in
        switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1))
        {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugged_in = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugged_in = "USB";
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                plugged_in = "Wireless";
                break;
            default:
                plugged_in = "";
                break;
        }

        // present
        present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false) ? "Yes" : "No";

        // remaining_capacity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            remaining_capacity = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                + " %";
        else
            remaining_capacity = "";

        // scale
        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) + " %";

        // status
        switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1))
        {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                status = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                status = "Dis-Charging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                status = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                status = "Not-Charging";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                status = "Unknown";
                break;
            default:
                status = "";
                break;
        }

        // technology
        technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

        // temperature
        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10 + " °C";

        // voltage
        voltage = (intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)) / 1000 + " V";

        // bundle
        bundle.putString("charge_counter", charge_counter);
        bundle.putString("charging", charging);
        bundle.putString("current_average", current_average);
        bundle.putString("current_now", current_now);
        bundle.putString("energy", energy);
        bundle.putString("health", health);
        bundle.putString("level", level);
        bundle.putString("percentage", percentage);
        bundle.putString("plugged_in", plugged_in);
        bundle.putString("present", present);
        bundle.putString("remaining_capacity", remaining_capacity);
        bundle.putString("scale", scale);
        bundle.putString("status", status);
        bundle.putString("technology", technology);
        bundle.putString("temperature", temperature);
        bundle.putString("voltage", voltage);
        bundle.putInt("current_now_int", current_now_int);
        bundle.putBoolean("charging_boolean", charging_boolean);
    }
}
