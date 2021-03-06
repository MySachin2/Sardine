package com.fish.sardine.sardine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class FragmentAddFish extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    DatabaseReference mRef, ref;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Spinner spinner;
    ImageView imageViewFish;
    ProgressBar pBar;
    FirebaseStorage storage;
    private Uri fileUri, downloadUrl;
    String mCurrentPhotoPath, currentFish, mPrice, mtotal;
    EditText mPriceEdit, mTotalEdit;
    boolean photoTaken = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_fish, container, false);
        pBar = (ProgressBar) view.findViewById(R.id.determinateBar);
        pBar.setVisibility(View.INVISIBLE);

        storage = FirebaseStorage.getInstance();

        final ArrayList<String> fishes = new ArrayList<String>();
        spinner = (Spinner) view.findViewById(R.id.name);
        imageViewFish = (ImageView) view.findViewById(R.id.imageView2);
        mPriceEdit = (EditText) view.findViewById(R.id.price);
        mTotalEdit = (EditText) view.findViewById(R.id.total_qty);
        sharedPreferences = getActivity().getSharedPreferences(Utils.pref,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Fish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    fishes.add(ds.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (getContext(),android.R.layout.simple_list_item_1,fishes);
                spinner.setAdapter(adapter);
                spinner.setSelection(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imageViewFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });



        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrice = mPriceEdit.getText().toString();
                mtotal = mTotalEdit.getText().toString();
                currentFish = spinner.getSelectedItem().toString();
                if(TextUtils.isEmpty(mPrice))
                {
                    mPriceEdit.setError("Price cannot be empty");
                }
                else if(TextUtils.isEmpty(mtotal))
                {
                    mTotalEdit.setError("Quantity cannot be empty");
                }
                else if(!photoTaken)
                {
                    Toast.makeText(getActivity(),"Photo not taken",Toast.LENGTH_LONG).show();
                }
                else {
                    StorageReference storageReference = storage
                            .getReferenceFromUrl("gs://sardine-b43e1.appspot.com")
                            .child(spinner.getSelectedItem().toString() + ".jpg");
                    imageViewFish.setDrawingCacheEnabled(true);
                    imageViewFish.buildDrawingCache();
                    Bitmap bitmap = imageViewFish.getDrawingCache();

                    pBar.setVisibility(View.VISIBLE);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] dataB = baos.toByteArray();



                    UploadTask uploadTask = storageReference.putBytes(dataB);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.d("Progress", "Upload is " + progress + "% done");
                            int p = (int) progress;
                            pBar.setProgress(p);
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                        }
                    });
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            pBar.setVisibility(View.INVISIBLE);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            pBar.setVisibility(View.INVISIBLE);
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            System.out.println("Success");
                            System.out.println(downloadUrl);

                            mRef.child("Fish").child(currentFish).child("Image").setValue(downloadUrl.toString());
                            mRef.child("Fish").child(currentFish).child("Price").setValue(mPrice);
                            mRef.child("Fish").child(currentFish).child("Total").setValue(mtotal);
                        }
                    });
                }

//                if (!mPrice.matches("") && !mtotal.matches("")) {
//                    ref.child("Mackerel").child("Image").setValue(downloadUrl.toString());
//                    ref.child("Mackerel").child("Price").setValue(mPrice);
//                    ref.child("Mackerel").child("Total").setValue(mtotal);
//                    Log.d("Updation Status", "Update successful: "+ mPrice +" " + mtotal);
//                } else {
//                    if (mPrice.matches("")) {
//                        mPriceEdit.setError("Enter price of fish");
//                    }
//                    if (mtotal.matches("")){
//                        mTotalEdit.setError("Enter total quantity of fish");
//                    }
//                }
            }
        });
        return view;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                fileUri = FileProvider.getUriForFile(getContext(),
                        "com.fish.sardine.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("Storage Directory", storageDir.toString());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
                );
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("mCurrentPhotoPath", mCurrentPhotoPath);
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
//            ImageCompression ic = new ImageCompression(getContext());
//            String compressedImageFilePath = ic.compressImage(mCurrentPhotoPath);
            final Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
            Log.d("mCurrentPhotoPath",mCurrentPhotoPath);
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap , 0, 0, imageBitmap .getWidth(), imageBitmap .getHeight(), matrix, true);
            imageViewFish.setImageBitmap(imageBitmap);
            photoTaken = true;
            //pBar.setVisibility(View.VISIBLE);


        }
    }
}
