package kz.saa.vuzypvltelegrambot.service;


import kz.saa.vuzypvltelegrambot.egovapi.DataObjectService;
import kz.saa.vuzypvltelegrambot.service.memory.LocaleService;
import kz.saa.vuzypvltelegrambot.service.speciality.SpecialityService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.util.*;

@Service
public class MenuService {
    private final MessageSender messageSender;
    private final LocaleService localeService;
    private final SpecialityService specialityService;
    private final DataObjectService dataObjectService;
    private Map <Long, boolean[]> selectedParamsButtons;

    public MenuService(MessageSender messageSender, LocaleService localeService, SpecialityService specialityService, DataObjectService dataObjectService) {
        this.messageSender = messageSender;
        this.localeService = localeService;
        this.specialityService = specialityService;
        this.dataObjectService = dataObjectService;
        selectedParamsButtons = new HashMap<>();
    }

    public BotApiMethod<?> getWelcomeMsg(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "welcome.msg",
                Arrays.asList("lang.kz", "lang.ru"));
        }

    public BotApiMethod<?> getMainMenuMsg(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "main_menu.msg", Arrays.asList("menu.all", "menu.one", "menu.change_lang", "menu.help"));
    }

    public BotApiMethod<?> getSelectAllMsg(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "menu_all.msg", Arrays.asList("menu_all.all_param", "menu_all.select_param",
                /*"menu_all.compare_number",*/ "menu_all.compare_spec", "menu.search", "menu.back"));
    }

    public BotApiMethod<?> getSelectOneMsg(long chatId, String lang) {
        ArrayList<String> namesOfButtons = new ArrayList<>();
        namesOfButtons.addAll(dataObjectService.vuzList(chatId));
        namesOfButtons.addAll(messageSender.getButtonList(Arrays.asList("menu.help", "menu.back"), chatId));
        return messageSender.createMessageWithKeyboard(chatId, localeService.getMessage(
                "menu_one.msg", chatId), namesOfButtons);
    }

    public BotApiMethod<?> getSelectLangMsg(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "change_lang.message", Arrays.asList("lang.kz", "lang.ru"));
    }

    public BotApiMethod<?> getSearchMsg(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "search.instr", Arrays.asList("menu.back"));
    }

    public BotApiMethod<?> getSearchWarningMsg(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "search.warning", Arrays.asList("menu.back"));
    }

    public BotApiMethod<?> getSearchResults(long chatId, String messageText) {
        String result = specialityService.searchByKeyword(messageText, chatId);
        if(result.length()>=4096){
            return getSearchWarningMsg(chatId);
        }
        return messageSender.createMessageWithKeyboard(chatId, result, null);
    }

    public BotApiMethod<?> getAllParamsMsg(long chatId) {
        String text = dataObjectService.getAllParamsInfo(chatId);
        return messageSender.createMessageWithKeyboard(chatId, text, messageSender.getButtonList(
                Arrays.asList("btn.generate_pdf", "menu.back"), chatId));
    }

    public BotApiMethod<?> getCompareSpecMsg(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "compare_spec.instr",
                Arrays.asList("menu.compare_byname", "menu.compare_bycode", "menu.compare_byname_and_code", "menu.back"));
    }

    public BotApiMethod<?> getHelpMessage(long chatId) {
        return messageSender.createMessageWithKeyboardByTags(chatId, "help.msg",
                Arrays.asList("help.dataset_info", "help.opendata", "help.dataegov","help.write", "menu.back"));
    }

    public BotApiMethod<?> getHelpDatasetPassportMessage(long chatId){
        String[][] array = dataObjectService.getDatasetPassport();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            String[] row = array[i];
            if(!(row[1]==null) && !(row[1].equals(""))){
                stringBuilder.append("<b>").append(row[0]).append(": </b>").append(" ").append(row[1]).append("\n");
            }
        }
        return messageSender.createMessageWithKeyboard(chatId, stringBuilder.toString(), null);
    }

    public BotApiMethod<?> getHelpOpenDataMessage(long chatId){
        return messageSender.createMessageWithKeyboard(chatId, localeService.getMessage("help.opendata.msg", chatId), null);
    }

    public BotApiMethod<?> getHelpDataEgovMessage(long chatId){
        return messageSender.createMessageWithKeyboard(chatId, localeService.getMessage("help.dataegov.msg", chatId), null);
    }

    public BotApiMethod<?> getHelpWriteMessage(long chatId) {
        return messageSender.createMessageWithKeyboard(chatId, localeService.getMessage("help.write.msg", chatId), null);
    }
}

