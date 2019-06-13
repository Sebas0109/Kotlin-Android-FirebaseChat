package com.example.finalapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.finalapp.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainEmptyActivity : AppCompatActivity() {

    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ES DE PRACTIV¿CA COMUN POR SI ES QUE SE TIENE UN LOGGIN PAGE SE PONGA UN EMPTY ACTIVITY PARA CONTROLAR DE CIERTA AMNERA EL

        //LOGIN AND MAIN ACTIVITY FLOW

        //PARA EL CONTROL DE SI LA SESION ESTA ACTIVA  O NO E IR A DONDE SEA NECESARIO PARA ELLO

        /*HICIMOS ESTA ACTIVITY COMO LAUNCHER CON TRANSPARENCIA DE THEME DEBIDO A QUE NO MUESTRA NADASOLO SIRVE PARA VERIFICAR EL USUARIO Y PASAR A OTRA ACTIVITY*/
        if (mAuth.currentUser == null)
        {
            goToActivity<LoginActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }else
        {
            goToActivity<MainActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        finish()
    }
}
