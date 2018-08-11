package prof.mo.ed.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prof-Mohamed on 8/9/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="MoviePopular.db";
    public static final String TABLE_NAME="Favourites";

    public static final String COL_1="ID";
    public static final String COL_2="MovieID";
    public static final String COL_3="MovieTitle";
    public static final String COL_4="MovieOverView";
    public static final String COL_5="ReleaseDate";
    public static final String COL_6="Popularity";
    public static final String COL_7="VoteAverage";
    public static final String COL_8="IS_Favourite";
    public static final String COL_9="PosterPath";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase Db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, MovieID TEXT, MovieTitle TEXT, MovieOverView TEXT, ReleaseDate TEXT,Popularity TEXT,VoteAverage TEXT,IS_Favourite INTEGER,PosterPath TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String MovieID,String MovieTitle, String MovieOverView,String ReleaseDate, String Popularity,String VoteAverage,Integer IS__Favourite,String PosterPath ){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues content_values=new ContentValues();

        content_values.put(COL_2,MovieID);
        content_values.put(COL_3,MovieTitle);
        content_values.put(COL_4,MovieOverView);
        content_values.put(COL_5,ReleaseDate);
        content_values.put(COL_6,Popularity);
        content_values.put(COL_7,VoteAverage);
        content_values.put(COL_8,IS__Favourite);
        content_values.put(COL_9,PosterPath);

        long result= db.insert(TABLE_NAME,null,content_values);

        if (result==-1){
            return false;
        }
        else
            return true;
    }

    public boolean UpdateData(String id,Integer IS_Favourite){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues content_value=new ContentValues();

        content_value.put(COL_8,IS_Favourite);

        db.update(TABLE_NAME, content_value,"MovieID = ?",new String[] { id });
        return true;

    }

    public List<MovieEntity> getAllData(){
        ArrayList<MovieEntity>movieEntities=new ArrayList<MovieEntity>();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME+" where IS_Favourite = "+1,null);

        if (res.moveToNext()){
            do{
                MovieEntity movieEntity=new MovieEntity();
                movieEntity.setVIDEO_ID_STRING(res.getString(res.getColumnIndex("MovieID")));
                movieEntity.setPOSTER_PATH_STRING(res.getString(res.getColumnIndex("PosterPath")));
                movieEntity.setTITLE_STRING(res.getString(res.getColumnIndex("MovieTitle")));
                movieEntity.setOVERVIEW_STRING(res.getString(res.getColumnIndex("MovieOverView")));
                movieEntity.setRELEASE_DATE_STRING(res.getString(res.getColumnIndex("ReleaseDate")));
                movieEntity.setPOPULARITY_STRING(res.getString(res.getColumnIndex("Popularity")));
                movieEntity.setVOTE_AVERAGE(res.getString(res.getColumnIndex("VoteAverage")));
                movieEntities.add(movieEntity);

            }while (res.moveToNext());
        }
        return movieEntities;
    }

    public boolean get_Movie_ID(String movieID){

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select MovieID from "+TABLE_NAME+" where MovieID = "+movieID,null);

        if (res.moveToNext()){
           return true;
        }
        else return false;
    }

    public int getIS__Favourite_DB(String movieID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select IS_Favourite from " + TABLE_NAME + " where MovieID = " + movieID, null);
        int movieState = 0;
        if (res.moveToFirst()) {
            do {
                movieState =res.getInt(res.getColumnIndex("IS_Favourite"));
            }while (res.moveToNext());
        }
        res.close();
        return movieState;
    }
}