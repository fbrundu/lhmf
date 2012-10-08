package it.polito.ai.lhmf.android.admin;

import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.model.Member;
import it.polito.ai.lhmf.model.constants.MemberTypes;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MemberDetailsActivity extends Activity {
	private Member member;
	
	private TextView name = null, memberType = null, email = null,
			address = null, city = null, state = null, cap = null, tel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_details);
		
		name = (TextView) findViewById(R.id.memberName);
		memberType = (TextView) findViewById(R.id.memberType);
		email = (TextView) findViewById(R.id.memberEmail);
		address = (TextView) findViewById(R.id.memberAddress);
		city = (TextView) findViewById(R.id.memberCity);
		state = (TextView) findViewById(R.id.memberState);
		cap = (TextView) findViewById(R.id.memberCap);
		tel = (TextView) findViewById(R.id.memberTel);
		
		member = getIntent().getParcelableExtra("member");
		if(member != null){
			name.setText(member.getName() + " " + member.getSurname());
			
			int type = member.getMemberTypeId();
			if(type == MemberTypes.USER_NORMAL)
				memberType.setText(R.string.member_normal);
			else if(type == MemberTypes.USER_RESP)
				memberType.setText(R.string.member_resp);
			else if(type == MemberTypes.USER_SUPPLIER)
				memberType.setText(R.string.member_supplier);
			
			email.setText(member.getEmail());
			
			address.setText(member.getAddress());
			
			city.setText(member.getCity());
			
			state.setText(member.getState());
			
			cap.setText(member.getCap());
			
			if(member.getTel() != null)
				tel.setText(member.getTel());
		}
	}
}
