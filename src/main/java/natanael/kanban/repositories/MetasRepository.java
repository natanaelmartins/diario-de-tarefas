package natanael.kanban.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import natanael.kanban.model.MetasEntity;

@Repository
public interface MetasRepository extends JpaRepository<MetasEntity, UUID> {

    List<MetasEntity> findByUsuarioUsername(String username);
}