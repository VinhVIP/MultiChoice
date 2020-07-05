package com.vinh.multichoice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListTypeAdapter extends RecyclerView.Adapter<ListTypeAdapter.MyViewHolder> {

    public List<Type> listType;
    private Context context;
    private ListTypeActivity activity;

    public ListTypeAdapter(Context context, List<Type> listType, ListTypeActivity activity) {
        this.context = context;
        this.listType = listType;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_type, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int pos) {
        final Type type = listType.get(pos);
        myViewHolder.tvType.setText(type.getName());
        myViewHolder.tvSize.setText(type.getSize() + " câu hỏi");

        myViewHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.MODE == Helper.MODE_EDIT) {
                    // download this type
                    activity.checkWriteExternalStorage(type.getId());
                }
            }
        });

        myViewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.MODE == Helper.MODE_EDIT) {
                    moveToDialogConfirmDeleteType(type.getId(), listType.indexOf(type));
                }
            }
        });
        if (Helper.MODE != Helper.MODE_EDIT) {
//            myViewHolder.imgDelete.setVisibility(View.INVISIBLE);
//            myViewHolder.imgDownload.setVisibility(View.INVISIBLE);
            ViewAnimation.showOut(myViewHolder.imgDelete);
            ViewAnimation.showOut(myViewHolder.imgDownload);
        } else {
            ViewAnimation.showIn(myViewHolder.imgDelete);
            ViewAnimation.showIn(myViewHolder.imgDownload);
        }

        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    if (Helper.MODE == Helper.MODE_EDIT) moveToDialogChangeTypeName(type.getId());
                } else {
                    if (Helper.MODE == Helper.MODE_EDIT) viewQuestionsInType(type.getId());
                    else if (Helper.MODE == Helper.MODE_PRACTICE) practiceOneType(type.getId());
                }
            }
        });
    }

    private void moveToDialogChangeTypeName(int typeID) {
        activity.showDialogChangeTypeName(typeID);
    }

    private void moveToDialogConfirmDeleteType(int typeID, int position) {
        activity.dialogConfirmDeleteType(typeID, position);
    }

    private void viewQuestionsInType(int typeID) {
        activity.viewAllQuestionsInType(typeID);
    }

    private void practiceOneType(int typeID) {
        activity.practiceOneType(typeID);
    }

    @Override
    public int getItemCount() {
        return listType.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tvType, tvSize;
        private ImageView imgDelete, imgDownload;

        private ItemClickListener listener;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.type_name);
            tvSize = itemView.findViewById(R.id.type_size);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgDownload = itemView.findViewById(R.id.img_download);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }
}
