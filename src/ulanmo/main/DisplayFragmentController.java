package ulanmo.main;

import ulanmo.main.handlers.AbstractHandler;
import ulanmo.main.handlers.Communications;
import ulanmo.main.util.Utility;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

public abstract class DisplayFragmentController extends FragmentActivity
		implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	protected Communications comm;
	protected Location mCurrentLocation;
	protected LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		comm = new Communications(this);
		mLocationClient = new LocationClient(this, this, this);
	}

	public boolean requestAllBarangays(AbstractHandler barangayHandler) {
		return comm.requestAllBarangays(barangayHandler);
	}

	public boolean requestAllStations(AbstractHandler stationHandler) {
		return comm.requestAllBarangays(stationHandler);
	}

	public Communications getCommunications() {
		return comm;
	}

	@Override
	public void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	public void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

		mCurrentLocation = mLocationClient.getLastLocation();
		requestFavorites();

		LatLng myPos = new LatLng(14.52941820, 121.05085552);
		if (mCurrentLocation != null
				&& Utility.isInBound(new LatLng(mCurrentLocation.getLatitude(),
						mCurrentLocation.getLongitude())))
			myPos = new LatLng(mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude());

		if (myPos != null) {
			requestMeasurements(myPos, true);
		}
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			// showErrorDialog(connectionResult.getErrorCode());
		}
	}

	public abstract void mapClicked(LatLng point);

	public abstract void switchToMap();

	public abstract void switchToRain();

	public abstract void requestMeasurements(LatLng position, boolean geocode);

	public abstract void requestFavorites();

	public abstract void addMapMarker(LatLng position, String label,
			BitmapDescriptor bitmap, boolean animate, boolean clear,
			boolean visible);

	public abstract void changeActiveMarker(LatLng position, String label,
			BitmapDescriptor bitmap, boolean animate);

	public abstract void setMapCenter(LatLng position);
}
