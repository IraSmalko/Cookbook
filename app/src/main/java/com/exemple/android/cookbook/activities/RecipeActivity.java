package com.exemple.android.cookbook.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.IngredientsAdapter;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.entity.RecipeForSQLite;
import com.exemple.android.cookbook.helpers.CheckOnlineHelper;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.LocalSavingImagesHelper;
import com.exemple.android.cookbook.helpers.VoiceRecognitionHelper;
import com.exemple.android.cookbook.helpers.WriterDAtaSQLiteAsyncTask;
import com.exemple.android.cookbook.supporting.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String USER = "userId";
    private static final String IS_PERSONAL = "isPersonal";
    private static final int INT_EXTRA = 0;
    private static final int VOICE_REQUEST_CODE = 1234;
    private static final int REQUEST_SHARE = 8874;

    private Intent mIntent;
    private ImageView mImageView;
    private Bitmap mLoadPhotoStep;
    private ProgressDialog mProgressDialog;
    private List<Ingredient> mIngredients = new ArrayList<>();

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentTextView;
        public TextView commentatorTextView;
        public CircleImageView commentatorImageView;
        public RatingBar commentatorRecipeRating;

        public CommentViewHolder(View v) {
            super(v);
            commentTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            commentatorTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            commentatorImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            commentatorRecipeRating = (RatingBar) itemView.findViewById(R.id.ratingBar_small);
        }
    }

    private static final String TAG = "RecipeActivity";
    public String MESSAGES_CHILD;
    public String RATING_CHILD;

    private String mUsername;
    private String mPhotoUrl;
    private String mUserId;

    private Button mSendButton;
    private Button mEditButton;
    private RecyclerView mCommentsRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mCommentEditText;
    private RatingBar mRatingBar;
    private TextInputLayout mTextInputLayout;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> mFirebaseAdapter;
    private RecipeRating mRecipeRating;
    private IngredientsAdapter mIngredientsAdapter;
    private RecyclerView mRecyclerView;

    private DataSourceSQLite mDataSourceSQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerIngredients);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btnDetailRecipe);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        mIntent = getIntent();
        getSupportActionBar().setTitle(mIntent.getStringExtra(RECIPE));

        new FirebaseHelper(new FirebaseHelper.OnIngredientsRecipe() {
            @Override
            public void OnGet(List<Ingredient> ingredients) {
                mIngredients = ingredients;
                mIngredientsAdapter = new IngredientsAdapter(getApplicationContext(), mIngredients);
                mRecyclerView.setAdapter(mIngredientsAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        }).getIngredients(mIntent.getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent
                .getStringExtra(RECIPE_LIST), mIntent.getStringExtra(RECIPE));

        Glide.with(getApplicationContext())
                .load(mIntent.getStringExtra(PHOTO))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(660, 480) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mLoadPhotoStep = resource;
                        mImageView.setImageBitmap(mLoadPhotoStep);
                    }
                });

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.intentStepRecipeActivity(getApplicationContext(), mIntent
                        .getStringExtra(RECIPE), mIntent.getStringExtra(PHOTO), mIntent
                        .getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent.getStringExtra(RECIPE_LIST));
            }
        });

        mCommentsRecyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
        mTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout1);
        mCommentEditText = (EditText) findViewById(R.id.editTextComent);
        mSendButton = (Button) findViewById(R.id.save_comments);
        mEditButton = (Button) findViewById(R.id.edit_comments);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mCommentsRecyclerView.setLayoutManager(mLinearLayoutManager);

        if (mFirebaseUser == null) {
            mTextInputLayout.setVisibility(View.INVISIBLE);
            mSendButton.setVisibility(View.INVISIBLE);
        } else {
            Log.d("USER", mFirebaseUser.toString());
            mUsername = mFirebaseUser.getDisplayName();
            mUserId = mFirebaseUser.getUid();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        MESSAGES_CHILD = "Support/" + mIntent.getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE) + "/comments";
        RATING_CHILD = "Support/" + mIntent.getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE) + "/recipeRating";

        mFirebaseDatabaseReference.child(RATING_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mFirebaseDatabaseReference.child(RATING_CHILD).child("rating").setValue(0)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mFirebaseDatabaseReference.child(RATING_CHILD).child("numberOfUsers").setValue(0)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    setValueChangeListener();
                                                }
                                            });
                                }
                            });
                } else {
                    setValueChangeListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        layoutRefreshLogIn();

        //        Comments

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Comment,
                CommentViewHolder>(
                Comment.class,
                R.layout.item_comment,
                CommentViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected Comment parseSnapshot(DataSnapshot snapshot) {
                Comment comment = super.parseSnapshot(snapshot);
                if (comment != null) {
                    comment.setId(snapshot.getKey());
                }
                return comment;
            }

            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder,
                                              Comment comment, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.commentTextView.setText(comment.getText());
                viewHolder.commentatorTextView.setText(comment.getName());
                viewHolder.commentatorRecipeRating.setRating(comment.getRating());
                if (comment.getPhotoUrl() == null) {
                    viewHolder.commentatorImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(RecipeActivity.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(RecipeActivity.this)
                            .load(comment.getPhotoUrl())
                            .into(viewHolder.commentatorImageView);
                }

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int commentCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (commentCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mCommentsRecyclerView.scrollToPosition(positionStart);
                }
            }

        });


        mCommentsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCommentsRecyclerView.setAdapter(mFirebaseAdapter);

        mSendButton.setEnabled(false);
        mCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog((float) 3.0);
            }
        });

        mDataSourceSQLite = new DataSourceSQLite(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (new CheckOnlineHelper(this).isOnline()) {
                if (mLoadPhotoStep != null) {
                    saveRecipe(DataSourceSQLite.REQUEST_SAVED);
                } else {
                    Toast.makeText(this,getResources()
                            .getString(R.string.wait_for_download_picture), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RecipeActivity.this, getResources()
                        .getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home) {
            IntentHelper.intentRecipeListActivity(this, mIntent.getStringExtra(RECIPE_LIST));
            return true;
        } else if (id == R.id.action_basket) {
            if (new CheckOnlineHelper(this).isOnline()) {
                if (mLoadPhotoStep != null) {
                    saveRecipe(DataSourceSQLite.REQUEST_BASKET);
                } else {
                    Toast.makeText(this, getResources()
                            .getString(R.string.wait_for_download_picture), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RecipeActivity.this, getResources()
                        .getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_share) {
            if (new CheckOnlineHelper(this).isOnline()) {
                if (mLoadPhotoStep != null) {
                    shareRecipe();
                } else {
                    Toast.makeText(this, getResources()
                            .getString(R.string.wait_for_download_picture), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RecipeActivity.this, getResources()
                        .getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_recipe;
    }

    @Override
    public void onBackPressed() {
        IntentHelper.intentRecipeListActivity(this, mIntent.getStringExtra(RECIPE_LIST));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public static class RecipeRating {
        private float rating;
        private int numberOfUsers;

        RecipeRating() {
            this.rating = 0;
            this.numberOfUsers = 0;
        }

        public float getRating() {
            return rating;
        }


    }

    AlertDialog mRatingDialog;
    RatingBar ratingInDialog;

    public void showRatingDialog(float startValue) {

        final AlertDialog.Builder ratingDialog = new AlertDialog.Builder(this);
        ratingDialog.setIcon(android.R.drawable.btn_star_big_on);
        ratingDialog.setTitle(getResources()
                .getString(R.string.make_rate_of_recipe));
        ratingDialog.setCancelable(false);

        View linearLayout = getLayoutInflater().inflate(R.layout.rating_dialog, null);
        ratingDialog.setView(linearLayout);

        ratingInDialog = (RatingBar) linearLayout.findViewById(R.id.dialogRatingBar);
        ratingInDialog.setRating(startValue);

        ratingDialog.setPositiveButton(getResources().getString(R.string.done),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Comment comment = new
                                Comment(mCommentEditText.getText().toString(),
                                mUsername,
                                mPhotoUrl,
                                ratingInDialog.getRating(),
                                mUserId);
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                                .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateRating();
                            }
                        });
                        mCommentEditText.setText("");

                        updateRating();
                        dialog.dismiss();
                    }
                });

        mRatingDialog = ratingDialog.create();
        mRatingDialog.show();
    }


    AlertDialog mRatingEditDialog;
    EditText editTextInEditDialog;
    RatingBar ratingInEditDialog;
    String referenceKey;

    public void showEditRatingDialog(final DatabaseReference ref, final String commentText, final float commentRating) {
        final AlertDialog.Builder ratingEditDialog = new AlertDialog.Builder(this);
        ratingEditDialog.setIcon(android.R.drawable.btn_star_big_on);
        ratingEditDialog.setTitle(getResources()
                .getString(R.string.editing_rate_of_recipe));
        ratingEditDialog.setCancelable(false);

        View linearLayout = getLayoutInflater().inflate(R.layout.edit_rating_dialog, null);
        ratingEditDialog.setView(linearLayout);
        referenceKey = ref.getKey();
        ratingInEditDialog = (RatingBar) linearLayout.findViewById(R.id.editDialogRatingBar);
        editTextInEditDialog = (EditText) linearLayout.findViewById(R.id.editTextComentInDialog);

        ratingInEditDialog.setRating(commentRating);
        editTextInEditDialog.setText(commentText);

        ratingEditDialog.setPositiveButton(getResources()
                .getString(R.string.done),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ref.child("text").setValue(editTextInEditDialog.getText().toString());
                        ref.child("rating").setValue(ratingInEditDialog.getRating()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateRating();
                            }
                        });

                        dialog.dismiss();
                    }
                });

        mRatingEditDialog = ratingEditDialog.create();
        mRatingEditDialog.show();
    }

    public void setValueChangeListener() {
        mFirebaseDatabaseReference.child(RATING_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRecipeRating = dataSnapshot.getValue(RecipeRating.class);
                mRatingBar.setRating(mRecipeRating.getRating());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_REQUEST_CODE) {
            new VoiceRecognitionHelper(getApplicationContext()).onActivityResult(resultCode, data,
                    new Recipe(mIntent.getStringExtra(RECIPE), mIntent.getStringExtra(PHOTO), mIntent
                            .getIntExtra(IS_PERSONAL, INT_EXTRA)), mIntent.getStringExtra(RECIPE_LIST), 0);
        }
        if (requestCode == BaseActivity.SIGN_IN_REQUEST) {
            if (resultCode == RESULT_OK) {
                layoutRefreshLogIn();
            }
        }
    }

    private void saveRecipe(int target) {
        Long idRecipe = isRecipeInDB(mIntent.getStringExtra(RECIPE));
        if (idRecipe == null) {
            int inSaved = 0;
            int inBasket = 0;
            String message = "";
            if (target == DataSourceSQLite.REQUEST_BASKET) {
                inBasket = 1;
                message = getResources().getString(R.string.added_to_basket);
            } else if (target == DataSourceSQLite.REQUEST_SAVED) {
                inSaved = 1;
                message = getResources().getString(R.string.recipe_saved);
            }
            boolean isOnline = new CheckOnlineHelper(this).isOnline();
            if (isOnline) {
                String path = LocalSavingImagesHelper.getPathForNewPhoto(mIntent.getStringExtra(RECIPE), mLoadPhotoStep, getApplicationContext());
                new WriterDAtaSQLiteAsyncTask.WriterRecipe(this, new WriterDAtaSQLiteAsyncTask.WriterRecipe.OnWriterSQLite() {
                    @Override
                    public void onDataReady(Integer integer) {
                        new FirebaseHelper().getStepsRecipe(getApplicationContext(), integer, mIntent
                                .getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent.getStringExtra(RECIPE_LIST), mIntent
                                .getStringExtra(RECIPE), mIntent.getStringExtra(USER));
                    }
                }).execute(new RecipeForSQLite(mIntent.getStringExtra(RECIPE), path, 0, mIngredients, inSaved, inBasket));
                Toast.makeText(RecipeActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RecipeActivity.this, getResources()
                        .getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            }
        } else {
            String message = "";
            if (target == DataSourceSQLite.REQUEST_BASKET) {
                if (mDataSourceSQLite.checkSaveTarget(idRecipe, DataSourceSQLite.REQUEST_BASKET) == 0) {
                    mDataSourceSQLite.updateSaveTarget(idRecipe, DataSourceSQLite.REQUEST_BASKET, 1);
                    message = getResources()
                            .getString(R.string.added_to_basket);
                } else if (mDataSourceSQLite.checkSaveTarget(idRecipe, DataSourceSQLite.REQUEST_BASKET) == 1) {
                    message =getResources().getString(R.string.already_in_basket);
                }
            } else if (target == DataSourceSQLite.REQUEST_SAVED) {
                if (mDataSourceSQLite.checkSaveTarget(idRecipe, DataSourceSQLite.REQUEST_SAVED) == 0) {
                    mDataSourceSQLite.updateSaveTarget(idRecipe, DataSourceSQLite.REQUEST_SAVED, 1);
                    message = getResources()
                            .getString(R.string.recipe_saved);
                } else if (mDataSourceSQLite.checkSaveTarget(idRecipe, DataSourceSQLite.REQUEST_SAVED) == 1) {
                    message = getResources().getString(R.string.already_in_saved);
                }
            }
            Toast.makeText(RecipeActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public Long isRecipeInDB(String recipeName) {
        return mDataSourceSQLite.findRecipe(recipeName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("isEditRatingDialogShown")) {
            showEditRatingDialog(mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(savedInstanceState.getString("ref")),
                    savedInstanceState.getString("text"),
                    savedInstanceState.getFloat("ratingEdit"));
        } else if (savedInstanceState.getBoolean("isRatingDialogShown")) {
            showRatingDialog(savedInstanceState.getFloat("rating"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRatingEditDialog != null) {
            outState.putBoolean("isEditRatingDialogShown", mRatingEditDialog.isShowing());
            outState.putString("ref", referenceKey);
            outState.putString("text", editTextInEditDialog.getText().toString());
            outState.putFloat("ratingEdit", ratingInEditDialog.getRating());
        } else if (mRatingDialog != null) {
            outState.putBoolean("isRatingDialogShown", mRatingDialog.isShowing());
            outState.putFloat("rating", ratingInDialog.getRating());
        }
    }

    public void updateRating() {

        mFirebaseDatabaseReference.child(MESSAGES_CHILD).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float rating = 0;
                int number = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final Comment comment = data.getValue(Comment.class);
                    rating += comment.getRating();
                    number++;
                }
                rating = rating / number;
                mFirebaseDatabaseReference.child(RATING_CHILD).child("rating").setValue(rating);
                mFirebaseDatabaseReference.child(RATING_CHILD).child("numberOfUsers").setValue(number);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void layoutRefreshLogOut() {
        mTextInputLayout.setVisibility(View.INVISIBLE);
        mSendButton.setVisibility(View.INVISIBLE);
        mEditButton.setVisibility(View.INVISIBLE);
    }

    public void layoutRefreshLogIn() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null) {
            mUserId = mFirebaseUser.getUid();

            mFirebaseDatabaseReference.child(MESSAGES_CHILD).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int counter = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        final Comment comment = data.getValue(Comment.class);
                        final DatabaseReference ref = data.getRef();
                        if (comment.getUserId().equals(mUserId)) {
                            mSendButton.setVisibility(View.INVISIBLE);
                            mTextInputLayout.setVisibility(View.INVISIBLE);
                            mEditButton.setVisibility(View.VISIBLE);

                            mEditButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showEditRatingDialog(ref, comment.getText(), comment.getRating());
                                }
                            });
                            counter++;
                            break;
                        }
                    }
                    if (counter == 0) {
                        mSendButton.setVisibility(View.VISIBLE);
                        mTextInputLayout.setVisibility(View.VISIBLE);
                        mEditButton.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mSendButton.setVisibility(View.INVISIBLE);
            mTextInputLayout.setVisibility(View.INVISIBLE);
            mEditButton.setVisibility(View.INVISIBLE);
        }
    }

    public void shareRecipe() {
        try {
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            mLoadPhotoStep.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        final Uri contentUri = FileProvider.getUriForFile(this, "com.exemple.android.cookbook", newFile);

        if (contentUri != null) {
            final Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, mIntent.getStringExtra(RECIPE));
            shareIntent.putExtra(Intent.EXTRA_TEXT, LocalSavingImagesHelper
                    .getDescriptionOfRecipeToShare(mIntent.getStringExtra(RECIPE), mIngredients));
            shareIntent.putExtra(Intent.EXTRA_TITLE, mIntent.getStringExtra(RECIPE));
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

            String[] whiteList = new String[]{
                    "com.whatsapp", "org.telegram.messenger",
                    "com.twitter.android", "com.google.android.gm",
                    "com.facebook.katana", "com.google.android.apps.plus"};

            startActivityForResult(generateCustomChooserIntent(shareIntent, whiteList), REQUEST_SHARE);
//            List<ResolveInfo> shareActivityList = getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
//            for (ResolveInfo resInfo : shareActivityList) {
//                Log.d("NNN", resInfo.activityInfo.packageName);
//            }
        }
    }

    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
        List<Intent> targetedShareIntents = new ArrayList<>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<>();
        Intent chooserIntent;

        Intent dummy = new Intent(prototype.getAction());
        dummy.setType(prototype.getType());
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(dummy, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo == null)
                    continue;
                if (!Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue;

                HashMap<String, String> info = new HashMap<>();
                info.put("packageName", resolveInfo.activityInfo.packageName);
                info.put("className", resolveInfo.activityInfo.name);
                info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
                intentMetaInfo.add(info);
            }

            if (!intentMetaInfo.isEmpty()) {
                // sorting for nice readability
                Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                        return map.get("simpleName").compareTo(map2.get("simpleName"));
                    }
                });

                // create the custom intent list
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }

                chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), getString(R.string.share));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            }
        }

        return Intent.createChooser(prototype, getString(R.string.share));
    }
}

