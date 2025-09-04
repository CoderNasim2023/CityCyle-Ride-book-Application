package com.example.citycycle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SpalashScreenActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen); // Ensure your layout file name matches this

        // Initialize VideoView
        videoView = findViewById(R.id.videoView);

        // Set video URI from raw resources
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video);
        videoView.setVideoURI(videoUri);

        // Optional: Mute the video if audio is not needed
        videoView.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f); // Mute video
//            mp.setLooping(false); // Optional: Prevent looping
        });

        // Move to MainActivity when video completes
        videoView.setOnCompletionListener(mp -> {
            startActivity(new Intent(SpalashScreenActivity.this, MainActivity.class));
            finish();
        });

        // to Start the video
        videoView.start();
    }
}
