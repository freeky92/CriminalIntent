package com.asurspace.criminalintent.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.asurspace.criminalintent.R
import com.asurspace.criminalintent.databinding.MainActivityBinding
import com.asurspace.criminalintent.navigation.Navigator
import com.asurspace.criminalintent.navigation.ProviderCustomTitle
import com.asurspace.criminalintent.presentation.ui.create_crime.fragment.CreateCrimeFragment
import com.asurspace.criminalintent.presentation.ui.crimes_list.fragment.CrimesListFragment
import com.asurspace.criminalintent.presentation.ui.preview.fragment.PreviewFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    private val permissionLauncherAdd = registerForActivityResult(
        RequestPermission(),
        ::onGetPermissionResultAdd
    )

    private val currentFragment get() = supportFragmentManager.findFragmentById(R.id.main_container)

    private lateinit var binding: MainActivityBinding

    private lateinit var addItem: MenuItem
    private lateinit var showSubtitle: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater).also { setContentView(it.root) }

        setSupportActionBar(binding.toolbar)
        addItem = binding.toolbar.menu.findItem(R.id.action_add)
        showSubtitle = binding.toolbar.menu.findItem(R.id.action_show_subtitle)


        if (savedInstanceState == null) {
            openFragment(CrimesListFragment(), true, "CrimesListFragment")
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)
    }

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            updateUI()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        customTitleCheck(currentFragment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            permissionLauncherAdd.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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
        Log.d(TAG, supportFragmentManager.backStackEntryCount.toString())
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
        // ?????????????????? ????????????
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

        // ?????????????????? ????????????
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
        if (currentFragment is CreateCrimeFragment) return
        if (state) {
            openFragment(CreateCrimeFragment())
        } else {
            askForOpeningSettings()
        }
    }

    private fun updateUI() {
        val fragment = currentFragment
        customTitleCheck(fragment)
        actionCheck(fragment)
        hideToolbar(fragment)

    }

    private fun hideToolbar(fragment: Fragment?) {
        if (fragment is PreviewFragment) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }

    private fun customTitleCheck(fragment: Fragment?) {
        if (fragment is ProviderCustomTitle) {
            binding.toolbar.title = getString(fragment.getTitle())
            binding.toolbar.isTitleCentered = true
        } else {
            binding.toolbar.title = ""
        }
    }

    private fun actionCheck(fragment: Fragment?) {
        if (fragment !is CrimesListFragment) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            addItem.isVisible = true
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            addItem.isVisible = false
        }
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        super.onDestroy()
    }


    companion object {
        const val NAVIGATION_EVENT: String = "navigation_event"
        const val NAVIGATION_EVENT_FRAGMENT_NAME_DATA_KEY: String = "fragment_name"

        @JvmStatic
        private val TAG = "MainActivity"
    }

    override fun hideToolbar(status: Boolean) {
        if (status) {

        } else {

        }
    }
}