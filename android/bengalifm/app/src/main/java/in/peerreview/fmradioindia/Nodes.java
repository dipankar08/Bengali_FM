package in.peerreview.fmradioindia;

/**
 * Created by ddutta on 9/2/2017.
 */
class Nodes {
    public Nodes(String uid, String name, String img, String url, String tags) {
        this.uid = uid;
        this.name = name;
        this.img = img;
        this.tags = tags;
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getType() {
        return tags;
    }

    public String getUrl() {
        return url;
    }

    String uid, name, img, tags, url;
}