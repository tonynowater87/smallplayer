package com.tonynowater.smallplayer.util.google;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

// TODO: 2017/10/6 Release版的Google登入會失敗 (因為SHA1不一樣)
/**
 * Google登入並取得可供存取資料的token
 * Created by tonynowater on 2017/8/26.
 */
public class GoogleLoginUtil implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = GoogleLoginUtil.class.getSimpleName();
    private static final int RC_GOOGLE_LOGIN = 100;
    private static final String AUTH_YOUTUBE = "https://www.googleapis.com/auth/youtube";

    public interface OnGoogleLoginCallBack {
        void onGoogleLoginSuccess(String authToken);
        void onGoogleLoginFailure();
    }

    private GoogleApiClient mGoogleApiClient;
    private OnGoogleLoginCallBack mCallback;

    /**
     * @param mFragmentActivity
     * @param mCallback
     */
    public GoogleLoginUtil(FragmentActivity mFragmentActivity, OnGoogleLoginCallBack mCallback) {
        this.mCallback = mCallback;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(context.getString(R.string.default_web_client_id))//加這行登入成功後才取得到IdToken，但不知用來做什麼，先註解
                .requestEmail()//加這行登入成功後才取得到Account
                .build();
        //Log.d(TAG, "GoogleLoginUtil default_web_client_id : " + context.getString(R.string.default_web_client_id));
        //Log.d(TAG, "GoogleLoginUtil google_app_id : " + context.getString(R.string.google_app_id));
        //Log.d(TAG, "GoogleLoginUtil google_api_key : " + context.getString(R.string.google_api_key));

        mGoogleApiClient = new GoogleApiClient.Builder(mFragmentActivity)
                .enableAutoManage(mFragmentActivity, this)//此行啟用表示會自動在onStart及onStop中做connect()及disconnect()的動作
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage() + "\t" + connectionResult.getErrorCode());
        mCallback.onGoogleLoginFailure();
    }

    /** For Activity Google登入 */
    public void googleSignIn(final FragmentActivity fragmentActivity) {
        Log.d(TAG, "googleSignIn: ");
        //每次登入前都先登出
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    fragmentActivity.startActivityForResult(intent, RC_GOOGLE_LOGIN);
                } else {
                    mCallback.onGoogleLoginFailure();
                }
            }
        });
    }

    /** For Fragment Google登入 */
    public void googleSignIn(final Fragment fragment) {
        Log.d(TAG, "googleSignIn: ");
        //每次登入前都先登出
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    fragment.startActivityForResult(intent, RC_GOOGLE_LOGIN);
                } else {
                    mCallback.onGoogleLoginFailure();
                }
            }
        });
    }

    /**
     * 是否Google成功登入
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GOOGLE_LOGIN && resultCode == FragmentActivity.RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, "onActivityResult google login success : " + account.getEmail() + "\n" + account.getIdToken());
                new GetTokenAyncTask().execute(account.getAccount());
            } else {
                Log.e(TAG, "onActivityResult: " + result.getStatus().getStatusMessage());
                mCallback.onGoogleLoginFailure();
            }
        } else {
            Log.e(TAG, "onActivityResult:RC_CODE OR RESULT_CODE Not right");
            mCallback.onGoogleLoginFailure();
        }
    }

    private class GetTokenAyncTask extends AsyncTask<Account, Void, String> {

        @Override
        protected String doInBackground(Account... accounts) {
            String scopes = "oauth2:" + AUTH_YOUTUBE;
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(mGoogleApiClient.getContext(), accounts[0], scopes);
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (UserRecoverableAuthException e) {
                // TODO: 2017/9/1 出現此錯誤需要做重新登入的動作
                Log.e(TAG, "UserRecoverableAuthException: " + e.getMessage());
            } catch (GoogleAuthException e) {
                Log.e(TAG, "GoogleAuthException: " + e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            Log.d(TAG, "onActivityResult: getToken " + token);
            if (token != null) {
                mCallback.onGoogleLoginSuccess(token);
            } else {
                mCallback.onGoogleLoginFailure();
            }
        }
    }
}

