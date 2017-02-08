package ngo.patrick.weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ngo.patrick.weather.adapter.CityGridAdapter;
import ngo.patrick.weather.api.WeatherApi;
import ngo.patrick.weather.data.StoredLocations;
import ngo.patrick.weather.model.CityConditionsResponse;
import ngo.patrick.weather.model.CityListResult;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static Integer DAY_TIME_START = 7;
    private static Integer NIGHT_TIME_START = 19;
    private static Integer REFRESH_TIMER = 60000;
    private static Integer MAX_NUM_CITIES = 4;

    RelativeLayout mMainView;
    RelativeLayout mSelectedCity;
    TextView mCityText;
    TextView mWeatherText;
    TextView mTemperatureText;
    ImageView mWeatherImage;
    Button mAddCityButton;

    private CityGridAdapter cityGridAdapter;
    private ArrayList<CityConditionsResponse> cityConditionsResponses = new ArrayList<>();
    private ArrayList<CityListResult> storedLocations = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get stored locations
        storedLocations = StoredLocations.getInstance(getApplicationContext()).getLocationsData();

        mMainView = (RelativeLayout) findViewById(R.id.activity_main);
        mSelectedCity = (RelativeLayout) findViewById(R.id.selected_city);
        mAddCityButton = (Button) findViewById(R.id.add_button);
        mCityText = (TextView) findViewById(R.id.city_text);
        mWeatherText = (TextView) findViewById(R.id.weather_text);
        mTemperatureText = (TextView) findViewById(R.id.temperature_text);
        mWeatherImage = (ImageView) findViewById(R.id.weather_image);


        mAddCityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (storedLocations.size() >= MAX_NUM_CITIES)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.max_cities), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Fire an Intent to launch the New Post Activity
                    Intent newPostIntent = new Intent(MainActivity.this, AddCityActivity.class);
                    startActivity(newPostIntent);
                }
            }
        });

        mSelectedCity.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                // set dialog message
                alertDialogBuilder
                        .setTitle(getString(R.string.remove_location))
                        .setMessage(mCityText.getText())
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                //remove city
                                StoredLocations.getInstance(getApplicationContext()).removeLocationAtIndex(0);
                                onUpdateUI();
                            }
                        })
                        .setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                //remove dialog
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                return true;
            }
        });


        //GridView and adapter
        cityGridAdapter = new CityGridAdapter(this, R.layout.city_grid_item);
        GridView gridview = (GridView) findViewById(R.id.gridview_cities);
        gridview.setAdapter(cityGridAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //adjust the index by 1, because index 0 is not part of the gridview
                int index = position + 1;
                StoredLocations.getInstance(getApplicationContext()).moveLocationToBeginning(index);

                onUpdateUI();
            }
        });





        //Declare the timer
        Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {
           @Override
           public void run()
           {
               Log.d("WEATHER", "Pull weather data");
               getWeatherData();
           }

         },REFRESH_TIMER,REFRESH_TIMER);
        //Set how long before to start calling the TimerTask (in milliseconds
        //Set the amount of time between each execution (3 seconds)


    }


    @Override
    protected void onResume()
    {
        super.onResume();

        if (storedLocations.size() > 0)
        {
            onUpdateUI();
            getWeatherData();
        }
        //if no locations saved, launch activity to select location
        else
        {
            Intent newPostIntent = new Intent(MainActivity.this, AddCityActivity.class);
            startActivity(newPostIntent);
        }
    }

    public void onUpdateUI()
    {
        cityGridAdapter.clear();

        for (int i = 0; i < storedLocations.size(); i++)
        {
            for (int j = 0; j < cityConditionsResponses.size(); j++)
            {
                CityConditionsResponse result = cityConditionsResponses.get(j);

                if (storedLocations.get(i).getName().contains(result.getCurrentObservation().getDisplayLocation().getCity()))
                {
                    //main selected city
                    if (i == 0)
                    {
                        String cityName = result.getCurrentObservation().getDisplayLocation().getFull();
                        String weather = result.getCurrentObservation().getWeather();
                        Long temperatureC = Math.round(result.getCurrentObservation().getTempC());
                        Long temperatureF = Math.round(result.getCurrentObservation().getTempF());

                        mCityText.setText(cityName);
                        mWeatherText.setText(weather);
                        mTemperatureText.setText(temperatureC + getString(R.string.temperature_celcius) + " / " + temperatureF + getString(R.string.temperature_farenheight));
                        mWeatherImage.setImageDrawable(getWeatherImage(weather));

                        //string pattern "EEE, dd MMM yyyy HH:mm:ss Z"
                        String rfcDate = result.getCurrentObservation().getLocalTimeRfc822();
                        String hourString = rfcDate.substring(17, 19);
                        Integer hour = Integer.parseInt(hourString);

                        //day time
                        if ((hour >= DAY_TIME_START) && (hour < NIGHT_TIME_START)) {
                            mMainView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }
                        //night time
                        else {
                            mMainView.setBackgroundColor(getResources().getColor(R.color.colorNight));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = getWindow();
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(getResources().getColor(R.color.colorNight));
                            }
                        }
                    }
                    //city grid view
                    else
                    {
                        cityGridAdapter.add(result);
                    }
                }
            }
        }
    }

    public Drawable getWeatherImage(String weather)
    {
        if (weather.contains(getString(R.string.weather_clear))) {
            return getResources().getDrawable(R.drawable.sun_2x);
        }
        else if (weather.contains(getString(R.string.weather_overcast))){
            return getResources().getDrawable(R.drawable.very_cloudy_2x);
        }
        else if (weather.contains(getString(R.string.weather_fog))){
            return getResources().getDrawable(R.drawable.fog_2x);
        }
        else if (weather.contains(getString(R.string.weather_hail))){
            return getResources().getDrawable(R.drawable.hail_2x);
        }
        else if (weather.contains(getString(R.string.weather_partly_cloudy))){
            return getResources().getDrawable(R.drawable.cloud_2x);
        }
        else if (weather.contains(getString(R.string.weather_mostly_cloudy))){
            return getResources().getDrawable(R.drawable.cloudy_2x);
        }
        else if (weather.contains(getString(R.string.weather_partial_fog))){
            return getResources().getDrawable(R.drawable.fog_cloudy_2x);
        }
        else if (weather.contains(getString(R.string.weather_snow))){
            return getResources().getDrawable(R.drawable.snow_2x);
        }
        else if (weather.contains(getString(R.string.weather_rain))){
            return getResources().getDrawable(R.drawable.rain_2x);
        }
        else {
            return getResources().getDrawable(R.drawable.sun_2x);
        }
    }


    public void getWeatherData()
    {

        cityConditionsResponses.clear();

        for (int i = 0; i < storedLocations.size(); i++) {

            //Get initial data to display weather details
            WeatherApi weatherService = WeatherApi.conditions.create(WeatherApi.class);
            final Call<CityConditionsResponse> call = weatherService.getCityConditions(storedLocations.get(i).getZmw());  //first item

            FetchCurrentWeatherTask currentWeatherTask = new FetchCurrentWeatherTask(MainActivity.this, findViewById(android.R.id.content));
            currentWeatherTask.execute(call);
        }
    }





    public class FetchCurrentWeatherTask extends AsyncTask<Call, Void, CityConditionsResponse> {
        private Context mContext;
        private View mRootView;
        private final String LOG_TAG = FetchCurrentWeatherTask.class.getSimpleName();

        public FetchCurrentWeatherTask(Context context, View rootView){
            mContext = context;
            mRootView = rootView;
        }

        /**
         * Retrieve single weather data by Http Request
         * Retrieval done on separate thread to avoid cluttering main UI thread
         */
        @Override
        protected CityConditionsResponse doInBackground(Call ... params)
        {
            try
            {
                Call<CityConditionsResponse> call = params[0];
                Response<CityConditionsResponse> response = call.execute();

                return response.body();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Once weather data is retrieved,
         * Display to view
         */
        @Override
        protected void onPostExecute(CityConditionsResponse result)
        {
            if (result != null && (storedLocations.size() > 0))
            {
                if (result.getCurrentObservation() != null)
                {
                    cityConditionsResponses.add(result);

                    //if all results received, can update UI
                    if (cityConditionsResponses.size() >= storedLocations.size())
                    {
                        onUpdateUI();
                    }
                }
            }
        }

    }
}
