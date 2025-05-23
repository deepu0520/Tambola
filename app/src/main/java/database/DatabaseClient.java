package database;

import android.content.Context;
import androidx.room.Room;

import com.newitzone.desitambola.LoginActivity;

public class DatabaseClient {

    private Context mCtx;
    private static DatabaseClient mInstance;
    
    //our app database object
    private AppDatabase appDatabase;

    private DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
        
        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, LoginActivity.DATABASE_NAME).allowMainThreadQueries().build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}