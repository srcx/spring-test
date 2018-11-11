package cz.srnet.spring.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
class DatabaseConnectionsImpl implements DatabaseConnections {

	private final Map<Database, DatabaseConnection> connections = new ConcurrentHashMap<>();

	@Override
	public DatabaseConnection get(Database database) {
		return connections.computeIfAbsent(database, DatabaseConnection::new);
	}

}
