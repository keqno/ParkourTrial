package me.keano.parkour.board;

import lombok.Getter;
import lombok.Setter;
import me.keano.parkour.Parkour;
import me.keano.parkour.board.listener.BoardListener;
import me.keano.parkour.board.thread.BoardThread;
import me.keano.parkour.board.type.BoardType;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
/*
Credits: https://github.com/ThatKawaiiSam/Assemble
Assemble made cleaner - Keqno_
 */
public class BoardManager {

    private Parkour instance;
    private BoardAdapter adapter;
    private BoardThread thread;

    private Map<UUID, Board> boards;
    private long ticks = 2;

    private final ChatColor[] chatColorCache = ChatColor.values();

    public BoardManager(Parkour instance) {
        this.instance = instance;

        this.adapter = new BoardType(instance);
        this.boards = new ConcurrentHashMap<>();
        this.thread = new BoardThread(this);

        instance.getServer().getPluginManager().registerEvents(new BoardListener(), instance);
    }
}