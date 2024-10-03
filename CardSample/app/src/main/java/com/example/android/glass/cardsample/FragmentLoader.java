package com.example.android.glass.cardsample;
import com.example.android.glass.cardsample.fragments.BaseFragment;
import com.example.android.glass.cardsample.fragments.LightFragment;
import com.example.android.glass.cardsample.fragments.SwitchFragment;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;

public class FragmentLoader {

    public static ArrayList<BaseFragment> loadFragmentsFromJson(Context context, String fileName) {
        ArrayList<BaseFragment> fragments = new ArrayList<>();
        try {
            // Load JSON from asset or file
            String json = loadJsonFromAssets(context, fileName);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("fragments");

            // Iterate through the fragment configurations
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject fragmentJson = jsonArray.getJSONObject(i);
                BaseFragment fragment = createFragmentFromJson(context, fragmentJson);
                if (fragment != null) {
                    fragments.add(fragment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragments;
    }

    private static String loadJsonFromAssets(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private static BaseFragment createFragmentFromJson(Context context, JSONObject fragmentJson) {
        try {
            String type = fragmentJson.getString("type");
            switch (type) {
                case "LightFragment":
                    return LightFragment.newInstance(
                            context,
                            convertToDrawable(fragmentJson.getString("drawable")),
                            fragmentJson.getString("text"),
                            fragmentJson.getString("footnote"),
                            fragmentJson.getString("timestamp")
                    );
                case "SwitchFragment":
                    return SwitchFragment.newInstance(
                            context,
                            fragmentJson.getString("name"),
                            fragmentJson.getString("entity_id")
                    );
                case "ColumnLayoutFragment":
                    // Implement similar logic for ColumnLayoutFragment
                    break;
                // Add cases for other fragment types
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int convertToDrawable(String drawableName) {
        // Implement logic to convert drawableName to actual drawable resource ID
        return 0;
    }
}
