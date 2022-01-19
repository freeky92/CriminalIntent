package com.asurspace.criminalintent

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.asurspace.criminalintent.databinding.MainActivityBinding
import com.asurspace.criminalintent.ui.crime.CrimeFragment
import com.asurspace.criminalintent.ui.crimes_list.CrimesListFragment
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object {
        const val NAVIGATION_EVENT: String = "navigation_event"
        const val NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY: String = "fragment_name"
    }

    private lateinit var binding: MainActivityBinding

    private val activityName: String = "MainActivity"

    private lateinit var addItem: MenuItem
    private lateinit var showSubtitle: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        Repository.init(applicationContext)
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater).also { setContentView(it.root) }

        setSupportActionBar(binding.actionBar)

        if (savedInstanceState == null) {
            openFragment(CrimesListFragment(), true, "CrimesListFragment")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        addItem = menu.findItem(R.id.action_add)
        showSubtitle = menu.findItem(R.id.action_show_subtitle)

        listenNavigationEvents()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            openFragment(CrimeFragment())
            true
        }

        R.id.action_show_subtitle -> {

            true
        }

        android.R.id.home -> {
            onBackPressed()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    fun openFragment(
        fragment: Fragment,
        clearBackStack: Boolean = false,
        fragmentName: String = fragment::class.java.simpleName
    ) {
        if (clearBackStack) {
            clearBackStack()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragment, fragmentName)
            .addToBackStack(fragmentName)
            .commit()
    }

    private fun clearBackStack() =
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

    fun showSnackBar(
        message: String,
        linkAction: Pair<String, String>? = null,
        warpOnUrlOnClick: String? = null,
        textAlignment: Int = View.TEXT_ALIGNMENT_CENTER
    ) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.DKGRAY)

        // Настройка текста
        val sBarPar = snackBar.view.findViewById<TextView>(R.id.snackbar_text)
        sBarPar.textSize = 12f

        sBarPar.textAlignment = textAlignment


        if (linkAction != null) {
            snackBar.setAction(linkAction.first) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkAction.second)))
            }.setActionTextColor(Color.BLUE)
        }

        // Настройка действие по клику на бар
        if (warpOnUrlOnClick != null) {
            snackBar.view.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(warpOnUrlOnClick)))
            }
        } else {
            snackBar.view.setOnClickListener { snackBar.dismiss() }
        }
        snackBar.show()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    private fun listenNavigationEvents() {
        supportFragmentManager.setFragmentResultListener(NAVIGATION_EVENT, this) { _, bundle ->

            when (bundle.get(NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY) as String) {

                // on crime fragment
                FragmentNameList.CRIME_FRAGMENT -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.title = resources.getString(R.string.crime_text)
                    addItem.isVisible = false
                    showSubtitle.isVisible = false
                }

                // on crimes list fragment
                FragmentNameList.CRIMES_LIST_FRAGMENT -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.title = resources.getString(R.string.crimes_list)
                    addItem.isVisible = true
                    showSubtitle.isVisible = true
                }

                else -> {
                    addItem.isVisible = true
                    showSubtitle.isVisible = true
                }
            }

        }
    }

}