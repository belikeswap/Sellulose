package com.sellulose.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by swapn on 16-Jan-18.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {

    private ArrayList<SalesforceTask> mTasksList = new ArrayList<SalesforceTask>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView taskName, taskContact, dueDate;
        public MyViewHolder(View view) {
            super(view);

            taskName = (TextView) view.findViewById(R.id.task_name);
            taskContact = (TextView) view.findViewById(R.id.task_contact);
            dueDate = (TextView) view.findViewById(R.id.due_date);

        }
    }

    //Default constructor
    public TasksAdapter() {

    }

    public TasksAdapter(ArrayList<SalesforceTask> tasksList) {

        this.mTasksList = tasksList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_salesforce_task, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.taskName.setText(mTasksList.get(position).getTaskName());
        holder.taskContact.setText(mTasksList.get(position).getTaskContact());
        holder.dueDate.setText(mTasksList.get(position).getDueDate());

    }

    @Override
    public int getItemCount() {
        return mTasksList.size();
    }

    public void clearAdapter() {
        this.mTasksList.clear();
        notifyDataSetChanged();
    }
}
