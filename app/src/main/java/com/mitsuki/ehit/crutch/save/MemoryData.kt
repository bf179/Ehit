package com.mitsuki.ehit.crutch.save

import com.mitsuki.ehit.crutch.network.Site

class MemoryData(private val shareData: ShareData) {

    var domain: Int = shareData.spDomain
        set(value) {
            if (value != field) {
                field = value
                shareData.spDomain = value
                Site.refreshDomain(field)
            }
        }

    var screenOrientation: Int = shareData.spScreenOrientation
        set(value) {
            if (value != field) {
                field = value
                shareData.spScreenOrientation = value
            }
        }

    var readOrientation: Int = shareData.spReadOrientation
        set(value) {
            if (value != field) {
                field = value
                shareData.spReadOrientation = value
            }
        }

    var imageZoom: Int = shareData.spImageZoom
        set(value) {
            if (value != field) {
                field = value
                shareData.spImageZoom = value
            }
        }

    var keepBright: Boolean = shareData.spKeepBright
        set(value) {
            if (value != field) {
                field = value
                shareData.spKeepBright = value
            }
        }

    var showTime: Boolean = shareData.spShowTime
        set(value) {
            if (value != field) {
                field = value
                shareData.spShowTime = value
            }
        }

    var showBattery: Boolean = shareData.spShowBattery
        set(value) {
            if (value != field) {
                field = value
                shareData.spShowBattery = value
            }
        }

    var showProgress: Boolean = shareData.spShowProgress
        set(value) {
            if (value != field) {
                field = value
                shareData.spShowProgress = value
            }
        }

    var showPagePadding: Boolean = shareData.spShowPagePadding
        set(value) {
            if (value != field) {
                field = value
                shareData.spShowPagePadding = value
            }
        }

    var volumeButtonTurnPages: Boolean = shareData.spVolumeButtonTurnPages
        set(value) {
            if (value != field) {
                field = value
                shareData.spVolumeButtonTurnPages = value
            }
        }

    var fullScreen: Boolean = shareData.spFullScreen
        set(value) {
            if (value != field) {
                field = value
                shareData.spFullScreen = value
            }
        }

    var disableScreenshots: Boolean = shareData.spDisableScreenshots
        set(value) {
            if (value != field) {
                field = value
                shareData.spDisableScreenshots = value
            }
        }

    init {
        Site.refreshDomain(domain)
    }
}