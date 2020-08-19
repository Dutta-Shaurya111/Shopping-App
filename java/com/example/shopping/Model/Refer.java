package com.example.shopping.Model;

public class Refer {
    private String code, senderno, receiverno,status;

    public Refer() {


    }

    public Refer(String code, String senderno, String receiverno, String status) {
        this.code = code;
        this.senderno = senderno;
        this.receiverno = receiverno;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSenderno() {
        return senderno;
    }

    public void setSenderno(String senderno) {
        this.senderno = senderno;
    }

    public String getReceiverno() {
        return receiverno;
    }

    public void setReceiverno(String receiverno) {
        this.receiverno = receiverno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}