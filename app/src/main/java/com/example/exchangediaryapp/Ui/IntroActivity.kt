package com.example.exchangediaryapp.Ui

import Utils.NAlert
import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.exchangediaryapp.R
import com.example.exchangediaryapp.databinding.ActivityIntroBinding
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class IntroActivity : AppCompatActivity() {

    // 정적변수
    companion object {
        private const val TAG = "ActivityIntro"
    }

    // 내부변수
    private val mViewBinding: ActivityIntroBinding by lazy { // 뷰바인딩 사용
        ActivityIntroBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        mContext = this

        KakaoSdk.init(this, getString(R.string.kakao_native_key))

        //로컬DB 초기화
        initLocalDB()

        // 퍼미션 체크
        checkRuntimePermissions()

    }

    //로컬 DB 초기화 함수
    private fun initLocalDB() {}

    // 퍼미션 체크
    private fun checkRuntimePermissions() {
        // 버전별로 권한 옵션이 다름
        if (Build.VERSION.SDK_INT >= 30) {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // MANAGE_EXTERNAL_STORAGE 권한을 요청 하지 않아도 됨
                        Log.d(mContext.toString(), Build.VERSION.SDK_INT.toString())
                        initApp()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>, permissionToken: PermissionToken
                ) {
                    NAlert.showToastShort(
                        mContext, resources.getString(R.string.text_permission_error)
                    )
                }
            }).check()

        } else if (Build.VERSION.SDK_INT >= 23){
            Dexter.withContext(this).withPermissions(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Log.d(mContext.toString(), Build.VERSION.SDK_INT.toString())
                        initApp()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>, permissionToken: PermissionToken
                ) {
                    NAlert.showToastShort(
                        mContext, resources.getString(R.string.text_permission_error)
                    )
                }
            }).check()
        } else {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Log.d(mContext.toString(), Build.VERSION.SDK_INT.toString())
                        initApp()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>, permissionToken: PermissionToken
                ) {
                    NAlert.showToastShort(
                        mContext, resources.getString(R.string.text_permission_error)
                    )
                }
            }).check()
        }
    }

    private fun LoginCheck(){
        // 카카오 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else if (tokenInfo != null) {
//                val intent = Intent(this@MainActivity, MyPageActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }
    }

    private fun initApp() {
        val intent = Intent(mContext, LoginActivity::class.java)
        startActivity(intent)
    }
}