package com.sungfamilyadmin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sungfamilyadmin.events.ChatMessageReceivedEvent;
import com.sungfamilyadmin.models.ChatMessage;
import com.sungfamilyadmin.models.HttpCourierResponse;
import com.sungfamilyadmin.models.User;
import com.sungfamilyadmin.rest.RemoteService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ChatMessageAdapter mAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getIntent() != null) {
            Bundle arg = getIntent().getExtras();
            if (arg.containsKey("user")) {
                user = (User) arg.getSerializable("user");
            }
        }

        SungFamilyAdminApp.getApp().setCurrentScreen(this.getClass().getSimpleName());

        mRecyclerView = findViewById(R.id.recyclerView);
        mButtonSend = findViewById(R.id.btn_send);
        mEditTextMessage = findViewById(R.id.et_message);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mRecyclerView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(user.getFullName() + ": " + message);
                mEditTextMessage.setText("");
            }
        });

        if (getIntent() != null) {
            Bundle arg = getIntent().getExtras();
            if (arg != null && arg.containsKey("chat_message")) {
                ChatMessage chatMessage = (ChatMessage) arg.getSerializable("chat_message");
                if (chatMessage != null) {
                    updateReceivedMessage(chatMessage);
                }
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        SungFamilyAdminApp.getApp().setCurrentScreen("ItemListActivity");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChatMessageReceivedEvent e) {
        if (e != null) {
            if (e.getChatMessage() != null) {
                ChatMessage c = e.getChatMessage();
                updateReceivedMessage(c);
            }
        }
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        RemoteService remoteService = RemoteService.Factory.getInstance(ChatActivity.this);
        remoteService.sendFirebaseMessage(user.getId(), message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<HttpCourierResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<HttpCourierResponse> response) {
                        if (response.isSuccessful()) {
                            //do nothing...

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateReceivedMessage(ChatMessage message) {
        //ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(message);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
}
