package com.barmej.streetissues.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.streetissues.Model.Issues;
import com.barmej.streetissues.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class IssuesListAdapter  extends RecyclerView.Adapter<IssuesListAdapter.IssuesViewHolder>{

    public interface OnIssuesClickListener{

        void onIssuesClick(Issues issues);
    }

    private List<Issues> mIssuesList;
    private OnIssuesClickListener onIssuesClickListener;

    public IssuesListAdapter(List<Issues> mIssuesList,OnIssuesClickListener mOnIssuesClickListener) {
        this.mIssuesList = mIssuesList;
        this.onIssuesClickListener=mOnIssuesClickListener;
    }


    @NonNull
    @Override
    public IssuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue,parent,false);
        return new IssuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuesViewHolder holder, int position) {
        holder.bind(mIssuesList.get(position));
    }


    @Override
    public int getItemCount() {
        return mIssuesList.size();
    }


    public class IssuesViewHolder extends RecyclerView.ViewHolder {

        TextView issuesTittleTextView;
        ImageView issuesViewImageView;
        Issues issues;

        public IssuesViewHolder(@NonNull View itemView) {
            super(itemView);
            issuesTittleTextView= itemView.findViewById(R.id.issuesTittleTextView);
            issuesViewImageView= itemView.findViewById(R.id.issuesViewImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onIssuesClickListener.onIssuesClick(issues);
                }
            });
        }
        public void bind(Issues issues){
            this.issues=issues;
            issuesTittleTextView.setText(issues.name);
            Glide.with(issuesViewImageView).load(issues.getPhoto()).into(issuesViewImageView);
        }
    }
}


