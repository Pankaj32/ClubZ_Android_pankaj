package com.clubz.ui.user_activities.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.*
import com.clubz.ClubZ

import com.clubz.R
import com.clubz.data.local.pref.SessionManager
import com.clubz.data.remote.WebService
import com.clubz.helper.vollyemultipart.VolleyMultipartRequest
import com.clubz.ui.cv.CusDialogProg
import com.clubz.ui.user_activities.activity.ActivitiesDetails
import com.clubz.ui.user_activities.expandable_recycler_view.ExpandableRecyclerAdapter
import com.clubz.ui.user_activities.expandable_recycler_view.MovieCategory
import com.clubz.ui.user_activities.expandable_recycler_view.MovieCategoryAdapter
import com.clubz.ui.user_activities.expandable_recycler_view.Movies
import com.clubz.ui.user_activities.listioner.ChildViewClickListioner
import com.clubz.ui.user_activities.listioner.ParentViewClickListioner
import com.clubz.utils.Util
import com.google.gson.Gson
import kotlinx.android.synthetic.main.frag_find_activities.*
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Frag_Find_Activities.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Frag_Find_Activities.newInstance] factory method to
 * create an instance of this fragment.
 */
class Frag_Find_Activities : Fragment(), View.OnClickListener, ParentViewClickListioner, ChildViewClickListioner {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mContext: Context? = null
    private var todayAdapter: MovieCategoryAdapter? = null
    private var tomorrowAdapter: MovieCategoryAdapter? = null
    private var soonAdapter: MovieCategoryAdapter? = null
    private var isTodayOpen: Boolean = false
    private var isTomorrowOpen: Boolean = false
    private var isSoonOpen: Boolean = false
    private val INITIAL_POSITION = 0.0f
    private val ROTATED_POSITION = 180f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.frag_find_activities, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewToday.layoutManager = LinearLayoutManager(mContext)
        recyclerViewTomorrow.layoutManager = LinearLayoutManager(mContext)
        recyclerViewSoon.layoutManager = LinearLayoutManager(mContext)

        val movie_one = Movies("The Shawshank Redemption")
        val movie_two = Movies("The Godfather")
        val movie_three = Movies("The Dark Knight")
        val movie_four = Movies("Schindler's List ")
        val movie_five = Movies("12 Angry Men ")
        val movie_six = Movies("Pulp Fiction")
        val movie_seven = Movies("The Lord of the Rings: The Return of the King")
        val movie_eight = Movies("The Good, the Bad and the Ugly")
        val movie_nine = Movies("Fight Club")
        val movie_ten = Movies("Star Wars: Episode V - The Empire Strikes")
        val movie_eleven = Movies("Forrest Gump")
        val movie_tweleve = Movies("Inception")

        val molvie_category_one = MovieCategory("Drama", Arrays.asList<Movies>(movie_one, movie_two, movie_three, movie_four))
        val molvie_category_two = MovieCategory("Action", Arrays.asList<Movies>(movie_five, movie_six, movie_seven, movie_eight))
        val molvie_category_three = MovieCategory("History", Arrays.asList<Movies>(movie_nine, movie_ten, movie_eleven, movie_tweleve))
        val molvie_category_four = MovieCategory("Thriller", Arrays.asList<Movies>(movie_one, movie_five, movie_nine, movie_tweleve))

        val movieCategories = Arrays.asList<MovieCategory>(molvie_category_one, molvie_category_two, molvie_category_three, molvie_category_four)

        getActivitiesList("", "", "")

        todayAdapter = MovieCategoryAdapter(mContext, movieCategories, this@Frag_Find_Activities,this@Frag_Find_Activities)
        todayAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = movieCategories[position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = movieCategories[position]
            }

        })
        tomorrowAdapter = MovieCategoryAdapter(mContext, movieCategories, this@Frag_Find_Activities, this@Frag_Find_Activities)
        tomorrowAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = movieCategories[position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = movieCategories[position]
            }

        })
        soonAdapter = MovieCategoryAdapter(mContext, movieCategories, this@Frag_Find_Activities, this@Frag_Find_Activities)
        soonAdapter!!.setExpandCollapseListener(object : ExpandableRecyclerAdapter.ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                val expandedMovieCategory = movieCategories[position]

            }

            override fun onListItemCollapsed(position: Int) {
                val collapsedMovieCategory = movieCategories[position]
            }

        })
        recyclerViewToday.setAdapter(todayAdapter)
        recyclerViewTomorrow.setAdapter(tomorrowAdapter)
        recyclerViewSoon.setAdapter(soonAdapter)
        todayLay.setOnClickListener(this@Frag_Find_Activities)
        tomorrowLay.setOnClickListener(this@Frag_Find_Activities)
        soonLay.setOnClickListener(this@Frag_Find_Activities)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context

    }

    override fun onDetach() {
        super.onDetach()

    }


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Frag_Find_Activities.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): Frag_Find_Activities {
            val fragment = Frag_Find_Activities()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.todayLay -> {
                if (isTodayOpen) {
                    setRotation(arowToday, isTodayOpen)
                    isTodayOpen = false
                    arowToday.setImageResource(R.drawable.ic_down_arrow)
                    recyclerViewToday.visibility = View.GONE
                } else {
                    setRotation(arowToday, isTodayOpen)
                    isTodayOpen = true
                    recyclerViewToday.visibility = View.VISIBLE
                    arowToday.setImageResource(R.drawable.ic_drop_up_arrow)

                }
            }
            R.id.tomorrowLay -> {
                if (isTomorrowOpen) {
                    setRotation(arowTomorrow, isTomorrowOpen)
                    isTomorrowOpen = false
                    recyclerViewTomorrow.visibility = View.GONE
                    arowTomorrow.setImageResource(R.drawable.ic_down_arrow)
                } else {
                    setRotation(arowTomorrow, isTomorrowOpen)
                    isTomorrowOpen = true
                    recyclerViewTomorrow.visibility = View.VISIBLE
                    arowTomorrow.setImageResource(R.drawable.ic_drop_up_arrow)
                }
            }
            R.id.soonLay -> {
                if (isSoonOpen) {
                    setRotation(arowSoon, isSoonOpen)
                    isSoonOpen = false
                    recyclerViewSoon.visibility = View.GONE
                    arowSoon.setImageResource(R.drawable.ic_down_arrow)
                } else {
                    setRotation(arowSoon, isSoonOpen)
                    isSoonOpen = true
                    recyclerViewSoon.visibility = View.VISIBLE
                    arowSoon.setImageResource(R.drawable.ic_drop_up_arrow)
                }
            }
        }
    }

    fun getActivitiesList(listType: String, limit: String, offset: String) {
        val dialog = CusDialogProg(mContext!!)
        dialog.show()
        val request = object : VolleyMultipartRequest(Request.Method.GET, WebService.get_my_activity_list + listType + "&limit=&offset=", object : Response.Listener<NetworkResponse> {
            override fun onResponse(response: NetworkResponse) {
                val data = String(response.data)
                Util.e("data", data)
                dialog.dismiss()
                try {
                    val obj = JSONObject(data)
                    if (obj.getString("status").equals("success")) {

                        /*var leaderResponce: GetLeaderResponce = Gson().fromJson(data, GetLeaderResponce::class.java)*/

                    } else {
                        /* Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()*/
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(mContext, R.string.swr, Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                dialog.dismiss()
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }) {

            override fun getHeaders(): MutableMap<String, String> {
                val params = java.util.HashMap<String, String>()
                //  params.put("language", SessionManager.getObj().getLanguage())
                params.put("authToken", SessionManager.getObj().user.auth_token)
                return params
            }
        }
        request.setRetryPolicy(DefaultRetryPolicy(70000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        ClubZ.instance.addToRequestQueue(request)
    }

    override fun onResume() {
        super.onResume()
        getActivitiesList("", "", "")
    }

    private fun setRotation(imgView: ImageView, expanded: Boolean) {
        val rotateAnimation: RotateAnimation
        if (expanded) { // rotate clockwise
            rotateAnimation = RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        } else { // rotate counterclockwise
            rotateAnimation = RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        }

        rotateAnimation.duration = 200
        rotateAnimation.fillAfter = true
        imgView.startAnimation(rotateAnimation)
    }

    override fun onItemMenuClick(position: Int) {
        Log.e("parent " + position," "+ position)
        Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(position: Int) {
    }

    override fun onItemLike(position: Int) {
    }

    override fun onItemChat(position: Int) {
    }

    override fun onItemJoin(position: Int) {
        startActivity(Intent(mContext, ActivitiesDetails::class.java))
    }

    override fun onJoin(parentPosition: Int, childPosition: Int) {
        Toast.makeText(context, "parent " + parentPosition + " child " + childPosition, Toast.LENGTH_SHORT).show()
        Log.e("parent " + parentPosition + " child " + childPosition,"hh")
    }
}
