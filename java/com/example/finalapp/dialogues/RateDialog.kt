package com.example.finalapp.dialogues

import android.support.v7.app.AlertDialog //LIBRERIA DE SOPORTE
import android.support.v4.app.DialogFragment //MAS SOPORTTE
import android.app.Dialog
//import android.app.DialogFragment PARA SDK DE 23 O MAS
import android.os.Bundle
import com.example.finalapp.R
import com.example.finalapp.models.NewRateEvent
import com.example.finalapp.models.Rate
import com.example.finalapp.toast
import com.example.finalapp.utlis.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dislog_rate.view.*
import java.util.*

class RateDialog : DialogFragment()
{

    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser : FirebaseUser

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        setUpCurrentUser()

        //VAMOS A INFLAR LA VISTA DE RATES PARA VER DESPUES COMO PASAR EL RATE A LA LISTA DE RATESDE LA VISTA DEL FRAGMENT
        val view = activity!!.layoutInflater.inflate(R.layout.dislog_rate, null)

        //PARA ARREGLAR EL ERROR DEL MIN SDK VERISON SE PUEDEN HACER 2 COSAS 1 CAMBIAR EL SDK A LO QUE PIDE Y DOS AGRAGAR LIBRERIAS DE SOBORTE DE ANDROID ARRIBA ESTAN
        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.dialog_title))
            .setView(R.layout.dislog_rate)
            .setPositiveButton(getString(R.string.dialog_ok)) { _ , _ ->
                val textRate = view.editTextRateFeedback.text.toString()
                if (textRate.isNotEmpty()) { //VERIFICAMOS QUE EL TEXTO NO SEA EMPTY
                    val imgURL = currentUser.photoUrl?.toString() ?: run { "" }
                    val rate = Rate( currentUser.uid, textRate, view.ratingBar.rating, Date(), imgURL)
                    //HASTA AQUI SOLO ASIGNAREMOS VALORES PARA CREAR UN NUEVO RATE SE HACE EN EL RATE FRAGMETN SE LO TENEMOS QUE PARAS USAMOS EVENTO REACTIVO PUBLICANDOD ESDE AQUI
                    //CREAMOS UN MODELO DE NEW RATE EVENT DATA CLASS
                    RxBus.publish(NewRateEvent(rate))
                }
            }
            .setNegativeButton(getString(R.string.dialog_cancel)) { _ , _ ->
                activity!!.toast("Pressed Cancel")
            }
            .create()
    }

    private fun setUpCurrentUser()
    {
        currentUser = mAuth.currentUser!!
    }
}