package it.polito.ai.lhmf.orm;

// Generated 25-lug-2012 11.32.27 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Notify generated by hbm2java
 */
@Entity
@Table(name = "notify", catalog = "malnati_project")
public class Notify implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer idNotify;
	private Member member;
	private String text;
	private Date notifyTimestamp;
	private boolean isReaded;
	private int notifyCategory;

	public Notify() {
	}

	public Notify(Member member, String text, Date notifyTimestamp,
			boolean isReaded, int notifyCategory) {
		this.member = member;
		this.text = text;
		this.notifyTimestamp = notifyTimestamp;
		this.isReaded = isReaded;
		this.notifyCategory = notifyCategory;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idNotify", unique = true, nullable = false)
	public Integer getIdNotify() {
		return this.idNotify;
	}

	public void setIdNotify(Integer idNotify) {
		this.idNotify = idNotify;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idMember", nullable = false)
	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@Column(name = "text", nullable = false, length = 300)
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "notify_timestamp", nullable = false, length = 19)
	public Date getNotifyTimestamp() {
		return this.notifyTimestamp;
	}

	public void setNotifyTimestamp(Date notifyTimestamp) {
		this.notifyTimestamp = notifyTimestamp;
	}

	@Column(name = "isReaded", nullable = false)
	public boolean isIsReaded() {
		return this.isReaded;
	}

	public void setIsReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	@Column(name = "notify_category", nullable = false)
	public int getNotifyCategory() {
		return this.notifyCategory;
	}

	public void setNotifyCategory(int notifyCategory) {
		this.notifyCategory = notifyCategory;
	}

}
