package com.gestion.filmotheque.repository;

import com.gestion.filmotheque.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Find room by name
    Room findByName(String name);
}