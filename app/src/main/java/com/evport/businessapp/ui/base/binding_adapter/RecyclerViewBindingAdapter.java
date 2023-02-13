/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evport.businessapp.ui.base.binding_adapter;

import android.graphics.Color;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.kunminx.architecture.ui.layout_manager.WrapContentGridLayoutManager;
import com.kunminx.architecture.ui.layout_manager.WrapContentLinearLayoutManager;
import com.kunminx.architecture.ui.layout_manager.WrapContentStaggeredGridLayoutManager;
import com.evport.businessapp.utils.SpaceItemDecoration;

import java.util.List;


/**
 * Create by KunMinX at 20/4/18
 */
public class RecyclerViewBindingAdapter {

    @BindingAdapter(value = {"adapter", "submitList", "addItemDecoration",  "spanCountDynamic",  "reverseLayout", "autoScrollToTopWhenInsert", "autoScrollToBottomWhenInsert"}, requireAll = false)
    public static void bindList(RecyclerView recyclerView, ListAdapter adapter, List list,
                                boolean addItemDecoration,int spanCountDynamic, boolean reverseLayout,  boolean autoScrollToTopWhenInsert, boolean autoScrollToBottomWhenInsert) {

        if (recyclerView != null && list != null) {

            if (recyclerView.getAdapter() == null) {

                if (recyclerView.getLayoutManager() != null) {

                    if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                        int spanCount = ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                        if (spanCountDynamic>0)
                            spanCount = spanCountDynamic;
                        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(recyclerView.getContext(), spanCount));

                    } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        int or = ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation();
                        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(recyclerView.getContext(), or, false));

                    } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                        int spanCount = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                        int orientation = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getOrientation();
                        recyclerView.setLayoutManager(new WrapContentStaggeredGridLayoutManager(spanCount, orientation));
                    }
                }
                if (addItemDecoration)
                    recyclerView.addItemDecoration(new SpaceItemDecoration(1, Color.LTGRAY, LinearLayoutManager.VERTICAL, 10, 10));

                recyclerView.setAdapter(adapter);

                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        if (autoScrollToTopWhenInsert) {
                            recyclerView.scrollToPosition(0);
                        } else if (autoScrollToBottomWhenInsert) {
                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount());
                        }
                    }
                });
            }

            adapter.submitList(list);
        }else {
        }
    }

    @BindingAdapter(value = {"notifyCurrentListChanged"})
    public static void notifyListChanged(RecyclerView recyclerView, boolean notify) {
        if (notify && recyclerView != null && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
