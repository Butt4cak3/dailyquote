package io.github.dasspike.dailyquote;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * This class represents the main app activity using a navigation drawer.
 */
public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private int selectedMenuItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fm = getSupportFragmentManager();
        NavigationView navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer);

        // set the toolbar as our apps action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add the hamburger button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        }

        // select the first entry at app start, otherwise keep the current one selected
        if (savedInstanceState == null) {
            MenuItem menuItem = navigationView.getMenu().getItem(0);
            menuItem.setChecked(true);
            selectedMenuItemID = menuItem.getItemId();
            fm.beginTransaction()
                    .replace(R.id.content_frame, new QuoteViewerFragment())
                    .commit();
        } else {
            selectedMenuItemID = savedInstanceState.getInt("selectedMenuItemID");
        }

        // listen for navigation item selection changes
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    if (menuItem.getItemId() != selectedMenuItemID) {
                        selectedMenuItemID = menuItem.getItemId();
                        switch (menuItem.getItemId()) {
                            case R.id.quote_viewer:
                                fm.beginTransaction()
                                        .replace(R.id.content_frame, new QuoteViewerFragment())
                                        .commit();
                                break;
                            case R.id.settings:
                                fm.beginTransaction()
                                        .replace(R.id.content_frame, new SettingsFragment())
                                        .commit();
                                break;
                            case R.id.about:
                                fm.beginTransaction()
                                        .replace(R.id.content_frame, new AboutFragment())
                                        .commit();
                        }
                    }
                    drawer.closeDrawers();
                    return true;
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedMenuItemID", selectedMenuItemID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
