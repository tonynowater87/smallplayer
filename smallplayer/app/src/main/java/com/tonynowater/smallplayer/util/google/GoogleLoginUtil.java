package com.tonynowater.smallplayer.util.google;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.Logger;

/**
 * Google登入並取得可存取資料的token
 * Created by tonynowater on 2017/8/26.
 */
public class GoogleLoginUtil implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = GoogleLoginUtil.class.getSimpleName();
    private static final int RC_GOOGLE_LOGIN = 100;
    private static final int RC_UserRecoverableAuth = 200;
    private static final String AUTH_YOUTUBE = "https://www.googleapis.com/auth/youtube";
    private static final int ACTIVITY_LOGIN = 0;
    public static final int FRAGMENT_LOGIN = 1;

    public interface OnGoogleLoginCallBack {
        void onGoogleLoginSuccess();
        void onGoogleLoginFailure();
    }

    private GoogleApiClient mGoogleApiClient;
    private OnGoogleLoginCallBack mCallback;
    private FragmentActivity mFragmentActivity;
    private Fragment mFragment;
    private int mLoginMode;

    /**
     * @param mFragmentActivity
     * @param mCallback
     */
    public GoogleLoginUtil(FragmentActivity mFragmentActivity, OnGoogleLoginCallBack mCallback) {
        this(mFragmentActivity, null, 0, mCallback);
    }

    /**
     * @param mFragmentActivity
     * @param mCallback
     */
    public GoogleLoginUtil(FragmentActivity mFragmentActivity, Fragment fragment, int mLoginMode, OnGoogleLoginCallBack mCallback) {
        this.mLoginMode = mLoginMode;
        this.mCallback = mCallback;
        this.mFragmentActivity = mFragmentActivity;
        this.mFragment = fragment;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 //.requestIdToken(mFragmentActivity.getString(R.string.default_web_client_id))//加這行登入成功後才取得到IdToken，但不知用來做什麼，先註解
                .requestEmail()//加這行登入成功後才取得到Account
                //取AuthServerCode
                .requestScopes(new Scope(AUTH_YOUTUBE))
                .requestServerAuthCode(mFragmentActivity.getString(R.string.default_web_client_id))
                .build();
        //Logger.getInstance().d(TAG, "GoogleLoginUtil default_web_client_id : " + context.getString(R.string.default_web_client_id));
        //Logger.getInstance().d(TAG, "GoogleLoginUtil google_app_id : " + context.getString(R.string.google_app_id));
        //Logger.getInstance().d(TAG, "GoogleLoginUtil google_api_key : " + context.getString(R.string.google_api_key));

        mGoogleApiClient = new GoogleApiClient.Builder(mFragmentActivity)
                .enableAutoManage(mFragmentActivity, fragment == null ? 0 : fragment.hashCode(), this)//此行啟用表示會自動在onStart及onStop中做connect()及disconnect()的動作
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage() + "\t" + connectionResult.getErrorCode());
        mCallback.onGoogleLoginFailure();
    }

    public void onStop() {
        mGoogleApiClient.stopAutoManage(mFragmentActivity);
        mGoogleApiClient.disconnect();
    }

    /** For Activity Google登入 */
    public void googleSignIn(final FragmentActivity fragmentActivity) {
        Logger.getInstance().d(TAG, "googleSignIn: ");
        if (mGoogleApiClient.isConnected()) {
            //若有登入就先登出
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
        } else {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            fragmentActivity.startActivityForResult(intent, RC_GOOGLE_LOGIN);
        }
    }

    /** For Fragment Google登入 */
    public void googleSignIn(final Fragment fragment) {
        Logger.getInstance().d(TAG, "googleSignIn: ");
        if (mGoogleApiClient.isConnected()) {
            //若有登入就先登出
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
        } else {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            fragment.startActivityForResult(intent, RC_GOOGLE_LOGIN);
        }
    }

    /**
     * 是否Google成功登入
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            switch (requestCode) {
                case RC_GOOGLE_LOGIN:
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if (result.isSuccess()) {
                        GoogleSignInAccount account = result.getSignInAccount();
                        Logger.getInstance().d(TAG, "onActivityResult google login success : "
                                + account.getEmail()
                                + "\n" + account.getIdToken()
                                + "\n" + account.getServerAuthCode());
                        U2BApi.newInstance().getYoutubeToken(account.getServerAuthCode(), new U2BApi.OnRequestTokenCallback() {
                            @Override
                            public void onSuccess() {
                                mCallback.onGoogleLoginSuccess();
                            }

                            @Override
                            public void onFailure() {
                                mCallback.onGoogleLoginFailure();
                            }
                        });
                    } else {
                        Log.e(TAG, "onActivityResult: " + result.getStatus().getStatusMessage());
                        mCallback.onGoogleLoginFailure();
                    }
                    break;
                case RC_UserRecoverableAuth:
                    switch (mLoginMode) {
                        case ACTIVITY_LOGIN:
                            googleSignIn(mFragmentActivity);
                            break;
                        case FRAGMENT_LOGIN:
                            googleSignIn(mFragment);
                            break;
                        default:
                            mCallback.onGoogleLoginFailure();
                    }
                    break;
            }
        } else {
            Log.e(TAG, "onActivityResult:RC_CODE OR RESULT_CODE Not right");
            mCallback.onGoogleLoginFailure();
        }
    }
//用GoogleAuthUtil取得AccessToken的方式
//    private class GetTokenAyncTask extends AsyncTask<Account, Void, String> {
//        @Override
//        protected String doInBackground(Account... accounts) {
//            String scopes = "oauth2:" + AUTH_YOUTUBE;
//            String token = null;
//            try {
//                token = GoogleAuthUtil.getToken(mGoogleApiClient.getContext(), accounts[0], scopes);
//            } catch (IOException e) {
//                Log.e(TAG, "IOException: " + e.getMessage());
//            } catch (UserRecoverableAuthException e) {
//                Log.e(TAG, "UserRecoverableAuthException: " + e.getMessage());
//                mFragment.startActivityForResult(e.getIntent(), RC_UserRecoverableAuth);
//
//            } catch (GoogleAuthException e) {
//                Log.e(TAG, "GoogleAuthException: " + e.getMessage());
//            }
//            return token;
//        }
//
//        @Override
//        protected void onPostExecute(String token) {
//            super.onPostExecute(token);
//            Logger.getInstance().d(TAG, "onActivityResult: getToken " + token);
//            if (token != null) {
//                mCallback.onGoogleLoginSuccess(token);
//            } else {
//                mCallback.onGoogleLoginFailure();
//            }
//        }
//    }
}

