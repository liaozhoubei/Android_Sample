package com.example.example.retrofit;

import java.util.List;

public class GankBean {

    /**
     * error : false
     * results : [{"_id":"5d19f32b9d2122774f0cd8d2","createdAt":"2019-07-01T11:48:59.726Z","desc":"2019 最前沿的几个 Flutter 实践：微信、咸鱼、美团 ","publishedAt":"2019-07-01T11:49:08.311Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s/TyjwBASNvxnQNXtC3zCG1w","used":true,"who":"潇湘剑雨"},{"_id":"5d19f2d19d21220321462148","createdAt":"2019-07-01T11:47:29.85Z","desc":"Dart基础系统性的学习笔记总结","publishedAt":"2019-07-01T11:47:47.35Z","source":"web","type":"Android","url":"https://juejin.im/post/5d19dbf06fb9a07f014f0e46","used":true,"who":"潇湘剑雨"},{"_id":"5d1035ca9d2122031b7980b2","createdAt":"2019-06-24T02:30:34.222Z","desc":"Android 录屏 && 音轨剪辑（剔除环境声音），抗住百万级日活APP挑战，附带详细 Blog 实现思路，","publishedAt":"2019-06-27T02:38:12.271Z","source":"web","type":"Android","url":"https://github.com/nanchen2251/ScreenRecordHelper","used":true,"who":"潇湘剑雨"},{"_id":"5d11ba249d2122031b7980b5","createdAt":"2019-06-25T06:07:32.359Z","desc":"每日一问：从不一样的角度吐槽一下 DataBinding","publishedAt":"2019-06-26T02:49:35.15Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s/GgDLJTU8x0txkSgGEtp6VA","used":true,"who":"潇湘剑雨"},{"_id":"5d12271b9d2122031ea5221a","createdAt":"2019-06-25T13:52:27.478Z","desc":"万级日活 App 的屏幕录制功能是如何实现的","publishedAt":"2019-06-26T02:49:28.163Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s/3vs2vV8txaCctdO8RDqqSA","used":true,"who":"潇湘剑雨"},{"_id":"5d0cac019d2122031ea52212","createdAt":"2019-06-21T10:05:53.14Z","desc":"flutter吐司库，自定义设置吐司标题，内容，背景颜色，文字颜色，字体大小，背景圆角，吐司时间等等","images":["http://img.gank.io/9b4c9f7f-61b3-4db8-b7bf-79d0ab4a1c32","http://img.gank.io/ec2b4eff-27dc-459c-b0fa-6ae557766429","http://img.gank.io/68dc5a14-bd4b-4b80-a39c-6d0725232840"],"publishedAt":"2019-06-23T07:11:47.63Z","source":"web","type":"Android","url":"https://github.com/yangchong211/YCFlutterToast","used":true,"who":"潇湘剑雨"},{"_id":"5d0214689d212203197e0f1e","createdAt":"2019-06-13T09:16:24.944Z","desc":"自定义红点控件，不用修改之前的代码，完全解耦，可以支持设置在TextView，Button，LinearLayout，RelativeLayout，TabLayout等等控件上\u2026\u2026","images":["http://img.gank.io/be00e66e-cb86-4f46-8e92-47609045a571"],"publishedAt":"2019-06-13T09:16:45.189Z","source":"web","type":"Android","url":"https://github.com/yangchong211/YCRedDotView","used":true,"who":"潇湘剑雨"},{"_id":"5d00fd349d2122031b798097","createdAt":"2019-06-12T13:25:08.262Z","desc":"🦌 Flutter 学习练手项目。包括完整UI设计图，更贴近真实项目的练习。","publishedAt":"2019-06-13T06:33:47.258Z","source":"web","type":"Android","url":"https://github.com/simplezhli/flutter_deer","used":true,"who":"潇湘剑雨"},{"_id":"5d01a2319d2122031ea521fc","createdAt":"2019-06-13T01:09:05.990Z","desc":"谁的Bug指给了我？害我损失5W奖金！","publishedAt":"2019-06-13T06:33:40.443Z","source":"web","type":"Android","url":"https://mp.weixin.qq.com/s/Jl-nSxn1LpSMe2rxxMxxHA","used":true,"who":"潇湘剑雨"},{"_id":"5cff1bb49d2122031b798092","createdAt":"2019-06-11T03:10:44.937Z","desc":"BaseUrlManager 主要用于开发时，有多个环境需要打包APK的场景，通过BaseUrlManager提供的BaseUrl动态设置入口，只需打一次包，即可轻松随意的切换不同的开发环境或测试环境。在打生产环境包时，关闭BaseUrl动态设置入口即可。","publishedAt":"2019-06-12T01:02:00.437Z","source":"web","type":"Android","url":"https://github.com/jenly1314/BaseUrlManager","used":true,"who":"潇湘剑雨"}]
     */

    private boolean error;
    private List<ResultsBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * _id : 5d19f32b9d2122774f0cd8d2
         * createdAt : 2019-07-01T11:48:59.726Z
         * desc : 2019 最前沿的几个 Flutter 实践：微信、咸鱼、美团
         * publishedAt : 2019-07-01T11:49:08.311Z
         * source : web
         * type : Android
         * url : https://mp.weixin.qq.com/s/TyjwBASNvxnQNXtC3zCG1w
         * used : true
         * who : 潇湘剑雨
         * images : ["http://img.gank.io/9b4c9f7f-61b3-4db8-b7bf-79d0ab4a1c32","http://img.gank.io/ec2b4eff-27dc-459c-b0fa-6ae557766429","http://img.gank.io/68dc5a14-bd4b-4b80-a39c-6d0725232840"]
         */

        private String _id;
        private String createdAt;
        private String desc;
        private String publishedAt;
        private String source;
        private String type;
        private String url;
        private boolean used;
        private String who;
        private List<String> images;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        @Override
        public String toString() {
            return "ResultsBean{" +
                    "_id='" + _id + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", desc='" + desc + '\'' +
                    ", publishedAt='" + publishedAt + '\'' +
                    ", source='" + source + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", used=" + used +
                    ", who='" + who + '\'' +
                    ", images=" + images +
                    '}';
        }
    }
}
