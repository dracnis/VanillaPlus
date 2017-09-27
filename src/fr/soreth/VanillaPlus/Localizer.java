package fr.soreth.VanillaPlus;


public enum Localizer {
	AFRIKAANS("Afrikaans", "af_ZA"),
	ARABIC("العربية", "ar_SA"),
	ARMENIAN("Հայերեն", "hy_AM"),
	BAHASA_INDONESIA("Bahasa Indonesia", "id_ID"),
	BULGARIAN("Български", "bg_BG"),
	CATALAN("Català", "ca_ES"),
	CHINESE_SIMPLIFIED("简体中文", "zh_CN"),
	CHINESE_TRADITIONAL("繁體中文", "zh_TW"),
	CROATIAN("Hrvatski", "hr_HR"),
	CZECH("Čeština", "cs_CZ"),
	DANISH("Dansk", "da_DK"),
	DUTCH("Netherlands", "nl_NL"),
	ENGLISH("English", "en_GB"),
	ENGLISH_AUSTRALIAN("Australian English", "en_AU"),
	ENGLISH_CANADIAN("Canadian English", "en_CA"),
	ESPERANTO("Esperanto", "eo_EO"),
	ESTONIAN("Eesti", "et_EE"),
	FINNISH("Suomi", "fi_FI"),
	FRENCH("Français", "fr_FR"),
	FRENCH_CANADIAN("Canadien Français", "fr_CA"),
	GALICIAN("Galego", "gl_ES"),
	GEORGIAN("ქართული", "ka_GE"),
	GERMAN("Deutsch", "de_DE"),
	GREEK("Ελληνικά", "el_GR"),
	HEBREW("עברית", "he_IL"),
	HUNGARIAN("Magyar", "hu_HU"),
	ICELANDIC("Íslenska", "is_IS"),
	ITALIAN("Italiano", "it_IT"),
	JAPANESE("日本語", "ja_JP"),
	KOREAN("한국어", "ko_KR"),
	LITHUANIAN("Lietuvių", "lt_LT"),
	LATVIAN("Latviešu", "lv_LV"),
	MALTI("Malti", "mt_MT"),
	NORWEGIAN("Norsk", "nb_NO"),
	PIRATE_SPEAK("Pirate Speak", "en_PT"),
	POLISH("Polski", "pl_PL"),
	PORTUGUESE("Português", "pt_PT"),
	ROMANIAN("Română", "ro_RO"),
	RUSSIAN("Русский", "ru_RU"),
	SERBIAN("Српски", "sr_SP"),
	SLOVENIAN("Slovenščina", "sl_SI"),
	SPANISH("Español", "es_ES"),
	SPANISH_ARGENTINEAN("Español Argentine", "es_AR"),
	SPANISH_MEXICO("Español México", "es_MX"),
	SPANISH_URUGUAY("Español Uruguay", "es_UY"),
	SPANISH_VENEZUELA("Español Venezuela", "es_VE"),
	SWEDISH("Svenska", "sv_SE"),
	THAI("ภาษาไทย", "th_TH"),
	TURKISH("Türkçe", "tr_TR"),
	UKRAINIAN("Українська", "uk_UA"),
	VIETNAMESE("Tiếng Việt", "vi_VI");
	
    private String name;
    private String code;
 
    private Localizer(String name, String code) {
        this.name = name;
        this.code = code;
    }
	/**
	 * Get the Language by code.
	 *
	 * @param code The language code.
	 * @return The language or default server value if invalid.
	 */
	public static Localizer getByCode(String code) {
		return getByCode(code, VanillaPlusCore.getDefaultLang());
	}
	/**
	 * Get the Language by code.
	 *
	 * @param code The language code.
	 * @param defaultValue The default value.
	 * @return The language or default if invalid.
	 */
	public static Localizer getByCode(String code, Localizer defaultValue) {
        for (Localizer l : values()) {
            if (l.getCode().equalsIgnoreCase(code))
            	return l;
        }
        ErrorLogger.addError(code + " not found use "+ defaultValue.code);
        return defaultValue;
	}
	/**
	 * Get the Language's code.
	 *
	 * @return The language's code.
	 */
    public String getCode() {
        return code;
    }
	/**
	 * Get the Language's name.
	 *
	 * @return The language's name.
	 */
    public String getName() {
        return name;
    }
}