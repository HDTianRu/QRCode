package com.TianRu.QRCode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.TianRu.QRCode.UserAdapter;
import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
  private List<User> userList;
  private OnUserItemClickListener listener;

  public UserAdapter(List<User> userList, OnUserItemClickListener listener) {
    this.userList = userList;
    this.listener = listener;
  }

  @NonNull
  @Override
  public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
    return new UserViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
    final User user = userList.get(position);
    holder.uid.setText(user.uid + (user.current == 1 ? "(当前)" : ""));
    
    holder.viewUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onViewClick(user,position);
        }
      });

    holder.switchUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onSwitchClick(user,position);
          notifyDataSetChanged();
        }
      });

    holder.deleteUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          listener.onDeleteClick(user,position);
          userList.remove(position);
          notifyItemRemoved(position);
        }
      });
  }

  @Override
  public int getItemCount() {
    return userList.size();
  }

  static class UserViewHolder extends RecyclerView.ViewHolder {
    TextView uid;
    Button viewUser;
    Button switchUser;
    Button deleteUser;

    UserViewHolder(@NonNull View itemView) {
      super(itemView);
      uid = itemView.findViewById(R.id.uid);
      viewUser = itemView.findViewById(R.id.viewUser);
      switchUser = itemView.findViewById(R.id.switchUser);
      deleteUser = itemView.findViewById(R.id.deleteUser);
    }
  }

  public interface OnUserItemClickListener {
    void onViewClick(User user,int position);
    void onSwitchClick(User user,int position);
    void onDeleteClick(User user,int position);
  }
}

