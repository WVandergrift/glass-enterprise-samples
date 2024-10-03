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

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.glass.cardsample.HomeAssistant;
import com.example.android.glass.cardsample.R;

/**
 * Fragment with the two column layout.
 */
public class SwitchFragment extends BaseFragment {

  private static final String IMAGE_KEY = "image_key";
  private static final String NAME_KEY = "name_key";
  private static final String ENTITY_ID = "entity_id";
  private static final int BODY_TEXT_SIZE = 40;
  private static final int IMAGE_PADDING = 40;
  private static HomeAssistant homeAssistant;

  /**
   * Returns new instance of {@link SwitchFragment}.
   *
   * @param name is a String with the name of the switch.
   */
  public static SwitchFragment newInstance(Context context, String name, String entityId) {
    final SwitchFragment myFragment = new SwitchFragment();
    homeAssistant = HomeAssistant.getInstance(context);

    final Bundle args = new Bundle();
    args.putInt(IMAGE_KEY, R.drawable.ic_switch);
    args.putString(NAME_KEY, name);
    args.putString(ENTITY_ID, entityId);
    myFragment.setArguments(args);

    return myFragment;
  }

  @Override
  public void onSingleTapUp() {
    if (getArguments() != null) {
      homeAssistant.toggleSwitch(getArguments().getString(ENTITY_ID));
      Toast.makeText(getActivity(), "Toggling Light", Toast.LENGTH_SHORT)
              .show();
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.left_column_layout, container, false);

    if (getArguments() != null) {
      final ImageView imageView = new ImageView(getActivity());
      imageView.setPadding(IMAGE_PADDING, IMAGE_PADDING, IMAGE_PADDING, IMAGE_PADDING);
      imageView.setImageResource(getArguments().getInt(IMAGE_KEY));

      final FrameLayout leftColumn = view.findViewById(R.id.left_column);
      leftColumn.addView(imageView);

      final TextView textView = new TextView(getActivity());
      textView.setText(getArguments().getString(NAME_KEY));
      textView.setTextSize(BODY_TEXT_SIZE);
      textView.setTypeface(Typeface.create(getString(R.string.thin_font), Typeface.NORMAL));

      final FrameLayout rightColumn = view.findViewById(R.id.right_column);
      rightColumn.addView(textView);
    }
    return view;
  }
}
