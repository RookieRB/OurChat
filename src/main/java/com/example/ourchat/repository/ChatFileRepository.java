package com.example.ourchat.repository;

import com.example.ourchat.entity.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatFileRepository  extends JpaRepository<ChatFile, Long> {
    Optional<ChatFile> findByFileId(String fileId);
}
