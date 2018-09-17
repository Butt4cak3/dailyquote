package io.github.dasspike.dailyquote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This fragment is used for the 'About' page in the navigation drawer.
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        // show attribution notice
        TextView textView = rootView.findViewById(R.id.textView_link);
        textView.setText(getString(R.string.uses_api, getString(R.string.theysaidso_link)));

        return rootView;
    }

    // TODO: Populate the "About" fragment / style it

}
