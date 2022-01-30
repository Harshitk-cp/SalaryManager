package com.harshit.salarymanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harshit.salarymanager.R;
import com.harshit.salarymanager.listeners.IRecyclerViewClickListener;
import com.harshit.salarymanager.models.StaffModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class staffAdapter extends RecyclerView.Adapter<staffAdapter.MyStaffViewHolder> {

    private Context context;
    private List<StaffModel> staffModelList;
    private staffAdapter.sendItemData onSendData;

    public staffAdapter(Context context, List<StaffModel> staffModelList, staffAdapter.sendItemData listener) {
        this.context = context;
        this.staffModelList = staffModelList;
        this.onSendData = listener;
    }

    @NonNull
    @Override
    public staffAdapter.MyStaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new staffAdapter.MyStaffViewHolder(LayoutInflater.from(context).inflate(R.layout.choose_staff_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull staffAdapter.MyStaffViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txtStaffFirstName.setText(staffModelList.get(position).getFirstname());
        holder.txtStaffLastName.setText(staffModelList.get(position).getLastname());


        holder.setListener(((view, adapterPosition) ->
        {
            onSendData.getStaff(staffModelList.get(position), position, staffModelList.get(position).getId());
        }));
    }

    @Override
    public int getItemCount() {
        return staffModelList.size();
    }

    public interface sendItemData {
        void getStaff(StaffModel itemViewModel, int pos, String value);

    }

    public class MyStaffViewHolder extends RecyclerView.ViewHolder {

        //BindView
        @BindView(R.id.txtStaffFirstName)
        TextView txtStaffFirstName;

        @BindView(R.id.txtStaffLastName)
        TextView txtStaffLastName;


        IRecyclerViewClickListener listener;
        private Unbinder unbinder;


        public MyStaffViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRecyclerClick(view, getAdapterPosition());
                }
            });
        }

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }
    }
}
