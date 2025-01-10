package com.example.lunaproject.character.service;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.repository.CharactersRepository;
import jakarta.transaction.Transactional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Service
public class CharacterService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

    private final CharactersRepository repository;

    @Autowired
    public CharacterService(CharactersRepository repository) {
        this.repository = repository;
    }


    public JSONArray Characters(String characterName){
        try{
            URL url = new URL("https://developer-lostark.game.onstove.com/characters/"+ URLEncoder.encode(characterName, "UTF-8") +"/siblings");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("accept", "application/json");
            httpURLConnection.setRequestProperty("authorization",
                    "bearer API-KEY");
            int responseCode = httpURLConnection.getResponseCode();
            InputStream inputStream;
            if(responseCode == 200){
                inputStream = httpURLConnection.getInputStream();
            }else{
                inputStream = httpURLConnection.getErrorStream();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(inputStreamReader);
            logger.info("====================="+array.toString());
            return array;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public JSONObject characterProfiles(String characterName){
        try {
            logger.info("=================="+characterName);
            characterName = URLEncoder.encode(characterName, "UTF-8");
            URL url = new URL("https://developer-lostark.game.onstove.com/armories/characters/"+characterName+"/profiles");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("authorization", "Bearer API-KEY");
            httpURLConnection.setRequestProperty("accept","application/json");
            httpURLConnection.setRequestProperty("content-Type","application/json");
            httpURLConnection.setDoOutput(true);

            int result = httpURLConnection.getResponseCode();
            InputStream inputStream;
            if(result == 200) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(inputStreamReader);
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public LoaCharacter testService(){
        LoaCharacter character = LoaCharacter.builder()
                .serverName("루페온")
                .characterName("창수리는수리조아")
                .characterClassName("창술사")
                .characterLevel(60)
                .itemLevel("1580.0")
                .build();
        return repository.save(character);
    }
    @Transactional
    public LoaCharacter addCharacter(CharacterDTO dto){
        LoaCharacter character = LoaCharacter.builder()
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .characterLevel(dto.getCharacterLevel())
                .itemLevel(dto.getItemLevel())
                .build();
        return repository.save(character);
    }
}
