package ngo.patrick.weather.adapter;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import ngo.patrick.weather.R;
import ngo.patrick.weather.model.CityConditionsResponse;

/**
 * CityGridAdapter: Custom ListAdapter to bind the city weather data to each item in the GridView
 */
public class CityGridAdapter extends ArrayAdapter<CityConditionsResponse>
{

    public CityGridAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public CityGridAdapter(Context context, int resource, List<CityConditionsResponse> items)
    {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.city_grid_item, null);
        }

        //Get city data from specified position
        CityConditionsResponse p = getItem(position);

        //Find appropriate layout components and display respective data
        if (p != null)
        {
            TextView mCityText = (TextView) v.findViewById(R.id.city_text);
            TextView mWeatherText = (TextView) v.findViewById(R.id.weather_text);
            TextView mTemperatureText = (TextView) v.findViewById(R.id.temperature_text);
            ImageView mWeatherImage = (ImageView) v.findViewById(R.id.weather_image);

            //mock text
            String cityName = p.getCurrentObservation().getDisplayLocation().getCity();
            String weather = p.getCurrentObservation().getWeather();
            Long temperatureC = Math.round(p.getCurrentObservation().getTempC());
            Long temperatureF = Math.round(p.getCurrentObservation().getTempF());

            mCityText.setText(cityName);
            mWeatherText.setText(weather);
            mTemperatureText.setText(temperatureC + getContext().getString(R.string.temperature_celcius) + " / " + temperatureF + getContext().getString(R.string.temperature_farenheight));

            mWeatherImage.setImageDrawable(getWeatherImage(weather));
        }

        return v;
    }


    public Drawable getWeatherImage(String weather)
    {
        Context c = getContext();

        if (weather.contains(c.getString(R.string.weather_clear))) {
            return c.getResources().getDrawable(R.drawable.sun_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_overcast))){
            return c.getResources().getDrawable(R.drawable.very_cloudy_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_fog))){
            return c.getResources().getDrawable(R.drawable.fog_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_hail))){
            return c.getResources().getDrawable(R.drawable.hail_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_partly_cloudy))){
            return c.getResources().getDrawable(R.drawable.cloud_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_mostly_cloudy))){
            return c.getResources().getDrawable(R.drawable.cloudy_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_partial_fog))){
            return c.getResources().getDrawable(R.drawable.fog_cloudy_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_snow))){
            return c.getResources().getDrawable(R.drawable.snow_1x);
        }
        else if (weather.contains(c.getString(R.string.weather_rain))){
            return c.getResources().getDrawable(R.drawable.rain_1x);
        }
        else {
            return c.getResources().getDrawable(R.drawable.sun_1x);
        }
    }
}