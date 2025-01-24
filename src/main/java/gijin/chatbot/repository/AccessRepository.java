package gijin.chatbot.repository;

import gijin.chatbot.entity.AccessEntity;
import gijin.chatbot.entity.RefreshEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AccessRepository extends JpaRepository<AccessEntity,Long>
{
    Optional<AccessEntity> findByUserid(String userid);
    Boolean existsByAccess(String access);
    @Transactional
    void deleteByUserid(String userid);
}
