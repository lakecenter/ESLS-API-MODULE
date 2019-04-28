package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import com.wdy.module.converter.StringToTimestampConverter;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Data
@Table(name = "logs", schema = "tags", catalog = "")
@ToString
public class Logs implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "username")
    @ExcelField(title = "createDate", order = 2)
    private String username;
    @Column(name = "operation")
    @ExcelField(title = "operation", order = 3)
    private String operation;
    @Column(name = "method")
    @ExcelField(title = "method", order = 4)
    private String method;
    @Column(name = "params")
    @ExcelField(title = "params", order = 5)
    private String params;
    @Column(name = "ip")
    @ExcelField(title = "ip", order = 6)
    private String ip;
    @Column(name = "runningTime")
    @ExcelField(title = "runningTime", order = 7)
    private String runningTime;
    @Column(name = "createDate")
    @ExcelField(title = "createDate", order = 8, readConverter = StringToTimestampConverter.class)
    private Timestamp createDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Logs logs = (Logs) o;
        return Objects.equals(id, logs.id) &&
                Objects.equals(username, logs.username) &&
                Objects.equals(operation, logs.operation) &&
                Objects.equals(method, logs.method) &&
                Objects.equals(params, logs.params) &&
                Objects.equals(ip, logs.ip) &&
                Objects.equals(runningTime, logs.runningTime) &&
                Objects.equals(createDate, logs.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, operation, method, params, ip, runningTime, createDate);
    }
}
