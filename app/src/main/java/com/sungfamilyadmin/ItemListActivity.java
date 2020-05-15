package com.sungfamilyadmin;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sungfamilyadmin.events.ChatMessageArrivedEvent;
import com.sungfamilyadmin.events.ChatMessageReceivedEvent;
import com.sungfamilyadmin.events.ReviewReceivedEvent;
import com.sungfamilyadmin.models.Admin;
import com.sungfamilyadmin.models.Review;
import com.sungfamilyadmin.models.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ItemListActivity extends AppCompatActivity
{
    private boolean mTwoPane;
    private SimpleItemRecyclerViewAdapter adapter;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        if(getIntent() != null){
            user = (User) getIntent().getSerializableExtra("user");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        SungFamilyAdminApp.getApp().setCurrentScreen(this.getClass().getSimpleName());
        Admin.getInstance().generateDummyReviews();

        if (findViewById(R.id.item_detail_container) != null)
        {
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReviewReceivedEvent e)
    {
        if(e != null)
        {
            Review r = e.getReview();
            if(r != null)
            {
                adapter.notifyItemInserted(0);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final ChatMessageArrivedEvent e)
    {
        if(e != null)
        {
            //open up alert dialog
            new MaterialDialog.Builder(this)
                    .title("Confirm")
                    .content("Someone has sent a message. Would you like to check out the message?")
                    .positiveText("OK")
                    .negativeText("CANCEL")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                        {
                            Intent intent = new Intent(ItemListActivity.this, ChatActivity.class);
                            Bundle arg = new Bundle();
                            arg.putSerializable("user", user);
                            arg.putSerializable("chat_message", e.getChatMessage());
                            intent.putExtras(arg);
                            startActivity(intent);

                            dialog.dismiss();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback()
                    {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .cancelable(false)
                    .show();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        adapter = new SimpleItemRecyclerViewAdapter(this, Admin.getInstance().getReviews(), mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<Review> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Review item = (Review) view.getTag();
                if (mTwoPane)
                {
                    Bundle arguments = new Bundle();
                    arguments.putSerializable("review", item);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<Review>  items,
                                      boolean twoPane)
        {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position)
        {
            Review r = mValues.get(position);
            if(r != null)
            {
                holder.txtUserName.setText(r.getUserName());
                holder.txtReviewedAt.setText(r.getReviewedAt());
                holder.itemView.setTag(r);

            }
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            final TextView txtUserName;
            final TextView txtReviewedAt;

            ViewHolder(View view)
            {
                super(view);
                txtUserName = view.findViewById(R.id.txtUserName);
                txtReviewedAt = view.findViewById(R.id.txtReviewedAt);
            }
        }
    }
}
