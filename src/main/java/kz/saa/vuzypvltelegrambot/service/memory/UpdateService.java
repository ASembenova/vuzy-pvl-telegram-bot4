package kz.saa.vuzypvltelegrambot.service.memory;

import kz.saa.vuzypvltelegrambot.db.domain.UserUpdate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public interface UpdateService {

   void save(Update update);
   List<UserUpdate> findAll();
   List<UserUpdate> findAllByChatId(long chatId);
   UserUpdate findByUpdateId(int updateId);
}
