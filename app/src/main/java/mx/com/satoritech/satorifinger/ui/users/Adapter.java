package mx.com.satoritech.satorifinger.ui.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.com.satoritech.satorifinger.R;
import mx.com.satoritech.satorifinger.models.User;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<User> users;
    private Context context;
    private OnUserClickListener onUserClickListener;
    private OnUserRegisterFingerClickListener onUserRegisterFingerClickListener;

    public Adapter(@NonNull List<User> users,
                   @NonNull Context context,
                   @NonNull OnUserClickListener onUserClickListener,
                   @NonNull OnUserRegisterFingerClickListener onUserRegisterFingerClickListener) {

        this.users = users;
        this.context = context;
        this.onUserClickListener = onUserClickListener;
        this.onUserRegisterFingerClickListener = onUserRegisterFingerClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_user,
                parent,
                false
        );
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.tvUserName.setText(user.getFullName());
        holder.tvJob.setText(user.getJob().getName());
        if (onUserClickListener != null) {
            holder.itemRoot.setOnClickListener(
                    view -> onUserClickListener.onGroupCLickListener(user)
            );
        }
        if (onUserRegisterFingerClickListener != null) {
            holder.ivRegisterFinger.setOnClickListener(
                    v -> onUserRegisterFingerClickListener.onUserRegisterFingerClickListener(user));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView itemRoot;
        TextView tvUserName;
        TextView tvJob;
        ImageView ivRegisterFinger;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRoot = itemView.findViewById(R.id.item_root);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvJob = itemView.findViewById(R.id.tv_job);
            ivRegisterFinger = itemView.findViewById(R.id.iv_register_finger);
        }
    }

    interface OnUserClickListener {
        void onGroupCLickListener(User user);
    }

    interface OnUserRegisterFingerClickListener {
        void onUserRegisterFingerClickListener(User user);
    }

}