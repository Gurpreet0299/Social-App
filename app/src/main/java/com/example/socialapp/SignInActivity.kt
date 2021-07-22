package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 123
    private lateinit var auth: FirebaseAuth

    lateinit var googleSignInClient : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        signInbtn.setOnClickListener {
            signIn()
        }
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

    }

    //after onCreate(),onStart() gets called so here we'll check if account is already signed in or not
      override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    //Callback for startActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

        }
    }
//task will have account which you have used to log in In application
    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {

    try {

        // Google Sign In was successful, authenticate with Firebase
            val account = task?.getResult(ApiException::class.java)!!

            firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {                                        //DIFFICULTY FACED WAS NEEDED TO ADD SHA KEY IN FIREBASE THATS WHY IT WAS GIVING EXCEPTION ON SIGNING UP
                    // Google Sign In failed, update UI appropriately
        Toast.makeText(this,"${e} ",Toast.LENGTH_LONG).show()

    }
            }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signInbtn.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        //Using coroutines , work will be done on the seperate thread other than the MainThread
        //Using coroutines you can prevent callback hell i.e using nesting alot of listeners to do this job
        GlobalScope.launch(Dispatchers.IO){
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            //UI will update on MainThread so going back to MainThread because you can't update UI elements in background thread
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {

        if(firebaseUser!=null){

            val user = User(firebaseUser.uid,firebaseUser.displayName, firebaseUser.photoUrl.toString())
            val usersDao =UserDao()
            usersDao.addUser(user)
            val myactivityIntent = Intent(this,MainActivity::class.java)
            startActivity(myactivityIntent)
            finish()
        }
        else{
            signInbtn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}
