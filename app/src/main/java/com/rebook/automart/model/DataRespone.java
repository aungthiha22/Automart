package com.rebook.automart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataRespone {
    @SerializedName("orders")
    @Expose
    private Orders orders;

    @Expose
    private List<Orders> testOrders = new ArrayList<Orders>();


    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

}
