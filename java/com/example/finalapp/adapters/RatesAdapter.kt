package com.example.finalapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.finalapp.R
import com.example.finalapp.inflate
import com.example.finalapp.models.Rate
import com.example.finalapp.utlis.CircleTransform
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_rates_item.view.*
import java.text.SimpleDateFormat

class RatesAdapter (private val items : List<Rate>) : RecyclerView.Adapter<RatesAdapter.ViewHolder>(){

    //CREAMOS LA VISTA DEL RECYCLER VIEW
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.fragment_rates_item))

    //PASAMOS A LA FUNCION DEL VIEWHOLDER DE BIND PARA ASIGNACION DE VALORES
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        //WITH ES EN EL CONTEXTO DE EL ITEMVIEW
        fun bind(rate : Rate) = with(itemView){
            textViewRate.text = rate.text
            textViewStar.text = rate.rate.toString()
            textViewCalendar.text = SimpleDateFormat("dd MMM, yyyy").format(rate.createdAt)

            //Metemos logica para por si es vacia o null la ruta de imgurl agregar la de defautl
            if (rate.profileImgURL.isEmpty())
            {
                Picasso.get().load(R.drawable.ic_chat).resize(100, 100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfile)
            }else {
                Picasso.get().load(rate.profileImgURL).resize(100, 100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfile)
            }
        }
    }
}