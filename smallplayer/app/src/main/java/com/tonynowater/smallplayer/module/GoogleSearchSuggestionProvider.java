package com.tonynowater.smallplayer.module;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by tonynowater on 2017/5/27.
 */

public class GoogleSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    private static final String TAG = GoogleSearchSuggestionProvider.class.getSimpleName();
    public static final String AUTHORITY = "com.tonynowater.smallplayer.module.GoogleSearchSuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public GoogleSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY,MODE);
    }
}
