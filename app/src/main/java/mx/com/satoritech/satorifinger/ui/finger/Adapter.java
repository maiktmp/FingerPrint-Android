package mx.com.satoritech.satorifinger.ui.finger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import mx.com.satoritech.satorifinger.R;
import mx.com.satoritech.satorifinger.models.RegistryType;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<RegistryType> registryTypes;
    private ArrayList<Adapter.ViewHolder> holders = new ArrayList<>();
    private OnCardClickListener onCardClickListener;
    Context context;


    public Adapter(ArrayList<RegistryType> registryTypes, OnCardClickListener onCardClickListener, Context context) {
        this.registryTypes = registryTypes;
        this.onCardClickListener = onCardClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_registry_type,
                parent,
                false
        );
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holders.add(holder);
        RegistryType registryType = registryTypes.get(position);
        holder.tvRegistryType.setText(registryType.getName());
        holder.itemRoot.setCheckable(true);
        holder.itemRoot.setChecked(false);

        holder.itemRoot.setOnClickListener(v -> {
            for (ViewHolder viewHolder : holders) {
                viewHolder.itemRoot.setChecked(false);
            }
            holder.itemRoot.setChecked(true);
            onCardClickListener.OnCardClickListener(registryType);
        });
        holder.itemRoot.setOnCheckedChangeListener(((card, isChecked) -> {
            card.setStrokeColor(context.getResources().getColor(R.color.colorAccent));
            if (isChecked) {
                card.setStrokeWidth(2);
            } else {
                card.setStrokeWidth(0);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return registryTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView itemRoot;
        ImageView ivRegistryType;
        TextView tvRegistryType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRegistryType = itemView.findViewById(R.id.iv_registry_type);
            tvRegistryType = itemView.findViewById(R.id.tv_registry_type);
            itemRoot = itemView.findViewById(R.id.item_root);
        }
    }

    interface OnCardClickListener {
        void OnCardClickListener(RegistryType registryType);
    }
}
