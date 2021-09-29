package edu.utep.cs.cs4330.ires10.Presentation;

/**
 * <h1> Main Activity </h1>
 *
 * Registration and login of users into the SCS prototype application
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import edu.utep.cs.cs4330.ires10.Model.UserInfo;
import edu.utep.cs.cs4330.ires10.R;
import edu.utep.cs.cs4330.ires10.Utils.LoginHelper;

public class MainActivity extends AppCompatActivity {
    private final String TAG="Security";
    private final String ACTIVITY="MainActivity: ";

    private TextView txtCreateAccount;
    private MaterialEditText edtStudentId;
    private Button btnLogin;
    private LoginHelper loginHelper;
    private Handler handler;
    private UserInfo userInfo;
    private final String collection = "securityUsers";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);
        //Init variables
        this.handler = new Handler(this.getMainLooper());
        loginHelper = LoginHelper.getInstance();
        loginHelper.setLoginHelperListener(new LoginHelper.LoginHelperListener(){
            @Override
            public void loginReturned(String result , String ID) {
                if(result.equals("1")){
                    userInfo = UserInfo.getInstance();
                    userInfo.setUserID(ID);
                    //Bundle created to pass login to front page activity. This link of intent passing eventually gets sent to FullViewReport to add the login to the reports so that a user can only confirm or deny once.
                    Bundle bundle = new Bundle();
                    bundle.putString("id", ID);
                    Intent i=new Intent(MainActivity.this, FrontPageActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }else if(result.equals("-2")){
                    handler.post(()->{
                        Toast.makeText(MainActivity.this,R.string.login_student,Toast.LENGTH_SHORT).show();
                    });
                }else if(result.equals("-1")){
                    handler.post(()->{
                        Toast.makeText(MainActivity.this,R.string.login_pending,Toast.LENGTH_SHORT).show();
                    });
                }else if(result.equals("0")){
                    handler.post(()->{
                        Toast.makeText(MainActivity.this,R.string.login_denied,Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void registerReturned(boolean authentication) {
                if(authentication){
                    handler.post(()->{
                        Toast.makeText(MainActivity.this,R.string.registration_success,Toast.LENGTH_SHORT).show();
                    });
                }else{
                    handler.post(()->{
                        Toast.makeText(MainActivity.this,R.string.registration_unsuccess,Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
        //Init Views
        edtStudentId= findViewById(R.id.edt_student_id);

        btnLogin= findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v ->{
            loginUser(edtStudentId.getText().toString());
        });

        txtCreateAccount = findViewById(R.id.txt_create_account);
        txtCreateAccount.setOnClickListener(v ->{
            View registerLayout= LayoutInflater.from(MainActivity.this).inflate(R.layout.user_register,null);

            new MaterialStyledDialog.Builder(MainActivity.this)
                    .setIcon(R.drawable.ic_user)
                    .setTitle(R.string.registration_title)
                    .setDescription(R.string.registration_description)
                    .setCustomView(registerLayout)
                    .setNegativeText(R.string.registration_negative)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveText(R.string.registration_positive)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MaterialEditText edtRegisterStudentId= registerLayout.findViewById(R.id.edt_student_id);
                            if(TextUtils.isEmpty(edtRegisterStudentId.getText().toString())){
                                Toast.makeText(MainActivity.this,R.string.sId_msg,Toast.LENGTH_SHORT).show();
                                return;
                            }
                            registerUser(edtRegisterStudentId.getText().toString());
                        }
                    }).show();
        });
    }

    private void registerUser(String studentId) {
        Map<String,String> params=new HashMap<String, String>();
        params.put("betaTesterID",studentId);
        params.put("status","-1");
        params.put("collection",collection);
        loginHelper.registerUser(params);

    }

    private void loginUser(String studentId) {
        if(TextUtils.isEmpty(studentId)){
            Toast.makeText(this,R.string.sId_msg,Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,String> params=new HashMap<String, String>();
        params.put("betaTesterID",studentId);
        params.put("collection",collection);
        loginHelper.tryLogin(params);

    }
}
