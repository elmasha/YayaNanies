package com.intech.yayananies.Interface;



import com.intech.yayananies.Models.ResponseStk;
import com.intech.yayananies.Models.Result;
import com.intech.yayananies.Models.ResultStk;
import com.intech.yayananies.Models.StkQuery;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/stk")
    Call<ResponseStk> stk_push(@Body Map<String,Object> pushStk);

    @POST("/stk_callback")
    Call<ResultStk>  getResponse();

    @GET("/")
    Call<Result>  getResult();

    @POST("/stk/query")
    Call<StkQuery>   stk_Query  (@Body Map<String ,String> stkQuey);


}
