package glorydark.dcurrency.commands.players;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;

public class CurrencyPayCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyPayCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.isPlayer();
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
    public boolean execute(CommandSender sender, String[] strings) {
        if (strings.length != 4) {
            sender.sendMessage(getHelp());
            return false;
        }
        Player player = Server.getInstance().getPlayer(strings[1]);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(CurrencyMain.getLang("message_default_playerNotFound"));
            return true;
        }
        String currencyName = strings[2];
        double count = Double.parseDouble(strings[3]);
        if (Double.isInfinite(count) || Double.isNaN(count)) {
            sender.sendMessage(CurrencyMain.getLang("message_player_general_unsupported_value"));
            return true;
        }
        if (CurrencyMain.getProvider().reduceCurrencyBalance(sender.getName(), currencyName, count)) {
            CurrencyMain.getProvider().addCurrencyBalance(player.getName(), currencyName, count);
            sender.sendMessage(CurrencyMain.getLang("message_player_pay_success"));
        } else {
            sender.sendMessage(CurrencyMain.getLang("message_player_pay_noEnoughCurrency"));
        }
        return true;
    }

    @Override
    public String getHelp() {
        return help;
    }
}
