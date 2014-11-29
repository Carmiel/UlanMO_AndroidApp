package ulanmo.main;

import ulanmo.main.fragments.MapViewFragment;
import ulanmo.main.fragments.RainMeasurementFragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

public class DisplayActivity extends DisplayFragmentController {

	private MapViewFragment mapViewFragment;
	private RainMeasurementFragment rainFragment;
	private boolean isMultipane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("Display", "Is fragment_container2 null? " + (findViewById(R.id.fragment_container2) == null));
		
		if (findViewById(R.id.fragment_container2) != null) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}

		setContentView(R.layout.activity_display);
		MapsInitializer.initialize(getApplicationContext());

		rainFragment = new RainMeasurementFragment();
		mapViewFragment = new MapViewFragment();

		if (findViewById(R.id.fragment_container) != null) {

			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, rainFragment).commit();
			if (findViewById(R.id.fragment_container2) != null) {
				isMultipane = true;
				getSupportFragmentManager().beginTransaction()
						.add(R.id.fragment_container2, mapViewFragment)
						.commit();
			} else {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.fragment_container, mapViewFragment)
						.hide(mapViewFragment).commit();
				isMultipane = false;
			}
		}
	}

	public void switchToMap() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right,
						R.anim.slide_out_left).hide(rainFragment)
				.show(mapViewFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void switchToRain() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_left,
						R.anim.slide_out_right).hide(mapViewFragment)
				.show(rainFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isMultipane)
			switchToRain();
		setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.design, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem checkable = menu.findItem(R.id.toggle_demo);
		checkable.setChecked(comm.isDemo());
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.toggle_demo:
			comm.setDemo(!comm.isDemo());
			item.setChecked(comm.isDemo());
			return true;
		default:
			return false;
		}
	}

	public void requestMeasurements(LatLng position, boolean geocode) {
		rainFragment.requestInterpolation(position.latitude,
				position.longitude, geocode);
	}

	public void mapClicked(LatLng point) {
		if (!isMultipane)
			switchToRain();
		requestMeasurements(point, true);
	}

	public void addMapMarker(LatLng position, String label,
			BitmapDescriptor bitmap, boolean animate, boolean clear,
			boolean visible) {
		mapViewFragment.addMapMarker(position, label, bitmap, animate, clear,
				visible);
	}

	public void changeActiveMarker(LatLng position, String label,
			BitmapDescriptor bitmap, boolean animate) {
		mapViewFragment.changeActiveMarker(position, label, bitmap, animate);
	}

	public void setMapCenter(LatLng position) {
		mapViewFragment.setMapCenter(position);
	}

	private boolean setUpMapIfNeeded() {
		if (!isMultipane)
			return false;
		return mapViewFragment.setUpMapIfNeeded();
	}

	public void requestFavorites() {
		rainFragment.requestBookmarks();
	}
}
