
package glorydark.dcurrency.commands.admins;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;
import glorydark.dcurrency.provider.CurrencyJsonProvider;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CurrencyReloadCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyReloadCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    public static double add(double n1, double n2) {
        return new BigDecimal(n1).add(new BigDecimal(n2)).doubleValue();
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        Config config = new Config(CurrencyMain.getInstance().getPath() + File.separator + "config.yml", Config.YAML);
        CurrencyMain.setRegisteredCurrencies(new ArrayList<>(config.getStringList("registered_currencies")));
        sender.sendMessage(TextFormat.GREEN + "Reload successfully");
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