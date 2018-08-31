package com.example.falin.simpleweather

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.pref_main)
    }

//    override fun onPrepareOptionsMenu(menu: Menu?) {
//        super.onPrepareOptionsMenu(menu)
//
//        val menuItem: MenuItem = menu!!.findItem(R.id.action_go_to_settings)
//        menuItem.isVisible = false
//
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        if (item!!.itemId == android.R.id.home) {
//            try {
//                iToolbar!!.onBackPressed()
//            } catch (e: Exception){
//                Log.e(LOG_TAG, "Послание " + e.message)
//            }
//
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = super.onCreateView(inflater, container, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimaryLight))

        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(activity.findViewById(R.id.toolbar) as Toolbar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Настройки"
        setHasOptionsMenu(true)

        return view
    }
}