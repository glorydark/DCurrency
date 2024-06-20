package glorydark.dcurrency.commands.admins;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;

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
        if (strings.length != 4) {
            return false;
        }
        if (!CurrencyMain.getRegisteredCurrencies().contains(strings[2])) {
            sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency_unregistered_currencies", strings[2]));
            return false;
        }
        switch (strings[1]) {
            case "@a":
                List<Player> allPlayerList = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());
                if (allPlayerList.size() == 0) {
                    sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency_no_online_player"));
                    return true;
                }
                List<String> playerNames = new ArrayList<>();
                for (Player player : allPlayerList) {
                    String playerName = player.getName();
                    playerNames.add(playerName);
                    CurrencyMain.getProvider().addCurrencyBalance(playerName, strings[2], Double.parseDouble(strings[3]));
                    CurrencyMain.getPluginLogger().info(CurrencyMain.getLang("log.command.give", sender.getName(), playerName, strings[2], String.valueOf(Double.parseDouble(strings[3])), CurrencyAPI.getCurrencyBalance(playerName, strings[2])));
                }
                sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency_all", Arrays.toString(playerNames.toArray()).replace("[", "").replace("]", ""), strings[2], strings[3]));
                break;
            case "@r":
                Collection<Player> allPlayerCollection = Server.getInstance().getOnlinePlayers().values();
                if (allPlayerCollection.size() == 0) {
                    sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency_no_online_player"));
                    return true;
                }
                ThreadLocalRandom random = ThreadLocalRandom.current();
                String playerName = allPlayerCollection.toArray(new Player[0])[random.nextInt(0, allPlayerCollection.size())].getName();
                CurrencyMain.getProvider().addCurrencyBalance(playerName, strings[2], Double.parseDouble(strings[3]));
                sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency", playerName, strings[2], strings[3]));
                CurrencyMain.getPluginLogger().info(CurrencyMain.getLang("log.command.give", sender.getName(), playerName, strings[2], String.valueOf(Double.parseDouble(strings[3]))));
                break;
            default:
                CurrencyMain.getProvider().addCurrencyBalance(strings[1], strings[2], Double.parseDouble(strings[3]));
                sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency", strings[1], strings[2], strings[3]));
                CurrencyMain.getPluginLogger().info(CurrencyMain.getLang("log.command.give", sender.getName(), strings[1], strings[2], String.valueOf(Double.parseDouble(strings[3]))));
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