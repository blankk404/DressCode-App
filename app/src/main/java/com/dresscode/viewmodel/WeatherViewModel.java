package com.dresscode.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dresscode.ai.WeatherOutfitAdvisorClient;
import com.dresscode.data.model.WeatherInfo;
import com.dresscode.data.repository.WeatherRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherViewModel extends AndroidViewModel {

    private final WeatherRepository repo = new WeatherRepository();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<WeatherInfo> weather = new MutableLiveData<>();
    private final MutableLiveData<String> advice = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public WeatherViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<WeatherInfo> getWeather() {
        return weather;
    }

    public LiveData<String> getAdvice() {
        return advice;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void loadByLatLon(double lat, double lon, String city) {
        loading.setValue(true);
        repo.fetchCurrent(lat, lon, city, new WeatherRepository.OnWeatherResult() {
            @Override
            public void onSuccess(WeatherInfo info) {
                weather.postValue(info);
                generateAdvice(info);
            }

            @Override
            public void onFailed(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }

    public void geocodeAndLoad(String cityName) {
        loading.setValue(true);
        repo.geocodeCity(cityName, new WeatherRepository.OnGeoResult() {
            @Override
            public void onSuccess(String name, double lat, double lon) {
                loadByLatLon(lat, lon, name);
            }

            @Override
            public void onFailed(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }

    private void generateAdvice(WeatherInfo info) {
        advice.postValue(null);
        executor.execute(() -> {
            try {
                String text = new WeatherOutfitAdvisorClient().suggest(info);
                advice.postValue(text);
            } catch (Exception e) {
                advice.postValue("建议生成失败，可稍后重试");
            } finally {
                loading.postValue(false);
            }
        });
    }
}


