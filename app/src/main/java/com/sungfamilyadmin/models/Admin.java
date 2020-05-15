package com.sungfamilyadmin.models;

import com.sungfamilyadmin.Utils;
import com.sungfamilyadmin.constants.Constant;

import java.util.ArrayList;
import java.util.List;

public class Admin
{
    private static Admin instance;

    public static Admin getInstance()
    {
        synchronized (Admin.class)
        {
            if(instance == null)
                instance = new Admin();

            return instance;
        }
    }

    private List<Review> reviews;

    public void addReview(Review review)
    {
        if(reviews != null)
        {
            reviews.add(0, review);
        }
    }

    public void setReviews(List<Review> reviews)
    {
        this.reviews = reviews;
    }

    public List<Review> getReviews()
    {
        return this.reviews;
    }

    public void generateDummyReviews()
    {
        reviews = new ArrayList<>();
        for(int i = 0; i < 25; i++)
        {
            Review r = new Review();
            r.setUserName(Utils.getRandomUserName());
            r.setRecipientFbToken(Constant.RECIPIENT_FB_TOKEN);
            r.setReviewedAt(Utils.getFormattedDateTime(System.currentTimeMillis()));
            r.setRating(Utils.getRandomNumber(1, 5));
            r.setComment(Constant.SAMPLE_REVIEWS[Utils.getRandomNumber(0, Constant.SAMPLE_REVIEWS.length - 1)]);
            reviews.add(r);
        }
    }
}
