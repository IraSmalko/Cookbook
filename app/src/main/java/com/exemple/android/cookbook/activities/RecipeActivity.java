package com.exemple.android.cookbook.activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.helpers.CheckOnlineHelper;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.WriterDAtaSQLiteAsyncTask;
import com.exemple.android.cookbook.supporting.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String USERNAME = "username";
    private static final String IS_PERSONAL = "isPersonal";
    private static final int INT_EXTRA = 0;

    private Intent mIntent;
    private ImageView mImageView;
    private Bitmap mLoadPhotoStep;
    private ProgressDialog mProgressDialog;

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

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientNameTextView;
        public TextView ingredientQuantityTextView;
        public TextView ingredientUnitTextView;

        public IngredientViewHolder(View v) {
            super(v);
            ingredientNameTextView = (TextView) itemView.findViewById(R.id.ingredient_name);
            ingredientQuantityTextView = (TextView) itemView.findViewById(R.id.ingredient_quantity);
            ingredientUnitTextView = (TextView) itemView.findViewById(R.id.ingredient_unit);
        }
    }

    private static final String TAG = "RecipeActivity";
    public String MESSAGES_CHILD;
    public String RATING_CHILD;
    public String INGREDIENTS_CHILD;

    private String mUsername;
    private String mPhotoUrl;
    private String mUserId;

    private Button mSendButton;
    private Button mEditButton;
    private RecyclerView mCommentsRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManagerForIngredients;
    private EditText mCommentEditText;
    private RatingBar mRatingBar;
    private TextInputLayout mTextInputLayout;
    private RecyclerView mIngredientsRecyclerView;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> mCommentsFirebaseAdapter;
    private GoogleApiClient mGoogleApiClient;
    private RecipeRating mRecipeRating;
    private FirebaseRecyclerAdapter<Ingredient, IngredientViewHolder> mIngredientsFirebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        TextView descriptionRecipe = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btnDetailRecipe);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        ActionBar actionBar = getSupportActionBar();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        mIntent = getIntent();

        if (actionBar != null) {
            actionBar.setTitle(mIntent.getStringExtra(RECIPE));
        }

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

        descriptionRecipe.setText(mIntent.getStringExtra(DESCRIPTION));

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.intentStepRecipeActivity(getApplicationContext(), mIntent
                        .getStringExtra(RECIPE), mIntent.getStringExtra(PHOTO), mIntent
                        .getStringExtra(DESCRIPTION), mIntent.getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent
                        .getStringExtra(RECIPE_LIST));
            }
        });

        mCommentsRecyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
        mTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout1);
        mCommentEditText = (EditText) findViewById(R.id.editTextComent);
        mSendButton = (Button) findViewById(R.id.save_comments);
        mEditButton = (Button) findViewById(R.id.edit_comments);
        mIngredientsRecyclerView = (RecyclerView) findViewById(R.id.ingredients_list);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManagerForIngredients = new LinearLayoutManager(this);
        mLinearLayoutManagerForIngredients.setStackFromEnd(true);
        mCommentsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mIngredientsRecyclerView.setLayoutManager(mLinearLayoutManagerForIngredients);

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
        INGREDIENTS_CHILD = "Recipe_lists/" + mIntent.getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE) + "/ingredients";


        //        Rating

        //        final LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        //        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
        //        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.starPartiallySelected), PorterDuff.Mode.SRC_ATOP);
        //        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.starNotSelected), PorterDuff.Mode.SRC_ATOP);

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

        mFirebaseDatabaseReference.child(INGREDIENTS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    dataSnapshot.getRef().push().setValue(new Ingredient());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFirebaseDatabaseReference.child(MESSAGES_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final Comment comment = data.getValue(Comment.class);
                    final DatabaseReference ref = data.getRef();
                    if (mUserId.equals(comment.getUserId())) {
                        if (mSendButton.getVisibility() == View.VISIBLE) {
                            mSendButton.setVisibility(View.INVISIBLE);
                        }
                        if (mTextInputLayout.getVisibility() == View.VISIBLE) {
                            mTextInputLayout.setVisibility(View.INVISIBLE);
                        }
                        if (mEditButton.getVisibility() == View.INVISIBLE) {
                            mEditButton.setVisibility(View.VISIBLE);
                        }

                        mEditButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showEditRatingDialog(comment, ref);
                            }
                        });

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //        Comments


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

//        COMMENTS_ADAPTER

        mCommentsFirebaseAdapter = new FirebaseRecyclerAdapter<Comment,
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

        mCommentsFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int commentCount = mCommentsFirebaseAdapter.getItemCount();
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
        mCommentsRecyclerView.setAdapter(mCommentsFirebaseAdapter);



//      INGREDIENTS_ADAPTER

        mIngredientsFirebaseAdapter = new FirebaseRecyclerAdapter<Ingredient, IngredientViewHolder>(
                Ingredient.class,
                R.layout.item_ingridient,
                IngredientViewHolder.class,
                mFirebaseDatabaseReference.child(INGREDIENTS_CHILD)) {

            @Override
            protected Ingredient parseSnapshot(DataSnapshot snapshot) {
                Ingredient ingredient = super.parseSnapshot(snapshot);
                if (ingredient != null) {
                    ingredient.setId(snapshot.getKey());
                }
                return ingredient;
            }

            @Override
            protected void populateViewHolder(IngredientViewHolder viewHolder,
                                              Ingredient ingredient, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.ingredientNameTextView.setText(ingredient.getName());
                viewHolder.ingredientQuantityTextView.setText(String.valueOf(ingredient.getQuantity()));
                viewHolder.ingredientUnitTextView.setText(ingredient.getUnit());
            }
        };

        mIngredientsFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int ingredientCount = mIngredientsFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManagerForIngredients.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (ingredientCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mIngredientsRecyclerView.scrollToPosition(positionStart);
                }
            }

        });

        mIngredientsRecyclerView.setLayoutManager(mLinearLayoutManagerForIngredients);
        mIngredientsRecyclerView.setAdapter(mIngredientsFirebaseAdapter);




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
                showRatingDialog();
            }
        });

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
            boolean isOnline = new CheckOnlineHelper(this).isOnline();
            if (isOnline) {
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        mLoadPhotoStep, Environment.getExternalStorageDirectory().getAbsolutePath(), null);
                new WriterDAtaSQLiteAsyncTask.WriterRecipe(this, new WriterDAtaSQLiteAsyncTask.WriterRecipe.OnWriterSQLite() {
                    @Override
                    public void onDataReady(Integer integer) {
                        new FirebaseHelper().getStepsRecipe(getApplicationContext(), integer, mIntent
                                .getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent.getStringExtra(RECIPE_LIST), mIntent
                                .getStringExtra(RECIPE), mIntent.getStringExtra(USERNAME));
                    }
                }).execute(new Recipe(mIntent.getStringExtra(RECIPE), path, mIntent
                        .getStringExtra(DESCRIPTION), 0));

            } else {
                Toast.makeText(RecipeActivity.this, getResources()
                        .getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home) {
            IntentHelper.intentRecipeListActivity(this, mIntent.getStringExtra(RECIPE_LIST));
            return true;
        } else if (id == R.id.action_shop) {
            Intent intent = mIntent;
            intent.setClass(this, ShoppingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        IntentHelper.intentRecipeListActivity(this, mIntent.getStringExtra(RECIPE_LIST));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public static class RecipeRating {
        private float rating;
        private int numberOfUsers;

        RecipeRating() {
            this.rating = 0;
            this.numberOfUsers = 0;
        }

        public void addRating(float rating) {
            this.rating = (this.rating * this.numberOfUsers + rating) / (this.numberOfUsers + 1);
            this.numberOfUsers++;
        }

        public float getRating() {
            return rating;
        }

        public int getNumberOfUsers() {
            return numberOfUsers;
        }

        public void editRating(float oldRating, float newRating) {
            rating = (rating * numberOfUsers - oldRating + newRating) / numberOfUsers;
        }
    }

    public void showRatingDialog() {

        final AlertDialog.Builder ratingDialog = new AlertDialog.Builder(this);
        ratingDialog.setIcon(android.R.drawable.btn_star_big_on);
        ratingDialog.setTitle("Будь ласка, оцініть рецепт!");
        ratingDialog.setCancelable(false);

        View linearlayout = getLayoutInflater().inflate(R.layout.rating_dialog, null);
        ratingDialog.setView(linearlayout);

        final RatingBar ratingInDialog = (RatingBar) linearlayout.findViewById(R.id.dialogRatingBar);

        ratingDialog.setPositiveButton("Готово",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Comment comment = new
                                Comment(mCommentEditText.getText().toString(),
                                mUsername,
                                mPhotoUrl,
                                ratingInDialog.getRating(),
                                mUserId);
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                                .push().setValue(comment);
                        mCommentEditText.setText("");

                        mRecipeRating.addRating(ratingInDialog.getRating());
                        mFirebaseDatabaseReference.child(RATING_CHILD).child("rating").setValue(mRecipeRating.getRating());
                        mFirebaseDatabaseReference.child(RATING_CHILD).child("numberOfUsers").setValue(mRecipeRating.getNumberOfUsers());
                        dialog.dismiss();
                    }
                });

        ratingDialog.create();
        ratingDialog.show();
    }

    public void showEditRatingDialog(final Comment comment, final DatabaseReference ref) {
        final AlertDialog.Builder ratingDialog = new AlertDialog.Builder(this);
        ratingDialog.setIcon(android.R.drawable.btn_star_big_on);
        ratingDialog.setTitle("Редагування оцінки");
        ratingDialog.setCancelable(false);

        View linearlayout = getLayoutInflater().inflate(R.layout.edit_rating_dialog, null);
        ratingDialog.setView(linearlayout);

        final RatingBar ratingInEditDialog = (RatingBar) linearlayout.findViewById(R.id.editDialogRatingBar);
        final EditText editTextInEditDialog = (EditText) linearlayout.findViewById(R.id.editTextComentInDialog);

        ratingInEditDialog.setRating(comment.getRating());
        editTextInEditDialog.setText(comment.getText());

        ratingDialog.setPositiveButton("Готово",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mRecipeRating.editRating(comment.getRating(), ratingInEditDialog.getRating());
                        mFirebaseDatabaseReference.child(RATING_CHILD).child("rating").setValue(mRecipeRating.getRating());
                        mFirebaseDatabaseReference.child(RATING_CHILD).child("numberOfUsers").setValue(mRecipeRating.getNumberOfUsers());

                        ref.child("text").setValue(editTextInEditDialog.getText().toString());
                        ref.child("rating").setValue(ratingInEditDialog.getRating());
                        dialog.dismiss();
                    }
                });

        ratingDialog.create();
        ratingDialog.show();
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

}
