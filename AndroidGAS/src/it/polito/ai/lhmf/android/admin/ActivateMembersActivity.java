package it.polito.ai.lhmf.android.admin;

import it.polito.ai.lhmf.android.R;
import it.polito.ai.lhmf.android.api.Gas;
import it.polito.ai.lhmf.android.api.util.GasConnectionHolder;
import it.polito.ai.lhmf.model.Member;
import it.polito.ai.lhmf.model.constants.MemberStatuses;
import it.polito.ai.lhmf.model.constants.MemberTypes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.connect.Connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivateMembersActivity extends Activity {
	private static final int CONFIRM_MEMBER_ACTIVATION = 0;
	
	private GasConnectionHolder holder;
	private Gas api = null;
	private CustomAdapter adapter = null;
	
	private ListView membersListView = null;
	
	private TextView noMembers = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		holder = new GasConnectionHolder(getApplicationContext());
		Connection<Gas> conn = holder.getConnection();
		if(conn != null){
			api  = conn.getApi();
			setContentView(R.layout.members_activation);
			
			membersListView = (ListView) findViewById(R.id.membersList);
			
			noMembers = (TextView) findViewById(R.id.no_members);
		}
	}
	
	@Override
	protected void onResume() {
		if(api != null){
			new GetMembersToActivateTask().execute(api);
		}
		super.onResume();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == CONFIRM_MEMBER_ACTIVATION){
			AlertDialog.Builder builder = new AlertDialog.Builder(ActivateMembersActivity.this);
			builder.setTitle("Conferma attivezione utente");
			builder.setMessage("");
			
			builder.setPositiveButton("Ok", null);
			
			builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			return builder.create();
		}
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if(id == CONFIRM_MEMBER_ACTIVATION){
			final Member m = (Member) args.getParcelable("member");
			if(m != null){
				AlertDialog aDialog = (AlertDialog) dialog;
				aDialog.setMessage("Confermi di voler attivare l'utente " + m.getName() + " " + m.getSurname() + "?");
				
				aDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ActivateMemberTask().execute(api, m);
					}
				});
			}
		}
		else
			super.onPrepareDialog(id, dialog, args);
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.logout:
				if(api != null)
					new LogoutAsyncTask().execute(api);
				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private class LogoutAsyncTask extends AsyncTask<Gas, Void, Void>{

		@Override
		protected Void doInBackground(Gas... params) {
			params[0].logout();
			return null;
		}
		 @Override
		protected void onPostExecute(Void result) {
			holder.destroy();
			holder = null;
			api = null;
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			nm.cancelAll();
			
			ActivateMembersActivity.this.finish();
		}
	}
	*/
	
	private class GetMembersToActivateTask extends AsyncTask<Gas, Void, Member[]>{

		@Override
		protected Member[] doInBackground(Gas... params) {
			Gas gas = params[0];
			
			return gas.userOperations().getMembersToActivate();
		}
		
		@Override
		protected void onPostExecute(Member[] result) {
			if(result != null && result.length > 0){
				List<Member> adapterList = new ArrayList<Member>();
				for(Member m : result)
					adapterList.add(m);
				adapter = new CustomAdapter(ActivateMembersActivity.this, R.layout.member_item, R.id.memberName, adapterList);
				membersListView.setAdapter(adapter);
				membersListView.setVisibility(View.VISIBLE);
				noMembers.setVisibility(View.GONE);
			}
			else{
				membersListView.setVisibility(View.GONE);
				noMembers.setVisibility(View.VISIBLE);
			}
		}		
	}
	
	private class ActivateMemberTask extends AsyncTask<Object, Void, Object[]>{

		@Override
		protected Object[] doInBackground(Object... params) {
			Gas gas = (Gas) params[0];
			Member member = (Member) params[1];
			
			Object[] ret = new Object[2];
			ret[0] = member;
			ret[1] = gas.userOperations().activateMember(member.getIdMember());
			return ret;
		}
		
		@Override
		protected void onPostExecute(Object[] res) {
			Member member = (Member) res[0];
			Integer result = (Integer) res[1];
			if(result == null || result != 1){
				Toast.makeText(ActivateMembersActivity.this, "Errore durante l'attivazione dell'utente", Toast.LENGTH_LONG).show();
			}
			else{
				adapter.remove(member);
				if(adapter.getCount() == 0){
					membersListView.setVisibility(View.GONE);
					noMembers.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	private class CustomAdapter extends ArrayAdapter<Member>{
		
		public CustomAdapter(Context context, int resource,
				int textViewResourceId, List<Member> adapterList) {
			super(context, resource, textViewResourceId, adapterList);
		}
		
		public CustomAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			TextView memberName;
			TextView memberType;
			TextView mailCheckStatus;
			Button activateButton;
			
			final Member member = getItem(position);
			
			if(row == null){
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.member_item, parent, false);
			}
			memberName = (TextView) row.findViewById(R.id.memberName);
			
			memberType = (TextView) row.findViewById(R.id.memberType);
			
			mailCheckStatus = (TextView) row.findViewById(R.id.mail_status);
			
			activateButton = (Button) row.findViewById(R.id.member_activate_button);
			
			memberName.setText(member.getName() + " " + member.getSurname());
			
			if(member.getMemberTypeId() == MemberTypes.USER_NORMAL)
				memberType.setText(R.string.member_normal);
			else if(member.getMemberTypeId() == MemberTypes.USER_RESP)
				memberType.setText(R.string.member_resp);
			else if(member.getMemberTypeId() == MemberTypes.USER_SUPPLIER)
				memberType.setText(R.string.member_supplier);
			
			if(member.getMemberStatusId() == MemberStatuses.NOT_VERIFIED){
				mailCheckStatus.setText(R.string.mail_not_verified);
				mailCheckStatus.setTextColor(Color.RED);
			}
			else if(member.getMemberStatusId() == MemberStatuses.VERIFIED_DISABLED){
				mailCheckStatus.setText(R.string.mail_verified);
				mailCheckStatus.setTextColor(Color.GREEN);
			}
			
			activateButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					args.putParcelable("member", member);
					showDialog(CONFIRM_MEMBER_ACTIVATION, args);
				}
			});
			
			row.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ActivateMembersActivity.this, MemberDetailsActivity.class);
					intent.putExtra("member", member);
					startActivity(intent);
				}
			});
			
			return row;
		}
	}
}
