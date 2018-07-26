package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.DownloadStatus;

public interface DownloadStatusRepository extends JpaRepository<DownloadStatus, UUID> {
	
	Optional<DownloadStatus> findFirstByTypeAndStateOrderByPriorityDesc(String type, String state);
}
