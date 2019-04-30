package com.example.gallery_and_camera_together;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends Activity {
    ImageView viewImage;
    Button b;
    private Intent galleryIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b=(Button)findViewById(R.id.btnSelectPhoto);
        viewImage=(ImageView)findViewById(R.id.viewImage);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }
    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA )!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, 10);

                    }
                    else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 10);
                    }

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 11);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK && data != null )
        {
            viewImage.setVisibility(View.VISIBLE);


            switch (requestCode)
            {
                case 10:

                    Bitmap bitmap= (Bitmap) data.getExtras().get("data");
                    viewImage = (ImageView) findViewById(R.id.viewImage);
                    viewImage.setImageBitmap(bitmap);
                    Uri image = data.getData();
//                    String[] filePathColumn1 = { MediaStore.Images.Media.DATA };
//                    Cursor cursor1 = managedQuery(
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            filePathColumn1, null, null, null);
//                    int column_index_data = cursor1
//                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    cursor1.moveToLast();
//
//                    String picturePath1 = cursor1.getString(column_index_data);
//                    Toast.makeText(MainActivity.this,"Path is:"+picturePath1,Toast.LENGTH_LONG).show();
                    //App.getSinltonPojo().setDoctorUpload_photo(picturePath1);

                    break;

                case 11:
                    Uri selectedImage = data.getData();
                    viewImage = (ImageView) findViewById(R.id.viewImage);
                    viewImage.setImageURI(selectedImage);

//                    close.setVisibility(View.VISIBLE);
//                    click.setVisibility(View.GONE);
//                    imagecaptured.setVisibility(View.VISIBLE);

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

//                    App.getSinltonPojo().setDoctorUploadCertificate(picturePath);
//                    Toast.makeText(this, "certificate path:"+picturePath, Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 10:
                boolean cam=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if (grantResults.length>0 && cam){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 10);
                }else if (Build.VERSION.SDK_INT>23 && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
                    builder.setTitle("Permissions");
                    builder.setMessage("Permissions are required");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Go to the setting for granting permissions", Toast.LENGTH_SHORT).show();
                            boolean sentToSettings = true;
                            Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri=Uri.fromParts("package",MainActivity.this.getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).create().show();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
                }
                break;

            case 11:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 11);

                } else if (Build.VERSION.SDK_INT>23 && !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
                    builder.setTitle("Permissions");
                    builder.setMessage("Permissions are required");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Go to the setting for granting permissions", Toast.LENGTH_SHORT).show();
                            boolean sentToSettings = true;
                            Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri=Uri.fromParts("package",MainActivity.this.getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).create().show();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
                }

                break;
        }
    }

}