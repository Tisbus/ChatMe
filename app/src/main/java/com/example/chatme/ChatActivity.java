package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private Button buttonSend;
    private MessageAdapter adapter;
    private List<ChatMe> list;
    private ProgressBar progressBar;
    private String userName;
    private ImageButton imageButton;
    private FirebaseAuth currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private ChildEventListener messagesChild;
    private DatabaseReference userRef;
    private ChildEventListener usersChild;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private final static int RC_CODE = 457;
    private String recipientId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.recyclerViewChat);
        editText = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.sendMessage);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        imageButton = findViewById(R.id.insertPhoto);
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("chat_images");
        myRef = firebaseDatabase.getReference().child("message");
        userRef = firebaseDatabase.getReference().child("users");
/*        if(currentUser.getCurrentUser() != null){
            userName =  currentUser.getCurrentUser().getDisplayName();
        }else{
            userName = "defaultUser";
        }*/
        recipientId = getIntent().getStringExtra("recipientId");
        if(getIntent().hasExtra("name")){
            userName =  getIntent().getStringExtra("name") + " - " + currentUser.getCurrentUser().getEmail();
        }

        list = new ArrayList<>();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() > 0){
                    buttonSend.setEnabled(true);
                }else{
                    buttonSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.push().setValue(new ChatMe(String.valueOf(editText.getText()), userName, null, recipientId, currentUser.getCurrentUser().getUid(), true));
                editText.setText("");
                recyclerView.scrollBy(0, 1000000000);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_CODE);
            }
        });
        messagesChild = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMe chatMe = snapshot.getValue(ChatMe.class);
                if(chatMe.getSender().equals(currentUser.getCurrentUser().getUid()) && chatMe.getRecipient().equals(recipientId))
                {
                    chatMe.setMine(true);
                    list.add(chatMe);
                    adapter.setChatMeList(list);
                    adapter.notifyDataSetChanged();

                }else if(chatMe.getRecipient().equals(currentUser.getCurrentUser().getUid()) && chatMe.getSender().equals(recipientId)){
                    chatMe.setMine(false);
                    list.add(chatMe);
                    adapter.setChatMeList(list);
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
        myRef.addChildEventListener(messagesChild);
        usersChild = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Users user = snapshot.getValue(Users.class);
                if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    userName = user.getName() + " - " + currentUser.getCurrentUser().getEmail();
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
        adapter = new MessageAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
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
                        ChatMe chatMe = new ChatMe();
                        chatMe.setImageUrl(downloadUri.toString());
                        chatMe.setName(userName);
                        chatMe.setRecipient(recipientId);
                        chatMe.setSender(currentUser.getCurrentUser().getUid());
                        myRef.push().setValue(chatMe);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
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
                startActivity(new Intent(ChatActivity.this, StartActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}