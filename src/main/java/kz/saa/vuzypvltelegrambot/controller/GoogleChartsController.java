package kz.saa.vuzypvltelegrambot.controller;

import kz.saa.vuzypvltelegrambot.service.memory.UpdateService;
import kz.saa.vuzypvltelegrambot.service.memory.UserService;
import kz.saa.vuzypvltelegrambot.service.report.DocumentDBService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GoogleChartsController {
    private final UserService userService;
    private final UpdateService updateService;
    private final DocumentDBService documentDBService;

    public GoogleChartsController(UserService userService, UpdateService updateService, DocumentDBService documentDBService) {
        this.userService = userService;
        this.updateService = updateService;
        this.documentDBService = documentDBService;
    }

    @GetMapping("/chart")
    public String getPieChart(Model model) {
        Map<String, Integer> graphData = new HashMap<>();
        for (int i = 0; i < updateService.findAll().size(); i++) {
            long chatId = updateService.findAll().get(i).getChatId();
            graphData.put(String.valueOf(chatId), updateService.findAllByChatId(chatId).size());
        }

        model.addAttribute("chartData", graphData);
        return "chart";
    }

    @GetMapping("/doc")
    public String getDocs(Model model) {
        model.addAttribute("generatedDocuments", documentDBService.findAll());
        return "docs";
    }
}
