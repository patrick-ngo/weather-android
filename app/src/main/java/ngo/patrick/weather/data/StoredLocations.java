package ngo.patrick.weather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ngo.patrick.weather.model.CityConditionsResponse;
import ngo.patrick.weather.model.CityListResult;

/**
 * StoredLocations: Singleton class to manage the stored data (cities) using Gson to store to the SharedPrefs
 */

public class StoredLocations {
    private static StoredLocations mInstance = null;

    private Context context;
    private ArrayList<CityListResult> storedLocationsData = new ArrayList<CityListResult>();


    public static final String MY_PREFS_NAME = "MyPrefsFile";

    private StoredLocations(){ }

    public static StoredLocations getInstance(Context context){
        if(mInstance == null)
        {
            mInstance = new StoredLocations();
            mInstance.context = context;
            mInstance.retrieveLocationsData();
        }
        return mInstance;
    }

    public void retrieveLocationsData()
    {
        String savedLocations = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString("SAVED_LOCATIONS_DATA", null);
        if (savedLocations != null)
        {

            // How to retrieve your Java object back from the string
            Gson gson = new Gson();

            Type type = new TypeToken<List<CityListResult>>() {}.getType();

            storedLocationsData = gson.fromJson(savedLocations, type);
        }
    }


    public ArrayList<CityListResult> getLocationsData()
    {
        return storedLocationsData;
    }

    public void addLocationData(CityListResult location)
    {
        storedLocationsData.add(0, location);

        //store array as json
        Gson gson = new Gson();
        String json = gson.toJson(storedLocationsData);
        context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit().putString("SAVED_LOCATIONS_DATA", json).commit();

        Log.d("ADDING", json);
    }

    public void removeLocationAtIndex(Integer index)
    {
        CityListResult itemToMove = storedLocationsData.get(index);
        storedLocationsData.remove(itemToMove);

        //store array as json
        Gson gson = new Gson();
        String json = gson.toJson(storedLocationsData);
        context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit().putString("SAVED_LOCATIONS_DATA", json).commit();
    }

    public void moveLocationToBeginning(Integer index)
    {
        if (index == 0) {
            return;
        }

        CityListResult itemToMove = storedLocationsData.get(index);
        storedLocationsData.remove(itemToMove);
        storedLocationsData.add(0, itemToMove);

        //store array as json
        Gson gson = new Gson();
        String json = gson.toJson(storedLocationsData);
        context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit().putString("SAVED_LOCATIONS_DATA", json).commit();
    }
}