package glorydark.dcurrency.commands.admins;

import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;

import java.math.BigDecimal;
import java.util.HashMap;

public class CurrencyAllCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyAllCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    public static double add(double n1, double n2) {
        return new BigDecimal(n1).add(new BigDecimal(n2)).doubleValue();
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if (sender.isPlayer() && !sender.isOp()) {
            return false;
        }
        HashMap<String, Double> map = new HashMap<>();
        for (String registeredCurrency : CurrencyMain.getRegisteredCurrencies()) {
            for (double value : CurrencyMain.getProvider().getAllPlayerData(registeredCurrency).values()) {
                map.put(registeredCurrency, map.getOrDefault(registeredCurrency, 0d) + value);
            }
        }
        sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_seeAll_title"));
        map.forEach((key, value) -> sender.sendMessage(key + ":" + value));
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