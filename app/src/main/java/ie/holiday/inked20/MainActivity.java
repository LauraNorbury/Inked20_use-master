package ie.holiday.inked20;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import static ie.holiday.inked20.R.id.fragment_container;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;


    //for grid
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.delaroystudios.viewpagertogrid.key.currentPosition";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById( R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager( ).beginTransaction( ).replace(R.id.fragment_container, new dashboardFragment( )).commit( );

            navigationView.setCheckedItem(R.id.dashboard);

        }

        else if (savedInstanceState != null){

            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            // Return here to prevent adding additional GridFragments when changing orientation.
            return;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId( ) ){

            case R.id.upload:

                getSupportFragmentManager().beginTransaction().replace(fragment_container, new uploadFragment()).commit();

                break;

            case R.id.dashboard:

                getSupportFragmentManager().beginTransaction().replace(fragment_container, new dashboardFragment()).commit();

                break;

            case R.id.map:

               // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new mapsFragment()).commit();

                break;

            case R.id.profile:

                // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new profileFragment()).commit();

                break;

            case R.id.share:

                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show( );

                break;


            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));

                break;
        }

        drawer. closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)){

            drawer.closeDrawer(GravityCompat.START);
        }  else {
            super.onBackPressed( );

        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

}
