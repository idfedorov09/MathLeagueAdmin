package ru.mathleague.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import ru.mathleague.service.RedisService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

@Component
public class ProblemSender {

    private String BOT_TOKEN;
    private String CHAT_ID;

    @Autowired
    private RedisService redisService;

    @Autowired
    private final ResourceLoader resourceLoader;

    @Autowired
    public ProblemSender(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init(){

        try{
            BOT_TOKEN = (String) redisService.getConfig("botToken");
            CHAT_ID = (String) redisService.getConfig("chatId");
            if( BOT_TOKEN == null || CHAT_ID == null) throw new NullPointerException();
        }catch (NullPointerException nullptr) {

            System.out.println("Can't load bot properties from Redis. Trying get info using config file.");

            Properties properties = new Properties();
            Resource resource = resourceLoader.getResource("classpath:tg.properties");


            try (InputStream inputStream = resource.getInputStream()) {
                properties.load(inputStream);
                this.BOT_TOKEN = properties.getProperty("tgBotToken");
                this.CHAT_ID = properties.getProperty("tgChatId");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    public void setBotToken(String botToken) {
        BOT_TOKEN = botToken;
        redisService.saveConfig("botToken", botToken);
    }

    public String getChatId() {
        return CHAT_ID;
    }

    public void setChatId(String chatId) {
        CHAT_ID = chatId;
        redisService.saveConfig("chatId", chatId);
    }

    public void sendPhotoToTelegram(String imagePath, String description) {

        try {
            URL url = new URL("https://api.telegram.org/bot" + BOT_TOKEN + "/sendPhoto");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String boundary = "------------------------" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = conn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n");
            writer.append(CHAT_ID).append("\r\n");

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"caption\"\r\n\r\n");
            writer.append(description).append("\r\n");

            File imageFile = new File(imagePath);
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"photo\"; filename=\"").append(imageFile.getName()).append("\"\r\n");
            writer.append("Content-Type: ").append("image/jpeg").append("\r\n\r\n");
            writer.flush();

            FileInputStream inputStream = new FileInputStream(imageFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append("\r\n");
            writer.append("--").append(boundary).append("--\r\n");
            writer.close();

            int responseCode = conn.getResponseCode();
            //System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //System.out.println(response.toString());
            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
