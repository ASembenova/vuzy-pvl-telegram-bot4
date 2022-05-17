package kz.saa.vuzypvltelegrambot.service.memory;

import kz.saa.vuzypvltelegrambot.db.domain.BotStep;
import kz.saa.vuzypvltelegrambot.db.domain.User;
import kz.saa.vuzypvltelegrambot.db.repo.BotStepRepo;
import kz.saa.vuzypvltelegrambot.db.repo.UserRepo;
import kz.saa.vuzypvltelegrambot.model.Step;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserServiceDBImpl implements UserService{

    private final UserRepo userRepo;
    private final BotStepRepo botStepRepo;
    private final Set<Long> set;
    public UserServiceDBImpl(UserRepo userRepo, BotStepRepo botStepRepo) {
        this.userRepo = userRepo;
        this.botStepRepo = botStepRepo;
        this.set = new HashSet<>();
        List<User> users = userRepo.findAll();
        if(!users.isEmpty()){
            for (User user:users) {
                set.add(user.getChatId());
            }
        }
    }

    @Override
    public void addUser(User user) {
        User old = userRepo.findByChatId(user.getChatId());
        if(old!=null){
            //botStepRepo.deleteBotStepsByChatId(user.getChatId());
            userRepo.delete(old);
        }
        userRepo.save(user);
        set.add(user.getChatId());
    }

    @Override
    public void addStep(long chatId, Step step) {
        User user = userRepo.findByChatId(chatId);
        BotStep oldStep = user.getLastStep();
        BotStep newStep = new BotStep(step, chatId);
        if(user.getStepList()==null){
            newStep.setSequence(0);
        } else {
            newStep.setSequence(user.getStepList().size());
        }
        if(!oldStep.getStep().name().equals(newStep.getStep().name())){
            user.getStepList().add(newStep);
            userRepo.save(user);
        }
    }

    @Override
    public BotStep deleteLastStep(long chatId) {
        User user = userRepo.getById(chatId);
        if(user!=null){
            BotStep botStep = user.removeLastStep();
            userRepo.save(user);
            return botStep;
        }
        return null;
    }

    @Override
    public BotStep getLastStep(long chatId) {
        if(containsUser(chatId)){
            List<BotStep> list = userRepo.findByChatId(chatId).getStepList();
            if(list.size()>1){
                return list.get(list.size()-1);
            }
        }
        return new BotStep(Step.START, chatId);
    }

    @Override
    public boolean containsUser(long chatId) {
        return set.contains(chatId);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public User findByChatId(long chatId) {
        return userRepo.findByChatId(chatId);
    }
}

