package org.davbo.wakeonlan;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class WakeonlanActivity extends Activity {
    public static final int PORT = 9; 
    private static final String TAG = "WOLActivity";
    private static final String PREFS_NAME = "WOLLY";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configs = wifi.getConfiguredNetworks();
        ArrayAdapter <String> adapter =
        		  new ArrayAdapter <String> (this, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add("Select a Wifi Network");
        for (WifiConfiguration config : configs) {
			adapter.add(config.toString());
		}
        Spinner spinner = (Spinner)findViewById(R.id.networkspin);
        spinner.setAdapter(adapter);
    }

    public void addNewMAC(View view) {
        EditText edit_macaddr = (EditText) findViewById(R.id.macaddr);
        String macStr = edit_macaddr.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.networkspin);
        String netcfg = (String)spinner.getSelectedItem();
        Log.i(TAG, "Network conf: "+netcfg);
        Log.i(TAG, "MAC Address: "+macStr);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(netcfg, macStr);
        editor.commit();
    }
}