package google.hates.pixel_unlocker

object DeviceProps {

    class Features(
        val displayName: String,
        val featureFlags: List<String>,
    ) {
        constructor(displayName: String, vararg featureFlags: String) : this(
            displayName, featureFlags.toList()
        )
    }

    val allFeatures = listOf(
        Features(
            "Pixel 2016", // Pixel XL
            "com.google.android.apps.photos.NEXUS_PRELOAD",
            "com.google.android.apps.photos.nexus_preload",
            "com.google.android.feature.PIXEL_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_PRELOAD",
            "com.google.android.apps.photos.PIXEL_2016_PRELOAD",
        ),

        Features(
            "Pixel 2017", // Pixel 2
            "com.google.android.feature.PIXEL_2017_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2017_PRELOAD"
        ),

        Features(
            "Pixel 2018", // Pixel 3 XL
            "com.google.android.feature.PIXEL_2018_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2018_PRELOAD"
        ),

        Features(
            "Pixel 2019", // Pixel 4 XL
            "com.google.android.feature.PIXEL_2019_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2019_PRELOAD",
        ),

        Features(
            "Pixel 2020", // Pixel 5
            "com.google.android.feature.PIXEL_2020_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2020_PRELOAD",
        ),

        Features(
            "Pixel 2021", // Pixel 6 Pro
            "com.google.android.feature.PIXEL_2021_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2021_PRELOAD",
        ),

        Features(
            "Pixel 2022", // Pixel 7 Pro
            "com.google.android.feature.PIXEL_2022_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2022_PRELOAD"
        ),

        Features(
            "Pixel 2023", // Pixel 8 Pro
            "com.google.android.feature.PIXEL_2023_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2023_PRELOAD"
        ),

        Features(
            "Pixel 2024", // Pixel 9 Pro
            "com.google.android.feature.PIXEL_2024_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2024_PRELOAD"
        )
    )

    private fun getFeaturesUpTo(featureLevel: String): List<Features> {
        val allFeatureDisplayNames = allFeatures.map { it.displayName }
        val levelIndex = allFeatureDisplayNames.indexOf(featureLevel)
        return if (levelIndex == -1) listOf()
        else {
            allFeatures.withIndex().filter { it.index <= levelIndex }.map { it.value }
        }
    }

    data class AndroidVersion(
        val label: String,
        val release: String,
        val sdk: Int,
    ) {
        fun getAsMap() = hashMapOf(
            Pair("RELEASE", release),
            Pair("SDK_INT", sdk),
            Pair("SDK", sdk.toString()),
        )
    }

    val allAndroidVersions = listOf(
        AndroidVersion("Nougat 7.1.2", "7.1.2", 25),
        AndroidVersion("Oreo 8.1.0", "8.1.0", 27),
        AndroidVersion("Pie 9.0", "9", 28),
        AndroidVersion("Q 10.0", "10", 29),
        AndroidVersion("R 11.0", "11", 30),
        AndroidVersion("S 12.0", "12", 31),
        AndroidVersion("Tiramisu 13.0", "13", 33),
        AndroidVersion("Upside Down Cake 14.0", "14", 34),
        AndroidVersion("Vanilla Ice Cream 15.0", "15", 35)
    )

    fun getAndroidVersionFromLabel(label: String) = allAndroidVersions.find { it.label == label }

    data class DeviceEntries(
        val deviceName: String,
        val props: HashMap<String, String>,
        val featureLevelName: String,
        val androidVersion: AndroidVersion?,
    )

    val allDevices = listOf(

        DeviceEntries("None", hashMapOf(), "None", null),

        DeviceEntries(
            "Pixel XL",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "marlin"),
                Pair("PRODUCT", "marlin"),
                Pair("BOARD", "marlin"),
                Pair("MODEL", "Pixel XL"),
                Pair(
                    "FINGERPRINT",
                    "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys"
                ),
            ),
            "Pixel 2016",
            getAndroidVersionFromLabel("Q 10.0"),
        ),

        DeviceEntries(
            "Pixel 2",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "walleye"),
                Pair("PRODUCT", "walleye"),
                Pair("BOARD", "walleye"),
                Pair("MODEL", "Pixel 2"),
                Pair(
                    "FINGERPRINT",
                    "google/walleye/walleye:8.1.0/OPM1.171019.021/4565141:user/release-keys"
                ),
            ),
            "Pixel 2017",
            getAndroidVersionFromLabel("Oreo 8.1.0"),
        ),

        DeviceEntries(
            "Pixel 3 XL",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "crosshatch"),
                Pair("PRODUCT", "crosshatch"),
                Pair("BOARD", "crosshatch"),
                Pair("MODEL", "Pixel 3 XL"),
                Pair(
                    "FINGERPRINT",
                    "google/crosshatch/crosshatch:11/RQ3A.211001.001/7641976:user/release-keys"
                ),
            ),
            "Pixel 2018",
            getAndroidVersionFromLabel("R 11.0"),
        ),

        DeviceEntries(
            "Pixel 4 XL",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "coral"),
                Pair("PRODUCT", "coral"),
                Pair("BOARD", "coral"),
                Pair("MODEL", "Pixel 4 XL"),
                Pair(
                    "FINGERPRINT", "google/coral/coral:12/SP1A.211105.002/7743617:user/release-keys"
                ),
            ),
            "Pixel 2019",
            getAndroidVersionFromLabel("S 12.0"),
        ),

        DeviceEntries(
            "Pixel 5",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "redfin"),
                Pair("PRODUCT", "redfin"),
                Pair("BOARD", "redfin"),
                Pair("MODEL", "Pixel 5"),
                Pair(
                    "FINGERPRINT",
                    "google/redfin/redfin:12/SP1A.211105.003/7757856:user/release-keys"
                ),
            ),
            "Pixel 2020",
            getAndroidVersionFromLabel("S 12.0"),
        ),

        DeviceEntries(
            "Pixel 6 Pro",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "raven"),
                Pair("PRODUCT", "raven"),
                Pair("BOARD", "raven"),
                Pair("MODEL", "Pixel 6 Pro"),
                Pair(
                    "FINGERPRINT", "google/raven/raven:12/SD1A.210817.036/7805805:user/release-keys"
                ),
            ),
            "Pixel 2021",
            getAndroidVersionFromLabel("S 12.0"),
        ),

        DeviceEntries(
            "Pixel 7 Pro",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "cheetah"),
                Pair("PRODUCT", "cheetah"),
                Pair("BOARD", "cheetah"),
                Pair("MODEL", "Pixel 7 Pro"),
                Pair(
                    "FINGERPRINT",
                    "google/cheetah/cheetah:13/TD1A.220804.031/8749374:user/release-keys"
                ),
            ),
            "Pixel 2022",
            getAndroidVersionFromLabel("Tiramisu 13.0"),
        ),

        DeviceEntries(
            "Pixel 8 Pro",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "husky"),
                Pair("PRODUCT", "husky"),
                Pair("BOARD", "husky"),
                Pair("MODEL", "Pixel 8 Pro"),
                Pair(
                    "FINGERPRINT",
                    "google/husky/husky:14/AP2A.240805.005/12025142:user/release-keys"
                ),
            ),
            "Pixel 2023",
            getAndroidVersionFromLabel("Upside Down Cake 14.0"),
        ),

        DeviceEntries(
            "Pixel 9 Pro",
            hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "caiman"),
                Pair("PRODUCT", "caiman"),
                Pair("BOARD", "caiman"),
                Pair("MODEL", "Pixel 9 Pro"),
                Pair(
                    "FINGERPRINT",
                    "google/caiman/caiman:14/AD1A.240530.047.U1/12150698:user/release-keys"
                ),
            ),
            "Pixel 2024",
            getAndroidVersionFromLabel("Vanilla Ice Cream 15.0"),
        ),

        )

    fun getDeviceProps(deviceName: String?) = allDevices.find { it.deviceName == deviceName }

    fun getFeaturesUpToFromDeviceName(deviceName: String?): Set<String> {
        return getDeviceProps(deviceName)?.let { it ->
            getFeaturesUpTo(it.featureLevelName).map { it.displayName }.toSet()
        } ?: setOf()
    }

    const val defaultDeviceName = "Pixel 9 Pro"
    val defaultFeatures = getFeaturesUpTo("Pixel 2024")
}