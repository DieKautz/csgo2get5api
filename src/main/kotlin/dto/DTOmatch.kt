package dto

data class DTOmatch(
    val id: Int,
    val user_id: Int = 1,
    val sever_id: Int = 1,
    val team1_id: Int,
    val team2_id: Int,
    val winner: Int,
    val team1_score: Int,
    val team2_score: Int,
    val team1_series_score: Int,
    val team2_series_score: Int,
    val team1_string: String,
    val team2_string: String,
    val cancelled: Int = 0,
    val forfeit: Int = 0,
    val start_time: String,
    val end_time: String,
    val max_maps: Int = 1,
    val title: String = "Map {MAPNUMBER} of {MAXMAPS}",
    val skip_veto: Int = 0,
    val api_key: String = "",
    val veto_mappol: String,
    val veto_first: String = "team1",
    val side_type: String = "standard",
    val plugin_version: String = "unknown",
    val private_match: Int = 0,
    val enforce_teams: Int = 1,
    val min_player_ready: Int = 1,
    val season_id: Int = 1,
    val ispug: Int = 0, //this should be is_pug but somehow jackson does not like this name?
    val players_per_team: Int,
    val min_spectators_to_ready: Int = 1,
) {
    init {
        all_match.add(this)
    }
    companion object {
        val all_match = mutableListOf<DTOmatch>()
    }
}
