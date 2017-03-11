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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FirebaseHelper {

    private List<StepRecipe> mStepRecipe = new ArrayList<>();
    private List<CategoryRecipes> mCategory = new ArrayList<>();
    private List<Recipe> mRecipes = new ArrayList<>();
    private FirebaseHelper.OnUserCategoryRecipe mOnUserCategoryRecipe;
    private FirebaseHelper.OnUserRecipes mOnUserRecipes;
    private FirebaseHelper.OnSaveImage mOnSaveImage;
    private FirebaseHelper.OnStepRecipes mOnStepRecipes;
    private Context mContext;

    public FirebaseHelper() {
    }

    public FirebaseHelper(FirebaseHelper.OnStepRecipes onStepRecipes) {
        mOnStepRecipes = onStepRecipes;
    }

    public FirebaseHelper(FirebaseHelper.OnSaveImage onSaveImage) {
        mOnSaveImage = onSaveImage;
    }

    public FirebaseHelper(FirebaseHelper.OnUserRecipes onUserRecipes) {
        mOnUserRecipes = onUserRecipes;
    }

    public FirebaseHelper(FirebaseHelper.OnUserCategoryRecipe onUserCategoryRecipe) {
        mOnUserCategoryRecipe = onUserCategoryRecipe;
    }

    public void getStepsRecipe(Context context, final int idRecipe, String recipeList,
                               String recipe, String username) {
        this.mContext = context;
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
                    mStepRecipe.add(step);
                }
                new DataSourceSQLite(FirebaseHelper.this.mContext).saveStepsSQLite(mStepRecipe, idRecipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getStepsRecipe(String reference) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(reference);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                    mStepRecipe.add(step);
                }
                mOnStepRecipes.OnGet(mStepRecipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getUserCategoryRecipe(String username,
                                      FirebaseDatabase firebaseDatabase) {
        DatabaseReference databaseUserReference = firebaseDatabase.getReference(username + "/Сategory_Recipes");
        databaseUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        CategoryRecipes categoryRecipes = postSnapshot.getValue(CategoryRecipes.class);
                        mCategory.add(categoryRecipes);
                        mOnUserCategoryRecipe.OnGet(mCategory);
                    }
                } else {
                    mOnUserCategoryRecipe.OnGet(mCategory);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserRecipe(FirebaseDatabase firebaseDatabase,
                              String reference) {

        DatabaseReference databaseUserReference = firebaseDatabase.getReference(reference);
        databaseUserReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = postSnapshot.getValue(Recipe.class);
                        mRecipes.add(recipe);
                        mOnUserRecipes.OnGet(mRecipes);
                    }
                } else {
                    mOnUserRecipes.OnGet(mRecipes);
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
                mOnSaveImage.OnSave(downloadUrlCamera);
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

    public void removeCategory(Context context, String item) {
        mContext = context;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String username = firebaseUser.getDisplayName();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child(username + "/Сategory_Recipes").orderByChild("name").equalTo(item);

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        postSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void removeRecipe(Context context, String item, String nameRecipeList) {
        mContext = context;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String username = firebaseUser.getDisplayName();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child(username + "/Recipe_lists/" + nameRecipeList).orderByChild("name").equalTo(item);

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        postSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            removeSteps(item, username, nameRecipeList);
        }
    }

    private void removeSteps(String item, String username, String nameRecipeList) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child(username + "/Step_recipe/" + nameRecipeList).child(item);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    postSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
