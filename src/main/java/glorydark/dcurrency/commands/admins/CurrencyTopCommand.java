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

public class CurrencyTopCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyTopCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    public static double add(double n1, double n2) {
        return new BigDecimal(n1).add(new BigDecimal(n2)).doubleValue();
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if (sender.isPlayer() && !sender.isOp() && strings.length > 1) {
            return false;
        }
        if (CurrencyMain.getProvider() instanceof CurrencyJsonProvider) {
            String key = strings[1];
            int page = 1;
            if (strings.length == 3) {
                page = Integer.parseInt(strings[2]);
            }
            Map<String, Double> map = new HashMap<>();
            File file = new File(CurrencyMain.getInstance().getPath() + "/players/");
            for (File json : Objects.requireNonNull(file.listFiles())) {
                if (json.isFile() && json.getName().endsWith(".json")) {
                    try {
                        Config config = new Config(json, Config.JSON);
                        map.put(json.getName().substring(0, json.getName().lastIndexOf(".")), config.getDouble(key, 0d));
                    } catch (Throwable t) {
                        CurrencyMain.getInstance().getLogger().error("Found a broken file: " + json);
                        t.printStackTrace();
                    }
                }
            }
            List<Map.Entry<String, Double>> results = map.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());
            Collections.reverse(results);
            int total = 0;
            for (Map.Entry<String, Double> result : results) {
                total += result.getValue();
            }
            if (!results.isEmpty()) {
                int startIndex = (page - 1) * 10; // 0
                int endIndex = Math.min(results.size() - 1, startIndex + 9); // 0
                sender.sendMessage(CurrencyMain.getLang().getTranslation("message.op.top.title", key, page, results.size() / page + ((results.size() % page > 0) ? 1 : 0), total));
                for (int i = startIndex; i <= endIndex; i++) {
                    Map.Entry<String, Double> result = results.get(i);
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message.op.top.entry", i + 1, result.getKey(), result.getValue()));
                }
            } else {
                sender.sendMessage("Short of data!");
            }
        } else {
            sender.sendMessage(TextFormat.RED + "This can be only used when JsonProvider is on!");
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