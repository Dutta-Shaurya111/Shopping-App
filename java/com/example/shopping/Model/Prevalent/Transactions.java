package com.example.shopping.Model.Prevalent;

public class Transactions
{
    private String productId,receiverName,amount,transactionStatus,transactionId,
            note,upiId;

    public Transactions(){}

    public Transactions(String productId, String receiverName, String amount, String transactionStatus, String transactionId, String note, String upiId) {
        this.productId = productId;
        this.receiverName = receiverName;
        this.amount = amount;
        this.transactionStatus = transactionStatus;
        this.transactionId = transactionId;
        this.note = note;
        this.upiId = upiId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}
