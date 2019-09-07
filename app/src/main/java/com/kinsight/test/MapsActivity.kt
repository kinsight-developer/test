package com.kinsight.test

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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    }

    /**
     * ユーザー名入力ダイアログでキャンセルボタンをタップしたときの処理
     * @method userNameDialogCancel
     */
    fun userNameDialogCancel() {
        // キャンセルをタップしたときはアプリ終了
        moveTaskToBack(true);
    }

}
