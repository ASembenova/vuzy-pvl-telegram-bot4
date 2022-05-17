package kz.saa.vuzypvltelegrambot.db.domain;

import javax.persistence.*;
import java.util.Locale;

@Entity
public class Lang {
    @Id
    private Long chatId;
    private Locale locale;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatId")
    private User user;

    public Lang(){
    }

    public Lang(Long chatId, Locale locale) {
        this.chatId = chatId;
        this.locale = locale;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
