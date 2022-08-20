package com.fredjunior.fjsinsta;

import android.app.Application;

import com.fredjunior.fjsinsta.Models.Post;
import com.parse.Parse;
import com.parse.ParseObject;



public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("W6mRMuiAFugo8mH4j1AMzZ6Fjd682euZlrJAenuZ")
                .clientKey("w7aO6F88NG02Eo3SSAx6CWU0S1Kc0JjWnaKcZ3HR")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }

}

