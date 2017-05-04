package android.spirithunt.win.controller;

import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.protocol.AuthRegister;
import android.spirithunt.win.provider.SocketProvider;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.socket.client.Ack;
import io.socket.client.Socket;

import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Handles registration of users. Auto sign-in when registration is successful.
 *
 * @author Roelof Roos
 */

public class RegisterController extends AuthorisationController {
    private TextView errorTextView;

    private EditText registerName;
    private EditText registerEmail;
    private EditText registerPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_view);

        errorTextView = (TextView) findViewById(R.id.text_error);
        registerName = (EditText) findViewById(R.id.register_name);
        registerEmail = (EditText) findViewById(R.id.register_email);
        registerPassword = (EditText) findViewById(R.id.register_password);

        // Fix the width of the error message before it gets filled with content, otherwise the
        // layout messes up and goes full-width.
        LinearLayoutCompat layout = (LinearLayoutCompat) errorTextView.getParent();
        errorTextView.setWidth(layout.getWidth());
    }

    public void submitForm(View view) {
        final RegisterController self = this;

        // Validate some fields
        String name = registerName.getText().toString().trim();
        String email = registerEmail.getText().toString().toLowerCase().trim();
        String password = registerPassword.getText().toString();

        EditText target = null;

        showError(null);

        if (name.isEmpty()) {
            showFieldError(registerName, R.string.auth_error_name_empty);
            target = registerName;
        } else if (name.length() < 3) {
            showFieldError(registerName, R.string.auth_error_name_short);
            target = registerName;
        }

        if (email.isEmpty()) {
            showFieldError(registerEmail, R.string.auth_error_email_empty);
            target = target != null ? target : registerEmail;
        } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
            showFieldError(registerEmail, R.string.auth_error_email_invalid);
            target = target != null ? target : registerEmail;
        }

        if (password.isEmpty()) {
            showFieldError(registerPassword, R.string.auth_error_password_empty);
            target = target != null ? target : registerPassword;
        } else if (password.length() < 4) {
            showFieldError(registerPassword, R.string.auth_error_password_short);
            target = target != null ? target : registerPassword;
        }

        if (target != null) {
            target.requestFocus();
            return;
        }

        // Communicate with online server to register account.

        this.showProgressDialog();

        Socket socket = SocketProvider.getInstance().getConnection();

        socket.emit("auth:register", new AuthRegister(name, email, password), new Ack() {
            @Override
            public void call(final Object... args) {
            self.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                if(args[0] == null) {
                    self.saveJwt(args[1].toString());
                    self.hideProgressDialog();
                    self.showMainMenu(self);
                } else {
                    String message = args[0].toString();
                    self.showError(self.getErrorFromDictionary(message));
                    self.hideProgressDialog();
                }
                }
            });
            }
        });
    }

    /**
     * Show that we're doing something
     */
    private void showProgressDialog() {
        showProgressDialog(
            getString(R.string.register_busy_title),
            getString(R.string.register_busy_text)
        );
    }

    private void showFieldError(EditText target, int message) {
        showFieldError(target, getString(message));
    }

    private void showFieldError(EditText target, String message) {
        if (message == null) {
            target.setError("");
        } else {
            target.setError(message);
        }
    }

    /**
     * Shows an error underneath the sign up button. Accepts resource IDs
     *
     * @param messageNumber ID of the error message.
     */
    private void showError(int messageNumber) {
        showError(getString(messageNumber));
    }

    /**
     * Shows an error underneath the sign up button.
     *
     * @param message Message to show.
     */
    private void showError(String message) {
        if (message == null) {
            errorTextView.setVisibility(View.INVISIBLE);
        } else {
            errorTextView.setText(message);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }
}
