package com.example.shopping.Model;

public class Reviews {
    private String review,name;
    private String rating;

    public Reviews(String review, String name, String rating) {
        this.review = review;
        this.name = name;
        this.rating = rating;
    }

    public Reviews(){}

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }
}
