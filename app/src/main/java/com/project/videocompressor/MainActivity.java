package com.project.videocompressor;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.abedelazizshe.lightcompressorlibrary.config.Configuration;
import com.abedelazizshe.lightcompressorlibrary.config.StorageConfiguration;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.elevation.SurfaceColors;
import com.permissionx.guolindev.PermissionX;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
ArrayList<Uri> uriArrayList;
    ProgressBar progressBar;
    ExoPlayer exoplayer;
    String quality;
    Button pickfilebtn;

    TextView textView;
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK&& requestCode==100)
        {
            uriArrayList = new ArrayList<>();
            assert data != null;
            uriArrayList.add(
                    data.getData()
            );
            String filename = Integer.toString((int) System.currentTimeMillis()) + ".mp4";
            StorageConfiguration storageConfiguration = new StorageConfiguration(
                    filename, // => an optional value for a custom video name.
                    Environment.DIRECTORY_MOVIES,  // => the directory to save the compressed video(s). Will be ignored if isExternal = false.
                    true // => false means save at app-specific file directory. Default is true.
            );
            Configuration configuration = null;
            if (Objects.equals(quality, "Low"))
            {
                configuration= new Configuration(
                        VideoQuality.VERY_LOW,
                        false, /*isMinBitrateCheckEnabled*/
                        null, /*videoBitrate: int, or null*/
                        false, /*disableAudio: Boolean, or null*/
                        false, /*keepOriginalResolution: Boolean, or null*/
                        null, /*videoWidth: Double, or null*/
                        null /*videoHeight: Double, or null*/
                );
            }
            if (Objects.equals(quality, "Medium"))
            {
                configuration= new Configuration(
                        VideoQuality.MEDIUM,
                        false, /*isMinBitrateCheckEnabled*/
                        null, /*videoBitrate: int, or null*/
                        false, /*disableAudio: Boolean, or null*/
                        false, /*keepOriginalResolution: Boolean, or null*/
                        null, /*videoWidth: Double, or null*/
                        null /*videoHeight: Double, or null*/
                );
            }
            if (Objects.equals(quality, "High"))
            {
                configuration= new Configuration(
                        VideoQuality.HIGH,
                        false, /*isMinBitrateCheckEnabled*/
                        null, /*videoBitrate: int, or null*/
                        false, /*disableAudio: Boolean, or null*/
                        false, /*keepOriginalResolution: Boolean, or null*/
                        null, /*videoWidth: Double, or null*/
                        null /*videoHeight: Double, or null*/
                );
            }

            assert configuration != null;
            VideoCompressor.start(
                    getApplicationContext(), // => This is required
                   uriArrayList, // => Source can be provided as content uris
                    false, // => isStreamable
                    storageConfiguration,
                    configuration,
                    new CompressionListener() {


                        @Override
                        public void onSuccess(int i, long l, String s) {
//                            Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                            textView.setText("Video Compression Completed with "+quality+" quality.\n File saved in MOVIES Folder");
                            String vpath = Environment.DIRECTORY_MOVIES +File.separator+filename;
                            String path = Environment.getExternalStorageDirectory() + File.separator + "Movies" + File.separator + filename;
                            exoplayer = new ExoPlayer.Builder(MainActivity.this).build();
//            com.google.android.exoplayer2.ui.StyledPlayerView
                            com.google.android.exoplayer2.ui.StyledPlayerView styledplayerView = findViewById(R.id.videoview);
                            styledplayerView.setVisibility(View.VISIBLE);

                            styledplayerView.setPlayer(exoplayer);
                            styledplayerView.setUseController(true);
                            MediaItem mediaItem= MediaItem.fromUri(Uri.parse(path));
                            exoplayer.addMediaItem(mediaItem);
                            exoplayer.setRepeatMode(exoplayer.REPEAT_MODE_ONE);
                            exoplayer.prepare();
                            exoplayer.play();
                            setHideButton(false);
                        }

                        @Override
                        public void onStart(int i) {
                            textView.setText("Video Compression Started with "+quality+" quality.");
                            setHideButton(true);

                        }



                        @Override
                        public void onFailure(int index, String failureMessage) {
                            // On Failure
                            textView.setText("Video Compression Failed : "+failureMessage );
                            setHideButton(false);
                        }

                        @Override
                        public void onProgress(int index, float progressPercent) {
                            // Update UI with progress value
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    textView.setText(new StringBuilder().append(progressPercent).append(" Completed.").toString());
                                    progressBar.setProgress((int) progressPercent,true);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(int index) {
                            // On Cancelled
                            textView.setText("Video Compression Cancled." );
                            setHideButton(false);
                        }
                    }
            );



        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int color =  SurfaceColors.SURFACE_0.getColor(this);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(color);

        int night= getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if(night == android.content.res.Configuration.UI_MODE_NIGHT_NO)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        PermissionX.init(this);

        progressBar = findViewById(R.id.progressBar);
        textView= findViewById(R.id.textView);
         pickfilebtn = findViewById(R.id.button);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);



        pickfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("video/*");


                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);
                quality =  radioButton.getText().toString();



                startActivityForResult(intent,100);





            }
        });





    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onConfigurationChanged(@NonNull android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int night = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (night == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    void setHideButton(boolean should_hide)
    {
        if(should_hide)
        {
            pickfilebtn.setVisibility(View.GONE);
        }
        else
        {
            pickfilebtn.setVisibility(View.VISIBLE);
        }
    }

}