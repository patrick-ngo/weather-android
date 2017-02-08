package ngo.patrick.weather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.IOException;

import ngo.patrick.weather.adapter.CityListAdapter;
import ngo.patrick.weather.api.WeatherApi;
import ngo.patrick.weather.data.StoredLocations;
import ngo.patrick.weather.model.CityList;
import ngo.patrick.weather.model.CityListResult;
import retrofit2.Call;
import retrofit2.Response;

public class AddCityActivity extends AppCompatActivity {

    Button mCloseButton;
    EditText mAddCityText;
    ListView mCityListView;

    private CityListAdapter cityListAdapter;

    FetchAllCitiesTask cityNameListTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);


        mCloseButton = (Button) findViewById(R.id.close_button);
        mAddCityText = (EditText) findViewById(R.id.add_city_text);
        mCityListView = (ListView) findViewById(R.id.city_listview);

        //Create an adapter and bind it to the listview in the layout
        cityListAdapter = new CityListAdapter(AddCityActivity.this, R.layout.city_name_list_item);

        mCityListView.setAdapter(cityListAdapter);

        //Create the click listener for the list items (clicking on name will add it to the disk and return to the main activity)
        mCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Add selected city to disk
                CityListResult selectedCity = cityListAdapter.getItem(position);
                StoredLocations.getInstance(getApplicationContext()).addLocationData(selectedCity);

                Toast.makeText(AddCityActivity.this, selectedCity.getName(), Toast.LENGTH_SHORT).show();

                //finish activity
                finish();
            }
        });



        mCloseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //close activity
                finish();
            }
        });



        mAddCityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //if a an autocomplete query has already started, cancel it
                if (cityNameListTask != null) {
                    cityNameListTask.cancel(true);
                }

                //if at least one letter, start an autocomplete query
                if (count > 0)
                {
                    WeatherApi weatherService = WeatherApi.autocomplete.create(WeatherApi.class);
                    final Call<CityList> call = weatherService.getAllCities(s.toString());
                    cityNameListTask = new FetchAllCitiesTask();
                    cityNameListTask.execute(call);
                }
                else
                {
                    cityListAdapter.clear();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }





    private class FetchAllCitiesTask extends AsyncTask<Call, Void, CityList>
    {

        private final String LOG_TAG = FetchAllCitiesTask.class.getSimpleName();
        /**
         * Retrieve citylist via HTTP
         * Retrieval done on separate thread to avoid cluttering main UI thread
         */
        @Override
        protected CityList doInBackground(Call... params)
        {
            try
            {
                Call<CityList> call = params[0];
                Response<CityList> response = call.execute();


                return response.body();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Once citylist data is retrieved,
         * Fill the List Adapter for display on the view
         */
        @Override
        protected void onPostExecute(CityList results)
        {
            if (results != null)
            {
                //fill the list adapter with cities
                cityListAdapter.clear();
                for (CityListResult singleCity : results.getResults())
                {
                    //exclude countries
                    String subString = singleCity.getL().substring(0,3);
                    if (subString.equals("/q/"))
                    {
                        cityListAdapter.add(singleCity);
                    }
                }
            }
        }

    }
}
