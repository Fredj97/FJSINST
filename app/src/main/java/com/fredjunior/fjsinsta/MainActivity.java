package com.fredjunior.fjsinsta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import com.fredjunior.fjsinsta.Models.Post;

public class MainActivity extends AppCompatActivity {

    String TAG= "MainActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName=  "photo.jpg";
     EditText etDescription;
     ImageView ivPostImage;
     Button btnSubmit;
     File photoFile;
     ImageView TakePic;
     ImageView Account;
     ImageView Homepage;



    public void showAlertDialogButtonClicked(View view ) {

        //setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //add the buttons
        builder.setPositiveButton("TAKE PICTURE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                LaunchCamera();
            }
        });
        //create and show the alert dialog
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        //alert.setTitle("AlertDialogExample");
        alert . show ();
    }




    public void showAlertDialogButtonClicked1(View view ) {

        //setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //add the buttons
        builder.setPositiveButton("LOGOOUT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        //create and show the alert dialog
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        //alert.setTitle("AlertDialogExample");
        alert . show ();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription=findViewById(R.id.etDescription);
        ivPostImage=findViewById(R.id.ivPostImage);
        btnSubmit=findViewById(R.id.btnSubmit);
        TakePic=findViewById(R.id.TakePic);
        Homepage=findViewById(R.id.HomePage);
        Account=findViewById(R.id.Account);


        TakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
                if(ivPostImage!=null){
                    etDescription.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }

            }
        });

        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked1(view);

            }
        });
        //queryPosts();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             String Description= etDescription.getText().toString();
             if(Description.isEmpty()){
                 Toast.makeText(MainActivity.this, "Description cannit be empty", Toast.LENGTH_SHORT).show();
                 return;
             }

             if(photoFile == null || ivPostImage.getDrawable() == null){
                 Toast.makeText(MainActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
                 return;
             }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(Description, currentUser, photoFile);
            }
        });
    }

    private void LaunchCamera(){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //by this we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                //RESIZE BITMAP, see the section below
                //Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);

            }
        }
    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file= new File(mediaStorageDir.getPath()+ File.separator+ fileName);
        return file;

    }

    private void savePost(String Description, ParseUser currentUser, File photoFile) {
        Post post= new Post();
        post.setDescription(Description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e !=null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(MainActivity.this, "Erro while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });

    }

    private void queryPosts() {
        ParseQuery<Post> query= ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post:  " + post.getDescription() + ",username: " + post.getUser().getUsername());
                }
            }
        });
    }
    }