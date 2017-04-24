package com.velychko.kyrylo.mapandroute.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.velychko.kyrylo.mapandroute.R;
import com.velychko.kyrylo.mapandroute.data.SQLite.DataModel.PlaceModel;
import com.velychko.kyrylo.mapandroute.data.SQLite.DatabaseHelper;
import com.velychko.kyrylo.mapandroute.data.SQLite.DatabaseMaster;
import com.velychko.kyrylo.mapandroute.data.network.PlaceDataModel.JSONResult;
import com.velychko.kyrylo.mapandroute.data.network.RetrofitGoogleMap;
import com.velychko.kyrylo.mapandroute.data.network.RouteDataModel.RouteResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback, TextView.OnEditorActionListener {

    public static final String RADIUS = "1000";
    public static final String STRING_MARKER_FROM = "from";
    public static final String STRING_MARKER_TO = "to";

    GoogleMap map;
    private LocationManager locationManager;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private double fromLatitude = 0.0;
    private double fromLongitude = 0.0;
    private String fromName;
    private Marker markerFrom;
    private double toLatitude = 0.0;
    private double toLongitude = 0.0;
    private String toName;
    private Marker markerTo;
    Polyline polyline;

    EditText etAddressFrom;
    EditText etAddressTo;
    Button btnBuildRoute;
    Button btnClearFrom;
    Button btnClearTo;
    TextView tv_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViewComponents();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViewComponents() {
        etAddressFrom = (EditText) findViewById(R.id.et_address_from);
        etAddressTo = (EditText) findViewById(R.id.et_address_to);
        etAddressFrom.setOnEditorActionListener(this);
        etAddressTo.setOnEditorActionListener(this);

        btnBuildRoute = (Button) findViewById(R.id.btn_build_route);
        btnBuildRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoute();
            }
        });
        btnClearFrom = (Button) findViewById(R.id.btn_clear_from);
        btnClearFrom.setOnClickListener(btnClearFromClickListener());
        btnClearTo = (Button) findViewById(R.id.btn_clear_to);
        btnClearTo.setOnClickListener(btnClearToClickListener());

        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_user_name.setText(getIntent().getStringExtra("user_name"));
//        tv_user_name.setText("qqq");
    }

    private View.OnClickListener btnClearFromClickListener(){
         return new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 etAddressFrom.setText("");
                 if (markerFrom != null) {
                     markerFrom.remove();
                     markerFrom = null;
                 }
                 map.clear();
                 if (markerTo != null) {
                     map.addMarker(new MarkerOptions()
                             .position(new LatLng(markerTo.getPosition().latitude, markerTo.getPosition().longitude))
                             .title(markerTo.getTitle())
                             .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                 }
             }
         };
    }

    private View.OnClickListener btnClearToClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddressTo.setText("");
                if (markerTo != null) {
                    markerTo.remove();
                    markerTo = null;
                }
                map.clear();
                if (markerFrom != null) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(markerFrom.getPosition().latitude, markerFrom.getPosition().longitude))
                            .title(markerFrom.getTitle())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }
        };
    }

    private void checkLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setUpMap();

        loadSavedMarkers();

//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public void setUpMap() {

        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 20));
//        map.animateCamera(CameraUpdateFactory.zoomTo(2), 2000, null);

        //при нажатии на карту - погашение клавиатуры
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideKeyboard();
            }
        });

    }

    private void loadSavedMarkers() {
        Cursor cursor = DatabaseMaster.getInstance(getApplicationContext())
                .getUserPlaces(tv_user_name.getText().toString());
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double lat = cursor.getDouble(cursor.getColumnIndex
                        (DatabaseHelper.Places.COLUMN_LATITUDE));
                double lng = cursor.getDouble(cursor.getColumnIndex
                        (DatabaseHelper.Places.COLUMN_LONGITUDE));
                String name = cursor.getString(cursor.getColumnIndex
                        (DatabaseHelper.Places.COLUMN_NAME));
                String from_or_to = cursor.getString(cursor.getColumnIndex
                        (DatabaseHelper.Places.COLUMN_FROM_OR_TO));

                if (from_or_to.equals(STRING_MARKER_FROM)) {
                    markerFrom = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    etAddressFrom.setText(name);
                    fromName = name;
                    fromLatitude = lat;
                    fromLongitude = lng;
                } else if (from_or_to.equals(STRING_MARKER_TO)){
                    markerTo = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    etAddressTo.setText(name);
                    toName = name;
                    toLatitude = lat;
                    toLongitude = lng;
                }
                cursor.moveToNext();
            }
        }

        if (markerFrom != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    markerFrom.getPosition().latitude, markerFrom.getPosition().longitude), 15));
        } else if (markerTo != null){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    markerTo.getPosition().latitude, markerTo.getPosition().longitude), 15));
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //обновлениетекущего местоположения
            if (markerFrom == null && markerTo == null && currentLatitude == 0.0 && currentLongitude == 0.0) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            }
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            currentLatitude = locationManager.getLastKnownLocation(provider).getLatitude();
            currentLongitude = locationManager.getLastKnownLocation(provider).getLongitude();
            map.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude))
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    //кнопка поиска на виртуальной клавиатуре
    @Override
    public boolean onEditorAction(TextView v, final int actionId, KeyEvent event) {
        final String address;
        boolean moveToNext = false;
        if (v.getId() == etAddressFrom.getId()) {
            address = etAddressFrom.getText().toString();
            moveToNext = true;
        } else if (v.getId() == etAddressTo.getId()) {
            address = etAddressTo.getText().toString();
        } else {
            return false;
        }

//        //запрос на получение мест по ТИПУ
//        RetrofitGoogleMap.getPlacesByType(currentLatitude, currentLongitude, address, RADIUS).enqueue(new Callback<JSONResult>() {
//            @Override
//            public void onResponse(Call<JSONResult> call, Response<JSONResult> response) {
//                if (response.body().status.equals("OK")) {
//                    //сохраняем отдельно полученный ответ и проставляем новые маркеры
////                    lastJSONResult = response;
//                    addMarkersToMap(response);
//                } else {
        //Если по типу не нашли, ищем тексту
        RetrofitGoogleMap.getPlacesByQuery(currentLatitude, currentLongitude, address, RADIUS).enqueue(new Callback<JSONResult>() {
            @Override
            public void onResponse(Call<JSONResult> call, Response<JSONResult> response) {
                addMarkersToMap(response);
            }

            @Override
            public void onFailure(Call<JSONResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error. Check internet connection", Toast.LENGTH_LONG).show();
            }
        });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JSONResult> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Error. Check internet connection", Toast.LENGTH_LONG).show();
//            }
//        });


        return true;

    }

    //добавление новых маркеров на карту
    private void addMarkersToMap(Response<JSONResult> jsonResults) {
//        map.clear();
        if (polyline != null) {
            polyline.remove();
        }
        double lat = 0.0;
        double lng = 0.0;
        View view = this.getCurrentFocus();

        if (jsonResults.body().results.length > 0) {
            lat = jsonResults.body().results[0].geometry.location.lat;
            lng = jsonResults.body().results[0].geometry.location.lng;
//            map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
//                    .title(jsonResults.body().results[0].name));

            if (view.getId() == etAddressFrom.getId()) {
                if (etAddressFrom.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    etAddressTo.requestFocus();
//                    etAddressTo.setSelection(etAddressTo.getText().length());
                }
                if (markerFrom != null) {
                    markerFrom.remove();
                    markerFrom = null;
                }
                markerFrom = map.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(jsonResults.body().results[0].name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                fromLatitude = lat;
                fromLongitude = lng;
                fromName = jsonResults.body().results[0].name;
            } else if (view.getId() == etAddressTo.getId()) {
//            if (etAddressTo.getText().toString().equals("")) {
                if (etAddressTo.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                    etAddressFrom.setSelection(etAddressFrom.getText().length());
                    etAddressFrom.requestFocus();
                }
                if (markerTo != null) {
                    markerTo.remove();
                    markerTo = null;
                }
                markerTo = map.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(jsonResults.body().results[0].name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                toLatitude = lat;
                toLongitude = lng;
                toName = jsonResults.body().results[0].name;
            }

            if (!etAddressTo.getText().toString().equals("") && !etAddressFrom.getText().toString().equals("")) {
                hideKeyboard();
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14));
            map.animateCamera(CameraUpdateFactory.zoomTo(15), 1500, null);

        }
// else if (jsonResults.body().results.length > 1) {
//            for (int i = 0; i < jsonResults.body().results.length; i++) {
//                lat = jsonResults.body().results[i].geometry.location.lat;
//                lng = jsonResults.body().results[i].geometry.location.lng;
//                map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
//                        .title(jsonResults.body().results[i].name));
//            }
//        }

    }

    private void createRoute() {
        if (markerFrom == null) {
            Toast.makeText(getApplicationContext(), "Fill origin point", Toast.LENGTH_SHORT).show();
            return;
        } else if (markerTo == null) {
            Toast.makeText(getApplicationContext(), "Fill destination point", Toast.LENGTH_SHORT).show();
            return;
        }
        //запрос на получение маршрута
        String origin = String.valueOf(markerFrom.getPosition().latitude) + "," + String.valueOf(markerFrom.getPosition().longitude);
        String destination = String.valueOf(markerTo.getPosition().latitude) + "," + String.valueOf(markerTo.getPosition().longitude);
        RetrofitGoogleMap.getRoute(origin, destination).enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                //прорисовка маршрута
                drawRoute(response);
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Route didn't create", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(Response<RouteResponse> response) {
        List<LatLng> mPoints = PolyUtil.decode(response.body().getPoints());
        PolylineOptions line = new PolylineOptions();
        line.width(9f);
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        map.clear();
        for (int i = 0; i < mPoints.size(); i++) {
            if (i == 0) {
                markerFrom.remove();
                MarkerOptions startMarkerOptions = new MarkerOptions()
                        .position(mPoints.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerFrom = map.addMarker(startMarkerOptions);
            } else if (i == mPoints.size() - 1) {
                markerTo.remove();
                MarkerOptions endMarkerOptions = new MarkerOptions()
                        .position(mPoints.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerTo = map.addMarker(endMarkerOptions);
            }
            line.add(mPoints.get(i));
            latLngBuilder.include(mPoints.get(i));
        }
        if (polyline != null) {
            polyline.setPoints(new ArrayList<LatLng>());

        }
        polyline = map.addPolyline(line);
        int size = getResources().getDisplayMetrics().widthPixels;
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        map.moveCamera(track);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseMaster.getInstance(getApplicationContext())
                .deleteCurrentUserPlaces(tv_user_name.getText().toString());

        if (markerFrom != null) {
            DatabaseMaster.getInstance(getApplicationContext())
                    .addPlace(new PlaceModel(tv_user_name.getText().toString(),
                            fromLatitude, fromLongitude, fromName), STRING_MARKER_FROM);
        }
        if (markerTo != null) {
            DatabaseMaster.getInstance(getApplicationContext())
                    .addPlace(new PlaceModel(tv_user_name.getText().toString(),
                            toLatitude, toLongitude, toName), STRING_MARKER_TO);
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        }
        super.onBackPressed();
    }


}
