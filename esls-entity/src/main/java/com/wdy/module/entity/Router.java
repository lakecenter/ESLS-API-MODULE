package com.wdy.module.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "routers", schema = "tags", catalog = "")
@Data
public class Router implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    private long id;
    @Column(name = "mac")
    private String mac;
    @Column(name = "ip")
    private String ip;
    @Column(name = "outNetIp")
    private String outNetIp;
    @Column(name = "port")
    private Integer port;
    @Column(name = "channelId")
    private String channelId;
    @Column(name = "state")
    private Byte state;
    @Column(name = "softVersion")
    private String softVersion;
    @Column(name = "frequency")
    private String frequency;
    @Column(name = "hardVersion")
    private String hardVersion;
    @Column(name = "execTime")
    private Integer execTime;
    @Column(name = "barCode")
    private String barCode;
    @Column(name = "isWorking")
    private Byte isWorking;
    @Column(name = "completeTime")
    private Timestamp completeTime;
    @ManyToOne
    @JoinColumn(name = "shopid", referencedColumnName = "id")
    private Shop shop;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Router router = (Router) o;
        return id == router.id &&
                Objects.equals(mac, router.mac) &&
                Objects.equals(ip, router.ip) &&
                Objects.equals(port, router.port) &&
                Objects.equals(channelId, router.channelId) &&
                Objects.equals(state, router.state) &&
                Objects.equals(softVersion, router.softVersion) &&
                Objects.equals(frequency, router.frequency) &&
                Objects.equals(hardVersion, router.hardVersion) &&
                Objects.equals(execTime, router.execTime) &&
                Objects.equals(barCode, router.barCode) &&
                Objects.equals(isWorking, router.isWorking) &&
                Objects.equals(completeTime, router.completeTime) &&
                Objects.equals(shop, router.shop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mac, ip, port, channelId, state, softVersion, frequency, hardVersion, execTime, barCode, isWorking, completeTime, shop);
    }

    @Override
    public String toString() {
        return "Router{" +
                "id=" + id +
                ", mac='" + mac + '\'' +
                ", ip='" + ip + '\'' +
                ", outNetIp='" + outNetIp + '\'' +
                ", port=" + port +
                ", channelId='" + channelId + '\'' +
                ", state=" + state +
                ", softVersion='" + softVersion + '\'' +
                ", frequency='" + frequency + '\'' +
                ", hardVersion='" + hardVersion + '\'' +
                ", execTime=" + execTime +
                ", barCode='" + barCode + '\'' +
                ", isWorking=" + isWorking +
                ", completeTime=" + completeTime +
                '}';
    }
}
