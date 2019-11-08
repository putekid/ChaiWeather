package com.example.chaiweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chaiweather.gson.WeatherItem;
import com.example.chaiweather.util.WeatherImageUtil;

import java.util.List;
import java.util.Objects;

public class WeatherItemAdapter extends RecyclerView.Adapter<WeatherItemAdapter.ViewHolder> {
    private List<WeatherItem> mWeatherItemList;
    private Context mContext;

    public WeatherItemAdapter(List<WeatherItem> mWeatherItemList) {
        this.mWeatherItemList = mWeatherItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(Objects.isNull(mContext))
            mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        WeatherItem weatherItem = mWeatherItemList.get(position);
        viewHolder.cityNameText.setText(weatherItem.getCityName());
        viewHolder.cityWeatherText.setText(weatherItem.getCityWeather());
        viewHolder.cityTemText.setText(weatherItem.getCityTem());
        int selectImageId = WeatherImageUtil.getImageIdByWeather(weatherItem.getCityWeather());
        Glide.with(mContext).load(selectImageId).into(viewHolder.cityWeatherImage);
    }

    @Override
    public int getItemCount() {
        return mWeatherItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView cityWeatherImage;
        private TextView cityNameText;
        private TextView cityWeatherText;
        private TextView cityTemText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            cityWeatherImage = itemView.findViewById(R.id.city_weather_img);
            cityNameText = itemView.findViewById(R.id.city_name);
            cityWeatherImage = itemView.findViewById(R.id.city_weather);
            cityTemText = itemView.findViewById(R.id.city_tem);
        }
    }
}
