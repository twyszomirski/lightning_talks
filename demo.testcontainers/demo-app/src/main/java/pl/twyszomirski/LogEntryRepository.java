package pl.twyszomirski;

import org.springframework.data.repository.CrudRepository;


public interface LogEntryRepository extends CrudRepository<LogEntry, Integer> {
}
