package ru.example.sic.my_ads.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.ViewHolder> {
    private ArrayList<ParseObject> contacts;
    private Context context;

    public UserViewAdapter(Context context, ArrayList<ParseObject> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserViewAdapter.ViewHolder holder, final int position) {
        int color = Color.parseColor("#2196F3"); //The color u want
        switch (contacts.get(position).getInt(Parse.Constants.USER_CONTACTS_OF_TYPE)) {
            case 0:
                holder.mImageView.setImageResource(R.drawable.home);
                holder.mImageView.setColorFilter(color);
                holder.mTextView.setText(contacts.get(position).getString(Parse.Constants.USER_CONTACTS_VALUE));
                break;
            case 1:
                holder.mImageView.setImageResource(R.drawable.vector_drawable_ic_call_black___px);
                holder.mImageView.setColorFilter(color);
                holder.mTextView.setText(contacts.get(position).getString(Parse.Constants.USER_CONTACTS_COUNTRY_CODE) +
                        contacts.get(position).getString(Parse.Constants.USER_CONTACTS_VALUE));
                break;
            case 2:
                holder.mImageView.setImageResource(R.drawable.vector_drawable_ic_email_black___px);
                holder.mImageView.setColorFilter(color);
                holder.mTextView.setText(contacts.get(position).getString(Parse.Constants.USER_CONTACTS_VALUE));
                break;
            default:
                holder.mTextView.setText(contacts.get(position).getString(Parse.Constants.USER_CONTACTS_VALUE));
                holder.mTextView.setTextColor(Color.BLUE);
                holder.mImageView.setImageResource(R.drawable.vector_drawable_ic_link_black___px);
                holder.mImageView.setColorFilter(color);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uriUrl = Uri.parse(holder.mTextView.getText().toString());
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        context.startActivity(launchBrowser);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            mTextView = (TextView) view.findViewById(R.id.text1);
        }

    }
}