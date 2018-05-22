package com.example.tin.openweatherforecast;

public interface MainContract {

    interface MainScreen {

        /**
         * Used to navigate the user to another Activity/Fragment, but this app only has one
         * Activity, so it's never used.
         */

    }

    interface MainPresenter {

        void refreshButtonClick();

        void updateButtonClick();
    }

}
