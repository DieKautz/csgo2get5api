package dto

data class DTOmapList(
    val id: Int = all_map_list.size+1,
    val user_id: Int = 1,
    val map_name: String,
    val map_display_name: String = map_name,
    val enabled: Int = 1,
    val inserted_at: String = "2022-02-10 01:33:07"
) {
    init {
        all_map_list.add(this)
    }
    companion object {
        val all_map_list = mutableListOf<DTOmapList>()
    }
}
