package com.harsh.sainih.multibashi1.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harsh.sainih.multibashi1.R;
import com.harsh.sainih.multibashi1.model.Lesson;
import com.harsh.sainih.multibashi1.model.LessonData;
import com.harsh.sainih.multibashi1.presenter.LessonPresenter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LessonPresenter.LessonPresenterListener, View.OnClickListener {
    private static final int NO_DATA_RETURNED = -1;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    LessonPresenter lessonPresenter;
    private MediaPlayer mMediaPlayer;

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };
    AudioManager am;
    static String pronunciation;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lessonPresenter = new LessonPresenter(this, getApplicationContext());
        lessonPresenter.loadLessons();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    mMediaPlayer.start();
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    releaseMediaPlayer();
                    // Stop playback
                }
            }
        };

        Toolbar mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        mytoolbar.setTitle("MultiBashi");
        mytoolbar.inflateMenu(R.menu.menu);
        mytoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_refresh) {
                    recyclerViewAdapter.addItems(null);
                    lessonPresenter.loadLessons();
                    return true;
                }
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Lesson>(), this, this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    /** called once the data is loaded either from the API if connection available or SQLite if
     * internet not available
     *
     * @param lessonData the Data passed by the Presenter to the View
     */

    @Override
    public void lessonsReady(LessonData lessonData) {
        if (lessonData.getThrowable() == null)
            recyclerViewAdapter.addItems(lessonData.getLessonList());
        else
            openDialogBox(NO_DATA_RETURNED);

    }


    @Override
    public void onClick(View view) {
        Lesson lesson = (Lesson) view.getTag();
        if (view.getId() == R.id.play_button) {
            String url = lesson.getAudio_url();
            String fileName = Uri.parse(url).getLastPathSegment();
            Uri uri = Uri.parse("file://" + getFilesDir() + "/" + fileName);
            releaseMediaPlayer();
            // Request audio focus for playback
            int result = am.requestAudioFocus(afChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mMediaPlayer = MediaPlayer.create(this, uri);
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(onCompletionListener);
                // Start playback.
            }
        } else if (view.getId() == R.id.record_audio) {
            startVoiceRecognitionActivity(lesson.getPronunciation().toLowerCase());
        }

    }

    /**
     * starts the activity which converts speech into text to be compared
     * with the pronunciation
     * @param pronunciation
     */

    public void startVoiceRecognitionActivity(String pronunciation) {
        this.pronunciation = pronunciation;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**Is responsible for releasing media player,
     * when not required
     *
     */

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();
            am.abandonAudioFocus(afChangeListener);

            mMediaPlayer = null;
        }
    }

    /**
     * Calls a method of presenter to take care of the Activity's result
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches == null) openDialogBox(NO_DATA_RETURNED);
            else
                lessonPresenter.getCorrectPercentage(matches, pronunciation);


        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    /**
     * creates a dialog box to display the accuracy of the pronounciation
     * @param percentage
     */

    @Override
    public void openDialogBox(int percentage) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final CardView cardView = new CardView(this);
        final TextView textView = new TextView(this);
        final ImageView imageView = new ImageView(this);

        cardView.setCardElevation(4);
        cardView.setMinimumHeight(600);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorAccentLight));
        cardView.setPadding(8, 8, 8, 8);
        textView.setPadding(8, 20, 8, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(R.dimen.dialog_box_text_size);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
        }
        CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL);
        textView.setLayoutParams(layoutParams);


        CardView.LayoutParams layoutParams1 = new CardView.LayoutParams(250,
                250,
                Gravity.CENTER);
        imageView.setLayoutParams(layoutParams1);

        if (isBetween(percentage, 0, 24)) {
            textView.setText("Try Again! Listen to the pronunciation!");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.happy));
        } else if (isBetween(percentage, 25, 49)) {
            textView.setText("Can do better! Let's try again?");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.happy));
        } else if (isBetween(percentage, 51, 74)) {
            textView.setText("Good job! Can be perfect");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.happy));
        } else if (isBetween(percentage, 75, 100)) {
            textView.setText("Perfect! Move on to the next lesson");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.happy));
        } else {
            textView.setText("Error! Check your connection or try pronouncing clearly");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.sad));
        }
        cardView.addView(textView);
        cardView.addView(imageView);

        alert.setView(cardView);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();

    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }


}
