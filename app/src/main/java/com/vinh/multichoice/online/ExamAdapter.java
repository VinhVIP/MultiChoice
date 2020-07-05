package com.vinh.multichoice.online;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinh.multichoice.R;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.MyViewHolder> {

    private ExamListActivity activity;
    private List<Exam> examList;
    private Context context;

    public ExamAdapter(ExamListActivity activity, List<Exam> examList, Context context) {
        this.activity = activity;
        this.examList = examList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_exam, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final Exam exam = examList.get(position);
        myViewHolder.txtExamName.setText(exam.getName());
        myViewHolder.txtDetails.setText(exam.getAmount() + " câu - " + exam.getTime() + " phút");

        myViewHolder.imgDownload.setVisibility(exam.isDownloaded()?View.VISIBLE:View.INVISIBLE);

        myViewHolder.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                activity.downloadExam(exam);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtExamName, txtDetails;
        ImageView imgDownload;
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtExamName = itemView.findViewById(R.id.txt_name_exam);
            txtDetails = itemView.findViewById(R.id.txt_details);
            imgDownload = itemView.findViewById(R.id.img_download);
            view = itemView;
        }
    }
}
