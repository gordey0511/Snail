package com.user.snail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText username, email, password;
    Button btn_register;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Регистрация");

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        username = findViewById(R.id.userName);
        email = findViewById(R.id.userEmail);
        password = findViewById(R.id.userPassword);
        db=FirebaseFirestore.getInstance();

        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "Необходимо заполнить все поля", Toast.LENGTH_SHORT).show();
                } else if(txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Пароль должен быть не короче 6 символов", Toast.LENGTH_SHORT).show();
                } else {
                    register(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    private void register(final String username, final String email, final String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            Log.e("register",email);

                            setTask(firebaseUser,email,password);

                            sendEmailVerificationWithContinueUrl(firebaseUser);
                            showVerifyEmail(firebaseUser);
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            onError(errorCode);
                        }
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendEmailVerificationWithContinueUrl(final FirebaseUser user) {
        // [START send_email_verification_with_continue_url]

        String url = "https://snail-project.firebaseapp.com/verify?uid=" + user.getUid();
        Log.e("actionCodeSettings",url);
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(url)
                .setIOSBundleId("com.example.ios")
                // The default for this is populated with the current android package name.
                .setAndroidPackageName("com.user.snail", false, null)
                .build();


        user.sendEmailVerification(actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("onCompleteEmail", "Email sent. "+String.valueOf(user.isEmailVerified()));
                        }
                    }
                });

        // [END send_email_verification_with_continue_url]
        // [START localize_verification_email]
        // To apply the default app language instead of explicitly setting it.
        // auth.useAppLanguage();
        // [END localize_verification_email]
    }



    public void showVerifyEmail(final FirebaseUser user){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Подвердите Email")
                .setMessage("Мы отправили вам на почту письмо. Перейдите, пожайлуйста, по ссылке из письма для подтверждения")

                .setNegativeButton("Отправить письмо ещё раз",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendEmailVerificationWithContinueUrl(user);
                                Toast.makeText(getApplicationContext(),"Отправлено новое письмо",Toast.LENGTH_LONG).show();
                            }
                        });

        builder.show();
    }


    private void onError(String errorCode) {
        switch (errorCode) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(getApplicationContext(), "Неверный формат токена. Пожалуйста, проверьте данные", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(getApplicationContext(), "Пользовательский токен соответствует другой аудитории.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(getApplicationContext(), "Предоставленные учетные данные неверны или устарели.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(getApplicationContext(), "Адрес электронной почты имеет неправильный формат", Toast.LENGTH_LONG).show();
//                                    etEmail.setError("The email address is badly formatted.");
//                                    etEmail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(getApplicationContext(), "Пароль неверен или у пользователя нет пароля.", Toast.LENGTH_LONG).show();
//                                    etPassword.setError("password is incorrect ");
//                                    etPassword.requestFocus();
//                                    etPassword.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(getApplicationContext(), "Предоставленные учетные данные не соответствуют ранее зарегистрированному пользователю.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(getApplicationContext(), "Эта операция чувствительна и требует недавней аутентификации. Войдите еще раз, прежде чем повторять этот запрос.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(getApplicationContext(), "Учетная запись уже существует с тем же адресом электронной почты, но с другими учетными данными для входа. Войдите, используя аккаунт, связанного с этим адресом электронной почты.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(getApplicationContext(), "Этот адрес электронной почты уже используется другой учетной записью.", Toast.LENGTH_LONG).show();
//                                    etEmail.setError("The email address is already in use by another account.");
//                                    etEmail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(getApplicationContext(), "Эти учетные данные уже связаны с другой учетной записью пользователя.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(getApplicationContext(), "Учетная запись пользователя была отключена администратором.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(getApplicationContext(), "Учетные данные пользователя больше не действительны. Пользователь должен снова войти в систему.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(getApplicationContext(), "Нет записи пользователя, соответствующей этому идентификатору. Возможно, пользователь был удален.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(getApplicationContext(), "Учетные данные пользователя больше не действительны. Пользователь должен снова войти в систему.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(getApplicationContext(), "Эта операция не разрешена. Вы должны включить эту службу в консоли.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(getApplicationContext(), "Пароль должен сожержать минимум 6 символов", Toast.LENGTH_LONG).show();
//                                    etPassword.setError("The password is invalid it must 6 characters at least");
//                                    etPassword.requestFocus();
                break;

        }
    }


    public void setTask(final FirebaseUser user,final String email,final String password){
//        db.collection("task")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            Log.e("checkcheckRegis","GOOOD");
//                            DocumentReference docRef=db.collection("account").document(user.getUid());
//                            HashMap<String,Object> m3=new HashMap<>();
//                            m3.put("imageURL", "default");
//                            m3.put("name",username.getText().toString());
//                            m3.put("email",email);
//                            m3.put("password",password);
//                            docRef.set(m3, SetOptions.merge());
//                        }
//                    }
//                });
    }

    public void updateUI(){
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        Log.e("checkcheckVxod","VXOD GOOD");
        startActivity(intent);
    }

}
