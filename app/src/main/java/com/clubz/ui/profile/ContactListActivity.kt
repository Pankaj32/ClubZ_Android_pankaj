package com.clubz.ui.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.clubz.R
import com.clubz.data.model.ClubMember
import com.clubz.ui.profile.adapter.AdapterContactList
import android.app.SearchManager
import android.content.Context
import android.support.v7.widget.SearchView
import android.content.Intent
import android.widget.Toast
import com.clubz.chat.AllChatActivity
import com.clubz.chat.util.ChatUtil
import com.clubz.data.local.db.DatabaseTable
import com.clubz.data.local.db.repo.AllFabContactRepo
import com.clubz.data.model.AllFavContact
import com.clubz.data.model.Profile
import kotlinx.android.synthetic.main.activity_contact_list.*

class ContactListActivity : AppCompatActivity(), AdapterContactList.Listner {


    private lateinit var db: DatabaseTable
    private lateinit var searchView: SearchView
    private lateinit var adapter: AdapterContactList
    private var contactList: ArrayList<ClubMember> = arrayListOf()

    private val ARG_CHATFOR = "chatFor"
    private val ARG_HISTORY_ID = "historyId"
    private val ARG_HISTORY_NAME = "historyName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        db = DatabaseTable(this)

        handleIntent(intent)

        toolbar.title = getString(R.string.contacts)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val favContactList = AllFabContactRepo().getAllFavContats()
        adapter = AdapterContactList(favContactList, this@ContactListActivity, this@ContactListActivity)
        recycleViewContactList.adapter = adapter
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchManager = this@ContactListActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this@ContactListActivity.componentName))

        return super.onCreateOptionsMenu(menu)
    }

    private fun handleIntent(intent: Intent?) {

        if (Intent.ACTION_SEARCH == intent?.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
            val c = db.getWordMatches(query, null)
            //process Cursor and display results
        }
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
}
