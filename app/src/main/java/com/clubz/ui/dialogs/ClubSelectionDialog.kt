package com.clubz.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.clubz.R
import com.clubz.data.model.ClubName
import kotlinx.android.synthetic.main.dialog_club_selection.*

abstract class ClubSelectionDialog(internal val context: Context, val clubList: ArrayList<ClubName>) : Dialog(context), View.OnClickListener {

    var selectedClub: ClubName? = null

    override fun onClick(p0: View?) {

    }

    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_club_selection, null);
        this.setContentView(view)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = Adapter()
        recycleView.adapter = adapter

        mCancel.setOnClickListener(View.OnClickListener {
            this.dismiss()
        })

        mDone.setOnClickListener(View.OnClickListener {
            if (selectedClub != null)
                onClubSelect(selectedClub!!)
            else Toast.makeText(context, "Select club first", Toast.LENGTH_SHORT).show()
        })
    }


    abstract fun onClubSelect(clubName: ClubName)


     inner class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

        var lasetSelectedPosition = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(context).inflate(R.layout.item_club_selection, parent, false))
        }

        override fun getItemCount(): Int = clubList.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val club = clubList.get(position)
            holder?.tvClubName?.text = club.club_name
            holder?.radioBtn?.isChecked = club.isSelected
            /*if(lasetSelectedPosition==position){
                holder?.radioBtn?.isChecked = true
            }*/
        }


        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {

            val tvClubName = view.findViewById<TextView>(R.id.tvClubName)
            val image_icon = view.findViewById<ImageView>(R.id.image_icon)
            val radioBtn = view.findViewById<RadioButton>(R.id.radioBtn)

            init {
                view.setOnClickListener({
                    try {
                        for (t in clubList) t.isSelected = false
                        clubList[adapterPosition].isSelected = true
                        selectedClub = clubList[adapterPosition]
                        notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                })

                radioBtn.setOnClickListener({
                    try {
                        for (t in clubList) t.isSelected = false
                        clubList[adapterPosition].isSelected = true
                        selectedClub = clubList[adapterPosition]
                        notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            }
        }
    }
}