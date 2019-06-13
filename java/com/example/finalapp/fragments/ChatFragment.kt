package com.example.finalapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.finalapp.R
import com.example.finalapp.adapters.ChatAdapter
import com.example.finalapp.models.Message
import com.example.finalapp.models.TotalMessagesEvent
import com.example.finalapp.toast
import com.example.finalapp.utlis.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    private lateinit var _view : View

    private lateinit var adapter : ChatAdapter
    private val messageList : ArrayList<Message> = ArrayList()

    private val  mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser : FirebaseUser

    //PARA LA CONEXION A LA FIRESTORRE O DB DE FIREBASE
    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef : CollectionReference

    //PARA ELIMINAR EL LISTENER DEL LOS MENSAJE SIS E SQUE NO SE ESTA EN LA VIEW DEL RECYCLER VIEW
    private var chatSubscription : ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view =  inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpChatBtn()

        subscribeToChatMessages()

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

    private fun setUpRecyclerView()
    {
        //NECESITAMOS UN ADAPTADOR , layout manager
        val layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(messageList, currentUser.uid)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter
    }

    private fun setUpChatBtn()
    {
        _view.btnSend.setOnClickListener {
            val messageText = editTextMessage.text.toString()
            if (messageText.isNotEmpty())
            {
                //si tiene una url sera ese el res si no sera un string vacio
                val photo = currentUser!!.photoUrl?.let { currentUser.photoUrl.toString() } ?: run {""}
                val message = Message(currentUser.uid, messageText, currentUser.photoUrl.toString(), Date())
                saveMessage(message)
                _view.editTextMessage.setText("")
            }
        }
    }

    private fun saveMessage(message: Message)
    {
        //string es el nombre osea la llave vlaor de los datos que se ponene en la DB osea el nombre de la propiedad
        //Any va a ser campo que se envia
        val newMessage = HashMap<String, Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageURL"] = message.profileImageURL
        newMessage["sentAt"] = message.sentAt

        chatDBRef.add(newMessage).addOnCompleteListener {
            activity!!.toast("Message Added")
        }.addOnFailureListener {
            activity!!.toast("Message Error , Try again!")
        }
    }

    //escuchar o sacar mensaje s de la colecicon
    private fun subscribeToChatMessages()
    {
        //LE ASIGNAMOS A LA VARIABLE EL CHATDBREF PARA QUE CUANDO DESTRUYAMOS LA ACTIVITY SE BORRARA ESA VARIABLE PARA LIBERAR SISTEMA
        //ORDER BY QUE Y ASCENDIENTE MAXIMO 100 MENSAJES
        chatSubscription = chatDBRef
            .orderBy("sentAt", Query.Direction.DESCENDING).limit(100)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {

            //cada vez que agregemos borremos etc un mensaje este se triggerea
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception!")
                    return
                }

                snapshot?.let {
                    messageList.clear()
                    val messages = it.toObjects(Message::class.java)
                    messageList.addAll(messages.asReversed())
                    //agregamos una coleccion completa
                    adapter.notifyDataSetChanged()
                    _view.recyclerView.smoothScrollToPosition(messageList.size)

                    //PUBLICAMOS UN EVENTO TOTALMESSAGESEVENT PARA EL BUS QUE LO RECOJA
                    RxBus.publish(TotalMessagesEvent(messageList.size))
                }
            }
        })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }

}
