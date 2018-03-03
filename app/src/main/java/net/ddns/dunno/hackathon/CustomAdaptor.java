package net.ddns.dunno.hackathon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Laurentiu on 2/27/2018.
 */

public class CustomAdaptor extends BaseAdapter{
    Context c;
    ArrayList<Transaction> transactions;

    public CustomAdaptor(Context c, ArrayList<Transaction> transactions){
        this.c = c;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(c).inflate(R.layout.item_transaction, parent, false);
        }

        TextView textViewID = convertView.findViewById(R.id.textViewID);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);

        final Transaction s = (Transaction) this.getItem(position);

        textViewID.setText(s.getUID().substring(0, 5) + "         ");
        textViewPrice.setText(s.getPrice() + " lei");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, s.getUID(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
