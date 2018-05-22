package com.example.tin.openweatherforecast;

/**
 * Responsible for handling actions from MainScreen/MainActivity and updating the UI as required
 */
public class MainPresenter implements MainContract.MainPresenter {
    private MainContract.MainScreen mainScreen;

    MainPresenter(MainContract.MainScreen screen) {
        mainScreen = screen;
    }

    @Override
    public void refreshButtonClick() {

    }

    @Override
    public void updateButtonClick() {

    }
}
