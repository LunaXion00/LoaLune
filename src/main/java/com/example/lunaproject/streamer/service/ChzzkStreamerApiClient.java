package com.example.lunaproject.streamer.service;

import com.example.lunaproject.character.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;

@RequiredArgsConstructor
@Service
public class ChzzkStreamerApiClient {
    private final ChzzkApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

    public JSONObject findStreamerByChannelId(String channelId){
        String link = "https://api.chzzk.naver.com/service/v1/channels/" + channelId;
        InputStreamReader inputStreamReader = apiClient.ChzzkGetApi(link);
        JSONParser parser = new JSONParser();
        try{
            JSONObject object = (JSONObject) parser.parse(inputStreamReader);
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
