package com.classic.demo;

import java.util.List;

/**
 * 应用名称: RxJava2Demo
 * 包 名 称: com.classic.demo
 *
 * 文件描述: 人脸识别结果 {https://www.faceplusplus.com.cn/face/index.html}
 * 创 建 人: 续写经典
 * 创建时间: 2016/12/19 16:06
 */
@SuppressWarnings("unused") class IdentifyResult {
    /**
     * faces1 : [{"face_rectangle":{"width":54,"top":37,"left":25,"height":54},"face_token":"a5c10fad04c6a4aa173bd"}]
     * faces2 : [{"face_rectangle":{"width":211,"top":260,"left":431,"height":211},"face_token":"a1a43744ab4a71e542a1a0"}]
     * time_used : 1017
     * thresholds : {"1e-3":65.3,"1e-5":76.5,"1e-4":71.8}
     * confidence : 87.538
     * image_id2 : H3Jigcmksj2y==
     * image_id1 : qdl1Uhn1UScej2==
     * request_id : 148180,f9803-ad0-484-ba1-f35ab77
     */

    private int            time_used;
    //private String         thresholds;
    private double         confidence;
    private String         image_id2;
    private String         image_id1;
    private String         request_id;
    private List<FaceBean> faces1;
    private List<FaceBean> faces2;

    static class FaceBean {

        /**
         * face_rectangle : {"width":211,"top":260,"left":431,"height":211}
         * face_token : a1a0437aaaaaaa45b842ae014a0
         */

        private FaceRectangleBean face_rectangle;
        private String face_token;

        public FaceRectangleBean getFace_rectangle() { return face_rectangle;}

        public void setFace_rectangle(FaceRectangleBean face_rectangle) {
            this.face_rectangle = face_rectangle;
        }

        public String getFace_token() { return face_token;}

        public void setFace_token(String face_token) { this.face_token = face_token;}

        static class FaceRectangleBean {
            /**
             * width : 211
             * top : 260
             * left : 431
             * height : 211
             */

            private int width;
            private int top;
            private int left;
            private int height;

            public int getWidth() { return width;}

            public void setWidth(int width) { this.width = width;}

            public int getTop() { return top;}

            public void setTop(int top) { this.top = top;}

            public int getLeft() { return left;}

            public void setLeft(int left) { this.left = left;}

            public int getHeight() { return height;}

            public void setHeight(int height) { this.height = height;}
        }
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getImage_id2() {
        return image_id2;
    }

    public void setImage_id2(String image_id2) {
        this.image_id2 = image_id2;
    }

    public String getImage_id1() {
        return image_id1;
    }

    public void setImage_id1(String image_id1) {
        this.image_id1 = image_id1;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public List<FaceBean> getFaces1() {
        return faces1;
    }

    public void setFaces1(List<FaceBean> faces1) {
        this.faces1 = faces1;
    }

    public List<FaceBean> getFaces2() {
        return faces2;
    }

    public void setFaces2(List<FaceBean> faces2) {
        this.faces2 = faces2;
    }

    @Override public String toString() {
        return "IdentifyResult{" +
               "time_used=" + time_used +
               ", confidence=" + confidence +
               ", image_id2='" + image_id2 + '\'' +
               ", image_id1='" + image_id1 + '\'' +
               ", request_id='" + request_id +
               '}';
    }
}
