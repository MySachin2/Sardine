package com.fish.sardine.sardine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView navigationView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String roleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(Utils.pref,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.container, new MainListFragment(),"MainFragment").commit();
        navigationView.getMenu().getItem(0).setChecked(true);
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.display_name);
        TextView phone = navigationView.getHeaderView(0).findViewById(R.id.display_phone);

        name.setText(sharedPreferences.getString("name",""));
        phone.setText(sharedPreferences.getString("phone",""));

        roleText = sharedPreferences.getString("role","");
        if (roleText.equals("Admin")) {
            addMenuItemInNavMenuDrawer();
        }

    }
    private void addMenuItemInNavMenuDrawer() {
        Menu menu = navigationView.getMenu();
        Menu submenu = menu.addSubMenu("Admin Area");

        submenu.add(R.id.main_nav, 9567909, Menu.NONE, "Add Fish").setIcon(R.drawable.ic_add_circle_black_24dp);

        navigationView.invalidate();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment myFragment =  getSupportFragmentManager().findFragmentByTag("MainFragment");
            if (myFragment != null && myFragment.isVisible()) {
                super.onBackPressed();
            }
            else
            {
                Menu menu = navigationView.getMenu();
                menu.getItem(0).setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainListFragment(),"MainFragment").commit();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainListFragment(),"MainFragment").commit();
            // Handle the camera action
        } else if (id == R.id.nav_orders) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentViewOrders(),"OrderFragment").commit();

        }else if (id == R.id.nav_contact) {

        }else if (id == 9567909) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentAddFish(),"AddFishFragment").commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
