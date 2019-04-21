package hkcc.ccn3165.assignment.crowdsource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Java SE 8與Android 7.x程式設計範例教本
// Java SE 11與Android 9.x程式設計範例教本
// https://books.google.com.hk/books?id=V5ApDwAAQBAJ
// https://www.books.com.tw/products/0010753967
public class StdDBHelper extends SQLiteOpenHelper {
    private final static String DB = "wifi";
    private final static String TB = "test";
    private final static String Data = "Data";
    private final static int version = 1;

    public StdDBHelper(Context context) {
        super(context, DB, null, version);
    }

    // https://tips.androidhive.info/2013/10/android-insert-datetime-value-in-sqlite-database/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE IF NOT EXISTS " +
                Data +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP ," +
                "_longitude DOUBLE ,_latitude DOUBLE ,_wifi VARCHAR(1000))";
        db.execSQL(SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL= "DROP TABLE IF EXISTS " + Data;
        db.execSQL(SQL);
    }

    public void input_table(double longitude, double latitude, String wifi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_longitude", longitude);
        contentValues.put("_latitude", latitude);
        contentValues.put("_wifi", wifi);
        long result = db.insert(Data,null, contentValues);
        db.close();
    }

    public Cursor getalldata(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Data, null);
        return cursor;
    }

}
