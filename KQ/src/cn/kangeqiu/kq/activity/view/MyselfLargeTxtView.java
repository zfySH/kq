package cn.kangeqiu.kq.activity.view;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.activity.MyselfMessageDetailActivity;

public class MyselfLargeTxtView {
	private Activity context;
	private LayoutInflater inflater;
	private EditText mEditText = null;
	private TextView mTextView = null;

	private static final int MAX_COUNT = 30;

	public MyselfLargeTxtView(Activity context) {
		this.context = context;
		inflater = context.getLayoutInflater();
	}

	public View getView(String msg) {
		View view = inflater.inflate(
				R.layout.abc_myself_message_large_txt_item, null);
		mEditText = (EditText) view.findViewById(R.id.txt);
		mTextView = (TextView) view.findViewById(R.id.number);

		mEditText.setText(msg);

		mEditText.addTextChangedListener(mTextWatcher);
		mEditText.setSelection(mEditText.length()); // 将光标移动最后一个字符后面
		return view;
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		private int editStart;
		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = mEditText.getSelectionStart();
			editEnd = mEditText.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			mEditText.removeTextChangedListener(mTextWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			// mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
			mEditText.setSelection(editStart);

			// 恢复监听器
			mEditText.addTextChangedListener(mTextWatcher);

			setLeftCount();
			
			((MyselfMessageDetailActivity) context).commitContent(mEditText
					.getText().toString());
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};
	
	
	/** 
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1 
     *  
     * @param c 
     * @return 
     */  
    private long calculateLength(CharSequence c) {  
        double len = 0;  
        for (int i = 0; i < c.length(); i++) {  
            int tmp = (int) c.charAt(i);  
            if (tmp > 0 && tmp < 127) {  
                len += 0.5;  
            } else {  
                len++;  
            }  
        }  
        return Math.round(len);  
    }  
  
    /** 
     * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字 
     */  
    private void setLeftCount() {  
        mTextView.setText(String.valueOf((MAX_COUNT - getInputCount())));  
    }  
  
	/**
	 * 获取用户输入的分享内容字数
	 * 
	 * @return
	 */
	private long getInputCount() {
		return calculateLength(mEditText.getText().toString());
	}
}
