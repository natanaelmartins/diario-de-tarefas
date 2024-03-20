package natanael.kanban.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import natanael.kanban.model.TarefasEntity;

@Repository
public interface TarefasRepository extends JpaRepository<TarefasEntity, UUID> {
    
}
