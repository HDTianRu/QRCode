package com.TianRu.QRCode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import android.view.View;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

public class Account extends AppCompatActivity {

  private UserHelper uh;
  private int current;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account);
    uh = UserHelper.getHelper();
    final List<User> users = uh.queryUsers();
    current = currentIndex(users);
    RecyclerView recyclerView = findViewById(R.id.userList);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    final UserAdapter adapter = new UserAdapter(users, new UserAdapter.OnUserItemClickListener() {

        @Override
        public void onViewClick(User user, int position) {
          View view = LayoutInflater.from(Account.this).inflate(R.layout.dialog_view, null);
          ((TextView) view.findViewById(R.id.uid)).setText(user.uid);
          ((TextView) view.findViewById(R.id.stoken)).setText(user.stoken);
          ((TextView) view.findViewById(R.id.cookie)).setText(user.cookie);
          ((TextView) view.findViewById(R.id.raw)).setText(user.raw);
          AlertDialog dialog = new AlertDialog.Builder(Account.this)
            .setTitle("信息详情")
            .setView(view)
            .show();
        }

        @Override
        public void onSwitchClick(User user, int position) {
          if (position == current) return;
          uh.switchUser(user.uid);
          if (current == -1) {
            current = position;
            return;
          }
          User tmpUser = users.get(current);
          tmpUser.current = 0;
          users.set(current,tmpUser);
          current = position;
          user.current = 1;
        }

        @Override
        public void onDeleteClick(User user, int position) {
          uh.deleteUser(user.uid);
          if (current != -1) current = currentIndex(users);
        }
      });
    recyclerView.setAdapter(adapter);
  }

  public int currentIndex(List<User> list) {
    int current = -1;
    for (int i = 0;i < list.size();i++) {
      if (list.get(i).current == 1) {
        current = i;
        break;
      }
      continue;
    }
    return current;
  }
}
