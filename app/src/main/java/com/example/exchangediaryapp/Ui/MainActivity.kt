package com.example.exchangediaryapp.Ui

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.exchangediaryapp.Objects.UserInfoObject
import com.example.exchangediaryapp.R
import com.example.exchangediaryapp.databinding.ActivityIntroBinding
import com.example.exchangediaryapp.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {

    // 내부변수
    private val mViewBinding: ActivityMainBinding by lazy { // 뷰바인딩 사용
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    var mUserInfoObject = UserInfoObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

        getToken()
        getUserInfo()
        CheckUserAutority()
        CheckFriendAutority()
    }

    // 유저의 토큰 정보를 불러오는 함수
    fun getToken() {
        // 토큰 정보 보기
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e(TAG, "토큰 정보 보기 실패", error)
            }
            else if (tokenInfo != null) {
                Log.i(TAG, "토큰 정보 보기 성공" +
                        "\n회원번호: ${tokenInfo.id}" +
                        "\n만료시간: ${tokenInfo.expiresIn} 초")
            }
        }
    }

    fun LogOut() {
        // 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }
            else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    // 사용자 정보 요청 (기본)
    fun getUserInfo() {
        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                mUserInfoObject.UserId = user.id
                mUserInfoObject.UserEmail = user.kakaoAccount?.email
                mUserInfoObject.UserNickname = user.kakaoAccount?.profile?.nickname
                mUserInfoObject.UserProfile = user.kakaoAccount?.profile?.thumbnailImageUrl

                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
            }
        }
    }

    //유저 동의 항목 조회
    fun CheckUserAutority() {
        UserApiClient.instance.scopes { scopeInfo, error->
            if (error != null) {
                Log.e(TAG, "동의 정보 확인 실패", error)
            }else if (scopeInfo != null) {
                Log.i(TAG, "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
            }
        }
    }

    fun CheckFriendAutority() {
        // 동의 내역을 조회할 동의 항목 ID의 목록
        val scopes = mutableListOf("account_email", "friends")

        UserApiClient.instance.scopes(scopes) { scopeInfo, error->
            if (error != null) {
                Log.e(TAG, "동의 정보 확인 실패", error)
            }else if (scopeInfo != null) {
                Log.i(TAG, "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
            }
        }
    }

//    fun GetFriendList() {
//        UserApiClient.instance.me { user, error ->
//            if (error != null) {
//                Log.e(TAG, "사용자 정보 요청 실패", error)
//            } else if (user != null) {
//                UserApiClient.instance.friends { friends, error ->
//                    if (error != null) {
//                        Log.e(TAG, "친구 목록 가져오기 실패", error)
//                    } else {
//                        for (friend in friends!!.elements) {
//                            Log.i(TAG, "친구: ${friend.nickname}")
//                        }
//                    }
//                }
//            }
//        }
//    }
}