package ngo.patrick.weather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Patrick on 2/6/2017.
 */

public class CityCurrentWeather {

    //Used to pass ID of blog post through Intent parameter
    public static String INTENT_ID = "blogpost_id";

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    /**
     *
     * @return
     *     The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     *
     * @param caption
     *     The caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     *
     * @return
     *     The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     *     The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @return
     *     The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     *
     * @param thumbnail
     *     The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     *
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     *     The createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     *     The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     *     The updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
