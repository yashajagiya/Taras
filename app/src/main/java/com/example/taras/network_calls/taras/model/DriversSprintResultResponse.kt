import kotlinx.serialization.Serializable

@Serializable
data class SRResults(
    val position: String,
    val driverNumber: String,
    val driverName: String,
    val team: String,
    val laps: String,
    val timeOrRetired: String,
    val points: String
)

@Serializable
data class SprintResultResponse(
    val country: String,
    val session: String,
    val raceName: String,
    val date: String,
    val circuitName: String,
    val circuitId: String,
    val results: List<SRResults>
)