/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.kangeqiu.kq.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.model.MemberInfoModel;
import cn.kangeqiu.kq.widget.ExpandGridView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.jingyi.MiChat.core.http.MCHttpCallBack;
import com.jingyi.MiChat.core.http.MCHttpResp;
import com.nowagame.kq.activity.AboutMatchActivity;
import com.nowagame.kq.activity.HourseMemberChooseActivity;
import com.nowagame.kq.activity.MyBragActivity;
import com.nowagame.kq.activity.MyGuessActivity;
import com.nowagme.football.AppConfig;
import com.nowagme.football.FragmentPersonActivity;
import com.nowagme.util.CPorgressDialog;
import com.nowagme.util.ImagerLoader;
import com.nowagme.util.ShareUtils;
import com.nowagme.util.UpdataUtil;
import com.nowagme.util.WebRequestUtil;
import com.squareup.picasso.Picasso;

public class GroupDetailsActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "GroupDetailsActivity";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
	private static final int REQUEST_CODE_ADD_TO_BALCKLIST = 4;
	private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
	private static final int REQUEST_CODE_LOOG_CHUINIU = 6;
	private static final int REQUEST_CODE_LOOG_GUESS = 7;
	private static final int REQUEST_CODE_CHOOSE_MATCH = 8;

	private static final int REQUEST_CODE_ADD = 9;

	String longClickUsername = null;

	private ExpandGridView userGridview;
	private String groupId;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private EMGroup group;
	private GridAdapter adapter;
	private int referenceWidth;
	private int referenceHeight;
	private ProgressDialog progressDialog;

	private RelativeLayout rl_switch_block_groupmsg;
	/**
	 * 屏蔽群消息imageView
	 */
	private ImageView iv_switch_block_groupmsg;
	/**
	 * 关闭屏蔽群消息imageview
	 */
	private ImageView iv_switch_unblock_groupmsg;

	public static GroupDetailsActivity instance;

	String st = "";
	// 清空所有聊天记录
	private RelativeLayout clearAllHistory;
	private RelativeLayout blacklistLayout;
	private RelativeLayout changeGroupNameLayout, rl_match_name;

	private RelativeLayout rl_chuiniu, rl_guess;

	private String roomId;
	private TextView hourse_name, invite_number, match_name, member_sum;
	private String newIds = "";
	private String removeFriendId = "";
	private ImagerLoader loader = new ImagerLoader();
	private String matchId = "";
	// private List<MemberInfoModel> memberArray = new
	// ArrayList<MemberInfoModel>();
	// private JSONArray records = new JSONArray();
	private String memberIds = "";
	private ShareUtils shareUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 获取传过来的groupid
		groupId = getIntent().getStringExtra("groupId");
		group = EMGroupManager.getInstance().getGroup(groupId);
		roomId = getIntent().getStringExtra("roomId");
		// we are not supposed to show the group if we don't find the group
		if (group == null) {
			finish();
			return;
		}

		setContentView(R.layout.activity_group_details);

		shareUtil = new ShareUtils(this);

		instance = this;
		st = getResources().getString(R.string.people);

		clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
		userGridview = (ExpandGridView) findViewById(R.id.gridview);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
		exitBtn = (Button) findViewById(R.id.btn_exit_grp);
		deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
		blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
		changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);

		rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);

		iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
		iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);
		hourse_name = (TextView) findViewById(R.id.hourse_name);
		invite_number = (TextView) findViewById(R.id.invite_number);
		match_name = (TextView) findViewById(R.id.match_name);
		rl_chuiniu = (RelativeLayout) findViewById(R.id.rl_chuiniu);
		rl_guess = (RelativeLayout) findViewById(R.id.rl_guess);
		rl_match_name = (RelativeLayout) findViewById(R.id.rl_match_name);
		member_sum = (TextView) findViewById(R.id.member_sum);

		rl_chuiniu.setOnClickListener(this);
		rl_guess.setOnClickListener(this);
		rl_switch_block_groupmsg.setOnClickListener(this);

		Drawable referenceDrawable = getResources().getDrawable(
				R.drawable.smiley_add_btn);
		referenceWidth = referenceDrawable.getIntrinsicWidth();
		referenceHeight = referenceDrawable.getIntrinsicHeight();

		if (group.getOwner() == null
				|| "".equals(group.getOwner())
				|| !group.getOwner().equals(
						EMChatManager.getInstance().getCurrentUser())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.GONE);
			blacklistLayout.setVisibility(View.GONE);
			// changeGroupNameLayout.setVisibility(View.GONE);
			changeGroupNameLayout.setClickable(false);
			changeGroupNameLayout.setFocusable(false);
			findViewById(R.id.hourse_name_right).setVisibility(View.INVISIBLE);
		}
		// 如果自己是群主，显示解散按钮
		if (EMChatManager.getInstance().getCurrentUser()
				.equals(group.getOwner())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.VISIBLE);
			findViewById(R.id.hourse_name_right).setVisibility(View.VISIBLE);
			findViewById(R.id.match_right).setVisibility(View.VISIBLE);
			changeGroupNameLayout.setOnClickListener(this);
			rl_match_name.setOnClickListener(this);
		}

		// ((TextView)
		// findViewById(R.id.group_name)).setText(group.getGroupName()
		// + "(" + group.getAffiliationsCount() + st);

		List<String> members = new ArrayList<String>();
		members.addAll(group.getMembers());

		adapter = new GridAdapter(this, R.layout.grid);
		userGridview.setAdapter(adapter);

		// 保证每次进详情看到的都是最新的group
		updateGroup();

		// 设置OnTouchListener
		userGridview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (adapter.isInDeleteMode) {
						adapter.isInDeleteMode = false;
						adapter.notifyDataSetChanged();
						return true;
					}
					break;
				default:
					break;
				}
				return false;
			}
		});

		clearAllHistory.setOnClickListener(this);
		blacklistLayout.setOnClickListener(this);

		initDate();
	}

	public void OnShare(View view) {
		shareUtil.open();
	}

	private void initDate() {
		adapter.clear();
		memberIds = "";
		doPullDate(false, "2050", new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						JSONObject room = resp.getJson().getJSONObject("room");
						hourse_name.setText(room.getString("name"));
						invite_number.setText(room.getString("invitation_code"));
						member_sum.setText("成员 ("
								+ room.getString("member_count") + "/"
								+ room.getString("max_member_count") + ")");
						JSONObject match = resp.getJson()
								.getJSONObject("match");

						match_name.setText(match.getJSONObject("team1")
								.getString("name")
								+ "VS"
								+ match.getJSONObject("team2")
										.getString("name"));

						JSONArray records = resp.getJson().getJSONArray(
								"records");
						// 设置分享的内容
						shareUtil.setShareContent(
								"我们在【" + room.getString("name") + "】"
										+ "吹牛、聊球，就差你了，一起来吧！", records
										.getJSONObject(0).getString("icon"));
						List<MemberInfoModel> memberArray = new ArrayList<MemberInfoModel>();

						for (int i = 0; i < records.length(); i++) {
							MemberInfoModel member = new MemberInfoModel();
							member.setIcon(records.getJSONObject(i).getString(
									"icon"));
							member.setName(records.getJSONObject(i).getString(
									"nickname"));
							member.setId(records.getJSONObject(i).getString(
									"id"));

							memberIds += ","
									+ records.getJSONObject(i).getString("id");
							memberArray.add(member);
						}
						adapter.setMembers(memberArray);
					} else {
						Toast.makeText(GroupDetailsActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				Toast.makeText(GroupDetailsActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void doPullDate(boolean isLazyLoad, String action,
			MCHttpCallBack listen) {

		ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();
		pair.add(new BasicNameValuePair("app_action", action));
		pair.add(new BasicNameValuePair("app_platform", "0"));
		pair.add(new BasicNameValuePair("app_version", UpdataUtil
				.getAppVersion(this)));
		pair.add(new BasicNameValuePair("u_uid", AppConfig.getInstance()
				.getPlayerId() + ""));
		pair.add(new BasicNameValuePair("u_room_id", roomId));
		if (action.equals("2036"))
			pair.add(new BasicNameValuePair("u_friend_uid", newIds));
		if (action.equals("2049"))
			pair.add(new BasicNameValuePair("u_friend_uid", removeFriendId));
		if (action.equals("2053"))
			pair.add(new BasicNameValuePair("u_match_id", matchId));
		new WebRequestUtil().execute(isLazyLoad, AppConfig.getInstance()
				.makeUrl(Integer.parseInt(action)), pair, listen);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		shareUtil.ssoResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.being_added);
		String st2 = getResources().getString(R.string.is_quit_the_group_chat);
		String st3 = getResources().getString(R.string.chatting_is_dissolution);
		String st4 = getResources().getString(R.string.are_empty_group_of_news);
		String st5 = getResources()
				.getString(R.string.is_modify_the_group_name);
		final String st6 = getResources().getString(
				R.string.Modify_the_group_name_successful);
		final String st7 = getResources().getString(
				R.string.change_the_group_name_failed_please);
		String st8 = getResources().getString(R.string.Are_moving_to_blacklist);
		final String st9 = getResources().getString(
				R.string.failed_to_move_into);

		final String stsuccess = getResources().getString(
				R.string.Move_into_blacklist_success);
		if (resultCode == RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(GroupDetailsActivity.this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// 添加群成员
				// final String[] newmembers = data
				// .getStringArrayExtra("newmembers");
				newIds = data.getStringExtra("id");
				progressDialog.setMessage(st1);
				progressDialog.show();
				addMembersToGroup();
				break;
			case REQUEST_CODE_EXIT: // 退出群
				progressDialog.setMessage(st2);
				progressDialog.show();
				exitGrop();
				break;
			case REQUEST_CODE_EXIT_DELETE: // 解散群
				progressDialog.setMessage(st3);
				progressDialog.show();
				deleteGrop();
				break;
			case REQUEST_CODE_CLEAR_ALL_HISTORY:
				// 清空此群聊的聊天记录
				progressDialog.setMessage(st4);
				progressDialog.show();
				clearGroupHistory();
				break;

			case REQUEST_CODE_EDIT_GROUPNAME: // 修改群名称
				final String returnData = data.getStringExtra("data");
				hourse_name.setText(returnData);
				Toast.makeText(getApplicationContext(), st6, 0).show();
				// ((TextView) findViewById(R.id.group_name)).setText(returnData
				// + "("
				// + group.getAffiliationsCount()
				// + st);

				// if (!TextUtils.isEmpty(returnData)) {
				// progressDialog.setMessage(st5);
				// progressDialog.show();
				//
				// new Thread(new Runnable() {
				// public void run() {
				// try {
				// EMGroupManager.getInstance().changeGroupName(
				// groupId, returnData);
				// runOnUiThread(new Runnable() {
				// public void run() {
				// ((TextView) findViewById(R.id.group_name)).setText(returnData
				// + "("
				// + group.getAffiliationsCount()
				// + st);
				// progressDialog.dismiss();
				// Toast.makeText(getApplicationContext(),
				// st6, 0).show();
				// }
				// });
				//
				// } catch (EaseMobException e) {
				// e.printStackTrace();
				// runOnUiThread(new Runnable() {
				// public void run() {
				// progressDialog.dismiss();
				// Toast.makeText(getApplicationContext(),
				// st7, 0).show();
				// }
				// });
				// }
				// }
				// }).start();
				// }
				break;
			case REQUEST_CODE_ADD_TO_BALCKLIST:
				progressDialog.setMessage(st8);
				progressDialog.show();
				new Thread(new Runnable() {
					public void run() {
						try {
							EMGroupManager.getInstance().blockUser(groupId,
									longClickUsername);
							runOnUiThread(new Runnable() {
								public void run() {
									refreshMembers();
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(),
											stsuccess, 0).show();
								}
							});
						} catch (EaseMobException e) {
							runOnUiThread(new Runnable() {
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(),
											st9, 0).show();
								}
							});
						}
					}
				}).start();

				break;
			default:
				break;
			}
		} else if (resultCode == 30) {
			try {
				final JSONObject jsonMatch = new JSONObject(
						data.getStringExtra("match"));
				matchId = jsonMatch.getString("id");
				CPorgressDialog.showProgressDialog(GroupDetailsActivity.this);
				doPullDate(false, "2053", new MCHttpCallBack() {
					@Override
					public void onSuccess(MCHttpResp resp) {
						super.onSuccess(resp);
						CPorgressDialog.hideProgressDialog();
						try {
							String resultCode = resp.getJson().getString(
									"result_code");
							if (resultCode.equals("0")) {
								match_name.setText(jsonMatch.getJSONObject(
										"team1").getString("name")
										+ "VS"
										+ jsonMatch.getJSONObject("team2")
												.getString("name"));
							} else {
								Toast.makeText(GroupDetailsActivity.this,
										resp.getJson().getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onError(MCHttpResp resp) {
						super.onError(resp);
						CPorgressDialog.hideProgressDialog();
					}
				});
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	private void refreshMembers() {
		adapter.clear();

		List<String> members = new ArrayList<String>();
		members.addAll(group.getMembers());
		adapter.addAll(members);

		adapter.notifyDataSetChanged();
	}

	/**
	 * 点击退出群组按钮
	 * 
	 * @param view
	 */
	public void exitGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class),
				REQUEST_CODE_EXIT);

	}

	/**
	 * 点击解散群组按钮
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
		startActivityForResult(
				new Intent(this, ExitGroupDialog.class).putExtra("deleteToast",
						getString(R.string.dissolution_group_hint)),
				REQUEST_CODE_EXIT_DELETE);

	}

	/**
	 * 清空群聊天记录
	 */
	public void clearGroupHistory() {

		EMChatManager.getInstance().clearConversation(group.getGroupId());
		progressDialog.dismiss();
		// adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));

	}

	/**
	 * 退出群组
	 * 
	 * @param groupId
	 */
	private void exitGrop() {
		doPullDate(false, "2051", new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				progressDialog.dismiss();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						setResult(RESULT_OK);
						finish();
						if (ChatActivity.activityInstance != null)
							ChatActivity.activityInstance.finish();
					} else {
						Toast.makeText(GroupDetailsActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.Exit_the_group_chat_failure)
									+ " " + e.getMessage(), 1).show();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				progressDialog.dismiss();
				Toast.makeText(GroupDetailsActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
		// new Thread(new Runnable() {
		// public void run() {
		// try {
		// EMGroupManager.getInstance().exitFromGroup(groupId);
		// runOnUiThread(new Runnable() {
		// public void run() {
		// progressDialog.dismiss();
		// setResult(RESULT_OK);
		// finish();
		// if (ChatActivity.activityInstance != null)
		// ChatActivity.activityInstance.finish();
		// }
		// });
		// } catch (final Exception e) {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// progressDialog.dismiss();
		// Toast.makeText(
		// getApplicationContext(),
		// getResources()
		// .getString(
		// R.string.Exit_the_group_chat_failure)
		// + " " + e.getMessage(), 1).show();
		// }
		// });
		// }
		// }
		// }).start();
	}

	/**
	 * 解散群组
	 * 
	 * @param groupId
	 */
	private void deleteGrop() {
		final String st5 = getResources().getString(
				R.string.Dissolve_group_chat_tofail);

		doPullDate(false, "2038", new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				progressDialog.dismiss();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						setResult(RESULT_OK);
						finish();
						if (ChatActivity.activityInstance != null)
							ChatActivity.activityInstance.finish();
					} else {
						Toast.makeText(GroupDetailsActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							st5 + e.getMessage(), 1).show();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				progressDialog.dismiss();
				Toast.makeText(GroupDetailsActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});

		// new Thread(new Runnable() {
		// public void run() {
		// try {
		// EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
		// runOnUiThread(new Runnable() {
		// public void run() {
		// progressDialog.dismiss();
		// setResult(RESULT_OK);
		// finish();
		// if (ChatActivity.activityInstance != null)
		// ChatActivity.activityInstance.finish();
		// }
		// });
		// } catch (final Exception e) {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// progressDialog.dismiss();
		// Toast.makeText(getApplicationContext(),
		// st5 + e.getMessage(), 1).show();
		// }
		// });
		// }
		// }
		// }).start();
	}

	/**
	 * 增加群成员
	 * 
	 * @param newmembers
	 */
	private void addMembersToGroup() {
		final String st6 = getResources().getString(
				R.string.Add_group_members_fail);
		doPullDate(false, "2036", new MCHttpCallBack() {

			@Override
			public void onSuccess(MCHttpResp resp) {
				super.onSuccess(resp);
				progressDialog.dismiss();
				try {
					String resultCode = resp.getJson().getString("result_code");
					if (resultCode.equals("0")) {
						Toast.makeText(GroupDetailsActivity.this, "增加成功",
								Toast.LENGTH_SHORT).show();
						// refreshMembers();
						// refreshMembers();
						initDate();
					} else {
						Toast.makeText(GroupDetailsActivity.this,
								resp.getJson().getString("message"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(MCHttpResp resp) {
				super.onError(resp);
				progressDialog.dismiss();
				Toast.makeText(GroupDetailsActivity.this,
						resp.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
		});
		// new Thread(new Runnable() {
		//
		// public void run() {
		// try {
		// // 创建者调用add方法
		// if (EMChatManager.getInstance().getCurrentUser()
		// .equals(group.getOwner())) {
		//
		// // EMGroupManager.getInstance().addUsersToGroup(groupId,
		// // newmembers);
		// } else {
		// // 一般成员调用invite方法
		// EMGroupManager.getInstance().inviteUser(groupId,
		// newmembers, null);
		// }
		// runOnUiThread(new Runnable() {
		// public void run() {
		// refreshMembers();
		// ((TextView) findViewById(R.id.group_name))
		// .setText(group.getGroupName() + "("
		// + group.getAffiliationsCount() + st);
		// progressDialog.dismiss();
		// }
		// });
		// } catch (final Exception e) {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// progressDialog.dismiss();
		// Toast.makeText(getApplicationContext(),
		// st6 + e.getMessage(), 1).show();
		// }
		// });
		// }
		// }
		// }).start();
	}

	@Override
	public void onClick(View v) {
		String st6 = getResources().getString(R.string.Is_unblock);
		final String st7 = getResources().getString(R.string.remove_group_of);
		switch (v.getId()) {
		case R.id.rl_switch_block_groupmsg: // 屏蔽群组
			if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
				EMLog.d(TAG, "change to unblock group msg");
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(
							GroupDetailsActivity.this);
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.setMessage(st6);
				progressDialog.show();
				new Thread(new Runnable() {
					public void run() {
						try {
							EMGroupManager.getInstance().unblockGroupMessage(
									groupId);
							runOnUiThread(new Runnable() {
								public void run() {
									iv_switch_block_groupmsg
											.setVisibility(View.INVISIBLE);
									iv_switch_unblock_groupmsg
											.setVisibility(View.VISIBLE);
									progressDialog.dismiss();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(),
											st7, 1).show();
								}
							});

						}
					}
				}).start();

			} else {
				String st8 = getResources()
						.getString(R.string.group_is_blocked);
				final String st9 = getResources().getString(
						R.string.group_of_shielding);
				EMLog.d(TAG, "change to block group msg");
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(
							GroupDetailsActivity.this);
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.setMessage(st8);
				progressDialog.show();
				new Thread(new Runnable() {
					public void run() {
						try {
							EMGroupManager.getInstance().blockGroupMessage(
									groupId);
							runOnUiThread(new Runnable() {
								public void run() {
									iv_switch_block_groupmsg
											.setVisibility(View.VISIBLE);
									iv_switch_unblock_groupmsg
											.setVisibility(View.INVISIBLE);
									progressDialog.dismiss();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(),
											st9, 1).show();
								}
							});
						}

					}
				}).start();
			}
			break;

		case R.id.clear_all_history: // 清空聊天记录
			String st9 = getResources().getString(R.string.sure_to_empty_this);
			Intent intent = new Intent(GroupDetailsActivity.this,
					AlertDialog.class);
			intent.putExtra("cancel", true);
			intent.putExtra("titleIsCancel", true);
			intent.putExtra("msg", st9);
			startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
			break;

		case R.id.rl_blacklist: // 黑名单列表
			startActivity(new Intent(GroupDetailsActivity.this,
					GroupBlacklistActivity.class).putExtra("groupId", groupId));
			break;

		case R.id.rl_change_group_name:
			startActivityForResult(
					new Intent(this, EditActivity.class).putExtra("data",
							group.getGroupName()).putExtra("roomId", roomId),
					REQUEST_CODE_EDIT_GROUPNAME);
			break;

		case R.id.rl_chuiniu:
			startActivityForResult(
					new Intent(this, MyBragActivity.class).putExtra("roomId",
							roomId), REQUEST_CODE_LOOG_CHUINIU);
			break;

		case R.id.rl_guess:
			startActivityForResult(
					new Intent(this, MyGuessActivity.class).putExtra("roomId",
							roomId), REQUEST_CODE_LOOG_GUESS);
			break;
		case R.id.rl_match_name:
			startActivityForResult(new Intent(this, AboutMatchActivity.class),
					REQUEST_CODE_CHOOSE_MATCH);
			break;
		default:
			break;
		}

	}

	/**
	 * 群组成员gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;
		public boolean isInDeleteMode;
		// private List<String> objects;
		private List<MemberInfoModel> member = new ArrayList<MemberInfoModel>();

		public GridAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			// this.objects = objects;
			res = textViewResourceId;
			isInDeleteMode = false;
		}

		public void setMembers(List<MemberInfoModel> member) {
			this.member = member;
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(res,
						null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.iv_avatar);
				holder.textView = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.badgeDeleteView = (ImageView) convertView
						.findViewById(R.id.badge_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final LinearLayout button = (LinearLayout) convertView
					.findViewById(R.id.button_avatar);
			// 最后一个item，减人按钮
			if (position == getCount() - 1) {
				holder.textView.setText("");
				// 设置成删除按钮
				holder.imageView.setImageResource(R.drawable.smiley_minus_btn);
				// button.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.smiley_minus_btn, 0, 0);
				// 如果不是创建者或者没有相应权限，不提供加减人按钮
				if (!group.getOwner().equals(
						EMChatManager.getInstance().getCurrentUser())) {
					// if current user is not group admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else { // 显示删除按钮
					if (isInDeleteMode) {
						// 正处于删除模式下，隐藏删除按钮
						convertView.setVisibility(View.INVISIBLE);
					} else {
						// 正常模式
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete)
								.setVisibility(View.INVISIBLE);
					}
					final String st10 = getResources().getString(
							R.string.The_delete_button_is_clicked);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st10);
							isInDeleteMode = true;
							notifyDataSetChanged();
						}
					});
				}
			} else if (position == getCount() - 2) { // 添加群组成员按钮
				holder.textView.setText("");
				holder.imageView.setImageResource(R.drawable.smiley_add_btn);
				// button.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.smiley_add_btn, 0, 0);
				// 如果不是创建者或者没有相应权限
				// if (!group.isAllowInvites()
				// && !group.getOwner().equals(
				// EMChatManager.getInstance().getCurrentUser())) {
				// // if current user is not group admin, hide add/remove btn
				// convertView.setVisibility(View.INVISIBLE);
				// } else {
				// 正处于删除模式下,隐藏添加按钮
				if (isInDeleteMode) {
					convertView.setVisibility(View.INVISIBLE);
				} else {
					convertView.setVisibility(View.VISIBLE);
					convertView.findViewById(R.id.badge_delete).setVisibility(
							View.INVISIBLE);
				}
				final String st11 = getResources().getString(
						R.string.Add_a_button_was_clicked);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						EMLog.d(TAG, st11);
						// 进入选人页面
						// Intent intent = new Intent();
						// intent.setClass(GroupDetailsActivity.this,
						// HourseMemberChooseActivity.class);
						// GroupDetailsActivity.this.startActivityForResult(
						// intent, 0);

						startActivityForResult((new Intent(
								GroupDetailsActivity.this,
								HourseMemberChooseActivity.class).putExtra(
								"roomId", roomId).putExtra("memberId",
								memberIds.substring(1))), REQUEST_CODE_ADD_USER);

					}
				});
				// }
			} else { // 普通item，显示群组成员
				// final String username = getItem(position);
				final MemberInfoModel memberInfo = member.get(position);
				convertView.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
				// Drawable avatar =
				// getResources().getDrawable(R.drawable.default_avatar);
				// avatar.setBounds(0, 0, referenceWidth, referenceHeight);
				// button.setCompoundDrawables(null, avatar, null, null);
				holder.textView.setText(memberInfo.getName());
				// UserUtils.setUserAvatar(getContext(), username,
				// holder.imageView);

				if (member.size() == getCount() - 2) {
					holder.imageView.setTag(R.id.iconUrl, member.get(position)
							.getIcon());
					// new DownAndShowImageTask(member.get(position).getIcon(),
					// holder.imageView, true).execute();

					loader.LoadImage(member.get(position).getIcon(),
							holder.imageView);
					holder.imageView.setTag(member.get(position).getId());
					holder.textView.setText(member.get(position).getName());
					holder.imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View view) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.putExtra("userId", view.getTag().toString());
							intent.setClass(GroupDetailsActivity.this,
									FragmentPersonActivity.class);
							GroupDetailsActivity.this.startActivity(intent);
						}
					});
				} else {
					Picasso.with(GroupDetailsActivity.this)
							.load(R.drawable.default_avatar)
							.into(holder.imageView);
				}
				// demo群组成员的头像都用默认头像，需由开发者自己去设置头像
				if (isInDeleteMode) {
					// 如果是删除模式下，显示减人图标
					convertView.findViewById(R.id.badge_delete).setVisibility(
							View.VISIBLE);
				} else {
					convertView.findViewById(R.id.badge_delete).setVisibility(
							View.INVISIBLE);
				}
				final String st12 = getResources().getString(
						R.string.not_delete_myself);
				final String st13 = getResources().getString(
						R.string.Are_removed);
				final String st14 = getResources().getString(
						R.string.Delete_failed);
				final String st15 = getResources().getString(
						R.string.confirm_the_members);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isInDeleteMode) {
							// 如果是删除自己，return
							if (EMChatManager.getInstance().getCurrentUser()
									.equals(memberInfo.getId())) {
								startActivity(new Intent(
										GroupDetailsActivity.this,
										AlertDialog.class)
										.putExtra("msg", st12));
								return;
							}
							if (!NetUtils.hasNetwork(getApplicationContext())) {
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.network_unavailable),
										0).show();
								return;
							}

							removeFriendId = member.get(position).getId();
							deleteMembersFromGroup();
						} else {
							// 正常情况下点击user，可以进入用户详情或者聊天页面等等
							// startActivity(new
							// Intent(GroupDetailsActivity.this,
							// ChatActivity.class).putExtra("userId",
							// user.getUsername()));

						}
					}

					/**
					 * 删除群成员
					 * 
					 * @param username
					 */
					protected void deleteMembersFromGroup() {
						final ProgressDialog deleteDialog = new ProgressDialog(
								GroupDetailsActivity.this);
						deleteDialog.setMessage(st13);
						deleteDialog.setCanceledOnTouchOutside(false);
						deleteDialog.show();
						doPullDate(false, "2049", new MCHttpCallBack() {

							@Override
							public void onSuccess(MCHttpResp resp) {
								super.onSuccess(resp);
								isInDeleteMode = false;
								try {
									String resultCode = resp.getJson()
											.getString("result_code");
									if (resultCode.equals("0")) {
										deleteDialog.dismiss();
										// refreshMembers();
										initDate();
										Toast.makeText(
												GroupDetailsActivity.this,
												"删除成功", Toast.LENGTH_SHORT)
												.show();
										// refreshMembers();
										// ((TextView)
										// findViewById(R.id.group_name)).setText(group
										// .getGroupName()
										// + "("
										// + group.getAffiliationsCount()
										// + st);
									} else {
										Toast.makeText(
												GroupDetailsActivity.this,
												resp.getJson().getString(
														"message"),
												Toast.LENGTH_SHORT).show();
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onError(MCHttpResp resp) {
								super.onError(resp);
								deleteDialog.dismiss();
								Toast.makeText(GroupDetailsActivity.this,
										resp.getErrorMessage(),
										Toast.LENGTH_SHORT).show();
							}
						});
						// new Thread(new Runnable() {
						//
						// @Override
						// public void run() {
						// try {
						// // 删除被选中的成员
						// EMGroupManager.getInstance()
						// .removeUserFromGroup(groupId,
						// username);
						// isInDeleteMode = false;
						// runOnUiThread(new Runnable() {
						//
						// @Override
						// public void run() {
						// deleteDialog.dismiss();
						// refreshMembers();
						// ((TextView)
						// findViewById(R.id.group_name)).setText(group
						// .getGroupName()
						// + "("
						// + group.getAffiliationsCount()
						// + st);
						// }
						// });
						// } catch (final Exception e) {
						// deleteDialog.dismiss();
						// runOnUiThread(new Runnable() {
						// public void run() {
						// Toast.makeText(
						// getApplicationContext(),
						// st14 + e.getMessage(), 1)
						// .show();
						// }
						// });
						// }
						//
						// }
						// }).start();
					}
				});

				button.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						if (EMChatManager.getInstance().getCurrentUser()
								.equals(memberInfo.getId()))
							return true;
						if (group.getOwner().equals(
								EMChatManager.getInstance().getCurrentUser())) {
							Intent intent = new Intent(
									GroupDetailsActivity.this,
									AlertDialog.class);
							intent.putExtra("msg", st15);
							intent.putExtra("cancel", true);
							startActivityForResult(intent,
									REQUEST_CODE_ADD_TO_BALCKLIST);
							longClickUsername = memberInfo.getId();
						}
						return false;
					}
				});
			}
			return convertView;
		}

		@Override
		public int getCount() {
			return member.size() + 2;
		}
	}

	protected void updateGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
					final EMGroup returnGroup = EMGroupManager.getInstance()
							.getGroupFromServer(groupId);
					// 更新本地数据
					EMGroupManager.getInstance().createOrUpdateLocalGroup(
							returnGroup);

					runOnUiThread(new Runnable() {
						public void run() {
							// ((TextView) findViewById(R.id.group_name))
							// .setText(group.getGroupName() + "("
							// + group.getAffiliationsCount()
							// + ")");
							loadingPB.setVisibility(View.INVISIBLE);
							refreshMembers();
							if (EMChatManager.getInstance().getCurrentUser()
									.equals(group.getOwner())) {
								// 显示解散按钮
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.VISIBLE);
							} else {
								// 显示退出按钮
								exitBtn.setVisibility(View.VISIBLE);
								deleteBtn.setVisibility(View.GONE);
							}

							// update block
							EMLog.d(TAG,
									"group msg is blocked:"
											+ group.getMsgBlocked());
							if (group.isMsgBlocked()) {
								iv_switch_block_groupmsg
										.setVisibility(View.VISIBLE);
								iv_switch_unblock_groupmsg
										.setVisibility(View.INVISIBLE);
							} else {
								iv_switch_block_groupmsg
										.setVisibility(View.INVISIBLE);
								iv_switch_unblock_groupmsg
										.setVisibility(View.VISIBLE);
							}
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}

	public void back(View view) {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}

	private static class ViewHolder {
		ImageView imageView;
		TextView textView;
		ImageView badgeDeleteView;
	}

}
