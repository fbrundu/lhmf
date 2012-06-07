package it.polito.ai.gasproject.orm;

// Generated 7-giu-2012 22.56.48 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Log generated by hbm2java
 */
@Entity
@Table(name = "log", catalog = "malnati_project")
public class Log implements java.io.Serializable {

	private Integer idLog;
	private Member member;
	private String logtext;

	public Log() {
	}

	public Log(Member member, String logtext) {
		this.member = member;
		this.logtext = logtext;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idLog", unique = true, nullable = false)
	public Integer getIdLog() {
		return this.idLog;
	}

	public void setIdLog(Integer idLog) {
		this.idLog = idLog;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idMember", nullable = false)
	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@Column(name = "logtext", nullable = false, length = 300)
	public String getLogtext() {
		return this.logtext;
	}

	public void setLogtext(String logtext) {
		this.logtext = logtext;
	}

}
