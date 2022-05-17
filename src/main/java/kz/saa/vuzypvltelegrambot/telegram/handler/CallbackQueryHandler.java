package kz.saa.vuzypvltelegrambot.telegram.handler;

import kz.saa.vuzypvltelegrambot.egovapi.DataObjectService;
import kz.saa.vuzypvltelegrambot.egovapi.Vuz;
import kz.saa.vuzypvltelegrambot.service.ParametersMenuService;
import kz.saa.vuzypvltelegrambot.service.report.CompareService;
import kz.saa.vuzypvltelegrambot.service.speciality.InfoService;
import kz.saa.vuzypvltelegrambot.service.MenuService;
import kz.saa.vuzypvltelegrambot.service.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.sql.Date;

@Component
public class CallbackQueryHandler {
    private final InfoService infoService;
    private final MessageSender messageSender;
    private final DataObjectService dataObjectService;
    private final CompareService compareService;
    private final MenuService menuService;
    private final ParametersMenuService parametersMenuService;

    public CallbackQueryHandler(InfoService infoService, MessageSender messageSender, DataObjectService dataObjectService, CompareService compareService, MenuService menuService, ParametersMenuService parametersMenuService) {
        this.infoService = infoService;
        this.messageSender = messageSender;
        this.dataObjectService = dataObjectService;
        this.compareService = compareService;
        this.menuService = menuService;
        this.parametersMenuService = parametersMenuService;
    }

    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        final String callbackId = callbackQuery.getId();
        final long messageId = callbackQuery.getMessage().getMessageId();
        String data = callbackQuery.getData();
        if (data.contains("btn")){
            return processCallbackBtn(chatId, data, messageId);
        } else if(data.contains("generate_pdf")){
            Date date = new Date(System.currentTimeMillis());
            return generatePdfComparingSpec(chatId, data, callbackId, callbackQuery, date);
        } else if (data.contains("compare")){
            return addCheckMarkVuz(chatId, data, messageId);
        } else if (data.contains("name")){
            return addCheckMarkParams(chatId, data, messageId);
        } else if (data.contains("custom_params_msg")){
            return generateByParams(chatId, data, messageId, callbackId);
        }
        System.out.println("process call back fall");
        return null;
    }

    private BotApiMethod<?> generateByParams(long chatId, String data, long messageId, String callbackId) {
        return parametersMenuService.generatePdfByParams(chatId, callbackId);
        //return messageSender.createMessageWithKeyboard(chatId, "test", null);
    }

    private BotApiMethod<?> addCheckMarkVuz(long chatId, String data, long messageId) {
        String[] params = data.split(" "); //example: "compare_byname first 1"
        int[] selected = new int[2];
        if(params[1].equals("first")){
            selected[0] = Integer.parseInt(params[2]);
        } else if(params[1].equals("second")){
            selected[1] = Integer.parseInt(params[2]);
        }
        return compareService.getEditedComparingMessage(chatId, messageId, data.split(" ")[0], selected);
    }

    private BotApiMethod<?> addCheckMarkParams(long chatId, String data, long messageId) {
        int num = Integer.parseInt(data.substring(4)); // "name11"
        return parametersMenuService.getEditedCustomParametersMessage(chatId, messageId, num-1);
    }

    private BotApiMethod<?> generatePdfComparingSpec(long chatId, String data, String callbackId, CallbackQuery callbackQuery, Date dateOrder) {
        String[] params = data.split(" ");
        String compareMode = params[1];
        int first = Integer.parseInt(params[2]);
        int second = Integer.parseInt(params[3]);
        if(first!=0&&second!=0){
            if(first==second){
                return messageSender.getAnswerCallbackQuery(chatId, "compare.choose_other", callbackId);
            }
            messageSender.execute(messageSender.getAnswerCallbackQuery(chatId,
                            "compare.wait", callbackId));
            Vuz vuz1 = dataObjectService.getVuzy()[first-1];
            Vuz vuz2 = dataObjectService.getVuzy()[second-1];
            String textMessage = compareService.getComparingResults(chatId, vuz1, vuz2, compareMode, callbackQuery.getFrom().getFirstName(), callbackQuery.getFrom().getLastName(), dateOrder);
            return messageSender.createMessageWithKeyboard(chatId, textMessage, null);
        } else{
            return messageSender.getAnswerCallbackQuery(chatId, "compare.not_selected", callbackId);
        }
    }

    private BotApiMethod<?> processCallbackBtn(long chatId, String data, long messageId) {
        int index = Integer.valueOf(String.valueOf(data.charAt(data.length()-1)));
        data = data.substring(0, data.length()-1);
        switch (data){
            case ("btn.previous"):
                return infoService.getMessageWithNextVuz(chatId, messageId, index);
            case ("btn.next"):
                return infoService.getMessageWithPreviousVuz(chatId, messageId, index);
            case ("btn.speclist"):
                return infoService.getMessageWithSpecList(chatId, index);
            case ("btn.geo"):
                return infoService.getMessageWithLocation(chatId, index);
            case ("btn.call"):
                return infoService.getMessageWithContact(chatId, index);
            case ("btn.site"):
                return infoService.getMessageWithContact(chatId, index);
        }
        return null;
    }
}

