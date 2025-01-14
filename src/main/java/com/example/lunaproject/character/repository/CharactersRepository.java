package com.example.lunaproject.character.repository;

import com.example.lunaproject.character.entity.LoaCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharactersRepository extends JpaRepository<LoaCharacter, String> {
    @Query("select t from LoaCharacter t Where t.characterName= :Charactername")
    Optional<LoaCharacter> findByCharacterName(String charactername);
}
