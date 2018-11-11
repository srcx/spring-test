package cz.srnet.spring.test;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class DatabaseController {

	private final DatabaseRepository repository;
	private final DatabaseConnections connections;

	public DatabaseController(DatabaseRepository repository, DatabaseConnections connections) {
		this.repository = repository;
		this.connections = connections;
	}

	@GetMapping("/databases")
	public List<Database> list() {
		return repository.findAll(Sort.by("name"));
	}

	@PostMapping("/databases")
	public Database create(@RequestBody Database database) {
		return repository.save(database);
	}

	@GetMapping("/databases/{name}")
	public Database getByName(@PathVariable String name) {
		return repository.findById(name).orElseThrow(() -> new DatabaseNotFoundException(name));
	}

	@PutMapping("/databases/{name}")
	public @NotNull Database update(@RequestBody Database database, @PathVariable String name) {
		if (database.getName() == null) {
			database.setName(name);
		}
		return repository.save(database);
	}

	@DeleteMapping("/databases/{name}")
	public void delete(@PathVariable String name) {
		repository.deleteById(name);
	}

	@GetMapping("/databases/{name}/tables")
	public List<Table> listTables(@PathVariable String name) {
		Database database = getByName(name);
		DatabaseConnection connection = connections.get(database);
		return connection.listTables();
	}

	@GetMapping("/databases/{name}/tables/{table}")
	public Table tableByName(@PathVariable String name, @PathVariable String table) {
		Database database = getByName(name);
		DatabaseConnection connection = connections.get(database);
		return connection.getTable(table);
	}

	@GetMapping("/databases/{name}/tables/{table}/data")
	public TableData tableData(@PathVariable String name, @PathVariable String table) {
		Database database = getByName(name);
		DatabaseConnection connection = connections.get(database);
		return connection.getTableData(table, 10);
	}

}
