package com.example.finalapp

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern

fun Activity.toast(message :CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()

fun Activity.toast(resourceid : Int, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resourceid, duration).show()

fun ViewGroup.inflate(layoutId : Int) = LayoutInflater.from(context).inflate(layoutId, this, false)!!

//fun ImageView.loadByUrl(url : String) = Picasso.get().load(url).into(this)

//fun ImageView.loadByResource(resource : Int) = Picasso.get().load(resource).fit().into(this)

//T ES UN MANERA DE DECLARAR ALGO QUE EN VERDAD NO SABEMOS QEU ES AQUI E SALGO QUE NO SABEMOS QUE ES PERO QUE HEREDA DE ACTIVITY
/*TAMBIEN PONEMOS QUE SOLO USARA LOS METODOS DEL INTENT QUE DEVUELVAN UN UNIT LOS CUALES SON LOS DEL PUT EXTRA
* APARTE DE ELLO EL INIT ES LO QUE HACE ESE USO DE ESO DE LO DEL UNIT DEL INTENT*/

//EL REIFIED AHCE QUE SEA UNA FUNCION EN LINEA PARA HACER USO DEL GENERICO T Y CREA UNA FUNCION EN LINEA
inline fun <reified T : Activity> Activity.goToActivity(noinline init : Intent.() -> Unit = {}) //ES UNA UNIT VACIAS POR QUE NO S EL EPASAN EXTRAS POR DEFAULT
{
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

//FUNCION PARA QUE TODOS EDIT TEXT NOS MANDE UN RESULTADO CUANDO SE HAGA UN CAMBIO EN EL TEXTO EN VIVO
fun EditText.validate(validation : (String) -> Unit){
    this.addTextChangedListener(object  : TextWatcher { //EL THIS LE PERMITE ACCEDER AL EDITTEXT POR QUE ES DE PARTE DEL MIMSO GRUPO
        override fun afterTextChanged(s: Editable?) {
            validation(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

fun Activity.isValidEmail(email:String):Boolean{
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

fun Activity.isValidPassword(password :String):Boolean{
    val passPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    //Necesita contener --> 1 Num /1 Minuscula/1 Mayuscula/ 1 C.Especial/ Min. Carac 4
    val pattern = Pattern.compile(passPattern)
    return pattern.matcher(password).matches()
}

fun Activity.isValidConfirmPassword(password : String, password2  :String): Boolean{
    return password == password2
}