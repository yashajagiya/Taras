package com.example.taras.network_calls

object ApiConstants {
    const val BASE_URL_TARAS_GITHUB = "https://yashajagiya.github.io/tarasF1Data/"
    const val PRIVACY_POLICY_URL = "https://yashajagiya.github.io/tarasF1Data/privacy-policy.html"
    const val FALLBACK_DRIVER_IMAGE_URL =
        "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/"

    const val BASE_URL_TARAS_V2 = "https://yashajagiya.github.io/tarasF1Data/v2/"

    // v2 API Endpoints (relative to BASE_URL_TARAS_GITHUB)
    const val ENDPOINT_V2_OVERVIEW = "v2/overview.json"
    const val ENDPOINT_V2_CALENDAR = "v2/calendar.json"
    const val ENDPOINT_V2_STANDINGS = "v2/standings.json"
    const val ENDPOINT_V2_DRIVERS = "v2/drivers.json"
    const val ENDPOINT_V2_TEAMS = "v2/teams.json"
    const val ENDPOINT_V2_RESULTS_LATEST = "v2/results/latest.json"
    const val ENDPOINT_V2_RESULTS_ROUND = "v2/results/round_{round}.json"


}
