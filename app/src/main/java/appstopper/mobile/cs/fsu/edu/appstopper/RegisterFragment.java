package appstopper.mobile.cs.fsu.edu.appstopper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    private DatabaseReference mDatabase;
    private Button registerButton;
    protected EditText registerEmail, registerPassword, registerPasswordConfirm, registerName;
    private FirebaseAuth mAuth;
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        // Firebase database and authentication
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        registerButton = root.findViewById(R.id.register_button);
        registerEmail = root.findViewById(R.id.register_email);
        registerPassword = root.findViewById(R.id.register_password);
        registerPasswordConfirm = root.findViewById(R.id.register_password_confirm);
        registerName = root.findViewById(R.id.register_display_name);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registerPassword.getText().toString().equals(registerPasswordConfirm.getText().toString()))
                {
                    createWithEmail(registerEmail.getText().toString(), registerPassword.getText()
                            .toString(), registerName.getText().toString());
                }
                else {  // Passwords do not match
                    Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    registerPassword.setText("");
                    registerPasswordConfirm.setText("");
                }
            }
        });

        return root;
    }

    public void createWithEmail(String email, final String password, String displayName) {
        final String name = displayName;
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser user = mAuth.getCurrentUser();

                        // Database logic begins here
                        Map<String, Object> userObj = new HashMap<>();
                        userObj.put("name", name);
                        userObj.put("email", user.getEmail());
                        userObj.put("uid", user.getUid());
                        userObj.put("devices", "none");
                        mDatabase.child("users").child(user.getUid()).setValue(userObj);
                        Log.v(TAG, "userId : " + userObj.get("uid"));
                        Toast.makeText(getActivity(), "Authentication Succeeded.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(), "Error, try again",
                                Toast.LENGTH_SHORT).show();
                        registerEmail.setText("");
                        registerPassword.setText("");
                        registerPasswordConfirm.setText("");
                    }
                }
            });

    }

}
