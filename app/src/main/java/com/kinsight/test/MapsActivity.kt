package com.kinsight.test

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// タップしてピン止めし、緯度経度を取得（トースト表示）
import java.util.Locale
import java.math.BigDecimal
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.location.LocationProvider
import android.location.Location

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000)
        } else {
            locationStart()

            if (::locationManager.isInitialized) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    50f,
                    this)
            }

        }
    }

    override fun onResume() {
        super.onResume()

        val intent = intent
        val action = intent.action
        if (Intent.ACTION_VIEW == action) {
            val uri = intent.data
            if (uri != null) {
                // パラメータ値を取得
                val param1 = uri.getQueryParameter("test_prm1")
                val param2 = uri.getQueryParameter("test_prm2")
                // 取得したパラメータをトーストで表示
                //Toast.makeText(this, "$param1★$param2", Toast.LENGTH_LONG).show();

//                // 取得したパラメータをクリップボードに格納
//                val clipboardManager =
//                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", "$param1☆$param2"))
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(0.0, 0.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // タップした時のリスナーをセット
        mMap.setOnMapClickListener { tapLocation ->
            // tapされた位置の緯度経度
            var location = LatLng(tapLocation.latitude, tapLocation.longitude)
            val str = String.format(Locale.US, "%f, %f", tapLocation.latitude, tapLocation.longitude)
            mMap.addMarker(MarkerOptions().position(location).title(str))
            // タップした場所を真ん中に持ってくる
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
            val context = applicationContext
            val location1 = BigDecimal.valueOf(tapLocation.latitude).toPlainString()
            val location2 = BigDecimal.valueOf(tapLocation.longitude).toPlainString()
            val locations = "緯度：$location1　　経度：$location2"
            Toast.makeText(context, locations, Toast.LENGTH_LONG).show()
        }

        // ダイアログ表示
        dispUserNameDialog();
    }

    /**
     * ユーザ名入力ダイアログ表示
     * @method dispUserNameDialog
     */
    fun dispUserNameDialog() {
        // ダイアログ表示
        val editText = EditText(this)
        editText.hint = "表示名"
        AlertDialog.Builder(this)
            //ダイアログ以外の場所をタップしても消えないようにする
            .setCancelable(false)
            //タイトル
            .setTitle("表示名入力")

            .setMessage("表示する名前を入力してください")
            .setView(editText)
            .setPositiveButton("OK", DialogInterface.OnClickListener { _, _->
                //userNameDialogOK(editText.toString())
                userNameDialogOK(editText.text.toString())
            })
            .setNegativeButton("キャンセル", DialogInterface.OnClickListener { _, _->
                userNameDialogCancel()
            })
            .show()
    }

    /**
     * ユーザー名入力ダイアログでOKボタンをタップしたときの処理
     * @method userNameDialogOK
     *
     */
    fun userNameDialogOK(userName : String) {
        // OKをタップしたときの処理
        Toast.makeText(this, "$userName", Toast.LENGTH_LONG).show()

        // 取得したパラメータをクリップボードに格納
        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("label", userName))

        // setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000)
        } else {
            // locationStart()

            if (::locationManager.isInitialized) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    50f,
                    this)
            }

        }
    }

    /**
     * ユーザー名入力ダイアログでキャンセルボタンをタップしたときの処理
     * @method userNameDialogCancel
     */
    fun userNameDialogCancel() {
        // キャンセルをタップしたときはアプリ終了
        moveTaskToBack(true);
    }

    @SuppressLint("MissingPermission")
    private fun locationStart() {
        Log.d("debug", "locationStart()")

        // Instances of LocationManager class must be obtained using Context.getSystemService(Class)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled")
        } else {
            // to prompt setting up GPS
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            Log.d("debug", "not gpsEnable, startActivity")
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)

            Log.d("debug", "checkSelfPermission false")
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            50f,
            this)

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.AVAILABLE ->
                Log.d("debug", "LocationProvider.AVAILABLE")
            LocationProvider.OUT_OF_SERVICE ->
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE")
            LocationProvider.TEMPORARILY_UNAVAILABLE ->
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE")
        }
    }

    override fun onLocationChanged(location: Location) {
        var loc1 = location.getLatitude()
        var loc2 = location.getLongitude()
        var latlng = LatLng(loc1, loc2)
        val str = String.format(Locale.US, "%f, %f", loc1, loc2)
        mMap.addMarker(MarkerOptions().position(latlng).title(str))
        // タップした場所を真ん中に持ってくる
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13f))

        val context = applicationContext
        val str1 = "Latitude:" + loc1
        val str2 = "Longtude:" + loc2
        val str3 = str1 + str2
        Toast.makeText(context, str3, Toast.LENGTH_LONG).show()

//        val obj = NCMBObject("TestClass")
//
//        // 検索
//        val query = NCMBQuery<NCMBObject>("TestClass")
//        query.whereEqualTo("objectId", "8mnAQR48G2rdXJjd")
//        query.findInBackground { results, e ->
//            if (e != null) {
//                //検索失敗時の処理
//            } else {
//                //検索成功時の処理
//                var res = results.get(0)
//                obj.setObjectId(res.objectId)
//                try {
//                    var msg = "" + loc1 + ", " + loc2
//                    obj.put("message", msg)
//                    obj.saveInBackground { e ->
//                        if (e != null) {
//                            //保存に失敗した場合の処理
//                        } else {
//                            //保存に成功した場合の処理
//                        }
//                    }
//                } catch (e: NCMBException) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }


}
