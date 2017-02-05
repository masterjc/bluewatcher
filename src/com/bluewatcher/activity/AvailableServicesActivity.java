package com.bluewatcher.activity;

import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bluewatcher.R;
import com.bluewatcher.ServicesContainer;
import com.bluewatcher.ble.Service;

public class AvailableServicesActivity extends Activity {

	private ListView availableServicesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_available_services);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		availableServicesList = (ListView) findViewById(R.id.available_services_list);
		availableServicesList.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.setEnabled(false);
				return true;
			}
		});

		String servicesData = getIntent().getStringExtra(ServicesContainer.SERVICES_INFO_EXTRA);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_list_item_checked);
		if (servicesData == null) {
			availableServicesList.setAdapter(adapter);
			return;
		}

		ServicesContainer container = ServicesContainer.fromIntentData(servicesData);
		availableServicesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Set<Service> services = container.getServices();
		for (Service service : services) {
			adapter.add(getApplicationContext().getString(service.getDescriptionResourceId()));
		}
		availableServicesList.setAdapter(adapter);
		int serviceId = 0;
		for (Service service : services) {

			availableServicesList.setItemChecked(serviceId, service.isAvailable());
			serviceId++;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.available_services, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
}
