package com.example.neilpelow.finalyearproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neilpelow on 16/02/2017.
 */

public class EventsAdapter extends
        RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.name);
        }
    }

    private ArrayList<Event> mEvents;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public EventsAdapter(Context context, ArrayList<Event> events) {
        mEvents = events;
        mContext = context;
    }

    // Easy access to the callback object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Event event = mEvents.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(event.getName());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}