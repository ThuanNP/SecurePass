package com.thuannp.securepass.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;
import com.thuannp.securepass.R;
import com.thuannp.securepass.models.CredModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import ir.androidexception.roomdatabasebackupandrestore.AESUtils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.viewHolder> {
    private static final String PREFS_NAME = "appEssentials";
    private List<CredModel> credList = new ArrayList<>();
    private onItemClickListener listener;
    MasterKey masterKey = null;
    SharedPreferences sharedPreferences = null;
    String sha;

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items, parent, false);
        Context context = parent.getContext();
        try {
            //x.security
            masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            //init sharedPef
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        sha = sharedPreferences.getString("HASH", "0");
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CredModel cred = credList.get(position);
        holder.provider.setText(cred.getProviderName());
        switch (cred.getProviderName()) {
            case "Amazon":
                holder.providerImage.setImageResource(R.drawable.amazon);
                break;
            case "Apple":
                holder.providerImage.setImageResource(R.drawable.apple);
                break;
            case "Facebook":
                holder.providerImage.setImageResource(R.drawable.facebook);
                break;
            case "Flickr":
                holder.providerImage.setImageResource(R.drawable.flickr);
                break;
            case "Foursquare":
                holder.providerImage.setImageResource(R.drawable.foursquare);
                break;
            case "Github":
                holder.providerImage.setImageResource(R.drawable.github);
                break;
            case "Gmail":
                holder.providerImage.setImageResource(R.drawable.google);
                break;
            case "Instagram":
                holder.providerImage.setImageResource(R.drawable.instagram);
                break;
            case "Linkedin":
                holder.providerImage.setImageResource(R.drawable.linkedin);
                break;
            case "Medium":
                holder.providerImage.setImageResource(R.drawable.medium);
                break;
            case "Paypal":
                holder.providerImage.setImageResource(R.drawable.paypal);
                break;
            case "Pinterest":
                holder.providerImage.setImageResource(R.drawable.pinterest);
                break;
            case "Reddit":
                holder.providerImage.setImageResource(R.drawable.reddit);
                break;
            case "Skype":
                holder.providerImage.setImageResource(R.drawable.skype);
                break;
            case "Slack":
                holder.providerImage.setImageResource(R.drawable.slack);
                break;
            case "Snapchat":
                holder.providerImage.setImageResource(R.drawable.snapchat);
                break;
            case "Spotify":
                holder.providerImage.setImageResource(R.drawable.spotify);
                break;
            case "Stackoverflow":
                holder.providerImage.setImageResource(R.drawable.stackoverflow);
                break;
            case "Tinder":
                holder.providerImage.setImageResource(R.drawable.tinder);
                break;
            case "Trello":
                holder.providerImage.setImageResource(R.drawable.trello);
                break;
            case "Tumblr":
                holder.providerImage.setImageResource(R.drawable.tumblr);
                break;
            case "Twitter":
                holder.providerImage.setImageResource(R.drawable.twitter);
                break;
            case "Wordpress":
                holder.providerImage.setImageResource(R.drawable.wordpress);
                break;
            case "Yahoo":
                holder.providerImage.setImageResource(R.drawable.yahoo);
                break;
            case "Outlook":
                holder.providerImage.setImageResource(R.drawable.outlook);
                break;
            default:
                holder.providerImage.setImageResource(R.drawable.keys);
                break;
        }
        try {
            String keyValue = Hasher.Companion.hash(sha, HashType.MD5);
            String dec = cred.getEmail();
            String decEmail = AESUtils.decrypt(dec, keyValue);
            holder.cat1.setText(decEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return credList.size();
    }

    public void setCred(List<CredModel> credList) {
        this.credList = credList;
        notifyDataSetChanged();
    }

    public CredModel getCredAt(int position) {
        return credList.get(position);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView provider, cat1;
        private ImageView providerImage;

        public viewHolder(@NonNull View view) {
            super(view);
            providerImage = view.findViewById(R.id.image);
            provider = view.findViewById(R.id.provider);
            //Email field
            cat1 = view.findViewById(R.id.imp_cat);
            view.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(credList.get(pos));
                }
            });
            view.setOnLongClickListener(v -> {
                Log.d("OnLongClick", "Long Click");
                return false;
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(CredModel cred);
    }
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
