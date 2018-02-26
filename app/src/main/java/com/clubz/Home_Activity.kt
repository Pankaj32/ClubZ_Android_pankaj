package com.clubz

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.clubz.helper.SessionManager
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Created by mindiii on 2/23/18.
 */

class Home_Activity : Activity(), TabLayout.OnTabSelectedListener, View.OnClickListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tablayout.addOnTabSelectedListener(this)
        for(view in arrayOf(logout))view.setOnClickListener(this)
        setTab(tablayout.getTabAt(0)!!,R.drawable.ic_news_active,true)

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()){
            0->{    }
            1->{    }
            2->{    }
            3->{    }
        }

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()){
            0->{ setTab(tab,R.drawable.ic_news,false)          }
            1->{ setTab(tab,R.drawable.ic_activity,false)      }
            2->{ setTab(tab,R.drawable.ic_chat_bubble,false)   }
            3->{ setTab(tab,R.drawable.ic_ads,false)           }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab!!.getPosition()){
            0->{ setTab(tab,R.drawable.ic_news_active,true)         }
            1->{ setTab(tab,R.drawable.ic_activity_active,true)     }
            2->{ setTab(tab,R.drawable.ic_chat_bubble_active,true)  }
            3->{ setTab(tab,R.drawable.ic_ads_active,true)          }
        }
    }

    internal fun setTab(tab: TabLayout.Tab , imageRes :Int , isActive :Boolean) {
        tab.customView!!.findViewById<AppCompatImageView>(android.R.id.icon).setImageResource(imageRes)
        tab.customView!!.findViewById<TextView>(android.R.id.text1).setTextColor(resources.getColor(if(isActive)R.color.active_tab else R.color.inactive_tab))
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.logout->SessionManager.getObj().logout(this)
        }

    }
}
