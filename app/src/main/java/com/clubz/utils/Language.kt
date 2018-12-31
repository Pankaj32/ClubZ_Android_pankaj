package com.clubz.utils

import android.content.Context
import android.content.res.Configuration

import java.util.Locale
import android.os.Build
import com.clubz.R.string.language



/**
 * Created by mindiii on 20/10/16.
 */
public object Language {

    fun SetLanguage(_context: Context, code: String) {


       val locale = Locale(code)
        Locale.setDefault(locale)
        val resources = _context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}
