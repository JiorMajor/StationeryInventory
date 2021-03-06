package team5.ad.sa40.stationeryinventory.Fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team5.ad.sa40.stationeryinventory.R;


public class InventoryStockCard extends android.support.v4.app.Fragment  {

    private String itemID = "";
    private String itemName = "";
    private int stockQty = 0;
    private int roLvl = 0;
    private RecyclerView mRecyclerView;
    private SCListAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public InventoryStockCard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_inventory_stockcard, container, false);

        getActivity().setTitle("Stock Card");

        if (getArguments() != null) {
            Log.i("arguments: ",getArguments().toString());
            itemID = getArguments().getString("ItemID");
            itemName = getArguments().getString("ItemName");
            stockQty = getArguments().getInt("StockQty");
            roLvl = getArguments().getInt("ROLvl");
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.stockcard_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this.getActivity().getBaseContext(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new SCListAdapter(itemID);
        mRecyclerView.setAdapter(adapter);

        return view;
    }

}
