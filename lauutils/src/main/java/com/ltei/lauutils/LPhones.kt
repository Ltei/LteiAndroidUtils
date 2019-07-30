package com.ltei.lauutils

import android.content.Context
import android.telephony.TelephonyManager


object LPhones {

    fun isNumberValid(phoneNumber: String): Boolean {
        val formattedPhoneNumber = phoneNumber.replace(" ", "").replace("-", "")
        if (formattedPhoneNumber[0] != '+' && !formattedPhoneNumber[0].isDigit())
            return false

        for (c in formattedPhoneNumber.substring(1))
            if (!c.isDigit())
                return false

        return true
    }

    fun countryISO(context: Context): String {
        return (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso
    }

    fun format(@Suppress("UNUSED_PARAMETER") context: Context, phoneNumber: String): String {
        /*LLog.debug(this.javaClass, "Input = $phoneNumber")
        val result = PhoneNumberUtils.formatNumber(phoneNumber.replace(" ", "").replace("-", ""), LPhones.countryISO(context))
        LLog.debug(this.javaClass, "Output = $result")
        return result*/
        return phoneNumber // todo
    }
}