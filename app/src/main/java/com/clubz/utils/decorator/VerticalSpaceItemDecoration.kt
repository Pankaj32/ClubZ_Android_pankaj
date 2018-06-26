package com.clubz.utils.decorator

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class VerticalSpaceItemDecoration(val verticalSpaceHeight: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect?.bottom = verticalSpaceHeight

        if (parent.getChildAdapterPosition(view) != parent.adapter.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    override fun getItemOffsets(outRect: Rect?, itemPosition: Int, parent: RecyclerView?) {
        super.getItemOffsets(outRect, itemPosition, parent)
        outRect?.bottom = verticalSpaceHeight
    }

}