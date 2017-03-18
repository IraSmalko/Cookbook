package com.exemple.android.cookbook.models.realm;

import com.exemple.android.cookbook.models.firebase.FirebaseIngredient;
import com.google.firebase.database.DataSnapshot;

import io.realm.RealmObject;

/**
 * Created by Sakurov on 15.03.2017.
 */

public class RealmIngredient extends RealmObject {

    private String name;
    private float quantity;
    private String unit;

    public RealmIngredient() {
    }

    public RealmIngredient(FirebaseIngredient firebaseIngredient) {
        name = firebaseIngredient.getName();
        quantity = firebaseIngredient.getQuantity();
        unit = firebaseIngredient.getUnit();
    }

    public RealmIngredient(RealmIngredient realmIngredient){
        name = realmIngredient.getName();
        quantity = realmIngredient.getQuantity();
        unit = realmIngredient.getUnit();
    }

    public RealmIngredient(String name, float quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public RealmIngredient(DataSnapshot dataSnapshot) {
        RealmIngredient realmIngredient = dataSnapshot.getValue(RealmIngredient.class);
        name = realmIngredient.getName();
        quantity = realmIngredient.getQuantity();
        unit = realmIngredient.getUnit();
    }

    public String getName() {
        return name;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
