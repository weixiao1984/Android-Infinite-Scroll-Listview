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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import ca.weixiao.demotime.DemoListAdapter.NewPageListener;
import ca.weixiao.widget.InfiniteScrollListView;
import ca.weixiao.widget.InfiniteScrollListView.LoadingMode;
import ca.weixiao.widget.InfiniteScrollListView.StopPosition;

/**
 * A demo for the listView with infinite scrolling capability
 * It shows how to make the loading happen when the list view reaches its top or bottom
 * It also shows how to display a customizable view as the loading indicator view either
 * at the top or bottom of the list view
 */
public class InfiniteScrollListViewDemoActivity extends Activity {

	// A setting for how many items should be loaded at once from the server
	private static final int SEVER_SIDE_BATCH_SIZE = 10;
	private static final String GITHUB_LINK = "https://github.com/weixiao1984/Android-Infinite-Scroll-Listview";

	private InfiniteScrollListView demoListView;
	private RadioGroup loadingModeGroup;
	private RadioGroup stopPositionGroup;
	private ImageView githubImageView;
	
	private DemoListAdapter demoListAdapter;
	private BogusRemoteService bogusRemoteService;
	private Handler handler;
	private AsyncTask<Void, Void, List<String>> fetchAsyncTask;

	private Map<String, Integer> sushiMappings;
	private LoadingMode loadingMode = LoadingMode.SCROLL_TO_BOTTOM;
	private StopPosition stopPosition = StopPosition.REMAIN_UNCHANGED;

	public InfiniteScrollListViewDemoActivity () {
		super();
		bogusRemoteService = new BogusRemoteService();
		// Set up the image mapping for data points
		sushiMappings = new LinkedHashMap<String, Integer>();
		sushiMappings.put("Akaki", R.drawable.sushi_akagi);
		sushiMappings.put("Ama Ebi", R.drawable.sushi_amaebi);
	    sushiMappings.put("Anago", R.drawable.sushi_anago);
	    sushiMappings.put("Kuruma Ebi", R.drawable.sushi_ebi);
	    sushiMappings.put("Hamachi", R.drawable.sushi_hamachi);
	    sushiMappings.put("Hirame", R.drawable.sushi_hirame);
	    sushiMappings.put("Hokki", R.drawable.sushi_hokki);
	    sushiMappings.put("Hotate", R.drawable.sushi_hotate);
	    sushiMappings.put("Ika", R.drawable.sushi_ika);
	    sushiMappings.put("Ikura", R.drawable.sushi_ikura);
	    sushiMappings.put("Inari", R.drawable.sushi_inari);
	    sushiMappings.put("Kaibashira", R.drawable.sushi_kaibashira);
	    sushiMappings.put("Kaki", R.drawable.sushi_kaki);
	    sushiMappings.put("Maguro", R.drawable.sushi_maguro);
	    sushiMappings.put("Hon Maguro", R.drawable.sushi_hon_maguro);
	    sushiMappings.put("Maguro Toro", R.drawable.sushi_maguro);
	    sushiMappings.put("Masago", R.drawable.sushi_masago);
	    sushiMappings.put("Mirugai", R.drawable.sushi_mirugai);
	    sushiMappings.put("Saba", R.drawable.sushi_saba);
	    sushiMappings.put("Sake", R.drawable.sushi_sake);
	    sushiMappings.put("Tai", R.drawable.sushi_tai);
	    sushiMappings.put("Tako", R.drawable.sushi_tako);
	    sushiMappings.put("Tamago", R.drawable.sushi_tamago);
	    sushiMappings.put("Tobiko", R.drawable.sushi_seasoned_tobiko);
	    sushiMappings.put("Torigai", R.drawable.sushi_torigai);
	    sushiMappings.put("Unagi", R.drawable.sushi_unagi);
	    sushiMappings.put("Uni", R.drawable.sushi_uni);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		handler = new Handler();

		demoListView = (InfiniteScrollListView) this.findViewById(R.id.infinite_listview_infinitescrolllistview);
		loadingModeGroup = (RadioGroup) this.findViewById(R.id.infinite_listview_radiogroup_loading_mode);
		stopPositionGroup = (RadioGroup) this.findViewById(R.id.infinite_listview_radiogroup_stop_position);
		githubImageView = (ImageView) this.findViewById(R.id.infinite_listview_imageview_github);

		demoListView.setLoadingMode(loadingMode);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		demoListView.setLoadingView(layoutInflater.inflate(R.layout.loading_view_demo, null));
		demoListAdapter = new DemoListAdapter(new NewPageListener() {
			@Override
			public void onScrollNext() {
				fetchAsyncTask = new AsyncTask<Void, Void, List<String>>() {
					@Override
					protected void onPreExecute() {
						// Loading lock to allow only one instance of loading
                    	demoListAdapter.lock();
					}
					@Override
                    protected List<String> doInBackground(Void ... params) {
                    	List<String> result;
                    	// Mimic loading data from a remote service
                    	if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
    						result = bogusRemoteService.getNextMessageBatch(SEVER_SIDE_BATCH_SIZE);
    					} else {
    						result = bogusRemoteService.getNextSushiBatch(SEVER_SIDE_BATCH_SIZE);
    					}
    					return result;
                    }
                    @Override
                    protected void onPostExecute(List<String> result) {
        				if (isCancelled() || result == null || result.isEmpty()) {
        					demoListAdapter.notifyEndOfList();
        				} else {
        					// Add data to the placeholder
        					if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
        						demoListAdapter.addEntriesToTop(result);
        					} else {
            					demoListAdapter.addEntriesToBottom(result);
        					}
        					// Add or remove the loading view depend on if there might be more to load
        					if (result.size() < SEVER_SIDE_BATCH_SIZE) {
        						demoListAdapter.notifyEndOfList();
        					} else {
        						demoListAdapter.notifyHasMore();
        					}
        					// Get the focus to the specified position when loading completes
        					if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
        						switch(stopPosition) {
        							case REMAIN_UNCHANGED:
                						demoListView.setSelection(result.size());
                						break;
        							case START_OF_LIST:
        								demoListView.setSelection(result.size() < SEVER_SIDE_BATCH_SIZE ? 0 : 1);
                    					break;
        							case END_OF_LIST:
                						demoListView.setSelection(1);
    	        						demoListView.smoothScrollToPosition(demoListAdapter.getCount());
        						}
        					} else {
        						if (stopPosition != StopPosition.REMAIN_UNCHANGED) {
        							demoListView.smoothScrollToPosition(stopPosition == StopPosition.START_OF_LIST ? 0 : (result.size() < SEVER_SIDE_BATCH_SIZE ? demoListAdapter.getCount() : demoListAdapter.getCount() - 2));
        						}
        					}
        				}
                    };
                    @Override
                    protected void onCancelled() {
                    	// Tell the adapter it is end of the list when task is cancelled
    					demoListAdapter.notifyEndOfList();
                    }
				}.execute();
			}
			@Override
			public View getInfiniteScrollListView(int position, View convertView, ViewGroup parent) {
				// Customize the row for list view
				if(convertView == null) {
					LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = layoutInflater.inflate(R.layout.row_demo, null);
				}
				String name = (String) demoListAdapter.getItem(position);
				if (name != null) {
					TextView rowName = (TextView) convertView.findViewById(R.id.row_name);
					ImageView rowPhoto = (ImageView) convertView.findViewById(R.id.row_photo);
					rowName.setText(name);
					if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
						rowPhoto.setImageResource(position % 2 == 0 ? R.drawable.conversation_driver : R.drawable.conversation_officer);
					} else {
						rowPhoto.setImageResource(sushiMappings.get(name));
					}
				}
				return convertView;
			}
		});
		demoListView.setAdapter(demoListAdapter);
		// Display a toast when a list item is clicked
		demoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(InfiniteScrollListViewDemoActivity.this, demoListAdapter.getItem(position) + " " + getString(R.string.ordered), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		loadingModeGroup.check(loadingMode == LoadingMode.SCROLL_TO_TOP ? R.id.infinite_listview_radio_scroll_to_top : R.id.infinite_listview_radio_scroll_to_bottom);
		loadingModeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	    		// Cancel a task if exists when loading mode changes
	        	if (fetchAsyncTask != null) {
	    			fetchAsyncTask.cancel(true);
	    		}
	        	// Reset fake remote service when loading mode changes
	        	bogusRemoteService.reset();
	        	// Remove loading view
	        	demoListAdapter.notifyEndOfList();
	        	// Clear all data points
	        	demoListAdapter.clearEntries();
	        	loadingMode = checkedId == R.id.infinite_listview_radio_scroll_to_top ? LoadingMode.SCROLL_TO_TOP : LoadingMode.SCROLL_TO_BOTTOM;
	        	// Overwrite new loading mode
	        	demoListAdapter.setLoadingMode(loadingMode);
	        	demoListView.setLoadingMode(loadingMode);
	        	// Disable row clicks when display messages
	        	demoListAdapter.setRowEnabled(checkedId == R.id.infinite_listview_radio_scroll_to_top ? false : true);
	        	// Manually loads the first page
	        	demoListAdapter.onScrollNext();
	        }
	    });
		stopPositionGroup.check(stopPosition == StopPosition.START_OF_LIST ? R.id.infinite_listview_radio_start_of_list : (stopPosition == StopPosition.END_OF_LIST ? R.id.infinite_listview_radio_end_of_list : R.id.infinite_listview_radio_remain_unchanged));
		stopPositionGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	    		// Cancel a task if exists when stop position setting changes
	        	if (fetchAsyncTask != null) {
	    			fetchAsyncTask.cancel(true);
	    		}
	        	// Reset fake remote service when stop position setting changes
	        	bogusRemoteService.reset();
	        	// Remove loading view
	        	demoListAdapter.notifyEndOfList();
	        	// Clear all data points
	        	demoListAdapter.clearEntries();
	        	stopPosition = checkedId == R.id.infinite_listview_radio_start_of_list ? StopPosition.START_OF_LIST : (checkedId == R.id.infinite_listview_radio_end_of_list ? StopPosition.END_OF_LIST : StopPosition.REMAIN_UNCHANGED);
	        	// Overwrite stop position setting
	        	demoListAdapter.setStopPosition(stopPosition);
	        	demoListView.setStopPosition(stopPosition);
	        	// Manually load the first page
	        	demoListAdapter.onScrollNext();
	        }
	    });
		githubImageView.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_LINK));
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Load the first page to start demo
		demoListAdapter.onScrollNext();
	}
}