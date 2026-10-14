package com.example.taras.network_calls

object ApiConstants {
    const val BASE_URL_TARAS_GITHUB = "https://yashajagiya.github.io/TarasAPIstatic/"
    const val PRIVACY_POLICY_URL = "https://yashajagiya.github.io/TarasF1/privacy-policy.html"
    const val TERMS_OF_SERVICE_URL = "https://yashajagiya.github.io/TarasF1/terms-of-service.html"
    const val ABOUT_URL = "https://yashajagiya.github.io/TarasF1/about.html"
    const val GRIEVANCE_EMAIL = "ajagiyayash@gmail.com"
    const val GITHUB_REPO_URL = "https://github.com/yashajagiya/Taras"
    const val TARAS_API_REPO_URL = "https://github.com/yashajagiya/TarasAPIstatic"
    const val FALLBACK_DRIVER_IMAGE_URL =
        "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/"

    const val BASE_URL_TARAS_V2 = "https://yashajagiya.github.io/TarasAPIstatic/v2/"
    const val BASE_URL_TARAS_RAW = "https://raw.githubusercontent.com/yashajagiya/TarasAPIstatic/main/"

    // v2 API Endpoints (relative to BASE_URL_TARAS_GITHUB or BASE_URL_TARAS_RAW)
    const val ENDPOINT_V2_OVERVIEW = "v2/overview.json"
    const val ENDPOINT_V2_CALENDAR = "v2/calendar.json"
    const val ENDPOINT_V2_STANDINGS = "v2/standings.json"
    const val ENDPOINT_V2_DRIVERS = "v2/drivers.json"
    const val ENDPOINT_V2_TEAMS = "v2/teams.json"
    const val ENDPOINT_V2_RESULTS_LATEST = "v2/results/latest.json"
    const val ENDPOINT_V2_RESULTS_ROUND = "v2/results/round_{round}.json"
    const val ENDPOINT_V2_LIVE = "v2/live.json"
}
