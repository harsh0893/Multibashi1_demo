package com.harsh.sainih.multibashi1.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.harsh.sainih.multibashi1.model.AppDatabase;
import com.harsh.sainih.multibashi1.model.LessonData;
import com.harsh.sainih.multibashi1.model.LessonRepository;
import com.harsh.sainih.multibashi1.utils.LevenshteinDistance;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Created by sainih on 10/18/2017.
 */

public class LessonPresenter implements LessonRepository.LessonRepositoryListener {
    LessonPresenterListener lessonPresenterListener;
    AppDatabase appDatabase;
    ApiDataSaveExecutor executor = new ApiDataSaveExecutor();
    LessonRepository lessonRepository;
    LevenshteinDistance distance;

    /**
     * Interface which the view needs to implement So
     * Presenter can communicate back with it
     */

    public interface LessonPresenterListener {
        void lessonsReady(LessonData lessonData);
        void openDialogBox(int percentage);
    }


    public LessonPresenter(LessonPresenterListener lessonPresenterListener, Context applicationContext) {

        this.lessonPresenterListener = lessonPresenterListener;
        this.appDatabase = AppDatabase.getDatabase(applicationContext);
        lessonRepository = new LessonRepository(this, appDatabase.lessonDao(), executor);

    }


    @Override
    public void lessonsReady(LessonData lessonData) {
        lessonPresenterListener.lessonsReady(lessonData);

    }

    public void loadLessons() {
        lessonRepository.getData();
    }

    /**
     * Calculates the accuracy of the pronunciation
     * @param matches
     * @param pronunciation
     */

    public void getCorrectPercentage(ArrayList<String> matches, String pronunciation) {
        distance = new LevenshteinDistance();
        int percentage;
        int editDistance = 999;
        int editDistance1;
        for (int i = 0; i < matches.size(); i++) {
            editDistance1 = distance.LD(pronunciation, matches.get(i).toLowerCase());
            if (editDistance >= editDistance1)
                editDistance = editDistance1;

        }
        if (editDistance < pronunciation.length()) {
            percentage = (100 - (editDistance * 100) / pronunciation.length());
            lessonPresenterListener.openDialogBox(percentage);
        } else {
            percentage = -1;
            lessonPresenterListener.openDialogBox(percentage);
        }


    }

    public class ApiDataSaveExecutor implements Executor {

        @Override
        public synchronized void execute(@NonNull Runnable runnable) {
            new Thread(runnable).start();
        }
    }


}
