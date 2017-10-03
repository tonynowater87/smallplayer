package com.tonynowater.smallplayer.util.google;

/**
 * Created by tonynowater on 2017/10/3.
 */
public class TokenResponse {

    /**
     * access_token : ya29.GlvZBMyW9kx0kxiqEOPZAXFaP8FRuMPxvmDmmaMOfg7p-N0c5S7hYVgTDiEkQgPMBgL4UyfG31YdRdq1TcrKWf2Sq-tOHCJpe128cDZ05PvqrXEoBcf3JCrYonfF
     * token_type : Bearer
     * expires_in : 3600
     * refresh_token : 1/M267voKEWVurPiD5MZnjQWwW9wOi1uDcBLGS67SN1_U
     * id_token : eyJhbGciOiJSUzI1NiIsImtpZCI6IjA2NWY1YTBhZWFkOTgzMmNjMzZjZTk4NmZlOGE5MjllYjZlYzUxYjcifQ.eyJhenAiOiI2MTE3Mjg0MTMzOTMtODg3NzFwYnNtM2cyaG9jaGphNHJoMDdnazVrNDB1djIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI2MTE3Mjg0MTMzOTMtODg3NzFwYnNtM2cyaG9jaGphNHJoMDdnazVrNDB1djIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTI4NDk2MTA4ODQ0OTQyNTM2MjEiLCJlbWFpbCI6InRvbnkxMDUzMkBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IkJyOGtLeWF0cUxrbjNqTkFQaDgzSGciLCJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJpYXQiOjE1MDY5OTcwMzYsImV4cCI6MTUwNzAwMDYzNiwibmFtZSI6ImxpYW8gdG9ueSIsInBpY3R1cmUiOiJodHRwczovL2xoNS5nb29nbGV1c2VyY29udGVudC5jb20vLWxCZDhmcUprRFY4L0FBQUFBQUFBQUFJL0FBQUFBQUFBQVRzLzZ4NVJKTVc2WHVzL3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJsaWFvIiwiZmFtaWx5X25hbWUiOiJ0b255IiwibG9jYWxlIjoiemgtVFcifQ.ewXpgIkK7u38dQoResJNrz8tINoaCu8N85zxyKRDCufZPsDibZN-5zhIOfqj8w81kRJMM70WXRfqqHlIj-HpvCzqZHWmvbZxZizygszytFauckDoWXFf2Ct-ZOU9aSSwlmc8jAQuJZg0V4iMQbqD4EcAlK0jeGwAkeFGeex8Yw4GTKMaf6--s5oTpV6XuORskT3xaBU3pxSZFlvVGLPKtsDwK8Ymc6zcnWzSamHkgap3ePaarvsNBniwMaKt4ZhnE-a_MuG5xIafLPYVKo4Re6gVoBg92TIZajNAeWlMcq8ZT2pI0rLU8fpYO82iwrXXkONwSHlRGsRQ3e9w_taY1g
     */

    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String id_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
}
