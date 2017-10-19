package com.harsh.sainih.multibashi1.model;



import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sainih on 10/18/2017.
 */
@Entity(primaryKeys = {"type", "conceptName", "targetScript"})
public class Lesson {

    @SerializedName("type")
    public String type;

    @SerializedName("conceptName")
    private String conceptName;

    @SerializedName("pronunciation")
    private String pronunciation;

    @SerializedName("targetScript")
    private String targetScript;

    @SerializedName("audio_url")
    private String audio_url;


    public Lesson(String type, String conceptName, String pronunciation, String targetScript, String audio_url) {
        this.type = type;
        this.conceptName = conceptName;
        this.pronunciation = pronunciation;
        this.targetScript = targetScript;
        this.audio_url = audio_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getTargetScript() {
        return targetScript;
    }

    public String getAudio_url() {
        return audio_url;
    }

    @Override
    public String toString() {
        return type.toString() + audio_url.toString();
    }
}
