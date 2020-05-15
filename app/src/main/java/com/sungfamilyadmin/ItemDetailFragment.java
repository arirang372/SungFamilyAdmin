package com.sungfamilyadmin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.sungfamilyadmin.models.Review;
import org.greenrobot.eventbus.EventBus;


public class ItemDetailFragment extends Fragment
{
    private Review review;

    public ItemDetailFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        if(arg != null && arg.containsKey("review"))
        {
            review = (Review) arg.getSerializable("review");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        return rootView;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        setupUI(view);
    }

    private void setupUI(View view)
    {
        TextView txtUserName = view.findViewById(R.id.txtUserName);
        txtUserName.setText(review.getUserName());
        TextView txtReviewedAt = view.findViewById(R.id.txtReviewedAt);
        txtReviewedAt.setText(review.getReviewedAt());
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setRating(review.getRating());
        TextView txtComment = view.findViewById(R.id.txtComment);
        txtComment.setText(review.getComment());
    }
}
