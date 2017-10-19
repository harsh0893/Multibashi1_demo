package com.harsh.sainih.multibashi1.model;

/**
 * Created by sainih on 10/18/2017.
 */

import android.util.Log;

import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;


public class LessonRepository {
    private static Retrofit retrofit = null;
    private LessonDao mLessonDao;
    private LessonData lessonData;
    private Executor mExecutor;
    private LessonRepositoryListener mListener;
    private String BASE_URL = "http://www.akshaycrt2k.com/";

    public LessonRepository(LessonRepositoryListener lessonRepositoryListener, LessonDao lessonDao, Executor executor) {
        mListener = lessonRepositoryListener;
        mLessonDao = lessonDao;
        mExecutor = executor;
    }

    public void getData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        final LessonApiService lessonApiService = retrofit.create(LessonApiService.class);
        Call<LessonData> call = lessonApiService.getLessons();
        call.enqueue(new Callback<LessonData>() {
            @Override
            public void onResponse(Call<LessonData> call, Response<LessonData> response) {
                mListener.lessonsReady(response.body());
                LessonData lessonData = response.body();
                SaveData runnable = new SaveData(lessonData.getLessonList());
                mExecutor.execute(runnable);
            }

            @Override
            public void onFailure(Call<LessonData> call, final Throwable throwable) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        List<Lesson> lessonList = mLessonDao.getAllLessonItems();
                        if (lessonList == null)
                            mListener.lessonsReady(new LessonData(throwable));
                        else
                            mListener.lessonsReady(new LessonData(lessonList));
                    }
                };
                mExecutor.execute(runnable);
                Log.e(TAG, throwable.toString());
            }
        });
    }


    public interface LessonRepositoryListener {
        void lessonsReady(LessonData lessonData);
    }

    class SaveData implements Runnable {
        List<Lesson> lessonList;

        SaveData(List<Lesson> lessonList) {
            this.lessonList = lessonList;
        }

        @Override
        public void run() {
            mLessonDao.addLessons(lessonList);
        }
    }


}
