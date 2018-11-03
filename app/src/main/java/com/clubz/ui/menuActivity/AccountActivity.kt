package com.clubz.ui.menuActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.clubz.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity(), View.OnClickListener {
    private var langSpinnAdapter: ArrayAdapter<String>? = null
    private var deleteSpinnAdapter: ArrayAdapter<String>? = null
    val langArrayList = ArrayList<String>()
    val deleteArrayList = ArrayList<String>()
    private lateinit var autocompleteFragment: PlaceAutocompleteFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        langArrayList.add(getString(R.string.english))
        langArrayList.add(getString(R.string.spanish))
        deleteArrayList.add(getString(R.string.no_way))
        langSpinnAdapter = ArrayAdapter(this@AccountActivity, R.layout.spinner_item, R.id.spinnText, langArrayList)
        deleteSpinnAdapter = ArrayAdapter(this@AccountActivity, R.layout.spinner_item, R.id.spinnText, deleteArrayList)
        spinnerLang.adapter = langSpinnAdapter
        spinnerDeleteAccount.adapter = deleteSpinnAdapter
        ivBack.setOnClickListener(this)
        autocompleteFragment = fragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlaceAutocompleteFragment

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place?) {
                mainLocation.setText(p0!!.address)
                /*latitute = p0!!.latLng.latitude.toString()
                longitute = p0!!.latLng.longitude.toString()*/
            }

            override fun onError(p0: Status?) {

            }

        })

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }
}