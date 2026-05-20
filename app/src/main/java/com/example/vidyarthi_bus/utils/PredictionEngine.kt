package com.example.vidyarthi_bus.utils

import com.example.vidyarthi_bus.domain.model.CrowdLevel
import com.example.vidyarthi_bus.domain.model.Report

object PredictionEngine {
    
    /**
     * AI-Style Weighted Prediction with Strict 15-Minute Expiry:
     * - Reports older than 15 minutes are discarded for live logic.
     * - More recent reports have significantly higher weight.
     */
    fun predictCrowdLevel(reports: List<Report>): CrowdLevel {
        if (reports.isEmpty()) return CrowdLevel.EMPTY
        
        val currentTime = System.currentTimeMillis()
        val EXPIRY_WINDOW = 900000 // 15 minutes in ms
        
        val recentReports = reports.filter { it.timestamp > currentTime - EXPIRY_WINDOW }
        
        if (recentReports.isEmpty()) return CrowdLevel.EMPTY
        
        var totalWeight = 0f
        var weightedScore = 0f
        
        recentReports.forEach { report ->
            // Linear decay weight based on 15-min window
            val ageFactor = 1f - ((currentTime - report.timestamp).toFloat() / EXPIRY_WINDOW.toFloat())
            val score = when(report.crowdLevel) {
                CrowdLevel.FULL -> 3f
                CrowdLevel.HALF_FULL -> 2f
                CrowdLevel.EMPTY -> 1f
            }
            weightedScore += score * ageFactor
            totalWeight += ageFactor
        }
        
        val finalScore = weightedScore / totalWeight
        
        return when {
            finalScore > 2.4f -> CrowdLevel.FULL
            finalScore > 1.4f -> CrowdLevel.HALF_FULL
            else -> CrowdLevel.EMPTY
        }
    }
    
    fun getConfidenceScore(reports: List<Report>): Int {
        if (reports.isEmpty()) return 0
        val currentTime = System.currentTimeMillis()
        // Confidence depends on how many reports we received in the last 15 mins
        val recentCount = reports.count { it.timestamp > currentTime - 900000 }
        
        val baseConfidence = (recentCount * 25).coerceAtMost(95)
        return if (recentCount >= 4) 98 else baseConfidence
    }
}