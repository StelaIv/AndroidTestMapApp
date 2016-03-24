package map.mymapretry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StelaIvanova on 15-Jan-16.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locations.db";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "_address";
    public static final String COLUMN_COUNTRY = "_country";
    public static final String COLUMN_LAT = "_lat";
    public static final String COLUMN_LNG = "_lng";
    public static final String COLUMN_IMAGE = "_image";
    private static final String TAG = "DataBaseHelper";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " +
                TABLE_LOCATIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_COUNTRY + " TEXT, " +
                COLUMN_LAT + " REAL, " +
                COLUMN_LNG + " REAL, " +
                COLUMN_IMAGE + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public void InsertMarker(List<Markers> marker){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values= new ContentValues();;
        long test=0;
        for (Markers item : marker) {
            values.put(COLUMN_ADDRESS,item.getStreetAddress());
            values.put(COLUMN_COUNTRY,item.getCountryName());
            values.put(COLUMN_LAT, item.getLatitude());
            values.put(COLUMN_LNG, item.getLongitude());
            test =db.insert(TABLE_LOCATIONS,null,values);
            values.clear();
        }

       if (marker.size()==test) {
           Log.v(TAG,"Success");
       }else{
           Log.v(TAG,"Error");
       }

        db.close();
    }

    public ArrayList<Markers> getMarkers(){
        ArrayList<Markers>list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_LOCATIONS;
        Cursor curs = db.rawQuery(query,null);
        try {
            if (curs != null) {
                curs.moveToFirst();
                do {
                    Markers mark = new Markers();
                    mark.setStreetAddress(curs.getString(1));
                    mark.setCountryName(curs.getString(2));
                    mark.setLatitude(curs.getDouble(3));
                    mark.setLongitude(curs.getDouble(4));
                    list.add(mark);
                    curs.moveToNext();
                } while (!curs.isAfterLast());
            }
        }catch (CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        curs.close();
        db.close();
        return  list;
    }

}
