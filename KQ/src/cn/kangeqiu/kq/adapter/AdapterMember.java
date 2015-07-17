package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.nowagme.football.AppConfig;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;

public class AdapterMember extends BaseAdapter {
	private LayoutInflater inflater;
	private JSONArray records = new JSONArray();
	private OneHolder oneHolder;
	String state = "", id = "";
	/**
	 * 上下文对象
	 */
	private Activity context = null;
	ImagerLoader loader = new ImagerLoader();

	public AdapterMember(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();

	}

	public void choose(int position) {
		try {
			if (records.getJSONObject(position).getBoolean("isCheck")) {
				records.getJSONObject(position).put("isCheck", false);

			} else {
				records.getJSONObject(position).put("isCheck", true);
			}
			// if (records.getJSONObject(position).getBoolean("isMember")) {
			// records.getJSONObject(position).put("isMember", false);
			//
			// } else {
			// records.getJSONObject(position).put("isMember", true);
			// }
			notifyDataSetChanged();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setItem(JSONArray records) {
		if (records == null)
			return;
		this.records = records;

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return records == null ? 0 : records.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return records.getJSONObject(position);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		oneHolder = new OneHolder();

		if (view == null) {
			view = inflater.inflate(R.layout.friend_item, null);
			oneHolder.name = (TextView) view.findViewById(R.id.txt_name);
			oneHolder.logo = (ImageView) view
					.findViewById(R.id.abc_fragment_nearby__listview__iv_faceimg);
			// oneHolder.txt_reply = (ImageButton) view
			// .findViewById(R.id.txt_reply);
			oneHolder.txt_attention = (ImageButton) view
					.findViewById(R.id.txt_attention);

			view.setTag(oneHolder);

		} else {
			oneHolder = (OneHolder) view.getTag();
		}
		try {
			id = records.getJSONObject(position).getString("id");
			// new
			// DownAndShowImageTask(records.getJSONObject(position).getString(
			// "icon"), oneHolder.logo).execute();
			loader.LoadImage(records.getJSONObject(position).getString("icon"),
					oneHolder.logo);

			oneHolder.name.setText(records.getJSONObject(position).getString(
					"nickname"));

			state = records.getJSONObject(position).getString("state");

			if (records.getJSONObject(position).getBoolean("isMember")) {
				oneHolder.txt_attention.setVisibility(View.VISIBLE);
				oneHolder.txt_attention
						.setBackgroundResource(R.drawable.cannot_choose);
			} else {
				// oneHolder.txt_attention.setVisibility(View.INVISIBLE);
				if (records.getJSONObject(position).getBoolean("isCheck")) {
					oneHolder.txt_attention.setVisibility(View.VISIBLE);
					oneHolder.txt_attention
							.setBackgroundResource(R.drawable.can_choose);
				} else {
					oneHolder.txt_attention.setVisibility(View.INVISIBLE);
				}
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return view;
	}

	private void doPullDate(int position, String action, MCHttpCallBack listen) {

		try {
			ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
			pair.add(new BasicNameValuePair("app_action", action));
			pair.add(new BasicNameValuePair("app_platform", "0"));
			pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
					.getPlayerId() + ""));
			pair.add(new BasicNameValuePair("u_user_id", records.getJSONObject(
					position).getString("id")));
			new WebRequestUtil().execute(false, AppConfig.getInstance()
					.makeUrl(Integer.parseInt("2025")), pair, listen);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class OneHolder {
		private TextView name;
		private ImageView logo;
		private ImageButton txt_attention;

	}

}
