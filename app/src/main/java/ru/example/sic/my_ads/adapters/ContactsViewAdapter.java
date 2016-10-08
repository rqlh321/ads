package ru.example.sic.my_ads.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

import ru.example.sic.my_ads.R;

import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_COUNTRY_CODE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_IS_VISIBLE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_OF_TYPE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_VALUE;

public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewAdapter.ViewHolder> {

    ArrayList<ParseObject> listParse = new ArrayList<>();

    public ContactsViewAdapter(ArrayList<ParseObject> list) {
        this.listParse = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int color = Color.parseColor("#2196F3"); //The color u want
        switch (listParse.get(position).getInt(USER_CONTACTS_OF_TYPE)) {
            case 0:
                holder.image.setImageResource(R.drawable.home);
                holder.image.setColorFilter(color);
                holder.contactText.setText(listParse.get(position).getString(USER_CONTACTS_VALUE));
                break;
            case 1:
                holder.image.setImageResource(R.drawable.vector_drawable_ic_call_black___px);
                holder.image.setColorFilter(color);
                holder.contactText.setText("(" + listParse.get(position).getString(USER_CONTACTS_COUNTRY_CODE) + ")"
                        + listParse.get(position).getString(USER_CONTACTS_VALUE));
                break;
            case 2:
                holder.image.setImageResource(R.drawable.vector_drawable_ic_email_black___px);
                holder.image.setColorFilter(color);
                holder.contactText.setText(listParse.get(position).getString(USER_CONTACTS_VALUE));
                break;
            default: {
                holder.image.setImageResource(R.drawable.vector_drawable_ic_link_black___px);
                holder.image.setColorFilter(color);
                holder.contactText.setText(listParse.get(position).getString(USER_CONTACTS_VALUE));
                break;
            }
        }
        holder.contactActivity.setChecked(listParse.get(position).getBoolean(USER_CONTACTS_IS_VISIBLE));
        holder.contactActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listParse.get(position).put(USER_CONTACTS_IS_VISIBLE, holder.contactActivity.isChecked());
                listParse.get(position).saveInBackground();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listParse.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView contactText;
        public final ImageView image;
        public final Switch contactActivity;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.image);
            contactText = (TextView) view.findViewById(R.id.contact_text);
            contactActivity = (Switch) view.findViewById(R.id.contact_activity);
        }
    }
}