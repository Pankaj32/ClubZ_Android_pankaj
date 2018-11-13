package com.clubz.ui.receipt

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.clubz.R
import com.clubz.ui.receipt.adapter.ReceiptAdapter
import kotlinx.android.synthetic.main.activity_receipt.*

class ReceiptActivity : AppCompatActivity(), ReceiptAdapter.Listner, View.OnClickListener {

    private var adapter: ReceiptAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        ivBack.setOnClickListener(this)
        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewReceipt.itemAnimator = null
        recyclerViewReceipt.layoutManager = lm
        recyclerViewReceipt.setHasFixedSize(true)

        // feedRecycleView.setItemViewCacheSize(20);
        recyclerViewReceipt.setDrawingCacheEnabled(true)
        recyclerViewReceipt.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = ReceiptAdapter(this, this)
        recyclerViewReceipt.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }

    override fun onMemberItemClick(pos: Int) {
        startActivity(Intent(this@ReceiptActivity, ReceiptDetailsActivity::class.java))
    }
}
