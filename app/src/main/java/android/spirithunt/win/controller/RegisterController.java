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

    /**
     * Points to the name field
     */
    private EditText registerName;

    /**
     * Points to the e-mail address field
     */
    private EditText registerEmail;

    /**
     * Points to the password field
     */
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

    public String getRegisterName() {
        return registerName.getText().toString().trim();
    }

    public String getRegisterEmail() {
        return registerEmail.getText().toString().trim().toLowerCase();
    }

    public String getRegisterPassword() {
        return registerPassword.getText().toString();
    }

    public void submitForm(View view) {
        if (!validateFields()) {
            return;
        }

        // Communicate with online server to register account.
        showProgressDialog();

        Socket socket = SocketProvider.getInstance().getConnection();

        // Acquire the fields
        String name = getRegisterName();
        String email = getRegisterEmail();
        String password = getRegisterPassword();

        // Get the acknowledgement handler
        RegistrationAcknowledgement ack = new RegistrationAcknowledgement(this);

        socket.emit("auth:register", new AuthRegister(name, email, password), ack);
    }

    /**
     * Validates a field, making sure it's of a given length
     * @param value Value to check
     * @param field Field to add the error on
     * @param emptyString Message to show when forgotten
     * @param shortString Message to show when short
     * @param minLength Minimum length of the field
     * @return true if valid
     */
    protected boolean validateField(String value, EditText field, int emptyString, int shortString, int minLength) {
        if (value.isEmpty()) {
            showFieldError(field, emptyString);
            return false;
        } else if (value.length() < minLength) {
            showFieldError(field, shortString);
            return false;
        }
        return true;
    }

    /**
     * Validates an e-mail address
     *
     * @param value E-mail address to check
     * @param field E-mail field to set the error on
     * @param emptyString Message to show when forgotten
     * @param invalidString Message to show when empty
     * @return true if non-empty and valid
     */
    protected boolean validateEmail(String value, EditText field, int emptyString, int invalidString) {
        if (value.isEmpty()) {
            showFieldError(field, emptyString);
            return false;
        } else if (!EMAIL_ADDRESS.matcher(value).matches()) {
            showFieldError(field, invalidString);
            return false;
        }
        return true;
    }

    /**
     * Performs field validation
     * @return True if there are no problems with the fields
     */
    protected boolean validateFields() {
        EditText target = null;

        // Hide default error
        showError(null);

        if (!validateField(getRegisterName(), registerName, R.string.auth_error_name_empty, R.string.auth_error_name_short, 3)) {
            target = registerName;
        }

        if (!validateEmail(getRegisterEmail(), registerEmail, R.string.auth_error_email_empty, R.string.auth_error_email_invalid)) {
            target = target != null ? target : registerEmail;
        }

        if (!validateField(getRegisterPassword(), registerPassword, R.string.auth_error_password_empty, R.string.auth_error_password_short, 4)) {
            target = target != null ? target : registerName;
        }

        if (target == null) {
            return true;
        }

        target.requestFocus();
        return false;

    }

    /**
     * Makes a registration fail, showing the given error below the "Sign up" button. Usually
     * called with a response from the server.
     *
     * @param reason Message to show.
     */
    protected void failRegistration(String reason) {
        EditText target = null;

        switch (reason) {
            case "empty-name":
            case "short-name":
                target = registerName;
                break;
            case "empty-pass":
            case "short-pass":
                target = registerPassword;
                break;
            case "empty-mail":
            case "invalid-email":
            case "taken-email":
                target = registerEmail;
                break;
            default:
                // Default means not linked to any field.
                target = null;
                break;
        }

        String error = getErrorFromDictionary(reason);
        if (target != null) {
            showFieldError(target, error);
            target.requestFocus();
        } else {
            showErrorDialog(error);
            showError(error);
        }
        hideProgressDialog();
    }

    /**
     * Called after registration was successful. Stores the JWT and Starts the main menu activity,
     * discarding this and the login activity, if it's the previous one.
     *
     * @param token
     */
    protected void completeRegistration(String token) {
        saveJwt(token);
        hideProgressDialog();

        // Finish parent activity
        if (getParent() != null) {
            getParent().finish();
        }

        // Show main menu, finishes this activity
        showMainMenu(this);
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
     * Shows a registration failed message, with reason
     *
     * @param message Message to show as body
     */
    private void showErrorDialog(String message) {
        showErrorDialog(
            getString(R.string.register_alert_title),
            message,
            getString(R.string.register_alert_button)
        );
    }

    /**
     * Shows a message from the given resource ID on the given field. Internally calls
     * showFieldError(EditText, String)
     *
     * @param target
     * @param message
     */
    private void showFieldError(EditText target, int message) {
        showFieldError(target, getString(message));
    }

    /**
     * Adds a message to the given field. Cleared when the user changes the field.
     *
     * @param target
     * @param message
     */
    private void showFieldError(EditText target, String message) {
        if (message == null) {
            target.setError("");
        } else {
            target.setError(message);
        }
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

class RegistrationAcknowledgement implements Ack {

    private final RegisterController controller;

    RegistrationAcknowledgement(RegisterController controller) {
        this.controller = controller;
    }

    @Override
    public void call(final Object... args) {
        controller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(args[0] == null) {
                    controller.completeRegistration(args[1].toString());
                } else {
                    controller.failRegistration(args[0].toString());
                }
            }
        });
    }
}
