package com.exemple.android.cookbook.supporting;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Sakurov on 14.03.2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
