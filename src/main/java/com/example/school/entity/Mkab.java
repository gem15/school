package com.example.school.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mkab {

    String n_pol;
    String ss;
    String s_pol;
//    String pol;
}
/*
package com.example.school.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "hlt_mkab")//,schema = "dbo"
public class Mkab {
    @Id
    @Column(name = "MKABID", nullable = false)
    private Integer id;

    @Column(name = "family")
    String lastName;

    @Column(name = "ot")
    String patronymic;

    @Column(name = "name")
    String firstName;

    @Column(name = "date_bd")
    LocalDate birthDate;
}*/
