package io.authme.home.history;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.authme.home.R;

public class PendingLogins extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String pendingLogins;
    private ArrayList<PendingData> pendingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_logins);

        if (getIntent().hasExtra("pending")) {
            pendingLogins = getIntent().getStringExtra("pending");
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        pendingList = getDataSet();
        mAdapter = new MyRecyclerViewAdapter(pendingList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(getApplicationContext(), pendingList.get(position).getReferenceId(), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private ArrayList<PendingData> getDataSet() {
        ArrayList results = new ArrayList<>();
        try {
            JSONArray pendingArray = new JSONArray(pendingLogins);
            for (int index = 0; index < pendingArray.length(); index++) {
                PendingData obj = new PendingData((JSONObject) pendingArray.get(index));
                results.add(index, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }
}
