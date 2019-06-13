package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.finalapp.activities.LoginActivity
import com.example.finalapp.adapters.PagerAdapter
import com.example.finalapp.fragments.ChatFragment
import com.example.finalapp.fragments.InfoFragment
import com.example.finalapp.fragments.RatesFragment
import com.example.mylibrary2.ToolBarActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ToolBarActivity() {

    private val mAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var prevBottomSelected : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //RECORDAR QUE SE AGREGARON COSAS EN LOS GRADLE PARA EL USO DE FIREBASE

        toolBarToload(toolbarView as Toolbar) //AGREGAMOS AL TOOLBAR EN LA ACTIVITY


        //llamando conf del bottom nav
        seTupViewPager(getPagerAdapter())
        setUpBottomNavigationBar()
    }

    private fun getPagerAdapter() : PagerAdapter
    {
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment())
        adapter.addFragment(RatesFragment())
        adapter.addFragment(ChatFragment())

        return adapter
    }

    private fun seTupViewPager(adapter : PagerAdapter)
    {
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count //PARA POENR EN LIMITE CUANTAS PAGINAS SE VAN A ENCONTRAR EN CACHE O EN MEMORIA DEL BOTTOM MENUN NAV NO ES BUENO ON MUCHAS TABS
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {} //NO LO EMPLEAREMOS
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}//NO LO EMPLEAREMOS

            //DESACTIVMAOS LA OPCION 1 Y ACTIVAMOS LA OPCION DE DONDE ESTA Y SEPA COMO MOVERSE
            override fun onPageSelected(p0: Int) {
                if(prevBottomSelected == null)
                {
                    bottomNavigation.menu.getItem(0).isChecked = false
                } else {
                    prevBottomSelected!!.isChecked = false
                }
                bottomNavigation.menu.getItem(p0).isChecked = true
                prevBottomSelected = bottomNavigation.menu.getItem(p0)
            }
        })
    }

    private fun setUpBottomNavigationBar()
    {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_nav_info -> {
                    viewPager.currentItem = 0; true
                }
                R.id.bottom_nav_rates -> {
                    viewPager.currentItem = 1; true
                }
                R.id.bottom_nav_chat -> {
                    viewPager.currentItem = 2; true
                }
                else -> false
            }
        }
    }
    //BUSCAR LIBRERIAS DE RXJAV AY DE RXKOTLIN QUE AÑADEN ,ASM EXTENSION FUNCTIONS DE MAENRA QUE CIERTAS ACCIONES TOMAN MENOS CODIGO PARA REALIZAR ALGO

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()
                goToActivity<LoginActivity>{
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

/*EN CARPETA DE DESCARGAS ESTA UNA CARPETA ZIP QUE S ELLMAMA RECURSOS EN ELLA ESTAN CUSTOM STARS OAR LA CUSTOM RATING BAR APARTE DE ELLO ESTA UNA PAGINA DE CREACION DE ICONOS CUSTOMIZADOS PARA LA CREACION*/