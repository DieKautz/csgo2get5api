import dto.DTOmapList

object Datastore {

    var mapList = mutableListOf<DTOmapList>(
        DTOmapList(map_name = "de_dust2", map_display_name = "Dust II"),
        DTOmapList(map_name = "de_inferno", map_display_name = "Inferno"),
        DTOmapList(map_name = "de_mirage", map_display_name = "Mirage"),
        DTOmapList(map_name = "de_nuke", map_display_name = "Nuke"),
        DTOmapList(map_name = "de_overpass", map_display_name = "Overpass"),
        DTOmapList(map_name = "de_vertigo", map_display_name = "Vertigo"),
        DTOmapList(map_name = "de_ancient", map_display_name = "Ancient"),

        DTOmapList(map_name = "de_train", map_display_name = "Train"),
        DTOmapList(map_name = "de_cache", map_display_name = "Cache"),
        DTOmapList(map_name = "cs_agency", map_display_name = "Agency"),
        DTOmapList(map_name = "cs_office", map_display_name = "Office"),
    )
    fun idOfMap(name: String): Int {
        var index = mapList.indexOfFirst { it.map_name == name }
        if (index == -1) {
            index = mapList.size
            mapList.add(DTOmapList(map_name = name))
            println("NEW MAP $name has id ${mapList.size}")
        }
        return index +1
    }

    var players = mutableListOf(Player("76561198112923396", "Kauz"))
    fun idOfPlayer(player: Player): Int {
        var index = players.indexOfFirst { it.steam_id == player.steam_id }
        if (index == -1) {
            players.add(player)
            index = players.indexOfFirst { it.steam_id == player.steam_id }
            println("NEW PLAYER ${player.name} has id ${index+1}")
        }
        return index +1
    }
}