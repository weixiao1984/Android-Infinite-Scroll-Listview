/**
 *
 * Copyright 2013 Wei Xiao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package ca.weixiao.demotime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import ca.weixiao.widget.InfiniteScrollListAdapter;

/**
 * A customized list adapter to work with infinite scroll list view demo
 */
public class DemoListAdapter extends InfiniteScrollListAdapter {

	// A placeholder for all the data points
	private List<String> entries = new ArrayList<String>();
	private NewPageListener newPageListener;
	
	// A demo listener to pass actions from view to adapter
	public static abstract class NewPageListener {
		public abstract void onScrollNext();
		public abstract View getInfiniteScrollListView(int position, View convertView, ViewGroup parent);
	}

	public DemoListAdapter(NewPageListener newPageListener) {
		this.newPageListener = newPageListener;
	}

	public void addEntriesToTop(List<String> entries) {
		// Add entries in reversed order to achieve a sequence used in most of messaging/chat apps
		if (entries != null) {
			Collections.reverse(entries);
		}
		// Add entries to the top of the list
		this.entries.addAll(0, entries);
		notifyDataSetChanged();
	}
	
	public void addEntriesToBottom(List<String> entries) {
		// Add entries to the bottom of the list
		this.entries.addAll(entries);
		notifyDataSetChanged();
	}
	
	public void clearEntries() {
		// Clear all the data points
		this.entries.clear();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return entries.size();
	}

	@Override
	public Object getItem(int position) {
		return entries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected void onScrollNext() {
		if (newPageListener != null) {
			newPageListener.onScrollNext();
		}
	}

	@Override
	public View getInfiniteScrollListView(int position, View convertView, ViewGroup parent) {
		if (newPageListener != null) {
			return newPageListener.getInfiniteScrollListView(position, convertView, parent);
		}
		return convertView;
	}

}