package com.example.weatherappgfg.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherappgfg.Models.WeatherRvModel;
import com.example.weatherappgfg.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private Context context ;
    private ArrayList<WeatherRvModel> weatherRvModelArrayList ;

    public WeatherAdapter( Context context , ArrayList<WeatherRvModel> weatherRvModelArrayList){
        this.context = context ;
        this.weatherRvModelArrayList = weatherRvModelArrayList ;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.weather_rv_item , parent , false) ;
        return new WeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
         WeatherRvModel weatherRvModel = weatherRvModelArrayList.get(position) ;

         Picasso.get().load("https:".concat(weatherRvModel.getIcon())).into(holder.rvWeatherCondition) ;
         holder.rvTemp.setText(weatherRvModel.getTemperature() + "°c") ;
         holder.rvWindSpeed.setText(weatherRvModel.getWindSpeed() + "km/hr");

         //time
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm") ;
        SimpleDateFormat output = new SimpleDateFormat("hh:mm") ;

        try{
            Date t = input.parse(weatherRvModel.getTime()) ;
            holder.rvTime.setText(output.format(t));
        }catch(ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        private TextView rvTime , rvTemp , rvWindSpeed ;
        private ImageView rvWeatherCondition ;
        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            rvTime = itemView.findViewById(R.id.rvTime) ;
            rvTemp = itemView.findViewById(R.id.rvTemp) ;
            rvWindSpeed = itemView.findViewById(R.id.rvWindSpeed) ;
            rvWeatherCondition = itemView.findViewById(R.id.rvWeatherCondition) ;
        }
    }
}
