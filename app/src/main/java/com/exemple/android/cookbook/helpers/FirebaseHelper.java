package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.entity.ImageCard;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FirebaseHelper {

    private static List<StepRecipe> stepRecipe = new ArrayList<>();
    private List<CategoryRecipes> category = new ArrayList<>();
    private List<Recipe> recipes = new ArrayList<>();
    private FirebaseHelper.OnUserCategoryRecipe onUserCategoryRecipe;
    private FirebaseHelper.OnUserRecipes onUserRecipes;
    private FirebaseHelper.OnSaveImage onSaveImage;
    private FirebaseHelper.OnStepRecipes onStepRecipes;
    private Context cnx;

    public FirebaseHelper() {
    }

    public FirebaseHelper(FirebaseHelper.OnStepRecipes onStepRecipes) {
        this.onStepRecipes = onStepRecipes;
    }

    public FirebaseHelper(FirebaseHelper.OnSaveImage onSaveImage) {
        this.onSaveImage = onSaveImage;
    }

    public FirebaseHelper(FirebaseHelper.OnUserRecipes onUserRecipes) {
        this.onUserRecipes = onUserRecipes;
    }

    public FirebaseHelper(FirebaseHelper.OnUserCategoryRecipe onUserCategoryRecipe) {
        this.onUserCategoryRecipe = onUserCategoryRecipe;
    }

    public void getStepsRecipe(Context context, final int idRecipe, String recipeList,
                               String recipe, String username) {
        cnx = context;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        if (username != null) {
            databaseReference = firebaseDatabase.getReference()
                    .child(username + "/Step_recipe/" + recipeList + "/" + recipe);
        } else {
            databaseReference = firebaseDatabase.getReference()
                    .child("Step_recipe/" + recipeList + "/" + recipe);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                    stepRecipe.add(step);
                }
                new DataSourceSQLite(cnx).saveStepsSQLite(stepRecipe, idRecipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getStepsRecipe(List<StepRecipe> stepRecipes, String reference) {
        stepRecipe = stepRecipes;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(reference);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                    stepRecipe.add(step);
                    onStepRecipes.OnGet(stepRecipe);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getUserCategoryRecipe(List<CategoryRecipes> categoryRecipesList, String username,
                                      FirebaseDatabase firebaseDatabase) {
        category = categoryRecipesList;

        DatabaseReference databaseUserReference = firebaseDatabase.getReference(username + "/Сategory_Recipes");
        databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryRecipes categoryRecipes = postSnapshot.getValue(CategoryRecipes.class);
                    category.add(categoryRecipes);
                    onUserCategoryRecipe.OnGet(category);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserRecipe(List<Recipe> recipesList, FirebaseDatabase firebaseDatabase,
                              String reference) {
        recipes = recipesList;

        DatabaseReference databaseUserReference = firebaseDatabase.getReference(reference);
        databaseUserReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = postSnapshot.getValue(Recipe.class);
                        recipes.add(recipe);
                        onUserRecipes.OnGet(recipes);
                    }
                } else {
                    onUserRecipes.OnGet(recipes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveImage(StorageReference storageReference, ImageCard imageCard) {
        final Random random = new Random();
        UploadTask uploadTask = storageReference.child("Photo" + String.valueOf(random.nextInt()))
                .putBytes(imageCard.getBytesImage());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrlCamera = taskSnapshot.getDownloadUrl();
                onSaveImage.OnSave(downloadUrlCamera);
            }
        });
    }

    public interface OnSaveImage {
        void OnSave(Uri photoUri);
    }

    public interface OnUserCategoryRecipe {
        void OnGet(List<CategoryRecipes> categoryRecipesList);
    }

    public interface OnUserRecipes {
        void OnGet(List<Recipe> recipes);
    }

    public interface OnStepRecipes {
        void OnGet(List<StepRecipe> stepRecipes);
    }
}
