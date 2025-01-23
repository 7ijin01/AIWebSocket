package gijin.chatbot.repository;

import gijin.chatbot.entity.AccessEntity;
import gijin.chatbot.entity.RefreshEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity,Long>
{
    Optional<RefreshEntity> findByUserid(String userid);
    Boolean existsByRefresh(String refresh);
    @Transactional
    void deleteByRefresh(String refresh);
}
