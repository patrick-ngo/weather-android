package ngo.patrick.weather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 2/6/2017.
 */

public class CityList {

    @SerializedName("RESULTS")
    @Expose
    private List<CityListResult> results = new ArrayList<CityListResult>();

    /**
     *
     * @return
     *     The results
     */
    public List<CityListResult> getResults() {
        return results;
    }

    /**
     *
     * @param results
     *     The results
     */
    public void setResults(List<CityListResult> results) {
        this.results = results;
    }
}
