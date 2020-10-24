package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login_.*

class Login_Activity : AppCompatActivity() {
    var auth : FirebaseAuth? =null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_)
        auth = FirebaseAuth.getInstance()
        Btn_Email_Login.setOnClickListener{
            SiginAndSignup()
        }
        Btn_Google_Login.setOnClickListener {
            googlelogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }
    fun googlelogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result!!.isSuccess){
                var account = result.signInAccount
                FirebaseAuthWithGoogle(account)

            }
        }
    }
    fun FirebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // 로그인 성공
                    moveMainPage(task.result?.user)
                }else{
                    // 로그인 실패
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun SiginAndSignup(){
        auth?.createUserWithEmailAndPassword(Edt_Text_Email.text.toString(), Edt_Text_Password.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //생성성공
                    moveMainPage(task.result?.user)
                }else if(!task.exception?.message.isNullOrEmpty()){
                    // 로그인에러부분
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }else{
                    //로그인 부분으로 빠지
                    SiginEamil()
                }
            }
    }
    fun SiginEamil(){
        auth?.signInWithEmailAndPassword(Edt_Text_Email.text.toString(), Edt_Text_Password.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // 로그인 성공
                    moveMainPage(task.result?.user)
                }else{
                    // 로그인 실패
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun moveMainPage(user: FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}
