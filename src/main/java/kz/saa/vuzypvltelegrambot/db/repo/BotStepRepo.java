package kz.saa.vuzypvltelegrambot.db.repo;

import kz.saa.vuzypvltelegrambot.db.domain.BotStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotStepRepo extends JpaRepository<BotStep, Long> {
    void deleteBotStepsByChatId(long chatId);
}
