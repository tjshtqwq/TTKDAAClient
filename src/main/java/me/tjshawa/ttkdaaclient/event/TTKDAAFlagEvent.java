package me.tjshawa.ttkdaaclient.event;

import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class TTKDAAFlagEvent extends Event {

    private final Player player;
    private final String complex;
    private final String message;
    private final Check check;
    private final PlayerData data;


    public TTKDAAFlagEvent(Player player, String complex, String message, Check check, PlayerData data) {
        super(true);
        this.player = player;
        this.check = check;
        this.complex = complex;
        this.message = message;
        this.data = data;
    }

    public Player getPlayer() {return player;}

    public Check getCheck() {return check;}

    public String getComplex() {return complex;}

    public String getMessage() {return message;}

    public PlayerData getData() {return data;}

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}