package team5.ad.sa40.stationeryinventory.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.livotov.zxscan.ScannerView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team5.ad.sa40.stationeryinventory.API.InventoryAPI;
import team5.ad.sa40.stationeryinventory.API.ItemAPI;
import team5.ad.sa40.stationeryinventory.API.RequestCartAPI;
import team5.ad.sa40.stationeryinventory.Model.JSONCategory;
import team5.ad.sa40.stationeryinventory.Model.JSONItem;
import team5.ad.sa40.stationeryinventory.Model.JSONRequestCart;
import team5.ad.sa40.stationeryinventory.R;
import team5.ad.sa40.stationeryinventory.Utilities.Setup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScannerFragment extends android.support.v4.app.Fragment implements ScannerView.ScannerViewEventListener,MainActivity.OnBackPressedListener {

    @Bind(R.id.scanner) ScannerView embeddedScanner;
    @Bind(R.id.rescanBtn) Button btnReScan;
    @Bind(R.id.btnStopScanner) Button btnStopScan;
    @Bind(R.id.itemNumberValue) TextView textItemNumber;
    @Bind(R.id.item_name_value) TextView textItemName;
    @Bind(R.id.category_value) TextView textCategory;
    @Bind(R.id.waitLabel) TextView waitLabel;
    @Bind(R.id.scannerRoot) FrameLayout embeddedScannerRoot;
    @Bind(R.id.fab) FloatingActionButton btnFab;
    Boolean scannerRunning = false;
    RestAdapter restAdapter;
    static String itemID;
    JSONItem myItem;
    //zxscanlib
    private String lastEmbeddedScannerScannedData;
    private long lastEmbeddedScannerScannedDataTimestamp;
    android.support.v4.app.FragmentTransaction fragmentTran;

    public ScannerFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        ((MainActivity)getActivity()).setOnBackPressedListener(this);
        ButterKnife.bind(this, view);
        embeddedScanner.setScannerViewEventListener(this);
        startEmbeddedScanner();
        restAdapter = new RestAdapter.Builder().setEndpoint(Setup.baseurl).build();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    //Button Events Here
    //Butterknife
    @OnClick(R.id.btnStopScanner) void stopScan(){
        stopEmbeddedScanner();
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.rescanBtn) void restartScan(){
        if(scannerRunning){
            return;
        }
        else {

            startEmbeddedScanner();
        }
    }

    @OnClick(R.id.fab) void actionAdd(){


        //Add to request cart methods to be implement here

        final int empID = Setup.user.getEmpID();
        if(Setup.user.getRoleID().equals("SC")){
            InventoryDetails detailsFragment = new InventoryDetails();
            detailsFragment.item = myItem;
            android.support.v4.app.FragmentTransaction fragmentTransaction2 = getFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.frame, detailsFragment).addToBackStack("INVENTORYLIST TAG").commit();
        }
        else {
            //need to check empID; implement condition here
            //SC go other page
            final int qty = 1;
            final JsonObject reqItem = new JsonObject();
            reqItem.addProperty("EmpID", empID);
            reqItem.addProperty("ItemID", itemID);
            reqItem.addProperty("Qty", qty);
            //retrofit
            final RequestCartAPI rqCartAPI = restAdapter.create(RequestCartAPI.class);
            rqCartAPI.getItemsbyEmpID(empID, new Callback<List<JSONRequestCart>>() {
                @Override
                public void success(List<JSONRequestCart> jsonRequestCarts, Response response) {
                    JsonElement jsonElement = reqItem;
                    int qty = jsonElement.getAsJsonObject().get("Qty").getAsInt();
                    String ItemID = jsonElement.getAsJsonObject().get("ItemID").getAsString();
                    System.out.println(jsonElement.getAsJsonObject().get("ItemID").getAsString() + ItemID);
                    if (jsonRequestCarts.size() > 0) {
                        Setup.allRequestItems = jsonRequestCarts;
                        for (JSONRequestCart jCart : jsonRequestCarts) {
                            if (itemID.equals(jCart.getItemID())) {
                                System.out.println("We are the same " + jCart.getItemID() + " " + ItemID);
                                System.out.println("JSON new" + jsonElement.getAsJsonObject());
                                reqItem.addProperty("Qty", jCart.getQty() + qty);
                                rqCartAPI.updatetoCart(jsonElement.getAsJsonObject(), new Callback<Boolean>() {
                                    @Override
                                    public void success(Boolean aBoolean, Response response) {
                                        Toast.makeText(ScannerFragment.this.getActivity(), "Your item is added to Cart.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Toast.makeText(ScannerFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }//end of if statement which item equal others inside cart
                            else {
                                reqItem.addProperty("EmpID", empID);
                                reqItem.addProperty("ItemID", itemID);
                                reqItem.addProperty("Qty", qty);
                                rqCartAPI.addtoCart(reqItem, new Callback<Boolean>() {
                                    @Override
                                    public void success(Boolean aBoolean, Response response) {
                                        Toast.makeText(ScannerFragment.this.getActivity(), "Your item is added to Cart.", Toast.LENGTH_SHORT).show();
                                        RequestCartAPI rqAPI = restAdapter.create(RequestCartAPI.class);
                                        rqAPI.getItemsbyEmpID(Setup.user.getEmpID(), new Callback<List<JSONRequestCart>>() {
                                            @Override
                                            public void success(List<JSONRequestCart> jsonRequestCarts, Response response) {
                                                Setup.allRequestItems = jsonRequestCarts;
                                                if (Setup.allRequestItems.size() > 0) {
                                                    RequestCartFragment rqFrag = new RequestCartFragment();
                                                    fragmentTran = getFragmentManager().beginTransaction();
                                                    fragmentTran.replace(R.id.frame, rqFrag).addToBackStack("REQUEST_CART_FRAG").commit();
                                                } else {
                                                    Toast.makeText(getActivity(), "We acknowledge you that you haven't add any item yet.Please add some items before you proceed.", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void failure(RetrofitError error) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Toast.makeText(ScannerFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });// end of add to cart method
                            }//end of else statement which is item not equal inside cart
                        } //end of forloop
                    }//end of checking return jsonarray size
                    //Log.i("Success", String.valueOf(Setup.allRequestItems.size()));
                    else {
                        rqCartAPI.addtoCart(jsonElement.getAsJsonObject(), new Callback<Boolean>() {
                            @Override
                            public void success(Boolean aBoolean, Response response) {
                                Toast.makeText(ScannerFragment.this.getActivity(), "Your item is added to Cart.", Toast.LENGTH_SHORT).show();
                                RequestCartAPI rqAPI = restAdapter.create(RequestCartAPI.class);
                                rqAPI.getItemsbyEmpID(Setup.user.getEmpID(), new Callback<List<JSONRequestCart>>() {
                                    @Override
                                    public void success(List<JSONRequestCart> jsonRequestCarts, Response response) {
                                        Setup.allRequestItems = jsonRequestCarts;
                                        if (Setup.allRequestItems.size() > 0) {
                                            RequestCartFragment rqFrag = new RequestCartFragment();
                                            fragmentTran = getFragmentManager().beginTransaction();
                                            fragmentTran.replace(R.id.frame, rqFrag).addToBackStack("REQUEST_CART_FRAG").commit();
                                        } else {
                                            Toast.makeText(getActivity(), "We acknowledge you that you haven't add any item yet.Please add some items before you proceed.", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                    }
                                });
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(ScannerFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(ScannerFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void startEmbeddedScanner()
    {
        scannerRunning = true;
        embeddedScanner.setVisibility(View.VISIBLE);
        waitLabel.setVisibility(View.VISIBLE);
        embeddedScanner.startScanner();
    }

    private void stopEmbeddedScanner()
    {
        scannerRunning = false;
        embeddedScanner.stopScanner();
        embeddedScanner.setVisibility(View.INVISIBLE);
    }

    private void displayScannedResult(final String data)
    {
        //call api here
        itemID = data;
        InventoryAPI  invAPI = restAdapter.create(InventoryAPI.class);
        invAPI.getItemDetails(data, new Callback<JSONItem>() {
            @Override
            public void success(final JSONItem jsonItem, Response response) {
                myItem = jsonItem;
                textItemNumber.setText(jsonItem.getItemID());
                textItemName.setText(jsonItem.getItemName());
                ItemAPI itemAPI = restAdapter.create(ItemAPI.class);
                itemAPI.getCategory(new Callback<List<JSONCategory>>() {
                    @Override
                    public void success(List<JSONCategory> jsonCategories, Response response) {
                        for (JSONCategory category : jsonCategories) {
                            if (category.getItemCatID().equals(jsonItem.getItemCatID())) {
                                Log.i("Category", category.getItemDescription());
                                textCategory.setText(category.getItemDescription());
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("Category", error.toString());
                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("Item Detail", error.toString());
            }
        });



    }

    @Override
    public void onScannerReady()
    {
        if (waitLabel.getVisibility() == View.VISIBLE)
        {
            waitLabel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onScannerFailure(int cameraError)
    {
        Toast.makeText(getActivity(), getString(R.string.camera_error, cameraError), Toast.LENGTH_LONG).show();
        startEmbeddedScanner();
    }

    public boolean onCodeScanned(final String data)
    {
        // As we run embedded scanner in continuous mode, we have to add same code protection here in order to avoid
        // generating a lot of same-code scan events
        if (data != null)
        {
            if (data.equalsIgnoreCase(lastEmbeddedScannerScannedData) && System.currentTimeMillis() - lastEmbeddedScannerScannedDataTimestamp < 1000)
            {
                return false;
            }
            else
            {
                displayScannedResult(data);
                lastEmbeddedScannerScannedData = data;
                lastEmbeddedScannerScannedDataTimestamp = System.currentTimeMillis();
                stopEmbeddedScanner();
                return true;
            }
        }

        return false;
    }

    @Override
    public void doBack() {
        if(!Setup.user.getRoleID().equals("SC")) {
            CategoryFragment fragment = new CategoryFragment();
            FragmentTransaction fragTran = getFragmentManager().beginTransaction();
            fragTran.replace(R.id.frame, fragment).commit();
        }
        else{
            InventoryAPI invAPI = restAdapter.create(InventoryAPI.class);

            invAPI.getList(new Callback<List<JSONItem>>() {
                @Override
                public void success(List<JSONItem> jsonItems, Response response) {
                    Log.i("Result :", jsonItems.toString());
                    Log.i("First item: ", jsonItems.get(0).getItemID().toString());
                    Log.i("Response: ", response.getBody().toString());
                    System.out.println("Response Status " + response.getStatus());
                    InvListAdapter.mJSONItems = jsonItems;
                    System.out.println("SIZE:::::" + InvListAdapter.mJSONItems.size());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("Error: ", error.toString());
                }
            });
            InventoryList fragment = new InventoryList();
            fragmentTran = getFragmentManager().beginTransaction();
            fragmentTran.replace(R.id.frame, fragment);
            fragmentTran.commit();
        }
    }
}
