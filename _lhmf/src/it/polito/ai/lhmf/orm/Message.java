package it.polito.ai.lhmf.orm;

// Generated 24-lug-2012 21.25.07 by Hibernate Tools 3.4.0.CR1

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
 * Message generated by hbm2java
 */
@Entity
@Table(name = "message")
public class Message implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer idMessage;
	private Order order;
	private Member memberByIdReceiver;
	private Product product;
	private Member memberByIdSender;
	private String text;
	private Date messageTimestamp;
	private boolean isReaded;
	private int messageCategory;

	public Message() {
	}

	public Message(Member memberByIdReceiver, Date messageTimestamp,
			boolean isReaded, int messageCategory) {
		this.memberByIdReceiver = memberByIdReceiver;
		this.messageTimestamp = messageTimestamp;
		this.isReaded = isReaded;
		this.messageCategory = messageCategory;
	}

	public Message(Order order, Member memberByIdReceiver, Product product,
			Member memberByIdSender, String text, Date messageTimestamp,
			boolean isReaded, int messageCategory) {
		this.order = order;
		this.memberByIdReceiver = memberByIdReceiver;
		this.product = product;
		this.memberByIdSender = memberByIdSender;
		this.text = text;
		this.messageTimestamp = messageTimestamp;
		this.isReaded = isReaded;
		this.messageCategory = messageCategory;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idMessage", unique = true, nullable = false)
	public Integer getIdMessage() {
		return this.idMessage;
	}

	public void setIdMessage(Integer idMessage) {
		this.idMessage = idMessage;
	}

	
	/**
	 * Restituisce l'ordine oggetto di questo messaggio
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Order_idOrder")
	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * Restituisce il membro destinatario di questo messaggio
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_receiver", nullable = false)
	public Member getMemberByIdReceiver() {
		return this.memberByIdReceiver;
	}

	public void setMemberByIdReceiver(Member memberByIdReceiver) {
		this.memberByIdReceiver = memberByIdReceiver;
	}

	/**
	 * Restituisce il prodotto 'oggetto' di questo messaggio
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Product_idProduct")
	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * Restituisce il membro 'mittente'/'oggetto' di questo messaggio
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sender")
	public Member getMemberByIdSender() {
		return this.memberByIdSender;
	}

	public void setMemberByIdSender(Member memberByIdSender) {
		this.memberByIdSender = memberByIdSender;
	}

	@Column(name = "text", length = 300)
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "message_timestamp", nullable = false, length = 19)
	public Date getMessageTimestamp() {
		return this.messageTimestamp;
	}

	public void setMessageTimestamp(Date messageTimestamp) {
		this.messageTimestamp = messageTimestamp;
	}

	@Column(name = "isReaded", nullable = false)
	public boolean isIsReaded() {
		return this.isReaded;
	}

	public void setIsReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	@Column(name = "message_category", nullable = false)
	public int getMessageCategory() {
		return this.messageCategory;
	}

	public void setMessageCategory(int messageCategory) {
		this.messageCategory = messageCategory;
	}

}
