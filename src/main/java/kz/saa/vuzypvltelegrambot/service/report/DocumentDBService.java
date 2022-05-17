package kz.saa.vuzypvltelegrambot.service.report;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kz.saa.vuzypvltelegrambot.db.domain.GeneratedDocument;
import kz.saa.vuzypvltelegrambot.db.repo.GeneratedDocumentRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class DocumentDBService {
    @Value("https://api.telegram.org/bot${telegrambot.botToken}/getFile?file_id=")
    private String linkRequest;
    @Value("https://api.telegram.org/file/bot${telegrambot.botToken}/")
    private String linkFileBase;
    private final GeneratedDocumentRepo generatedDocumentRepo;

    public DocumentDBService(GeneratedDocumentRepo generatedDocumentRepo) {
        this.generatedDocumentRepo = generatedDocumentRepo;
    }

    public void add(GeneratedDocument generatedDocument){
        try {
            URL url = new URL(linkRequest+generatedDocument.getFileId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String result = response.toString();
            Gson gson = new Gson();
            TelegramResponse telegramResponse = gson.fromJson(result, TelegramResponse.class);
            generatedDocument.setDownloadLink(linkFileBase+telegramResponse.result.file_path);
        }catch (IOException e){
            e.printStackTrace();
        }
        //generatedDocument.setDownloadLink();
        generatedDocumentRepo.save(generatedDocument);
    }

    public List<GeneratedDocument> findAll(){
        return generatedDocumentRepo.findAll();
    }

    public int getCounter(){
        return findAll().size();
    }

}

