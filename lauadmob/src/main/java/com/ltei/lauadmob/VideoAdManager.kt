package com.ltei.lauadmob

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener

class VideoAdManager(activity: Activity, val adUnitId: String) {


    open class Event {
        open fun onCompleted(reward: RewardItem?) {}
        open fun onFailedToLoad() {}
        open fun onCanceled() {}
    }


    private val adInstance: RewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)
    private val events: com.ltei.ljuutils.collections.SimpleQueue<Event> = com.ltei.ljuutils.collections.SimpleQueue()

    init {
        adInstance.rewardedVideoAdListener = InnerAdListener()
        adInstance.loadAd(adUnitId, AdRequest.Builder().build())
    }


    fun post(event: Event) {
        synchronized(this@VideoAdManager) {
            events.push(event)
            if (adInstance.isLoaded) {
                adInstance.show()
            }
        }
    }


    private inner class InnerAdListener : RewardedVideoAdListener {

        private var rewarded = false

        override fun onRewardedVideoAdLoaded() {
            synchronized(this@VideoAdManager) {
                if (events.isNotEmpty()) {
                    adInstance.show()
                }
            }
        }

        override fun onRewardedVideoStarted() {
            synchronized(this@VideoAdManager) {
                rewarded = false
            }
        }

        override fun onRewarded(reward: RewardItem?) {
            synchronized(this@VideoAdManager) {
                events.tryPop()?.onCompleted(reward)
                rewarded = true
            }
        }

        override fun onRewardedVideoAdClosed() {
            synchronized(this@VideoAdManager) {
                if (!rewarded) {
                    events.tryPop()?.onCanceled()
                }
                adInstance.loadAd(adUnitId, AdRequest.Builder().build())
            }
        }

        override fun onRewardedVideoAdFailedToLoad(p0: Int) {
            synchronized(this@VideoAdManager) {
                if (events.isNotEmpty()) {
                    events.tryPop()?.onFailedToLoad()
                }
            }
        }

        override fun onRewardedVideoAdOpened() {}
        override fun onRewardedVideoCompleted() {}
        override fun onRewardedVideoAdLeftApplication() {}

    }

}