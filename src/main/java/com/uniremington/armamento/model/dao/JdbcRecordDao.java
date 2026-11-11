package com.uniremington.armamento.model.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public abstract class JdbcRecordDao<T> {
    private static final Pattern IDENTIFIER = Pattern.compile("[a-z][a-z0-9_]*");

    private final JdbcTemplate jdbcTemplate;
    private final Class<T> recordType;
    private final String tableName;
    private final List<String> idColumns;
    private final RecordComponent[] components;
    private final Constructor<T> constructor;
    private final RowMapper<T> rowMapper = this::mapRow;

    protected JdbcRecordDao(
            JdbcTemplate jdbcTemplate,
            Class<T> recordType,
            String tableName,
            String... idColumns) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate");
        this.recordType = Objects.requireNonNull(recordType, "recordType");
        this.tableName = requireIdentifier(tableName);
        this.idColumns = Arrays.stream(idColumns).map(JdbcRecordDao::requireIdentifier).toList();
        if (this.idColumns.isEmpty()) {
            throw new IllegalArgumentException("At least one primary-key column is required");
        }
        if (!recordType.isRecord()) {
            throw new IllegalArgumentException(recordType.getName() + " must be a record");
        }

        components = recordType.getRecordComponents();
        List<String> componentColumns = Arrays.stream(components)
                .map(component -> toSnakeCase(component.getName()))
                .toList();
        if (!componentColumns.containsAll(this.idColumns)) {
            throw new IllegalArgumentException("Primary-key columns must match record components");
        }
        try {
            constructor = recordType.getDeclaredConstructor(
                    Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new));
        } catch (NoSuchMethodException exception) {
            throw new IllegalArgumentException("Record canonical constructor was not found", exception);
        }
    }

    public List<T> findAll() {
        return jdbcTemplate.query("SELECT * FROM " + tableName, rowMapper);
    }

    public Optional<T> findById(Object... idValues) {
        validateIdValues(idValues);
        String predicate = String.join(" AND ", idColumns.stream().map(column -> column + " = ?").toList());
        List<T> results = jdbcTemplate.query(
                "SELECT * FROM " + tableName + " WHERE " + predicate, rowMapper, idValues);
        return results.stream().findFirst();
    }

    public T save(T record) {
        Objects.requireNonNull(record, "record");
        Object[] idValues = readIdValues(record);
        if (Arrays.stream(idValues).allMatch(Objects::nonNull)) {
            int updated = update(record, idValues);
            if (updated > 0 || exists(idValues)) {
                return record;
            }
        } else if (idColumns.size() > 1 && Arrays.stream(idValues).anyMatch(Objects::nonNull)) {
            throw new IllegalArgumentException("All composite primary-key values must be provided");
        }
        return insert(record, idValues);
    }

    public boolean deleteById(Object... idValues) {
        validateIdValues(idValues);
        String predicate = String.join(" AND ", idColumns.stream().map(column -> column + " = ?").toList());
        return jdbcTemplate.update("DELETE FROM " + tableName + " WHERE " + predicate, idValues) > 0;
    }

    private int update(T record, Object[] idValues) {
        List<RecordComponent> mutableComponents = Arrays.stream(components)
                .filter(component -> !idColumns.contains(toSnakeCase(component.getName())))
                .toList();
        String assignments = String.join(", ", mutableComponents.stream()
                .map(component -> toSnakeCase(component.getName()) + " = ?").toList());
        List<Object> parameters = new ArrayList<>();
        for (RecordComponent component : mutableComponents) {
            parameters.add(readComponent(record, component));
        }
        parameters.addAll(Arrays.asList(idValues));
        String predicate = String.join(" AND ", idColumns.stream().map(column -> column + " = ?").toList());
        return jdbcTemplate.update("UPDATE " + tableName + " SET " + assignments + " WHERE " + predicate,
                parameters.toArray());
    }

    private T insert(T record, Object[] idValues) {
        List<RecordComponent> insertComponents = new ArrayList<>();
        for (RecordComponent component : components) {
            String column = toSnakeCase(component.getName());
            int keyIndex = idColumns.indexOf(column);
            if (keyIndex < 0 || idValues[keyIndex] != null) {
                insertComponents.add(component);
            }
        }

        String columns = String.join(", ", insertComponents.stream()
                .map(component -> toSnakeCase(component.getName())).toList());
        String placeholders = String.join(", ", insertComponents.stream().map(component -> "?").toList());
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        Object[] values = insertComponents.stream().map(component -> readComponent(record, component)).toArray();
        boolean generatedId = idColumns.size() == 1 && idValues[0] == null;

        if (!generatedId) {
            jdbcTemplate.update(sql, values);
            return record;
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[] { idColumns.get(0) });
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }
            return statement;
        }, keyHolder);
        Number generatedKey = keyHolder.getKey();
        if (generatedKey == null) {
            throw new IllegalStateException("The database did not return a generated key for " + tableName);
        }
        return withGeneratedId(record, generatedKey);
    }

    private boolean exists(Object[] idValues) {
        String predicate = String.join(" AND ", idColumns.stream().map(column -> column + " = ?").toList());
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + tableName + " WHERE " + predicate, Integer.class, idValues);
        return count != null && count > 0;
    }

    private Object[] readIdValues(T record) {
        Object[] values = new Object[idColumns.size()];
        for (int keyIndex = 0; keyIndex < idColumns.size(); keyIndex++) {
            String keyColumn = idColumns.get(keyIndex);
            RecordComponent component = Arrays.stream(components)
                    .filter(candidate -> toSnakeCase(candidate.getName()).equals(keyColumn))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Primary-key component was not found"));
            values[keyIndex] = readComponent(record, component);
        }
        return values;
    }

    private T withGeneratedId(T record, Number generatedKey) {
        Object[] values = new Object[components.length];
        for (int index = 0; index < components.length; index++) {
            RecordComponent component = components[index];
            if (toSnakeCase(component.getName()).equals(idColumns.get(0))) {
                values[index] = convertNumber(generatedKey, component.getType());
            } else {
                values[index] = readComponent(record, component);
            }
        }
        return construct(values);
    }

    private T mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        ResultSetMetaData metadata = resultSet.getMetaData();
        List<String> columnNames = new ArrayList<>();
        for (int index = 1; index <= metadata.getColumnCount(); index++) {
            columnNames.add(metadata.getColumnLabel(index).toLowerCase(Locale.ROOT));
        }

        Object[] values = new Object[components.length];
        for (int index = 0; index < components.length; index++) {
            RecordComponent component = components[index];
            String column = toSnakeCase(component.getName());
            int columnIndex = columnNames.indexOf(column);
            if (columnIndex < 0) {
                throw new SQLException("Missing column " + column + " in table " + tableName);
            }
            Object value = resultSet.getObject(columnIndex + 1);
            values[index] = convertValue(value, component.getType());
        }
        return construct(values);
    }

    private T construct(Object[] values) {
        try {
            return constructor.newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Could not map database row to " + recordType.getName(), exception);
        }
    }

    private static Object convertValue(Object value, Class<?> targetType) throws SQLException {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        if (targetType == String.class && value instanceof Clob clob) {
            return clob.getSubString(1, Math.toIntExact(clob.length()));
        }
        if (targetType == LocalDateTime.class && value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (targetType == LocalDate.class && value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        if (value instanceof Number number) {
            return convertNumber(number, targetType);
        }
        if (targetType == Boolean.class && value instanceof String text) {
            return Boolean.valueOf(text);
        }
        return value;
    }

    private static Object convertNumber(Number number, Class<?> targetType) {
        if (targetType == Integer.class || targetType == int.class) {
            return number.intValue();
        }
        if (targetType == Long.class || targetType == long.class) {
            return number.longValue();
        }
        if (targetType == Short.class || targetType == short.class) {
            return number.shortValue();
        }
        if (targetType == BigDecimal.class && !(number instanceof BigDecimal)) {
            return new BigDecimal(number.toString());
        }
        return number;
    }

    private Object readComponent(T record, RecordComponent component) {
        try {
            return component.getAccessor().invoke(record);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Could not read " + component.getName(), exception);
        }
    }

    private void validateIdValues(Object[] idValues) {
        if (idValues.length != idColumns.size() || Arrays.stream(idValues).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Expected " + idColumns.size() + " non-null primary-key value(s)");
        }
    }

    private static String requireIdentifier(String identifier) {
        if (identifier == null || !IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("Invalid SQL identifier: " + identifier);
        }
        return identifier;
    }

    private static String toSnakeCase(String value) {
        return value.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }
}
