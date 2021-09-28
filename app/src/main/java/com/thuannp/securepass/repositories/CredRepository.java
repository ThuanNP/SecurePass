package com.thuannp.securepass.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.thuannp.securepass.models.CredModel;
import com.thuannp.securepass.room.CredDB;
import com.thuannp.securepass.room.CredDao;

import java.util.List;

public class CredRepository  {

    private CredDao credDao;
    private LiveData<List<CredModel>> allCred, mailCred, socialCred, wifiCred;

    public CredRepository(Application application) {
        CredDB database = CredDB.getInstance(application);
        credDao = database.credDao();
        allCred = credDao.getAllCred();
        mailCred = credDao.getAllMail();
        socialCred = credDao.getAllSocial();
        wifiCred = credDao.getAllWifi();
    }

    private static class InsertNoteAsyncTask extends AsyncTask<CredModel, Void, Void> {
        private CredDao credDao;

        private InsertNoteAsyncTask(CredDao credDao) {
            this.credDao = credDao;
        }

        @Override
        protected Void doInBackground(CredModel... credModels) {
            this.credDao.insert(credModels[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<CredModel, Void, Void> {
        private CredDao credDao;

        private UpdateNoteAsyncTask(CredDao credDao) {
            this.credDao = credDao;
        }

        @Override
        protected Void doInBackground(CredModel... credModels) {
            this.credDao.update(credModels[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<CredModel, Void, Void> {
        private CredDao credDao;

        private DeleteNoteAsyncTask(CredDao credDao) {
            this.credDao = credDao;
        }

        @Override
        protected Void doInBackground(CredModel... credModels) {
            this.credDao.delete(credModels[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<CredModel, Void, Void> {
        private CredDao credDao;

        private DeleteAllNotesAsyncTask(CredDao credDao) {
            this.credDao = credDao;
        }

        @Override
        protected Void doInBackground(CredModel... credModels) {
            this.credDao.update(credModels[0]);
            return null;
        }
    }

    public void insert(CredModel credModel) {
        new InsertNoteAsyncTask(credDao).execute(credModel);
    }

    public void update(CredModel credModel) {
        new UpdateNoteAsyncTask(credDao).execute(credModel);
    }

    public void delete(CredModel credModel) { new DeleteNoteAsyncTask(credDao).execute(credModel); }

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(credDao).execute();
    }

    public LiveData<List<CredModel>> getAllNote() {
        return allCred;
    }

    public LiveData<List<CredModel>> getAllMail() {
        return mailCred;
    }

    public LiveData<List<CredModel>> getAllWifi() {
        return wifiCred;
    }

    public LiveData<List<CredModel>> getAllSocial() {
        return socialCred;
    }
}
