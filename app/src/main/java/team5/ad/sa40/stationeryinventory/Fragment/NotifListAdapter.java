package team5.ad.sa40.stationeryinventory.Fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import team5.ad.sa40.stationeryinventory.Model.JSONNotification;
import team5.ad.sa40.stationeryinventory.R;
import team5.ad.sa40.stationeryinventory.Utilities.Setup;

public class NotifListAdapter extends RecyclerView.Adapter<NotifListAdapter.ViewHolder> {

    public static List<JSONNotification> mJSONNotifications;
    String[] retId;
    NotifListAdapter.OnItemClickListener mItemClickListener;

    public NotifListAdapter(List<JSONNotification> JSONNotificationList){
        super();
        mJSONNotifications = new ArrayList<JSONNotification>();

        if(JSONNotificationList.size() >0) {
            List<JSONNotification> temp = JSONNotificationList;
            Collections.sort(temp);
            List<JSONNotification> readNotif = new ArrayList<JSONNotification>();
            for(JSONNotification jn : temp){
                if(jn.getStatus().equals("UNREAD")){
                    mJSONNotifications.add(jn);
                }
                else{
                    readNotif.add(jn);
                }
            }
            for(JSONNotification j : readNotif){
                mJSONNotifications.add(j);
            }
        }

        retId = new String[mJSONNotifications.size()];
        if(mJSONNotifications.size() >0) {
            Log.i("Size of list", String.valueOf(mJSONNotifications.size()));
            Setup s = new Setup();
            for (int i = 0; i < mJSONNotifications.size(); i++) {
                String temp = String.valueOf(mJSONNotifications.get(i).getNotifID());
                retId[i] = temp;
            }
            Log.i("First of string ", retId[0]);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.inv_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        JSONNotification JSONNotification = mJSONNotifications.get(i);

        viewHolder.JSONNotificationID.setText(JSONNotification.getNotifName());
        viewHolder.JSONNotificationName.setText(JSONNotification.getNotifDesc());
        Date d = Setup.parseJSONDateToJavaDate(JSONNotification.getDateTime());
        String displayD = "";
        Date today = new Date();
        if(d.getDate() == today.getDate()) {
            if(d.getHours() >= 12){
                String hour = String.valueOf(d.getHours() - 12);
                Log.i("Hour", hour);
                String min = "";
                if(d.getMinutes() < 10){
                    min = 0+String.valueOf(d.getMinutes());
                }
                else{
                    min = String.valueOf(d.getMinutes());
                }
                Log.i("Minute", min + min.length());
                displayD = hour+ " : "+min+ " PM";
            }
            else {
                String hour = String.valueOf(d.getHours());
                Log.i("Hour", hour);
                String min = "";
                if(d.getMinutes() < 10){
                    min = 0+String.valueOf(d.getMinutes());
                }
                else{
                    min = String.valueOf(d.getMinutes());
                }
                Log.i("Minute", min);
                displayD = hour+ " : "+min+ " AM";
            }
        }
        else {
            displayD = Setup.parseDateToString(d);
        }
        viewHolder.JSONNotificationStatus.setText(displayD);
        viewHolder.JSONNotificationStatus.setTextSize(11);
        if(JSONNotification.getStatus().equals("READ")) {
            viewHolder.JSONNotificationStatus.setTextColor(R.color.DefaultTextColor);
        }

        //format notification category icon:
        String categoryID = JSONNotification.getNotifName();
        switch(categoryID) {
            case "New Pending Requisition":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif1);
                break;
            case "Requisition Approved":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif1);
                break;
            case "Requisition Rejected":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif1);
                break;
            case "Processing Requisition":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif1);
                break;
            case "Requisition Processed":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif1);
                break;
            case "Requisition Disbursed":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif1);
                break;
            case "New Pending Adjustment Voucher":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif4);
                break;
            case "Adjustment Voucher Approved":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif4);
                break;
            case "Adjustment Voucher Rejected":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif4);
                break;
            case "Requisition Items Not Fulfilled":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif1);
                break;
            case "New Collection Schedule":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif3);
                break;
            case "Change in Department's Representative":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif5);
                break;
            case "Change in Department's Collection Point":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif5);
                break;
            case "Low Stock Inventory":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif2);
                break;
            case "New Report Generated":
                viewHolder.JSONNotificationCatIcon.setImageResource(R.drawable.icon_notif6);
                break;
        }
    }

    @Override
    public int getItemCount() {

        return mJSONNotifications.size();
    }

    public JSONNotification removeJSONNotification(int position) {
        final JSONNotification JSONNotification = mJSONNotifications.remove(position);
        notifyItemRemoved(position);
        return JSONNotification;
    }

    public void addJSONNotification(int position, JSONNotification JSONNotification) {
        mJSONNotifications.add(position, JSONNotification);
        notifyItemInserted(position);
    }

    public void moveJSONNotification(int fromPosition, int toPosition) {
        final JSONNotification model = mJSONNotifications.remove(fromPosition);
        mJSONNotifications.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<JSONNotification> JSONNotifications) {
        applyAndAnimateRemovals(JSONNotifications);
        applyAndAnimateAdditions(JSONNotifications);
        applyAndAnimateMovedJSONNotifications(JSONNotifications);
    }

    private void applyAndAnimateRemovals(List<JSONNotification> newJSONNotifications) {
        for (int i = mJSONNotifications.size() - 1; i >= 0; i--) {
            final JSONNotification model = mJSONNotifications.get(i);
            if (!newJSONNotifications.contains(model)) {
                removeJSONNotification(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<JSONNotification> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final JSONNotification JSONNotification = newModels.get(i);
            if (!mJSONNotifications.contains(JSONNotification)) {
                addJSONNotification(i, JSONNotification);
            }
        }
    }

    private void applyAndAnimateMovedJSONNotifications(List<JSONNotification> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final JSONNotification model = newModels.get(toPosition);
            final int fromPosition = mJSONNotifications.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveJSONNotification(fromPosition, toPosition);
            }
        }
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView JSONNotificationID;
        public TextView JSONNotificationName;
        public TextView JSONNotificationStatus;
        public ImageView JSONNotificationCatIcon;

        public ViewHolder(View ItemView){
            super(ItemView);
            JSONNotificationID = (TextView) ItemView.findViewById(R.id.reportItemCode);
            JSONNotificationName = (TextView) ItemView.findViewById(R.id.reportItemName);
            JSONNotificationStatus = (TextView) ItemView.findViewById(R.id.reportReason);
            JSONNotificationCatIcon = (ImageView) ItemView.findViewById(R.id.inv_icon);

            ItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener!=null){
                mItemClickListener.onItemClick(v, getAdapterPosition());

            }
        }
    }
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
}
