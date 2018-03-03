package net.ddns.dunno.hackathon;

/**
 * Created by Laurentiu on 2/26/2018.
 */

public class Transaction {
    private String UID;
    private String price;
    private String date;
    private String time;
    private String status;

    public Transaction() {
    }

    public String getPrice() {
        return price;
    }
    public String getUID() {return UID;}
    public void setPrice(String price) {
        this.price = price;
    }
    public void setUID(String UID){ this.UID = UID;}
}
