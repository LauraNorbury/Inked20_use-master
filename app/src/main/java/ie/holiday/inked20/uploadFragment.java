package ie.holiday.inked20;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class uploadFragment extends Fragment {


    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageURI; //for pointing to image


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseref;


    private StorageTask mUploadTask;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_upload, container, false);


        mButtonChooseImage = RootView.findViewById(R.id.button_choose_image);
        mButtonUpload = RootView.findViewById(R.id.button_upload);
        mTextViewShowUploads = RootView.findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = RootView.findViewById(R.id.edit_text_file_name);
        mImageView = RootView.findViewById(R.id.image_view);
        mProgressBar = RootView.findViewById(R.id.progress_bar);

        FirebaseApp.initializeApp(getContext( ));


        mStorageRef = FirebaseStorage.getInstance( ).getReference("uploads"); //uploading to a folder i called uploads
        mDatabaseref = FirebaseDatabase.getInstance( ).getReference("uploads"); //same again here


        mButtonChooseImage.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

                openFileChooser( );

            }
        });


        mButtonUpload.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress( )) {  //so user cant spamthe upload button and upload multiples

                    Toast.makeText(getContext( ), "Uploading...", Toast.LENGTH_SHORT).show( );
                } else {

                    uploadFile( );
                }
            }
        });


        mTextViewShowUploads.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

                openDashboardFragment( );

            }
        });


        return RootView;
    }


    private void openFileChooser() {

        Intent intent = new Intent( );
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData( ) != null) {

            mImageURI = data.getData( );

            Picasso.get( ).load(mImageURI).into(mImageView);

        }
    }

    private String getFileExtension(Uri uri) {  //taking image extension https://www.youtube.com/watch?v=lPfQN-Sfnjw


        ContentResolver cr = getActivity( ).getContentResolver( );
        MimeTypeMap mime = MimeTypeMap.getSingleton( );
        return mime.getExtensionFromMimeType(cr.getType(uri));

    }


    private void uploadFile() {

        if (mImageURI != null) {

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis( )
                    + "." + getFileExtension(mImageURI));//saving the file as current time in milliseconds will give each one a unique name


            final String fileconext = fileReference.toString( );


            mUploadTask = fileReference.putFile(mImageURI);




                   /*
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>( ) {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler( );
                            handler.postDelayed(new Runnable( ) {
                                @Override
                                public void run() {

                                    mProgressBar.setProgress(0);
                                } */


            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>( ) {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful( )) {
                        throw task.getException( );
                    }
                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl( );
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>( ) {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful( )) {
                        Uri downloadUri = task.getResult( );
                        String miUrlOk = downloadUri.toString( );

                        Upload upload = new Upload(mEditTextFileName.getText( ).toString( ).trim( ), miUrlOk);
                        String uploadId = mDatabaseref.push( ).getKey( );
                        mDatabaseref.child(uploadId).setValue(upload);


                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }).addOnFailureListener(new OnFailureListener( ) {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext( ), e.getMessage( ), Toast.LENGTH_SHORT).show( );
                }
            });


        } else {
            Toast.makeText(getContext( ), "No File selected", Toast.LENGTH_SHORT).show( );
        }


        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

    }









                   /* }, 500);   //this is to delay y the progress bar from resarting without the user ever seeing it has completed successfully, for UX not for Functionality












                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                    taskSnapshot.getStorage().getDownloadUrl().toString());


                            String uploadID = mDatabaseref.push().getKey();
                            mDatabaseref.child(uploadID).setValue(upload) ;


                        }


                        })

                    .addOnFailureListener(new OnFailureListener( ) {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>( ) {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);

                        }
                    });


        } */

    private void openDashboardFragment(){


    }

}
