package com.asurspace.criminalintent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.asurspace.criminalintent.databinding.MainActivityBinding
import com.asurspace.criminalintent.ui.create_crime.CreateCrimeFragment
import com.asurspace.criminalintent.ui.crimes_list.CrimesListFragment
import com.asurspace.criminalintent.util.CREATE_CRIME_FRAGMENT
import com.asurspace.criminalintent.util.CRIMES_LIST_FRAGMENT
import com.asurspace.criminalintent.util.CRIME_FRAGMENT
import com.asurspace.criminalintent.util.PREVIEW_FRAGMENT
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val permissionLauncherAdd = registerForActivityResult(
        RequestPermission(),
        ::onGetPermissionResultAdd
    )

    private val permissionLauncherShowSub = registerForActivityResult(
        RequestPermission(),
        ::onGetPermissionResultShowSub
    )

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
            permissionLauncherAdd.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            true
        }

        R.id.action_show_subtitle -> {
            permissionLauncherShowSub.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    // todo PB

    fun showSnackBar(
        message: String,
        textAlignment: Int = View.TEXT_ALIGNMENT_CENTER
    ) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.DKGRAY)
        snackBar.view.setOnClickListener {
            snackBar.dismiss()
        }
        // Настройка текста
        val sBarPar = snackBar.view.findViewById<AppCompatTextView>(R.id.snackbar_text)
        with(sBarPar) {
            textSize = 12f
            sBarPar.textAlignment = textAlignment
        }
        snackBar.show()
    }

    fun showDialog(
        message: String,
        positiveActionBTitle: String,
        intent: Intent
    ) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.BLACK)

        // Настройка текста
        val sBarPar = snackBar.view.findViewById<AppCompatTextView>(R.id.snackbar_text)
        with(sBarPar) {
            textSize = 12f
        }
        with(snackBar) {
            setAction(positiveActionBTitle) {
                startActivity(intent)
            }
            view.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    private fun askForOpeningSettings() {
        val startSettingActivityIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager?.resolveActivity(
                startSettingActivityIntent, PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            showSnackBar("Permission denied forever!")
        } else {
            showDialog(
                "Our app will not works without this permission, you can add it in settings.",
                "Settings",
                startSettingActivityIntent
            )
        }
    }

    private fun onGetPermissionResultAdd(state: Boolean) {
        if (state) {
            openFragment(CreateCrimeFragment())
        } else {
            askForOpeningSettings()
        }
    }

    private fun onGetPermissionResultShowSub(state: Boolean) {
        if (state) {
            //openFragment()
        } else {
            askForOpeningSettings()
        }

    }

    private fun listenNavigationEvents() {
        supportFragmentManager.setFragmentResultListener(NAVIGATION_EVENT, this) { _, bundle ->

            when (bundle.get(NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY) as String) {

                // on crimes list fragment
                CRIMES_LIST_FRAGMENT -> {
                    supportActionBar?.let {
                        it.setDisplayHomeAsUpEnabled(false)
                        it.title = resources.getString(R.string.crimes_list)
                        if (!it.isShowing) {
                            it.show()
                        }
                    }
                    addItem.isVisible = true
                    showSubtitle.isVisible = true
                }

                // on crime fragment
                CRIME_FRAGMENT -> {

                    supportActionBar?.let {
                        it.setDisplayHomeAsUpEnabled(true)
                        it.title = resources.getString(R.string.crime_text)
                        if (!it.isShowing) {
                            it.show()
                        }
                    }
                    addItem.isVisible = true
                    showSubtitle.isVisible = false
                }

                // on crimes list fragment
                CREATE_CRIME_FRAGMENT -> {
                    supportActionBar?.let {
                        it.setDisplayHomeAsUpEnabled(true)
                        it.title = resources.getString(R.string.create_crime)
                        if (!it.isShowing) {
                            it.show()
                        }
                    }
                    addItem.isVisible = false
                    showSubtitle.isVisible = false
                }

                // on PREVIEW
                PREVIEW_FRAGMENT -> {
                    supportActionBar?.hide()
                }

                else -> {
                    addItem.isVisible = true
                    showSubtitle.isVisible = true
                }
            }

        }
    }


}