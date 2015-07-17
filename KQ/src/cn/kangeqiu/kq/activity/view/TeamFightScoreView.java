package cn.kangeqiu.kq.activity.view;

import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kangeqiu.kq.R;

import com.nowagme.football.DownAndShowImageTask;

public class TeamFightScoreView {
	private Activity context;
	private LayoutInflater inflater;

	private ImageView icon;
	private TextView name;
	private EditText goleEdit;
	private EditText assitEdit;

	private String mpId;
	private String nickname;

	public TeamFightScoreView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public String chargeMsg() {
		if (goleEdit.getText().toString() == null
				|| goleEdit.getText().toString().equals(""))
			return "请填写" + nickname + "的进球";
		if (assitEdit.getText().toString() == null
				|| assitEdit.getText().toString().equals(""))
			return "请填写" + nickname + "的助攻";

		return null;
	}

	public String getMsg() {
		int goles = Integer.parseInt(goleEdit.getText().toString());
		int assits = Integer.parseInt(assitEdit.getText().toString());
		if (goles >= 0 || assits >= 0) {
			return mpId + "," + goles + "," + assits + ";";
		}
		return null;
	}

	public View getView(Map<String, String> msg) {
		mpId = String.valueOf(msg.get("mp_id"));
		nickname = String.valueOf(msg.get("nickname"));

		View view = inflater.inflate(R.layout.abc_fight_score_message_item,
				null);

		icon = (ImageView) view.findViewById(R.id.player_icon);
		name = (TextView) view.findViewById(R.id.name);
		goleEdit = (EditText) view.findViewById(R.id.gole_edit);
		assitEdit = (EditText) view.findViewById(R.id.assit_edit);
		name.setText(String.valueOf(msg.get("nickname")));
		new DownAndShowImageTask(String.valueOf(msg.get("faceimg")), icon)
				.execute();
		goleEdit.setText(String.valueOf(msg.get("count_jq")));
		assitEdit.setText(String.valueOf(msg.get("count_zg")));
		return view;
	}
}
