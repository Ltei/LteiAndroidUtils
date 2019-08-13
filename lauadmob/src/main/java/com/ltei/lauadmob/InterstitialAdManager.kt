package com.ltei.lauadmob

import android.Manifest
import android.app.Activity
import androidx.annotation.RequiresPermission
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class InterstitialAdManager @RequiresPermission(Manifest.permission.INTERNET) constructor(activity: Activity, adUnitId: String) {

    open class Event {
        fun onAdCompleted() {}
        fun onAdFailedToLoad() {}
    }

    private val adInstance: InterstitialAd = InterstitialAd(activity)
    private val events: com.ltei.ljuutils.collections.SimpleQueue<Event> = com.ltei.ljuutils.collections.SimpleQueue()

    init {
        adInstance.adListener = InnerAdListener()
        adInstance.adUnitId = adUnitId
        adInstance.loadAd(AdRequest.Builder().build())
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    fun post(event: Event) {
        synchronized(this@InterstitialAdManager) {
            events.push(event)
            if (adInstance.isLoaded) {
                adInstance.show()
            } else {
                adInstance.loadAd(AdRequest.Builder().build())
            }
        }
    }


    private inner class InnerAdListener : AdListener() {

        override fun onAdLoaded() {
            synchronized(this@InterstitialAdManager) {
                if (events.isNotEmpty()) {
                    adInstance.show()
                }
            }
        }

        override fun onAdClosed() {
            synchronized(this@InterstitialAdManager) {
                if (events.isNotEmpty()) {
                    events.tryPop()?.onAdCompleted()
                }
            }
        }

        override fun onAdFailedToLoad(p0: Int) {
            synchronized(this@InterstitialAdManager) {
                if (!adInstance.isLoading) {
                    events.tryPop()?.onAdFailedToLoad()
                }
            }
        }

    }

}