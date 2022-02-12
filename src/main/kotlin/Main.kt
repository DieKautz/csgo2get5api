import dto.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

val dataFormatter = DataFormatter()
val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


fun main(args: Array<String>) {
    val parser = ArgParser("CSGO Demos Manager to Get5 Api")
    val importFolder by parser.argument(ArgType.String, description = "CSGO Demos Manager xlsx exports folder")
    val exportFolder by parser.argument(ArgType.String, description = "Get 5 API import csv folder")
    parser.parse(args)

    val folder = File(importFolder)
    if (!folder.exists()) {
        println("Import location does not exist!")
        exitProcess(1)
    }

    folder.listFiles()?.forEachIndexed { index, file ->

        parseMatch(index+1, file)
    }
//    parseMatch(1, XSSFWorkbook(folder.listFiles()?.first()))

    println("Writing csv..")
    writeCsvFile(DTOmapList.all_map_list, "$exportFolder/all_map_list.csv")
    writeCsvFile(DTOmapStats.all_map_stats, "$exportFolder/all_map_stats.csv")
    writeCsvFile(DTOmatch.all_match, "$exportFolder/all_match.csv")
    writeCsvFile(DTOplayer_stats.all_player_stats, "$exportFolder/all_player_stats.csv")
    writeCsvFile(DTOteam.all_teams, "$exportFolder/all_teams.csv")
    writeCsvFile(DTOteam_auth_names.all_team_auth_names, "$exportFolder/all_team_auth_names.csv")
}

operator fun XSSFWorkbook.get(index: Int): XSSFSheet = this.getSheetAt(index)
operator fun XSSFSheet.get(index: Int): XSSFRow = this.getRow(index)
operator fun XSSFRow.get(index: Int): XSSFCell = this.getCell(index)

fun XSSFWorkbook.getEntry(sheet: Int, row: Int, col: Int): String? = if (sheet > this.numberOfSheets-1) null else this[sheet].getEntry(row, col)
fun XSSFSheet.getEntry(row: Int, col: Int): String? = if (row > this.lastRowNum) null else this[row].getEntry(col)
fun XSSFRow.getEntry(col: Int): String? = if (col > this.lastCellNum) null else dataFormatter.formatCellValue(this[col])

fun parseMatch(matchNr: Int, file: File) {
    val workbook = XSSFWorkbook(file.inputStream())
    println("processing game $matchNr ${file.nameWithoutExtension}")
    val startDate = LocalDateTime.from(dateTimeFormatter.parse(workbook.getEntry(0, 1, 2)))

    val mapName = workbook.getEntry(0, 1, 5)!!.split("/").last()
    val mapId = Datastore.idOfMap(mapName)
    val duration = workbook.getEntry(0, 1, 10)!!.split(",").first().toLong()
    val endDate = startDate.plusSeconds(duration)

    val teamNames = arrayOf(
        workbook.getEntry(0, 1, 12)!!,
        workbook.getEntry(0, 1, 13)!!
    )
    val scores = arrayOf(
        workbook.getEntry(0, 1, 14)!!.toInt(),
        workbook.getEntry(0, 1, 15)!!.toInt()
    )
    DTOteam(name = teamNames[0]!!)
    DTOteam(name = teamNames[1]!!)
    val winnerName = workbook.getEntry(0, 1, 20)

    val mapStats = DTOmapStats(
        matchNr, matchNr,
        (matchNr - 1) * 2 + teamNames.indexOf(winnerName) + 1,
        mapId,
        mapName,
        scores[0],
        scores[1],
        dateTimeFormatter.format(startDate),
        dateTimeFormatter.format(endDate),
        ""
    )

    val firstCtTeamIndex = if (workbook.getEntry(2, 1, 4) == "CT") {
        teamNames.indexOf(workbook.getEntry(2, 1, 3))
    } else {
        1 - teamNames.indexOf(workbook.getEntry(2, 1, 3))
    }

    val roundsCount = workbook[7].count()
    val playersCount = workbook[1].count() - 1

    val firstKillStartingSide = mutableMapOf<String, Int>()
    val firstDeathStartingSide = mutableMapOf<String, Int>()
    val firstKillSwitchSide = mutableMapOf<String, Int>()
    val firstDeathSwitchSide = mutableMapOf<String, Int>()
    for (roundNum in 1 until roundsCount) {
        val holdKillsRow = workbook[4][roundNum]
        val killsRow = workbook[7][roundNum]

        val killerSteamId = holdKillsRow.getEntry(2) ?: killsRow.getEntry(2)
        val victimSteamId = holdKillsRow.getEntry(4) ?: killsRow.getEntry(4)

        if (killerSteamId == null || victimSteamId == null) {
            continue
        }

        if (roundNum <= 15 || roundNum % 6 <= 3) { // is starting side for each team
            firstKillStartingSide[killerSteamId] = (firstKillStartingSide[killerSteamId]?: 0) + 1
            firstDeathStartingSide[victimSteamId] = (firstDeathStartingSide[victimSteamId]?: 0) + 1
        } else {
            firstKillSwitchSide[killerSteamId] = (firstKillSwitchSide[killerSteamId]?: 0) + 1
            firstDeathSwitchSide[victimSteamId] = (firstDeathSwitchSide[victimSteamId]?: 0) + 1
        }
    }


    for (rowIndex in 1..playersCount) {
        val row = workbook[1][rowIndex]
        val teamName = row.getEntry(3)
        var teamIndex = teamNames.indexOf(teamName)
        if (teamIndex == -1) {
            teamIndex = when(teamName) {
                "Team 1" -> 0
                "Team 2" -> 1
                else -> throw IllegalStateException("wrong wrong wrong!!")
            }
        }
        val teamId = (matchNr - 1) * 2 + teamIndex + 1
        val startingCt = teamIndex == firstCtTeamIndex
        val player = Player(
            row.getEntry(1)!!,
            row.getEntry(0)!!
        )
        val playerId = Datastore.idOfPlayer(player)
        //println("${player.name} playing in $teamName ($teamIndex)")
        DTOteam_auth_names(
            team_id = teamId,
            auth = player.steam_id
        )
        DTOplayer_stats(
            match_id = matchNr,
            map_id = mapId,
            team_id = teamId,
            steam_id = player.steam_id,
            name = player.name,
            kills = row.getEntry(4)!!.toInt(),
            assists = row.getEntry(5)!!.toInt(),
            deaths = row.getEntry(6)!!.toInt(),
            headshot_kills = row.getEntry(8)!!.toInt(),
            teamkills = row.getEntry(10)!!.toInt(),
            bomb_plants = row.getEntry(12)!!.toInt(),
            bomb_defuses = row.getEntry(13)!!.toInt(),
            mvp = row.getEntry(14)!!.toInt(),
            contribution_score = row.getEntry(15)!!.toInt(),
            damage = row.getEntry(22)!!.split(",").first().toInt() * roundsCount,
            k5 = row.getEntry(25)!!.toInt(),
            k4 = row.getEntry(26)!!.toInt(),
            k3 = row.getEntry(27)!!.toInt(),
            k2 = row.getEntry(28)!!.toInt(),
            k1 = row.getEntry(29)!!.toInt(),
            v1 = row.getEntry(35)!!.toInt(),
            v2 = row.getEntry(39)!!.toInt(),
            v3 = row.getEntry(43)!!.toInt(),
            v4 = row.getEntry(47)!!.toInt(),
            v5 = row.getEntry(51)!!.toInt(),
            enemies_flashed = row.getEntry(54)!!.toInt(),
            roundsplayed = roundsCount,
            firstkill_ct = if (startingCt) firstKillStartingSide[player.steam_id]?: 0 else firstKillSwitchSide[player.steam_id]?: 0 ,
            firstkill_t = if (!startingCt) firstKillStartingSide[player.steam_id]?: 0 else firstKillSwitchSide[player.steam_id]?: 0 ,
            firstdeath_ct = if (startingCt) firstDeathStartingSide[player.steam_id]?: 0  else firstDeathStartingSide[player.steam_id]?: 0 ,
            firstdeath_t = if (!startingCt) firstDeathStartingSide[player.steam_id]?: 0  else firstDeathStartingSide[player.steam_id]?: 0 ,
            friendlies_flashed = 0,
            flashbang_assists = 2,
            knife_kills = 0,
            suicides = 0,
            util_damage = 0,
            kast = roundsCount/playersCount
            )
    }

    val match = DTOmatch(
        id = matchNr,
        team1_id = (matchNr - 1) * 2 + 1,
        team2_id = (matchNr - 1) * 2 + 2,
        winner = (matchNr - 1) * 2 + teamNames.indexOf(winnerName) + 1,
        team1_score = scores[0],
        team2_score = scores[1],
        team1_string = teamNames[0],
        team2_string = teamNames[1],
        team1_series_score = 1-teamNames.indexOf(winnerName),
        team2_series_score = teamNames.indexOf(winnerName),
        start_time = dateTimeFormatter.format(startDate),
        end_time = dateTimeFormatter.format(endDate),
        veto_mappol = mapName,
        players_per_team = playersCount / 2
    )
}