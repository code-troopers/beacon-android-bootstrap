package com.codetroopers.materialAndroidBootstrap.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codetroopers.materialAndroidBootstrap.R;
import com.codetroopers.materialAndroidBootstrap.core.HasComponent;
import com.codetroopers.materialAndroidBootstrap.core.components.ComponentsFactory;
import com.codetroopers.materialAndroidBootstrap.core.components.HomeActivityComponent;
import com.codetroopers.materialAndroidBootstrap.example.DummyContent;
import com.codetroopers.materialAndroidBootstrap.example.DummyContentFactory;
import com.codetroopers.materialAndroidBootstrap.ui.activity.core.BaseActionBarActivity;
import com.codetroopers.materialAndroidBootstrap.util.Strings;
import com.codetroopers.materialAndroidBootstrap.util.UIUtils;

import javax.inject.Inject;

import butterknife.Bind;
import icepick.State;
import timber.log.Timber;

import static java.lang.String.format;

public class HomeActivity extends BaseActionBarActivity implements DrawerAdapter.OnItemClickListener, HasComponent<HomeActivityComponent> {

    private HomeActivityComponent component;

    @Bind(R.id.drawer)
    DrawerLayout mDrawer;
    @Bind(R.id.left_drawer)
    RecyclerView mDrawerList;

    @Bind(R.id.content)
    TextView tvContent;

    @Inject
    DummyContentFactory dummyContentFactory;

    @State
    DummyContent dummyContent;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = getComponent();
        component.injectActivity(this);

        final String device = UIUtils.isTablet(this) ? "tablet" : "phone";
        Timber.d("Creating activity for a %s context...", device);
        /**
         * examples of {@link #com.codetroopers.materialAndroidBootstrap.util.Strings} utilities methods
         */
        Timber.d(Strings.joinAnd(", ", " and ",
                Strings.namedFormat("Android SDK = $sdkVersion ($release)",
                        "sdkVersion", Build.VERSION.SDK_INT,
                        "release", Build.VERSION.RELEASE),
                Strings.namedFormat("CODENAME = $codeName",
                        "codeName", Build.VERSION.CODENAME),
                Strings.namedFormat("INCREMENTAL = $incremental",
                        "incremental", Build.VERSION.INCREMENTAL)));

        setupDrawer();

        if (dummyContent == null) {
            dummyContent = dummyContentFactory.getDummyContent();
        }
        tvContent.setText(format("[%s] %s", dummyContent.creationDate(), dummyContent.content()));
    }

    private void setupDrawer() {
        mAdapter = new DrawerAdapter(this);

        mDrawerList.setAdapter(mAdapter);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawer.setStatusBarBackground(R.color.statusBarTransparentColor);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, getToolbar(), R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawer.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    @Override
    public HomeActivityComponent getComponent() {
        if (component == null) {
            component = ComponentsFactory.get().buildHomeActivityComponent(getApplicationComponent(), this);
        }
        return component;
    }

    private void selectItem(int position) {
        mDrawer.closeDrawer(mDrawerList);
        mAdapter.setActive(position);
    }
}
