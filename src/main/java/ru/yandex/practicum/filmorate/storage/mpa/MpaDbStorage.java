package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "select * from rating_mpa where mpa_id = ?";

        if (doesTheMpaExist(id)) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } else {
            throw new NotFoundException(String.format("Рейтинга с ID %d не существует!", id));
        }
    }

    @Override
    public List<Mpa> showAllMpa() {
        String sqlQuery = "select * from rating_mpa";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }

    private boolean doesTheMpaExist(long id) {
        String sqlQuery = "select count(mpa_id) from rating_mpa where mpa_id = ?";
        long countId = jdbcTemplate.queryForObject(sqlQuery, Long.class, id);

        if (countId > 0) {
            return true;
        } else {
            return false;
        }
    }
}
