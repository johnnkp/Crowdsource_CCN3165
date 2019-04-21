package hkcc.ccn3165.assignment.crowdsource;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    CountDownTimer mCountDownTimer;
    public int f = 2000;
    public long startTime;
    String email_name, email_title, email_content;
    TextView current_location, bssid;
    Button save, open, email, fre;
    EditText fre_set;
    private LocationManager lms;
    private WifiManager wifi;
    public StdDBHelper DH = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DH = new StdDBHelper(this);

        save = (Button) findViewById(R.id.save);
        open = (Button) findViewById(R.id.open);
        email = (Button) findViewById(R.id.email);
        fre = (Button) findViewById(R.id.fre);
        current_location = (TextView) findViewById(R.id.current_location);
        bssid = (TextView) findViewById(R.id.bssid);
        fre_set = (EditText) findViewById(R.id.fre_set);
        lms = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        obtainWifiInfo();
        frequency_of_scanning(f);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        final Location location = lms.getLastKnownLocation(lms.NETWORK_PROVIDER);

        obtainWifiInfo();
        frequency_of_scanning(f);
        // onLocationChanged(location);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String local = getlocation(location);
                String wifi = getwifi().toString();
                DH.input_table(local, wifi);
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, page2activity.class));
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });

        fre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sec = fre_set.getText().toString();
                if (sec == null) {
                } else {
                    f = Integer.parseInt(sec);
                    f = f * 1000;
                    frequency_of_scanning(f);
                    fre_set.setText("");
                }
            }
        });
    }

    private void obtainWifiInfo() {
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            wifi.setWifiEnabled(true);
        }
        StringBuilder scanBuilder = new StringBuilder();
        List<ScanResult> scanResults = wifi.getScanResults();

        for (ScanResult scanResult : scanResults) {
            scanBuilder.append("\nSSID：" + scanResult.SSID
                    + "\nBSSID:" + scanResult.BSSID + "\n");
        }
        bssid.setText(scanBuilder);
    }

    public StringBuilder getwifi() {
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        StringBuilder scanBuilder = new StringBuilder();
        List<ScanResult> scanResults = wifi.getScanResults();

        for (ScanResult scanResult : scanResults) {

            scanBuilder.append("\nSSID：" + scanResult.SSID
                    + "\nBSSID:" + scanResult.BSSID + "\n");
        }
        return scanBuilder;
    }

    public void frequency_of_scanning(int x) {
        mCountDownTimer = new CountDownTimer(startTime = 500000000, x) {
            @Override
            public void onTick(long millisUntilFinished) {
                startTime = millisUntilFinished;
                obtainWifiInfo();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void alertDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mview = getLayoutInflater().inflate(R.layout.email_box, null);
        final EditText email_id = (EditText) mview.findViewById(R.id.email_id);
        final EditText set_title = (EditText) mview.findViewById(R.id.set_title);

        mBuilder.setMessage("Send Email")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!email_id.getText().toString().isEmpty() && !set_title.getText().toString().isEmpty()) {
                            email_name = email_id.getText().toString();
                            String[] recipients = email_name.split(",");
                            email_title = set_title.getText().toString();
                            Cursor cursor = DH.getalldata();
                            StringBuilder stringBuilder = new StringBuilder();
                            while (cursor.moveToNext()) {
                                stringBuilder.append(cursor.getInt(0) + ":\n" + cursor.getString(1) + "\n" + cursor.getString(2) + "\n");
                            }
                            email_content = stringBuilder.toString();

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                            intent.putExtra(Intent.EXTRA_SUBJECT, email_title);
                            intent.putExtra(Intent.EXTRA_TEXT, email_content);

                            intent.setType("message/rfc822");
                            startActivity(Intent.createChooser(intent, "choose an email client"));

                            dialog.dismiss();
                        } else
                            Toast.makeText(MainActivity.this, "Please input all information", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        mBuilder.setView(mview);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        current_location.setText("Longitude: " + longitude + "\nLatitude: " + latitude);
    }

    public String getlocation(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String location_1 = ("Longitude: " + longitude + "\nLatitude: " + latitude);
        return location_1;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}
