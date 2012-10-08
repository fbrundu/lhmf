package it.polito.ai.lhmf.android.api;

import it.polito.ai.lhmf.model.Member;

public interface UserOperations {
	Integer getMemberType();

	Member getMember(Integer memberId);
	
	Member[] getMembersToActivate();

	Integer activateMember(Integer idMember);
}
