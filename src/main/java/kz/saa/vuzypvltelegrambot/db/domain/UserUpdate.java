package kz.saa.vuzypvltelegrambot.db.domain;


import com.vdurmont.emoji.EmojiParser;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name="telegram_update")
public class UserUpdate {
    @Id
    private Integer updateId;
    private Long chatId;
    private String text;
    private String callbackData;
    private Date date;
    private Time time;


    public UserUpdate() {
    }

    public UserUpdate(org.telegram.telegrambots.meta.api.objects.Update update) {
        updateId=update.getUpdateId();
        if(update.hasMessage()){
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            date = new Date((long) update.getMessage().getDate()*1000);
            time = new Time((long) update.getMessage().getDate()*1000);
        }
        if(update.hasCallbackQuery()){
            callbackData = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getFrom().getId();
            date = new Date((long) update.getCallbackQuery().getMessage().getDate()*1000);
            time = new Time((long) update.getCallbackQuery().getMessage().getDate()*1000);
        }
    }


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }

    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "UserUpdate{" +
                "updateId=" + updateId +
                ", chatId=" + chatId +
                ", text='" + text + '\'' +
                ", callbackData='" + callbackData + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
