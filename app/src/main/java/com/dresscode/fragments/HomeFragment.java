package com.dresscode.fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationManager;
import android.location.Geocoder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.dresscode.ProfileActivity;
import com.dresscode.databinding.FragmentHomeBinding;
import com.dresscode.viewmodel.UserViewModel;
import com.dresscode.viewmodel.WeatherViewModel;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private UserViewModel userViewModel;
    private WeatherViewModel weatherViewModel;
    private int userId;

    private SharedPreferences weatherPrefs;
    private static final String WP = "weather_prefs";
    private static final String K_CITY = "city";
    private static final String K_LAT = "lat";
    private static final String K_LON = "lon";

    private final ActivityResultLauncher<String[]> locationPermLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if ((fine != null && fine) || (coarse != null && coarse)) {
                    loadByLocation();
                } else {
                    // 无权限时用上次缓存/默认城市
                    loadFromCacheOrDefault();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        SharedPreferences sp = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);
        weatherPrefs = requireActivity().getSharedPreferences(WP, MODE_PRIVATE);
        bindUserInfo();
        setupWeather();

        binding.ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        binding.ivNotification.setOnClickListener(v -> {
            // TODO: 打开通知页面
        });
    }

    private void setupWeather() {
        // 初始状态
        binding.tvWeatherCity.setText(getString(com.dresscode.R.string.weather_locating));
        binding.tvWeatherTemp.setText("--°C");
        binding.tvWeatherDesc.setText(getString(com.dresscode.R.string.weather_refreshing));
        // 天气卡片右下角保留“天气提示”，不被大模型建议覆盖
        binding.tvWeatherAdvice.setText(getString(com.dresscode.R.string.weather_suitable));

        // AI 穿搭建议卡片（独立展示）
        binding.tvOutfitAdviceText.setText(getString(com.dresscode.R.string.outfit_advice_loading));

        // 点击天气卡片：切换城市/回到定位
        binding.cardWeather.setOnClickListener(v -> showSwitchCityDialog());

        weatherViewModel.getWeather().observe(getViewLifecycleOwner(), info -> {
            if (info == null) return;
            binding.tvWeatherCity.setText(info.city + " · 今日天气");
            binding.tvWeatherTemp.setText(Math.round(info.temperatureC) + "°C");
            binding.tvWeatherDesc.setText(info.weatherText + " · 风 " + Math.round(info.windSpeedKmh) + "km/h");
            // 缓存
            weatherPrefs.edit()
                    .putString(K_CITY, info.city)
                    .putFloat(K_LAT, (float) info.latitude)
                    .putFloat(K_LON, (float) info.longitude)
                    .apply();
        });

        weatherViewModel.getAdvice().observe(getViewLifecycleOwner(), text -> {
            if (!TextUtils.isEmpty(text)) {
                binding.tvOutfitAdviceText.setText(text);
            }
        });

        weatherViewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (!TextUtils.isEmpty(msg)) {
                // 不打扰 UI，轻提示
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        // 优先使用缓存城市，否则定位
        loadFromCacheOrDefaultOrLocation();
    }

    private void loadFromCacheOrDefaultOrLocation() {
        float lat = weatherPrefs.getFloat(K_LAT, Float.NaN);
        float lon = weatherPrefs.getFloat(K_LON, Float.NaN);
        String city = weatherPrefs.getString(K_CITY, null);
        if (!Float.isNaN(lat) && !Float.isNaN(lon) && !TextUtils.isEmpty(city)) {
            weatherViewModel.loadByLatLon(lat, lon, city);
        } else {
            // 请求定位权限
            locationPermLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void loadFromCacheOrDefault() {
        float lat = weatherPrefs.getFloat(K_LAT, Float.NaN);
        float lon = weatherPrefs.getFloat(K_LON, Float.NaN);
        String city = weatherPrefs.getString(K_CITY, null);
        if (!Float.isNaN(lat) && !Float.isNaN(lon) && !TextUtils.isEmpty(city)) {
            weatherViewModel.loadByLatLon(lat, lon, city);
        } else {
            // 默认：上海（可按你需要改）
            weatherViewModel.geocodeAndLoad("上海");
        }
    }

    private void loadByLocation() {
        try {
            LocationManager lm = (LocationManager) requireContext().getSystemService(android.content.Context.LOCATION_SERVICE);
            Location loc = null;
            if (lm != null) {
                // 尝试 last known
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (loc == null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if (loc == null) {
                loadFromCacheOrDefault();
                return;
            }

            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            String city = reverseGeocodeCity(lat, lon);
            if (TextUtils.isEmpty(city)) city = "当前位置";
            weatherViewModel.loadByLatLon(lat, lon, city);
        } catch (SecurityException se) {
            loadFromCacheOrDefault();
        } catch (Exception e) {
            loadFromCacheOrDefault();
        }
    }

    private String reverseGeocodeCity(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(requireContext());
            java.util.List<android.location.Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && !list.isEmpty()) {
                android.location.Address a = list.get(0);
                String city = a.getLocality();
                if (TextUtils.isEmpty(city)) city = a.getSubAdminArea();
                if (TextUtils.isEmpty(city)) city = a.getAdminArea();
                return city;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void showSwitchCityDialog() {
        String[] items = {getString(com.dresscode.R.string.weather_switch_city), getString(com.dresscode.R.string.weather_use_location)};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("天气")
                .setItems(items, (d, which) -> {
                    if (which == 0) {
                        EditText et = new EditText(getContext());
                        et.setHint(getString(com.dresscode.R.string.weather_city_hint));
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle(getString(com.dresscode.R.string.weather_switch_city))
                                .setView(et)
                                .setPositiveButton("确定", (d2, w2) -> {
                                    String name = et.getText().toString().trim();
                                    if (!TextUtils.isEmpty(name)) {
                                        weatherViewModel.geocodeAndLoad(name);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {
                        locationPermLauncher.launch(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        });
                    }
                })
                .show();
    }

    private void bindUserInfo() {
        if (userId == -1) return;
        userViewModel.getUserById(userId).observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            // 首页问候语：早上好！+昵称
            String nickname = user.getNickname();
            if (TextUtils.isEmpty(nickname)) nickname = "你";
            binding.tvGreeting.setText("早上好，" + nickname + "！");

            // 顶部头像（DressCode 前）
            if (!TextUtils.isEmpty(user.getAvatar())) {
                try {
                    binding.ivHomeAvatar.setImageURI(Uri.parse(user.getAvatar()));
                    binding.ivHomeAvatar.setImageTintList(null); // 有真实头像时去掉tint
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}