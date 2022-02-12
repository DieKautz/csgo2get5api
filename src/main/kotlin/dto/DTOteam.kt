package dto

data class DTOteam(
    val id: Int = all_teams.size+1,
    val user_id: Int = 1,
    val name: String,
    val flag: String = "DE",
    val logo: String = "",
    val tag: String = "",
    val public_team: Int = 0
) {
    init {
        all_teams.add(this)
    }
    companion object {
        val all_teams = mutableListOf<DTOteam>()
    }
}
