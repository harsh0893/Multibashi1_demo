package com.harsh.sainih.multibashi1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sainih on 10/18/2017.
 */

public class LessonData {
    @SerializedName("lesson_data")
    private List<Lesson> lessonList;
    private Throwable throwable;

    public Throwable getThrowable() {
        return throwable;
    }

    public LessonData(List<Lesson> lessonList) {
        this.lessonList = lessonList;
    }

    public LessonData(Throwable throwable) {
        this.throwable = throwable;
    }

    public List<Lesson> getLessonList() {
        return lessonList;
    }


}
