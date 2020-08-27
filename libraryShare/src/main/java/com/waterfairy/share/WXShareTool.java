package com.waterfairy.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.waterfairy.share.BitmapUtils.bitmap2Bytes;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/18 17:10
 * @info:
 */
public class WXShareTool {
    private boolean hasRegisterWX;

    public static WXShareTool newInstance() {
        return new WXShareTool();
    }

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    public static String APP_ID = "wx1ce3476299ca5c8d";

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI iWXApi;

    public void register(Context context) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        iWXApi = WXAPIFactory.createWXAPI(context, APP_ID, true);

        // 将应用的appId注册到微信
        hasRegisterWX = iWXApi.registerApp(APP_ID);
    }

    public static WXMediaMessage.IMediaObject geneWXWebPageObject(String desc, String webPageUrl) {
        if (!TextUtils.isEmpty(webPageUrl)) {
            WXWebpageObject webPage = new WXWebpageObject();
            webPage.webpageUrl = webPageUrl;
            return webPage;
        } else if (!TextUtils.isEmpty(desc)) {
            WXTextObject wxTextObject = new WXTextObject();
            wxTextObject.text = desc;
            return wxTextObject;
        }
        return null;
    }

    public static WXMediaMessage geneWXMediaMessage(WXMediaMessage.IMediaObject iMediaObject) {
        WXMediaMessage wxMediaMessage;
        if (iMediaObject == null) wxMediaMessage = new WXMediaMessage();
        else wxMediaMessage = new WXMediaMessage(iMediaObject);
        return wxMediaMessage;
    }

    public static byte[] geneThumbDataFromRes(Context context, int imageDrawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageDrawable);
        return bitmap2Bytes(bitmap, 20);
    }

    /**
     * 分享链接
     *
     * @param title
     * @param desc
     * @param url
     * @param thumbData
     * @return
     */
    public static WXMediaMessage geneWxMediaMessage(String title, String desc, String url, byte[] thumbData) {
        WXMediaMessage wxMediaMessage;
        wxMediaMessage = geneWXMediaMessage(geneWXWebPageObject(desc, url));
        wxMediaMessage.description = desc;
        wxMediaMessage.title = title;
        if (thumbData != null)
            wxMediaMessage.thumbData = thumbData;
        return wxMediaMessage;
    }

    /**
     * 发送
     *
     * @param isFriendCircle
     * @param wxMediaMessage
     * @return
     */
    public boolean send(boolean isFriendCircle, WXMediaMessage wxMediaMessage) {
        if (!hasRegisterWX) return false;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = wxMediaMessage;
        if (isFriendCircle) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return iWXApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
