package com.thuannp.securepass.ui.wifi;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.thuannp.securepass.models.CredModel;
import com.thuannp.securepass.repositories.CredRepository;

import java.util.List;

public class WifiViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private CredRepository repository;
    private LiveData<List<CredModel>> allCred, wifiCred;

    public WifiViewModel(@NonNull Application application) {
        super(application);
        repository = new CredRepository(application);
        allCred = repository.getAllNote();
        wifiCred = repository.getAllSocial();
    }

    public void insert(CredModel credModel) {
        repository.insert(credModel);
    }

    public void update(CredModel credModel) {
        repository.update(credModel);
    }

    public void delete(CredModel credModel) {
        repository.delete(credModel);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<CredModel>> getAllCred() {
        return allCred;
    }

    public LiveData<List<CredModel>> getAllWifi() {
        return wifiCred;
    }
}