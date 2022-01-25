package me.keano.parkour.board;

import lombok.Getter;
import me.keano.parkour.Parkour;
import me.keano.parkour.board.entry.BoardEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Board {

    private final List<BoardEntry> entries;
    private final List<String> identifiers;

    private final UUID uuid;

    public Board(Player player) {
        this.entries = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.uuid = player.getUniqueId();
        this.setup(player);
    }

    public Scoreboard getScoreboard() {
        Player player = Bukkit.getPlayer(getUuid());

        if (player == null) return null;

        if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
            return player.getScoreboard();
        } else {
            return Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }

    public Objective getObjective() {
        Scoreboard scoreboard = getScoreboard();
        if (scoreboard.getObjective("Assemble") == null) {
            Objective objective = scoreboard.registerNewObjective(
                    "Assemble",
                    "dummy",
                    // keqno - set title when registering, 1.17
                    Parkour.getInstance().getBoardManager().getAdapter().getTitle(Bukkit.getPlayer(getUuid()))
            );

            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            return objective;
        } else {
            return scoreboard.getObjective("Assemble");
        }
    }

    private void setup(Player player) {
        Scoreboard scoreboard = getScoreboard();
        player.setScoreboard(scoreboard);
        getObjective();
    }

    public BoardEntry getEntryAtPosition(int pos) {
        return pos >= this.entries.size() ? null : this.entries.get(pos);
    }

    public String getUniqueIdentifier(int position) {
        String identifier = getRandomChatColor(position) + ChatColor.WHITE;

        while (this.identifiers.contains(identifier)) {
            identifier = identifier + getRandomChatColor(position) + ChatColor.WHITE;
        }

        // This is rare, but just in case, make the method recursive
        if (identifier.length() > 16) {
            return this.getUniqueIdentifier(position);
        }

        // Add our identifier to the list so there are no duplicates
        this.identifiers.add(identifier);

        return identifier;
    }

    private String getRandomChatColor(int position) {
        return Parkour.getInstance().getBoardManager().getChatColorCache()[position].toString();
    }
}