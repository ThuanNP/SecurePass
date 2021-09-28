package com.thuannp.securepass.ui.wifi;

import static android.app.Activity.RESULT_OK;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thuannp.securepass.AddActivity;
import com.thuannp.securepass.ModifyActivity;
import com.thuannp.securepass.R;
import com.thuannp.securepass.adapters.RecyclerViewAdapter;
import com.thuannp.securepass.models.CredModel;
import com.thuannp.securepass.utils.DefConstant;

import java.util.List;

public class WifiFragment extends Fragment {
    private static final String PROVIDER = "wifi";
    private WifiViewModel mViewModel;
    private TextView empty;
    boolean status = false;
    private RecyclerViewAdapter viewAdapter;

    public static WifiFragment newInstance() {
        return new WifiFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.password_fragment, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        empty = root.findViewById(R.id.empty);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(PROVIDER, Context.MODE_PRIVATE);
        SharedPreferences sp = this.getActivity().getSharedPreferences(DefConstant.PREF_NAME, Context.MODE_PRIVATE);
        if (sp.getBoolean(DefConstant.PREF_KEY_SECURE_CORE_MODE, false)) {
            try {
                ImageButton copyImage = root.findViewById(R.id.copy);
                copyImage.setEnabled(false);
            } catch (Exception e) {
                e.getStackTrace();
            }
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        status = sharedPreferences.getBoolean(DefConstant.NO_DATA, false);
        if (status) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setText(R.string.empty_list_text);
        }
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        viewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(viewAdapter);

        mViewModel = new ViewModelProvider(this).get(WifiViewModel.class);
        mViewModel.getAllWifi().observe(getViewLifecycleOwner(), new Observer<List<CredModel>>() {
            @Override
            public void onChanged(List<CredModel> credModels) {
                viewAdapter.setCred(credModels);
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.confirm_title)
                        .setMessage(R.string.confirm_delete_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm_possible_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                mViewModel.delete(viewAdapter.getCredAt(viewHolder.getAdapterPosition()));
                                viewAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        viewAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        viewAdapter.setOnItemClickListener(viyCred -> {
            Intent intent = new Intent(getActivity(), ModifyActivity.class);
            intent.putExtra(DefConstant.EXTRA_ID, viyCred.getId());
            intent.putExtra(DefConstant.EXTRA_PROVIDER_NAME, viyCred.getProviderName());
            intent.putExtra(DefConstant.EXTRA_EMAIL, viyCred.getEmail());
            intent.putExtra(DefConstant.EXTRA_ENCRYPT, viyCred.getCat());
            startActivityForResult(intent, DefConstant.MODIFY_RECORD);
        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddActivity.class);
            intent.putExtra(DefConstant.EXTRA_PROVIDER, PROVIDER);
            startActivityForResult(intent, DefConstant.ADD_RECORD);

        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DefConstant.ADD_RECORD && resultCode == RESULT_OK) {
            String providerName = data.getStringExtra(DefConstant.EXTRA_PROVIDER_NAME);
            String enc_passwd = data.getStringExtra(DefConstant.EXTRA_ENCRYPT);
            String enc_email = data.getStringExtra(DefConstant.EXTRA_EMAIL);
            CredModel credModel = new CredModel(PROVIDER, providerName, enc_email, enc_passwd);
            //For showing "No data" or not on activity if the list is empty
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(PROVIDER, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(DefConstant.NO_DATA, true).apply();
            empty.setVisibility(View.GONE);
            mViewModel.insert(credModel);
            Toast.makeText(getContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();
        } else if (requestCode == DefConstant.MODIFY_RECORD && resultCode == RESULT_OK) {
            int id = data.getIntExtra(DefConstant.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getContext(), "Cannot be updated!", Toast.LENGTH_LONG).show();
                return;
            }
            String providerName = data.getStringExtra(DefConstant.EXTRA_PROVIDER_NAME);
            String enc_passwd = data.getStringExtra(DefConstant.EXTRA_ENCRYPT);
            String enc_email = data.getStringExtra(DefConstant.EXTRA_EMAIL);
            CredModel credModel = new CredModel(PROVIDER, providerName, enc_email, enc_passwd);
            //IMP
            credModel.setId(id);
            if (!data.getBooleanExtra(DefConstant.EXTRA_DELETE, false)) {
                // Log.d(TAG, "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd);
                mViewModel.update(credModel);
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.confirm_title)
                        .setMessage(R.string.confirm_delete_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm_possible_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                mViewModel.delete(credModel);
                                viewAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        viewAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}