package com.exemple.android.cookbook.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
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
import com.exemple.android.cookbook.*;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.supporting.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StepRecipeActivity extends AppCompatActivity {

    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";

    private Intent intent;
    private List<StepRecipe> stepRecipe = new ArrayList<>();
    private TextView txtStepRecipe;
    private ImageView imgStepRecipe;
    private ActionBar actionBar;
    private Context context = StepRecipeActivity.this;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        txtStepRecipe = (TextView) findViewById(R.id.txtStepRecipe);
        imgStepRecipe = (ImageView) findViewById(R.id.imgStepRecipe);
        actionBar = getSupportActionBar();

        intent = getIntent();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child("Step_recipe/" + intent.getStringExtra(RECIPE_LIST) + "/" + intent.getStringExtra(RECIPE));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                    stepRecipe.add(step);
                }
                if (stepRecipe.size() != 0) {
                    updateData(index);
                } else {
                    Toast.makeText(context, getResources().getString(R
                            .string.no_information_available), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_step);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = ++index;
                updateData(index);
            }
        });
    }

    public void updateData(int i) {
        if (i < stepRecipe.size()) {
            actionBar.setTitle(stepRecipe.get(i).getNumberStep());
            txtStepRecipe.setText(stepRecipe.get(i).getTextStep());
            Glide.with(context).load(stepRecipe.get(i).getPhotoUrlStep()).into(imgStepRecipe);
        } else {
            IntentHelper.intentRecipeActivity(context, intent.getStringExtra(RECIPE), intent
                    .getStringExtra(PHOTO), intent.getStringExtra(DESCRIPTION), intent.getStringExtra(RECIPE_LIST));
        }
    }

    public static class RecipeActivity extends AppCompatActivity
            implements GoogleApiClient.OnConnectionFailedListener {

        private String RECIPE = "recipe";
        private String PHOTO_URL = "photo";
        private String DESCRIPTION = "description";
        private String RECIPE_LIST = "recipeList";

        TextView descriptionRecipe;

        public static class CommentViewHolder extends RecyclerView.ViewHolder {
            public TextView commentTextView;
            public TextView commentatorTextView;
            public CircleImageView commentatorImageView;

            public CommentViewHolder(View v) {
                super(v);
                commentTextView = (TextView) itemView.findViewById(R.id.messageTextView);
                commentatorTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
                commentatorImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            }
        }


        private static final String MESSAGE_URL = "https://cookbook-6cce5.firebaseio.com/comments/";
        private static final String TAG = "RecipeActivity";
        public static final String ANONYMOUS = "anonymous";
        public String MESSAGES_CHILD;

        private String mUsername;
        private String mPhotoUrl;

        private Button mSendButton;
        private RecyclerView mCommentsRecyclerView;
        private LinearLayoutManager mLinearLayoutManager;
        private EditText mCommentEditText;

        private FirebaseAuth mFirebaseAuth;
        private FirebaseUser mFirebaseUser;
        private DatabaseReference mFirebaseDatabaseReference;
        private FirebaseRecyclerAdapter<Comment, CommentViewHolder> mFirebaseAdapter;
        private GoogleApiClient mGoogleApiClient;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_recipe);

            final Intent intent = getIntent();

            MESSAGES_CHILD = "comments/" + intent.getStringExtra(RECIPE_LIST) + "/" + intent.getStringExtra(RECIPE) + "/comments";

            descriptionRecipe = (TextView) findViewById(R.id.textView);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
//            Button btnDetailRecipe = (Button) findViewById(R.id.btn_detail_recipe);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ActionBar actionBar = getSupportActionBar();

            mCommentsRecyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
            mCommentEditText = (EditText) findViewById(R.id.editTextComent);
            mSendButton = (Button) findViewById(R.id.save_comments);

            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            mCommentsRecyclerView.setLayoutManager(mLinearLayoutManager);


            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();

            if (mFirebaseUser == null) {
                // Not signed in, launch the Sign In activity
                mUsername = ANONYMOUS;
                return;
            } else {
                mUsername = mFirebaseUser.getDisplayName();
                if (mFirebaseUser.getPhotoUrl() != null) {
                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                }
            }

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();

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

    //                 write this message to the on-device index
                    FirebaseAppIndex.getInstance().update(getCommentIndexable(comment));
    //                 log a view action on it
                    FirebaseUserActions.getInstance().end(getCommentViewAction(comment));
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
                    Comment comment = new
                            Comment(mCommentEditText.getText().toString(),
                            mUsername,
                            mPhotoUrl);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                            .push().setValue(comment);
                    mCommentEditText.setText("");
                }
            });


            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.starPartiallySelected), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.starNotSelected), PorterDuff.Mode.SRC_ATOP);

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {

                    Toast.makeText(getApplicationContext(), "рейтинг: " + String.valueOf(rating),
                            Toast.LENGTH_LONG).show();
                }
            });


            actionBar.setTitle(intent.getStringExtra(RECIPE));
            Glide.with(this).load(intent.getStringExtra(PHOTO_URL)).into(imageView);
            descriptionRecipe.setText(intent.getStringExtra(DESCRIPTION));

//            btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intentStepRecipeActivity = new Intent(getApplicationContext(), StepRecipeActivity.class);
//                    intentStepRecipeActivity.putExtra(RECIPE, intent.getStringExtra(RECIPE));
//                    intentStepRecipeActivity.putExtra(PHOTO_URL, intent.getStringExtra(PHOTO_URL));
//                    intentStepRecipeActivity.putExtra(DESCRIPTION, intent.getStringExtra(DESCRIPTION));
//                    startActivity(new Intent(intentStepRecipeActivity));
//                }
//            });


        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.activity_recipe, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_search) {

                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        private Indexable getCommentIndexable(Comment comment) {
            PersonBuilder sender = Indexables.personBuilder()
                    .setIsSelf(mUsername == comment.getName())
                    .setName(comment.getName())
                    .setUrl(MESSAGE_URL.concat(comment.getId() + "/sender"));

            PersonBuilder recipient = Indexables.personBuilder()
                    .setName(mUsername)
                    .setUrl(MESSAGE_URL.concat(comment.getId() + "/recipient"));

            Indexable commentToIndex = Indexables.messageBuilder()
                    .setName(comment.getText())
                    .setUrl(MESSAGE_URL.concat(comment.getId()))
                    .setSender(sender)
                    .setRecipient(recipient)
                    .build();

            return commentToIndex;
        }

        private Action getCommentViewAction(Comment comment) {
            return new Action.Builder(Action.Builder.VIEW_ACTION)
                    .setObject(comment.getName(), MESSAGE_URL.concat(comment.getId()))
                    .setMetadata(new Action.Metadata.Builder().setUpload(false))
                    .build();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            // An unresolvable error has occurred and Google APIs (including Sign-In) will not
            // be available.
            Log.d(TAG, "onConnectionFailed:" + connectionResult);
            Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        }
    }
}
