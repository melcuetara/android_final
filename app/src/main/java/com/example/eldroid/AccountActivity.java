package com.example.eldroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.eldroid.databinding.ActivityAccountBinding;
import com.example.eldroid.model.Account;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityAccountBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser user;

    private Account account;

    private String userId;
    private String firstName;
    private String lastName;
    private String strAge;
    private String sex;
    private String stringUri;
    private String key = "";
    private int age;
    private Uri imageUri;
    boolean isImageUpdated = false;

    public AccountActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            binding.etFirstName.setText(bundle.getString("firstName"));
            binding.etLastName.setText(bundle.getString("lastName"));
            binding.etAge.setText(""+ bundle.getInt("age"));
            binding.etSex.setText(bundle.getString("sex"));
            Picasso.get().load(bundle.getString("imageUrl")).into(binding.ivProfileImage);
            imageUri = Uri.parse(bundle.getString("imageUrl"));
            key = bundle.getString("key");
            Toast.makeText(this, "key" + key, Toast.LENGTH_SHORT).show();
        }

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstName = binding.etFirstName.getText().toString();
                lastName = binding.etLastName.getText().toString();
                strAge =binding.etAge.getText().toString();
                sex = binding.etSex.getText().toString();

                if (firstName.isEmpty() || lastName.isEmpty() || strAge.isEmpty() || sex.isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Fill In Empty Fields", Toast.LENGTH_SHORT).show();
                } else if (imageUri == null) {
                    Toast.makeText(AccountActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                } else {
                    age = Integer.parseInt(strAge);
                    account = new Account(firstName, lastName, age, stringUri, sex);
                    uploadFile();
                }
            }
        });

        binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoose();
            }
        });

    }

    private void openFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.ivProfileImage.setImageURI(imageUri);
            isImageUpdated = true;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (!isImageUpdated) {
            Toast.makeText(this, "Please Select New Image When Updating", Toast.LENGTH_SHORT).show();
            return;
        }
        StorageReference fileReference = firebaseStorage.getReference("AccountImages")
                .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AccountActivity.this, "Upload Success!", Toast.LENGTH_SHORT).show();
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                account.setImageUrl(uri.toString());
                                if (key.equals("")) {
                                    key = reference.push().getKey();
                                }
                                account.setKey(key);
                                reference.child(userId).child(key).setValue(account);
                                finish();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}