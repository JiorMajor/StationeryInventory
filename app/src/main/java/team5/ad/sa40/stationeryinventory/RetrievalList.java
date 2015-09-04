package team5.ad.sa40.stationeryinventory;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import team5.ad.sa40.stationeryinventory.Model.Retrieval;


public class RetrievalList extends android.support.v4.app.Fragment {

    private String empID;
    private String[] filters = {"View All","Pending","Retrieved"};
    private List<Retrieval> allRetrievals;
    private RecyclerView mRecyclerView;
    private RetListAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Bind(R.id.spinnerRet) Spinner spinnerRetStatus;

    public RetrievalList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            empID = getArguments().getString("EmpID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_retrieval_list, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.ret_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this.getActivity().getBaseContext(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        showAllRetrieval();

        ArrayAdapter<String> FiltersAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,filters);
        FiltersAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerRetStatus.setAdapter(FiltersAdapter);

        spinnerRetStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RetrievalList.this.getActivity(), "Selected: " + position, Toast.LENGTH_SHORT).show();
                Log.i("spinner's:", filters[position]);
                switch (position) {
                    case (0):
                        showAllRetrieval();
                        break;
                    case (1):
                        showPendingRetrieval();
                        break;
                    case (2):
                        showRetrievedRetrieval();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showAllRetrieval();
            }
        });

        return view;
    }

    public void showAllRetrieval() {
        adapter = new RetListAdapter();
        allRetrievals = new ArrayList<Retrieval>();
        allRetrievals = adapter.mRetrievals;
        mRecyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(new RetListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RetrievalList.this.getActivity(), "Click position at " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showPendingRetrieval() {
        List<Retrieval> pendingRetrievals = new ArrayList<Retrieval>();
        for(int i=0; i<allRetrievals.size(); i++) {
            Retrieval r = allRetrievals.get(i);
            if(r.getStatus()=="Pending") {
                pendingRetrievals.add(r);
            }
        }
        adapter.mRetrievals = pendingRetrievals;
        mRecyclerView.setAdapter(adapter);
    }

    public void showRetrievedRetrieval() {
        List<Retrieval> retrievedRetrievals = new ArrayList<Retrieval>();
        for(int i=0; i<allRetrievals.size(); i++) {
            Retrieval r = allRetrievals.get(i);
            if(r.getStatus()=="Retrieved") {
                retrievedRetrievals.add(r);
            }
        }
        adapter.mRetrievals = retrievedRetrievals;
        mRecyclerView.setAdapter(adapter);
    }

}