/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.glass.cardsample.fragments;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.glass.cardsample.R;

import java.util.ArrayList;

/**
 * Fragment with the main card layout.
 */
public class CameraFragment extends BaseFragment implements SurfaceHolder.Callback {
  private LibVLC libVLC;
  private MediaPlayer mediaPlayer;

  private static final String TEXT_KEY = "text_key";
  private static final String FOOTER_KEY = "footer_key";
  private static final String TIMESTAMP_KEY = "timestamp_key";
  private static final int BODY_TEXT_SIZE = 40;
  private static final String RTSP_URL = "";

  /**
   * Returns new instance of {@link CameraFragment}.
   *
   */
  public static CameraFragment newInstance(String rtspUrl) {
    final CameraFragment myFragment = new CameraFragment();

    final Bundle args = new Bundle();
    args.putString("RTSP_URL", rtspUrl);
    myFragment.setArguments(args);

    return myFragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.camera_layout, container, false);

    if (getArguments() != null) {
      final SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
      surfaceView.getHolder().addCallback(this);

      final TextView textView = new TextView(getContext());
      textView.setText(getArguments().getString(TEXT_KEY, getString(R.string.empty_string)));
      textView.setTextSize(BODY_TEXT_SIZE);
      textView.setTypeface(Typeface.create(getString(R.string.thin_font), Typeface.NORMAL));

      final FrameLayout bodyLayout = view.findViewById(R.id.body_layout);
      bodyLayout.addView(textView);

      final TextView footer = view.findViewById(R.id.footer);
      footer.setText(getArguments().getString(FOOTER_KEY, getString(R.string.empty_string)));

      final TextView timestamp = view.findViewById(R.id.timestamp);
      timestamp.setText(getArguments().getString(TIMESTAMP_KEY, getString(R.string.empty_string)));

      // Setup VLC and the media player
      ArrayList<String> options = new ArrayList<>();
      libVLC = new LibVLC(getContext(), options);
      mediaPlayer = new MediaPlayer(libVLC);
      mediaPlayer.getVLCVout().setWindowSize(640, 360);
    }
    return view;
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    String rtspUrl = getArguments().getString("RTSP_URL", ""); // Default to empty if not found

    mediaPlayer.getVLCVout().setVideoSurface(holder.getSurface(), holder);
    mediaPlayer.getVLCVout().attachViews();

    Media media = new Media(libVLC, Uri.parse(rtspUrl));
    media.setHWDecoderEnabled(true, false);
    media.addOption(":network-caching=600");
    mediaPlayer.setMedia(media);
    mediaPlayer.play();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    // Handle surface changes as needed
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    mediaPlayer.stop();
    mediaPlayer.getVLCVout().detachViews();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mediaPlayer.release();
    libVLC.release();
  }
}
