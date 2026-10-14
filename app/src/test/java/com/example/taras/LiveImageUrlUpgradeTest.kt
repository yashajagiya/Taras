package com.example.taras

import com.example.taras.view.subview.upgradeF1LiveImageUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LiveImageUrlUpgradeTest {

    @Test
    fun testNullAndBlank() {
        assertNull(upgradeF1LiveImageUrl(null))
        assertNull(upgradeF1LiveImageUrl(""))
        assertNull(upgradeF1LiveImageUrl("   "))
    }

    @Test
    fun testRelativeUrlResolution() {
        val relative = "/assets/singapore_lights_out.jpg"
        val baseUrl = "https://bra-slide-formatting.trycloudflare.com"
        val expected = "https://bra-slide-formatting.trycloudflare.com/assets/singapore_lights_out.jpg"
        assertEquals(expected, upgradeF1LiveImageUrl(relative, baseUrl))
    }

    @Test
    fun testCloudinaryFormula1MediaUpgrade() {
        val lowResF1 = "https://media.formula1.com/image/upload/c_limit,w_960/q_auto/v1728648000/content/singapore/verstappen_pole.jpg"
        val upgraded = upgradeF1LiveImageUrl(lowResF1)
        val expected = "https://media.formula1.com/image/upload/c_limit,w_1920,q_auto:best,f_auto/v1728648000/content/singapore/verstappen_pole.jpg"
        assertEquals(expected, upgraded)
    }

    @Test
    fun testMonterosaThumbnailUpgrade() {
        val monterosaThumb = "https://cdn.monterosa.cloud/assets/thumbnail/verstappen_action.jpg?width=400&height=225"
        val upgraded = upgradeF1LiveImageUrl(monterosaThumb)
        val expected = "https://cdn.monterosa.cloud/assets/original/verstappen_action.jpg?width=1920"
        assertEquals(expected, upgraded)
    }

    @Test
    fun testGenericCdnQueryParamUpgrade() {
        val cdnThumb = "https://images.formula1.com/photo.jpg?w=480&h=270&q=60"
        val upgraded = upgradeF1LiveImageUrl(cdnThumb)
        val expected = "https://images.formula1.com/photo.jpg?w=1920&q=100"
        assertEquals(expected, upgraded)
    }

    @Test
    fun testWordPressDimensionSuffixRemoval() {
        val thumb = "https://example.com/editorial/hamilton_ferrari-768x432.webp"
        val upgraded = upgradeF1LiveImageUrl(thumb)
        val expected = "https://example.com/editorial/hamilton_ferrari.webp"
        assertEquals(expected, upgraded)
    }
}
