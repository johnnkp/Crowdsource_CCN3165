package hkcc.ccn3165.assignment.crowdsource;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class page2activity extends AppCompatActivity {

    StdDBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2);

        db = new StdDBHelper(this);

        TextView show = findViewById(R.id.show);

        Cursor cursor = db.getalldata();

        StringBuilder stringBuilder = new StringBuilder();

        while (cursor.moveToNext()){
            stringBuilder.append(cursor.getInt(0) + ":\n" + cursor.getString(1) + "\n" + cursor.getString(2) + "\n");
        }

        show.setText(stringBuilder);
    }



}
