package com.thangld.managechildren.main.child;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thangld.managechildren.R;
import com.thangld.managechildren.collector.observer.ObserverService;
import com.thangld.managechildren.entry.ChildEntry;
import com.thangld.managechildren.main.parent.ContactFragment;
import com.thangld.managechildren.main.parent.RulesFragment;
import com.thangld.managechildren.main.parent.SmsFragment;
import com.thangld.managechildren.storage.controller.PreferencesController;
import com.thangld.managechildren.storage.model.ChildModel;

import java.util.ArrayList;

public class PanelParentInChildDeviceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationView.OnClickListener {

    private NavigationView navigationView;

    private View headerNavView;
    private ImageButton redirectionMenu;
    private TextView nameExtra;
    private TextView nameActive;


    private Toolbar toolbar;
    private boolean isMenuListChild;

    private Context mContext;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawer;

    private ArrayList<ChildEntry> mChildList = new ArrayList<>();
    private ChildEntry mChildActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_parent_in_child_device);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mChildActive = ChildModel.QueryHelper.getChildObjectActive(mContext);
        if (mChildActive == null) {
            // quay lại giao diện chọn child

        }

        initViews();
        setViews();
        setListeners();

    }

    private void initViews() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ChildEntry child;
                int countSet = 0;
//                if (mChildList.size() == 0) {
//                    ((ImageView) findViewById(R.id.avatar_active)).setVisibility(View.INVISIBLE);
//                } else if (mChildList.size() == 1) {
//                    child = mChildList.get(0);
//                    nameActive.setText(child.getFullName());
//                } else {
//                    ((ImageView) findViewById(R.id.avatar_extra)).setVisibility(View.VISIBLE);
//                    for (int i = 0; i < mChildList.size(); i++) {
//                        if (countSet >= 2)
//                            break;
//                        child = mChildList.get(i);
//                        if (child.getIsActive() == 1) {
//                            nameActive.setText(child.getFullName());
//                            countSet++;
//                        } else {
//                            nameExtra.setText(child.getFullName());
//                            countSet++;
//                        }
//                    }
//                }


            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerNavView = navigationView.getHeaderView(0);

        redirectionMenu = (ImageButton) headerNavView.findViewById(R.id.redirection_menu);
        nameActive = (TextView) headerNavView.findViewById(R.id.name_active);
        nameExtra = (TextView) headerNavView.findViewById(R.id.name_extra);
        isMenuListChild = false;


    }

    public void setViews() {
        nameActive.setText(mChildActive.getFullName());
    }

    private void setListeners() {
        redirectionMenu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean isExit = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (isExit) {
                isExit = true;
                Toast.makeText(mContext, getString(R.string.confirm_exit), Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.panel, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();
        String device_id = ChildModel.QueryHelper.getDeviceIdChildActive(mContext);
        if (id == R.id.nav_rules) {
            fragment = new RulesFragment();
            replaceFragmentInNavContainer(fragment);
        } else if (id == R.id.nav_information) {
            fragment = new ContactFragment();
            replaceFragmentInNavContainer(fragment);

        } else if (id == R.id.nav_change_child) {
            fragment = new SmsFragment();
            replaceFragmentInNavContainer(fragment);
        } else if (id == R.id.nav_uninstall) {
            replaceFragmentInNavContainer(fragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragmentInNavContainer(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.navigation_container, fragment)
                    .commit();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.redirection_menu:
                isMenuListChild = !isMenuListChild;
                Menu menu = navigationView.getMenu();
                menu.clear();
                if (isMenuListChild) {
                    redirectionMenu.setImageResource(R.drawable.arrow_up);
                    for (ChildEntry child : mChildList) {
                        menu.add(child.getFullName()).setIcon(R.drawable.child_drak_grey);
                    }
                } else {
                    redirectionMenu.setImageResource(R.drawable.arrow_down);
                    navigationView.inflateMenu(R.menu.activity_panel_drawer);
                }
                break;
        }
    }

    private void setExtraChildHide() {
        ImageView avatarExtra = (ImageView) findViewById(R.id.avatar_extra);
        avatarExtra.setVisibility(View.INVISIBLE);
    }


    public static void startMonitorForDevice(Context context, String childId) {
        // dat child
        ChildModel.QueryHelper.setChildIdActive(context, childId);

        // set privilege lien quan
        PreferencesController preferencesController = new PreferencesController(context);
        preferencesController.putPrivilege(PreferencesController.PRIVILEGE_CHILD);

        // Khoi dong service montitor
        Intent intent = new Intent(context, ObserverService.class);
        intent.setAction(ObserverService.TYPE_CHILD_ENABLE_FIRST);
        context.startService(intent);
    }


}
