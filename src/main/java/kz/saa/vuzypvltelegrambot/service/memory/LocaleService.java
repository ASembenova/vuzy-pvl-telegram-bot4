package kz.saa.vuzypvltelegrambot.service.memory;

import kz.saa.vuzypvltelegrambot.db.domain.Lang;
import kz.saa.vuzypvltelegrambot.db.repo.LangRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class LocaleService {
    private Map<Long, Locale> localeMap;
    private Locale defaultLocale;
    private MessageSource messageSource;
    private final LangRepo langRepo;

    public LocaleService(@Value("${localeTag}") String localeTag, MessageSource messageSource, LangRepo langRepo) {
        this.messageSource = messageSource;
        this.defaultLocale = Locale.forLanguageTag(localeTag);
        this.langRepo = langRepo;
        localeMap = new HashMap<>();
    }

    public void changeLang(String localeTag, long chatId){
        Locale locale = Locale.forLanguageTag(localeTag);
        Lang lang = new Lang();
        lang.setLocale(locale);
        lang.setChatId(chatId);
        langRepo.save(lang);
        localeMap.put(chatId, locale);
    }

    public String getMessage(String message, long chatId) {
        return messageSource.getMessage(message, null, getLocale(chatId));
    }


    public String getLocaleTag(long chatId){
        return getLocale(chatId).getLanguage();
    }

    private Locale getLocale(long chatId){
        if(localeMap.isEmpty() || !localeMap.containsKey(chatId)){
            if(langRepo.findByChatId(chatId)==null){
                Lang lang = new Lang();
                lang.setLocale(defaultLocale);
                lang.setChatId(chatId);
                langRepo.save(lang);
                localeMap.put(chatId, lang.getLocale());
            } else {
                Lang lang = langRepo.findByChatId(chatId);
                localeMap.put(chatId, lang.getLocale());
            }
        }
        return localeMap.get(chatId);
    }

    public boolean isEmpty(){
        return langRepo.findAll().isEmpty();
    }

    public boolean containsUser(long chatId){
        return langRepo.findByChatId(chatId)!=null;
    }
}
