package net.ddns.dunno.hackathon;

/**
 * Created by Laurentiu on 2/26/2018.
 */

public class Transaction {
    private String transactionID;
    private String price;
    private String status;
    private String pricePerLitre;
    private String litres;
    private String pumpID;

    public Transaction() {
    }

    public String getTransactionID() {
        return transactionID;
    }
    public String getPrice() {
        return price;
    }
    public String getStatus() {
        return status;
    }
    public String getPricePerLitre() {
        return pricePerLitre;
    }
    public String getLitres() {
        return litres;
    }
    public String getPumpID() {
        return pumpID;
    }
    public void setTransactionID(String ID) {
        this.transactionID = ID;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setPricePerLitre(String pricePerLitre) {
        this.pricePerLitre = pricePerLitre;
    }
    public void setLitres(String litres) {
        this.litres = litres;
    }
    public void setPumpID(String pumpID) {
        this.pumpID = pumpID;
    }
}
