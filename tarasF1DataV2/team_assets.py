"""Canonical Formula 1 team colors and 2026 car/logo image URLs for API v2."""

TEAM_ASSET_DATA = {
    "mercedes": ("0xFF27F4D2", "mercedes"),
    "ferrari": ("0xFFE8002D", "ferrari"),
    "mclaren": ("0xFFFF8000", "mclaren"),
    "red-bull-racing": ("0xFF3671C6", "redbullracing"),
    "alpine": ("0xFF00A1E8", "alpine"),
    "racing-bulls": ("0xFF6692FF", "racingbulls"),
    "haas": ("0xFFDEE1E2", "haasf1team"),
    "williams": ("0xFF1868DB", "williams"),
    "audi": ("0xFFFF2D00", "audi"),
    "aston-martin": ("0xFF229971", "astonmartin"),
    "cadillac": ("0xFFAAAAAD", "cadillac"),
}


def get_team_assets(team_id):
    color, image_slug = TEAM_ASSET_DATA[team_id]
    return {
        "team_color": color,
        "team_logo": (
            "https://media.formula1.com/image/upload/"
            "c_fit,w_1024/e_sharpen:100/q_auto:best/"
            "v1740000001/common/f1/2026/"
            f"{image_slug}/"
            f"{image_slug}logowhite.png"
        ),
        "team_car": (
            "https://media.formula1.com/image/upload/"
            "c_fit,w_2400,h_1200/q_auto:best/"
            "d_common:f1:2026:fallback:car:"
            "2026fallbackcarright.webp"
            f"/v1740000001/common/f1/2026/{image_slug}/"
            f"2026{image_slug}carright.png"
        ),
    }
