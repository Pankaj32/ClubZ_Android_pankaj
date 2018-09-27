package com.clubz.ui.activities.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.clubz.R
import com.clubz.data.model.DialogMenu
import com.clubz.ui.ads.fragment.AdsFragment
import com.clubz.ui.newsfeed.fragment.FragNewsList
import com.clubz.ui.user_activities.fragment.Frag_My_Activity
import kotlinx.android.synthetic.main.fragment_item_list_dialog.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_item.view.*

// TODO: Customize parameter argument names
const val ARG_DIALOG_MENU_ITEM = "item_dialogMenu"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ItemListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [ItemListDialogFragment.Listener].
 */
class ItemListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null
    private var instanceMyActivity: Frag_My_Activity? = null
    private var instanceMyAd: AdsFragment? = null
    private var instanceMyFeed: FragNewsList? = null
    private var menuList = ArrayList<DialogMenu>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        // list.adapter = ItemAdapter(arguments!!.getSerializable(ARG_DIALOG_MENU_ITEM) as ArrayList<DialogMenu>)
        list.adapter = ItemAdapter(menuList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        /* val parent = parentFragment
        mListener = if (parent != null) {
            parent as Listener
        } else {
            context as Listener
        }*/
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    interface Listener {
        fun onItemClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_item_list_dialog_item, parent, false)) {

        internal val text: TextView = itemView.text
        internal val imageView: ImageView = itemView.imageView

        init {
            text.setOnClickListener {
                mListener?.let {
                    it.onItemClicked(adapterPosition)
                    dismiss()
                }

            }
        }
    }

    private inner class ItemAdapter internal constructor(private val menuList: ArrayList<DialogMenu>) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = menuList[position]
            holder.text.text = data.title
            holder.imageView.setImageResource(data.id)
            holder.imageView.visibility = if (data.title.equals(getString(R.string.leave_activity))) View.GONE else View.VISIBLE

            /*holder.text.setOnClickListener(object: View.OnClickListener{
                override fun onClick(p0: View?) {
                    mListener?.onItemClicked(position)
                }

            })*/
        }

        override fun getItemCount(): Int {
            return menuList.size
        }
    }

    fun setInstanceMyActivity(instance: Frag_My_Activity, menuList: ArrayList<DialogMenu>) {
        this.instanceMyActivity = instance
        this.menuList = menuList
        mListener = instanceMyActivity
    }

    fun setInstanceMyAd(instance: AdsFragment, menuList: ArrayList<DialogMenu>) {
        this.instanceMyAd = instance
        this.menuList = menuList
        mListener = instanceMyAd
    }
    fun setInstanceMyFeed(instance: FragNewsList, menuList: ArrayList<DialogMenu>) {
        this.instanceMyFeed = instance
        this.menuList = menuList
        mListener = instanceMyFeed
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(menuList: ArrayList<DialogMenu>): ItemListDialogFragment =
                ItemListDialogFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_DIALOG_MENU_ITEM, menuList)
                    }
                }

    }

}
