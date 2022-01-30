package com.harshit.salarymanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.harshit.salarymanager.R;
import com.harshit.salarymanager.listeners.IRecyclerViewClickListener;
import com.harshit.salarymanager.models.SalaryModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class salaryListAdapter extends RecyclerView.Adapter<salaryListAdapter.MySalaryViewHolder> {

    private final Context context;
    private sendItemData onSendData;
    private List<SalaryModel> salaryModelList;

    public void filterList(ArrayList<SalaryModel> filteredList) {
        salaryModelList = filteredList;
        notifyDataSetChanged();
    }

    public interface sendItemData{
        void updateItem(SalaryModel itemViewModel, int pos, String value);

    }

    public salaryListAdapter(Context context, List<SalaryModel> salaryModelList, sendItemData listener) {
        this.context = context;
        this.salaryModelList = salaryModelList;
        this.onSendData = listener;
    }

    @NonNull
    @Override
    public MySalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MySalaryViewHolder(LayoutInflater.from(context).inflate(R.layout.salary_lisview_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MySalaryViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //holder.view
        holder.txtFirstName.setText(salaryModelList.get(position).getFirstname());
        holder.txtLastName.setText(salaryModelList.get(position).getLastname());
        holder.txtCommissionsPrice.setText(new StringBuilder("Commissions: ").append(salaryModelList.get(position).getSalarycommisions()));
        holder.txtFixedPrice.setText(new StringBuilder("Fixed: ").append(salaryModelList.get(position).getSalaryfixed()));
        Glide.with(holder.imgStaff.getContext()).load(R.drawable.ic_baseline_person_24).circleCrop().into(holder.imgStaff);

        holder.txtPrice.setText(new StringBuilder("₹ ").append(Integer.parseInt(salaryModelList.get(position).getSalaryfixed())+ Integer.parseInt(salaryModelList.get(position).getSalarycommisions())-Integer.parseInt(salaryModelList.get(position).getSalarydeductable())));

        holder.setListener(((view, adapterPosition) ->
        {
            onSendData.updateItem(salaryModelList.get(position),position, salaryModelList.get(position).getId());
        }));
    }

    @Override
    public int getItemCount() {
        return salaryModelList.size();
    }

    public class MySalaryViewHolder extends RecyclerView.ViewHolder {

        //BindView
        @BindView(R.id.txtFirstName)
        TextView txtFirstName;

        @BindView(R.id.txtLastName)
        TextView txtLastName;

        @BindView(R.id.txtFixedPrice)
        TextView txtFixedPrice;

        @BindView(R.id.txtCommissionsPrice)
        TextView txtCommissionsPrice;

        @BindView(R.id.imgStaff)
        ImageView imgStaff;

        @BindView(R.id.txtPrice)
        TextView txtPrice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        private Unbinder unbinder;

        public MySalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRecyclerClick(view, getAdapterPosition());
                }
            });
        }
    }
}



