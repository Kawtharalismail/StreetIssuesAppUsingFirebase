package com.barmej.streetissues;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.barmej.streetissues.Model.Issues;
import com.bumptech.glide.Glide;

public class IssuesDetailsActivity extends AppCompatActivity {

    public static final String ISSUES_DATA ="issues_data";
    private ImageView issuesPhoto;
    private TextView  issuesName;
    private TextView  issuesDescription;
    private TextView  issuesDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);
        issuesPhoto=findViewById(R.id.imageview_issuesPhoto);
        issuesName=findViewById(R.id.textview_issuesName);
        issuesDescription=findViewById(R.id.textview_issuesDate);
        issuesDate=findViewById(R.id.textview_issuesDate);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Issues issues=getIntent().getExtras().getParcelable(ISSUES_DATA);
            if(issues!=null){
                getSupportActionBar().setTitle(issues.getName());
                Glide.with(issuesPhoto).load(issues.getPhoto()).into(issuesPhoto);
                issuesName.setText(issues.getName());
                issuesDescription.setText(issues.getDescription());
                issuesDate.setText(issues.getFormattedDate());
            }
        }
    }
}
