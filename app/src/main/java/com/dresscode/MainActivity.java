package com.dresscode;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.dresscode.databinding.ActivityMainBinding;
import com.dresscode.fragments.ClosetFragment;
import com.dresscode.fragments.CommunityFragment;
import com.dresscode.fragments.HomeFragment;
import com.dresscode.fragments.InspirationFragment;
import com.dresscode.fragments.VirtualTryOnFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private ClosetFragment closetFragment;
    private VirtualTryOnFragment virtualTryOnFragment;
    private InspirationFragment inspirationFragment;
    private CommunityFragment communityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化Fragment实例
        homeFragment = new HomeFragment();
        closetFragment = new ClosetFragment();
        virtualTryOnFragment = new VirtualTryOnFragment();
        inspirationFragment = new InspirationFragment();
        communityFragment = new CommunityFragment();

        setupBottomNavigation();
        loadFragment(homeFragment);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    loadFragment(homeFragment);
                    return true;
                } else if (itemId == R.id.nav_closet) {
                    loadFragment(closetFragment);
                    return true;
                } else if (itemId == R.id.nav_tryon) {
                    loadFragment(virtualTryOnFragment);
                    return true;
                } else if (itemId == R.id.nav_inspiration) {
                    loadFragment(inspirationFragment);
                    return true;
                } else if (itemId == R.id.nav_community) {
                    loadFragment(communityFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
