package io.github.dasspike.dailyquote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuoteViewerFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quote_viewer, container, false);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            QuotePagerAdapter pagerAdapter = new QuotePagerAdapter(activity.getSupportFragmentManager());
            ViewPager viewPager = rootView.findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);
            TabLayout tabs = rootView.findViewById(R.id.pager_tab_layout);
            tabs.setupWithViewPager(viewPager);
        }

        return rootView;
    }

}