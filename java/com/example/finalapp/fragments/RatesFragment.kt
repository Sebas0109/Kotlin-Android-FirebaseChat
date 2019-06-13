package com.example.finalapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.finalapp.R
import com.example.finalapp.adapters.RatesAdapter
import com.example.finalapp.dialogues.RateDialog
import com.example.finalapp.models.NewRateEvent
import com.example.finalapp.models.Rate
import com.example.finalapp.toast
import com.example.finalapp.utlis.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_rates.view.*
import java.util.EventListener

class RatesFragment : Fragment() {

    private lateinit var  _view : View

    private lateinit var adapter : RatesAdapter
    private val ratesList : ArrayList<Rate> = ArrayList()
    private lateinit var scrollListener : RecyclerView.OnScrollListener

    //REFERENCIAS DEL USUARIO
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser : FirebaseUser

    //REFERENCIAS DE LA BASE DE DATOS
    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var ratesDBRef : CollectionReference

    //PARA ELIMINAR EL LISTENER DEL LOS MENSAJE SIS E SQUE NO SE ESTA EN LA VIEW DEL RECYCLER VIEW
    private var ratesSubscription : ListenerRegistration? = null
    private lateinit var rateBusListener : Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view =  inflater.inflate(R.layout.fragment_rates, container, false)

        setUpRatesDB()
        setUpCurrentUser()

        setUpRecyclerView()
        setUpFab()

        subscribeToRatings()
        subscribeToNewRatings()

        return _view
    }
    //CHECAR EN STACK OVERFLOW LO QUE SE DICE DE EL PADDING DEL RECYCLER VIEW ANDROID WHAT DOES THE CLIPTOPADDING ATTRIBUTE DO

    private fun hasUserRated(rates : ArrayList<Rate>) : Boolean
    {
        var res = false
        rates.forEach{
            if (it.userId == currentUser.uid){ //COMPROBAMOS QUE SEA DEL MIMSO TIPO ESTE ES UNO DE LOS METODOS PARA HACER QUE SI YA HIZO RATING NO LO PUEDA VOVLER A HACER
                res = true
            }
        }
        return res
    }

    private fun removeFabIfRated(rated : Boolean) //LO LLAMAMOS AL MOMENTO DE SUBSRIBIRNOS A LOS RATING S AL CAMBIAR EL ADAPTADOR
    {
        if (rated){
            _view.fabRating.hide()
            _view.recyclerView.removeOnScrollListener(scrollListener)
        }
    }

    private fun setUpRatesDB()
    {
        //SI NO EXISTE LA CREA
        ratesDBRef = store.collection("rates")
    }

    private fun setUpCurrentUser()
    {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpRecyclerView()
    {
        //PREPARAMOS EL RECYCLER VIEW CON LOS PARAM DEBIDOS
        val layoutManager = LinearLayoutManager(context)
        adapter = RatesAdapter(ratesList)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter

        scrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0 || dy < 0 && _view.fabRating.isShown)
                {
                    _view.fabRating.hide() //PARA OCULTARLO
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) //PARA CUANDO DEJE DE HACER SCROLL
                {
                    _view.fabRating.show() //PARA MOSTRARLO
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        }
    }

    private fun setUpFab()
    {
        //EN FRAGMENT RATES ESTA EL FAB
        //RATE DIALOG LO CREMAOS CON UN LAYOUT CON LAS ESTRELLAS Y UNA CLASE
        _view.fabRating.setOnClickListener { RateDialog().show(fragmentManager, "") } //TAG ES PARA ABRIR COSAS DOFERENTES DEL DIALOGO PARA INTENTS POR ASI DECIRLO
    }

    private fun saveRate(rate : Rate)
    {
        //HASH MAP 1° NOMBRE DE CAMPO 2° TIPO DE DATO
        //PASAMOS TIPO DE DATOS QUE ESTAN EN EL MODELO QUE CREAMOS PARA SU GUARDADO EN LA BASE DE DATOS
        val newRating = HashMap<String, Any>()
        newRating["userId"] = rate.userId
        newRating["text"] = rate.text
        newRating["rate"] = rate.rate
        newRating["createdAt"] = rate.createdAt
        newRating["profileImgURL"] = rate.profileImgURL

        ratesDBRef.add(newRating)
            .addOnCompleteListener{
                activity!!.toast("Rating Added!!")
            }
            .addOnFailureListener{
                activity!!.toast("Ratinf Error, try Again!!")
            }
        //AL GUARDAR EL RATING EN FIREBASE GUARDA PERO TODAVIA NO REFRESCA VIEW
    }

    private fun subscribeToRatings()
    {
       ratesSubscription =  ratesDBRef
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {

                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        activity!!.toast("Exception!!")
                    }

                    snapshot?.let {
                        ratesList.clear()
                        val rates = it.toObjects(Rate::class.java) //CONVERSION
                        ratesList.addAll(rates)
                        removeFabIfRated(hasUserRated(ratesList)) //DONDE PASAMOS PARA VERIFICAR SI EL USUSARIO EN VERDAD YA NO PUEDE PUBLICAR
                        adapter.notifyDataSetChanged()
                        _view.recyclerView.smoothScrollToPosition(0) //HACE EL QUERY A FIREBASE Y YA LOS GIARDA Y REFRESCA PERO MANDA UN ERROR D EQUE CREA MAS LISTENERS DE LO ENCESARIO SE SOL A CONT
                    }
                }
            })
    }

    private fun subscribeToNewRatings()
    {
        //YA VANMOS A RECIBIR EL EVENTO CUANDO SE PUBLIQUE EN EL EVETN BUS DESDE EL DIALOGO Y LO VAMOS A PASAR RECOJER Y GUARDAR EN BASE DE DATOS
        rateBusListener =  RxBus.listen(NewRateEvent::class.java).subscribe({
            saveRate(it.rate)
        })
    }

    override fun onDestroyView()
    {
        //COMO LOS LISTENERS TOMAN MEMORIA DEL EQUIPO TENEOMS QUE REMOVERLOS PARA QUE NO ESTEN ACTIVOS
        _view.recyclerView.removeOnScrollListener(scrollListener)
        ////COMO YA TINE E REFERENCIA EN EL ON DESTROY VIEW LIBERAMOS LAS REF DE LOS LISTENERS ACELERAMOS APP ETC.
        rateBusListener.dispose()
        ratesSubscription?.remove()
        super.onDestroyView()
    }
}
