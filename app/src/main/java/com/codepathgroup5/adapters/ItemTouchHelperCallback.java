package com.codepathgroup5.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by kristianss27 on 11/18/16.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final ItemTouchHelperAdapter itemTouchHelperAdapter;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        itemTouchHelperAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return itemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        itemTouchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition(),direction);
    }
}
