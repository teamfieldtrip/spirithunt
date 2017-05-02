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
 * @author Roelof Roos
 */

public class RegisterController extends AuthorisationController {
    private TextView errorTextView;

    private TextView registerName;
    private TextView registerEmail;
    private TextView registerPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_view);

        errorTextView = (TextView) findViewById(R.id.text_error);
        registerName = (EditText) findViewById(R.id.register_name);
        registerEmail = (EditText) findViewById(R.id.register_email);
        registerPassword = (EditText) findViewById(R.id.register_password);

        LinearLayoutCompat layout = (LinearLayoutCompat) errorTextView.getParent();
        errorTextView.setWidth(layout.getWidth());
    }

    public void submitForm(View view) {
        final RegisterController self = this;

        // Validate some fields
        String name = registerName.getText().toString().trim();
        String email = registerEmail.getText().toString().toLowerCase().trim();
        String password = registerPassword.getText().toString();

        showError(null);

        if (name.isEmpty()) {
            showError(R.string.register_error_name_empty);
            registerName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showError(R.string.register_error_email_empty);
            registerEmail.requestFocus();
            return;
        }

        if (!EMAIL_ADDRESS.matcher(email).matches()) {
            showError(R.string.register_error_email_invalid);
            registerEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError(R.string.register_error_password_empty);
            registerPassword.requestFocus();
            return;
        }

        if (password.length() < 4) {
            showError(R.string.register_error_password_short);
            registerPassword.requestFocus();
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
                        self.hideProgressDialog();

                        if(args[0] == null) {
                            self.saveJwt(args[1].toString());
                            self.showMainMenu(self);
                        } else {
                            String message = args[0].toString();
                            if (message == "email-taken") {
                                self.showError(R.string.register_error_email_taken);
                            } else {
                                self.showError(message);
                            }
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
