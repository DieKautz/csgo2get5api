package dto

data class DTOteam_auth_names(
    val id: Int = all_team_auth_names.size+1,
    val team_id: Int,
    val auth: String,
    val name: String = "",
    val captain: Int = 0
) {
    init {
        all_team_auth_names.add(this)
    }
    companion object {
        val all_team_auth_names = mutableListOf<DTOteam_auth_names>()
    }
}
