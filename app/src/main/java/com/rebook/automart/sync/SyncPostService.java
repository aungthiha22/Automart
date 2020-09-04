package com.rebook.automart.sync;

import com.rebook.automart.model.DataRespone;
import com.rebook.automart.model.Post;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rebook.automart.model.Product;

import org.json.JSONArray;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by YDN on 7/3/2017.
 */
public interface SyncPostService {
    String id = null;

     @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/product/latest")void getProductLatest(@Query("page") int page,
                                                 Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/product/bestseller")void getProductBestSeller(@Query("page") int page,
                                                         Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/categories")void getCategories(@Query("page") int page,
                                          Callback<JsonArray> jsonArrayCallback);


    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @Multipart @POST("/social_login") void logInSocial(@Part("provider") String provider,
                                                       @Part("token") String tokenId, Callback<JsonObject> response);



    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/subcategories/{id}")void getSubCategory(@Path("id") String id, @Query("page") int page,
                                                   Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/brand_lists/{year}")void getBrand(@Path("year") String year, @Query("page") int page,
                                             Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/model_lists/{year}/{brand_id}")
    void getModelList(@Path("year") String year, @Path("brand_id") String brand_id,
                      @Query("page") int page, Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/engine_lists/{year}/{brand_id}/{model_id}")
    void getEngineList(@Path("year") String year,
                       @Path("brand_id") String brand_id,
                       @Path("model_id") String model_id,
                       @Query("page") int page,
                       Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/chassi_code_lists/{year}/{brand_id}/{model_id}/{engine}")
    void getChassisCodeList(@Path("year") String year,
                            @Path("brand_id") String brand_id,
                            @Path("model_id") String model_id,
                            @Path("engine") String engine,
                            @Query("page") int page,
                            Callback<JsonArray> jsonArrayCallback);
//


//    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
//    @GET("/subcategories/{ArrayList<SubCategories>}")void getTesting(@Path("id") String id, @Query("page") int page,
//                                                                     Callback<JsonArray> jsonArrayCallback);
//  product_search
    //http://192.168.8.100/api/product_search?product=air&vehicle


    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/product_search")
    void getSearchProduct(@Query("product") String product, @Query("vehicle_name") String vehicle_name,
                          @Query("page") int page, Callback<JsonObject> jsonObjectCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/product_search")
    void getProductListCategory(@Query("product") String product,
                           @Query("page") int page, Callback<JsonObject> jsonObjectCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/slider")
    void getSlider(@Query("page") int page, Callback<JsonArray> jsonArrayCallback);


    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/product/detail/{id}")void getProductDetail(@Path("id") String id, @Query("page") int page,
                                                      Callback<JsonObject> jsonArrayCallback);

    //product_categories

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/product/lists/{id}")void getProductList(@Path("id") String id, @Query("page") int page,
                                                   Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/product_categories")void getSearchList(@Query("page") int page,
                                                  Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/blogs")void getBlogList(@Query("page") int page,
                                   Callback<JsonObject> jsonObjectCallback);
    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/blogs/detail/{id}")void getBlogDetail(@Path("id") String id, @Query("page") int page,
                                                 Callback<JsonObject> jsonObjectCallback);



    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @Multipart @POST("/contact") void getContact(@Part("name") String name,
                                                 @Part("email") String gmail,
                                                 @Part("phone_number") String phone_number,
                                                 @Part("message") String message,
                                                 Callback<JsonObject> response);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/orders/{token}/{flag}")void getUserOrderList(@Path("token") String token, @Path("flag") String flag, @Query("page") int page,
                                                        Callback<DataRespone> callback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @Multipart @POST("/payment_confirmation") void getPaymentConfirm(
            @Part("products") String data,
            @Part("token") String token,
            @Part("payment_method") String paymentMethod,
            @Part("total_amount") String totalAmount,
            @Part("promo-code") String promoAmt,
            @Part("shipping_address") String shippingAddress,
            @Part("shop_id")String userCreatedId,
            @Part("promo_amount")String promoAmount,
            @Part("phone_number")String phoneNumber,
            @Part("tax_amount")String taxAmount,
            @Part("net_amount")String netAmount,
            Callback<JsonObject> response);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @Multipart @POST("/authenticate") void getLogin(
            @Part("email") String email,
            @Part("password") String password,
            Callback<JsonObject> response);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @Multipart @POST("/signup") void getRegister(
            @Part("email") String email,
            @Part("password") String password,
            @Part("name") String name,
            @Part("address") String address,
            Callback<JsonObject> response);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/productbrand")void getBrandItem(Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/shops/{token}")void getShopSaleMan(@Path("token") String token, Callback<JsonObject> jsonObjectCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/promo-code/{token}/{promo}/{bank_transfer}")void checkPromo(@Path("token") String token,
                    @Path("promo") String promo,
                    @Path("bank_transfer") String bankTransfer,
                    Callback<JsonObject> callback);
    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @Multipart @POST("/order_cancel/{token}/{id}") void getOrderCancel(@Path("token")String token,
                                                          @Path("id") String id,
                                                 @Part("cancel_remark") String canelRemark,
                                                 @Part("cancel_info") String cancelInfo,
                                                 Callback<JsonObject> jsonObjectCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/promotion")void getPromotionList(@Query("page") int page,
                                   Callback<JsonObject> jsonObjectCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/advertisements")void getAdvertisement(Callback<JsonArray> jsonArrayCallback);

    @Headers({"X-Authorization:a01bd6c00469ee105a1b1ec86c0a456ba0ce95bb"})
    @GET("/advertisement")void getAdvertisementOne(Callback<JsonObject> jsonArrayCallback);



}
