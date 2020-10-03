package com.barmej.streetissues.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barmej.streetissues.Adapters.IssuesListAdapter;
import com.barmej.streetissues.IssuesDetailsActivity;
import com.barmej.streetissues.Model.Issues;
import com.barmej.streetissues.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IssuesListFragment extends Fragment implements IssuesListAdapter.OnIssuesClickListener {

    private RecyclerView  mRecyclerView;
    private IssuesListAdapter mIssuesListAdapter;
    private ArrayList<Issues> mIssues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_issues_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=view.findViewById(R.id.issuesListRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Issues").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"start",Toast.LENGTH_LONG).show();
                    mIssues=new ArrayList<>();
                    for(QueryDocumentSnapshot document:task.getResult()){
                        mIssues.add(document.toObject(Issues.class));
                        Toast.makeText(getContext(),"hi"+ document.toObject(Issues.class).getName(),Toast.LENGTH_LONG).show();

                    }
                    mIssuesListAdapter=new IssuesListAdapter(mIssues,IssuesListFragment.this::onIssuesClick);
                    mRecyclerView.setAdapter(mIssuesListAdapter);
                }else{
                    Toast.makeText(getContext(),"problemmmmm",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    public void onIssuesClick(Issues issues) {
        Intent intent=new Intent(getContext(), IssuesDetailsActivity.class);
        intent.putExtra(IssuesDetailsActivity.ISSUES_DATA,issues);
        startActivity(intent);
    }
}