package com.example.sensordemovimiento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class Videoview extends AppCompatActivity {


    VideoView videoview;
    String videoUrl="";
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);

        videoview=(VideoView)findViewById(R.id.video);
        pd= new ProgressDialog(Videoview.this);
        pd.show();
        Uri uri=Uri.parse(videoUrl);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pd.dismiss();

            }
        });

    }
}
