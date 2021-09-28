package com.thuannp.securepass.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.thuannp.securepass.models.CredModel;

@Database(entities = {CredModel.class}, version = 5, exportSchema = false)
public abstract class CredDB extends RoomDatabase {
    private static CredDB instance;

    public abstract CredDao credDao();

    public static synchronized CredDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CredDB.class, "CredDB")
                    .setJournalMode(JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private CredDao credDao;

        private PopulateDbAsyncTask(CredDB db) {
            credDao = db.credDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
