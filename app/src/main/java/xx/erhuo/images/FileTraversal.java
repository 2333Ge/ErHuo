package xx.erhuo.images;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


/**目录，及其包含的文件*/
public class FileTraversal implements Parcelable {
	private String filename;//所属图片的文件目录名称
	private List<String> filecontent=new ArrayList<String>();//该目录下文件列表
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public List<String> getFilecontent() {
		return filecontent;
	}
	public void setFilecontent(List<String> filecontent) {
		this.filecontent = filecontent;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(filename);
		dest.writeList(filecontent);
	}
	
	public static final Parcelable.Creator<FileTraversal> CREATOR=new Creator<FileTraversal>() {
		
		@Override
		public FileTraversal[] newArray(int size) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public FileTraversal createFromParcel(Parcel source) {
			FileTraversal ft=new FileTraversal();
			ft.filename= source.readString();
			
			ft.filecontent= source.readArrayList(FileTraversal.class.getClassLoader());
			
			return ft;
		}
		
		
	};
}
