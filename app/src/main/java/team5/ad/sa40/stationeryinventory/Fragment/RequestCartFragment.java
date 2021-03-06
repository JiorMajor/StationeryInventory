package team5.ad.sa40.stationeryinventory.Fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team5.ad.sa40.stationeryinventory.API.RequestCartAPI;
import team5.ad.sa40.stationeryinventory.API.RequisitionAPI;
import team5.ad.sa40.stationeryinventory.Model.JSONItem;
import team5.ad.sa40.stationeryinventory.Model.JSONRequestCart;
import team5.ad.sa40.stationeryinventory.Model.JSONRequisition;
import team5.ad.sa40.stationeryinventory.Model.JSONStatus;
import team5.ad.sa40.stationeryinventory.R;
import team5.ad.sa40.stationeryinventory.Utilities.Setup;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestCartFragment extends android.support.v4.app.Fragment implements MainActivity.OnBackPressedListener {


    @Bind(R.id.item_recycler_view)
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RequestCartAdapter mAdapter;
    @Bind(R.id.searchItem) SearchView search;
    private List<JSONRequestCart> mItems;
    private List<JSONItem> itemList;

    public RequestCartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        ((MainActivity)getActivity()).setOnBackPressedListener(this);
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        getActivity().setTitle("Request Cart");
        new AlertDialog.Builder(getActivity())
                .setTitle("Information")
                .setMessage("Please swipe left the item to delete from request cart!")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_info_white_24dp)
                .show();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity().getBaseContext(), 1));
        mAdapter = new RequestCartAdapter();
        Log.i("Request Cart Size: ", String.valueOf(MainActivity.requestCart.size()));
        mItems = new ArrayList<>();
        ScaleInAnimationAdapter animatedAdapter = new ScaleInAnimationAdapter(mAdapter);
        mRecyclerView.setAdapter(animatedAdapter);
        Log.i("mAdapterSize: ", String.valueOf(mAdapter.myItemlist.size()));
        for(JSONRequestCart it: mAdapter.myItemlist){
            mItems.add(it);
        }
        mAdapter.SetOnItemClickListener( new RequestCartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(RequestCartFragment.this.getActivity(), "Click position at " + position, Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<JSONRequestCart> filteredModelList = filter(mItems, newText);
                mAdapter.animateTo(filteredModelList);
                mRecyclerView.scrollToPosition(0);
                return true;
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("EmpID", Setup.user.getEmpID());
                jsonObject.addProperty("ItemID", Setup.allRequestItems.get(viewHolder.getAdapterPosition()).getItemID());
                jsonObject.addProperty("Qty", Setup.allRequestItems.get(viewHolder.getAdapterPosition()).getQty());
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Setup.baseurl).build();
                RequestCartAPI rqAPI = restAdapter.create(RequestCartAPI.class);
                rqAPI.deletefromCart(jsonObject, new Callback<Boolean>() {
                    @Override
                    public void success(Boolean aBoolean, Response response) {
                        Setup.allRequestItems.remove(viewHolder.getAdapterPosition());
                        mAdapter.myItemlist = Setup.allRequestItems;
                        mRecyclerView.setAdapter(mAdapter);
                        Toast.makeText(RequestCartFragment.this.getActivity(), "Item removed from Cart.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private List<JSONRequestCart> filter(List<JSONRequestCart> items, String query) {
        query = query.toLowerCase();

        final List<JSONRequestCart> filteredItemList = new ArrayList<>();
        for (JSONRequestCart itemm : items) {
            final String text = itemm.getItemName().toLowerCase();
            if (text.contains(query)) {
                filteredItemList.add(itemm);
            }
        }
        return filteredItemList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.getActivity().getMenuInflater().inflate(R.menu.my_request_cart_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Setup.baseurl).build();
        final RequestCartAPI rqAPI = restAdapter.create(RequestCartAPI.class);
        if(id == R.id.action_request_done){
            //alert for priority and remark
            LayoutInflater li = LayoutInflater.from(getActivity().getBaseContext());
            final View priortyAlert = li.inflate(R.layout.priorityalert, null);
            // set prompts.xml to alertdialog builder
            final EditText myRemark = (EditText) priortyAlert.findViewById(R.id.myRemark);
            final Switch mySwitch = (Switch) priortyAlert.findViewById(R.id.mySwitch);

            myRemark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Setup.strRemark = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mySwitch.setChecked(false);
            mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Setup.priorityID = 1;
                    } else {
                        Setup.priorityID = 2;
                    }
                }
            });
            //to load request cart list inside here
            rqAPI.getItemsbyEmpID(Setup.user.getEmpID(), new Callback<List<JSONRequestCart>>() {
                @Override
                public void success(List<JSONRequestCart> jsonRequestCarts, Response response) {
                    List<JsonObject> myRequest = new ArrayList<JsonObject>();
                    for (JSONRequestCart jitem : jsonRequestCarts) {
                        JsonObject myItem = new JsonObject();
                        myItem.addProperty("EmpID", Setup.user.getEmpID());
                        myItem.addProperty("ItemID", jitem.getItemID());
                        myItem.addProperty("Qty", jitem.getQty());
                        myRequest.add(myItem);
                    }
                    rqAPI.submit(myRequest, new Callback<Integer>() {
                        @Override
                        public void success(Integer integer, Response response) {
                            Setup.reqID = integer;
                            new AlertDialog.Builder(getActivity())
                                    .setCancelable(false)
                                    .setView(priortyAlert)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(final DialogInterface dialog, int id) {
                                                    // get user input and set it to result
                                                    // edit text
                                                    //call requisition api to set priority and remark
                                                    RequisitionAPI requisitionAPI = restAdapter.create(RequisitionAPI.class);
                                                    requisitionAPI.setReqPriority(Setup.reqID, Setup.priorityID, Setup.strRemark, new Callback<Boolean>() {
                                                        @Override
                                                        public void success(Boolean aBoolean, Response response) {
                                                            new AlertDialog.Builder(getActivity())
                                                                    .setTitle("Request Successful")
                                                                    .setMessage("Your request have been successfuly submitted.")
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            Log.i("User", Setup.user.getEmpID().toString());
                                                                            final RequisitionAPI reqAPI = restAdapter.create(RequisitionAPI.class);
                                                                            reqAPI.getRequisition(Setup.user.getEmpID(), new Callback<List<JSONRequisition>>() {
                                                                                @Override
                                                                                public void success(List<JSONRequisition> jsonRequisitions, Response response) {
                                                                                    reqAPI.getStatus(new Callback<List<JSONStatus>>() {
                                                                                        @Override
                                                                                        public void success(List<JSONStatus> jsonStatuses, Response response) {
                                                                                            RequisitionListAdapter.mStatus = jsonStatuses;
                                                                                            Log.i("Status Success", response.getUrl() + " " + String.valueOf(response.getStatus()));
                                                                                        }

                                                                                        @Override
                                                                                        public void failure(RetrofitError error) {
                                                                                            Log.i("Status Failed", error.toString() + " " + error.getUrl());
                                                                                        }
                                                                                    });
                                                                                    Log.i("URL", response.getUrl());
                                                                                    Log.i("STATUS", String.valueOf(response.getStatus()));
                                                                                    Log.i("REASON", response.getReason());
                                                                                    Log.i("Size of requisition", String.valueOf(jsonRequisitions.size()));
                                                                                    RequisitionListAdapter.mRequisitions = jsonRequisitions;
                                                                                    Setup.allRequisition = jsonRequisitions;
                                                                                    RequisitionListFragment rqListFrag = new RequisitionListFragment();
                                                                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                                                                    fragmentTransaction.replace(R.id.frame, rqListFrag).addToBackStack("REQUEST_CART").commit();
                                                                                }

                                                                                @Override
                                                                                public void failure(RetrofitError error) {
                                                                                    Log.i("GetRequisitionFail", error.toString() + " " + error.getUrl());
                                                                                }
                                                                            });

                                                                        }
                                                                    })
                                                                    .setIcon(android.R.drawable.ic_dialog_info)
                                                                    .show();
                                                        }

                                                        @Override
                                                        public void failure(RetrofitError error) {
                                                            Log.i("Error in setPrioriyt", error.getUrl()+error.toString());
                                                        }
                                                    });
                                                }
                                            }).show();
                            //
                            Log.i("Success", String.valueOf(integer));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.i("fail submit", error.toString() + " " + error.getUrl());
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Failed to Submit Requisition")
                                    .setMessage("Sorry Requisition cannot made for the moment! Please try again later.")
                                    .setCancelable(false)
                                    .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("Failed calling rqCart", error.toString());
                }
            });


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doBack() {
        ItemListFragment fragment = new ItemListFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).commit();
    }
}
