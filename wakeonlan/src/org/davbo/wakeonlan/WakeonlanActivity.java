package org.davbo.wakeonlan;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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
        Spinner spinner = (Spinner)findViewById(R.id.spin);
        spinner.setAdapter(adapter);
    }

    public void sendWOL(View view) {
        EditText edit_macaddr = (EditText) findViewById(R.id.macaddr);
        String macStr = edit_macaddr.getText().toString();      
        String ipStr = "255.255.255.255";

        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            
            Log.i(TAG, "Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to send Wake-on-LAN packet: + e");
        }
        
    }
    
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
    
}