package com.example.finalapp.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.finalapp.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val mAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    //para inicio con google
    private val mGoogleApiClient : GoogleApiClient by lazy { getGoogleApiClient() }
    //CODIGO PRA EL ACTIVIT FOR RESULT
    private val RC_GOOGLE_SIGN_IN = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            if (isValidEmail(email) && isValidPassword(password)) {
                logInByEmail(email, password)
            }else {
                toast("Please make sure all data is correct")
            }
        }

        textViewForgotPAssword.setOnClickListener { goToActivity<ForgotPasswordActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)}

        buttonCreateAccount.setOnClickListener{goToActivity<SignUpActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)}

        editTextEmail.validate {
            editTextEmail.error = if (isValidEmail(it)) null else "The email is not valid"
        }

        editTextPassword.validate {
            editTextPassword.error = if (isValidPassword(it)) null else "The password should contain 1 number, 1 uppercase , 1 lowercase, 1 special character and minimum 4 characters"
        }

        buttonLoginGoogle.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN) //CODIGO PARA DIFERENCIA QUE ES LO QUE SE HACE DE PARTE DE EL ACTIVITY FOR RESULT
        }

    }

    private fun getGoogleApiClient() : GoogleApiClient
    {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("25496613265-l719ulngrsuqeclmom3oocm92qhraprg.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleApiClient.Builder(this)
            .enableAutoManage(this, this) //segundo this es el aon failed connection
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso) //GOOGLE SIGNING OPTIONS
            .build()
    }

    private fun loginByGoogleAccIntoFirebase(googleAccount : GoogleSignInAccount)
    {
        // escojemos credenciales del google provider
        //recibimos credenciales pero sin desencriptarlas llamamos al mauth para iniciar sesion con credencial
        /*val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this)
        {
            if (mGoogleApiClient.isConnected)
            {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient) //SI TIENE VARIAS CUENTAS DE GOOGLE NO LE PERMITIRA GUARDAR MAS DE UNA CUENTA SIEMPRE SERA LA PRIMERA QUE PONGA POR ELLO ES QUE QUITA EL GOOGLEAPI
            }
            goToActivity<MainActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }*/

        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this)
            {task ->
                if (task.isSuccessful)
                {
                    val user = mAuth.currentUser
                    goToActivity<MainActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK}
                }else
                    {
                        toast("auth failed")
                    }

            }
    }


    //GOOGLE VIEJO
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            /*val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            try {
                if (result.isSuccess) {

                    val account = result.signInAccount

                    loginByGoogleAccIntoFirebase(account!!)

                }
            }catch (e : ApiException)
            {
                toast("LOGIN FAILED")
            }*/
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                loginByGoogleAccIntoFirebase(account!!)
            }catch (e : ApiException)
            {
                toast(e.toString())
            }
        }
    }

    private fun logInByEmail(email : String , password : String)
    {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //PARA VERIFICAR QUE EL CORREO SE HAYA CLICKEADO O ALGO ASI REFERENTE A ELLO
                    //LA PLANTILLA PARA CAMBIAR EL SAPECTO DEL EMAIL ES DESDE LA PAGINA DE FIREBASE EN LA AUTENTICACION
                    if (mAuth.currentUser!!.isEmailVerified)
                    {
                        toast("User is now Logged in")
                        val user = mAuth.currentUser!!
                        goToActivity<MainActivity> {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    } else {
                        toast("User must confirm email first")
                    }
                } else {
                    toast("An unexpected error ocurred try again later")
                    //updateUI(null)
                }
            }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        toast("Connection Failed")
    }

}



