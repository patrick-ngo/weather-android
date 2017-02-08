
package ngo.patrick.weather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityConditionsResponse {

    @SerializedName("response")
    @Expose
    private Response response;
    @SerializedName("current_observation")
    @Expose
    private CityConditionsResult currentObservation;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public CityConditionsResult getCurrentObservation() {
        return currentObservation;
    }

    public void setCurrentObservation(CityConditionsResult currentObservation) {
        this.currentObservation = currentObservation;
    }

}
