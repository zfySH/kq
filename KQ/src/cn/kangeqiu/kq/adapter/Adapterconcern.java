package cn.kangeqiu.kq.adapter;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.FriendsActivity;

import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagme.football.AppConfig;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.WebRequestUtil;

public class Adapterconcern extends BaseAdapter {
	private LayoutInflater inflater;
	private JSONArray records = new JSONArray();
	private OneHolder oneHolder;
	String state = "", id = "", userId = "";
	/**
	 * 上下文对象
	 */
	private Activity context = null;
	ImagerLoader loader = new ImagerLoader();

	public Adapterconcern(Activity context, String userId) {
		this.context = context;
		this.userId = userId;
		inflater = context.getLayoutInflater();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		oneHolder = new OneHolder();

		if (view == null) {
			view = inflater.inflate(R.layout.concern_item, null);
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
			if (userId.equals(AppConfig.getInstance().getPlayerId() + "")) {
				if (state.equals("0")) {
					// oneHolder.txt_reply.setVisibility(View.GONE);
					oneHolder.txt_attention
							.setBackgroundResource(R.drawable.img_yiattention);
					// oneHolder.txt_attention
					// .setImageResource(R.drawable.img_yiattention);

				} else {
					oneHolder.txt_attention
							.setBackgroundResource(R.drawable.img_attention);
				}
				oneHolder.txt_attention
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								doPullDate(position, "2017",
										new MCHttpCallBack() {
											@SuppressLint("NewApi")
											@Override
											public void onSuccess(
													MCHttpResp resp) {
												super.onSuccess(resp);
												try {
													String resultCode = resp
															.getJson()
															.getString(
																	"result_code");
													if ("0".equals(resultCode)) {
														try {

															Toast.makeText(
																	context,
																	"取消关注成功",
																	Toast.LENGTH_SHORT)
																	.show();
															// records.remove(position);
															// notifyDataSetChanged();
															((FriendsActivity) context)
																	.initData();
															context.setResult(context.RESULT_OK);
														} catch (Exception e) {
															e.printStackTrace();
														}
													} else {
														Toast.makeText(
																context,
																"取消关注失败",
																Toast.LENGTH_SHORT)
																.show();
													}
												} catch (Exception e1) {
													// TODO Auto-generated catch
													// block
													e1.printStackTrace();
												}

											}

											@Override
											public void onError(MCHttpResp resp) {
												super.onError(resp);
												Toast.makeText(context,
														resp.getErrorMessage(),
														Toast.LENGTH_SHORT)
														.show();
											}
										});

							}
						});
			} else {
				oneHolder.txt_attention.setVisibility(View.GONE);
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
