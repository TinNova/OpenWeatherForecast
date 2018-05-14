package com.example.tin.openweatherforecast.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.tin.openweatherforecast.utilities.NetworkUtils;

public class Weather implements Parcelable {

    private final int unixDateTime;
    private final String calculateDateTime;
    private final double tempCurrent;
    private final double tempMin;
    private final double tempMax;
    private final String weatherTitle;
    private final String weatherDescription;
    private final int weatherIcon;
    private final double windSpeed;
    private final double windDegree;

    public Weather(int unixDateTime, String calculateDateTime, double tempCurrent, double tempMin,
                   double tempMax, String weatherTitle, String weatherDescription, int weatherIcon,
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

    private Weather(Parcel in) {
        unixDateTime = in.readInt();
        calculateDateTime = in.readString();
        tempCurrent = in.readDouble();
        tempMin = in.readDouble();
        tempMax = in.readDouble();
        weatherTitle = in.readString();
        weatherDescription = in.readString();
        weatherIcon = in.readInt();
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
        parcel.writeInt(weatherIcon);
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

    public int getWeatherIcon() {

        return weatherIcon;
    }

    public double getWindSpeed() {

        return windSpeed;
    }

    public double getWindDegree() {

        return windDegree;
    }
}
