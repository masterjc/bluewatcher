package com.bluewatcher.config;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @version $Revision$
 */
public class OnConfigurationOptionSelected implements OnItemClickListener {
	private Activity parent;
	private ListView listView;

	public OnConfigurationOptionSelected(Activity parent, ListView view) {
		this.parent = parent;
		this.listView = view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		ConfigurationOption selected = (ConfigurationOption) listView.getItemAtPosition(position);
		Intent intent = new Intent(parent, selected.getActivityClass());
		if (selected.getConfigurationOptionData() != null) {
			intent.putExtra(selected.getConfigurationOptionData().getIntentDataKey(), selected.getConfigurationOptionData().getIntentDataValue());
		}
		parent.startActivityForResult(intent, 0);
	}
}
