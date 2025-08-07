package glorydark.dcurrency.event;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

/**
 * @author glorydark
 */
public class GiveMoneyByCommandEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final String player;

    private String currencyName;

    private double amount;

    private final String reason;

    public GiveMoneyByCommandEvent(String player, String currencyName, double amount, String reason) {
        this.player = player;
        this.currencyName = currencyName;
        this.amount = amount;
        this.reason = reason;
    }

    public String getPlayer() {
        return player;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
