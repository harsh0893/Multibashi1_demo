package com.harsh.sainih.multibashi1.model;



import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by sainih on 10/18/2017.
 */

public interface LessonApiService {
    @GET("getLessonData.php")
    Call<LessonData> getLessons();
}
