package hr.ferit.moodtracker.data

data class AnalyticsData(
    val averageMood: Double = 0.0,
    val bestActivity: String? = null,
    val totalEntries: Int = 0,
    val personalizedTip: String = "Nastavi bilježiti svoje raspoloženje za personalizirane savjete!"
)
