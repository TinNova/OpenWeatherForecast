package com.example.tin.openweatherforecast.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.tin.openweatherforecast.utilities.NetworkUtils;

public class Weather implements Parcelable {

    //TODO: Write out all of the code to save a data of Weather
    // Once completed and tested. We need to figure out how to only keep data that displays the
    // days data at 12:00:00 (Later once completed we can try figure out how to show the days
    // high and low data

    private int unixDateTime;
    private String calculateDateTime;
    private double tempCurrent;
    private double tempMin;
    private double tempMax;
    private String weatherTitle;
    private String weatherDescription;
    private String weatherIcon;
    private double windSpeed;
    private double windDegree;

    public Weather(int unixDateTime, String calculateDateTime, double tempCurrent, double tempMin,
                   double tempMax, String weatherTitle, String weatherDescription, String weatherIcon,
                   double windSpeed, double windDegree) {
        this.unixDateTime = unixDateTime;
        this.calculateDateTime = calculateDateTime;
        this.tempCurrent = tempCurrent;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.weatherTitle = weatherTitle;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;
        this.windSpeed = windSpeed;
        this.windDegree = windDegree;
    }

    protected Weather(Parcel in) {
        unixDateTime = in.readInt();
        calculateDateTime = in.readString();
        tempCurrent = in.readDouble();
        tempMin = in.readDouble();
        tempMax = in.readDouble();
        weatherTitle = in.readString();
        weatherDescription = in.readString();
        weatherIcon = in.readString();
        windSpeed = in.readDouble();
        windDegree = in.readDouble();
    }

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(unixDateTime);
        parcel.writeString(calculateDateTime);
        parcel.writeDouble(tempCurrent);
        parcel.writeDouble(tempMin);
        parcel.writeDouble(tempMax);
        parcel.writeString(weatherTitle);
        parcel.writeString(weatherDescription);
        parcel.writeString(weatherIcon);
        parcel.writeDouble(windSpeed);
        parcel.writeDouble(windDegree);
    }

    public int getUnixDateTime() {
        return unixDateTime;
    }

    public String getCalculateDateTime() {
        return calculateDateTime;
    }

    public double getTempCurrent() {
        return tempCurrent;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public String getWeatherTitle() {
        return weatherTitle;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getWeatherIcon() {
        return NetworkUtils.BASE_IMAGE_URL + weatherIcon + NetworkUtils.END_IMAGE_URL;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDegree() {
        return windDegree;
    }
}
