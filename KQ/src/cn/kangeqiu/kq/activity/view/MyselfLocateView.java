package cn.kangeqiu.kq.activity.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.util.LocationUtil;

public class MyselfLocateView {
	private Activity context;
	private LayoutInflater inflater;
	private LocationUtil locationUtil;
	private TextView txt;

	public MyselfLocateView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public void setCity(String city) {
		txt.setText(city);
	}

	public View getView(String msg) {
		View view = inflater.inflate(R.layout.abc_myself_message_locate_item, null);
		txt = (TextView) view.findViewById(R.id.txt);
		// txt.setText(msg);
		// locationUtil = LocationUtil.getInstance(context);
		//
		// try {
		// // 将用户输入的经纬度值转换成int类型
		// int longitude = (int) (1000000 * Double.parseDouble(longitudeStr));
		// int latitude = (int) (1000000 * Double.parseDouble(latitudeStr));
		//
		// // 查询该经纬度值所对应的地址位置信息
		// mMKSearch.reverseGeocode(new GeoPoint(latitude, longitude));
		// } catch (Exception e) {
		// // addressTextView.setText("查询出错，请检查您输入的经纬度值！");
		// Toast.makeText(context, "查询出错，请检查您输入的经纬度值！", Toast.LENGTH_LONG)
		// .show();
		// }
		return view;
	}
}
