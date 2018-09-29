package com.packagesharer.blash.packagesharer;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Login";

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setVisible(false);
        Utility.assetManager = getAssets();

        ImageView front = findViewById(R.id.front);
        ImageView logo = findViewById(R.id.logo);

        front.setScaleType(ImageView.ScaleType.CENTER_CROP);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        try {
            Utility.assetManager = getAssets();
            front.setImageBitmap(BitmapFactory.decodeStream(Utility.assetManager.open("front.jpg")));
            logo.setImageBitmap(BitmapFactory.decodeStream(Utility.assetManager.open("logo.png")));
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), LENGTH_SHORT).show();
        }



        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            updateUI(account);
        }else {
            findViewById(R.id.Blocker).setVisibility(View.INVISIBLE);
        }

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, String.valueOf("signInResult:failed code=" + e.getStatusCode()), LENGTH_SHORT).show();
            updateUI(null);
        }
    }
    private void updateUI(GoogleSignInAccount account) {
        Utility.account = account;
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
