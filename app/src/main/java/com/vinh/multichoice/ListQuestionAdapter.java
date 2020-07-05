package com.vinh.multichoice;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ListQuestionAdapter extends RecyclerView.Adapter<ListQuestionAdapter.MyViewHolder> {

    public List<Integer> list;
    private Context context;
    private FormTestActivity activity;


    public ListQuestionAdapter(FormTestActivity activity, List<Integer> list, Context context) {
        this.activity = activity;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.col_question, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final int number = list.get(position);
        myViewHolder.txtQuesNumber.setText(number + "");

        if (activity.isTesting) {
            // dang kiểm tra
            if (activity.getQuestionIndex() == number - 1) {
                myViewHolder.txtQuesNumber.setBackground(context.getResources().getDrawable(R.drawable.item_question_selected));
                myViewHolder.txtQuesNumber.setTextColor(Color.WHITE);
            } else {
                myViewHolder.txtQuesNumber.setBackground(context.getResources().getDrawable(R.drawable.item_question_default));

                myViewHolder.txtQuesNumber.setTextColor(Color.BLACK);
            }
        } else {
            // kết quả
            if (activity.getAnswer(position) == activity.getCorrectAnswer(position)) {
                myViewHolder.txtQuesNumber.setBackground(context.getResources().getDrawable(R.drawable.item_question_correct));
                myViewHolder.txtQuesNumber.setTextColor(Color.WHITE);
            } else {
                myViewHolder.txtQuesNumber.setBackground(context.getResources().getDrawable(R.drawable.item_question_incorrect));
                myViewHolder.txtQuesNumber.setTextColor(Color.WHITE);
            }
        }

        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {

                } else {
                    if (number - 1 != activity.getQuestionIndex()) {
                        activity.changeQuestion(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView txtQuesNumber;

        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuesNumber = itemView.findViewById(R.id.txt_ques_number);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener listener) {
            itemClickListener = listener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }
}
