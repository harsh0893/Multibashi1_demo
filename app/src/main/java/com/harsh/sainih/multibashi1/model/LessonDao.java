package com.harsh.sainih.multibashi1.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by sainih on 10/19/2017.
 */

@Dao
public interface LessonDao {

    @Query("select * from Lesson")
    List<Lesson> getAllLessonItems();

    @Insert(onConflict = REPLACE)
    void addLessons(List<Lesson> lessonList);

    @Delete
    void deleteLesson(Lesson lesson);


}
