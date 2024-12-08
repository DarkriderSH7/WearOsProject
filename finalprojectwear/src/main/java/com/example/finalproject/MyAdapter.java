package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.databinding.ListItemBinding;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final String[] items;

    // Constructor for the Adapter
    public MyAdapter(String[] items) {
        this.items = items;
    }

    // Create a new ViewHolder
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use ViewBinding to inflate the layout
        ListItemBinding binding = ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    // Bind data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get the item at the current position
        String text = items[position];

        String[] values = text.split(":");
        // Set the text to the TextView using ViewBinding
        holder.binding.itemTitle.setText(values[0]);
        holder.binding.itemValue.setText(values[1]);
    }

    // Return the number of items in the data set
    @Override
    public int getItemCount() {
        return items.length;
    }

    // ViewHolder class for each list item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ListItemBinding binding;

        public MyViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
