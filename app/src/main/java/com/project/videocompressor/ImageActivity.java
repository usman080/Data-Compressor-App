package com.project.videocompressor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.permissionx.guolindev.PermissionX;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//@RuntimePermissions
public class ImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        //@NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
        Button pickimg = findViewById(R.id.button3);
        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView2);

        pickimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkp();
            }
        });
    }
    private void checkp(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            if(data!=null){
                Uri image=data.getData();
                compressImage(image);
            }
        }

    }
    private void compressImage(Uri imageUri){
        try{
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String fileName = String.format("%d.jpg",System.currentTimeMillis());
            File finalfile = new File(path,fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(finalfile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,50,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            imageView.setImageURI(Uri.fromFile(finalfile));
            textView.setText("Your Image is Compressed And Stored In DCIM FOLDER");
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(finalfile));
            sendBroadcast(intent);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}