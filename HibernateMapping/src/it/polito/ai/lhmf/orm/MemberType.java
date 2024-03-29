package it.polito.ai.lhmf.orm;



import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * MemberType generated by hbm2java
 */
@Entity
@Table(name = "member_type", catalog = "malnati_project")
public class MemberType implements java.io.Serializable {

	private int idMemberType;
	private String description;
	private Set members = new HashSet(0);

	public MemberType() {
	}

	public MemberType(int idMemberType) {
		this.idMemberType = idMemberType;
	}

	public MemberType(int idMemberType, String description, Set members) {
		this.idMemberType = idMemberType;
		this.description = description;
		this.members = members;
	}

	@Id
	@Column(name = "idMember_Type", unique = true, nullable = false)
	public int getIdMemberType() {
		return this.idMemberType;
	}

	public void setIdMemberType(int idMemberType) {
		this.idMemberType = idMemberType;
	}

	@Column(name = "description", length = 45)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "memberType")
	public Set getMembers() {
		return this.members;
	}

	public void setMembers(Set members) {
		this.members = members;
	}

}
