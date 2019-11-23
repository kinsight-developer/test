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
import android.view.View
import android.widget.Button
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager

    // 待ち合わせ中フラグ
    // （初期値）0:待ち合わせ前, 1:待ち合わせ中
    private var meetingFlg: Int = 0

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

        // 終了ボタンObject取得
        val btnEnd : Button = findViewById(R.id.btnEnd) as Button
        // 終了ボタンクリックリスナー登録
        btnEnd.setOnClickListener(listener)
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

                // パラメータからの起動ということは、待ち合わせ中ということになる
                meetingFlg = 1

                // この後に「onMapReady」が処理される

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
            // 待ち合わせ前の場合、確認ダイアログ表示
            if (meetingFlg == 0) {
                // 確認ダイアログ表示
                dispMeetingPlaceDialog(tapLocation)
            }

            // 20191123 NISHIZONO Del Start dispMeetingPlaceDialogに移行したためコメントアウト
//            // tapされた位置の緯度経度
//            var location = LatLng(tapLocation.latitude, tapLocation.longitude)
//            val str = String.format(Locale.US, "%f, %f", tapLocation.latitude, tapLocation.longitude)
//            mMap.addMarker(MarkerOptions().position(location).title(str))
//            // タップした場所を真ん中に持ってくる
////            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
//            val context = applicationContext
//            val location1 = BigDecimal.valueOf(tapLocation.latitude).toPlainString()
//            val location2 = BigDecimal.valueOf(tapLocation.longitude).toPlainString()
//            val locations = "緯度：$location1　　経度：$location2"
//            Toast.makeText(context, locations, Toast.LENGTH_LONG).show()
            // 20191123 NISHIZONO Del End
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
//        AlertDialog.Builder(this)
//            //ダイアログ以外の場所をタップしても消えないようにする
//            .setCancelable(false)
//            //タイトル
//            .setTitle("表示名入力")
//            // メッセージ
//            .setMessage("表示する名前を入力してください")
//            .setView(editText)
//            // OKボタンタップ時
//            .setPositiveButton("OK", DialogInterface.OnClickListener { _, _->
//                // 待ち合わせ中の場合（パラメータ起動の場合）、名前チェック
//                if (meetingFlg == 1) {
//                    // 名前の重複チェック
//                    if (!true) {
//                        // 重複時、再度名前入力ダイアログ表示
//
//                    }
//                }
//
//                //userNameDialogOK(editText.toString())
//                userNameDialogOK(editText.text.toString())
//            })
//            // キャンセルボタンタップ時
//            .setNegativeButton("キャンセル", DialogInterface.OnClickListener { _, _->
//                userNameDialogCancel()
//            })
//            .show()
        val adb = AlertDialog.Builder(this)
        adb.setCancelable(false)
        adb.setTitle("表示名入力")
        adb.setMessage("表示する名前を入力してください")
        adb.setView(editText)
        adb.setPositiveButton("OK",null)
        adb.setNegativeButton("キャンセル",null)
        var mdlg = adb.show()

        val buttonOK: Button = mdlg.getButton(DialogInterface.BUTTON_POSITIVE );
        buttonOK.setOnClickListener {
            // 入力チェック
            if (editText.text.toString() == "") {
                // 警告メッセージ表示
                dispAlertDialog("名前を入力してください")
                // 名前入力ダイアログは、そのまま表示
                return@setOnClickListener
            }

            // 待ち合わせ中の場合（パラメータ起動の場合）、名前チェック
            if (meetingFlg == 1) {
                // 名前の重複チェック
                if (!sameNameCheck(editText.text.toString())) {
                    // 重複時、再度名前入力ダイアログ表示
                    dispAlertDialog("名前が重複しています。別の名前を入力してください")
                    // 名前入力ダイアログは、そのまま表示
                    return@setOnClickListener
                }
            }

            // 名前に問題ない場合、クリップボードに転送する（処理不要？？？）
            userNameDialogOK(editText.text.toString())

            // ダイアログを閉じる
            mdlg.dismiss()
        }

        val buttonCancel: Button = mdlg.getButton(DialogInterface.BUTTON_NEGATIVE );
        buttonCancel.setOnClickListener {
            userNameDialogCancel()
        }
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

        // 20191123 NISHIZONO Del Start 起動時に現在位置情報を取得しているから不要では
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                1000)
//        } else {
//            // locationStart()
//
//            if (::locationManager.isInitialized) {
//                locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    1000,
//                    50f,
//                    this)
//            }
//        }
        // 20191123 NISHIZONO Del End

    }

    /**
     * ユーザー名入力ダイアログでキャンセルボタンをタップしたときの処理
     * @method userNameDialogCancel
     */
    fun userNameDialogCancel() {
        // キャンセルをタップしたときはアプリ終了
        finishProc()
    }

    /**
     * 警告ダイアログ表示
     * @method dispAlertDialog
     */
    fun dispAlertDialog(errorMessage: String) {
        // ダイアログ表示
        AlertDialog.Builder(this)
            //ダイアログ以外の場所をタップしても消えないようにする
            .setCancelable(false)
            //タイトル
            .setTitle("警告")
            // メッセージ
            .setMessage(errorMessage)
            // OKボタンタップすると閉じる
            .setPositiveButton("OK", DialogInterface.OnClickListener { _, _->
            })
            .show()
    }

    /**
     * 名前の重複チェック
     * @method sameNameCheck
     * @param name:String（）
     */
    fun sameNameCheck(name: String) : Boolean {
        // TODO-チェック処理
        // 名前チェック
        if (name == "あ") {
            return false
        }
        return true;
    }

    /**
     * 待ち合わせ場所確認ダイアログ表示
     * @method dispMeetingPlaceDialog
     */
    fun dispMeetingPlaceDialog(tapLocation: LatLng) {
        // ダイアログ表示
        AlertDialog.Builder(this)
            //ダイアログ以外の場所をタップしても消えないようにする
            .setCancelable(false)
            //タイトル
            .setTitle("確認")
            //メッセージ
            .setMessage("待ち合わせ場所はここでよろしいですか")
            // OKボタンタップ
            .setPositiveButton("OK", DialogInterface.OnClickListener { _, _->
                // 待ち合わせ場所確認ダイアログ表示
                meetingPlaceDialogOK(tapLocation)
            })
            //キャンセルボタンタップ
            .setNegativeButton("キャンセル", DialogInterface.OnClickListener { _, _->
                //
            })
            .show()
    }

    /**
     * 待ち合わせ場所確認ダイアログでOKボタンをタップしたときの処理
     * @method meetingPlaceDialogOK
     */
    fun meetingPlaceDialogOK(tapLocation: LatLng) {
        // 待ち合わせフラグOn
        meetingFlg = 1

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

        // TODO-待ち合わせ場所、自分の位置の緯度経度をサーバーへ登録

        // TODO-URLの取得

        // TODO-URLをダイアログ表示（URLコピー）

        // TODO-サーバーの不要データ削除

        // TODO-緯度経度更新Timer処理開始（30秒間隔）
        Timer().schedule(locationsUpdateTimerCallback(), 0, 30000)
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

    /**
     * 終了ボタンタップリスナー登録
     */
    val listener = object: View.OnClickListener {
        override fun onClick(v: View?) {
            // 終了確認ダイアログ表示
            dispExitConfirmationDialog()
        }
    }

    /**
     * 終了確認ダイアログ表示
     * @method dispExitConfirmationDialog
     */
    fun dispExitConfirmationDialog() {
        // ダイアログ表示
        AlertDialog.Builder(this)
            //ダイアログ以外の場所をタップしても消えないようにする
            .setCancelable(false)
            //タイトル
            .setTitle("注意")
            // メッセージ
            .setMessage("待ち合わせている方から、あなたの位置がわからなくなりますが、よろしいですか？")
            // OKボタンタップ時、終了
            .setPositiveButton("OK", DialogInterface.OnClickListener { _, _->
                // 終了
                finishProc();
            })
            // キャンセルボタンタップ時、処理なし（ダイアログを閉じるだけ）
            .setNegativeButton("キャンセル", DialogInterface.OnClickListener { _, _->
                //
            })
            .show()
    }

    /**
     * 緯度経度の更新タイマー処理
     * @method locationsUpdateTimerCallback
     */
    class locationsUpdateTimerCallback: TimerTask() {
        override fun run() {
            Log.d("debug", "緯度経度更新処理")
            // TODO-自分の位置情報を更新する
            // TODO-相手の位置情報を更新する
            // TODO-MAPを更新する
        }
    }

    /**
     * 終了処理
     * @method finishProc
     */
    fun finishProc() {
        // TODO-サーバーの不要データ削除

        // 終了
        finish()
    }

}
