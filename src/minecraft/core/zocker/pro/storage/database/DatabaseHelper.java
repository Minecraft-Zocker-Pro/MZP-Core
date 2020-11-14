package minecraft.core.zocker.pro.storage.database;

import minecraft.core.zocker.pro.storage.StorageManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseHelper {

	public void createTable(String createString) {
		try {
			Statement createStatement;
			if (StorageManager.isMySQL()) {
				if (StorageManager.getMySQLDatabase() == null) {
					// TODO error
					return;
				}

				createStatement = StorageManager.getMySQLDatabase().getConnection().createStatement();

			} else {
				if (StorageManager.getSQLiteDatabase() == null) {
					// TODO error
					return;
				}

				createStatement = StorageManager.getSQLiteDatabase().getConnection().createStatement();
			}

			createStatement.executeUpdate(createString);
			createStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// region Select

	public ResultSet select(String table, String[] columns) {
		if ((table != null) && (columns.length > 0)) {

			String selectString = "SELECT";
			int i = 1;
			for (String key : columns) {
				selectString += " " + key;
				if (!key.equals(columns[columns.length - 1])) {
					selectString += ",";
				}
			}
			selectString += " FROM `" + table + "`";
			try {
				PreparedStatement selectStatement;
				if (StorageManager.isMySQL()) {
					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return null;
					}

					selectStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(selectString);

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return null;
					}

					selectStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(selectString);
				}

				return selectStatement.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		} else return null;
	}

	public ResultSet select(String table, String column, String where, String condition) {
		if (table == null || column == null || where == null || condition == null) return null;

		try {
			PreparedStatement selectStatement;
			String selectString = "SELECT " + column + " FROM `" + table + "` WHERE " + where + " = ?";

			if (StorageManager.isMySQL()) {
				if (StorageManager.getMySQLDatabase() == null) {
					// TODO error
					return null;
				}

				selectStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(selectString);

			} else {
				if (StorageManager.getSQLiteDatabase() == null) {
					// TODO error
					return null;
				}

				selectStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(selectString);
			}


			selectStatement.setString(1, condition);
			return selectStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		return null;
	}

	public ResultSet select(String table, String[] columns, String where, String condition) {
		if ((table != null) && (columns.length > 0) && (where != null) && (condition != null)) {

			String selectString = "SELECT";
			int i = 1;
			for (String key : columns) {
				selectString += " " + key;
				if (!key.equals(columns[columns.length - 1])) {
					selectString += ",";
				}
			}
			selectString += " FROM `" + table + "` WHERE " + where + " = ?";
			try {
				PreparedStatement selectStatement;
				if (StorageManager.isMySQL()) {
					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return null;
					}

					selectStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(selectString);

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return null;
					}

					selectStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(selectString);
				}

				selectStatement.setString(i, condition);
				return selectStatement.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		} else return null;
	}

	public ResultSet selectConditional(String table, String column, String[] primaryKeys, Object[] primaryValues) {
		if (table == null || column == null || primaryKeys.length != primaryValues.length) return null;

		StringBuilder selectString = new StringBuilder("SELECT");

		selectString.append(" ").append(column);
		selectString.append(" FROM `").append(table).append("` WHERE ");

		for (String primaryKey : primaryKeys) {
			selectString.append(primaryKey).append(" = ?");
			if (!primaryKey.equals(primaryKeys[primaryKeys.length - 1])) selectString.append(" AND ");
		}

		try {
			PreparedStatement selectStatement;
			if (StorageManager.isMySQL()) {
				if (StorageManager.getMySQLDatabase() == null) {
					// TODO error
					return null;
				}

				selectStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(selectString.toString());

			} else {
				if (StorageManager.getSQLiteDatabase() == null) {
					// TODO error
					return null;
				}

				selectStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(selectString.toString());
			}

			for (int i = 1; i <= primaryValues.length; i++) {
				selectStatement.setString(i, primaryValues[i - 1].toString());
			}

			return selectStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet selectConditional(String table, String[] columns, String[] primaryKeys, Object[] primaryValues) {
		if (table == null || columns == null || primaryKeys.length != primaryValues.length) return null;

		StringBuilder selectString = new StringBuilder("SELECT");

		for (String column : columns) {
			selectString.append(" ").append(column);
			if (!column.equals(columns[columns.length - 1])) {
				selectString.append(",");
			}
		}

		selectString.append(" FROM `").append(table).append("` WHERE ");

		for (String primaryKey : primaryKeys) {
			selectString.append(primaryKey).append(" = ?");
			if (!primaryKey.equals(primaryKeys[primaryKeys.length - 1])) selectString.append(" AND ");
		}

		try {
			PreparedStatement selectStatement;

			if (StorageManager.isMySQL()) {
				if (StorageManager.getMySQLDatabase() == null) {
					// TODO error
					return null;
				}

				selectStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(selectString.toString());

			} else {
				if (StorageManager.getSQLiteDatabase() == null) {
					// TODO error
					return null;
				}

				selectStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(selectString.toString());
			}

			for (int i = 1; i <= primaryValues.length; i++) {
				selectStatement.setString(i, primaryValues[i - 1].toString());
			}

			return selectStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// endregion

	// region Update

	public boolean update(String table, String[] columns, Object[] values, String where, String condition) {
		if (((table != null)) && (columns.length > 0) && (columns.length == values.length)) {
			int i = 1;
			String updateString = "UPDATE `" + table + "` SET";
			try {
				//add "?" for all columns to command
				for (String key : columns) {
					updateString += " " + key + " = ?";
					if (!key.equals(columns[columns.length - 1])) {
						updateString += ",";
					}
				}

				updateString += " WHERE " + where + " = ?";
				PreparedStatement updateStatement;
				if (StorageManager.isMySQL()) {

					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(updateString.toString());

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(updateString.toString());
				}

				//add all columns and values to command
				for (int x = 0; x < columns.length; x++) {
					if (values[x] != null) {
						updateStatement.setString(i, values[x].toString());
					} else {
						updateStatement.setString(i, "");
					}
					i++;
				}

				updateStatement.setString(i, condition);
				boolean success = updateStatement.executeUpdate() > 0;
				updateStatement.close();
				return success;

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;

	}

	public boolean update(String table, String column, Object value, String where, String condition) {
		if ((table != null) && (column != null) && (value != null) && (where != null) && (condition != null)) {
			int i = 1;
			String updateString = "UPDATE `" + table + "` SET";
			updateString += " " + column + " = ? WHERE " + where + " = ?";
			try {
				PreparedStatement updateStatement;
				if (StorageManager.isMySQL()) {

					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(updateString);

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(updateString);
				}

				updateStatement.setString(i, value.toString());
				updateStatement.setString(i + 1, condition);

				boolean success = updateStatement.executeUpdate() > 0;
				updateStatement.close();

				return success;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} else return false;

	}

	public boolean updateConditional(String table, String column, Object value, String[] whereKeys, Object[] whereValues) {
		if (((table != null)) && (column != null) && (value != null) && (whereKeys != null) && (whereValues != null)) {
			int i = 0;
			String updateString = "UPDATE `" + table + "` SET";
			updateString += " " + column + " = ? WHERE ";

			for (String whereKey : whereKeys) {
				updateString += whereKey;
				updateString += " = ";
				updateString += "?";
				i++;
				if (!whereKey.equals(whereKeys[whereKeys.length - 1])) {
					updateString += " AND ";
				}

			}

			try {
				PreparedStatement updateStatement;
				if (StorageManager.isMySQL()) {

					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(updateString);

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(updateString);
				}

				updateStatement.setString(1, value.toString());
				for (int x = 0; x < whereValues.length; x++) {
					updateStatement.setString(x + 2, whereValues[x].toString());
				}

				boolean success = updateStatement.executeUpdate() > 0;
				updateStatement.close();

				return success;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} else return false;

	}

	public boolean updateConditional(String table, String[] columns, Object[] values, String[] whereKeys, Object[] whereValues) {
		if ((table != null) && (columns.length > 0) && (columns.length == values.length) && (whereKeys != null) && (whereValues != null)) {
			StringBuilder updateString = new StringBuilder("UPDATE `" + table + "` SET");
			for (String key : columns) {
				updateString.append(" ").append(key).append(" = ?");
				if (!key.equals(columns[columns.length - 1])) {
					updateString.append(",");
				}
			}

			updateString.append(" WHERE ");
			for (String whereKey : whereKeys) {
				updateString.append(whereKey);
				updateString.append(" = ");
				updateString.append("?");

				if (!whereKey.equals(whereKeys[whereKeys.length - 1])) {
					updateString.append(" AND ");
				}
			}

			try {
				PreparedStatement updateStatement;
				if (StorageManager.isMySQL()) {

					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(updateString.toString());

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return false;
					}

					updateStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(updateString.toString());
				}

				for (int x = 0; x < columns.length; x++) {
					if (values[x] != null) {
						updateStatement.setString(x + 1, values[x].toString());
					} else {
						updateStatement.setString(x + 1, "");
					}
				}

				for (int x = 0; x < whereValues.length; x++) {
					updateStatement.setString(x + columns.length + 1, whereValues[x].toString());
				}

				boolean success = updateStatement.executeUpdate() > 0;
				updateStatement.close();

				return success;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

	// endregion

	// region Insert

	public boolean insert(String table, String[] columns, Object[] values) {
		int i = 1;
		String insertString = "INSERT INTO `" + table + "` (";

		// add columns to command
		for (String key : columns) {
			insertString += " " + key;
			if (!key.equals(columns[columns.length - 1])) {
				insertString += ",";
			}
		}

		insertString += ") VALUES (";

		// add "?" for all values to command
		for (int v = 0; v < values.length; v++) {
			insertString += " ?";
			if (v < values.length - 1) {
				insertString += ",";
			}
		}

		insertString += " )";

		try {
			PreparedStatement insertStatement;

			if (StorageManager.isMySQL()) {
				if (StorageManager.getMySQLDatabase() == null) {
					// TODO error
					return false;
				}

				insertStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(insertString);

			} else {
				if (StorageManager.getSQLiteDatabase() == null) {
					// TODO error
					return false;
				}

				insertStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(insertString);
			}

			// add all columns to command
			for (Object value : values) {
				insertStatement.setString(i, value.toString());
				i++;
			}
			boolean success = insertStatement.executeUpdate() > 0;
			insertStatement.close();

			return success;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// endregion

	// region Delete

	public boolean delete(String table, String where, String condition) {
		if ((table != null) && (where != null) && (condition != null)) {
			String deleteString = "DELETE FROM `" + table + "` WHERE " + where + " = '" + condition + "';";

			try {
				PreparedStatement deleteStatement;

				if (StorageManager.isMySQL()) {
					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return false;
					}

					deleteStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(deleteString);

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return false;
					}

					deleteStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(deleteString);
				}

				boolean success = deleteStatement.executeUpdate() > 0;
				deleteStatement.close();

				return success;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} else return false;

	}

	public boolean deleteConditional(String table, String[] columns, Object[] values) {
		if ((table != null) && columns.length > 0 && values.length > 0) {
			String deleteString = "DELETE FROM `" + table + "` WHERE ";
			for (String key : columns) {
				deleteString += key + " = ?";
				if (!key.equals(columns[columns.length - 1])) {
					deleteString += " AND ";
				}
			}

			try {
				PreparedStatement deleteStatement;

				if (StorageManager.isMySQL()) {
					if (StorageManager.getMySQLDatabase() == null) {
						// TODO error
						return false;
					}

					deleteStatement = StorageManager.getMySQLDatabase().getConnection().prepareStatement(deleteString);

				} else {
					if (StorageManager.getSQLiteDatabase() == null) {
						// TODO error
						return false;
					}

					deleteStatement = StorageManager.getSQLiteDatabase().getConnection().prepareStatement(deleteString);
				}

				for (int i = 0; i < columns.length; i++) {
					deleteStatement.setString(i + 1, values[i].toString());
				}
				boolean success = deleteStatement.executeUpdate() > 0;
				deleteStatement.close();

				return success;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

	// endregion

}
