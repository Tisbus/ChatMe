package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private List<Users> usersList;
    private UserAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private ChildEventListener usersChild;
    private FirebaseAuth fAuth;
/*    private StorageReference storageRef;
    private FirebaseStorage firebaseStorage;
    private static final int RC_CODE = 457;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        fAuth = FirebaseAuth.getInstance();
/*        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference().child("avatar");*/
        usersList = new ArrayList<>();
        attachUserDR();
        buildRecyclerView();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ListUserActivity.this, StartActivity.class));
                return true;
/*            case R.id.add_avatar:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "choose avatar"), RC_CODE);*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_CODE && resultCode == RESULT_OK){
            Uri uriSelectedImage = data.getData();
            final StorageReference storageReference = storageRef.child(uriSelectedImage.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(uriSelectedImage);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Users user = new Users();
                        user.setUrlAvatar(downloadUri.toString());
                        userRef.push().setValue(user);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }*/

    private void buildRecyclerView() {
        adapter = new UserAdapter(usersList);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.addItemDecoration(new DividerItemDecoration(recyclerViewUsers.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewUsers.setAdapter(adapter);
        adapter.setOnClickUserListener(new UserAdapter.OnClickUserListener() {
            @Override
            public void onClick(int position) {
                goIntent(position);
            }
        });

    }

    private void goIntent(int position) {
        Intent intent = new Intent(ListUserActivity.this, ChatActivity.class);
        intent.putExtra("recipientId", usersList.get(position).getId());
        intent.putExtra("name", usersList.get(position).getName());
        startActivity(intent);
    }

    private void attachUserDR() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users");
        if (usersChild == null) {
            usersChild = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Users user = snapshot.getValue(Users.class);
                    if(!fAuth.getCurrentUser().getUid().equals(user.getId())){
                        user.setImgAvatar(R.drawable.ic_baseline_person_50);
                        usersList.add(user);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            userRef.addChildEventListener(usersChild);
        }
    }

}