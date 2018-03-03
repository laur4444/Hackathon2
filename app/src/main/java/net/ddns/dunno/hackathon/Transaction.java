package net.ddns.dunno.hackathon;

/**
 * Created by Laurentiu on 2/26/2018.
 */

public class Transaction {
    private String UID = "Error";
    private String price = "Error";
    private String date = "Error";
    private String status1 = "Error";

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
    public String getDate(){
        return date;
    }
    public String getStatus1(){
        return status1;
    }

}
