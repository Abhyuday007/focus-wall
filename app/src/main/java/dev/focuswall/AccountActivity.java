package dev.focuswall;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executors;

/** Optional identity only. Focus Wall does not upload settings or activity data from this screen. */
public class AccountActivity extends Activity {
    private FirebaseAuth auth;
    private CredentialManager credentialManager;
    private TextView accountCopy;
    private Button accountAction;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        auth = FirebaseApp.initializeApp(this) == null ? null : FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(28), dp(42), dp(28), dp(28)); page.setBackgroundColor(getColor(R.color.paper));
        page.addView(label("ACCOUNT")); page.addView(gap(18)); page.addView(title("Private by default.")); page.addView(gap(12)); page.addView(copy("Focus Wall works completely on-device. An account is optional and never changes your reel budget.")); page.addView(gap(28));
        LinearLayout accountCard = card(); accountCard.addView(label("OPTIONAL GOOGLE ACCOUNT")); accountCard.addView(gap(8)); accountCopy = copy(""); accountCard.addView(accountCopy); accountCard.addView(gap(18)); accountAction = button("", getColor(R.color.mint_deep), getColor(R.color.on_mint)); accountCard.addView(accountAction); page.addView(accountCard); page.addView(gap(16));
        LinearLayout backupCard = card(); backupCard.addView(label("YOUR DATA")); backupCard.addView(gap(8)); backupCard.addView(copy("No settings, screen activity, or social-media data is uploaded. Cloud backup will be offered only after it has a clear, encrypted, per-account design.")); page.addView(backupCard); page.addView(gap(16));
        Button done = button("Done", getColor(R.color.surface_tint), getColor(R.color.ink)); done.setOnClickListener(v -> finish()); page.addView(done);
        setContentView(page);
        renderAccount();
    }

    @Override public void onStart() { super.onStart(); if (auth != null) renderAccount(); }

    private void renderAccount() {
        if (auth == null) {
            accountCopy.setText("Google sign-in is not configured in this build. Focus Wall remains fully local and usable without an account.");
            accountAction.setVisibility(View.GONE);
            return;
        }
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            accountCopy.setText("Sign in only if you want a portable Focus Wall identity later. We use Google’s secure account picker—Focus Wall never sees or stores a password.");
            accountAction.setText("Continue with Google"); accountAction.setEnabled(true); accountAction.setOnClickListener(v -> beginGoogleSignIn());
        } else {
            String name = user.getDisplayName();
            accountCopy.setText("Connected as " + (name == null || name.isEmpty() ? "your Google account" : name) + ". Your Focus Wall settings still stay on this phone.");
            accountAction.setText("Sign out on this phone"); accountAction.setEnabled(true); accountAction.setOnClickListener(v -> signOut());
        }
    }

    private void beginGoogleSignIn() {
        int clientIdResource = getResources().getIdentifier("default_web_client_id", "string", getPackageName());
        if (clientIdResource == 0) {
            showSignInError("Google sign-in is not enabled in this Firebase project yet. Enable the Google provider, add this app’s SHA-1, then replace google-services.json.");
            return;
        }
        accountAction.setEnabled(false); accountAction.setText("Opening Google…");
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setServerClientId(getString(clientIdResource)).build();
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();
        credentialManager.getCredentialAsync(this, request, new CancellationSignal(), Executors.newSingleThreadExecutor(), new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override public void onResult(@NonNull GetCredentialResponse result) { runOnUiThread(() -> handleCredential(result.getCredential())); }
            @Override public void onError(@NonNull GetCredentialException error) { runOnUiThread(() -> showSignInError("Google sign-in was cancelled or is not ready on this device.")); }
        });
    }

    private void handleCredential(Credential credential) {
        if (credential instanceof CustomCredential && GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
            try {
                GoogleIdTokenCredential google = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(google.getIdToken(), null);
                auth.signInWithCredential(firebaseCredential).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) renderAccount();
                    else showSignInError("Couldn’t finish Google sign-in. Check that Google is enabled in Firebase and this app’s SHA-1 is registered.");
                });
            } catch (Exception error) { showSignInError("Google returned an unusable account response. Please try again."); }
        } else showSignInError("Google didn’t return an account credential. Please try again.");
    }

    private void signOut() {
        auth.signOut();
        credentialManager.clearCredentialStateAsync(new androidx.credentials.ClearCredentialStateRequest(), new CancellationSignal(), Executors.newSingleThreadExecutor(), new CredentialManagerCallback<Void, ClearCredentialException>() {
            @Override public void onResult(@NonNull Void ignored) { runOnUiThread(AccountActivity.this::renderAccount); }
            @Override public void onError(@NonNull ClearCredentialException error) { runOnUiThread(AccountActivity.this::renderAccount); }
        });
    }

    private void showSignInError(String message) { accountCopy.setText(message); accountAction.setText("Try Google sign-in again"); accountAction.setEnabled(true); accountAction.setOnClickListener(v -> beginGoogleSignIn()); }
    private LinearLayout card() { LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(20), dp(20), dp(20), dp(20)); GradientDrawable background = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{getColor(R.color.surface), getColor(R.color.surface_tint)}); background.setCornerRadius(dp(24)); card.setBackground(background); return card; }
    private Button button(String text, int color, int textColor) { Button button = new Button(this); button.setText(text); button.setAllCaps(false); button.setTextSize(16); button.setTextColor(textColor); button.setGravity(Gravity.CENTER); GradientDrawable background = new GradientDrawable(); background.setColor(color); background.setCornerRadius(dp(18)); button.setBackground(background); button.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(56))); return button; }
    private TextView label(String text) { TextView view = copy(text); view.setTextSize(12); view.setLetterSpacing(.13f); view.setTextColor(getColor(R.color.mint)); return view; }
    private TextView title(String text) { TextView view = copy(text); view.setTextSize(34); return view; }
    private TextView copy(String text) { TextView view = new TextView(this); view.setText(text); view.setTextSize(17); view.setTextColor(getColor(R.color.ink)); view.setLineSpacing(dp(5), 1f); return view; }
    private View gap(int size) { View view = new View(this); view.setLayoutParams(new LinearLayout.LayoutParams(1, dp(size))); return view; }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density); }
}
