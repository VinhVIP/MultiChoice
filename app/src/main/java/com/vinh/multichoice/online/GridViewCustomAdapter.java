package com.vinh.multichoice.online;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinh.multichoice.Helper;
import com.vinh.multichoice.R;

import java.util.List;

public class GridViewCustomAdapter extends BaseAdapter {

    private List<Subject> subjectList;
    private LayoutInflater inflater;
    private Context context;
    private MenuActivity activity;

    public GridViewCustomAdapter(Context context, List<Subject> subjectList, MenuActivity activity) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.subjectList = subjectList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return subjectList.size();
    }

    @Override
    public Object getItem(int i) {
        return subjectList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return subjectList.get(i).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Subject subject = subjectList.get(position);
        ViewHolder viewHolder = new ViewHolder();

        if (view == null) {
            view = inflater.inflate(R.layout.item, viewGroup, false);
        }

        viewHolder.txtName = view.findViewById(R.id.txt_name_subject);
        viewHolder.txtName.setText(subject.getName());
        viewHolder.imgIcon = view.findViewById(R.id.img_icon_subject);
        if(subject.getIconPath() != null && subject.getIconPath().length() > 0){
            viewHolder.imgIcon.setImageBitmap(Helper.getImageBitmapOnInternalStorage(context, subject.getIconPath()));
        }else{
//            viewHolder.imgIcon.setImageDrawable(context.getDrawable(R.drawable.icon));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, subject.getName() + " : " + subject.getId(), Toast.LENGTH_SHORT).show();
                activity.getListExamFromSubject(subject.getId());
            }
        });
        return view;
    }

    public class ViewHolder {
        ImageView imgIcon;
        TextView txtName;
    }
}

