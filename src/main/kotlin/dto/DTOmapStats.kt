package dto

data class DTOmapStats(
    val id: Int,
    val match_id: Int,
    val winner: Int, //winner team id
    val map_number: Int,
    val map_name: String,
    val team1_score: Int,
    val team2_score: Int,
    val start_time: String,
    val end_time: String,
    val demoFile: String = ""
) {
    init {
        all_map_stats.add(this)
    }
    companion object {
        val all_map_stats = mutableListOf<DTOmapStats>()
    }
}
