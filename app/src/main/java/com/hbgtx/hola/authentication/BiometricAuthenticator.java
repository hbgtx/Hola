package com.hbgtx.hola.authentication;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

// TODO: Add method to find out biometric authentication is available or not.
public class BiometricAuthenticator {

    private final BiometricPrompt biometricPrompt;
    private final BiometricPrompt.PromptInfo promptInfo;

    public BiometricAuthenticator(Context context) {
        Executor executor = ContextCompat.getMainExecutor(context);
        biometricPrompt = new BiometricPrompt((AppCompatActivity) context, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(context, "Authentication error:" + errString,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(context,
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(context, "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();
    }

    public void authenticate() {
        biometricPrompt.authenticate(promptInfo);
    }
}
