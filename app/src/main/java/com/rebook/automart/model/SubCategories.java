package com.rebook.automart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by Dell on 1/22/2019.
 */

@Generated("org.jsonschema2pojo")
public class SubCategories {

    @SerializedName("category_id") @Expose
    private String categoriesId = "";

    @SerializedName("name") @Expose
    private String name = "";

    @SerializedName("description") @Expose
    private String description = "";

    @SerializedName("created_at") @Expose
    private String created_at = "";

    @SerializedName("updated_at") @Expose
    private String updated_at = "";

    @SerializedName("deleted_at") @Expose
    private String deleted_at = "";


}
