package com.example.finalapp.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.finalapp.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.finalapp.R.layout.activity_sign_up)

        buttonGoLoggin.setOnClickListener {
            goToActivity<LoginActivity> { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
            //PARA HACER QUE CON EL BOTON BACK NO RETONRE AL SIGNUP O MAS BIEN NO GUARDE EN EL HISTORIAL DE ACTIVITIES EL DE SIGN UP
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            //PARA ESTABLECER ANIMACIONES)
        }

        buttonSignUp.setOnClickListener {
            val email : String = editTextEmail.text.toString()
            val password : String = editTextPassword.text.toString()
            val password2 : String = editTextConfirmPassword.text.toString()
            if (isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password, password2))
            {
                signUpByEmail(email, password)
            }else {
                toast("Please make sure all data is correct")
            }
        }
        /*// Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.getCurrentUser()
        if(currentUser == null)
        {
            Toast.makeText(this,"User is not Logged in",Toast.LENGTH_SHORT).show()
            createAccount("sebastiangutierrezmartinez0109@gmail.com","123456789")
            //SE DEBERIA VER EN LA PAG DE FIREBASE EL NUEVO USUARI AL CORRERLO
        }else {
            Toast.makeText(this,"User is Logged in",Toast.LENGTH_SHORT).show()
        }
        //updateUI(currentUser) EN MANIFEST PUSIMOS COMO LAUNCHER A ESTE*/

        //USAMOS LA VALIDACION DE LAS EXTENSOINS FUNCTIONS PASANDO LA FUNCOIN QUE SE VA A EMPLEAR
        editTextEmail.validate {
            editTextEmail.error = if (isValidEmail(it)) null else "The email is not valid"
        }

        editTextPassword.validate {
            editTextPassword.error = if (isValidPassword(it)) null else "The password should contain 1 number, 1 uppercase , 1 lowercase, 1 special character and minimum 4 characters"
        }

        editTextConfirmPassword.validate {
            editTextConfirmPassword.error = if (isValidConfirmPassword(editTextPassword.text.toString(), it)) null else "Passwords are different check them again"
        }
    }

    //PARA LA CREACIOND EUSUSARIO SE IMPLEMENTARA TAMBIENC ONFRIMACION POR CORREO
    private fun signUpByEmail(email : String, password : String)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //para mandar mensaje de autenticasion por correo
                    mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this){
                        toast ("An email has been sent to you. Please confrim before you Log In")

                        //val user = mAuth.currentUser PARA ASIGNAR EL USUARIO DESPUES DEL SIGN UP O CREACION DE USUARIO
                        //updateUI(user)
                        goToActivity<LoginActivity> { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    toast("An unexpected error occurred please try again")
                    //updateUI(null)
                }
            }
    }

        //TRIPLE IGUALACION COMPARA OBJETOS Y SUS REFERENCIAS DOBLE ES CONTENIDO RECORDAR
}
