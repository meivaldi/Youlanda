package com.meivaldi.youlanda.data.network;

import com.meivaldi.youlanda.data.database.karyawan.Karyawan;
import com.meivaldi.youlanda.data.database.order.Order;
import com.meivaldi.youlanda.data.database.product.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {

    @POST("/android/get_product.php")
    @FormUrlEncoded
    Call<List<Product>> getAllBreads(@Field("unit") String unit);

    @POST("/android/store_product.php")
    @FormUrlEncoded
    Call<Product> saveProduct(@Field("stok") int stok, @Field("nama") String nama);

    @POST("/android/store_transaction.php")
    @FormUrlEncoded
    Call<Order> saveOrder(@Field("id") int id, @Field("nik_kasir") String kasir,
                          @Field("nik_pelayan") String pelayan, @Field("waktu") String waktu,
                          @Field("jenis") String jenis, @Field("sub_total") int total,
                          @Field("diskon_produk") int diskon, @Field("diskon_spesial") int diskon_spesial,
                          @Field("pajak") int pajak, @Field("total_tagihan") int tagihan,
                          @Field("uang_tunai") int cash);

    @POST("/android/get_karyawan.php")
    Call<List<Karyawan>> getAllKaryawan();

    @GET("/android/get_order_number.php")
    Call<OrderNumber> getOrderNumber();

    @POST("android/upload_base64.php")
    @FormUrlEncoded
    Call<ResponseApi> uploadImage(@Field("image") String image, @Field("name") String name, @Field("nik") String nik);

}
