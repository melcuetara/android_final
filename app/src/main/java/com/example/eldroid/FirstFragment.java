package com.example.eldroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eldroid.adapter.AccountsAdapter;
import com.example.eldroid.databinding.ActivityMainBinding;
import com.example.eldroid.databinding.FragmentFirstBinding;
import com.example.eldroid.model.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private FragmentFirstBinding binding;
    private AccountsAdapter accountsAdapter;
    private ArrayList<Account> accounts = new ArrayList<>();;

    private AccountsAdapter.RecyclerViewClickListener listener;

    private String userId;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child(userId);

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvAccountsList.setLayoutManager(new LinearLayoutManager(getContext()));
        populateRecyclerView();
        setOnClickListener();

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateRecyclerView(binding.etSearchName.getText().toString());
            }
        });

    }

    private void setOnClickListener() {
        listener = new AccountsAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getContext(), AccountActivity.class);
                intent.putExtra("firstName", accounts.get(position).getFirstName());
                intent.putExtra("lastName", accounts.get(position).getLastName());
                intent.putExtra("key", accounts.get(position).getKey());
                intent.putExtra("age", accounts.get(position).getAge());
                intent.putExtra("sex", accounts.get(position).getSex());
                intent.putExtra("imageUrl", accounts.get(position).getImageUrl());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View v, int position) {
                DatabaseReference delete = reference.child(accounts.get(position).getKey());
                delete.removeValue();
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateRecyclerView(String search) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accounts.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Account account = dataSnapshot.getValue(Account.class);
                        if (account.getLastName().equals(search)) {
                            accounts.add(account);
                        }
                    }
                    if (accounts.size() == 0) {
                        Toast.makeText(getContext(), "No Results Found", Toast.LENGTH_SHORT).show();
                    }
                    accountsAdapter = new AccountsAdapter(getContext(), accounts, listener);
                    binding.rvAccountsList.setAdapter(accountsAdapter);
                    accountsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Add Students!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateRecyclerView() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accounts.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Account account = dataSnapshot.getValue(Account.class);
                        accounts.add(account);
                    }
                } else {
                    Toast.makeText(getContext(), "Add Students!", Toast.LENGTH_SHORT).show();
                }
                accountsAdapter = new AccountsAdapter(getContext(), accounts, listener);
                binding.rvAccountsList.setAdapter(accountsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}