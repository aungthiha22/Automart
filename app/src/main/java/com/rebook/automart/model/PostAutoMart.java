package com.rebook.automart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class PostAutoMart {

  @SerializedName("id") @Expose
  private String id = "";
  @SerializedName("category_id") @Expose
  private String categotyId = "";
  @SerializedName("sub_category_id")@Expose
  private String subCategoryId="";
  @SerializedName("brand_id") @Expose
  private String brandId = "";
  @SerializedName("shops")@Expose
  private String shop;

  @SerializedName("vehicles") @Expose
  private String vehicle = "";
  @SerializedName("name") @Expose
  private String name = "";
  @SerializedName("price") @Expose
  private Integer price = 0;
  @SerializedName("image1") @Expose
  private String image1;
  @SerializedName("image2") @Expose
  private String image2 = "";

  @SerializedName("description") @Expose
  private String description;
  /*@SerializedName("products") @Expose
  private String products = "";*/
  @SerializedName("created_at") @Expose
  private String createdAt = "";
  @SerializedName("updated_at") @Expose
  private String updatedAt = "";
  @SerializedName("deleted_at") @Expose
  private String deletedAt = "";

  @SerializedName("quantity")
  @Expose
  private Integer quantity;
  @SerializedName("promotion")
  @Expose
  private Integer promotion;

  public Integer getPromotion() {
    return promotion;
  }

  public void setPromotion(Integer promotion) {
    this.promotion = promotion;
  }

  @Expose
  private List<Product> product = new ArrayList<Product>();

  public List<Product> getProduct() {
    return product;
  }

  public void setProduct(List<Product> product) {
    this.product = product;
  }

  public String getId() {
    return id;
  }

  public void setId(String postId) {
    this.id = postId;
  }

  public String getCategotyId() {
    return categotyId;
  }

  public void setCategotyId(String categotyId) {
    this.categotyId = categotyId;
  }

  public String getSubCategoryId() {
    return subCategoryId;
  }

  public void setSubCategoryId(String subCategoryId) {
    this.subCategoryId = subCategoryId;
  }

  public String getBrandId() {
    return brandId;
  }

  public void setBrandId(String brandId) {
    this.brandId = brandId;
  }

  public String getShop() {
    return shop;
  }

  public void setShop(String shop) {
    this.shop = shop;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public String getImage1() {
    return image1;
  }

  public void setImage1(String image1) {
    this.image1 = image1;
  }

  public String getImage2() {
    return image2;
  }

  public void setImage2(String image2) {
    this.image2 = image2;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /*public String getProducts() {
    return products;
  }

  public void setProducts(String products) {
    this.products = products;
  }*/

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(String deletedAt) {
    this.deletedAt = deletedAt;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}
