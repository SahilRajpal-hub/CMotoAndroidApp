package com.example.cmotoemployee;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;

public class NavigationActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(2131492899);
        setSupportActionBar((Toolbar)findViewById(2131296759));
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(2131296449);
        NavigationView navigationView = (NavigationView)findViewById(2131296593);
        this.mAppBarConfiguration = (new AppBarConfiguration.Builder(new int[] { 2131296589, 2131296588, 2131296592 })).setDrawerLayout(drawerLayout).build();
        NavController navController = Navigation.findNavController((Activity)this, 2131296590);
        NavigationUI.setupActionBarWithNavController(this, navController, this.mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(2131558403, paramMenu);
        return true;
    }

    public boolean onSupportNavigateUp() {
        return (NavigationUI.navigateUp(Navigation.findNavController((Activity)this, 2131296590), this.mAppBarConfiguration) || super.onSupportNavigateUp());
    }
}

