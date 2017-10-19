package com.harsh.sainih.multibashi1.view;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harsh.sainih.multibashi1.R;
import com.harsh.sainih.multibashi1.model.Lesson;
import com.harsh.sainih.multibashi1.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LEARN_TYPE = "learn";
    private List<Lesson> lessonArrayList;
    private Context context;
    private View.OnClickListener onClickListener;

    public RecyclerViewAdapter(List<Lesson> lessonArrayList, Context context, View.OnClickListener onClickListener) {
        this.lessonArrayList = lessonArrayList;
        this.context = context;
        this.onClickListener = onClickListener;

    }

    /**
     * created the views based on the view types(learn or question)
     *
     * @param parent
     * @param viewType
     * @return
     */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item, parent, false));
            case 1:
                return new RecyclerViewHolder_1(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_1, parent, false));
            default:
                throw new RuntimeException("ViewType Not Implemented: " + viewType);
        }

    }

    /**
     * binds the data to the View being held by ViewHolders based on the viewtype
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Lesson lesson = lessonArrayList.get(position);
        switch (holder.getItemViewType()) {

            case 0:
                RecyclerViewHolder holder0 = (RecyclerViewHolder) holder;
                holder0.play_button.setTag(lesson);
                holder0.inEnglish.setText(lesson.getConceptName());
                holder0.inOther.setText(lesson.getTargetScript());
                holder0.inOtherEnglish.setText(lesson.getPronunciation());
                holder0.play_button.setOnClickListener(onClickListener);
                DownloadAudiosAsyncTask audiosAsyncTask = new DownloadAudiosAsyncTask();
                audiosAsyncTask.execute(lesson.getAudio_url());
                break;
            case 1:
                RecyclerViewHolder_1 holder1 = (RecyclerViewHolder_1) holder;
                holder1.play_button.setTag(lesson);
                holder1.inEnglish.setText(lesson.getConceptName());
                holder1.inOther.setText(lesson.getTargetScript());
                holder1.inOtherEnglish.setText(lesson.getPronunciation());
                holder1.play_button.setOnClickListener(onClickListener);
                holder1.record_audio.setOnClickListener(onClickListener);
                holder1.record_audio.setTag(lesson);
                DownloadAudiosAsyncTask audiosAsyncTask1 = new DownloadAudiosAsyncTask();
                audiosAsyncTask1.execute(lesson.getAudio_url());
                break;
            default:
                throw new RuntimeException("ViewType not implemented" + holder.getItemViewType());
        }

    }

    /**
     * gets the type of the view
     *
     * @param position
     * @return
     */

    @Override
    public int getItemViewType(int position) {
        if (lessonArrayList.get(position).getType().equals(LEARN_TYPE))
            return 0;
        else
            return 1;
    }

    /**
     * gets the item count
     *
     * @return the size of the list to be displayed
     */
    @Override
    public int getItemCount() {
        if (lessonArrayList != null) return lessonArrayList.size();
        else return 0;
    }

    public void addItems(List<Lesson> lessonArrayList) {
        this.lessonArrayList = lessonArrayList;
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView inEnglish;
        TextView inOther;
        TextView inOtherEnglish;
        ImageView play_button;

        RecyclerViewHolder(View view) {
            super(view);
            inEnglish = view.findViewById(R.id.tv_inEnglish);
            inOther = view.findViewById(R.id.tv_secondlang);
            inOtherEnglish = view.findViewById(R.id.tv_readable);
            play_button = view.findViewById(R.id.play_button);
        }
    }

    static class RecyclerViewHolder_1 extends RecyclerView.ViewHolder {
        TextView inEnglish;
        TextView inOther;
        TextView inOtherEnglish;
        ImageView play_button;
        ImageView record_audio;


        public RecyclerViewHolder_1(View view) {
            super(view);
            inEnglish = view.findViewById(R.id.tv_inEnglish);
            inOther = view.findViewById(R.id.tv_secondlang);
            inOtherEnglish = view.findViewById(R.id.tv_readable);
            play_button = view.findViewById(R.id.play_button);
            record_audio = view.findViewById(R.id.record_audio);
        }
    }

    class DownloadAudiosAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Boolean status = NetworkUtils.downloadAudioFromHttpUrl(strings[0], context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
