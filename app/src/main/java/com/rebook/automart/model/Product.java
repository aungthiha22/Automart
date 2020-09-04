package com.rebook.automart.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/**
 * Created by Dell on 2/5/2019.
 */
@Generated("org.jsonschema2pojo")
public class Product {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("sub_category_id")
    @Expose
    private Integer subCategoryId;
    @SerializedName("brand_id")
    @Expose
    private Integer brandId;
    @SerializedName("shops")
    @Expose
    private String shops;
    @SerializedName("vehicles")
    @Expose
    private String vehicles;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("promotion")
    @Expose
    private Integer promotion;

    @SerializedName("product_url")
    @Expose
    private String pruductUrl;


    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("image1")
    @Expose
    private String image1;
    @SerializedName("image2")
    @Expose
    private String image2;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("products")
    @Expose
    private String products;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;

    @SerializedName("booking_ref")
    @Expose
    private String bookingRef;

    @SerializedName("total_amount")
    @Expose
    private String totalAmount;

    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;

    @Expose
    private List<ProductImage> productImage = new ArrayList<>();

    @SerializedName("review")
    @Expose
    private List<Review> review = new ArrayList<>();

    @SerializedName("rating")
    @Expose
    private Rating rating;

    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;

    @SerializedName("sku")
    @Expose
    private String sku;

    private int star;
    private String countStar;


    public boolean selected;
        private int orderQuantity;
        private String checkOrder;
        private int addTablePrice;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public Integer getSubCategoryId() {
            return subCategoryId;
        }

        public void setSubCategoryId(Integer subCategoryId) {
            this.subCategoryId = subCategoryId;
        }

        public Integer getBrandId() {
            return brandId;
        }

        public void setBrandId(Integer brandId) {
            this.brandId = brandId;
        }

        public String getShops() {
            return shops;
        }

        public void setShops(String shops) {
            this.shops = shops;
        }

        public String getVehicles() {
            return vehicles;
        }

        public void setVehicles(String vehicles) {
            this.vehicles = vehicles;
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

        public Integer getPromotion() {
            return promotion;
        }

        public void setPromotion(Integer promotion) {
            this.promotion = promotion;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
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

        public String getProducts() {
            return products;
        }

        public void setProducts(String products) {
            this.products = products;
        }

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

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ProductImage> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<ProductImage> productImage) {
        this.productImage = productImage;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getCheckOrder() {
        return checkOrder;
    }

    public void setCheckOrder(String checkOrder) {
        this.checkOrder = checkOrder;
    }

    public int getAddTablePrice() {
        return addTablePrice;
    }

    public void setAddTablePrice(int addTablePrice) {
        this.addTablePrice = addTablePrice;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getCountStar() {
        return countStar;
    }

    public void setCountStar(String countStar) {
        this.countStar = countStar;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPruductUrl() {
        return pruductUrl;
    }

    public void setPruductUrl(String pruductUrl) {
        this.pruductUrl = pruductUrl;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
