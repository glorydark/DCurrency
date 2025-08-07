package glorydark.dcurrency.commands.admins;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;
import glorydark.dcurrency.event.GiveMoneyByCommandEvent;
import glorydark.dcurrency.provider.CurrencyJsonProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CurrencyGiveCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyGiveCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if (sender.isPlayer() && !sender.isOp()) {
            return false;
        }
        if (strings.length < 4) {
            return false;
        }
        String reason = "default";
        if (strings.length == 5) {
            reason = strings[4];
        }
        if (!CurrencyMain.getRegisteredCurrencies().contains(strings[2])) {
            sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_unregistered_currencies", strings[2]));
            return false;
        }
        String currencyName = strings[2];
        double count = Double.parseDouble(strings[3]);
        switch (strings[1]) {
            case "@each":
                if (CurrencyMain.getProvider() instanceof CurrencyJsonProvider) {
                    List<String> players = CurrencyAPI.getAllPlayers();
                    for (String player : players) {
                        GiveMoneyByCommandEvent ev = new GiveMoneyByCommandEvent(player, currencyName, count, reason);
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            CurrencyMain.getProvider().addCurrencyBalance(player, ev.getCurrencyName(), ev.getAmount(), reason);
                        }
                    }
                }
                break;
            case "@a":
                List<Player> allPlayerList = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());
                if (allPlayerList.isEmpty()) {
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_giveCurrency_no_online_player"));
                    return true;
                } else {
                    List<String> playerNames = new ArrayList<>();
                    for (Player player : allPlayerList) {
                        String playerName = player.getName();
                        playerNames.add(playerName);
                        GiveMoneyByCommandEvent ev = new GiveMoneyByCommandEvent(playerName, currencyName, count, reason);
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            CurrencyMain.getProvider().addCurrencyBalance(playerName, ev.getCurrencyName(), ev.getAmount(), reason);
                        }
                    }
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_giveCurrency_all", Arrays.toString(playerNames.toArray()).replace("[", "").replace("]", ""), strings[2], strings[3], reason));
                }
                break;
            case "@r":
                Collection<Player> allPlayerCollection = Server.getInstance().getOnlinePlayers().values();
                if (allPlayerCollection.isEmpty()) {
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_giveCurrency_no_online_player"));
                    return true;
                } else {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    String playerName = allPlayerCollection.toArray(new Player[0])[random.nextInt(0, allPlayerCollection.size())].getName();
                    GiveMoneyByCommandEvent ev = new GiveMoneyByCommandEvent(playerName, currencyName, count, reason);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        CurrencyMain.getProvider().addCurrencyBalance(playerName, ev.getCurrencyName(), ev.getAmount(), reason);
                    }
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_giveCurrency", playerName, strings[2], strings[3]));
                }
                break;
            default:
                if (!Server.getInstance().lookupName(strings[1]).isPresent()) {
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message.default.player_not_found", strings[1]));
                    return false;
                } else {
                    GiveMoneyByCommandEvent ev = new GiveMoneyByCommandEvent(strings[1], currencyName, count, reason);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        CurrencyMain.getProvider().addCurrencyBalance(strings[1], ev.getCurrencyName(), ev.getAmount(), reason);
                    }
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_giveCurrency", strings[1], strings[2], strings[3]));
                }
                break;
        }
        return true;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return !sender.isPlayer() || sender.isOp();
    }

    @Override
    public String getName() {
        return command;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getHelp() {
        return help;
    }
}