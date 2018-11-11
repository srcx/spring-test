package cz.srnet.spring.test;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

final class DatabaseConnection {

	private final Database database;
	private final String url;
	private final JdbcTemplate jdbcTemplate;

	public DatabaseConnection(Database database) {
		this.database = database;
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		url = "jdbc:mysql://" + database.getHostname() + ":" + database.getPort() + "/" + database.getDatabaseName();
		dataSource.setUrl(url);
		dataSource.setUsername(database.getUsername());
		dataSource.setPassword(database.getPassword());
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@SuppressWarnings("unchecked")
	public List<Table> listTables() throws DatabaseAccessException {
		try {
			return (List<Table>) JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(),
					new DatabaseMetaDataCallback() {

						@Override
						public Object processMetaData(DatabaseMetaData dbmd)
								throws SQLException, MetaDataAccessException {
							ResultSet rs = dbmd.getTables(null, null, null, null);
							List<Table> tables = new ArrayList<>();
							while (rs.next()) {
								tables.add(new Table(rs.getString(3)));
							}
							return tables;
						}
					});
		} catch (MetaDataAccessException e) {
			throw new DatabaseAccessException(database, url, e);
		}
	}

	public Table getTable(String tableName) {
		try {
			return (Table) JdbcUtils.extractDatabaseMetaData(jdbcTemplate.getDataSource(),
					new DatabaseMetaDataCallback() {

						@Override
						public Object processMetaData(DatabaseMetaData dbmd)
								throws SQLException, MetaDataAccessException {
							Map<String, Column> columns = retrieveColumns(tableName, dbmd);
							retrievePrimaryKeys(tableName, dbmd, columns);
							Table table = new Table(tableName);
							table.setColumns(new ArrayList<>(columns.values()));
							return table;
						}
					});
		} catch (MetaDataAccessException e) {
			throw new DatabaseAccessException(database, url, e);
		}
	}

	private Map<String, Column> retrieveColumns(String tableName, DatabaseMetaData dbmd) throws SQLException {
		Map<String, Column> columns = new HashMap<>();
		ResultSet rs = dbmd.getColumns(null, null, tableName, null);
		while (rs.next()) {
			Column col = new Column();
			col.setField(rs.getString(4));
			col.setType(rs.getString(6));
			int size = rs.getInt(7);
			if (!rs.wasNull()) {
				col.setSize(size);
			}
			col.setNullable(rs.getInt(11) != DatabaseMetaData.columnNoNulls);
			col.setDefaultValue(rs.getString(13));
			columns.put(col.getField(), col);
		}
		return columns;
	}

	private void retrievePrimaryKeys(String tableName, DatabaseMetaData dbmd, Map<String, Column> columns)
			throws SQLException {
		ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName);
		while (rs.next()) {
			String colName = rs.getString(4);
			columns.get(colName).setPrimaryKey(true);
		}
	}

	public TableData getTableData(String table, int limit) {
		return jdbcTemplate.query("SELECT * FROM " + table + " LIMIT ?", new Object[] { limit }, rs -> {
			List<List<String>> data = new ArrayList<>(limit);
			int columnCount = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				List<String> row = new ArrayList<>();
				for (int i = 1; i <= columnCount; i++) {
					row.add(rs.getString(i));
				}
				data.add(row);
			}
			return new TableData(data);
		});
	}

}
