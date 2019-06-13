package com.example.finalapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.finalapp.R
import com.example.finalapp.models.TotalMessagesEvent
import com.example.finalapp.toast
import com.example.finalapp.utlis.CircleTransform
import com.example.finalapp.utlis.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_info.view.*
import java.util.*
import java.util.EventListener

class InfoFragment : Fragment() {

    private lateinit var _view : View

    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var  currentUser : FirebaseUser

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef : CollectionReference

    private var chatSubscription : ListenerRegistration? = null
    private lateinit var infoBusListener : Disposable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_info, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInformation()

        //Total Messages Firebase Style
        //subscribeTotalMessagesFirebaseStyle()
        //SE EJECUTARA EL DE ARRIBA EN EL BUS DE UNA MANERA REACTIVA EL CODIGO DE ARRIBA QUEDA SIN UTILIZAR SE OPTIMIZA DE CIERTA MANERA

        //Totañ Messages Event Bus + Reactive Style
        subscribeToTotalMEssageEventBusReactiveStyle()

        return _view
    }

    private fun setUpChatDB()
    {
        //SI NO EXISTE LA CREA
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser()
    {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpCurrentUserInformation()
    {
        _view.textViewInfoEmail.text = currentUser.email
        _view.textViewInfoName.text = currentUser.displayName?.let { currentUser.displayName } ?: run{getString(R.string.info_no_name)}

        currentUser.photoUrl?.let {
            Picasso.get().load(currentUser.photoUrl).resize(300, 300)
                .centerCrop().transform(CircleTransform()).into(_view.imageViewInfo)

        } ?: run{
            Picasso.get().load(R.drawable.ic_chat).resize(300, 300)
            .centerCrop().transform(CircleTransform()).into(_view.imageViewInfo)}
    }

    private fun subscribeTotalMessagesFirebaseStyle()
    {
        chatSubscription = chatDBRef.addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {

                exception?.let {
                    activity!!.toast("Exception !!")
                    return
                }
                //ESTA SOLUCION ES BUENA PARA BASES PEQUEÑAS QUE NO SUPERARAN LAS 50 MIL LECTURAS AL DIA
                //SI LA BASE ES MUY GRANDE LO MEJOR ES HACEF QUE SE HAGA POR MEDIO DE CLOUD FUNCTION QUE LA QUERY LA HACE ESA FUNCTION AUNQUE ES MAS PARA WEB
                //CHECAR EN FUTUROS USOS COMO CAMBIAR DE PLANES DE FIREBASE PARA UNA APP DE ESA MANERA
                querySnapshot?.let { _view.textViewInfoTotalMessages.text = "${it.size()}" }
            }
        })
    }

    private fun subscribeToTotalMEssageEventBusReactiveStyle()
    {
        /*
        TODO AQUEL QUE ESCUCHE EVENTOS COMO ESTOS VA A EJECUTARSE ESE CODIGO
        ESCUCHAR EL EVENTO CON LA CLASE
        SIEMPRE QUE NO SE DESTRUYA ESTE FRAGMENTO ESTARA ESCUCHANDO A LOS EVENTOS DE CUALQUIER PARTE D ELA APP SIEMPRE QUE SEAN DE LA MISMA CLASE O EVENTO
        */
        infoBusListener = RxBus.listen(TotalMessagesEvent::class.java).subscribe {
            _view.textViewInfoTotalMessages.text = "${it.total}"
        }
    }

    //LIBERAR RECURSOS PARA NO ESTAR LLAMANDO A CADA RATO AL SUBSCRIPTION DE FIREBASE
    override fun onDestroyView() {
        infoBusListener.dispose()
        chatSubscription?.remove()
        super.onDestroyView()
    }

}
