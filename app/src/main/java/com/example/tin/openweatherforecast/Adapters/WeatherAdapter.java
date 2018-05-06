package com.example.tin.openweatherforecast.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tin.openweatherforecast.R;
import com.example.tin.openweatherforecast.models.Weather;
import com.example.tin.openweatherforecast.utilities.NetworkListener;

import java.util.List;

/**
 * Created by Tin on 06/05/2018.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private final List<Weather> mWeather;
    private final Context context;

    public WeatherAdapter(List<Weather> mWeather, Context context) {
        this.mWeather = mWeather;
        this.context = context;
    }

    /*
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        // Create a new View and inflate the list_item Layout into it
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.weather_list_item, viewGroup, false);

        // Return the View we just created
        return new ViewHolder(v);
    }

    /*
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param viewHolder   The ViewHolder which should be updated to represent the
     *                 contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder viewHolder, int position) {

        Weather weather = mWeather.get(position);

        viewHolder.tvDate.setText(weather.getCalculateDateTime());
        viewHolder.tvDescription.setText(weather.getWeatherDescription());
        viewHolder.tvTemp.setText(String.valueOf(weather.getTempCurrent()));

    }

    /*
     * Returns the number of items in the listItems List
     */
    @Override
    public int getItemCount() {
        return mWeather.size();
    }

    /*
     * This is the ViewHolder Class, it represents the rows in the RecyclerView (i.e every row is a ViewHolder)
     * In this example each row is made up of an ImageView
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDate;
        final TextView tvDescription;
        final TextView tvTemp;


        public ViewHolder(View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tV_date);
            tvDescription = itemView.findViewById(R.id.tV_description);
            tvTemp = itemView.findViewById(R.id.tV_temp);
        }
    }
}
