package ua.db;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.model.Synchronize;

import java.time.LocalDateTime;

@Repository
public interface SynchronizeRepository extends CrudRepository<Synchronize, Integer> {

    @Query("select s.synchronizeTime from Synchronize s where s.success = true order by s.synchronizeTime desc limit 1")
    LocalDateTime findLastSuccessBySynchronizeTime();
}
