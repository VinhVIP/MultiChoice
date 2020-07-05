package com.vinh.multichoice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    TextView txtInfo;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        settingToolbar();

        txtInfo = findViewById(R.id.txt_info);
        String htmlContent = "<b>Tên ứng dụng:</b> <font color=\"#212121\">Trắc nghiệm ITMC</font> <br /><br />"
                + "<b> Chức năng:</b> <br />"
                + " - Luyện tập đề online do CLB ITMC tổng hợp và biên tập <br />"
                + " - Thêm câu hỏi trắc nghiệm theo chủ đề, câu hỏi có định dạng Latex + hình ảnh <br />"
                + " - Luyện tập theo chủ đề <br />"
                + " - Kiểm tra tổng hợp nhiều chủ đề <br />"
                + " - Chia sẻ chủ đề cho bạn bè dưới dạng tập tin zip  <br /><br />"
                + "<b>Liên hệ:</b> <br />"
                + " - Ứng dụng được phát triển bỏi Ban Lập Trình - Câu lạc bộ ITMC <br />"
                + " - Nếu có thắc mắc, góp ý vui lòng liên hệ: <br />"
                + "     + Email: vinhvipit@gmail.com <br />"
                + "     + Fb: /tran.kuanvih ";
        txtInfo.setText(Html.fromHtml(htmlContent));
    }

    private void settingToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
