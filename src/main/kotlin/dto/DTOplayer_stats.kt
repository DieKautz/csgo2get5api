package dto

data class DTOplayer_stats(
    val id: Int = all_player_stats.size+1,
    val match_id: Int,
    val map_id: Int,
    val team_id: Int,
    val steam_id: String,
    val name: String,
    val kills: Int,
    val headshot_kills: Int,
    val deaths: Int,
    val assists: Int,
    val flashbang_assists: Int,
    val roundsplayed: Int,
    val teamkills: Int,
    val knife_kills: Int,
    val suicides: Int,
    val damage: Int,
    val util_damage: Int,
    val enemies_flashed: Int,
    val friendlies_flashed: Int,
    val bomb_plants: Int,
    val bomb_defuses: Int,
    val v1: Int,
    val v2: Int,
    val v3: Int,
    val v4: Int,
    val v5: Int,
    val k1: Int,
    val k2: Int,
    val k3: Int,
    val k4: Int,
    val k5: Int,
    val	firstdeath_ct: Int,
    val	firstdeath_t: Int,
    val	firstkill_ct: Int,
    val	firstkill_t: Int,
    val	kast: Int,
    val contribution_score: Int,
    val winner: Int = 0,
    val mvp: Int,
    val team_name: String = ""
) {
    init {
        all_player_stats.add(this)
    }
    companion object {
        val all_player_stats = mutableListOf<DTOplayer_stats>()
    }
}
