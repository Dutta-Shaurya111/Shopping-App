package com.example.shopping.Model;

public class Faqs
{
    private String ques,ans;

    public Faqs() {

    }

    public Faqs(String ques, String ans) {
        this.ques = ques;
        this.ans = ans;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
