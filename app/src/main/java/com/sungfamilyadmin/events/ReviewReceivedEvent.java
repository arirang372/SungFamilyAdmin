package com.sungfamilyadmin.events;

import com.sungfamilyadmin.models.Review;

public class ReviewReceivedEvent
{
    private Review review;
    public ReviewReceivedEvent(Review r)
    {
        review = r;
    }

    public Review getReview() {
        return review;
    }
}
