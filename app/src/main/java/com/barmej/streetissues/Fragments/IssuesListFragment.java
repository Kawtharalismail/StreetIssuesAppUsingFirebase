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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

        mIssues=new ArrayList<>();

        mIssuesListAdapter=new IssuesListAdapter(mIssues,IssuesListFragment.this::onIssuesClick);
        mRecyclerView.setAdapter(mIssuesListAdapter);

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Issues").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new
                EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e==null){
                            mIssues.clear();
                            for(QueryDocumentSnapshot document:queryDocumentSnapshots){
                                mIssues.add(document.toObject(Issues.class));
                            }
                            mIssuesListAdapter.notifyDataSetChanged();
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