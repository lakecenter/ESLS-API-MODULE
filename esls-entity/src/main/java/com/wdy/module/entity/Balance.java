package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "balance", schema = "tags", catalog = "")
public class Balance implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id;
    @Column(name = "weight")
    private String weight;
    @Column(name = "steady")
    private Byte steady;
    @Column(name = "flay")
    private Byte flay;
    @Column(name = "zero")
    private Byte zero;
    @Column(name = "overWeight")
    private Byte overWeight;
    @Column(name = "netWeight")
    private Byte netWeight;
    @Column(name = "powerInterger")
    private String powerInterger;
    @Column(name = "powerDecimal")
    private String powerDecimal;
    @ManyToOne
    @JoinColumn(name = "tagId", referencedColumnName = "id")
    @JsonIgnore
    private Tag tag;
}
