package com.clubz.ui.ads.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.clubz.ClubZ
import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.ui.ads.adapter.AdsAdapter
import com.clubz.ui.ads.model.AdsListBean
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.cv.recycleview.RecyclerViewScrollListener
import com.clubz.utils.VolleyGetPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_ads.*
import org.json.JSONObject


class AdsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        pageListner?.resetState()
        getAdsList(isPull = true)
    }

    private var pageListner: RecyclerViewScrollListener? = null
    private var param1: String? = null
    private var param2: String? = null
    private var mContext: Context? = null
    private var userId: String = ""
    private var userName: String = ""
    private var userImage: String = ""
    private var adList = ArrayList<AdsListBean.DataBean>()
    private var adsAdapter: AdsAdapter? = null
    private var isResume = false

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ads, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    fun initializeView() {
        userId = ClubZ.currentUser!!.id
        userName = ClubZ.currentUser!!.full_name
        userImage = ClubZ.currentUser!!.profile_image
        swiperefresh.setOnRefreshListener(this)

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewAds.itemAnimator = null
        recyclerViewAds.layoutManager = lm
        recyclerViewAds.setHasFixedSize(true)

        nodataLay.visibility = if (adList.isEmpty()) View.VISIBLE else View.GONE
        adsAdapter = AdsAdapter(mContext, adList)
        recyclerViewAds.adapter = adsAdapter

        pageListner = object : RecyclerViewScrollListener(lm) {
            override fun onScroll(view: RecyclerView?, dx: Int, dy: Int) {}
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getAdsList(offset = page * 10)
            }
        }
        recyclerViewAds.addOnScrollListener(pageListner)
        getAdsList(isPull = true)
    }

    override fun onResume() {
        super.onResume()
        if (isResume) {
            getAdsList(isPull = true)
        } else {
            getAdsList()
            isResume = true
        }
    }

    fun getAdsList(listType: String = "", limit: String = "10", offset: Int = 0, isPull: Boolean? = false) {
        val dialog = CusDialogProg(mContext)
        if (!swiperefresh.isRefreshing||!isResume) dialog.show()
        object : VolleyGetPost(mContext,
                "${WebService.getAdsList}?limit=${limit}&offset=${offset}&listType=${listType}", true) {
            override fun onVolleyResponse(response: String?) {
                try {
                    if (swiperefresh.isRefreshing) swiperefresh.setRefreshing(false)
                    dialog.dismiss()

                    val obj = JSONObject(response)
                    if (obj.getString("status").equals("success")) {
                        val adsBean: AdsListBean = Gson().fromJson(response, AdsListBean::class.java)
                        updateUi(adsBean, isPull)
                    } else {
                        nodataLay.visibility = View.VISIBLE
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onVolleyError(error: VolleyError?) {
                dialog.dismiss()
            }

            override fun onNetError() {

            }

            override fun setParams(params: MutableMap<String, String>): MutableMap<String, String> {
                /*params.put("listType", listType)
                params.put("offset", offset.toString())
                params.put("limit", limit)*/
                return params
            }

            override fun setHeaders(params: MutableMap<String, String>): MutableMap<String, String> {
                params["authToken"] = SessionManager.getObj().user.auth_token
                Log.e("Auth:", SessionManager.getObj().user.auth_token)
                return params
            }
        }.execute(AdsFragment::class.java.name)
    }

    private fun updateUi(adsBean: AdsListBean, pull: Boolean?) {
        if (pull!!) adList.clear()
        adList.addAll(adsBean.getData()!!)
        adsAdapter?.notifyDataSetChanged()
        nodataLay.visibility = if (adList.isEmpty()) View.VISIBLE else View.GONE
    }
}
