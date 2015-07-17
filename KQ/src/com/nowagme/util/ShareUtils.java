package com.nowagme.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import cn.kangeqiu.kq.R;
import cn.kangeqiu.kq.domain.Constants;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareUtils {
	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);
	private Activity context;

	public ShareUtils(Activity context) {
		// 配置需要分享的相关平台
		this.context = context;
		configPlatforms();
	}

	public void open() {
		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE);
		mController.openShare(context, false);
	}

	public void ssoResult(int requestCode, int resultCode, Intent data) {
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 配置分享平台参数</br>
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加QQ、QZone平台
		addQQQZonePlatform();

		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform() {
		String appId = Constants.QQAPP_ID;
		String appKey = Constants.QQAPP_KEY;
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, appId, appKey);
		qqSsoHandler.setTargetUrl(Constants.QQTARGET_URL);
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = Constants.WXAPP_ID;
		String appSecret = Constants.WXAPP_SECRET;
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(context, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(context, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	private int img = 0;
	private String imgUrl = "";

	public void setShareContent(String question, int img) {
		this.img = img;
		setShareContent(question, false);
	}

	public void setShareContent(String question, String imgUrl) {
		this.imgUrl = imgUrl;
		setShareContent(question, true);
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	public void setShareContent(String question, boolean isNeedLoad) {

		// 配置SSO
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSinaCallbackUrl("http://www.kangeqiu.cn/");

		mController.setShareContent(question + " http://www.kangeqiu.cn/");

		// APP ID：201874, API
		// * KEY：28401c0964f04a72a14c812d6132fcef, Secret
		// * Key：3bf66e42db1e4fa9829b955cc300b737.

		// UMImage localImage = new UMImage(context, R.drawable.logo);
		UMImage urlImage = new UMImage(context, imgUrl);
		UMImage image = new UMImage(context, BitmapFactory.decodeResource(
				context.getResources(), img));
		// UMImage resImage = new UMImage(getActivity(), R.drawable.icon);

		// 视频分享
		UMVideo video = new UMVideo(
				"http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		// vedio.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
		video.setTitle("友盟社会化组件视频");
		video.setThumb(urlImage);

		UMusic uMusic = new UMusic(
				"http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
		uMusic.setAuthor("umeng");
		uMusic.setTitle("天籁之音");
		// uMusic.setThumb(urlImage);
		uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

		// UMEmoji emoji = new UMEmoji(getActivity(),
		// "http://www.pc6.com/uploadimages/2010214917283624.gif");
		// UMEmoji emoji = new UMEmoji(getActivity(),
		// "/storage/sdcard0/emoji.gif");

		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(question + " http://www.kangeqiu.cn/");
		// weixinContent.setTitle("友盟社会化分享组件-微信");
		weixinContent.setTargetUrl("http://www.kangeqiu.cn/");
		if (isNeedLoad)
			weixinContent.setShareMedia(urlImage);
		else
			weixinContent.setShareMedia(image);

		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(question + " http://www.kangeqiu.cn/");
		circleMedia.setTitle(question);
		if (isNeedLoad)
			circleMedia.setShareMedia(urlImage);
		else
			circleMedia.setShareMedia(image);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl("http://www.kangeqiu.cn/");
		mController.setShareMedia(circleMedia);

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("share test");
		qzone.setTargetUrl("http://www.umeng.com");
		qzone.setTitle("QZone title");
		qzone.setShareMedia(image);
		// qzone.setShareMedia(uMusic);
		mController.setShareMedia(qzone);

		video.setThumb(new UMImage(context, BitmapFactory.decodeResource(
				context.getResources(), R.drawable.logo)));

		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QQ");
		qqShareContent.setTitle("hello, title");
		qqShareContent.setShareMedia(image);
		qqShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(qqShareContent);

		// 视频分享
		UMVideo umVideo = new UMVideo(
				"http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		umVideo.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
		umVideo.setTitle("友盟社会化组件视频");

	}
}
