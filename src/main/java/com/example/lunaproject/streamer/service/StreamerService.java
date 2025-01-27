package com.example.lunaproject.streamer.service;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.service.CharacterService;
import com.example.lunaproject.lostark.LostarkCharacterApiClient;
import com.example.lunaproject.streamer.dto.ChzzkResponseDTO;
import com.example.lunaproject.streamer.dto.StreamerDTO;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StreamerService {
    private final StreamerRepository streamerRepository;
    private final CharacterService characterService;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final ChzzkStreamerApiClient chzzkStreamerApiClient;
    private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

    @Value("${Lostark-API-KEY}")
    String apiKey;

    public void createStreamer(StreamerRequestDTO requestDTO) throws JsonProcessingException {
        String mainCharacter = requestDTO.getMainCharacter();
        String channelId = requestDTO.getChannelId();
        JSONObject format = chzzkStreamerApiClient.findStreamerByChannelId(channelId);
        ChzzkResponseDTO chzzkResponseDTO =  new ObjectMapper().readValue(format.toString(), ChzzkResponseDTO.class);
        logger.info("chzzkresponse: " + chzzkResponseDTO.toString());
        ChzzkResponseDTO.Content content = chzzkResponseDTO.getContent();
        StreamerDTO dto = new StreamerDTO();
        dto.setStreamerName(content.getChannelName());
        dto.setChannelImageUrl(content.getChannelImageUrl());
        logger.info("Streamer API information: "+dto.toString());

        boolean exists = streamerRepository.existsByChannelId(channelId);
        if(exists) throw new IllegalArgumentException("해당 스트리머는 이미 등록되어있습니다.");
        List<LoaCharacter> characterList = createAndCalculateCharaters(mainCharacter);
        Streamer streamer = Streamer.builder()
                .streamerName(dto.getStreamerName())
                .mainCharacter(mainCharacter)
                .channelId(requestDTO.getChannelId())
                .characters(new ArrayList<>())
                .channelImageUrl(dto.getChannelImageUrl())
                .build();
        streamer.createCharacter(characterList, mainCharacter);
        streamerRepository.save(streamer);
    }
    @Transactional(readOnly = true)
    public Streamer get(String streamerName){
        return streamerRepository.get(streamerName);
    }
    @Transactional
    public void editMainCharacter(String streamerName, String mainCharacter){
        Streamer streamer = get(streamerName);

    }

    private static void validateCreateStreamer(){

    }
    public List<LoaCharacter> getStreamerInfo(String streamerName){
        boolean exists = streamerRepository.existsByStreamerName(streamerName);
        if(!exists) throw new IllegalArgumentException(streamerName+"은(는) 등록되지 않은 스트리머명입니다.");
        Streamer streamer = streamerRepository.get(streamerName);
        return streamer.getCharacters();
    }


    private List<LoaCharacter> createAndCalculateCharaters(String characterName){
        List<LoaCharacter> characterList = lostarkCharacterApiClient.createCharacterList(characterName, apiKey);
        return characterList.stream().collect(Collectors.toList());
    }
}
