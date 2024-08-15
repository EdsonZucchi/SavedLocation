package com.example.savedlocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savedlocation.entity.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locations;

    public LocationAdapter(List<Location> locations) {
        this.locations = locations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.coordinatesTextView.setText("Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView coordinatesTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            coordinatesTextView = itemView.findViewById(R.id.location_coordinates);
        }
    }
}
