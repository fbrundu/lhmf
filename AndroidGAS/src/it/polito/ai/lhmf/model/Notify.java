package it.polito.ai.lhmf.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notify implements Parcelable{
	private Integer idNotify;
	private Integer notifyCategory;
	private String text;
	
	public Integer getIdNotify() {
		return idNotify;
	}

	public void setIdNotify(Integer idNotify) {
		this.idNotify = idNotify;
	}

	public Integer getNotifyCategory() {
		return notifyCategory;
	}

	public void setNotifyCategory(Integer notifyCategory) {
		this.notifyCategory = notifyCategory;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idNotify);
		dest.writeInt(notifyCategory);
		dest.writeString(text);
	}
	
	public static final Parcelable.Creator<Notify> CREATOR = new Creator<Notify>() {
		
		@Override
		public Notify[] newArray(int size) {
			return new Notify[size];
		}
		
		@Override
		public Notify createFromParcel(Parcel source) {
			Notify notify = new Notify();
			notify.setIdNotify(source.readInt());
			notify.setNotifyCategory(source.readInt());
			notify.setText(source.readString());
			return notify;
		}
	};
}