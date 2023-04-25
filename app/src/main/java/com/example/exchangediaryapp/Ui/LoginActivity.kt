package com.example.exchangediaryapp.Ui

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.exchangediaryapp.Objects.UserInfoObject
import com.example.exchangediaryapp.R
import com.example.exchangediaryapp.databinding.ActivityIntroBinding
import com.example.exchangediaryapp.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

//nO1fiCSpk16e1EWp/s6yS+wEbR8=
class LoginActivity : AppCompatActivity() {

    // 내부변수
    private val mViewBinding: ActivityLoginBinding by lazy { // 뷰바인딩 사용
        ActivityLoginBinding.inflate(
            layoutInflater
        )
    }

    val scopes = listOf("account_email", "friends")

    lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

        KakaoSdk.init(this, getString(R.string.kakao_native_key))

        setKakaoCallback()

        mViewBinding.btnKakaoLogin.setOnClickListener{
            btnKakaoLogin()
        }
    }


    fun btnKakaoLogin() {
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback = kakaoCallback)
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }
    fun setKakaoCallback() {
        kakaoCallback = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Log.d("[카카오로그인]","접근이 거부 됨(동의 취소)")
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Log.d("[카카오로그인]","유효하지 않은 앱")
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Log.d("[카카오로그인]","인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Log.d("[카카오로그인]","요청 파라미터 오류")
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Log.d("[카카오로그인]","유효하지 않은 scope ID")
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Log.d("[카카오로그인]","설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Log.d("[카카오로그인]","서버 내부 에러")
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Log.d("[카카오로그인]","앱이 요청 권한이 없음")
                    }
                    else -> { // Unknown
                        Log.d("[카카오로그인]","기타 에러")
                    }
                }
            }
            else if (token != null) {
                Log.d("[카카오로그인]","로그인에 성공하였습니다.\n${token.accessToken}")
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    UserApiClient.instance.me { user, error ->


                        var scopes = mutableListOf<String>()

                        if (user!!.kakaoAccount?.emailNeedsAgreement == true) { scopes.add("account_email") }
                        if (user!!.kakaoAccount?.birthdayNeedsAgreement == true) { scopes.add("birthday") }
                        if (user!!.kakaoAccount?.birthyearNeedsAgreement == true) { scopes.add("birthyear") }
                        if (user!!.kakaoAccount?.genderNeedsAgreement == true) { scopes.add("gender") }
                        if (user!!.kakaoAccount?.phoneNumberNeedsAgreement == true) { scopes.add("phone_number") }
                        if (user!!.kakaoAccount?.profileNeedsAgreement == true) { scopes.add("profile") }
                        if (user!!.kakaoAccount?.ageRangeNeedsAgreement == true) { scopes.add("age_range") }
                        if (user!!.kakaoAccount?.ciNeedsAgreement == true) { scopes.add("account_ci") }

                        if (scopes.count() > 0) {
                            Log.d(TAG, "사용자에게 추가 동의를 받아야 합니다.")

                            // OpenID Connect 사용 시
                            // scope 목록에 "openid" 문자열을 추가하고 요청해야 함
                            // 해당 문자열을 포함하지 않은 경우, ID 토큰이 재발급되지 않음
                            // scopes.add("openid")

                            //scope 목록을 전달하여 카카오 로그인 요청
                            UserApiClient.instance.loginWithNewScopes(this, scopes) { token, error ->
                                if (error != null) {
                                    Log.e(TAG, "사용자 추가 동의 실패", error)
                                } else {
                                    Log.d(TAG, "allowed scopes: ${token!!.scopes}")

                                    // 사용자 정보 재요청
                                    UserApiClient.instance.me { user, error ->
                                        if (error != null) {
                                            Log.e(TAG, "사용자 정보 요청 실패", error)
                                        }
                                        else if (user != null) {
                                            Log.i(TAG, "사용자 정보 요청 성공")
                                        }
                                    }
                                }
                            }
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            else {
                Log.d("카카오로그인", "토큰==null error==null")
            }
        }
    }



}