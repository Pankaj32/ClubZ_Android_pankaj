package com.clubz.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources

import java.util.Locale

/**
 * Created by mindiii on 20/10/16.
 */
public object Language {

    fun SetLanguage(_context: Context, code: String) {

        val locale = Locale(code)
        Locale.setDefault(locale)
        val resources = _context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)

    }


}
