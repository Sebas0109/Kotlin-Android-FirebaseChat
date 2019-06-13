package com.example.finalapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.finalapp.R
import com.example.finalapp.inflate
import com.example.finalapp.models.Message
import com.example.finalapp.utlis.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chat_item_left.view.*
import kotlinx.android.synthetic.main.fragment_chat_item_right.view.*
import java.text.SimpleDateFormat

class ChatAdapter (val items : List<Message>, val userId : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val GLOBAL_MESSAGE = 1
    private val  MY_MESSAGE = 2

    private val layoutRight = R.layout.fragment_chat_item_right
    private val layoutLeft = R.layout.fragment_chat_item_left

    //RETORNA LE TIPO DEL VIEW LO USAREMOS PARA QUE SE NOTE SI EL MENSJAE QUE LLEGUE DE LA LISTA ES DEL USUARIO O NO
    override fun getItemViewType(position: Int): Int {
        if (items[position].authorId == userId)
        {
            return MY_MESSAGE
        }else {
            return GLOBAL_MESSAGE
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType)
        {
            MY_MESSAGE -> ViewHolderRight(parent.inflate(layoutRight))
            else -> ViewHolderLeft(parent.inflate(layoutLeft))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //parent d elos viewholder contiene una propiedad que es el viewtype para reconocer de que clase es
        when(holder.itemViewType)
        {
            MY_MESSAGE -> (holder as ViewHolderRight).bind(items[position])
            GLOBAL_MESSAGE -> (holder as ViewHolderLeft).bind(items[position])
        }
    }

    class ViewHolderRight(itemView : View): RecyclerView.ViewHolder(itemView)
    {
        fun bind(message : Message) = with(itemView) {
            textViewMessageRight.text = message.message
            textViewTimeRight.text = SimpleDateFormat("hh:mm").format(message.sentAt)
            //LOGICA PARA VER SI EL ULR ES UN VAIVO O NO
            if (message.profileImageURL.isEmpty())
            {
                Picasso.get().load(R.drawable.ic_chat).resize(100,100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfileRight)
            }else {
                Picasso.get().load(message.profileImageURL).resize(100, 100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfileRight)
            }
        }
    }

    class ViewHolderLeft(itemView : View): RecyclerView.ViewHolder(itemView)
    {
        fun bind(message : Message) = with(itemView) {
            textViewMessageLeft.text = message.message
            textViewTimeleft.text = SimpleDateFormat("hh:mm").format(message.sentAt)
            //LOGICA PARA VER SI EL ULR ES UN VAIVO O NO
            if (message.profileImageURL.isEmpty()) {
                Picasso.get().load(R.drawable.ic_chat).resize(100, 100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfileLeft)
            } else {
                Picasso.get().load(message.profileImageURL).resize(100, 100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfileLeft)
            }
        }
    }
}