Android-Infinite-Scroll-Listview
================================

This project aims to provide a reusable listview widget with infinite scrolling capability for asynchronous data loading and displaying.

![Screenshot-1](https://lh6.ggpht.com/JhQPHz3_XehNRV1mrKYffaecLZ58nVYC_uSqOWi8CGvwZrG5tXte5lAQASqlck-2QMc=h400)
![Screenshot-2](https://lh3.ggpht.com/aLD-kad5CFXjc0lMAXVJzVt8HETFXgjjds4v8xwSPnqe6EqK40aqb3K6LVbPpIIWV1Q=h400)
![Screenshot-3](https://lh3.ggpht.com/9MDDStau27lBaJdOzZTf9wukMO9OiH0beoBHEx0ui-b001DCog3SPeD5mpijmT8bRlg=h400)
![Screenshot-4](https://lh6.ggpht.com/-Xvkir_c7G9UE_MclHiIVpgWHlGI0ai1x7T9SuAp48HJftL6KSjC9lhm9A7Vl-uapb0o=h400)

## Features
 * Users scroll up to top or down to bottom of the listview widget to load more data
 * Upon data loading finishes, listview widget automatically scrolls to the top/bottom, or stays where it was
 * Self-explanatory demo code

Support Android API Level 8+

## Quick Setup

#### 1. Include library

**Manual:**
 * Download library from https://github.com/weixiao1984/Android-Infinite-Scroll-Listview/tree/master/library
 * Import the library as an existing Android code into workspace
 * In Properties for your project, add the imported project as a library

#### 2. Adapter class

 * Create an adapter which extends InfiniteScrollListAdapter
 * Inside the adapter create an interface or abstract class so later actions in the view can be propagated to the customized adapter

``` java
public static abstract class NewPageListener {
	public abstract void onScrollNext();
	public abstract View getInfiniteScrollListView(int position, View convertView, ViewGroup parent);
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
```

#### 3. Activity class

1. Loading Mode
 * Either the list view scrolls to the top or the bottom
 * Use setLoadingMode(LoadingMode loadingMode) to specify one of the following two loading modes
``` java
enum LoadingMode {SCROLL_TO_TOP, SCROLL_TO_BOTTOM};
```

2. Stop Position 
 * When the data loading finishes, stop position determines weather the list view should automatically scroll up to the start of the list,
 * scroll down to the end of the list, or remains where it was
 * Use setStopPosition(StopPosition stopPosition) to specify one of the following three stop positions
``` java
enum StopPosition {START_OF_LIST, END_OF_LIST, REMAIN_UNCHANGED}
```

3. Use AsyncTask or IntentService to process your data, as shown in the demo code https://github.com/weixiao1984/Android-Infinite-Scroll-Listview/tree/master/demo
