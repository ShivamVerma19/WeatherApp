package com.example.weatherappgfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherappgfg.Adapter.WeatherAdapter;
import com.example.weatherappgfg.Models.WeatherRvModel;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout ;
    private TextView tvCityName , tvTemp , tvCondition ;
    private TextInputEditText TIETCityName ;
    private ImageView ivBack , ivSearch , ivWeatherCondition ;
    private RecyclerView recyclerView ;
    private ProgressBar progressBar ;

    private LocationManager locationManager ;
    private int PERMISSION_CODE = 1 ;
    ArrayList<WeatherRvModel> arrayList ;
    WeatherAdapter weatherAdapter ;
    String cityName , prevCityName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        relativeLayout = findViewById(R.id.idRLHome) ;
        tvCityName = findViewById(R.id.tvCityName) ;
        tvCondition = findViewById(R.id.tvCondition) ;
        tvTemp = findViewById(R.id.tvTemp) ;
        TIETCityName = findViewById(R.id.TIETCityName) ;
        ivBack = findViewById(R.id.ivBack) ;
        ivSearch = findViewById(R.id.ivSearch) ;
        ivWeatherCondition = findViewById(R.id.ivWeatherCondition) ;
        recyclerView = findViewById(R.id.recyclerView) ;
        progressBar = findViewById(R.id.progressBar) ;

        progressBar.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>() ;
        weatherAdapter = new WeatherAdapter(this ,arrayList) ;

        recyclerView.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE) ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Source:
            https:
//www.holadevs.com/pregunta/87883/problems-with-access-fine-location-permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            }

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ;


        cityName = getCityName(location.getLatitude() , location.getLongitude()) ;



        getWeatherInfo(cityName);
        Log.d("TAG" , "Code working 1") ;

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = TIETCityName.getText().toString() ;
                if(city.length() == 0){
                    Toast.makeText(MainActivity.this , "City Name is Empty" , Toast.LENGTH_SHORT).show() ;

                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);

                    Log.d("CitySearchName" , city) ;
                    tvCityName.setText(city);
                    getWeatherInfo(city);
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this , "Permission Granted..." , Toast.LENGTH_SHORT).show() ;
            }
            else{
                Toast.makeText(this , "Permission Denied..." , Toast.LENGTH_SHORT).show() ;
                finish() ;
            }
        }
    }

    private String getCityName(double lat , double lon){
        String cityName = "Not found" ;

        Geocoder geocoder = new Geocoder(getBaseContext() , Locale.getDefault()) ;

        try{
            List<Address> addresses = geocoder.getFromLocation(lat , lon , 10) ;

            for(Address adr : addresses){
                String city = adr.getLocality() ;
                if(city != null && !city.equals("") ){
                    cityName = city ;
                    Log.d("CityName" , city) ;
                    break ;
                }
                else{
                    Log.d("TAG" , "Could not get the location") ;
                    Toast.makeText(this , "Could not get the location" , Toast.LENGTH_SHORT).show() ;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return cityName ;
    }


    private void getWeatherInfo(String CityName){
        Log.d("TAGX" , CityName) ;
        tvCityName.setText(CityName);
        String url = "https://api.weatherapi.com/v1/forecast.json?key=3a8d71bf861848d2902192557222408&q=" + CityName +
                "&days=1&aqi=yes&alerts=yes" ;



        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this) ;
        Log.d("TAG" , "Checking1") ;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        prevCityName = CityName ;
                        Log.e("TAG" , "Checking1") ;
                        progressBar.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);

                        arrayList.clear();

                        try {
                            String temperature = response.getJSONObject("current").getString("temp_c") ;
                            int is_day = response.getJSONObject("current").getInt("is_day") ;
                            String weatherCondition = response.getJSONObject("current").getJSONObject("condition").getString("text") ;
                            String weatherConditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon") ;

                            tvTemp.setText(temperature + "°c") ;
                            tvCondition.setText(weatherCondition);
                            Picasso.get().load("https:".concat(weatherConditionIcon)).into(ivWeatherCondition) ;


                            if(is_day == 1){
                                //morning
                                ivBack.setImageResource(R.drawable.morning);
                            }
                            else{
                                //night
                                ivBack.setImageResource(R.drawable.night);
                            }



                            JSONObject forecastObject = response.getJSONObject("forecast") ;
                            JSONObject forecastO = forecastObject.getJSONArray("forecastday").getJSONObject(0) ;
                            JSONArray hoursArray = forecastO.getJSONArray("hour") ;

                            for(int i = 0 ; i < hoursArray.length() ; i++){
                                JSONObject hoursObject = hoursArray.getJSONObject(i) ;
                                String time = hoursObject.getString("time") ;
                                String temp = hoursObject.getString("temp_c") ;
                                String windSpeed = hoursObject.getString("wind_kph") ;
                                String icon = hoursObject.getJSONObject("condition").getString("icon") ;

                                WeatherRvModel weatherRvModel = new WeatherRvModel(time , temp , icon , windSpeed) ;
                                arrayList.add(weatherRvModel) ;
                            }

                            weatherAdapter.notifyDataSetChanged();
                            Log.d("TAG" , "Code working 4") ;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } ,new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY ERROR" , error.toString()) ;
                Toast.makeText(MainActivity.this , "Enter a valid city name" , Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                tvCityName.setText(prevCityName) ;
            }
        });

        requestQueue.add(jsonObjectRequest) ;
    }
}