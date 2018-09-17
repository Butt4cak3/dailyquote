package io.github.dasspike.dailyquote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

class QuotePagerAdapter extends FragmentStatePagerAdapter {

    // TODO: Fetch categories and show tabs dynamically
    // * Use the /qod/categories API
    // * Move outside adapter?

    private static final String ENDPOINT = "https://quotes.rest/qod?category=";
    private static final String[] URLS =
            {"inspire", "love", "life", "sports", "students", "funny", "art", "management"};
    private static final String[] CATEGORIES =
            {"Inspiring", "Love", "Life", "Sports", "Students", "Funny", "Art", "Management"};

    QuotePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new QuoteFragment();
        Bundle args = new Bundle();
        args.putString("url", ENDPOINT + URLS[i]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CATEGORIES[position];
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);

        QuoteFragment fragment = (QuoteFragment) object;
        fragment.setActionBarTitle();
    }
}
