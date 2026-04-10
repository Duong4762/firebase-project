package com.example.class_ex.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.class_ex.R;
import com.example.class_ex.models.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.VH> {

    private final List<Ticket> items = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public void setItems(List<Ticket> tickets) {
        items.clear();
        if (tickets != null) {
            items.addAll(tickets);
            Collections.sort(items, (a, b) -> Long.compare(b.getCreatedAtMillis(), a.getCreatedAtMillis()));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Ticket t = items.get(position);
        holder.movie.setText(t.getMovieTitle());
        holder.theater.setText(t.getTheaterName());
        holder.time.setText(sdf.format(new Date(t.getShowTimeMillis())));
        holder.seats.setText(holder.itemView.getContext().getString(R.string.ticket_seats, t.getSeatCount()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView movie;
        final TextView theater;
        final TextView time;
        final TextView seats;

        VH(@NonNull View itemView) {
            super(itemView);
            movie = itemView.findViewById(R.id.textMovie);
            theater = itemView.findViewById(R.id.textTheater);
            time = itemView.findViewById(R.id.textTime);
            seats = itemView.findViewById(R.id.textSeats);
        }
    }
}
