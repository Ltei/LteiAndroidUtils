package com.ltei.laubilling

import android.app.Activity
import android.content.Intent
import org.solovyev.android.checkout.*

class RemoveAdsBillingManager(activity: Activity, billing: Billing, private val skuString: String) {

    private val checkout = Checkout.forActivity(activity, billing)
    private val inventory: Inventory
    private var onBillingSuccess: (() -> Unit)? = null

    init {
        checkout.start()
        checkout.createPurchaseFlow(PurchaseListener())
        inventory = checkout.makeInventory()
        inventory.load(Inventory.Request.create().loadAllPurchases()
                .loadSkus(ProductTypes.IN_APP, skuString), InventoryCallback())
    }

    fun onDestroy() {
        checkout.destroyPurchaseFlow()
        checkout.stop()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkout.onActivityResult(requestCode, resultCode, data)
    }

    // Callables

    fun processBilling(onSuccess: () -> Unit) {
        onBillingSuccess = onSuccess
        checkout.whenReady(object : Checkout.EmptyListener() {
            override fun onReady(requests: BillingRequests) {
                requests.purchase(ProductTypes.IN_APP, skuString, null, checkout.purchaseFlow)
            }
        })
    }

    // Classes

    private inner class PurchaseListener : RequestListener<Purchase> {
        override fun onSuccess(purchase: Purchase) {
            onBillingSuccess?.invoke()
        }

        override fun onError(response: Int, e: Exception) {
            if (response == ResponseCodes.ITEM_ALREADY_OWNED) {
                onBillingSuccess?.invoke()
            }
        }
    }

    private class InventoryCallback : Inventory.Callback {
        override fun onLoaded(products: Inventory.Products) {
            // to do
        }
    }

}