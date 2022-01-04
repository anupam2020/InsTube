package com.sbdev.insta_youtube_video_downloader;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageView img,more,youtube;
    private CircleImageView insta;

    private NavigationView navigationView;

    private ClipboardManager clipboardManager;

    private SharedPreferences sp;

    private String SHARED_PREFS="SHARED_PREFS";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.WHITE);

        drawerLayout=findViewById(R.id.drawerLayout);
        img=findViewById(R.id.mainImg);
        more=findViewById(R.id.mainMore);
        navigationView=findViewById(R.id.navView);
        youtube=findViewById(R.id.mainYoutube);
        insta=findViewById(R.id.mainInsta);

        clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        sp=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        if(sp.getInt("key",0)==0)
        {

            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Welcome");
            builder.setMessage("Please copy the link first and then choose InsTube!");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    SharedPreferences.Editor editor=sp.edit();
                    editor.putInt("key",1);
                    editor.apply();

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    SharedPreferences.Editor editor=sp.edit();
                    editor.putInt("key",1);
                    editor.apply();
                }
            });

            builder.show();

        }

        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new HomeFragment()).commit();
        this.onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));


//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String type = intent.getType();
//        if ("android.intent.action.SEND".equals(action) && type != null && "text/plain".equals(type)) {
//            Log.d("Copied Text",intent.getStringExtra("android.intent.extra.TEXT"));
//
//            ClipData clipData=ClipData.newPlainText("label",intent.getStringExtra("android.intent.extra.TEXT"));
//            clipboardManager.setPrimaryClip(clipData);
//            DynamicToast.make(MainActivity.this, "Link successfully copied... Just paste it!", getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24),
//                    getResources().getColor(R.color.white), getResources().getColor(R.color.black), 2000).show();
//
//        }


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(MainActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId())
                        {

                            case R.id.exit:
                                finishAffinity();
                                break;

                            case R.id.rateUs:
                                openAppInGooglePlay();
                                break;

                        }

                        return true;
                    }
                });

                popupMenu.show();

            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openLinks("https://www.youtube.com");

            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openLinks("https://www.instagram.com");

            }
        });

    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {

            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Exit");
            builder.setMessage("Do you really want to exit?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finishAffinity();

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });

            builder.show();

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new HomeFragment()).commit();
                break;

            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new SearchFragment()).commit();
                break;

            case R.id.share:
                shareApp();
                break;

            case R.id.feedback:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new FeedbackFragment()).commit();
                break;

            case R.id.update:
                openAppInGooglePlay();
                break;

            case R.id.more:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new MoreFragment()).commit();
                break;

        }

        item.setCheckable(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp()
    {

        try {
            String appPackageName = getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch(Exception e) {
            DynamicToast.makeError(this,e.getMessage(),2000).show();
        }

    }

    public void openAppInGooglePlay() {
        String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) { // if there is no Google Play on device
            DynamicToast.makeError(this,e.getMessage(),2000).show();
        }
    }


    public void openLinks(String url)
    {

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            DynamicToast.makeError(MainActivity.this, ex.getMessage(),2000).show();
        }

    }

}