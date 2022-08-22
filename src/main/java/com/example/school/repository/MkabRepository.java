package com.example.school.repository;

import com.example.school.entity.Mkab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class MkabRepository {
    String query = "select top(1) m.n_pol,m.ss, m.s_pol " +
            "from dbo.hlt_MKAB m where\n" +
            "N_POL is not null and" +
            " m.date_bd = :date_bd and m.family = :family and m.name = :name and m.ot = :ot" +
            " order by DateMKAB desc";

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    Mkab mkab;

/*    public void setDataSource(DataSource dataSource) {
        namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }*/

    public Mkab findBy(LocalDate date_bd, String family, String name, String ot) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("date_bd", date_bd)
                .addValue("family", family)
                .addValue("name", name)
                .addValue("ot", ot);
        mkab= (Mkab) namedJdbcTemplate.queryForObject(query,params, new MkabMapper());
        return mkab;
    }

    private class MkabMapper implements RowMapper {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mkab m=new Mkab();
            m.setN_pol(rs.getString("n_pol"));
            m.setSs(rs.getString("ss"));
            return m;
        }
    }

    ;
}

/*
public interface MkabRepository extends JpaRepository<Mkab, Integer> {
    @Query(value = "select m.date_bd " +
            "from hlt_Mkab m where m.date_bd = ?1 and m.family = ?2 and m.name = ?3 and m.ot = ?4",nativeQuery = true)
    Mkab findByBirthDateAndLastNameAndFirstNameAndPatronymic(LocalDate birthDate, String lastName, String firstName, String patronymic);
}*/
