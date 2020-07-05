package com.vinh.multichoice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TestTypeAdapter extends RecyclerView.Adapter<TestTypeAdapter.MyViewHolder> {

    public List<Type> list;
    private Context context;
    private DeclareTest activity;

    public TestTypeAdapter(Context context, List<Type> list, DeclareTest activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_type_select, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int pos) {
        final Type type = list.get(pos);
        myViewHolder.tvName.setText(type.getName());
        myViewHolder.tvNum.setText(type.getSelect() + "/" + type.getSize());

        myViewHolder.imgSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.setSelect(Math.max(0, type.getSelect()-1));
                list.set(pos, type);
                notifyItemChanged(pos);
            }
        });

        myViewHolder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.setSelect(Math.min(type.getSize(), type.getSelect()+1));
                list.set(pos, type);
                notifyItemChanged(pos);
            }
        });

        myViewHolder.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.removeTypeFromList(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvName, tvNum;
        private ImageView imgCancel, imgSub, imgAdd;

        private ItemClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name_type);
            imgCancel = itemView.findViewById(R.id.imgCancel);
            tvNum = itemView.findViewById(R.id.tv_num_ques_per_type);
            imgSub = itemView.findViewById(R.id.img_nav_before);
            imgAdd = itemView.findViewById(R.id.img_nav_next);

//            imgCancel.setOnClickListener(this);
//            imgAdd.setOnClickListener(this);
//            imgSub.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {

        }


        @Override
        public boolean onLongClick(View view) {
            listener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

}
