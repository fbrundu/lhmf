package it.polito.ai.lhmf_gas.repo;

import java.util.List;

import it.polito.ai.lhmf_gas.domain.Member;

public interface MemberDao
{
    public Member findById(Long id);

    public Member findByEmail(String email);

    public List<Member> findAllOrderedByName();

    public void register(Member member);
}
