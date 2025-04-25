package com.vyorius.rtsp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import android.app.PictureInPictureParams;
import android.util.Rational;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText rtspUrlInput;
    private Button playButton;
    private VLCVideoLayout videoLayout;

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private Button recordButton, stopStreamButton;
    private boolean isRecording = false;
    private Button pipButton;
    private ViewGroup.LayoutParams originalVideoLayoutParams;

    private MediaPlayer mediaRecorder; // for recording only


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find UI elements
        rtspUrlInput = findViewById(R.id.rtsp_url_input);
        playButton = findViewById(R.id.play_button);
        videoLayout = findViewById(R.id.video_layout);
        recordButton = findViewById(R.id.record_button);
        pipButton = findViewById(R.id.pip_button);
        stopStreamButton= findViewById(R.id.stop_button);

        // Initialize LibVLC with some options
        ArrayList<String> options = new ArrayList<>();
        options.add("--no-drop-late-frames");
        options.add("--no-skip-frames");
        libVLC = new LibVLC(this, options);

        // Create VLC MediaPlayer
        mediaPlayer = new MediaPlayer(libVLC);

        // Attach Video Layout to player
        mediaPlayer.attachViews(videoLayout, null, false, false);

        // Set Buttons functionality
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = rtspUrlInput.getText().toString().trim();

                if (!url.isEmpty()) {
                    if (isValidUrl(url)) {
//                        url = url.substring(7); // Remove "file://" part
                        playRtspStream(url);
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        File recordDir = new File(getExternalFilesDir("recordings").getAbsolutePath());
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = rtspUrlInput.getText().toString().trim();
                if (!url.isEmpty()) {
                    if (!isRecording) {
                        startRecordingStream(url);
                        recordButton.setText("Stop");
                    } else {
//                        stopRecording();
                        stopRecordingStream();
                        recordButton.setText("Record");
                    }
                    isRecording = !isRecording;
                }
            }
        });

        pipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPipMode();
            }
        });

        stopStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStreaming();
            }
        });

    }

    private boolean isValidUrl(String url) {
        // Simple check to ensure the URL starts with "rtsp://"
        return url.startsWith("rtsp://");
    }

    private void playRtspStream(String rtspUrl) {
        if (!rtspUrl.startsWith("rtsp://")) {
            rtspUrl = "rtsp://" + rtspUrl;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        Uri rtspUri = Uri.parse(rtspUrl);
//        Media media = new Media(libVLC, rtspUri);
        Media media = new Media(libVLC, rtspUri);
        media.setHWDecoderEnabled(true, false);
        media.addOption(":network-caching=150"); // Optional: reduce latency
        media.addOption(":rtsp-tcp"); // Optionally force TCP for RTSP
        mediaPlayer.setMedia(media);
        media.release(); // Release after setting

        try {
            mediaPlayer.play();
        } catch (Exception e) {
            Toast.makeText(this, "Error playing stream: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
        //this is not working so created the duplicate.
//    private void startRecordingStream(String rtspUrl) {
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//        }
//
//        // File path to save
//        String fileName = "recorded_" + System.currentTimeMillis() + ".ts";
////        String fileName = "recorded_" + System.currentTimeMillis() + ".mkv";
//        String filePath = getExternalFilesDir("recordings").getAbsolutePath() + "/" + fileName;
//
//        Uri rtspUri = Uri.parse(rtspUrl);
//        // Setup media with output options
//        Media media = new Media(libVLC, rtspUri);
////        media.addOption(":sout=#file{dst=" + filePath + "}");
////        media.addOption(":no-sout-all");
////        media.addOption(":sout-keep");
//
//        media.addOption(":sout=#duplicate{dst=display,dst=standard{access=file,mux=ts,dst=" + filePath + "}}");
//
//        media.setHWDecoderEnabled(true, false);
//        media.addOption(":network-caching=150");
//
//        mediaPlayer.setMedia(media);
//        media.release();
//
//        mediaPlayer.play();


//    }

    private void stopStreaming() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            Toast.makeText(this, "Streaming Stopped.", Toast.LENGTH_SHORT).show();
        }
    }

    private void enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(videoLayout.getWidth(), videoLayout.getHeight());
            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(aspectRatio);
            enterPictureInPictureMode(pipBuilder.build());
        } else {
            Toast.makeText(this, "PiP mode requires Android 8.0 or higher", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        if (isInPictureInPictureMode) {
            if (originalVideoLayoutParams == null) {
                originalVideoLayoutParams = videoLayout.getLayoutParams();
            }
            RelativeLayout.LayoutParams fullScreenParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            videoLayout.setLayoutParams(fullScreenParams);
            findViewById(R.id.rtsp_url_input).setVisibility(View.GONE);
            findViewById(R.id.play_button).setVisibility(View.GONE);
            findViewById(R.id.record_button).setVisibility(View.GONE);
            findViewById(R.id.pip_button).setVisibility(View.GONE);
            findViewById(R.id.stop_button).setVisibility(View.GONE);
            if (getSupportActionBar() != null) getSupportActionBar().hide();
        } else {
            // Restore all UI when PiP exits
            if (originalVideoLayoutParams != null) {
                videoLayout.setLayoutParams(originalVideoLayoutParams);
            }
            findViewById(R.id.rtsp_url_input).setVisibility(View.VISIBLE);
            findViewById(R.id.play_button).setVisibility(View.VISIBLE);
            findViewById(R.id.record_button).setVisibility(View.VISIBLE);
            findViewById(R.id.pip_button).setVisibility(View.VISIBLE);
            findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) getSupportActionBar().show();
        }
    }

    private void startRecordingStream(String rtspUrl) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
//        String fileName = "recorded_" + System.currentTimeMillis() + ".ts"; //change this if needed other extension.
        String fileName = "recorded_" + System.currentTimeMillis() + ".mkv";
        String filePath = getExternalFilesDir("recordings").getAbsolutePath() + "/" + fileName;

        Uri rtspUri = Uri.parse(rtspUrl);

        // Create a new MediaPlayer only for recording
        mediaRecorder = new MediaPlayer(libVLC);
        Media media = new Media(libVLC, rtspUri);

        // Duplicate: one for saving, ignore display
        media.addOption(":sout=#standard{access=file,mux=ts,dst=" + filePath + "}");
        media.addOption(":sout-keep");
        media.addOption(":network-caching=300");

        media.setHWDecoderEnabled(true, false);

        mediaRecorder.setMedia(media);
        media.release();
        mediaRecorder.play();
    }

    private void stopRecordingStream() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Recording saved!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.detachViews();
        mediaPlayer.release();
        libVLC.release();
    }

}
