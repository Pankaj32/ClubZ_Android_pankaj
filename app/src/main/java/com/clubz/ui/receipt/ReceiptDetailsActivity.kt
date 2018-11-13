package com.clubz.ui.receipt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.clubz.R
import com.clubz.ui.receipt.adapter.ReceiptMemberAdapter
import com.clubz.utils.Util
import kotlinx.android.synthetic.main.activity_receipt_details.*

class ReceiptDetailsActivity : AppCompatActivity(), View.OnClickListener, ReceiptMemberAdapter.Listner {

    private var adapter: ReceiptMemberAdapter? = null
    private var isRecyclerViewVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_details)
        ivBack.setOnClickListener(this)
        membersLay.setOnClickListener(this)

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewReceiptMember.itemAnimator = null
        recyclerViewReceiptMember.layoutManager = lm
        recyclerViewReceiptMember.setHasFixedSize(true)

        // feedRecycleView.setItemViewCacheSize(20);
        recyclerViewReceiptMember.setDrawingCacheEnabled(true)
        recyclerViewReceiptMember.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = ReceiptMemberAdapter(this, this)
        recyclerViewReceiptMember.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.membersLay -> {
                if (isRecyclerViewVisible) {
                    recyclerViewReceiptMember.visibility = View.GONE
                    isRecyclerViewVisible = false
                    iv_arrow_expand.setImageResource(R.drawable.ic_keyboard_arrow_down)
                } else {
                    recyclerViewReceiptMember.visibility = View.VISIBLE
                    isRecyclerViewVisible = true
                    iv_arrow_expand.setImageResource(R.drawable.ic_keyboard_arrow_up)
                }
                Util.setRotation(iv_arrow_expand, false)
            }
        }
    }

    override fun onMemberItemClick(pos: Int) {
        Toast.makeText(this, "Position : " + pos, Toast.LENGTH_SHORT).show()
    }
}
