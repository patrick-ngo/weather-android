package ngo.patrick.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ngo.patrick.weather.R;
import ngo.patrick.weather.model.CityListResult;

import static java.security.AccessController.getContext;

/**
 * CityListAdapter: Custom ListAdapter to bind the city list data to each item in the listview
 */

public class CityListAdapter extends ArrayAdapter<CityListResult>
{

    public CityListAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public CityListAdapter(Context context, int resource, List<CityListResult> items)
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
            v = vi.inflate(R.layout.city_name_list_item, null);
        }

        //Get city data from specified position
        CityListResult p = getItem(position);

        //Find appropriate layout components and display respective data
        if (p != null)
        {
            TextView cityNameTextView = (TextView) v.findViewById(R.id.city_list_item_textview);

            cityNameTextView.setText(p.getName());
        }

        return v;
    }
}