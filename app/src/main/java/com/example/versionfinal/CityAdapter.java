package com.example.versionfinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.versionfinal.User.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends BaseAdapter implements Filterable {
    private final Context context;
    private final List<City> originalCities;
    private List<City> filteredCities;
    private CityFilter cityFilter;

    public CityAdapter(Context context) {
        this.context = context;
        this.originalCities = new ArrayList<>();
        this.filteredCities = new ArrayList<>();
        initCities();
        this.filteredCities.addAll(originalCities);
    }

    private void initCities() {
        originalCities.add(new City("macarena", R.drawable.macarena));
        originalCities.add(new City("maracana", R.drawable.maracana));
        originalCities.add(new City("arena", R.drawable.arena));
        originalCities.add(new City("footchamp", R.drawable.footchamp));
        originalCities.add(new City("beta", R.drawable.beta));
        originalCities.add(new City("olympiesky", R.drawable.olympiesky));
    }

    @Override
    public int getCount() {
        return filteredCities.size();
    }

    @Override
    public City getItem(int position) {
        return filteredCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
            holder = new ViewHolder();
            holder.cityImage = convertView.findViewById(R.id.cityImage);
            holder.cityName = convertView.findViewById(R.id.cityName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        City city = getItem(position);
        holder.cityName.setText(city.getName());
        holder.cityImage.setImageResource(city.getImageResource());

        // Ajouter le clic sur l'élément
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("cityName", city.getName());
            context.startActivity(intent);
        });

        return convertView;
    }
    @Override
    public Filter getFilter() {
        if (cityFilter == null) {
            cityFilter = new CityFilter();
        }
        return cityFilter;
    }

    private class CityFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<City> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalCities);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (City city : originalCities) {
                    if (city.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(city);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCities.clear();
            filteredCities.addAll((List<City>) results.values);
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        ImageView cityImage;
        TextView cityName;
    }
}