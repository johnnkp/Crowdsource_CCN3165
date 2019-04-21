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

public class MainActivity extends AppCompatActivity {
    CountDownTimer mCountDownTimer;
    public int f = 2000;
    public long startTime;
    String email_name, email_title, email_content;
    TextView current_location, bssid;
    Button save, open, email, fre;
    EditText fre_set;
    private WifiManager wifi;
    public StdDBHelper DH = null;
    GPSService mGPSService;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DH = new StdDBHelper(this);
        mGPSService = new GPSService(MainActivity.this);
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        final boolean isPlayServicesInstalled = appInstalledOrNot("com.google.android.gms");

        save = (Button) findViewById(R.id.save);
        open = (Button) findViewById(R.id.open);
        email = (Button) findViewById(R.id.email);
        fre = (Button) findViewById(R.id.fre);
        current_location = (TextView) findViewById(R.id.current_location);
        bssid = (TextView) findViewById(R.id.bssid);
        fre_set = (EditText) findViewById(R.id.fre_set);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        if (!mGPSService.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (isPlayServicesInstalled) {
                new GpsUtils(MainActivity.this).turnGPSOn();
            } else {
                mGPSService.askUserToOpenGPS();
            }
        }

        obtainWifiInfo();
        frequency_of_scanning(f);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wifi = getwifi().toString();
                if (location == null) {
                    DH.input_table(createEmptyLocation().getLongitude(), createEmptyLocation().getLatitude(), wifi);
                } else {
                    double longitude, latitude;
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    DH.input_table(longitude, latitude, wifi);
                }
                Toast.makeText(MainActivity.this, "Scan result saved to database successfully.", Toast.LENGTH_SHORT).show();
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
                try {
                    int sec = Integer.parseInt(fre_set.getText().toString());
                    if (sec > 0) {
                        f = sec * 1000;
                        frequency_of_scanning(f);
                    }
                } catch (Exception e) {
                }
                fre_set.setText("");
            }
        });
    }

    private void obtainWifiInfo() {
        if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            wifi.setWifiEnabled(true);
        }
        bssid.setText(getwifi());
        displayLocation();
    }

    public StringBuilder getwifi() {
        StringBuilder scanBuilder = new StringBuilder();
        List<ScanResult> scanResults = wifi.getScanResults();

        for (ScanResult scanResult : scanResults) {

            scanBuilder.append("\nSSID: " + scanResult.SSID
                    + "\nBSSID: " + scanResult.BSSID + "\n");
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
                                stringBuilder.append(cursor.getInt(0) + ":\n" +
                                        cursor.getString(1) + "\n" +
                                        "Longitude: " + cursor.getString(2) + "\n" +
                                        "Latitude: " + cursor.getString(3) + "\n" +
                                        cursor.getString(4) + "\n");
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

    private Location createEmptyLocation() {
        location = new Location("Error");
        location.setLongitude(-1000);
        location.setLatitude(-1000);
        return location;
    }

    private void displayLocation() {
        try {
            location = mGPSService.getLocation();
            current_location.setText("Longitude: " + location.getLongitude() + "\nLatitude: " + location.getLatitude());
        } catch (Exception e) {
            current_location.setText("Location not available");
        }
    }

    // https://discuss.erpnext.com/t/how-to-developing-apps-for-android/21016
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }
}
