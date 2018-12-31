package com.clubz.ui.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.clubz.R
import com.clubz.data.model.ClubMember
import com.clubz.ui.profile.adapter.AdapterContactList
import android.support.v7.widget.SearchView
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil

import com.clubz.data.local.db.repo.AllFabContactRepo
import com.clubz.data.model.AllFavContact
import com.clubz.data.model.Profile
import kotlinx.android.synthetic.main.activity_contact_list.*
import com.clubz.R.id.searchView
import android.widget.EditText
import android.graphics.Typeface
import android.view.View.GONE
import com.clubz.utils.Util


class ContactListActivity : AppCompatActivity(), AdapterContactList.Listner, View.OnClickListener {

    private lateinit var adapter: AdapterContactList
    private var contactList: ArrayList<ClubMember> = arrayListOf()

    private val ARG_CHATFOR = "chatFor"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"
    private var searchView: SearchView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        val favContactList = AllFabContactRepo().getAllFavContats()
        adapter = AdapterContactList(favContactList, this@ContactListActivity, this@ContactListActivity)
        recycleViewContactList.adapter = adapter
        ivBack.setOnClickListener(this)

       /* searchEditTxt!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.getFilter().filter(p0!!.trim())
            }
        })
        searchIcon.setOnClickListener(this)*/
        searchView = findViewById(R.id.searchView)
        searchView!!.setMaxWidth(Integer.MAX_VALUE);
        val searchEditText = searchView!!.findViewById(android.support.v7.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(resources.getColor(R.color.white))
        searchEditText.setHint(R.string.contacts)
        searchEditText.setHintTextColor(resources.getColor(R.color.white))
        val myCustomFont = Typeface.createFromAsset(assets, "teko_semibold.ttf")
        searchEditText.setTypeface(myCustomFont)
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)
                return false
            }
        })


        ivExpandBtn.setOnClickListener(View.OnClickListener {

            if(recycleViewContactList.visibility == GONE){
                ivExpandBtn.setImageResource(R.drawable.ic_event_up_arrow)
                Util.setRotation(ivExpandBtn, true)
                recycleViewContactList.setVisibility(View.VISIBLE)
            }
            else{
                ivExpandBtn.setImageResource(R.drawable.ic_event_down_arrow)
                Util.setRotation(ivExpandBtn, true)
                recycleViewContactList.setVisibility(View.GONE)
            }

        })
    }

    override fun onItemClick(contact: AllFavContact, pos: Int) {

    }

    override fun onChatClick(contact: AllFavContact) {
        if (!contact.userId.equals("")) {
            startActivity(Intent(this@ContactListActivity, AllChatActivity::class.java)
                    .putExtra(ARG_CHATFOR, ChatUtil.ARG_IDIVIDUAL)
                    .putExtra(ARG_HISTORY_ID, contact.userId)
                    .putExtra(ARG_HISTORY_NAME, contact.name)
            )
        } else {
            Toast.makeText(this@ContactListActivity, "Under development", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onProfileClick(contact: AllFavContact) {
        if (contact.userId!!.isNotEmpty()) {
            val profile = Profile()
            profile.userId = contact.userId!!
            profile.full_name = contact.name!!
            profile.profile_image = contact.profile_image!!
            startActivity(Intent(this@ContactListActivity, ProfileActivity::class.java).putExtra("profile", profile))
        } else {
            Toast.makeText(this@ContactListActivity, getString(R.string.under_development), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                finish()
            }
           /* R.id.searchIcon -> {
                searchEditTxt.focusable=View.FOCUSABLE
                searchEditTxt.requestFocus()
            }*/
        }
    }
}
