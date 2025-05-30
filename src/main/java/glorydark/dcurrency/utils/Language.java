package glorydark.dcurrency.utils;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.CurrencyMain;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Language {

    protected HashMap<String, Map<String, Object>> lang;

    protected String defaultLanguage;

    public Language() {
        this(Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
    }

    public Language(String defaultLanguage) {
        lang = new HashMap<>();
        this.defaultLanguage = defaultLanguage;
    }

    public void addLanguage(File file) {
        if (file.getName().endsWith(".properties")) {
            String locale = file.getName().substring(0, file.getName().lastIndexOf("."));
            lang.put(locale, new Config(file, Config.PROPERTIES).getAll());
            CurrencyMain.getInstance().getLogger().info("§aLanguage Loaded: " + locale);
        } else {
            CurrencyMain.getInstance().getLogger().info("§cInvalid Language File: " + file.getName());
        }
    }

    public String getTranslationWithDefaultValue(String language, String key, String defaultValue, Object... param) {
        String processedText = (String) lang.getOrDefault(language, new HashMap<>()).getOrDefault(key, defaultValue == null ? key : defaultValue);
        if (param.length > 0) {
            for (int i = 1; i <= param.length; i++) {
                processedText = processedText.replaceAll("%" + i + "%", String.valueOf(param[i - 1]));
            }
        }
        processedText = processedText.replace("\\n", "\n");
        return processedText;
    }

    public String getTranslation(String key, Object... param) {
        return getTranslationWithDefaultValue(defaultLanguage, key, key, param);
    }

    public String getTranslation(CommandSender sender, String key, Object... param) {
        if (sender.isPlayer()) {
            return getTranslation((Player) sender, key, param);
        } else {
            return getTranslation(key, param);
        }
    }

    public String getTranslation(Player player, String key, Object... param) {
        return getTranslationWithDefaultValue(getLang(player), key, key, param);
    }

    private String getLang(Player player) {
        String languageCode = player.getLoginChainData().getLanguageCode();
        return lang.containsKey(languageCode) ? languageCode : defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
